package crystalspider.justverticalslabs.handlers;

import javax.annotation.Nullable;

import crystalspider.justverticalslabs.JustVerticalSlabsLoader;
import crystalspider.justverticalslabs.utils.VerticalSlabUtils;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ColorHandlerEventHandler {
  @SubscribeEvent
  public void onColorHandlerEventBlock(ColorHandlerEvent.Block event) {
    event.getBlockColors().register(new BlockColor() {
      public int getColor(BlockState state, @Nullable BlockAndTintGetter getter, @Nullable BlockPos pos, int tintIndex) {
        if (getter != null && pos != null) {
          BlockState referredSlabState = VerticalSlabUtils.getReferredSlabState(getter, pos);
          if (referredSlabState != null) {
            Block slab = referredSlabState.getBlock();
            if (slab instanceof GrassBlock || slab instanceof LeavesBlock) {
              return event.getBlockColors().getColor(Block.byItem(VerticalSlabUtils.slabMap.get(slab.asItem())).defaultBlockState(), getter, pos, tintIndex);
            }
          }
        }
        return -1;
      }
    }, JustVerticalSlabsLoader.VERTICAL_SLAB_BLOCK.get());
  }

  /**
   * {@link net.minecraft.client.color.item.ItemColors#createDefault(net.minecraft.client.color.block.BlockColors)}.
   * 
   * @param event
   */
  @SubscribeEvent
  public void onColorHandlerEventItem(ColorHandlerEvent.Item event) {
    event.getItemColors().register(new ItemColor() {
      public int getColor(ItemStack itemStack, int tintIndex) {
        BlockState referredSlabState = VerticalSlabUtils.getReferredSlabState(itemStack);
        if (referredSlabState != null) {
          Block slab = referredSlabState.getBlock();
          if (slab instanceof GrassBlock || slab instanceof LeavesBlock) {
            return event.getItemColors().getColor(VerticalSlabUtils.slabMap.get(slab.asItem()).getDefaultInstance(), tintIndex);
            // return GrassColor.get(0.5, 1);
          }
        }
        return -1;
      }
    }, JustVerticalSlabsLoader.VERTICAL_SLAB_ITEM.get());
  }
}
