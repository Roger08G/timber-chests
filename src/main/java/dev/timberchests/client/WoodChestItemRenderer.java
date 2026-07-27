package dev.timberchests.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.timberchests.block.WoodChestBlock;
import dev.timberchests.block.entity.TimberChestBlockEntity;
import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;

public final class WoodChestItemRenderer extends BlockEntityWithoutLevelRenderer {
    private final BlockEntityRenderDispatcher dispatcher;
    private final Map<Block, TimberChestBlockEntity> itemBlockEntities = new IdentityHashMap<>();

    public WoodChestItemRenderer() {
        super(
            Minecraft.getInstance().getBlockEntityRenderDispatcher(),
            Minecraft.getInstance().getEntityModels()
        );
        this.dispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();
    }

    @Override
    public void renderByItem(
        ItemStack stack,
        ItemDisplayContext displayContext,
        PoseStack poseStack,
        MultiBufferSource buffer,
        int packedLight,
        int packedOverlay
    ) {
        if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof WoodChestBlock woodChest) {
            TimberChestBlockEntity blockEntity = this.itemBlockEntities.computeIfAbsent(
                woodChest,
                block -> new TimberChestBlockEntity(
                    BlockPos.ZERO,
                    block.defaultBlockState().setValue(ChestBlock.FACING, Direction.SOUTH)
                )
            );
            this.dispatcher.renderItem(blockEntity, poseStack, buffer, packedLight, packedOverlay);
            return;
        }

        super.renderByItem(stack, displayContext, poseStack, buffer, packedLight, packedOverlay);
    }
}
