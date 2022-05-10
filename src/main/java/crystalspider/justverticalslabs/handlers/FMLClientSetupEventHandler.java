package crystalspider.justverticalslabs.handlers;

import crystalspider.justverticalslabs.JustVerticalSlabsLoader;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * {@link FMLClientSetupEvent} handler.
 */
public class FMLClientSetupEventHandler {
  /**
   * Sets the correct {@link RenderType} for {@link crystalspider.justverticalslabs.blocks.VerticalSlabBlock VerticalSlabBlocks}.
   * 
   * @param event
   */
  @SubscribeEvent
  public void setup(FMLClientSetupEvent event) {
    event.enqueueWork(() -> {
      // TODO: Check if it work also for translucent blocks.
      ItemBlockRenderTypes.setRenderLayer(JustVerticalSlabsLoader.VERTICAL_SLAB_BLOCK.get(), RenderType.cutoutMipped());
    });
  }
}
