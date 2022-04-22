package crystalspider.justverticalslabs.model.item;

import java.util.List;
import java.util.Random;

import com.mojang.blaze3d.vertex.PoseStack;

import crystalspider.justverticalslabs.model.VerticalSlabBakedModel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;

public class VerticalSlabItemBakedModel implements IDynamicBakedModel {
  private final VerticalSlabBakedModel verticalSlabBakedModel;
  private final IModelData data;

  public VerticalSlabItemBakedModel(VerticalSlabBakedModel verticalSlabBakedModel, IModelData data) {
    this.verticalSlabBakedModel = verticalSlabBakedModel;
    this.data = data;
  }
  
  @Override
  public boolean isGui3d() {
    return verticalSlabBakedModel.isGui3d();
  }

  @Override
  public boolean usesBlockLight() {
    return verticalSlabBakedModel.usesBlockLight();
  }

  @Override
  public boolean isCustomRenderer() {
    return verticalSlabBakedModel.isCustomRenderer();
  }

  @Override
  public boolean useAmbientOcclusion() {
    return verticalSlabBakedModel.useAmbientOcclusion();
  }

  @Override
  public ItemOverrides getOverrides() {
    return verticalSlabBakedModel.getOverrides();
  }

  @Override
  public boolean doesHandlePerspectives() {
    return verticalSlabBakedModel.doesHandlePerspectives();
  }

  @Override
  public BakedModel handlePerspective(TransformType transformType, PoseStack poseStack) {
    verticalSlabBakedModel.handlePerspective(transformType, poseStack);
    return this;
  }

  @Override
  public TextureAtlasSprite getParticleIcon() {
    return verticalSlabBakedModel.getParticleIcon();
  }

  @Override
  public TextureAtlasSprite getParticleIcon(IModelData extraData) {
    return verticalSlabBakedModel.getParticleIcon(data);
  }

  @Override
  public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand, IModelData extraData) {
    return verticalSlabBakedModel.getQuads(state, side, rand, data);
  }
}
