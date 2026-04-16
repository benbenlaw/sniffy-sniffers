package com.benbenlaw.sniffysniffers.integration.jei;

import com.benbenlaw.sniffysniffers.SniffySniffers;
import com.benbenlaw.sniffysniffers.data.SSDataMapsProvider;
import com.benbenlaw.sniffysniffers.datamaps.SSDataMaps;
import com.benbenlaw.sniffysniffers.item.SSItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.DataMapProvider;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class JEISSPlugin implements IModPlugin {

    @Override
    public @NotNull Identifier getPluginUid() {
        return SniffySniffers.identifier("jei_plugin");
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(SSRecipeCategory.RECIPE_TYPE, new ItemStack(SSItems.SNIFFY_SNIFFER_SPAWN_EGG.get()));

    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new SSRecipeCategory(registration.getJeiHelpers().getGuiHelper()));

    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<SSRecipe> recipes = new ArrayList<>();

        assert Minecraft.getInstance().level != null;
        var blockRegistry = Minecraft.getInstance().level.registryAccess().lookupOrThrow(Registries.BLOCK);
        var dataMap = blockRegistry.getDataMap(SSDataMaps.SNIFFER_BLOCK_LOOTTABLE);

        if (dataMap != null) {

            dataMap.forEach((holder, info) -> {
                Block block = blockRegistry.getValue(holder);
                recipes.add(new SSRecipe(block, info));
            });

        }
        registration.addRecipes(SSRecipeCategory.RECIPE_TYPE, recipes);
    }
}
