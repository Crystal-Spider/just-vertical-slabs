package crystalspider.justverticalslabs.recipes.crafting.recipes;

import crystalspider.justverticalslabs.JustVerticalSlabsLoader;
import crystalspider.justverticalslabs.recipes.crafting.VerticalSlabCraftingRecipe;
import crystalspider.justverticalslabs.utils.VerticalSlabUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * {@link VerticalSlabCraftingRecipe} to craft 3 matching blocks in a column into the correct Vertical Slab.
 */
public class BlockToVerticalSlabCraftingRecipe extends VerticalSlabCraftingRecipe {
  /**
   * ID of this recipe.
   */
  public static final String ID = "block_to_vertical_slab_crafting_recipe";
  /**
   * {@link ResourceLocation} of this recipe used to uniquely identify it.
   */
  private static final ResourceLocation RESOURCE_LOCATION = VerticalSlabUtils.getResourceLocation(ID);

  public BlockToVerticalSlabCraftingRecipe() {
    super(1, 3);
  }

  @Override
  public ItemStack assemble(ItemStack matchedItem) {
    Item slab = VerticalSlabUtils.blockMap.get(matchedItem.getItem());
    ItemStack verticalSlab = VerticalSlabUtils.getVerticalSlabItem(VerticalSlabUtils.slabStateMap.get(slab), VerticalSlabUtils.isTranslucent(slab));
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
    return JustVerticalSlabsLoader.BLOCK_TO_VERTICAL_SLAB_CRAFTING_RECIPE_SERIALIZER.get();
  }

  @Override
  protected Integer getMatchIndex(CraftingContainer craftingContainer) {
    boolean correctPattern = true;
    Integer matchIndex = null, containerWidth = craftingContainer.getWidth();
    for (int h = 0; h < containerWidth && correctPattern; h++) {
      for (int w = 0; w < craftingContainer.getHeight() && correctPattern; w++) {
        int index = w + h * containerWidth;
        ItemStack itemStack1 = craftingContainer.getItem(index);
        if (!itemStack1.isEmpty()) {
          Item item = itemStack1.getItem();
          if (VerticalSlabUtils.blockMap.containsKey(item)) {
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
   * Serializer for {@link BlockToVerticalSlabCraftingRecipe}.
   */
  public static class Serializer extends VerticalSlabCraftingRecipe.Serializer<BlockToVerticalSlabCraftingRecipe> {
    /**
     * ID of this {@link Serializer}.
     */
    public static final String ID = BlockToVerticalSlabCraftingRecipe.ID + "_serializer";
    /**
     * {@link ResourceLocation} of this {@link Serializer} used to uniquely identify it.
     */
    public static final ResourceLocation RESOURCE_LOCATION = VerticalSlabUtils.getResourceLocation(ID);

    public Serializer() {
      super(BlockToVerticalSlabCraftingRecipe::new);
    }
  }
}
