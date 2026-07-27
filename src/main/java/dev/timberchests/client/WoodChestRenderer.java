package dev.timberchests.client;

import dev.timberchests.TimberChests;
import dev.timberchests.block.WoodChestBlock;
import dev.timberchests.block.entity.TimberChestBlockEntity;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.ChestType;

public final class WoodChestRenderer extends ChestRenderer<TimberChestBlockEntity> {
    public WoodChestRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected Material getMaterial(TimberChestBlockEntity blockEntity, ChestType chestType) {
        Block block = blockEntity.getBlockState().getBlock();
        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(block);
        String variant = "oak";

        if (block instanceof WoodChestBlock && blockId.getPath().endsWith("_chest")) {
            variant = blockId.getPath().substring(0, blockId.getPath().length() - "_chest".length());
        }

        String suffix = switch (chestType) {
            case LEFT -> "_left";
            case RIGHT -> "_right";
            case SINGLE -> "";
        };
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(
            TimberChests.MOD_ID,
            "entity/chest/" + variant + suffix
        );
        return new Material(Sheets.CHEST_SHEET, texture);
    }
}
