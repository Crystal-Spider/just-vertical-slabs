package crystalspider.justverticalslabs.loot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.gson.JsonObject;

import crystalspider.justverticalslabs.blocks.VerticalSlabBlockEntity;
import crystalspider.justverticalslabs.utils.VerticalSlabUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;

/**
 * Vertical Slabs Loot Modifier.
 */
public class VerticalSlabLootModifier extends LootModifier {
  public VerticalSlabLootModifier(LootItemCondition[] conditionsIn) {
    super(conditionsIn);
  }

  @Nonnull
  @Override
  public List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
    if (context.getParamOrNull(LootContextParams.BLOCK_ENTITY) instanceof VerticalSlabBlockEntity) {
      List<ItemStack> loot = new ArrayList<ItemStack>();
      for (ItemStack itemStack : generatedLoot) {
        BlockState referredSlabState = VerticalSlabUtils.getReferredSlabState(itemStack);
        if (referredSlabState != null) {
          LootTable referredLootTable = context.getLevel().getServer().getLootTables().get(referredSlabState.getBlock().getLootTable());
          LootContext.Builder builder = new LootContext.Builder(context).withOptionalParameter(LootContextParams.BLOCK_ENTITY, null).withParameter(LootContextParams.BLOCK_STATE, referredSlabState);
          for (ItemStack slabLoot : referredLootTable.getRandomItems(builder.create(LootContextParamSets.BLOCK))) {
            if (VerticalSlabUtils.slabStateMap.containsKey(slabLoot.getItem())) {
              loot.add(VerticalSlabUtils.getVerticalSlabItem(VerticalSlabUtils.slabStateMap.get(slabLoot.getItem())));
            } else {
              loot.add(slabLoot);
            }
          }
        }
      }
      return loot;
    }
    return generatedLoot;
  }

  /**
   * {@link VerticalSlabLootModifier} Serializer.
   */
  public static class Serializer extends GlobalLootModifierSerializer<VerticalSlabLootModifier> {
    @Override
    public VerticalSlabLootModifier read(ResourceLocation name, JsonObject json, LootItemCondition[] conditionsIn) {
      return new VerticalSlabLootModifier(conditionsIn);
    }

    @Override
    public JsonObject write(VerticalSlabLootModifier instance) {
      return makeConditions(instance.conditions);
    }
  }
}
