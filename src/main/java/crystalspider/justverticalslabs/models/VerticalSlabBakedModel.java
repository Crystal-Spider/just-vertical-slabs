package crystalspider.justverticalslabs.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;
import net.minecraftforge.client.model.pipeline.LightUtil;
import crystalspider.justverticalslabs.blocks.VerticalSlabBlockEntity;
import crystalspider.justverticalslabs.items.VerticalSlabItemOverrides;

public class VerticalSlabBakedModel implements IDynamicBakedModel {
  private final BakedModel bakedModel;
  private final ItemOverrides overrides;
  private final HashMap<VerticalSlabModelKey, List<BakedQuad>> bakedQuadsCache = new HashMap<VerticalSlabModelKey, List<BakedQuad>>();

  public VerticalSlabBakedModel(BakedModel bakedModel) {
    this.bakedModel = bakedModel;
    this.overrides = new VerticalSlabItemOverrides();
  }

  @Override
  public boolean useAmbientOcclusion() {
    return true;
  }

  @Override
  public boolean isGui3d() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean usesBlockLight() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean isCustomRenderer() {
    // TODO Auto-generated method stub
    return false;
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
  public ItemOverrides getOverrides() {
    return overrides;
  }

  @Override
  public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand, IModelData extraData) {
    ArrayList<BakedQuad> newbakedQuads = new ArrayList<BakedQuad>();
    BlockState referringBlockState = extraData.getData(VerticalSlabBlockEntity.REFERRING_BLOCK_STATE);
    if (referringBlockState != null && side != null) {
      VerticalSlabModelKey verticalSlabModelKey = new VerticalSlabModelKey(side, referringBlockState);
      if (bakedQuadsCache.containsKey(verticalSlabModelKey)) {
        return bakedQuadsCache.get(verticalSlabModelKey);
      }
      List<BakedQuad> referringBakedQuads = getReferringBakedModel(referringBlockState).getQuads(referringBlockState, side, rand, getReferringModelData(referringBlockState, extraData));
      if (referringBakedQuads.size() > 0) {
        if (referringBakedQuads.size() > 1) {
          // TODO: log warning.
        }
        BakedQuad referringBakedQuad = referringBakedQuads.get(0);
        for (BakedQuad bakedQuad : bakedModel.getQuads(referringBlockState, side, rand, extraData)) {
          BakedQuadBuilder quadBuilder = new BakedQuadBuilder(referringBakedQuad.getSprite());
          LightUtil.putBakedQuad(new VerticalSlabVertexTransformer(quadBuilder, referringBakedQuad), bakedQuad);
          newbakedQuads.add(quadBuilder.build());
        }
      } else {
        // TODO: log warning.
      }
      bakedQuadsCache.put(verticalSlabModelKey, newbakedQuads);
    }
    return newbakedQuads;
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
