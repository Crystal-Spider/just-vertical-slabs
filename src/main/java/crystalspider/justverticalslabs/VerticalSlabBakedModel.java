package crystalspider.justverticalslabs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;

public class VerticalSlabBakedModel implements IDynamicBakedModel {
  private final Function<Material, TextureAtlasSprite> spriteGetter;
  private final ModelState modelTransform;
  private final ItemOverrides overrides;
  private final ResourceLocation modelLocation;
  // TODO: implement cache.
  private final HashMap<VerticalSlabModelKey, List<BakedQuad>> bakedQuadCache = new HashMap<VerticalSlabModelKey, List<BakedQuad>>();

  public VerticalSlabBakedModel(Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation) {
    this.spriteGetter = spriteGetter;
    this.modelTransform = modelTransform;
    this.overrides = overrides;
    this.modelLocation = modelLocation;
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
    VoxelShape shape = ((VerticalSlabBlock) state.getBlock()).getShape(state, null, null, null);
    ArrayList<BakedQuad> bakedQuads = new ArrayList<BakedQuad>();

    ModelResourceLocation modelResourceLocation = BlockModelShaper.stateToModelLocation(referringBlockState);
    BakedModel model = Minecraft.getInstance().getModelManager().getModel(modelResourceLocation);
    return model.getQuads(referringBlockState, side, rand, extraData);
  }
}
