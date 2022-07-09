package crystalspider.justverticalslabs.blocks;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

/**
 * Cutout Vertical Slab Block.
 */
public class CutoutVerticalSlabBlock extends VerticalSlabBlock {
  public CutoutVerticalSlabBlock() {
    super(Material.WOOD);
  }

  @Override
  public boolean useShapeForLightOcclusion(BlockState state) {
    return true;
  }
}
