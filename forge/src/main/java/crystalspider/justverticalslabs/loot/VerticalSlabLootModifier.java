package crystalspider.justverticalslabs.loot;

import java.util.function.Supplier;

import javax.annotation.Nonnull;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import crystalspider.justverticalslabs.blocks.VerticalSlabBlock;
import crystalspider.justverticalslabs.blocks.VerticalSlabBlockEntity;
import crystalspider.justverticalslabs.utils.VerticalSlabUtils;
import crystalspider.justverticalslabs.utils.VerticalSlabUtils.MapsManager;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;

/**
 * Vertical Slabs Loot Modifier.
 */
public class VerticalSlabLootModifier extends LootModifier {
  public static final Supplier<Codec<VerticalSlabLootModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(inst -> codecStart(inst).apply(inst, VerticalSlabLootModifier::new)));

  public VerticalSlabLootModifier(LootItemCondition[] conditionsIn) {
    super(conditionsIn);
  }

  @Nonnull
  @Override
  @SuppressWarnings("null")
  public ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
    if (context.getParamOrNull(LootContextParams.BLOCK_ENTITY) instanceof VerticalSlabBlockEntity) {
      ObjectArrayList<ItemStack> loot = new ObjectArrayList<ItemStack>();
      for (ItemStack itemStack : generatedLoot) {
        BlockState referredSlabState = VerticalSlabUtils.getReferredSlabState(itemStack);
        if (referredSlabState != null) {
          LootTable referredLootTable = context.getLevel().getServer().getLootTables().get(referredSlabState.getBlock().getLootTable());
          LootContext.Builder builder = new LootContext.Builder(context).withOptionalParameter(LootContextParams.BLOCK_ENTITY, null).withParameter(LootContextParams.BLOCK_STATE, referredSlabState);
          for (ItemStack slabLoot : referredLootTable.getRandomItems(builder.create(LootContextParamSets.BLOCK))) {
            Item itemLoot = slabLoot.getItem();
            if (MapsManager.slabStateMap.containsKey(itemLoot)) {
              loot.add(VerticalSlabUtils.getVerticalSlabItem(MapsManager.slabStateMap.get(itemLoot), VerticalSlabUtils.isTranslucent(itemLoot)));
            } else {
              loot.add(slabLoot);
            }
            BlockState verticalSlaBlockState = context.getParamOrNull(LootContextParams.BLOCK_STATE);
            if (verticalSlaBlockState != null && verticalSlaBlockState.getValue(VerticalSlabBlock.DOUBLE)) {
              for (ItemStack itemStackLoot : loot) {
                itemStackLoot.setCount(itemStackLoot.getCount() * 2);
              }
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
  // public static class Serializer extends GlobalLootModifierSerializer<VerticalSlabLootModifier> {
  //   @Override
  //   public VerticalSlabLootModifier read(ResourceLocation name, JsonObject json, LootItemCondition[] conditionsIn) {
  //     return new VerticalSlabLootModifier(conditionsIn);
  //   }

  //   @Override
  //   public JsonObject write(VerticalSlabLootModifier instance) {
  //     return makeConditions(instance.conditions);
  //   }
  // }

  @Override
  public Codec<? extends IGlobalLootModifier> codec() {
    return CODEC.get();
  }
}
