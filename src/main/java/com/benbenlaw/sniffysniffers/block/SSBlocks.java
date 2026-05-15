package com.benbenlaw.sniffysniffers.block;

import com.benbenlaw.sniffysniffers.SniffySniffers;
import com.benbenlaw.sniffysniffers.item.SSItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;

public class SSBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SniffySniffers.MOD_ID);

    public static final DeferredBlock<Block> SNIFFY_SNIFFER_EGG = registerEggBlock("sniffy_sniffer_egg",
            properties -> new SniffySnifferEggBlock(properties
                    .mapColor(MapColor.COLOR_RED)
                    .strength(0.5F)
                    .sound(SoundType.METAL)
                    .noOcclusion()));






    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Function<BlockBehaviour.Properties, T> function) {
        DeferredBlock<T> toReturn = BLOCKS.registerBlock(name, function);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        SSItems.ITEMS.registerItem(name, properties -> new BlockItem(block.get(), properties.useBlockDescriptionPrefix()));
    }

    private static <T extends Block> DeferredBlock<T> registerEggBlock(String name, Function<BlockBehaviour.Properties, T> function) {
        DeferredBlock<T> toReturn = BLOCKS.registerBlock(name, function);
        registerEggBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerEggBlockItem(String name, DeferredBlock<T> block) {
        SSItems.ITEMS.registerItem(name, properties -> new BlockItem(block.get(),
                properties.useBlockDescriptionPrefix().rarity(Rarity.UNCOMMON)));
    }
}
