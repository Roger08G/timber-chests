package dev.timberchests.block;

import com.mojang.serialization.MapCodec;
import dev.timberchests.block.entity.TimberChestBlockEntity;
import dev.timberchests.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public final class WoodChestBlock extends ChestBlock {
    public static final MapCodec<WoodChestBlock> CODEC = simpleCodec(WoodChestBlock::new);

    public WoodChestBlock(BlockBehaviour.Properties properties) {
        super(() -> ModBlockEntities.TIMBER_CHEST.get(), SoundEvents.CHEST_OPEN, SoundEvents.CHEST_CLOSE, properties);
    }

    @Override
    public MapCodec<? extends WoodChestBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TimberChestBlockEntity(pos, state);
    }
}
