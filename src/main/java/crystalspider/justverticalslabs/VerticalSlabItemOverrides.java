package crystalspider.justverticalslabs;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
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
          BlockState referringBlockState = NbtUtils.readBlockState(referringBlockStateTag);
          if (referringBlockState != null) {
            IModelData data = new ModelDataMap.Builder().withInitial(VerticalSlabBlockEntity.REFERRING_BLOCK_STATE, referringBlockState).build();
            return new IDynamicBakedModel() {
              @Override
              public boolean useAmbientOcclusion() {
                return bakedModel.useAmbientOcclusion();
              }
  
              @Override
              public boolean isGui3d() {
                return bakedModel.isGui3d();
              }
  
              @Override
              public boolean usesBlockLight() {
                return bakedModel.usesBlockLight();
              }
  
              @Override
              public boolean isCustomRenderer() {
                return bakedModel.isCustomRenderer();
              }
  
              @Override
              public TextureAtlasSprite getParticleIcon() {
                return bakedModel.getParticleIcon();
              }
  
              @Override
              public TextureAtlasSprite getParticleIcon(IModelData extraData) {
                return bakedModel.getParticleIcon(data);
              }
  
              @Override
              public ItemOverrides getOverrides() {
                return bakedModel.getOverrides();
              }
  
              @Override
              public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand, IModelData extraData) {
                return bakedModel.getQuads(state, side, rand, data);
              }
            };
          }
        }
      }
    }
    return super.resolve(bakedModel, itemStack, level, entity, integer);
  }
}
