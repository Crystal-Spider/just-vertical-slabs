package crystalspider.justverticalslabs.recipes;

import crystalspider.justverticalslabs.JustVerticalSlabsLoader;
import crystalspider.justverticalslabs.utils.VerticalSlabUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/**
 * {@link VerticalSlabRecipe} to craft 2 matching adjacent Vertical Slabs into their referring block.
 */
public class ReferringBlockRecipe extends VerticalSlabRecipe {
  /**
   * ID of this recipe.
   */
  public static final String ID = "referring_block_recipe";
  /**
     * {@link ResourceLocation} of this recipe used to uniquely identify it.
     */
  private static final ResourceLocation RESOURCE_LOCATION = VerticalSlabUtils.getResourceLocation(ID);

  public ReferringBlockRecipe() {
    super(2, 1, Items.OAK_PLANKS.getDefaultInstance());
  }

  /**
   * Checks if the given {@link CraftingContainer} contains the correct items in the correct position to craft this recipe.
   */
  @Override
  public boolean matches(CraftingContainer craftingContainer, Level level) {
    return getMatchIndex(craftingContainer) != -1;
  }

  /**
   * Returns the {@link ItemStack} with the result of this recipe from the given {@link CraftingContainer}.
   */
  @Override
  public ItemStack assemble(CraftingContainer craftingContainer) {
    return VerticalSlabUtils.getReferringBlockState(craftingContainer.getItem(getMatchIndex(craftingContainer))).getBlock().asItem().getDefaultInstance();
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
    return JustVerticalSlabsLoader.REFERRING_BLOCK_RECIPE_SERIALIZER.get();
  }

  /**
   * Returns the index of the first Vertical Slab in the pair of matching adjacent Vertical Slabs.
   * Returns -1 if none valid pair could be found.
   * 
   * @param craftingContainer
   * @return index of the first Vertical Slab in the matching pair or -1.
   */
  private int getMatchIndex(CraftingContainer craftingContainer) {
    boolean correctPattern = true;
    int matchIndex = -1, containerWidth = craftingContainer.getWidth();
    for (int h = 0; h < containerWidth && correctPattern; h++) {
      for (int w = 0; w < craftingContainer.getHeight() && correctPattern; w++) {
        int index = w + h * containerWidth;
        ItemStack itemStack1 = craftingContainer.getItem(index);
        if (!itemStack1.isEmpty()) {
          if (isVerticalSlab(itemStack1)) {
            ItemStack itemStack2 = craftingContainer.getItem(index + 1);
            if (isVerticalSlab(itemStack2)) {
              if (matchIndex == -1 && verticalSlabsMatch(itemStack1, itemStack2)) {
                matchIndex = index;
              } else {
                matchIndex = -1;
                correctPattern = false;
              }
            } else if (matchIndex != index - 1 || index == 0) {
              matchIndex = -1;
              correctPattern = false;
            }
          } else {
            matchIndex = -1;
            correctPattern = false;
          }
        }
      }
    }
    return matchIndex;
  }

  /**
   * Serializer for {@link ReferringBlockRecipe}.
   */
  public static class Serializer extends VerticalSlabRecipe.Serializer<ReferringBlockRecipe> {
    /**
     * ID of this {@link Serializer}.
     */
    public static final String ID = ReferringBlockRecipe.ID + "_serializer";
    /**
     * {@link ResourceLocation} of this {@link Serializer} used to uniquely identify it.
     */
    public static final ResourceLocation RESOURCE_LOCATION = VerticalSlabUtils.getResourceLocation(ID);

    public Serializer() {
      super(ReferringBlockRecipe::new);
    }
  }
}
