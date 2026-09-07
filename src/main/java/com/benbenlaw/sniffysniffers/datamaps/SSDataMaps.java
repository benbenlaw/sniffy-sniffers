package com.benbenlaw.sniffysniffers.datamaps;

import com.benbenlaw.sniffysniffers.SniffySniffers;
import com.benbenlaw.sniffysniffers.core.ChanceResult;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

import java.util.List;

public class SSDataMaps {

    public static final DataMapType<Block, List<ChanceResult>> SNIFFER_LOOT = DataMapType.builder(
            SniffySniffers.identifier("sniffer_loot"), Registries.BLOCK, ChanceResult.CODEC.listOf()).synced(ChanceResult.CODEC.listOf(), true).build();
}