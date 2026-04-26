package com.benbenlaw.sniffysniffers.network.packet;

import com.benbenlaw.sniffysniffers.core.ChanceResult;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Block;

import java.util.List;

public record SnifferLootEntry(Block block, List<ChanceResult> results) {
    public static final StreamCodec<RegistryFriendlyByteBuf, SnifferLootEntry> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(Registries.BLOCK), SnifferLootEntry::block,
            ChanceResult.STREAM_CODEC.apply(ByteBufCodecs.list()), SnifferLootEntry::results,
            SnifferLootEntry::new
    );
}