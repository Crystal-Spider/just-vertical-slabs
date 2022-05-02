package crystalspider.justverticalslabs.recipes;

import javax.annotation.Nullable;

import crystalspider.justverticalslabs.JustVerticalSlabsLoader;
import crystalspider.justverticalslabs.utils.VerticalSlabUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

/**
 * {@link VerticalSlabRecipe} to craft 3 matching blocks in a column into the correct Vertical Slab.
 */
public class BlockVerticalSlabRecipe extends VerticalSlabRecipe {
  /**
   * ID of this recipe.
   */
  public static final String ID = "block_vertical_slab_recipe";
  /**
   * {@link ResourceLocation} of this recipe used to uniquely identify it.
   */
  private static final ResourceLocation RESOURCE_LOCATION = VerticalSlabUtils.getResourceLocation(ID);

  public BlockVerticalSlabRecipe() {
    super(1, 3, VerticalSlabUtils.getDefaultInstance());
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
    ItemStack verticalSlab = VerticalSlabUtils.getVerticalSlabItem(Block.byItem(craftingContainer.getItem(getMatchIndex(craftingContainer)).getItem()).defaultBlockState());
    verticalSlab.setCount(6);
    return verticalSlab;
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
    return JustVerticalSlabsLoader.BLOCK_VERTICAL_SLAB_RECIPE_SERIALIZER.get();
  }
  
  /**
   * Returns the index of the first Block in the column of matching blocks.
   * Returns null if none valid column could be found.
   * 
   * @param craftingContainer
   * @return index of the first Block in the matching column or null.
   */
  @Nullable
  private Integer getMatchIndex(CraftingContainer craftingContainer) {
    boolean correctPattern = true;
    Integer matchIndex = null, containerWidth = craftingContainer.getWidth();
    for (int h = 0; h < containerWidth && correctPattern; h++) {
      for (int w = 0; w < craftingContainer.getHeight() && correctPattern; w++) {
        int index = w + h * containerWidth;
        ItemStack itemStack1 = craftingContainer.getItem(index);
        if (!itemStack1.isEmpty()) {
          Item item = itemStack1.getItem();
          if (JustVerticalSlabsLoader.slabMap.values().contains(item)) {
            ItemStack itemStack2 = craftingContainer.getItem(index + containerWidth);
            ItemStack itemStack3 = craftingContainer.getItem(index + containerWidth * 2);
            if (itemStack2.is(item) && itemStack3.is(item)) {
              if (matchIndex == null) {
                matchIndex = index;
              } else {
                matchIndex = null;
                correctPattern = false;
              }
            } else if (matchIndex == null || (matchIndex != index - containerWidth && matchIndex != index - containerWidth * 2)) {
              matchIndex = null;
              correctPattern = false;
            }
          } else {
            matchIndex = null;
            correctPattern = false;
          }
        }
      }
    }
    return matchIndex;
  }

  /**
   * Serializer for {@link BlockVerticalSlabRecipe}.
   */
  public static class Serializer extends VerticalSlabRecipe.Serializer<BlockVerticalSlabRecipe> {
    /**
     * ID of this {@link Serializer}.
     */
    public static final String ID = BlockVerticalSlabRecipe.ID + "_serializer";
    /**
     * {@link ResourceLocation} of this {@link Serializer} used to uniquely identify it.
     */
    public static final ResourceLocation RESOURCE_LOCATION = VerticalSlabUtils.getResourceLocation(ID);

    public Serializer() {
      super(BlockVerticalSlabRecipe::new);
    }
  }
}
