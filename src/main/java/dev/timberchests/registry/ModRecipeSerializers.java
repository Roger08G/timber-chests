package dev.timberchests.registry;

import dev.timberchests.TimberChests;
import dev.timberchests.recipe.MixedPlanksChestRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModRecipeSerializers {
    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
        DeferredRegister.create(Registries.RECIPE_SERIALIZER, TimberChests.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<MixedPlanksChestRecipe>> MIXED_PLANKS_CHEST =
        RECIPE_SERIALIZERS.register(
            "mixed_planks_chest",
            () -> new SimpleCraftingRecipeSerializer<>(MixedPlanksChestRecipe::new)
        );

    private ModRecipeSerializers() {
    }

    public static void register(IEventBus modBus) {
        RECIPE_SERIALIZERS.register(modBus);
    }
}
