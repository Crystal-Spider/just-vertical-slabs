package crystalspider.justverticalslabs.model.item;

import java.util.List;
import java.util.Random;

import crystalspider.justverticalslabs.model.VerticalSlabBakedModel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;

public class VerticalSlabItemBakedModel extends VerticalSlabBakedModel {
  private final IModelData data;

  public VerticalSlabItemBakedModel(BakedModel jsonBakedModel, IModelData data) {
    super(jsonBakedModel);
    this.data = data;
  }
  
  @Override
  public TextureAtlasSprite getParticleIcon(IModelData extraData) {
    return super.getParticleIcon(data);
  }

  @Override
  public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand, IModelData extraData) {
    return super.getQuads(state, side, rand, data);
  }
}
