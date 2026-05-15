package com.benbenlaw.sniffysniffers.integration.jei;

import com.benbenlaw.sniffysniffers.SniffySniffers;
import com.benbenlaw.sniffysniffers.core.ChanceResult;
import com.benbenlaw.sniffysniffers.item.SSItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawablesView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.gui.widgets.IScrollGridWidget;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SSRecipeCategory implements IRecipeCategory<SSRecipe> {

    public static final Identifier TEXTURE = SniffySniffers.identifier("textures/gui/sniffy_sniffer_jei.png");
    public static final IRecipeType<SSRecipe> RECIPE_TYPE = IRecipeType.create(SniffySniffers.identifier("sniffy_sniffer"), SSRecipe.class);

    private final int width = 101;
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
        int centerX = 48;
        int centerY = 2;
        int slotWidth = 18;

        builder.addSlot(RecipeIngredientRole.INPUT, 1, 2).add(new ItemStack(recipe.block())).setBackground(JEISSPlugin.slotDrawable, -1, -1);

        List<ChanceResult> chanceResults = recipe.outputs();
        int totalResults = chanceResults.size();

        for (int i = 0; i < totalResults; i++) {
            int displayIndex = Math.min(i, 2);
            int xPos = centerX + (displayIndex * slotWidth);

            final int finalIndex = i;

            builder.addSlot(RecipeIngredientRole.OUTPUT, xPos, centerY)
                    .add(chanceResults.get(i).template().create()).addRichTooltipCallback((slotView, tooltip) -> {
                        ChanceResult output = chanceResults.get(finalIndex);
                        float chance = output.chance();
                        int displayChance = (int) (chance * 100);
                        tooltip.add(Component.translatable("jei.sniffysniffers.chance", displayChance).withStyle(ChatFormatting.GOLD));
                    }).setBackground(JEISSPlugin.slotDrawable, -1, -1);
        }
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, SSRecipe recipe, IFocusGroup focuses) {
        IRecipeSlotDrawablesView recipeSlots = builder.getRecipeSlots();
        List<IRecipeSlotDrawable> results = recipeSlots.getSlots(RecipeIngredientRole.OUTPUT);

        if (results.size() > 3) {
            IScrollGridWidget triggersGrid = builder.addScrollGridWidget(results, 2, 1);
            triggersGrid.setPosition(47, 1);
        }
        builder.addAnimatedRecipeArrow(200).setPosition(21, 2);
    }


    public void draw(SSRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, 0, 0, 0, 0, width, height, width, height);
    }
}
