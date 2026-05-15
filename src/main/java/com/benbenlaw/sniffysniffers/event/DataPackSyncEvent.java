package com.benbenlaw.sniffysniffers.event;

import com.benbenlaw.sniffysniffers.SniffySniffers;
import com.benbenlaw.sniffysniffers.core.ChanceResult;
import com.benbenlaw.sniffysniffers.datamaps.SSDataMaps;
import com.benbenlaw.sniffysniffers.network.packet.SnifferLootEntry;
import com.benbenlaw.sniffysniffers.network.packet.SyncLootTablesPayload;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = SniffySniffers.MOD_ID)
public class DataPackSyncEvent {

    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {

        MinecraftServer server = event.getPlayerList().getServer();
        var provider = server.registryAccess();

        List<SnifferLootEntry> entries = new ArrayList<>();
        var blockRegistry = provider.lookup(Registries.BLOCK).orElseThrow();

        blockRegistry.listElementIds().forEach(blockKey -> {
            Holder<Block> blockHolder = blockRegistry.getOrThrow(blockKey);
            List<ChanceResult> chanceResults = blockHolder.getData(SSDataMaps.SNIFFER_LOOT);

            if (chanceResults != null) {

                entries.add(new SnifferLootEntry(blockHolder.value(), chanceResults));
                System.out.println("ADDED" + chanceResults);

            }
        });

        SyncLootTablesPayload packet = new SyncLootTablesPayload(entries);

        if (event.getPlayer() != null) {
            event.getPlayer().connection.send(packet);
        } else {
            event.getPlayerList().getPlayers().forEach(p -> p.connection.send(packet));
        }
    }
}

