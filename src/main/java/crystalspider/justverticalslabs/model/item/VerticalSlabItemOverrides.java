package crystalspider.justverticalslabs.model.item;

import javax.annotation.Nullable;

import crystalspider.justverticalslabs.blocks.verticalslab.VerticalSlabBlockEntity;
import crystalspider.justverticalslabs.model.VerticalSlabBakedModel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.data.ModelDataMap;

public class VerticalSlabItemOverrides extends ItemOverrides {
  @Override
  public BakedModel resolve(BakedModel bakedModel, ItemStack itemStack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int integer) {
    CompoundTag itemStackTag = itemStack.getTag();
    if (itemStackTag != null) {
      CompoundTag blockEntityTag = itemStackTag.getCompound("BlockEntityTag");
      if (blockEntityTag != null) {
        CompoundTag referringBlockStateTag = blockEntityTag.getCompound("referringBlockState");
        if (referringBlockStateTag != null) {
          return new VerticalSlabItemBakedModel((VerticalSlabBakedModel) bakedModel, new ModelDataMap.Builder().withInitial(VerticalSlabBlockEntity.REFERRING_BLOCK_STATE, NbtUtils.readBlockState(referringBlockStateTag)).build());
        }
      }
    }
    return super.resolve(bakedModel, itemStack, level, entity, integer);
  }
}
