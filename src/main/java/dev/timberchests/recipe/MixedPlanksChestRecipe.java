package dev.timberchests.recipe;

import dev.timberchests.registry.ModRecipeSerializers;
import dev.timberchests.registry.WoodVariant;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
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
    public MixedPlanksChestRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (input.width() != 3 || input.height() != 3 || input.ingredientCount() != 8) {
            return false;
        }

        Item firstPlank = null;
        boolean homogeneous = true;

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                ItemStack stack = input.getItem(row, column);
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
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        return new ItemStack(Items.CHEST);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 3 && height >= 3;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.MIXED_PLANKS_CHEST.get();
    }
}
