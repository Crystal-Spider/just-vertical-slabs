package crystalspider.justverticalslabs.recipes.crafting.recipes;

import crystalspider.justverticalslabs.JustVerticalSlabsLoader;
import crystalspider.justverticalslabs.recipes.crafting.VerticalSlabCraftingRecipe;
import crystalspider.justverticalslabs.utils.VerticalSlabUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * {@link VerticalSlabCraftingRecipe} to craft 2 matching adjacent Vertical Slabs into their referring block.
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
    super(2, 1, Items.OAK_PLANKS.getDefaultInstance());
  }

  @Override
  public ItemStack assemble(ItemStack matchedItem) {
    return VerticalSlabUtils.getReferringBlockState(matchedItem).getBlock().asItem().getDefaultInstance();
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
   * Checks if both {@link ItemStack ItemStacks} represent the same kind of Vertical Slab, that is they all have the same referringBlockState.
   * 
   * @param verticalSlab1
   * @param verticalSlab2
   * @return whether both {@link ItemStack ItemStacks} represent the same kind of Vertical Slab.
   */
  protected boolean verticalSlabsMatch(ItemStack verticalSlab1, ItemStack verticalSlab2) {
    return VerticalSlabUtils.getReferringBlockState(verticalSlab1) == VerticalSlabUtils.getReferringBlockState(verticalSlab2);
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
