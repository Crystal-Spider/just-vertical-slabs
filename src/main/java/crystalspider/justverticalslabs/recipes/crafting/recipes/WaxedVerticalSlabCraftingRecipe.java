package crystalspider.justverticalslabs.recipes.crafting.recipes;

import crystalspider.justverticalslabs.JustVerticalSlabsLoader;
import crystalspider.justverticalslabs.recipes.crafting.VerticalSlabCraftingRecipe;
import crystalspider.justverticalslabs.utils.VerticalSlabUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class WaxedVerticalSlabCraftingRecipe extends VerticalSlabCraftingRecipe {
  /**
   * ID of this recipe.
   */
  public static final String ID = "waxed_vertical_slab_crafting_recipe";
  /**
   * {@link ResourceLocation} of this recipe used to uniquely identify it.
   */
  private static final ResourceLocation RESOURCE_LOCATION = VerticalSlabUtils.getResourceLocation(ID);

  public WaxedVerticalSlabCraftingRecipe() {
    super(1, 1);
  }

  @Override
  public ItemStack assemble(ItemStack matchedItem) {
    Item slab = VerticalSlabUtils.waxingMap.get(VerticalSlabUtils.getReferredSlabState(matchedItem).getBlock().asItem());
    return VerticalSlabUtils.getVerticalSlabItem(VerticalSlabUtils.slabStateMap.get(slab), VerticalSlabUtils.translucentMap.containsKey(slab));
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
    return JustVerticalSlabsLoader.WAXED_VERTICAL_SLAB_CRAFTING_RECIPE_SERIALIZER.get();
  }

  @Override
  public boolean canCraftInDimensions(int width, int height) {
    return width * height >= 2;
  }

  @Override
  protected Integer getMatchIndex(CraftingContainer craftingContainer) {
    boolean correctPattern = true, hasHoneycomb = false;
    Integer matchIndex = null;
    for (int i = 0; i < craftingContainer.getContainerSize() && correctPattern; i++) {
      ItemStack itemStack = craftingContainer.getItem(i);
      if (!itemStack.isEmpty()) {
        if (matchIndex == null && isVerticalSlab(itemStack) && VerticalSlabUtils.waxingMap.containsKey(VerticalSlabUtils.getReferredSlabState(itemStack).getBlock().asItem())) {
          matchIndex = i;
        } else if(!hasHoneycomb && itemStack.is(Items.HONEYCOMB)) {
          hasHoneycomb = true;
        } else {
          matchIndex = null;
          correctPattern = false;
        }
      }
    }
    return hasHoneycomb ? matchIndex : null;
  }

  /**
   * Serializer for {@link WaxedVerticalSlabCraftingRecipe}.
   */
  public static class Serializer extends VerticalSlabCraftingRecipe.Serializer<WaxedVerticalSlabCraftingRecipe> {
    /**
     * ID of this {@link Serializer}.
     */
    public static final String ID = WaxedVerticalSlabCraftingRecipe.ID + "_serializer";
    /**
     * {@link ResourceLocation} of this {@link Serializer} used to uniquely identify it.
     */
    public static final ResourceLocation RESOURCE_LOCATION = VerticalSlabUtils.getResourceLocation(ID);

    public Serializer() {
      super(WaxedVerticalSlabCraftingRecipe::new);
    }
  }
}
