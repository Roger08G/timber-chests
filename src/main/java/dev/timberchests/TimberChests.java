package dev.timberchests;

import dev.timberchests.registry.ModBlockEntities;
import dev.timberchests.registry.ModBlocks;
import dev.timberchests.registry.ModRecipeSerializers;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

@Mod(TimberChests.MOD_ID)
public final class TimberChests {
    public static final String MOD_ID = "timber_chests";

    public TimberChests(IEventBus modBus) {
        ModBlocks.register(modBus);
        ModBlockEntities.register(modBus);
        ModRecipeSerializers.register(modBus);
        modBus.addListener(this::addCreativeTabItems);
    }

    private void addCreativeTabItems(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            ModBlocks.chestItems().forEach(item -> event.accept(item.get()));
        }
    }
}
