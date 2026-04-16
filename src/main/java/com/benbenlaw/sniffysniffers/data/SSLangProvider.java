package com.benbenlaw.sniffysniffers.data;

import com.benbenlaw.sniffysniffers.SniffySniffers;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.jetbrains.annotations.NotNull;

public class SSLangProvider extends LanguageProvider {

    public SSLangProvider(PackOutput output) {
        super(output, SniffySniffers.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add("itemGroup.sniffysniffers", "Sniffy Sniffers");

        //Blocks
        add("block.sniffysniffers.sniffy_sniffer_egg", "Sniffy Sniffer Egg");

        //Items
        add("item.sniffysniffers.sniffy_sniffer_spawn_egg", "Sniffy Sniffer Spawn Egg");

        //Entity
        add("entity.sniffysniffers.sniffy_sniffer", "Sniffy Sniffer");

        //Jei
        add("jei.sniffysniffers.recipe_category", "Sniffy Sniffer will dig...");

    }

    @Override
    public @NotNull String getName() {
        return SniffySniffers.MOD_ID + " Language Provider";
    }
}
