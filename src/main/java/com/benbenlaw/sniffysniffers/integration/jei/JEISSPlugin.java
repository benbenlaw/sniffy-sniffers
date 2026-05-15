package com.benbenlaw.sniffysniffers.integration.jei;

import com.benbenlaw.sniffysniffers.SniffySniffers;
import com.benbenlaw.sniffysniffers.event.ClientLootCache;
import com.benbenlaw.sniffysniffers.item.SSItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class JEISSPlugin implements IModPlugin {

    public static IDrawableStatic slotDrawable;

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
        slotDrawable = registration.getJeiHelpers().getGuiHelper().getSlotDrawable();

        registration.addRecipeCategories(new SSRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<SSRecipe> recipes = new ArrayList<>();

        ClientLootCache.getCachedRecipes().forEach((block, results) -> {
            recipes.add(new SSRecipe(block, results));
        });

        if (!recipes.isEmpty()) {
            registration.addRecipes(SSRecipeCategory.RECIPE_TYPE, recipes);
        }
    }
}
