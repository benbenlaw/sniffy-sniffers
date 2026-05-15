package com.benbenlaw.sniffysniffers.data;

import com.benbenlaw.sniffysniffers.SniffySniffers;
import com.benbenlaw.sniffysniffers.block.SSBlocks;
import com.benbenlaw.sniffysniffers.loot.AddItemModifier;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

import java.util.concurrent.CompletableFuture;

public class SSLootModifierProvider extends GlobalLootModifierProvider {

    public SSLootModifierProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider, SniffySniffers.MOD_ID);
    }

    @Override
    protected void start() {

        add("sniffer_egg_in_pyramid", new AddItemModifier(
                new LootItemCondition[] {
                        new LootTableIdCondition.Builder(Identifier.withDefaultNamespace("archaeology/desert_pyramid")).build(),
                        LootItemRandomChanceCondition.randomChance(0.45f).build()
                },
                1001, SSBlocks.SNIFFY_SNIFFER_EGG.get().asItem()));

        add("sniffer_egg_in_well", new AddItemModifier(
                new LootItemCondition[] {
                        new LootTableIdCondition.Builder(Identifier.withDefaultNamespace("archaeology/desert_well")).build(),
                        LootItemRandomChanceCondition.randomChance(0.45f).build()
                },
                1001, SSBlocks.SNIFFY_SNIFFER_EGG.get().asItem()));
    }
}

