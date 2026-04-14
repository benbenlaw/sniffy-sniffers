package com.benbenlaw.sniffysniffers.item;

import com.benbenlaw.sniffysniffers.SniffySniffers;
import com.benbenlaw.sniffysniffers.block.SSBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class SSCreativeTab {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SniffySniffers.MOD_ID);

    public static final Supplier<CreativeModeTab> STRAINERS_TAB = CREATIVE_MODE_TABS.register("strainers", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> SSBlocks.SNIFFY_SNIFFER_EGG.asItem().getDefaultInstance())
            .title(Component.translatable("itemGroup.sniffysniffers"))
            .displayItems((featureFlagSet, output) -> {

                SSItems.ITEMS.getEntries().forEach((entry) -> output.accept(entry.get()));
            }).build());
    
}
