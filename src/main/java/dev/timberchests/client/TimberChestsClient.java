package dev.timberchests.client;

import dev.timberchests.TimberChests;
import dev.timberchests.registry.ModBlockEntities;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = TimberChests.MOD_ID, value = Dist.CLIENT)
public final class TimberChestsClient {
    private TimberChestsClient() {
    }

    @SubscribeEvent
    static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.TIMBER_CHEST.get(), WoodChestRenderer::new);
    }
}
