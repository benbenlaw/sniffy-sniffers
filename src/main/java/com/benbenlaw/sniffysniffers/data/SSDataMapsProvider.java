package com.benbenlaw.sniffysniffers.data;

import com.benbenlaw.sniffysniffers.core.ChanceResult;
import com.benbenlaw.sniffysniffers.datamaps.SSDataMaps;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.DataMapProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SSDataMapsProvider extends DataMapProvider {

    public SSDataMapsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> providerCompletableFuture) {
        super(output, providerCompletableFuture);
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        addBlockLootTable(Blocks.STONE, chanceResult(Blocks.COBBLESTONE, 1.0f));
        addBlockLootTable(Blocks.GRAVEL, chanceResult(Blocks.GRAVEL, 0.8f), chanceResult(Items.FLINT, 0.2f));
        addBlockLootTable(Blocks.DIAMOND_ORE, chanceResult(Items.DIAMOND, 0.5f), chanceResult(Items.COBBLESTONE, 0.75f));
        addBlockLootTable(Blocks.EMERALD_ORE, chanceResult(Items.EMERALD, 0.5f), chanceResult(Items.COBBLESTONE, 0.75f));
        addBlockLootTable(Blocks.IRON_ORE, chanceResult(Items.RAW_IRON, 0.5f), chanceResult(Items.COBBLESTONE, 0.75f));
        addBlockLootTable(Blocks.GOLD_ORE, chanceResult(Items.RAW_GOLD, 0.5f), chanceResult(Items.COBBLESTONE, 0.75f));
        addBlockLootTable(Blocks.COAL_ORE, chanceResult(Items.COAL, 0.5f), chanceResult(Items.COBBLESTONE, 0.75f));
        addBlockLootTable(Blocks.REDSTONE_ORE, chanceResult(Items.REDSTONE, 0.5f), chanceResult(Items.COBBLESTONE, 0.75f));
        addBlockLootTable(Blocks.NETHER_QUARTZ_ORE, chanceResult(Items.QUARTZ, 0.5f), chanceResult(Items.NETHER_BRICK, 0.75f));
        addBlockLootTable(Blocks.ANCIENT_DEBRIS, chanceResult(Items.ANCIENT_DEBRIS, 0.01f), chanceResult(Items.NETHER_BRICK, 0.75f));
    }

    public void addBlockLootTable(Block block, ChanceResult... results) {
        builder(SSDataMaps.SNIFFER_LOOT)
                .add(block.defaultBlockState().typeHolder(), List.of(results), false);
    }

    public ChanceResult chanceResult(ItemLike item, float chance) {
        return new ChanceResult(new ItemStackTemplate(item.asItem()), chance);
    }



}
