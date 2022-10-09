package crystalspider.justverticalslabs.handlers;

import javax.annotation.Nullable;

import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import crystalspider.justverticalslabs.JustVerticalSlabsLoader;
import crystalspider.justverticalslabs.utils.VerticalSlabUtils;
import crystalspider.justverticalslabs.utils.VerticalSlabUtils.MapsManager;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;

/**
 * {@link RegisterColorHandlersEvent} handler.
 */
@SuppressWarnings("null")
@EventBusSubscriber(value = Dist.CLIENT, bus = Bus.MOD)
public class RegisterColorHandlersEventHandler {
  /**
   * Registers the {@link BlockColor} for Vertical Slabs.
   * 
   * @param event
   */
  @SubscribeEvent
  public static void onRegisterColorHandlersEventBlock(RegisterColorHandlersEvent.Block event) {
    event.register(
      new BlockColor() {
        public int getColor(BlockState state, @Nullable BlockAndTintGetter getter, @Nullable BlockPos pos, int tintIndex) {
          if (getter != null && pos != null && !(getter instanceof ClientLevel)) {
            BlockState referredSlabState = VerticalSlabUtils.getReferredSlabState(getter, pos);
            if (referredSlabState != null) {
              Item slab = referredSlabState.getBlock().asItem();
              if (MapsManager.slabMap.containsKey(slab)) {
                return event.getBlockColors().getColor(Block.byItem(MapsManager.slabMap.get(slab)).defaultBlockState(), getter, pos, tintIndex);
              }
              return event.getBlockColors().getColor(MapsManager.slabStateMap.get(slab), getter, pos, tintIndex);
            }
          }
          return -1;
        }
      },
      JustVerticalSlabsLoader.CUTOUT_VERTICAL_SLAB_BLOCK.get(),
      JustVerticalSlabsLoader.TRANSLUCENT_VERTICAL_SLAB_BLOCK.get()
    );
  }

  /**
   * Registers the {@link ItemColor} for Vertical Slabs.
   * 
   * @param event
   */
  @SubscribeEvent
  public static void onRegisterColorHandlersEventItem(RegisterColorHandlersEvent.Item event) {
    event.register(
      new ItemColor() {
        public int getColor(ItemStack itemStack, int tintIndex) {
          BlockState referredSlabState = VerticalSlabUtils.getReferredSlabState(itemStack);
          if (referredSlabState != null) {
            Item slab = referredSlabState.getBlock().asItem();
            if (MapsManager.slabMap.containsKey(slab)) {
              return event.getItemColors().getColor(MapsManager.slabMap.get(slab).getDefaultInstance(), tintIndex);
            }
            return event.getItemColors().getColor(slab.getDefaultInstance(), tintIndex);
          }
          return -1;
        }
      },
      JustVerticalSlabsLoader.CUTOUT_VERTICAL_SLAB_ITEM.get(),
      JustVerticalSlabsLoader.TRANSLUCENT_VERTICAL_SLAB_ITEM.get()
    );
  }
}
