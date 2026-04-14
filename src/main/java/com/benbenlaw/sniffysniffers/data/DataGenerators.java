package com.benbenlaw.sniffysniffers.data;


import com.benbenlaw.sniffysniffers.SniffySniffers;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = SniffySniffers.MOD_ID)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event) {

        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();


        generator.addProvider(true, new SSDataMapsProvider(packOutput, lookupProvider));
        generator.addProvider(true, new SSLootModifierProvider(packOutput, lookupProvider));
        generator.addProvider(true, new LootTableProvider(packOutput, Collections.emptySet(),
                List.of(new LootTableProvider.SubProviderEntry(SSLootTableProvider::new, LootContextParamSets.BLOCK)), lookupProvider));

        generator.addProvider(true, new SSModelProvider(packOutput));
        generator.addProvider(true, new SSLangProvider(packOutput));


        /*
        generator.addProvider(true, new StrainersBlockTags(packOutput, lookupProvider));
        generator.addProvider(true, new StrainersItemTags(packOutput, lookupProvider));
        generator.addProvider(true, new StrainersDataMapsProvider(packOutput, lookupProvider));
        generator.addProvider(true, new LootTableProvider(packOutput, Collections.emptySet(),
                List.of(new LootTableProvider.SubProviderEntry(StrainersLootTableProvider::new, LootContextParamSets.BLOCK)), lookupProvider));
        generator.addProvider(true, new StrainersModelProvider(packOutput));

        //Recipes
        generator.addProvider(true, new StrainersRecipeProvider.Runner(packOutput, lookupProvider));
        generator.addProvider(true, new StrainersProcessingRecipeProvider.Runner(packOutput, lookupProvider));

         */


    }
}
