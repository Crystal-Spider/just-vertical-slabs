package crystalspider.justverticalslabs.items;

import javax.annotation.Nullable;

import crystalspider.justverticalslabs.JustVerticalSlabsLoader;
import crystalspider.justverticalslabs.utils.VerticalSlabUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class VerticalSlabBlockItem extends BlockItem {

  public VerticalSlabBlockItem(Block block, Properties properties) {
    super(block, properties);
  }

  @Override
  public void fillItemCategory(CreativeModeTab creativeModeTab, NonNullList<ItemStack> itemStacks) {
    if (this.allowdedIn(creativeModeTab)) {
      // for(BlockState referringBlockState : JustVerticalSlabsLoader.slabMap.values().stream().map(item -> Block.byItem(item).defaultBlockState()).toList()) {
      //   itemStacks.add(VerticalSlabUtils.getItemStackWithState(this, referringBlockState));
      // }
    }
  }

  /**
   * Forces BlockEntity update on client.
   * Refer to {@link BlockItem#updateCustomBlockEntityTag(Level, Player, BlockPos, ItemStack)} for implementation.
   */
  @Override
  protected boolean updateCustomBlockEntityTag(BlockPos pos, Level level, @Nullable Player player, ItemStack itemStack, BlockState state) {
    boolean updated = updateCustomBlockEntityTag(level, player, pos, itemStack);
    if (!updated && level.getServer() == null) {
      CompoundTag compoundtag = getBlockEntityData(itemStack);
      if (compoundtag != null) {
        BlockEntity blockentity = level.getBlockEntity(pos);
        if (blockentity != null) {
          if (!level.isClientSide && blockentity.onlyOpCanSetNbt() && (player == null || !player.canUseGameMasterBlocks())) {
            return false;
          }
          CompoundTag compoundtag1 = blockentity.saveWithoutMetadata();
          CompoundTag compoundtag2 = compoundtag1.copy();
          compoundtag1.merge(compoundtag);
          if (!compoundtag1.equals(compoundtag2)) {
            blockentity.load(compoundtag1);
            blockentity.setChanged();
            return true;
          }
        }
      }
      return false;
    }
    return updated;
  }
}
