package dev.timberchests.recipe;

import com.mojang.serialization.MapCodec;
import dev.timberchests.registry.ModRecipeSerializers;
import dev.timberchests.registry.WoodVariant;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

/**
 * Replaces the vanilla generic chest recipe. The eleven homogeneous vanilla
 * plank recipes are handled by normal shaped recipes; mixed planks (and
 * homogeneous modded planks) continue to produce the vanilla chest.
 */
public final class MixedPlanksChestRecipe extends CustomRecipe {
    private static final MixedPlanksChestRecipe INSTANCE = new MixedPlanksChestRecipe();
    public static final RecipeSerializer<MixedPlanksChestRecipe> SERIALIZER = new RecipeSerializer<>(
        MapCodec.unit(INSTANCE), StreamCodec.<RegistryFriendlyByteBuf, MixedPlanksChestRecipe>unit(INSTANCE)
    );

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (input.width() != 3 || input.height() != 3 || input.ingredientCount() != 8) {
            return false;
        }

        Item firstPlank = null;
        boolean homogeneous = true;

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                ItemStack stack = input.getItem(column, row);
                if (row == 1 && column == 1) {
                    if (!stack.isEmpty()) {
                        return false;
                    }
                    continue;
                }

                if (!stack.is(ItemTags.PLANKS)) {
                    return false;
                }

                if (firstPlank == null) {
                    firstPlank = stack.getItem();
                } else if (stack.getItem() != firstPlank) {
                    homogeneous = false;
                }
            }
        }

        return !homogeneous || WoodVariant.fromPlanks(firstPlank).isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        return new ItemStack(Items.CHEST);
    }

    @Override
    public RecipeSerializer<MixedPlanksChestRecipe> getSerializer() {
        return ModRecipeSerializers.MIXED_PLANKS_CHEST.get();
    }
}
