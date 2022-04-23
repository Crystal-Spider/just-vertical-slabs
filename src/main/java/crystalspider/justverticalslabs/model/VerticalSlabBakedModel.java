package crystalspider.justverticalslabs.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;
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

/**
 * Vertical Slab Baked Model ready for rendering.
 */
public class VerticalSlabBakedModel implements IDynamicBakedModel {
  /**
   * Original {@link BakedModel} read from JSON.
   */
  private final BakedModel jsonBakedModel;
  /**
   * {@link ItemOverrides} to use to render items.
   */
  private final ItemOverrides overrides;
  /**
   * {@link BakedQuad} cache. Needed because {@link #getQuads(BlockState, Direction, Random, IModelData)} is called several times every little interval, thus needs to be as fast as possible.
   */
  private final HashMap<VerticalSlabModelKey, List<BakedQuad>> bakedQuadsCache = new HashMap<VerticalSlabModelKey, List<BakedQuad>>();

  public VerticalSlabBakedModel(BakedModel jsonBakedModel) {
    this.jsonBakedModel = jsonBakedModel;
    this.overrides = new VerticalSlabItemOverrides();
  }

  /**
   * See {@link https://mcforge.readthedocs.io/en/1.18.x/rendering/modelloaders/bakedmodel/#isgui3d}.
   */
  @Override
  public boolean isGui3d() {
    return false;
  }

  /**
   * See {@link https://mcforge.readthedocs.io/en/1.18.x/rendering/modelloaders/bakedmodel/#iscustomrenderer}.
   */
  @Override
  public boolean isCustomRenderer() {
    return false;
  }

  /**
   * Whether to enable lighting in GUI when rendered as an item.
   */
  @Override
  public boolean usesBlockLight() {
    return true;
  }

  /**
   * Whether to render the block using ambient occlusion.
   */
  @Override
  public boolean useAmbientOcclusion() {
    return true;
  }

  /**
   * {@link ItemOverrides} to use to render items.
   */
  @Override
  public ItemOverrides getOverrides() {
    return overrides;
  }

  /**
   * Whether {@link #handlePerspective(TransformType, PoseStack)} will be used.
   */
  @Override
  public boolean doesHandlePerspectives() {
    return true;
  }

  /**
   * Handles item model transformations for different {@link TransformType}.
   * Which {@link Transformation} to use is decided by {@link VerticalSlabPerspectiveTransformer#getTransformation(TransformType)}.
   * 
   * @param transformType - {@link TransformType}.
   * @param poseStack - {@link PoseStack} to render.
   * @return this model.
   */
  @Override
  public BakedModel handlePerspective(TransformType transformType, PoseStack poseStack) {
    Transformation transformation = VerticalSlabPerspectiveTransformer.getTransformation(transformType);
    if (!transformation.isIdentity()) {
      transformation.push(poseStack);
    }
    return this;
  }

  /**
   * Returns the default {@link TextureAtlasSprite particle icon} of this model.
   * 
   * @return default {@link TextureAtlasSprite particle icon}.
   */
  @Override
  @SuppressWarnings("deprecation")
  public TextureAtlasSprite getParticleIcon() {
    return Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(MissingTextureAtlasSprite.getLocation());
  }

  /**
   * Returns the {@link TextureAtlasSprite particle icon} of this model based on the given {@link IModelData data}.
   * In details, uses the given {@link IModelData data} to search for the {@link VerticalSlabBlockEntity#REFERRING_BLOCK_STATE referringBlockState} property.
   * If such property is not null, returns the {@link BakedModel#getParticleIcon(IModelData)} of the {@link #getReferringBakedModel(BlockState) referred BakedModel},
   * otherwise returns the {@link #getParticleIcon() default particle icon} of this model.
   * 
   * @param extraData - {@link IModelData} to use to render.
   * @return {@link TextureAtlasSprite particle icon} based on the given {@link IModelData data}.
   */
  @Override
  public TextureAtlasSprite getParticleIcon(IModelData extraData) {
    BlockState referringBlockState = extraData.getData(VerticalSlabBlockEntity.REFERRING_BLOCK_STATE);
    if (referringBlockState != null) {
      return getReferringBakedModel(referringBlockState).getParticleIcon(getReferringModelData(referringBlockState, extraData));
    }
    return getParticleIcon();
  }

  /**
   * Returns the {@link List} of {@link BakedQuad} to use to render the model based on {@link Direction side} and {@link IModelData modelData}.
   * Caches as much as possible to speed up computational time of subsequent calls.
   * {@link IModelData modelData} should contain a {@link VerticalSlabBlockEntity#REFERRING_BLOCK_STATE referringBlockState} property in order to render this model correctly.
   * If such condition is not met, an {@link Collections#emptyList() empty list} is returned and thus nothing will be rendered.
   * 
   * @param state - {@link BlockState} of the block being rendered, null if an item is being rendered.
   * @param side - indicates to which culling {@link Direction face} the {@link BakedQuad baked quads} are associated to.
   *               If null, no culling {@link Direction face} is associated and the {@link BakedQuad baked quads} will always be rendered.
   * @param rand - {@link Random} instance.
   * @param modelData - {@link IModelData} to use to render.
   * @return {@link List} of {@link BakedQuad} based on parameters.
   */
  @Override
  public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData modelData) {
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
