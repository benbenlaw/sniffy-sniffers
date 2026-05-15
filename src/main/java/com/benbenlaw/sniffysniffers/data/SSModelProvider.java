package com.benbenlaw.sniffysniffers.data;

import com.benbenlaw.sniffysniffers.SniffySniffers;
import com.benbenlaw.sniffysniffers.block.SSBlocks;
import com.benbenlaw.sniffysniffers.item.SSItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SnifferEggBlock;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

import static net.minecraft.client.data.models.BlockModelGenerators.plainVariant;

public class SSModelProvider extends ModelProvider {

    public SSModelProvider(PackOutput output) {
        super(output, SniffySniffers.MOD_ID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {

        //Items
        createSnifferEgg(blockModels, itemModels);
        itemModels.generateFlatItem(SSItems.SNIFFY_SNIFFER_SPAWN_EGG.get(), ModelTemplates.FLAT_ITEM);


    }

    public void createSnifferEgg(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        itemModels.generateFlatItem(SSBlocks.SNIFFY_SNIFFER_EGG.asItem(), ModelTemplates.FLAT_ITEM);
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(SSBlocks.SNIFFY_SNIFFER_EGG.get()).with(PropertyDispatch.initial(SnifferEggBlock.HATCH).generate((stage) -> {
            String var10000;
            switch (stage) {
                case 1 -> var10000 = "_slightly_cracked";
                case 2 -> var10000 = "_very_cracked";
                default -> var10000 = "_not_cracked";
            }

            String suffix = var10000;
            TextureMapping texture = snifferEgg(suffix);
            return plainVariant(ModelTemplates.SNIFFER_EGG.createWithSuffix(SSBlocks.SNIFFY_SNIFFER_EGG.get(), suffix, texture, blockModels.modelOutput));
        })));
    }

    public static TextureMapping snifferEgg(String suffix) {
        return (new TextureMapping()).put(TextureSlot.PARTICLE, getBlockTexture(SSBlocks.SNIFFY_SNIFFER_EGG.get(), suffix + "_north")).put(TextureSlot.BOTTOM, getBlockTexture(SSBlocks.SNIFFY_SNIFFER_EGG.get(), suffix + "_bottom")).put(TextureSlot.TOP, getBlockTexture(SSBlocks.SNIFFY_SNIFFER_EGG.get(), suffix + "_top")).put(TextureSlot.NORTH, getBlockTexture(SSBlocks.SNIFFY_SNIFFER_EGG.get(), suffix + "_north")).put(TextureSlot.SOUTH, getBlockTexture(SSBlocks.SNIFFY_SNIFFER_EGG.get(), suffix + "_south")).put(TextureSlot.EAST, getBlockTexture(SSBlocks.SNIFFY_SNIFFER_EGG.get(), suffix + "_east")).put(TextureSlot.WEST, getBlockTexture(SSBlocks.SNIFFY_SNIFFER_EGG.get(), suffix + "_west"));
    }

    public static Material getBlockTexture(Block block, String suffix) {
        Identifier id = BuiltInRegistries.BLOCK.getKey(block);
        return new Material(id.withPath((path) -> "block/" + path + suffix));
    }

    @Override
    protected @NotNull Stream<? extends Holder<Block>> getKnownBlocks() {
        return SSBlocks.BLOCKS.getEntries().stream();
    }

    @Override
    protected @NotNull Stream<? extends Holder<Item>> getKnownItems() {
        return SSItems.ITEMS.getEntries().stream();
    }

    @Override
    public @NotNull String getName() {
        return SniffySniffers.MOD_ID + " Models";
    }
}
