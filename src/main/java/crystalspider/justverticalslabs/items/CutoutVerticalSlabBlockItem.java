package crystalspider.justverticalslabs.items;

import crystalspider.justverticalslabs.JustVerticalSlabsLoader;
import crystalspider.justverticalslabs.utils.VerticalSlabUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Cutout Vertical Slab BlockItem.
 */
public class CutoutVerticalSlabBlockItem extends VerticalSlabBlockItem {
  public CutoutVerticalSlabBlockItem() {
    super(JustVerticalSlabsLoader.CUTOUT_VERTICAL_SLAB_BLOCK.get());
  }

  @Override
  public void fillItemCategory(CreativeModeTab creativeModeTab, NonNullList<ItemStack> itemStacks) {
    if (this.allowdedIn(creativeModeTab) && VerticalSlabUtils.slabStateMap != null) {
      for(BlockState referredSlabState : VerticalSlabUtils.slabStateMap.values()) {
        if (!VerticalSlabUtils.isTranslucent(referredSlabState)) {
          itemStacks.add(VerticalSlabUtils.getVerticalSlabItem(referredSlabState, false));
        }
      }
    }
  }
}
