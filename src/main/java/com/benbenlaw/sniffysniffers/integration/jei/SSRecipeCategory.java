package com.benbenlaw.sniffysniffers.integration.jei;

import com.benbenlaw.sniffysniffers.SniffySniffers;
import com.benbenlaw.sniffysniffers.core.ChanceResult;
import com.benbenlaw.sniffysniffers.item.SSItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SSRecipeCategory implements IRecipeCategory<SSRecipe> {

    public static final Identifier TEXTURE = SniffySniffers.identifier("textures/gui/sniffy_sniffer_jei.png");
    public static final IRecipeType<SSRecipe> RECIPE_TYPE = IRecipeType.create(SniffySniffers.identifier("sniffy_sniffer"), SSRecipe.class);

    private final int width = 84;
    private final int height = 20;
    private final IDrawable icon;

    public SSRecipeCategory(IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(SSItems.SNIFFY_SNIFFER_SPAWN_EGG.get()));
    }

    @Override
    public @NotNull IRecipeType<SSRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("jei.sniffysniffers.recipe_category");
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SSRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 2, 2).add(new ItemStack(recipe.block().asItem()));

        int xOffset = 48;
        int yOffset = 2;

        for (ChanceResult result : recipe.outputs()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, xOffset, yOffset)
                    .add(result.template().create())
                    .addRichTooltipCallback((recipeSlotView, tooltip) -> {
                        float percentage = result.chance() * 100;
                        tooltip.add(Component.literal(String.format("%.1f%%", percentage))
                                .withStyle(net.minecraft.ChatFormatting.GOLD));
                    });

            xOffset += 18;

            if (xOffset > width - 18) {
                break;
            }
        }
    }

    public void draw(SSRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, 0, 0, 0, 0, width, height, width, height);
    }
}
