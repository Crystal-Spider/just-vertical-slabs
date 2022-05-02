package crystalspider.justverticalslabs.utils;

import javax.annotation.Nullable;

import crystalspider.justverticalslabs.JustVerticalSlabsLoader;
import crystalspider.justverticalslabs.blocks.verticalslab.VerticalSlabBlockEntity;
import crystalspider.justverticalslabs.items.VerticalSlabBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;

/**
 * Utility class for common operations regarding Vertical Slabs.
 */
public class VerticalSlabUtils {
  /**
   * ID to use when creating a new {@link CompoundTag} to store a Vertical Slab referring {@link BlockState}.
   */
  public static final String NBT_ID = "referringBlockState";

  /**
   * {@link BlockState} {@link ModelProperty} to use when building and reading {@link IModelData} of a Vertical Slab.
   */
  public static final ModelProperty<BlockState> REFERRING_BLOCK_STATE = new ModelProperty<BlockState>();

  /**
   * Builds and returns a new {@link IModelData} with {@link #REFERRING_BLOCK_STATE referringBlockState property}
   * holding the value of the given {@code referringBlockState}.
   * 
   * @param referringBlockState
   * @return Vertical Slab {@link IModelData}.
   */
  public static IModelData buildModelData(BlockState referringBlockState) {
    return new ModelDataMap.Builder().withInitial(REFERRING_BLOCK_STATE, referringBlockState).build();
  }

  /**
   * Returns a new {@link ResourceLocation} to uniquely identify a resource of this mod.
   * 
   * @param id - unique ID for the resource.
   * @return {@link ResourceLocation}.
   */
  public static ResourceLocation getResourceLocation(String id) {
    return new ResourceLocation(JustVerticalSlabsLoader.MODID, id);
  }

  /**
   * Returns the Vertical Slab {@link ItemStack} default instance.
   * 
   * @return Vertical Slab {@link ItemStack} default instance.
   */
  public static ItemStack getDefaultInstance() {
    return getVerticalSlabItem(Blocks.OAK_PLANKS.defaultBlockState());
  }

  /**
   * Returns a Vertical Slab {@link ItemStack} with the specified {@code referringBlockState}.
   * 
   * @param referringBlockState
   * @return Vertical Slab {@link ItemStack}.
   */
  public static ItemStack getVerticalSlabItem(BlockState referringBlockState) {
    return getItemStackWithState(JustVerticalSlabsLoader.VERTICAL_SLAB_BLOCK.get(), referringBlockState);
  }

  /**
   * Returns a {@link ItemStack} with the specified {@code referringBlockState}.
   * 
   * @param itemLike - {@link ItemLike} instance to use to get a basic {@link ItemStack}.
   * @param referringBlockState - {@link BlockState} to save in the {@link ItemStack} NBTs.
   * @return {@link ItemStack} with {@code referringBlockState} in its NBTs.
   */
  public static ItemStack getItemStackWithState(ItemLike itemLike, BlockState referringBlockState) {
    ItemStack itemStack = new ItemStack(itemLike);
    CompoundTag referringBlockTag = putReferringBlockState(new CompoundTag(), referringBlockState);
    BlockItem.setBlockEntityData(itemStack, JustVerticalSlabsLoader.VERTICAL_SLAB_BLOCK_ENTITY.get(), referringBlockTag);
    return itemStack;
  }

  /**
   * Puts the given {@code referringBlockState} in the given {@link CompoundTag}.
   * 
   * @param compoundTag
   * @param referringBlockState
   * @return modified {@link CompoundTag}.
   */
  public static CompoundTag putReferringBlockState(CompoundTag compoundTag, BlockState referringBlockState) {
    compoundTag.put(VerticalSlabUtils.NBT_ID, NbtUtils.writeBlockState(referringBlockState));
    return compoundTag;
  }

  /**
   * Returns the {@link VerticalSlabBlockEntity} for the given {@link BlockGetter} and {@link BlockPos}.
   * Returns {@code null} if none could be found.
   * 
   * @param getter
   * @param pos
   * @return {@link VerticalSlabBlockEntity} or {@code null}.
   */
  @Nullable
  public static VerticalSlabBlockEntity getVerticalSlabBlockEntity(BlockGetter getter, BlockPos pos) {
    BlockEntity blockEntity = getter.getBlockEntity(pos);
    if (blockEntity != null && blockEntity instanceof VerticalSlabBlockEntity) {
      return (VerticalSlabBlockEntity) blockEntity;
    }
    return null;
  }

  /**
   * Returns the {@link BlockState referringBlockState} for the given {@link BlockGetter} and {@link BlockPos}.
   * Returns {@code null} if none could be found.
   * 
   * @param getter
   * @param pos
   * @return {@link BlockState referringBlockState} or {@code null}.
   */
  @Nullable
  public static BlockState getReferringBlockState(BlockGetter getter, BlockPos pos) {
    VerticalSlabBlockEntity blockEntity = getVerticalSlabBlockEntity(getter, pos);
    if (blockEntity != null) {
      return blockEntity.getReferringBlockState();
    }
    return null;
  }

  /**
   * Returns the {@link BlockState referringBlockState} for the given {@link ItemStack}.
   * Returns {@code null} if none could be found.
   * 
   * @param itemStack
   * @return {@link BlockState referringBlockState} or {@code null}.
   */
  @Nullable
  public static BlockState getReferringBlockState(ItemStack itemStack) {
    if (itemStack.getItem() instanceof VerticalSlabBlockItem) {
      CompoundTag compoundTag = itemStack.getTagElement("BlockEntityTag");
      if (compoundTag != null) {
        compoundTag = compoundTag.getCompound(VerticalSlabUtils.NBT_ID);
        if (compoundTag != null) {
          return NbtUtils.readBlockState(compoundTag);
        }
      }
    }
    return null;
  }
}
