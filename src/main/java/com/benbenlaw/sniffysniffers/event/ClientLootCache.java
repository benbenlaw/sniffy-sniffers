package com.benbenlaw.sniffysniffers.event;

import com.benbenlaw.sniffysniffers.core.ChanceResult;
import com.benbenlaw.sniffysniffers.network.packet.SyncLootTablesPayload;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientLootCache {

    private static final Map<Block, List<ChanceResult>> CACHED_RECIPES = new HashMap<>();

    public static void updateCache(Block block, List<ChanceResult> results) {
        CACHED_RECIPES.put(block, results);
    }

    public static void clear() {
        CACHED_RECIPES.clear();
    }

    public static Map<Block, List<ChanceResult>> getCachedRecipes() {
        return CACHED_RECIPES;
    }

    public static void handleData(final SyncLootTablesPayload data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            clear();
            for (var entry : data.entries()) {
                updateCache(entry.block(), entry.results());
            }
        });
    }
}