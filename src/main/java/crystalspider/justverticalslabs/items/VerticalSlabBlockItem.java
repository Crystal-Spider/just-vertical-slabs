package crystalspider.justverticalslabs.items;

import java.util.ArrayList;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class VerticalSlabBlockItem extends BlockItem {
  private final ArrayList<BlockState> referringBlockStates = new ArrayList<BlockState>();

  public VerticalSlabBlockItem(Block block, Properties properties, ArrayList<BlockState> referringBlockStates) {
    super(block, properties);
    this.referringBlockStates.addAll(referringBlockStates);
  }

  @Override
  public void fillItemCategory(CreativeModeTab creativeModeTab, NonNullList<ItemStack> itemStacks) {
    if (this.allowdedIn(creativeModeTab)) {
      for(BlockState referringBlockState : referringBlockStates) {
        itemStacks.add(getItemStackWithState(referringBlockState));
      }
    }
  }

  private ItemStack getItemStackWithState(BlockState referringBlockState) {
    ItemStack itemStack = new ItemStack(this);
    CompoundTag referringBlockTag = new CompoundTag();
    referringBlockTag.put("referringBlockState", NbtUtils.writeBlockState(referringBlockState));
    itemStack.addTagElement("BlockEntityTag", referringBlockTag);
    return itemStack;
  }
}
