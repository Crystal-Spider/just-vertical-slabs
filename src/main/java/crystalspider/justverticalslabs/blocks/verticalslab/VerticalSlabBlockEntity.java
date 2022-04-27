package crystalspider.justverticalslabs.blocks.verticalslab;

import javax.annotation.Nullable;

import crystalspider.justverticalslabs.JustVerticalSlabsLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;

public class VerticalSlabBlockEntity extends BlockEntity {
  public static final ModelProperty<BlockState> REFERRING_BLOCK_STATE = new ModelProperty<BlockState>();

  private BlockState referringBlockState = null;

  public VerticalSlabBlockEntity(BlockPos pos, BlockState state) {
    super(JustVerticalSlabsLoader.VERTICAL_SLAB_BLOCK_ENTITY.get(), pos, state);
  }

  @Nullable
  public BlockState getReferringBlockState() {
    return referringBlockState;
  }

  @Override
  public void saveAdditional(CompoundTag tag) {
    saveReferringBlockState(tag);
  }

  @Override
  public void load(CompoundTag tag) {
    super.load(tag);
    loadReferringBlockState(tag);
  }

  @Override
  public CompoundTag getUpdateTag() {
    return saveReferringBlockState(super.getUpdateTag());
  }

  @Override
  public Packet<ClientGamePacketListener> getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this);
  } 

  @Override
  public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
    super.onDataPacket(net, pkt);
    requestModelDataUpdate();
    if (level != null && level.isClientSide) {
      level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
    }
  }

  @Override
  public IModelData getModelData() {
    return new ModelDataMap.Builder().withInitial(REFERRING_BLOCK_STATE, referringBlockState).build();
  }

  private CompoundTag saveReferringBlockState(CompoundTag tag) {
    if (referringBlockState != null) {
      tag.put("referringBlockState", NbtUtils.writeBlockState(referringBlockState));
    }
    return tag;
  }

  private void loadReferringBlockState(CompoundTag tag) {
    CompoundTag referringBlockStateTag = tag.getCompound("referringBlockState");
    if (referringBlockStateTag != null) {
      referringBlockState = NbtUtils.readBlockState(referringBlockStateTag);
    } else {
      // TODO: log warning.
      referringBlockState = Blocks.AIR.defaultBlockState();
    }
  }
}
