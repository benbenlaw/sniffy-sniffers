package com.benbenlaw.sniffysniffers.integration.jei;

import com.benbenlaw.sniffysniffers.core.ChanceResult;
import net.minecraft.world.level.block.Block;

import java.util.List;

public record SSRecipe(Block block, List<ChanceResult> outputs) {}