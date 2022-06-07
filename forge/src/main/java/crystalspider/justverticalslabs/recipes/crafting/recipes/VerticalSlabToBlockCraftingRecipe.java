package crystalspider.justverticalslabs.recipes.crafting.recipes;

import crystalspider.justverticalslabs.JustVerticalSlabsLoader;
import crystalspider.justverticalslabs.recipes.crafting.VerticalSlabCraftingRecipe;
import crystalspider.justverticalslabs.utils.VerticalSlabUtils;
import crystalspider.justverticalslabs.utils.VerticalSlabUtils.MapsManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;

/**
 * {@link VerticalSlabCraftingRecipe} to craft 2 matching adjacent Vertical Slabs into their referred block.
 */
public class VerticalSlabToBlockCraftingRecipe extends VerticalSlabCraftingRecipe {
  /**
   * ID of this recipe.
   */
  public static final String ID = "vertical_slab_to_block_crafting_recipe";
  /**
     * {@link ResourceLocation} of this recipe used to uniquely identify it.
     */
  private static final ResourceLocation RESOURCE_LOCATION = VerticalSlabUtils.getResourceLocation(ID);

  public VerticalSlabToBlockCraftingRecipe() {
    super(2, 1);
  }

  @Override
  public ItemStack assemble(ItemStack matchedItem) {
    return MapsManager.slabMap.get(VerticalSlabUtils.getReferredSlabState(matchedItem).getBlock().asItem()).getDefaultInstance();
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
    return JustVerticalSlabsLoader.VERTICAL_SLAB_TO_BLOCK_CRAFTING_RECIPE_SERIALIZER.get();
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
          if (isVerticalSlab(itemStack1)) {
            ItemStack itemStack2 = craftingContainer.getItem(index + 1);
            if (isVerticalSlab(itemStack2)) {
              if (matchIndex == null && (index + 1) % containerWidth != 0 && verticalSlabsMatch(itemStack1, itemStack2)) {
                matchIndex = index;
              } else {
                matchIndex = null;
                correctPattern = false;
              }
            } else if (matchIndex == null || matchIndex != index - 1) {
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
   * Checks if both {@link ItemStack ItemStacks} represent the same kind of Vertical Slab and that kind can be crafted into a block.
   * 
   * @param itemStack1
   * @param itemStack2
   * @return whether both {@link ItemStack ItemStacks} represent the same kind of Vertical Slab.
   */
  protected boolean verticalSlabsMatch(ItemStack itemStack1, ItemStack itemStack2) {
    return VerticalSlabUtils.getReferredSlabState(itemStack1) == VerticalSlabUtils.getReferredSlabState(itemStack2) && MapsManager.slabMap.containsKey(VerticalSlabUtils.getReferredSlabState(itemStack1).getBlock().asItem());
  }

  /**
   * Serializer for {@link VerticalSlabToBlockCraftingRecipe}.
   */
  public static class Serializer extends VerticalSlabCraftingRecipe.Serializer<VerticalSlabToBlockCraftingRecipe> {
    /**
     * ID of this {@link Serializer}.
     */
    public static final String ID = VerticalSlabToBlockCraftingRecipe.ID + "_serializer";
    /**
     * {@link ResourceLocation} of this {@link Serializer} used to uniquely identify it.
     */
    public static final ResourceLocation RESOURCE_LOCATION = VerticalSlabUtils.getResourceLocation(ID);

    public Serializer() {
      super(VerticalSlabToBlockCraftingRecipe::new);
    }
  }
}
