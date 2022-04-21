package crystalspider.justverticalslabs.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.common.model.TransformationHelper;
import crystalspider.justverticalslabs.blocks.verticalslab.VerticalSlabBlockEntity;
import crystalspider.justverticalslabs.model.item.VerticalSlabItemOverrides;
import crystalspider.justverticalslabs.model.perpective.VerticalSlabPerspectiveTransformer;
import crystalspider.justverticalslabs.model.vertex.VerticalSlabVertexTransformer;

public class VerticalSlabBakedModel implements IDynamicBakedModel {
  private final BakedModel jsonBakedModel;
  private final ItemOverrides overrides;
  private final HashMap<VerticalSlabModelKey, List<BakedQuad>> bakedQuadsCache = new HashMap<VerticalSlabModelKey, List<BakedQuad>>();

  public VerticalSlabBakedModel(BakedModel jsonBakedModel) {
    this.jsonBakedModel = jsonBakedModel;
    this.overrides = new VerticalSlabItemOverrides();
  }

  public BakedModel getJsonBakedModel() {
    return this.jsonBakedModel;
  }

  @Override
  public boolean isGui3d() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean usesBlockLight() {
    return false;
  }

  @Override
  public boolean isCustomRenderer() {
    return false;
  }

  @Override
  public boolean useAmbientOcclusion() {
    return true;
  }

  @Override
  public ItemOverrides getOverrides() {
    return overrides;
  }

  @Override
  public boolean doesHandlePerspectives() {
    return true;
  }

  @Override
  public BakedModel handlePerspective(TransformType transformType, PoseStack poseStack) {
    Transformation transformation = VerticalSlabPerspectiveTransformer.getTransformation(transformType);
    if (!transformation.isIdentity()) {
      transformation.push(poseStack);
    }
    return this;
  }

  @Override
  @SuppressWarnings("deprecation")
  public TextureAtlasSprite getParticleIcon() {
    // TODO: make a better choice for default particle icon.
    return getReferringBakedModel(Blocks.OAK_PLANKS.defaultBlockState()).getParticleIcon();
  }

  @Override
  public TextureAtlasSprite getParticleIcon(IModelData extraData) {
    BlockState referringBlockState = extraData.getData(VerticalSlabBlockEntity.REFERRING_BLOCK_STATE);
    if (referringBlockState != null) {
      return getReferringBakedModel(referringBlockState).getParticleIcon(getReferringModelData(referringBlockState, extraData));
    }
    return getParticleIcon();
  }

  @Override
  public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand, IModelData extraData) {
    // ArrayList<BakedQuad> bakedQuads = new ArrayList<BakedQuad>();
    // BlockState referringBlockState = extraData.getData(VerticalSlabBlockEntity.REFERRING_BLOCK_STATE);
    // if (referringBlockState != null && side != null) {
    //   VerticalSlabModelKey verticalSlabModelKey = new VerticalSlabModelKey(side, referringBlockState);
    //   if (bakedQuadsCache.containsKey(verticalSlabModelKey)) {
    //     return bakedQuadsCache.get(verticalSlabModelKey);
    //   }
    //   List<BakedQuad> referringBakedQuads = getReferringBakedModel(referringBlockState).getQuads(referringBlockState, side, rand, getReferringModelData(referringBlockState, extraData));
    //   if (referringBakedQuads.size() > 0) {
    //     if (referringBakedQuads.size() > 1) {
    //       // TODO: log warning.
    //     }
    //     BakedQuad referringBakedQuad = referringBakedQuads.get(0);
    //     for (BakedQuad bakedQuad : jsonBakedModel.getQuads(referringBlockState, side, rand, extraData)) {
    //       BakedQuadBuilder quadBuilder = new BakedQuadBuilder(referringBakedQuad.getSprite());
    //       LightUtil.putBakedQuad(new VerticalSlabVertexTransformer(quadBuilder, referringBakedQuad), bakedQuad);
    //       bakedQuads.add(quadBuilder.build());
    //     }
    //   } else {
    //     // TODO: log warning.
    //   }
    //   // newbakedQuads.addAll(referringBakedQuads);
    //   bakedQuadsCache.put(verticalSlabModelKey, bakedQuads);
    // }
    // return bakedQuads;
    return jsonBakedModel.getQuads(state, side, rand, extraData);
  }

  /**
   * Returns the {@link BakedModel} of the given {@link BlockState}.
   * 
   * @param referringBlockState - {@link BlockState} from which retrieve the {@link BakedModel}.
   * @return the {@link BakedModel} of the given {@link BlockState}.
   */
  private BakedModel getReferringBakedModel(BlockState referringBlockState) {
    return Minecraft.getInstance().getBlockRenderer().getBlockModel(referringBlockState);
  }

  /**
   * Returns the {@link IModelData model data} of the given {@link BlockState}, or the default model data provided if no model data could be retrieved from the given {@link BlockState}.
   * 
   * @param blockState - {@link BlockState} from which retrieve the {@link IModelData model data}.
   * @param defaultData - {@link IModelData default model data} to return if no model data can be retrieved from the given {@link BlockState}.
   * @return {@link IModelData model data} of the given {@link BlockState} or the {@link IModelData default model data}.
   */
  private IModelData getReferringModelData(BlockState blockState, IModelData defaultData) {
    return blockState.hasBlockEntity() ? ((EntityBlock) blockState.getBlock()).newBlockEntity(new BlockPos(0, 0, 0), blockState).getModelData() : defaultData;
  }
}
