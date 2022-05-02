package crystalspider.justverticalslabs.recipes;

import crystalspider.justverticalslabs.JustVerticalSlabsLoader;
import crystalspider.justverticalslabs.utils.VerticalSlabUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

/**
 * {@link VerticalSlabRecipe} to craft one Slab into its respective Vertical Slab.
 */
public class SlabVerticalSlabRecipe extends VerticalSlabRecipe {
  /**
   * ID of this recipe.
   */
  public static final String ID = "slab_vertical_slab_recipe";
  /**
   * {@link ResourceLocation} of this recipe used to uniquely identify it.
   */
  private static final ResourceLocation RESOURCE_LOCATION = VerticalSlabUtils.getResourceLocation(ID);

  public SlabVerticalSlabRecipe() {
    super(1, 1, VerticalSlabUtils.getDefaultInstance());
  }

  @Override
  public ItemStack assemble(ItemStack matchedItem) {
    return VerticalSlabUtils.getVerticalSlabItem(Block.byItem(JustVerticalSlabsLoader.slabMap.getOrDefault(matchedItem.getItem(), VerticalSlabUtils.getDefaultInstance().getItem())).defaultBlockState());
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
    return JustVerticalSlabsLoader.SLAB_VERTICAL_SLAB_RECIPE_SERIALIZER.get();
  }

  @Override 
  protected Integer getMatchIndex(CraftingContainer craftingContainer) {
    boolean correctPattern = true;
    Integer matchIndex = null;
    for (int i = 0; i < craftingContainer.getContainerSize() && correctPattern; i++) {
      ItemStack itemStack = craftingContainer.getItem(i);
      if (!itemStack.isEmpty()) {
        if (matchIndex == null && JustVerticalSlabsLoader.slabMap.containsKey(itemStack.getItem())) {
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
   * Serializer for {@link SlabVerticalSlabRecipe}.
   */
  public static class Serializer extends VerticalSlabRecipe.Serializer<SlabVerticalSlabRecipe> {
    /**
     * ID of this {@link Serializer}.
     */
    public static final String ID = SlabVerticalSlabRecipe.ID + "_serializer";
    /**
     * {@link ResourceLocation} of this {@link Serializer} used to uniquely identify it.
     */
    public static final ResourceLocation RESOURCE_LOCATION = VerticalSlabUtils.getResourceLocation(ID);

    public Serializer() {
      super(SlabVerticalSlabRecipe::new);
    }
  }
}
