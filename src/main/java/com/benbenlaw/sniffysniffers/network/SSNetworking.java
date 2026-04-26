package com.benbenlaw.sniffysniffers.network;

import com.benbenlaw.sniffysniffers.SniffySniffers;
import com.benbenlaw.sniffysniffers.event.ClientLootCache;
import com.benbenlaw.sniffysniffers.network.packet.SyncLootTablesPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class SSNetworking {

    public static void registerNetworking(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(SniffySniffers.MOD_ID);

        registrar.playToClient(SyncLootTablesPayload.TYPE, SyncLootTablesPayload.STREAM_CODEC, ClientLootCache::handleData);
    }
}
