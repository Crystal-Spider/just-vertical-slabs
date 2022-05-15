package crystalspider.justverticalslabs.items;

import javax.annotation.Nullable;

import crystalspider.justverticalslabs.JustVerticalSlabsLoader;
import crystalspider.justverticalslabs.utils.VerticalSlabUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;

/**
 * Vertical Slab {@link BlockItem}.
 */
public abstract class VerticalSlabBlockItem extends BlockItem {
  public VerticalSlabBlockItem(Block block) {
    super(block, new Item.Properties().tab(JustVerticalSlabsLoader.TAB_JUST_VERTICAL_SLABS));
  }

  /**
   * In the given {@link CreativeModeTab} adds as many Vertical Slab {@link ItemStack ItemStacks} as there are for Slabs.
   * 
   * @param creativeModeTab - {@link CreativeModeTab} in the creative inventory.
   * @param itemStacks - {@link NonNullList list} of {@link ItemStack ItemStacks} in the given {@link CreativeModeTab}.
   */
  @Override
  public abstract void fillItemCategory(CreativeModeTab creativeModeTab, NonNullList<ItemStack> itemStacks);

  /**
   * Returns a Vertical Slab {@link ItemStack} {@link VerticalSlabUtils#getDefaultInstance() default instance}.
   */
  @Override
  public ItemStack getDefaultInstance() {
    return VerticalSlabUtils.getDefaultInstance();
  }

  /**
   * Returns the correct burn time for the given Vertical Slab {@link ItemStack}.
   */
  @Override
  public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
    BlockState referredSlabState = VerticalSlabUtils.getReferredSlabState(itemStack);
    if (referredSlabState != null) {
      return ForgeHooks.getBurnTime(VerticalSlabUtils.slabStateMap.get(referredSlabState.getBlock().asItem()).getBlock().asItem().getDefaultInstance(), recipeType);
    }
    return super.getBurnTime(itemStack, recipeType);
  }

  /**
   * Returns the correct name {@link Component} for the given Vertical Slab {@link ItemStack}.
   */
  @Override
  public Component getName(ItemStack itemStack) {
    BlockState referredSlabState = VerticalSlabUtils.getReferredSlabState(itemStack);
    if (referredSlabState != null) {
      Item referredSlab = referredSlabState.getBlock().asItem();
      return referredSlab.getName(referredSlab.getDefaultInstance());
    }
    return super.getName(itemStack);
  }

  /**
   * Forces BlockEntity update on client.
   * Refer to {@link BlockItem#updateCustomBlockEntityTag(Level, Player, BlockPos, ItemStack)} for implementation.
   * 
   * @param pos
   * @param level
   * @param player
   * @param itemStack
   * @param state
   * @return whether the {@link BlockEntity} NBTs were updated.
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
            JustVerticalSlabsLoader.LOGGER.trace("Forced VerticalSlabBlockEntity update on client.");
            return true;
          }
        }
      }
      return false;
    }
    return updated;
  }
}
