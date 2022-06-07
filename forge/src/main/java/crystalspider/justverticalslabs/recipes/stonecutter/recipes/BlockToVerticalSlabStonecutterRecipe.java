package crystalspider.justverticalslabs.recipes.stonecutter.recipes;

import crystalspider.justverticalslabs.JustVerticalSlabsLoader;
import crystalspider.justverticalslabs.recipes.stonecutter.VerticalSlabStonecutterRecipe;
import crystalspider.justverticalslabs.utils.VerticalSlabUtils;
import crystalspider.justverticalslabs.utils.VerticalSlabUtils.MapsManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.StonecutterScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.util.thread.EffectiveSide;

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
    super(RESOURCE_LOCATION, "");
  }

  @Override
  public ItemStack getResultItem() {
    if (EffectiveSide.get().isClient()) {
      Minecraft minecraft = Minecraft.getInstance();
      Screen screen = minecraft.screen;
      if (screen instanceof StonecutterScreen) {
        return assemble(((StonecutterScreen) screen).getMenu().container);
      }
    }
    return super.getResultItem();
  }

  @Override
  public boolean matches(Container container, Level level) {
    return MapsManager.stonecuttingMap.containsKey(container.getItem(0).getItem());
  }

  @Override
  public ItemStack assemble(Container container) {
    Item slab = MapsManager.stonecuttingMap.get(container.getItem(0).getItem());
    ItemStack verticalSlab = VerticalSlabUtils.getVerticalSlabItem(MapsManager.slabStateMap.get(slab), VerticalSlabUtils.isTranslucent(slab));
    verticalSlab.setCount(2);
    return verticalSlab;
  }

  @Override
  public Serializer getSerializer() {
    return JustVerticalSlabsLoader.BLOCK_TO_VERTICAL_SLAB_STONECUTTER_RECIPE_SERIALIZER.get();
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
