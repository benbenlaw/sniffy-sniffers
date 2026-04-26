package com.benbenlaw.sniffysniffers.util;

import com.benbenlaw.sniffysniffers.core.ChanceResult;
import com.benbenlaw.sniffysniffers.mixin.*;
import com.mojang.datafixers.util.Either;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.*;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class LootTableUtil {

  public static List<ChanceResult> parseLootTable(LootTable table, HolderGetter.Provider provider) {
    List<ChanceResult> outputs = new ArrayList<>();
    List<LootPool> pools = ((AccessorMixinLootTable) table).getPools();

    for (LootPool pool : pools) {
      List<LootPoolEntryContainer> entries = ((AccessorMixinLootPool) pool).getEntries();
      outputs.addAll(processEntries(entries, provider));
    }
    return outputs;
  }

  private static List<ChanceResult> processEntries(List<LootPoolEntryContainer> entries, HolderGetter.Provider provider) {
    List<ChanceResult> results = new ArrayList<>();

    // 1. Filter entries: Only keep ones valid for a Sniffer (No Silk Touch/MatchTool)
    List<LootPoolEntryContainer> validEntries = new ArrayList<>();
    for (LootPoolEntryContainer entry : entries) {
      if (isValidForSniffer(entry)) {
        validEntries.add(entry);
      }
    }

    // 2. Calculate accurate total weight using actual JSON weight values
    float totalWeight = 0;
    for (LootPoolEntryContainer entry : validEntries) {
      totalWeight += getEntryWeight(entry);
    }

    final float finalTotalWeight = totalWeight;

    // 3. Process the filtered list into ChanceResults
    for (LootPoolEntryContainer entry : validEntries) {
      if (entry instanceof LootItem lootItem) {
        float weight = ((AccessorMixinLootPoolSingletonContainer) lootItem).ssGetWeight();
        float chance = finalTotalWeight > 0 ? weight / finalTotalWeight : 1.0f;
        results.add(new ChanceResult(new ItemStackTemplate(((AccessorMixinLootItem) lootItem).ssGetItem().value()), chance));
      }
      else if (entry instanceof TagEntry tagEntry) {
        TagKey<Item> tag = ((AccessorMixinTagEntry) tagEntry).ssGetTag();
        var tagIterable = BuiltInRegistries.ITEM.getTagOrEmpty(tag);

        if (tagIterable.iterator().hasNext()) {
          float weight = ((AccessorMixinLootPoolSingletonContainer) tagEntry).ssGetWeight();
          float chance = finalTotalWeight > 0 ? weight / finalTotalWeight : 1.0f;

          if (((AccessorMixinTagEntry) tagEntry).ssGetExpand()) {
            tagIterable.forEach(item ->
                    results.add(new ChanceResult(new ItemStackTemplate(item.value()), chance))
            );
          } else {
            results.add(new ChanceResult(new ItemStackTemplate(tagIterable.iterator().next().value()), chance));
          }
        }
      }
      else if (entry instanceof CompositeEntryBase composite) {
        // Alternatives logic: Recurse into children
        List<LootPoolEntryContainer> children = ((AccessorMixinCompositeEntryBase) composite).getChildren();
        results.addAll(processEntries(children, provider));
      }
      else if (entry instanceof NestedLootTable nested) {
        Either<ResourceKey<LootTable>, LootTable> contents = ((AccessorMixinNestedLootTable) nested).ssGetContents();
        contents.ifLeft(key -> {
          provider.lookup(Registries.LOOT_TABLE).flatMap(reg -> reg.get(key)).ifPresent(holder -> {
            results.addAll(parseLootTable(holder.value(), provider));
          });
        }).ifRight(table -> {
          results.addAll(parseLootTable(table, provider));
        });
      }
    }
    return results;
  }

  /**
   * Retrieves the weight of an entry.
   * For Singleton entries, it uses the accessor.
   * For Composites (Alternatives), it sums the weights of its valid children.
   */
  private static float getEntryWeight(LootPoolEntryContainer entry) {
    if (entry instanceof LootPoolSingletonContainer singleton) {
      if (singleton instanceof TagEntry tagEntry && ((AccessorMixinTagEntry) tagEntry).ssGetExpand()) {
        TagKey<Item> tag = ((AccessorMixinTagEntry) tagEntry).ssGetTag();
        long tagSize = BuiltInRegistries.ITEM.getTagOrEmpty(tag).spliterator().estimateSize();
        return tagSize * ((AccessorMixinLootPoolSingletonContainer) singleton).ssGetWeight();
      }
      return ((AccessorMixinLootPoolSingletonContainer) singleton).ssGetWeight();
    }

    if (entry instanceof CompositeEntryBase composite) {
      float compositeWeight = 0;
      for (LootPoolEntryContainer child : ((AccessorMixinCompositeEntryBase) composite).getChildren()) {
        if (isValidForSniffer(child)) {
          compositeWeight += getEntryWeight(child);
        }
      }
      return compositeWeight;
    }
    return 0;
  }

  /**
   * Mimics Sniffer behavior: Fails any entry requiring a tool or a player.
   */
  private static boolean isValidForSniffer(LootPoolEntryContainer entry) {
    List<LootItemCondition> conditions = ((AccessorLootPoolEntryContainer) entry).ssGetConditions();

    for (LootItemCondition condition : conditions) {
      if (condition instanceof MatchTool matchTool && matchTool.predicate().isPresent()) {
        String desc = matchTool.predicate().get().toString().toLowerCase();
        if (desc.contains("silk_touch") || desc.contains("enchantments")) {
          return false;
        }
      }

      String className = condition.getClass().getSimpleName().toLowerCase();
      if (className.contains("entityproperty") || className.contains("killedbyplayer")) {
        return false;
      }
    }
    return true;
  }

  public static List<Either<Item, TagKey<Item>>> recursivelyGetItems(LootTable table, HolderGetter.Provider provider) {
    List<Either<Item, TagKey<Item>>> result = new ArrayList<>();
    List<LootPool> pools = ((AccessorMixinLootTable) table).getPools();
    for (LootPool pool : pools) {
      List<LootPoolEntryContainer> entries = ((AccessorMixinLootPool) pool).getEntries();
      result.addAll(recursivelyGetItems(entries, provider));
    }
    return result;
  }

  public static List<Either<Item, TagKey<Item>>> recursivelyGetItems(List<LootPoolEntryContainer> entries, HolderGetter.Provider provider) {
    List<Either<Item, TagKey<Item>>> result = new ArrayList<>();
    for (LootPoolEntryContainer entry : entries) {
      if (!isValidForSniffer(entry)) continue;
      if (entry instanceof EmptyLootItem) continue;
      if (entry instanceof LootItem) {
        result.add(Either.left(((AccessorMixinLootItem) entry).ssGetItem().value()));
      } else if (entry instanceof TagEntry) {
        result.add(Either.right(((AccessorMixinTagEntry) entry).ssGetTag()));
      } else if (entry instanceof NestedLootTable) {
        var key = ((AccessorMixinNestedLootTable) entry).ssGetContents();
        LootTable table = key.map(o -> provider.lookup(Registries.LOOT_TABLE).orElseThrow().get(o).orElseThrow().value(), Function.identity());
        result.addAll(recursivelyGetItems(table, provider));
      } else if (entry instanceof CompositeEntryBase) {
        result.addAll(recursivelyGetItems(((AccessorMixinCompositeEntryBase) entry).getChildren(), provider));
      }
    }
    return result;
  }
}