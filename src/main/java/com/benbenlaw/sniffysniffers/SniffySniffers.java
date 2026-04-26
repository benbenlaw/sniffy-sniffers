package com.benbenlaw.sniffysniffers;

import com.benbenlaw.sniffysniffers.block.SSBlocks;
import com.benbenlaw.sniffysniffers.data.SSLootModifierProvider;
import com.benbenlaw.sniffysniffers.data.SSLootTableProvider;
import com.benbenlaw.sniffysniffers.datamaps.SSDataMaps;
import com.benbenlaw.sniffysniffers.entity.SSEntities;
import com.benbenlaw.sniffysniffers.entity.SniffySnifferEntity;
import com.benbenlaw.sniffysniffers.entity.client.SniffySnifferRenderer;
import com.benbenlaw.sniffysniffers.item.SSCreativeTab;
import com.benbenlaw.sniffysniffers.item.SSItems;
import com.benbenlaw.sniffysniffers.loot.SSLootModifiers;
import com.benbenlaw.sniffysniffers.network.SSNetworking;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(SniffySniffers.MOD_ID)
public class SniffySniffers {

    public static final String MOD_ID = "sniffysniffers";
    private static final Logger LOGGER = LogManager.getLogger();


    public SniffySniffers(IEventBus modEventBus) {

        SSBlocks.BLOCKS.register(modEventBus);
        SSItems.ITEMS.register(modEventBus);
        SSCreativeTab.CREATIVE_MODE_TABS.register(modEventBus);
        SSEntities.ENTITIES.register(modEventBus);
        SSLootModifiers.LOOT_MODIFIER_SERIALIZERS.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerDataMaps);


        modEventBus.addListener(SniffySniffers::registerAttributes);
    }

    @EventBusSubscriber(modid = SniffySniffers.MOD_ID, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(SSEntities.SNIFFY_SNIFFER.get(), SniffySnifferRenderer::new);
        }
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(SSEntities.SNIFFY_SNIFFER.get(), SniffySnifferEntity.createAttributes().build());
    }


    public void registerDataMaps(RegisterDataMapTypesEvent event) {
        event.register(SSDataMaps.SNIFFER_BLOCK_LOOTTABLE);
    }

    public void commonSetup(RegisterPayloadHandlersEvent event) {
        SSNetworking.registerNetworking(event);
    }

    public static Identifier identifier(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

}

