package crystalspider.justverticalslabs.blocks;

import javax.annotation.Nullable;

import crystalspider.justverticalslabs.JustVerticalSlabsLoader;
import crystalspider.justverticalslabs.utils.VerticalSlabUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;

public class VerticalSlabBlockEntity extends BlockEntity {
  /**
   * {@link BlockState} of the referred Slab.
   */
  private BlockState referredSlabState = null;

  public VerticalSlabBlockEntity(BlockPos pos, BlockState state) {
    super(JustVerticalSlabsLoader.VERTICAL_SLAB_BLOCK_ENTITY.get(), pos, state);
  }

  @Nullable
  public BlockState getReferredSlabState() {
    return referredSlabState;
  }

  @Override
  public void saveAdditional(CompoundTag tag) {
    saveReferredSlabState(tag);
  }

  @Override
  public void load(CompoundTag tag) {
    super.load(tag);
    loadReferredSlabState(tag);
  }

  @Override
  public CompoundTag getUpdateTag() {
    return saveReferredSlabState(super.getUpdateTag());
  }

  @Override
  public Packet<ClientGamePacketListener> getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this);
  }

  @Override
  public IModelData getModelData() {
    return VerticalSlabUtils.buildModelData(referredSlabState);
  }

  /**
   * Saves the {@link #referredSlabState} to the given {@link CompoundTag}.
   * 
   * @param tag - {@link CompoundTag} where to save the {@link #referredSlabState}.
   * @return updated {@link CompoundTag}.
   */
  private CompoundTag saveReferredSlabState(CompoundTag tag) {
    if (referredSlabState != null) {
      VerticalSlabUtils.putReferredSlabState(tag, referredSlabState);
    }
    return tag;
  }

  /**
   * Tries to load the {@link #referredSlabState} from the given {@link CompoundTag}.
   * 
   * @param tag - {@link CompoundTag} containing the {@link #referredSlabState}.
   */
  private void loadReferredSlabState(CompoundTag tag) {
    CompoundTag referredSlabStateTag = tag.getCompound(VerticalSlabUtils.NBT_ID);
    if (referredSlabStateTag != null) {
      referredSlabState = NbtUtils.readBlockState(referredSlabStateTag);
    } else {
      JustVerticalSlabsLoader.LOGGER.warn("No referredSlabState Tag could be found while loading NBTs for Vertical Slab in position [" + getBlockPos().getX() + ", " + getBlockPos().getY() + ", " + getBlockPos().getZ() + "].");
      referredSlabState = null;
    }
  }
}
