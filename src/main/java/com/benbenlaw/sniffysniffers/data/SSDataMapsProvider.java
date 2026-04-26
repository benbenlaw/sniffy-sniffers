package com.benbenlaw.sniffysniffers.data;

import com.benbenlaw.sniffysniffers.SniffySniffers;
import com.benbenlaw.sniffysniffers.datamaps.SSDataMaps;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
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
        addBlockLootTable(Blocks.STONE, Identifier.withDefaultNamespace("blocks/stone"));
        addBlockLootTable(Blocks.GRAVEL, Identifier.withDefaultNamespace("blocks/gravel"));
        addBlockLootTable(Blocks.DIAMOND_ORE, Identifier.withDefaultNamespace("blocks/diamond_ore"));
        addBlockLootTable(Blocks.EMERALD_ORE, Identifier.withDefaultNamespace("blocks/emerald_ore"));
        addBlockLootTable(Blocks.IRON_ORE, Identifier.withDefaultNamespace("blocks/iron_ore"));
        addBlockLootTable(Blocks.GOLD_ORE, Identifier.withDefaultNamespace("blocks/gold_ore"));
        addBlockLootTable(Blocks.COAL_ORE, Identifier.withDefaultNamespace("blocks/coal_ore"));
        addBlockLootTable(Blocks.REDSTONE_ORE, Identifier.withDefaultNamespace("blocks/redstone_ore"));
        addBlockLootTable(Blocks.NETHER_QUARTZ_ORE, Identifier.withDefaultNamespace("blocks/nether_quartz_ore"));
        addBlockLootTable(Blocks.ANCIENT_DEBRIS, Identifier.withDefaultNamespace("blocks/ancient_debris"));
    }

    public void addBlockLootTable(Block block, Identifier lootTableId) {
        builder(SSDataMaps.SNIFFER_BLOCK_LOOTTABLE)
                .add(block.defaultBlockState().typeHolder(), lootTableId, false);
    }


}
