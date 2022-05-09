package crystalspider.justverticalslabs.handlers;

import crystalspider.justverticalslabs.blocks.VerticalSlabBlockEntity;
import crystalspider.justverticalslabs.utils.VerticalSlabUtils;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * {@link RightClickBlock} event handler.
 */
public class RightClickBlockHandler {
  /**
   * Handles the event {@link RightClickBlock} to wax a vertical slab.
   * 
   * @param event - {@link RightClickBlock}.
   */
  @SubscribeEvent
  public void onRightClickBlock(RightClickBlock event) {
    Player player = event.getPlayer();
    if (!player.isSpectator()) {
      InteractionResult interactionResult = wax(event.getWorld(), event.getPos(), player, event.getItemStack());
      if (interactionResult != InteractionResult.PASS) {
        event.setCancellationResult(interactionResult);
        event.setCanceled(true);
      }
    }
  }

  /**
   * Checks if the interaction is a valid vertical slab waxing interaction and, in case, waxes the vertical slab.
   * 
   * @param level - {@link Level} where the interaction is happening.
   * @param blockPos - {@link BlockPos} of the clicked block.
   * @param player - {@link Player} player involved in the interaction.
   * @param itemStack - {@link ItemStack} held in the interaction hand.
   * @return {@link InteractionResult}.
   */
  private InteractionResult wax(Level level, BlockPos blockPos, Player player, ItemStack itemStack) {
    BlockState blockState = VerticalSlabUtils.getReferringBlockState(level, blockPos);
    if (blockState != null) {
      return HoneycombItem.getWaxed(blockState).map((waxedBlockState) -> {
        if (player instanceof ServerPlayer) {
          CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer) player, blockPos, itemStack);
        }
        if (!player.isCreative()) {
          itemStack.shrink(1);
        }
        VerticalSlabBlockEntity blockEntity = VerticalSlabUtils.getVerticalSlabBlockEntity(level, blockPos);
        blockEntity.load(VerticalSlabUtils.putReferringBlockState(new CompoundTag(), Block.byItem(VerticalSlabUtils.slabMap.get(waxedBlockState.getBlock().asItem())).defaultBlockState()));
        blockEntity.requestModelDataUpdate();
        level.setBlock(blockPos, blockEntity.getBlockState(), 11);
        level.levelEvent(player, 3003, blockPos, 0);
        return InteractionResult.sidedSuccess(level.isClientSide);
      }).orElse(InteractionResult.PASS);
    }
    return InteractionResult.PASS;
  }
}
