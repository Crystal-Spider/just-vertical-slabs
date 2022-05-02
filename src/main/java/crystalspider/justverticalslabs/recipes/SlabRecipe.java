package crystalspider.justverticalslabs.recipes;

import crystalspider.justverticalslabs.JustVerticalSlabsLoader;
import crystalspider.justverticalslabs.utils.VerticalSlabUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/**
 * {@link VerticalSlabRecipe} to craft one Vertical Slab into its respective Slab.
 */
public class SlabRecipe extends VerticalSlabRecipe {
  /**
   * ID of this recipe.
   */
  public static final String ID = "slab_recipe";
  /**
   * {@link ResourceLocation} of this recipe used to uniquely identify it.
   */
  private static final ResourceLocation RESOURCE_LOCATION = VerticalSlabUtils.getResourceLocation(ID);

  public SlabRecipe() {
    super(1, 1, Items.OAK_SLAB.getDefaultInstance());
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
    return JustVerticalSlabsLoader.SLAB_RECIPE_SERIALIZER.get();
  }
  
  /**
   * Serializer for {@link SlabRecipe}.
   */
  public static class Serializer extends VerticalSlabRecipe.Serializer<SlabRecipe> {
    /**
     * ID of this {@link Serializer}.
     */
    public static final String ID = SlabRecipe.ID + "_serializer";
    /**
     * {@link ResourceLocation} of this {@link Serializer} used to uniquely identify it.
     */
    public static final ResourceLocation RESOURCE_LOCATION = VerticalSlabUtils.getResourceLocation(ID);

    public Serializer() {
      super(SlabRecipe::new);
    }
  }
}
