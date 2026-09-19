package dev.timberchests.registry;

import dev.timberchests.TimberChests;
import dev.timberchests.block.entity.TimberChestBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlockEntities {
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
        DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, TimberChests.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TimberChestBlockEntity>> TIMBER_CHEST =
        BLOCK_ENTITY_TYPES.register(
            "timber_chest",
            () -> new BlockEntityType<>(TimberChestBlockEntity::new, ModBlocks.chestBlocksArray())
        );

    private ModBlockEntities() {
    }

    public static void register(IEventBus modBus) {
        BLOCK_ENTITY_TYPES.register(modBus);
    }
}
