package crystalspider.justverticalslabs.recipes.stonecutter.recipes;

import crystalspider.justverticalslabs.JustVerticalSlabsLoader;
import crystalspider.justverticalslabs.recipes.stonecutter.VerticalSlabStonecutterRecipe;
import crystalspider.justverticalslabs.utils.VerticalSlabUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

/**
 * {@link VerticalSlabStonecutterRecipe} to craft a block into 2 Vertical Slabs.
 */
public class BlockToVerticalSlabStonecutterRecipe extends VerticalSlabStonecutterRecipe {
  /**
   * ID of this recipe.
   */
  public static final String ID = "block_to_vertical_slab_stonecutter_recipe";
  /**
   * {@link ResourceLocation} of this recipe used to uniquely identify it.
   */
  private static final ResourceLocation RESOURCE_LOCATION = VerticalSlabUtils.getResourceLocation(ID);

  public BlockToVerticalSlabStonecutterRecipe() {
    super(RESOURCE_LOCATION, "", VerticalSlabUtils.getDefaultInstance());
  }

  @Override
  public boolean matches(Container container, Level level) {
    return VerticalSlabUtils.stonecuttingMap.containsKey(container.getItem(0).getItem());
  }

  @Override
  public ItemStack assemble(Container container) {
    ItemStack verticalSlab = VerticalSlabUtils.getVerticalSlabItem(Block.byItem(VerticalSlabUtils.slabMap.get(VerticalSlabUtils.stonecuttingMap.get(container.getItem(0).getItem()))).defaultBlockState());
    verticalSlab.setCount(2);
    return verticalSlab;
  }

  @Override
  public Serializer getSerializer() {
    return JustVerticalSlabsLoader.TEST_RECIPE_SERIALIZER.get();
  }

  /**
   * Serializer for {@link BlockToVerticalSlabStonecutterRecipe}.
   */
  public static class Serializer extends VerticalSlabStonecutterRecipe.Serializer<BlockToVerticalSlabStonecutterRecipe> {
    /**
     * ID of this {@link Serializer}.
     */
    public static final String ID = BlockToVerticalSlabStonecutterRecipe.ID + "_serializer";
    /**
     * {@link ResourceLocation} of this {@link Serializer} used to uniquely identify it.
     */
    public static final ResourceLocation RESOURCE_LOCATION = VerticalSlabUtils.getResourceLocation(ID);

    public Serializer() {
      super(BlockToVerticalSlabStonecutterRecipe::new);
    }
  }
}
