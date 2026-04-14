package com.benbenlaw.sniffysniffers.data;

import com.benbenlaw.sniffysniffers.SniffySniffers;
import com.benbenlaw.sniffysniffers.datamaps.SSDataMaps;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.neoforge.common.data.DataMapProvider;

import java.util.concurrent.CompletableFuture;

public class SSDataMapsProvider extends DataMapProvider {

    public SSDataMapsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> providerCompletableFuture) {
        super(output, providerCompletableFuture);
    }


    @Override
    protected void gather(HolderLookup.Provider provider) {
        builder(SSDataMaps.SNIFFER_BLOCK_LOOTTABLE)
                .add(Blocks.STONE.defaultBlockState().typeHolder(), Identifier.withDefaultNamespace("blocks/stone"), false);

        builder(SSDataMaps.SNIFFER_BLOCK_LOOTTABLE)
                .add(Blocks.DIAMOND_ORE.defaultBlockState().typeHolder(), Identifier.withDefaultNamespace("blocks/diamond_ore"), false);

        builder(SSDataMaps.SNIFFER_BLOCK_LOOTTABLE)
                .add(Blocks.EMERALD_ORE.defaultBlockState().typeHolder(), Identifier.withDefaultNamespace("blocks/emerald_ore"), false);

        builder(SSDataMaps.SNIFFER_BLOCK_LOOTTABLE)
                .add(Blocks.IRON_ORE.defaultBlockState().typeHolder(), Identifier.withDefaultNamespace("blocks/iron_ore"), false);

        builder(SSDataMaps.SNIFFER_BLOCK_LOOTTABLE)
                .add(Blocks.GOLD_ORE.defaultBlockState().typeHolder(), Identifier.withDefaultNamespace("blocks/gold_ore"), false);
        builder(SSDataMaps.SNIFFER_BLOCK_LOOTTABLE)

                .add(Blocks.COAL_ORE.defaultBlockState().typeHolder(), Identifier.withDefaultNamespace("blocks/coal_ore"), false);

        builder(SSDataMaps.SNIFFER_BLOCK_LOOTTABLE)
                .add(Blocks.REDSTONE_ORE.defaultBlockState().typeHolder(), Identifier.withDefaultNamespace("blocks/redstone_ore"), false);

        builder(SSDataMaps.SNIFFER_BLOCK_LOOTTABLE)
                .add(Blocks.NETHER_QUARTZ_ORE.defaultBlockState().typeHolder(), Identifier.withDefaultNamespace("blocks/nether_quartz_ore"), false);

        builder(SSDataMaps.SNIFFER_BLOCK_LOOTTABLE)
                .add(Blocks.ANCIENT_DEBRIS.defaultBlockState().typeHolder(), Identifier.withDefaultNamespace("blocks/ancient_debris"), false);

    }


}
