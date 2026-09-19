package dev.timberchests.client;

import dev.timberchests.TimberChests;
import dev.timberchests.block.entity.TimberChestBlockEntity;
import dev.timberchests.registry.ModBlocks;
import dev.timberchests.registry.WoodVariant;
import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.client.renderer.MultiblockChestResources;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

public final class WoodChestRenderer extends ChestRenderer<TimberChestBlockEntity> {
    private final Map<Block, MultiblockChestResources<SpriteId>> spritesByBlock = new IdentityHashMap<>();

    public WoodChestRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
        for (WoodVariant variant : WoodVariant.values()) {
            this.spritesByBlock.put(ModBlocks.chest(variant).get(), new MultiblockChestResources<>(
                sprite(variant.id()), sprite(variant.id() + "_left"), sprite(variant.id() + "_right")
            ));
        }
    }

    @Override
    protected SpriteId getCustomSprite(TimberChestBlockEntity blockEntity, ChestRenderState state) {
        MultiblockChestResources<SpriteId> sprites = this.spritesByBlock.get(blockEntity.getBlockState().getBlock());
        return sprites == null ? null : sprites.select(state.type);
    }

    private static SpriteId sprite(String name) {
        return Sheets.CHEST_MAPPER.apply(Identifier.fromNamespaceAndPath(TimberChests.MOD_ID, name));
    }
}
