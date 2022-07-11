package crystalspider.justverticalslabs.blocks.state;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;

/**
 * Utility wrapper for Block State position sensitive functions used to retrieve Block Properties.
 */
@FunctionalInterface
public interface PosFunctionBi<G extends BlockGetter, T1, T2, R> {
  public abstract R apply(G getter, BlockPos pos, T1 parameter1, T2 parameter2);
}
