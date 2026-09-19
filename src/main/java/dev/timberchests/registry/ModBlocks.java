package dev.timberchests.registry;

import dev.timberchests.TimberChests;
import dev.timberchests.block.WoodChestBlock;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(TimberChests.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(TimberChests.MOD_ID);
    private static final Map<WoodVariant, DeferredBlock<WoodChestBlock>> CHESTS = new EnumMap<>(WoodVariant.class);
    private static final List<DeferredItem<BlockItem>> CHEST_ITEMS = new ArrayList<>();

    static {
        for (WoodVariant variant : WoodVariant.values()) {
            String name = variant.id() + "_chest";
            DeferredBlock<WoodChestBlock> block = BLOCKS.registerBlock(
                name,
                WoodChestBlock::new,
                () -> BlockBehaviour.Properties.ofFullCopy(Blocks.CHEST)
            );
            CHESTS.put(variant, block);
            CHEST_ITEMS.add(ITEMS.registerSimpleBlockItem(name, block));
        }
    }

    private ModBlocks() {
    }

    public static void register(IEventBus modBus) {
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
    }

    public static List<DeferredBlock<WoodChestBlock>> chests() {
        return List.copyOf(CHESTS.values());
    }

    public static DeferredBlock<WoodChestBlock> chest(WoodVariant variant) {
        return CHESTS.get(variant);
    }

    public static List<DeferredItem<BlockItem>> chestItems() {
        return Collections.unmodifiableList(CHEST_ITEMS);
    }

    public static Block[] chestBlocksArray() {
        return CHESTS.values().stream().map(DeferredBlock::get).toArray(Block[]::new);
    }
}
