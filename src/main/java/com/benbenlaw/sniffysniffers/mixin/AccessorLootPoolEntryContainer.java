package com.benbenlaw.sniffysniffers.mixin;

import net.minecraft.world.level.storage.loot.entries.CompositeEntryBase;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(LootPoolEntryContainer.class)
public interface AccessorLootPoolEntryContainer {
  @Accessor("conditions")
  List<LootItemCondition> ssGetConditions();
}