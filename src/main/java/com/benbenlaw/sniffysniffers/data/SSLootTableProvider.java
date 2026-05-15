package com.benbenlaw.sniffysniffers.data;

import com.benbenlaw.sniffysniffers.block.SSBlocks;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.packs.VanillaBlockLoot;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class SSLootTableProvider extends VanillaBlockLoot {

    public SSLootTableProvider(HolderLookup.Provider provider) {
        super(provider);
    }
    @Override
    protected void generate() {
        this.dropSelf(SSBlocks.SNIFFY_SNIFFER_EGG.get());
    }

    @Override
    protected void add(@NotNull Block block, @NotNull LootTable.Builder table) {
        super.add(block, table);
        knownBlocks.add(block);
    }
    private final Set<Block> knownBlocks = new ReferenceOpenHashSet<>();

    @NotNull
    @Override
    protected Iterable<Block> getKnownBlocks() {
        return knownBlocks;
    }
}
