package crystalspider.justverticalslabs.recipes;

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

  @Override
  public boolean matches(CraftingContainer craftingContainer, Level level) {
    return getMatchIndex(craftingContainer) != -1;
  }

  @Override
  public ItemStack assemble(CraftingContainer craftingContainer) {
    ItemStack block = craftingContainer.getItem(getMatchIndex(craftingContainer));
    return VerticalSlabUtils.getItemStackWithState(block.getItem(), Block.byItem(block.getItem()).defaultBlockState());
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
   * Returns -1 if none valid column could be found.
   * 
   * @param craftingContainer
   * @return index of the first Block in the matching column or -1.
   */
  private int getMatchIndex(CraftingContainer craftingContainer) {
    boolean correctPattern = true;
    int matchIndex = -1, containerWidth = craftingContainer.getWidth();
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
              if (matchIndex == -1) {
                matchIndex = index;
              } else {
                matchIndex = -1;
                correctPattern = false;
              }
            } else if ((matchIndex != index - containerWidth && matchIndex != index - containerWidth * 2) || index == containerWidth + 1 || index == containerWidth * 2 + 1) {
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
