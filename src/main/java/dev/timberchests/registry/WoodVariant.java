package dev.timberchests.registry;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public enum WoodVariant {
    OAK("oak", Blocks.OAK_PLANKS),
    SPRUCE("spruce", Blocks.SPRUCE_PLANKS),
    BIRCH("birch", Blocks.BIRCH_PLANKS),
    JUNGLE("jungle", Blocks.JUNGLE_PLANKS),
    ACACIA("acacia", Blocks.ACACIA_PLANKS),
    DARK_OAK("dark_oak", Blocks.DARK_OAK_PLANKS),
    MANGROVE("mangrove", Blocks.MANGROVE_PLANKS),
    CHERRY("cherry", Blocks.CHERRY_PLANKS),
    BAMBOO("bamboo", Blocks.BAMBOO_PLANKS),
    CRIMSON("crimson", Blocks.CRIMSON_PLANKS),
    WARPED("warped", Blocks.WARPED_PLANKS);

    private static final Map<Item, WoodVariant> BY_PLANKS = createPlankLookup();

    private final String id;
    private final Block planks;

    WoodVariant(String id, Block planks) {
        this.id = id;
        this.planks = planks;
    }

    public String id() {
        return this.id;
    }

    public Item planksItem() {
        return this.planks.asItem();
    }

    public static Optional<WoodVariant> fromPlanks(Item item) {
        return Optional.ofNullable(BY_PLANKS.get(item));
    }

    private static Map<Item, WoodVariant> createPlankLookup() {
        Map<Item, WoodVariant> result = new IdentityHashMap<>();
        for (WoodVariant variant : values()) {
            result.put(variant.planksItem(), variant);
        }
        return result;
    }
}
