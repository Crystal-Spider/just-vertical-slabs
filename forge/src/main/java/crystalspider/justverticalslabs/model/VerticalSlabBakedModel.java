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

import crystalspider.justverticalslabs.JustVerticalSlabsLoader;
import crystalspider.justverticalslabs.blocks.VerticalSlabBlock;
import crystalspider.justverticalslabs.blocks.VerticalSlabBlockEntity;
import crystalspider.justverticalslabs.model.item.VerticalSlabItemOverrides;
import crystalspider.justverticalslabs.model.perpective.VerticalSlabPerspectiveTransformer;
import crystalspider.justverticalslabs.model.utils.BakedQuadUtils;
import crystalspider.justverticalslabs.model.utils.ModelUtils;
import crystalspider.justverticalslabs.utils.VerticalSlabUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;

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

  /**
   * @param jsonBakedModel
   */
  public VerticalSlabBakedModel(BakedModel jsonBakedModel) {
    this.jsonBakedModel = jsonBakedModel;
    this.overrides = new VerticalSlabItemOverrides();
    JustVerticalSlabsLoader.LOGGER.trace("Baked VerticalSlabModel.");
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
   * In details, uses the given {@link IModelData data} to search for the {@link VerticalSlabBlockEntity#REFERRED_SLAB_STATE referredSlabState} property.
   * If such property is not null, returns the {@link BakedModel#getParticleIcon(IModelData)} of the {@link #getReferredBakedModel(BlockState) referred BakedModel},
   * otherwise returns the {@link #getParticleIcon() default particle icon} of this model.
   * 
   * @param extraData - {@link IModelData} to use to render.
   * @return {@link TextureAtlasSprite particle icon} based on the given {@link IModelData data}.
   */
  @Override
  public TextureAtlasSprite getParticleIcon(IModelData extraData) {
    BlockState referredSlabState = extraData.getData(VerticalSlabUtils.REFERRED_SLAB_STATE);
    if (referredSlabState != null) {
      return ModelUtils.getReferredBakedModel(referredSlabState).getParticleIcon(ModelUtils.getReferredModelData(referredSlabState, extraData));
    }
    return getParticleIcon();
  }

  /**
   * Returns the {@link List} of {@link BakedQuad} to use to render the model based on {@link Direction side} and {@link IModelData modelData}.
   * Caches as much as possible to speed up computational time of subsequent calls.
   * {@link IModelData modelData} should contain a {@link VerticalSlabBlockEntity#REFERRED_SLAB_STATE referredSlabState} property in order to render this model correctly.
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
    BlockState referredSlabState = modelData.getData(VerticalSlabUtils.REFERRED_SLAB_STATE);
    if (referredSlabState != null) {
      boolean isDouble = state != null && state.getValue(VerticalSlabBlock.DOUBLE);
      VerticalSlabModelKey verticalSlabModelKey = new VerticalSlabModelKey(side, referredSlabState, isDouble);
      if (!bakedQuadsCache.containsKey(verticalSlabModelKey)) {
        if (isDouble) {
          bakedQuadsCache.put(verticalSlabModelKey, BakedQuadUtils.getReferredBakedQuads(referredSlabState.setValue(SlabBlock.TYPE, SlabType.DOUBLE), side, rand, modelData));
        } else {
          BlockState referredBlockState = VerticalSlabUtils.getReferredBlockState(referredSlabState);
          List<BakedQuad> bakedQuads = new ArrayList<BakedQuad>();
          for (BakedQuad jsonBakedQuad : jsonBakedModel.getQuads(state, side, rand, modelData)) {
            Direction orientation = jsonBakedQuad.getDirection();
            for (BakedQuad referredBakedQuad : BakedQuadUtils.getReferredBakedQuads(referredBlockState != null && VerticalSlabUtils.isTranslucent(referredSlabState) ? referredBlockState : referredSlabState, orientation, rand, modelData)) {
              bakedQuads.add(BakedQuadUtils.getNewBakedQuad(jsonBakedQuad, referredBakedQuad, orientation));
            }
          }
          bakedQuadsCache.put(verticalSlabModelKey, bakedQuads);
        }
      }
      return bakedQuadsCache.get(verticalSlabModelKey);
    }
    return jsonBakedModel.getQuads(state, side, rand, modelData);
  }
}
