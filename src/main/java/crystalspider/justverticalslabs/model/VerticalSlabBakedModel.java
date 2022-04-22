package crystalspider.justverticalslabs.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Transformation;

import crystalspider.justverticalslabs.blocks.verticalslab.VerticalSlabBlockEntity;
import crystalspider.justverticalslabs.model.item.VerticalSlabItemOverrides;
import crystalspider.justverticalslabs.model.perpective.VerticalSlabPerspectiveTransformer;
import crystalspider.justverticalslabs.model.vertex.VerticalSlabVertexTransformer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;
import net.minecraftforge.client.model.pipeline.LightUtil;

public class VerticalSlabBakedModel implements IDynamicBakedModel {
  private final BakedModel jsonBakedModel;
  private final ItemOverrides overrides;
  private final HashMap<VerticalSlabModelKey, List<BakedQuad>> bakedQuadsCache = new HashMap<VerticalSlabModelKey, List<BakedQuad>>();

  public VerticalSlabBakedModel(BakedModel jsonBakedModel) {
    this.jsonBakedModel = jsonBakedModel;
    this.overrides = new VerticalSlabItemOverrides();
  }

  @Override
  public boolean isGui3d() {
    return false;
  }

  @Override
  public boolean isCustomRenderer() {
    return false;
  }

  @Override
  public boolean usesBlockLight() {
    return true;
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
    return Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(MissingTextureAtlasSprite.getLocation());
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
  public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand, IModelData modelData) {
    BlockState referringBlockState = modelData.getData(VerticalSlabBlockEntity.REFERRING_BLOCK_STATE);
    if (referringBlockState != null) {
      VerticalSlabModelKey verticalSlabModelKey = new VerticalSlabModelKey(side, referringBlockState);
      if (!bakedQuadsCache.containsKey(verticalSlabModelKey)) {
        ArrayList<BakedQuad> bakedQuads = new ArrayList<BakedQuad>();
        List<BakedQuad> jsonBakedQuads = jsonBakedModel.getQuads(state, side, rand, modelData);
        if (jsonBakedQuads.size() > 0) {
          if (side != null) {
            BakedQuad referringBakedQuad = getReferringBakedQuad(referringBlockState, side, rand, modelData);
            if (referringBakedQuad != null) {
              for (BakedQuad jsonBakedQuad : jsonBakedQuads) {
                BakedQuadBuilder quadBuilder = new BakedQuadBuilder(referringBakedQuad.getSprite());
                LightUtil.putBakedQuad(new VerticalSlabVertexTransformer(quadBuilder, referringBakedQuad), jsonBakedQuad);
                bakedQuads.add(quadBuilder.build());
              }
            }
          } else {
            for (BakedQuad jsonBakedQuad : jsonBakedQuads) {
              BakedQuad referringBakedQuad = getReferringBakedQuad(referringBlockState, jsonBakedQuad.getDirection(), rand, modelData);
              if (referringBakedQuad != null) {
                BakedQuadBuilder quadBuilder = new BakedQuadBuilder(referringBakedQuad.getSprite());
                LightUtil.putBakedQuad(new VerticalSlabVertexTransformer(quadBuilder, referringBakedQuad), jsonBakedQuad);
                bakedQuads.add(quadBuilder.build());
              }
            }
          }
        }
        bakedQuadsCache.put(verticalSlabModelKey, bakedQuads);
      }
      return bakedQuadsCache.get(verticalSlabModelKey);
    }
    return Collections.emptyList();
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

  /**
   * Returns the first {@link BakedQuad} for the referred block given {@link Direction side}, {@link Random rand} and {@link IModelData modelData}.
   * Returns null if no {@link BakedQuad} could be retrieved.
   * Logs debug warnings when no {@link BakedQuad} can be retrieved or when there are more than 1 {@link BakedQuad} to choose from.
   * 
   * @param referringBlockState - {@link BlockState} of the referred block.
   * @param side - {@link Direction side} of the block being rendered.
   * @param rand - {@link Random rand} parameter.
   * @param modelData - {@link IModelData model data} of the block being rendered.
   * @return the first {@link BakedQuad} for the referred block, or null if none could be retrieved.
   */
  @Nullable
  private BakedQuad getReferringBakedQuad(BlockState referringBlockState, Direction side, Random rand, IModelData modelData) {
    List<BakedQuad> referringBakedQuads = getReferringBakedModel(referringBlockState).getQuads(referringBlockState, side, rand, getReferringModelData(referringBlockState, modelData));
    if (referringBakedQuads.size() > 0) {
      if (referringBakedQuads.size() > 1) {
        // TODO: log warning.
      }
      return referringBakedQuads.get(0);
    }
    // TODO: log warning.
    return null;
  }
}
