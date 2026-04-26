package com.benbenlaw.sniffysniffers.event;

import com.benbenlaw.sniffysniffers.SniffySniffers;
import com.benbenlaw.sniffysniffers.core.ChanceResult;
import com.benbenlaw.sniffysniffers.datamaps.SSDataMaps;
import com.benbenlaw.sniffysniffers.integration.jei.SSRecipe;
import com.benbenlaw.sniffysniffers.network.packet.SnifferLootEntry;
import com.benbenlaw.sniffysniffers.network.packet.SyncLootTablesPayload;
import com.benbenlaw.sniffysniffers.util.LootTableUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = SniffySniffers.MOD_ID)
public class DataPackSyncEvent {

    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        MinecraftServer server = event.getPlayerList().getServer();
        var provider = server.registryAccess();
        var registries = server.reloadableRegistries();

        List<SnifferLootEntry> entries = new ArrayList<>();
        var blockRegistry = provider.lookup(Registries.BLOCK).orElseThrow();

        blockRegistry.listElementIds().forEach(blockKey -> {
            Holder<Block> blockHolder = blockRegistry.getOrThrow(blockKey);
            Identifier lootTableId = blockHolder.getData(SSDataMaps.SNIFFER_BLOCK_LOOTTABLE);

            if (lootTableId != null) {
                LootTable table = registries.getLootTable(ResourceKey.create(Registries.LOOT_TABLE, lootTableId));
                List<ChanceResult> results = LootTableUtil.parseLootTable(table, provider);

                if (!results.isEmpty()) {
                    entries.add(new SnifferLootEntry(blockHolder.value(), results));
                    System.out.println("ADDED" + results);
                }
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
