package dev.timberchests.block.entity;

import dev.timberchests.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class TimberChestBlockEntity extends ChestBlockEntity {
    public TimberChestBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TIMBER_CHEST.get(), pos, state);
    }
}
