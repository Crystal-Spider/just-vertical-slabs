package crystalspider.justverticalslabs.items;

import java.util.ArrayList;

import javax.annotation.Nullable;

import crystalspider.justverticalslabs.JustVerticalSlabsLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class VerticalSlabBlockItem extends BlockItem {
  private final ArrayList<BlockState> referringBlockStates = new ArrayList<BlockState>();

  public static ItemStack getItemStackWithState(ItemLike itemLike, BlockState referringBlockState) {
    ItemStack itemStack = new ItemStack(itemLike);
    CompoundTag referringBlockTag = new CompoundTag();
    referringBlockTag.put("referringBlockState", NbtUtils.writeBlockState(referringBlockState));
    BlockItem.setBlockEntityData(itemStack, JustVerticalSlabsLoader.VERTICAL_SLAB_BLOCK_ENTITY.get(), referringBlockTag);
    return itemStack;
  }

  public VerticalSlabBlockItem(Block block, Properties properties, ArrayList<BlockState> referringBlockStates) {
    super(block, properties);
    this.referringBlockStates.addAll(referringBlockStates);
  }

  @Override
  public void fillItemCategory(CreativeModeTab creativeModeTab, NonNullList<ItemStack> itemStacks) {
    if (this.allowdedIn(creativeModeTab)) {
      for(BlockState referringBlockState : referringBlockStates) {
        itemStacks.add(getItemStackWithState(this, referringBlockState));
      }
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
