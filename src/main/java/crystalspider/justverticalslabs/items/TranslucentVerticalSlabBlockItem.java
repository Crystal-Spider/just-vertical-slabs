package crystalspider.justverticalslabs.items;

import crystalspider.justverticalslabs.JustVerticalSlabsLoader;
import crystalspider.justverticalslabs.utils.VerticalSlabUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 
 */
public class TranslucentVerticalSlabBlockItem extends VerticalSlabBlockItem {
  public TranslucentVerticalSlabBlockItem() {
    super(JustVerticalSlabsLoader.TRANSLUCENT_VERTICAL_SLAB_BLOCK.get());
  }

  @Override
  public void fillItemCategory(CreativeModeTab creativeModeTab, NonNullList<ItemStack> itemStacks) {
    if (this.allowdedIn(creativeModeTab) && VerticalSlabUtils.translucentMap != null) {
      for(BlockState referredSlabState : VerticalSlabUtils.translucentMap.values()) {
        itemStacks.add(VerticalSlabUtils.getVerticalSlabItem(referredSlabState, true));
      }
    }
  }
}
