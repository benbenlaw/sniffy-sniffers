package com.benbenlaw.sniffysniffers.datamaps;

import com.benbenlaw.sniffysniffers.SniffySniffers;
import com.benbenlaw.sniffysniffers.core.ChanceResult;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

import java.util.List;

public class SSDataMaps {
    public static final Codec<List<ChanceResult>> LIST_CODEC = ChanceResult.CODEC.listOf();

    public static final DataMapType<Block, List<ChanceResult>> SNIFFER_LOOT = DataMapType.builder(
                    SniffySniffers.identifier("sniffer_loot"), Registries.BLOCK, LIST_CODEC)
            .synced(LIST_CODEC, true).build();
}