package crystalspider.justverticalslabs.recipes;

import crystalspider.justverticalslabs.JustVerticalSlabsLoader;
import crystalspider.justverticalslabs.utils.VerticalSlabUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * {@link VerticalSlabRecipe} to craft 3 matching blocks in a row into the correct Vertical Slab.
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
    super(3, 1, VerticalSlabUtils.getDefaultInstance());
  }

  @Override
  public boolean matches(CraftingContainer craftingContainer, Level level) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public ItemStack assemble(CraftingContainer craftingContainer) {
    // TODO Auto-generated method stub
    return null;
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
