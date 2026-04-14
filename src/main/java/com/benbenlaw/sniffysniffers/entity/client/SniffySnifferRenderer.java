package com.benbenlaw.sniffysniffers.entity.client;

import com.benbenlaw.sniffysniffers.SniffySniffers;
import com.benbenlaw.sniffysniffers.entity.SniffySnifferEntity;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AgeableMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.AABB;

public class SniffySnifferRenderer extends AgeableMobRenderer<SniffySnifferEntity, SniffySnifferRenderState, SniffySnifferModel> {
    private static final Identifier SNIFFER_LOCATION = SniffySniffers.identifier("textures/entity/sniffy_sniffer/sniffy_sniffer.png");
    private static final Identifier SNIFFLET_LOCATION = SniffySniffers.identifier("textures/entity/sniffy_sniffer/sniffy_snifflet.png");

    public SniffySnifferRenderer(EntityRendererProvider.Context context) {
        super(context, new SniffySnifferModel(context.bakeLayer(ModelLayers.SNIFFER)), new SniffySnifferModel(context.bakeLayer(ModelLayers.SNIFFER_BABY)), 1.1F);
    }

    public Identifier getTextureLocation(SniffySnifferRenderState state) {
        return state.isBaby ? SNIFFLET_LOCATION : SNIFFER_LOCATION;
    }

    public SniffySnifferRenderState createRenderState() {
        return new SniffySnifferRenderState();
    }

    public void extractRenderState(SniffySnifferEntity entity, SniffySnifferRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.isSearching = entity.isSearching();
        state.diggingAnimationState.copyFrom(entity.diggingAnimationState);
        state.sniffingAnimationState.copyFrom(entity.sniffingAnimationState);
        state.risingAnimationState.copyFrom(entity.risingAnimationState);
        state.feelingHappyAnimationState.copyFrom(entity.feelingHappyAnimationState);
        state.scentingAnimationState.copyFrom(entity.scentingAnimationState);
    }

    protected AABB getBoundingBoxForCulling(SniffySnifferEntity entity) {
        return super.getBoundingBoxForCulling(entity).inflate(0.6F);
    }
}
