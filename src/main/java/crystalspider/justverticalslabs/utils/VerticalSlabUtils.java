package crystalspider.justverticalslabs.utils;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;

import crystalspider.justverticalslabs.JustVerticalSlabsLoader;
import crystalspider.justverticalslabs.blocks.VerticalSlabBlockEntity;
import crystalspider.justverticalslabs.items.VerticalSlabBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
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
   * {@link ImmutableMap} linking Slab {@link Item Items} to their respective {@link BlockState BlockStates}.
   */
  @Nullable
  public static volatile ImmutableMap<Item, BlockState> slabStateMap;
  /**
   * {@link ImmutableMap} linking Translucent Slab {@link Item Items} to their respective {@link BlockState BlockStates}.
   */
  @Nullable
  public static volatile ImmutableMap<Item, BlockState> translucentMap;
  /**
   * {@link ImmutableMap} linking Slab {@link Item Items} to their respective Block {@link Item Items}.
   */
  @Nullable
  public static volatile ImmutableMap<Item, Item> slabMap;
  /**
   * {@link ImmutableMap} linking Block {@link Item Items} to their respective Slab {@link Item Items}.
   */
  @Nullable
  public static volatile ImmutableMap<Item, Item> blockMap;
  /**
   * {@link ImmutableMap} linking Stonecuttable Block {@link Item Items} to their respective Slab {@link Item Items}.
   */
  @Nullable
  public static volatile ImmutableMap<Item, Item> stonecuttingMap;
  /**
   * {@link ImmutableMap} linking Oxidizable Slab {@link Item Items} to their respective Waxed Slab {@link Item Items}.
   */
  @Nullable
  public static volatile ImmutableMap<Item, Item> waxingMap;

  /**
   * ID to use when creating a new {@link CompoundTag} to store a Vertical Slab referred Slab {@link BlockState}.
   */
  public static final String NBT_ID = "referredSlabState";

  /**
   * {@link BlockState} {@link ModelProperty} to use when building and reading {@link IModelData} of a Vertical Slab.
   */
  public static final ModelProperty<BlockState> REFERRED_SLAB_STATE = new ModelProperty<BlockState>();

  /**
   * Builds and returns a new {@link IModelData} with {@link #REFERRED_SLAB_STATE referredSlabState property}
   * holding the value of the given {@code referredSlabState}.
   * 
   * @param referredSlabState
   * @return Vertical Slab {@link IModelData}.
   */
  public static IModelData buildModelData(BlockState referredSlabState) {
    return new ModelDataMap.Builder().withInitial(REFERRED_SLAB_STATE, referredSlabState).build();
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
    return getVerticalSlabItem(Blocks.OAK_SLAB.defaultBlockState(), false);
  }

  /**
   * Returns a Vertical Slab {@link ItemStack} with the specified {@code referredSlabState}.
   * 
   * @param referredSlabState
   * @param translucent - whether a Translucent or Cutout VerticalSlabBlock is to be used to generate the {@link ItemStack}.
   * @return Vertical Slab {@link ItemStack}.
   */
  public static ItemStack getVerticalSlabItem(BlockState referredSlabState, boolean translucent) {
    return translucent ? getItemStackWithState(JustVerticalSlabsLoader.TRANSLUCENT_VERTICAL_SLAB_BLOCK.get(), referredSlabState) : getItemStackWithState(JustVerticalSlabsLoader.CUTOUT_VERTICAL_SLAB_BLOCK.get(), referredSlabState);
  }

  /**
   * Returns a {@link ItemStack} with the specified {@code referredSlabState}.
   * 
   * @param itemLike - {@link ItemLike} instance to use to get a basic {@link ItemStack}.
   * @param referredSlabState - {@link BlockState} to save in the {@link ItemStack} NBTs.
   * @return {@link ItemStack} with {@code referredSlabState} in its NBTs.
   */
  public static ItemStack getItemStackWithState(ItemLike itemLike, BlockState referredSlabState) {
    ItemStack itemStack = new ItemStack(itemLike);
    BlockItem.setBlockEntityData(itemStack, JustVerticalSlabsLoader.VERTICAL_SLAB_BLOCK_ENTITY.get(), putReferredSlabState(new CompoundTag(), referredSlabState));
    return itemStack;
  }

  /**
   * Puts the given {@code referredSlabState} in the given {@link CompoundTag}.
   * 
   * @param compoundTag
   * @param referredSlabState
   * @return modified {@link CompoundTag}.
   */
  public static CompoundTag putReferredSlabState(CompoundTag compoundTag, BlockState referredSlabState) {
    compoundTag.put(VerticalSlabUtils.NBT_ID, NbtUtils.writeBlockState(referredSlabState));
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
   * Returns the {@link BlockState referredSlabState} for the given {@link BlockGetter} and {@link BlockPos}.
   * Returns {@code null} if none could be found.
   * 
   * @param getter
   * @param pos
   * @return {@link BlockState referredSlabState} or {@code null}.
   */
  @Nullable
  public static BlockState getReferredSlabState(BlockGetter getter, BlockPos pos) {
    VerticalSlabBlockEntity blockEntity = getVerticalSlabBlockEntity(getter, pos);
    if (blockEntity != null) {
      BlockState referredSlabState = blockEntity.getReferredSlabState();
      if (referredSlabState != null) {
        return referredSlabState;
      }
    }
    return null;
  }

  /**
   * Returns the {@link BlockState referredSlabState} for the given {@link ItemStack}.
   * Returns {@code null} if none could be found.
   * 
   * @param itemStack
   * @return {@link BlockState referredSlabState} or {@code null}.
   */
  @Nullable
  public static BlockState getReferredSlabState(ItemStack itemStack) {
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

  /**
   * Returns the {@link BlockState referredBlockState} for the given {@link BlockGetter} and {@link BlockPos}.
   * Returns {@code null} if none could be found.
   * 
   * @param getter
   * @param pos
   * @return {@link BlockState referredBlockState} or {@code null}.
   */
  @Nullable
  public static BlockState getReferredBlockState(BlockGetter getter, BlockPos pos) {
    return getReferredBlockState(getReferredSlabState(getter, pos));
  }

  /**
   * Returns the {@link BlockState referredBlockState} for the given {@link BlockState referredSlabState}.
   * Returns {@code null} if none could be found.
   * 
   * @param referredSlabState
   * @return {@link BlockState referredBlockState} or {@code null}.
   */
  @Nullable
  public static BlockState getReferredBlockState(BlockState referredSlabState) {
    if (referredSlabState != null && slabMap.containsKey(referredSlabState.getBlock().asItem())) {
      return Block.byItem(slabMap.get(referredSlabState.getBlock().asItem())).defaultBlockState();
    }
    return null;
  }
}
