package com.benbenlaw.sniffysniffers.datamaps;

import com.benbenlaw.sniffysniffers.SniffySniffers;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

public class SSDataMaps {

    public static final DataMapType<Block, Identifier> SNIFFER_BLOCK_LOOTTABLE = DataMapType.builder(
            SniffySniffers.identifier("sniffer_block_loottable"), Registries.BLOCK, Identifier.CODEC)
            .synced(Identifier.CODEC, true).build();
}
