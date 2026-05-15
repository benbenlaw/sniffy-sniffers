package com.benbenlaw.sniffysniffers.item;

import com.benbenlaw.sniffysniffers.SniffySniffers;
import com.benbenlaw.sniffysniffers.entity.SSEntities;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SSItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SniffySniffers.MOD_ID);


    public static final DeferredItem<Item> SNIFFY_SNIFFER_SPAWN_EGG = ITEMS.registerItem("sniffy_sniffer_spawn_egg",
            SpawnEggItem::new, properties -> properties.spawnEgg(SSEntities.SNIFFY_SNIFFER.get()));

}
