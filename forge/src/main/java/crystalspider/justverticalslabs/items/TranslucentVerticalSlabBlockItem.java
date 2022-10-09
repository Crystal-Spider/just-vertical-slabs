package crystalspider.justverticalslabs.items;

import crystalspider.justverticalslabs.JustVerticalSlabsLoader;
import crystalspider.justverticalslabs.utils.VerticalSlabUtils;
import crystalspider.justverticalslabs.utils.VerticalSlabUtils.MapsManager;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Translucent Vertical Slab BlockItem.
 */
public class TranslucentVerticalSlabBlockItem extends VerticalSlabBlockItem {
  public TranslucentVerticalSlabBlockItem() {
    super(JustVerticalSlabsLoader.TRANSLUCENT_VERTICAL_SLAB_BLOCK.get());
  }

  @Override
  @SuppressWarnings("null")
  public void fillItemCategory(CreativeModeTab creativeModeTab, NonNullList<ItemStack> itemStacks) {
    if (this.allowedIn(creativeModeTab) && MapsManager.translucentMap != null) {
      for(BlockState referredSlabState : MapsManager.translucentMap.values()) {
        itemStacks.add(VerticalSlabUtils.getVerticalSlabItem(referredSlabState, true));
      }
    }
  }
}
