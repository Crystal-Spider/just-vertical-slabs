package crystalspider.justverticalslabs.recipes;

import javax.annotation.Nullable;

import crystalspider.justverticalslabs.JustVerticalSlabsLoader;
import crystalspider.justverticalslabs.utils.VerticalSlabUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
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

  /**
   * Checks if the given {@link CraftingContainer} contains the correct items in the correct position to craft this recipe.
   */
  @Override
  public boolean matches(CraftingContainer craftingContainer, Level level) {
    return getMatchIndex(craftingContainer) != null;
  }

  /**
   * Returns the {@link ItemStack} with the result of this recipe from the given {@link CraftingContainer}.
   */
  @Override
  public ItemStack assemble(CraftingContainer craftingContainer) {
    return VerticalSlabUtils.getVerticalSlabItem(Block.byItem(JustVerticalSlabsLoader.slabMap.getOrDefault(craftingContainer.getItem(getMatchIndex(craftingContainer)).getItem(), VerticalSlabUtils.getDefaultInstance().getItem())).defaultBlockState());
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
  
  /**
   * Returns the index of the valid Slab.
   * Returns null if none could be found.
   * 
   * @param craftingContainer
   * @return index of the valid Slab or null.
   */
  @Nullable 
  private Integer getMatchIndex(CraftingContainer craftingContainer) {
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
