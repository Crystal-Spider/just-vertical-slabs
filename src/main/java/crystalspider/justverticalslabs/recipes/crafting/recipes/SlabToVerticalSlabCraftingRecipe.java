package crystalspider.justverticalslabs.recipes.crafting.recipes;

import crystalspider.justverticalslabs.JustVerticalSlabsLoader;
import crystalspider.justverticalslabs.recipes.crafting.VerticalSlabCraftingRecipe;
import crystalspider.justverticalslabs.utils.VerticalSlabUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

/**
 * {@link VerticalSlabCraftingRecipe} to craft one Slab into its respective Vertical Slab.
 */
public class SlabToVerticalSlabCraftingRecipe extends VerticalSlabCraftingRecipe {
  /**
   * ID of this recipe.
   */
  public static final String ID = "slab_to_vertical_slab_crafting_recipe";
  /**
   * {@link ResourceLocation} of this recipe used to uniquely identify it.
   */
  private static final ResourceLocation RESOURCE_LOCATION = VerticalSlabUtils.getResourceLocation(ID);

  public SlabToVerticalSlabCraftingRecipe() {
    super(1, 1, VerticalSlabUtils.getDefaultInstance());
  }

  @Override
  public ItemStack assemble(ItemStack matchedItem) {
    return VerticalSlabUtils.getVerticalSlabItem(Block.byItem(VerticalSlabUtils.slabMap.get(matchedItem.getItem())).defaultBlockState());
  }

  /**
   * Returns this {@link #RESOURCE_LOCATION ResourceLocation}.
   */
  @Override
  public ResourceLocation getId() {
    return RESOURCE_LOCATION;
  }

  @Override
  public Serializer getSerializer() {
    return JustVerticalSlabsLoader.SLAB_TO_VERTICAL_SLAB_CRAFTING_RECIPE_SERIALIZER.get();
  }

  @Override 
  protected Integer getMatchIndex(CraftingContainer craftingContainer) {
    boolean correctPattern = true;
    Integer matchIndex = null;
    for (int i = 0; i < craftingContainer.getContainerSize() && correctPattern; i++) {
      ItemStack itemStack = craftingContainer.getItem(i);
      if (!itemStack.isEmpty()) {
        if (matchIndex == null && VerticalSlabUtils.slabMap.containsKey(itemStack.getItem())) {
          matchIndex = i;
        } else {
          matchIndex = null;
          correctPattern = false;
        }
      }
    }
    return matchIndex;
  }

  /**
   * Serializer for {@link SlabToVerticalSlabCraftingRecipe}.
   */
  public static class Serializer extends VerticalSlabCraftingRecipe.Serializer<SlabToVerticalSlabCraftingRecipe> {
    /**
     * ID of this {@link Serializer}.
     */
    public static final String ID = SlabToVerticalSlabCraftingRecipe.ID + "_serializer";
    /**
     * {@link ResourceLocation} of this {@link Serializer} used to uniquely identify it.
     */
    public static final ResourceLocation RESOURCE_LOCATION = VerticalSlabUtils.getResourceLocation(ID);

    public Serializer() {
      super(SlabToVerticalSlabCraftingRecipe::new);
    }
  }
}
