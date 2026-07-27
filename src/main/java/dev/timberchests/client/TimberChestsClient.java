package dev.timberchests.client;

import dev.timberchests.TimberChests;
import dev.timberchests.registry.ModBlockEntities;
import dev.timberchests.registry.ModBlocks;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(modid = TimberChests.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class TimberChestsClient {
    private TimberChestsClient() {
    }

    @SubscribeEvent
    static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.TIMBER_CHEST.get(), WoodChestRenderer::new);
    }

    @SubscribeEvent
    static void registerItemRenderer(RegisterClientExtensionsEvent event) {
        event.registerItem(
            new IClientItemExtensions() {
                private BlockEntityWithoutLevelRenderer renderer;

                @Override
                public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                    if (this.renderer == null) {
                        this.renderer = new WoodChestItemRenderer();
                    }
                    return this.renderer;
                }
            },
            ModBlocks.chestItems().stream().map(item -> item.get()).toArray(Item[]::new)
        );
    }
}
