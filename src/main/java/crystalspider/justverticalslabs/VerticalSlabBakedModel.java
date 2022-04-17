package crystalspider.justverticalslabs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

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
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;

public class VerticalSlabBakedModel implements IDynamicBakedModel {
  private final BakedModel bakedModel;
  // TODO: implement cache.
  private final HashMap<VerticalSlabModelKey, List<BakedQuad>> bakedQuadCache = new HashMap<VerticalSlabModelKey, List<BakedQuad>>();

  public VerticalSlabBakedModel(BakedModel bakedModel) {
    this.bakedModel = bakedModel;
  }

  @Override
  public boolean useAmbientOcclusion() {
    // TODO Auto-generated method stub
    return false;
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
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ItemOverrides getOverrides() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand, IModelData extraData) {
    BlockState referringBlockState = extraData.getData(VerticalSlabBlockEntity.REFERRING_BLOCK_STATE);
    ArrayList<BakedQuad> newbakedQuads = new ArrayList<BakedQuad>();
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
    for (BakedQuad bakedQuad : bakedQuads) {
      TextureAtlasSprite sprite = bakedQuad.getSprite(); // TODO: Get sprite from referringBakedQuad
      newbakedQuads.add(new BakedQuad(bakedQuad.getVertices(), bakedQuad.getTintIndex(), bakedQuad.getDirection(), sprite, bakedQuad.isShade()));
    }
    return newbakedQuads;
  }
}
