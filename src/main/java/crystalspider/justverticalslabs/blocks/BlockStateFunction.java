package crystalspider.justverticalslabs.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;

/**
 * Utility wrapper for Block State position sensitive functions used to retrieve Block Properties.
 */
@FunctionalInterface
public interface BlockStateFunction<G extends BlockGetter, T, R> {
  public abstract R apply(G getter, BlockPos pos, T parameter);
}
