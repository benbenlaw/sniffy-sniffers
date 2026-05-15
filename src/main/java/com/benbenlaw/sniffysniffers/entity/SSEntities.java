package com.benbenlaw.sniffysniffers.entity;

import com.benbenlaw.sniffysniffers.SniffySniffers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class SSEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, SniffySniffers.MOD_ID);


    public static final Supplier<EntityType<SniffySnifferEntity>> SNIFFY_SNIFFER = ENTITIES.register("sniffy_sniffer", () ->
            EntityType.Builder.of(SniffySnifferEntity::new, MobCategory.CREATURE).sized(1.9F, 1.75F).eyeHeight(1.05F).passengerAttachments(2.09375F).nameTagOffset(2.05F).clientTrackingRange(10)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, SniffySniffers.identifier("sniffy_sniffer"))));

}
