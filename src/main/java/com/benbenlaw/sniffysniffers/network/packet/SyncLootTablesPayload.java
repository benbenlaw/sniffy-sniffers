package com.benbenlaw.sniffysniffers.network.packet;

import com.benbenlaw.sniffysniffers.SniffySniffers;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.List;

public record SyncLootTablesPayload(List<SnifferLootEntry> entries) implements CustomPacketPayload {

    public static final Type<SyncLootTablesPayload> TYPE = new Type<>(SniffySniffers.identifier("sync_loot_tables"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncLootTablesPayload> STREAM_CODEC = StreamCodec.composite(
            SnifferLootEntry.STREAM_CODEC.apply(ByteBufCodecs.list()), SyncLootTablesPayload::entries,
            SyncLootTablesPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}