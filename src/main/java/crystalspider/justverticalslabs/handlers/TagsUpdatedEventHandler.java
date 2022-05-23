package crystalspider.justverticalslabs.handlers;

import crystalspider.justverticalslabs.utils.VerticalSlabUtils.MapsManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

/**
 * {@link TagsUpdatedEvent} handler.
 * Client only.
 */
@EventBusSubscriber(value = Dist.CLIENT, bus = Bus.FORGE)
public class TagsUpdatedEventHandler {
  /**
   * If not already computed, computes maps and adds Vertical Slabs to the search tree.
   * 
   * @param event
   */
  @SubscribeEvent
  public static void onTagsUpdatedEvent(TagsUpdatedEvent event) {
    if (MapsManager.slabStateMap == null) {
      // If maps were not computed yet here it means there is no dedicated server and it's needed to computed them for the client.
      MapsManager.computeMaps();
      MapsManager.addToSearchTree();
    }
  }
}
