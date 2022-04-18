package crystalspider.justverticalslabs;

import javax.annotation.Nullable;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import crystalspider.justverticalslabs.models.VerticalSlabBakedModel;

public class VerticalSlabItemOverrides extends ItemOverrides {
  @Override
  public BakedModel resolve(BakedModel bakedModel, ItemStack itemStack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int integer) {
    return new VerticalSlabBakedModel(bakedModel, NbtUtils.readBlockState(itemStack.getTag().getCompound("BlockEntityTag").getCompound("referringBlockState")));
  }
}
