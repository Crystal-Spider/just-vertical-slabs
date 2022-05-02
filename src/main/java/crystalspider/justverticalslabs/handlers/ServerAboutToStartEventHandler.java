package crystalspider.justverticalslabs.handlers;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import crystalspider.justverticalslabs.JustVerticalSlabsLoader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ServerAboutToStartEventHandler {
  @SubscribeEvent(priority = EventPriority.LOWEST)
  public void onServerAboutToStartEvent(ServerAboutToStartEvent event) {
    Map<SlabBlock, Block> slabMap = new HashMap<SlabBlock, Block>(); 
    List<Block> slabs = Collections.emptyList(); // TODO: get list of all blocks/items matching BlockTags.SLABS.
    for (Block slab : slabs) {
      // TODO: get block/item from slab
      // TODO: save with blockMap.put(slab, value);
    }
    JustVerticalSlabsLoader.slabMap = ImmutableMap.copyOf(slabMap);
  }
}
