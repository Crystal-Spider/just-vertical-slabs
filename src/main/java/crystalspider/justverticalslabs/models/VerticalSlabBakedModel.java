package crystalspider.justverticalslabs.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import crystalspider.justverticalslabs.VerticalSlabBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraft.client.resources.model.BakedModel;

public class VerticalSlabBakedModel implements IDynamicBakedModel {
  private final BakedModel bakedModel;
  private final ItemOverrides overrides;
  // TODO: implement cache.
  private final HashMap<VerticalSlabModelKey, List<BakedQuad>> bakedQuadCache = new HashMap<VerticalSlabModelKey, List<BakedQuad>>();

  public VerticalSlabBakedModel(BakedModel bakedModel, ItemOverrides overrides) {
    this.bakedModel = bakedModel;
    this.overrides = overrides;
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
  public TextureAtlasSprite getParticleIcon() {
    // TODO: may be related to NullPointerException when destroying block.
    return null;
  }

  @Override
  public ItemOverrides getOverrides() {
    // TODO: check if these overrides are correct.
    return overrides;
  }

  @Override
  public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand, IModelData extraData) {
    ArrayList<BakedQuad> newbakedQuads = new ArrayList<BakedQuad>();
    BlockState referringBlockState = extraData.getData(VerticalSlabBlockEntity.REFERRING_BLOCK_STATE);
    if (referringBlockState != null && side != null) {
      System.out.println("Quads Direction: " + side.toString());
      System.out.println("Quads BlockState: " + referringBlockState.toString());
      List<BakedQuad> bakedQuads = bakedModel.getQuads(referringBlockState, side, rand, extraData);
      List<BakedQuad> referringBakedQuads;
      BakedModel referringBlockModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(referringBlockState);
      if (referringBlockState.hasBlockEntity()) {
        referringBakedQuads = referringBlockModel.getQuads(referringBlockState, side, rand, ((EntityBlock) referringBlockState.getBlock()).newBlockEntity(new BlockPos(0, 0, 0), referringBlockState).getModelData());
      } else {
        referringBakedQuads = referringBlockModel.getQuads(referringBlockState, side, rand, extraData);
      }
      System.out.println("RBQs: " + referringBakedQuads.size());
      System.out.println("BQs: " + bakedQuads.size());
      System.out.println("---------------------------------------------------------------");
      if (referringBakedQuads.size() > 0) {
        if (referringBakedQuads.size() > 1) {
          // TODO: log warning.
        }
        BakedQuad referringBakedQuad = referringBakedQuads.get(0);
        for (BakedQuad bakedQuad : bakedQuads) {
          newbakedQuads.add(new BakedQuad(bakedQuad.getVertices(), bakedQuad.getTintIndex(), bakedQuad.getDirection(), referringBakedQuad.getSprite(), bakedQuad.isShade()));
        }
      } else {
        // TODO: log warning.
      }
    }
    return newbakedQuads;
  }
}
