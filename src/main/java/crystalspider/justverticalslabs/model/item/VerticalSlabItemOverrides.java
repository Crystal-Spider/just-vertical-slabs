package crystalspider.justverticalslabs.model.item;

import javax.annotation.Nullable;

import crystalspider.justverticalslabs.model.VerticalSlabBakedModel;
import crystalspider.justverticalslabs.utils.VerticalSlabUtils;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * Vertical Slab Item Overrides, to override default {@link ItemOverrides} and properly render items.
 * See {@link #resolve(BakedModel, ItemStack, ClientLevel, LivingEntity, int)} for more details.
 */
public class VerticalSlabItemOverrides extends ItemOverrides {
  /**
   * Returns a different {@link BakedModel} to use for rendering.
   * Since no BlockEntity is associated to an item, the {@link net.minecraftforge.client.model.data.IModelData IModelData} parameter for methods
   * {@link VerticalSlabBakedModel#getParticleIcon(net.minecraftforge.client.model.data.IModelData) VerticalSlabBakedModel.getParticleIcon(IModelData)} and
   * {@link VerticalSlabBakedModel#getQuads(net.minecraft.world.level.block.state.BlockState, net.minecraft.core.Direction, java.util.Random, net.minecraftforge.client.model.data.IModelData) VerticalSlabBakedModel.getQuads(BlockState, Direction, Random, IModelData)}
   * won't contain the correct and necessary data to render the item correctly.
   * For this reason, an instance of {@link VerticalSlabItemBakedModel} is returned with proper {@link VerticalSlabItemBakedModel#data}.
   * 
   * @param bakedModel - {@link BakedModel} "owning" these {@link ItemOverrides}, in this case it will be an instance of {@link VerticalSlabBakedModel}.
   * @param itemStack - {@link ItemStack} being rendered.
   * @param level - {@link ClientLevel}.
   * @param entity - {@link LivingEntity} holding the {@link ItemStack}.
   * @param integer
   * @return new {@link VerticalSlabItemBakedModel} with proper data to use for rendering.
   */
  @Override
  public BakedModel resolve(BakedModel bakedModel, ItemStack itemStack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int integer) {
    CompoundTag itemStackTag = itemStack.getTag();
    if (itemStackTag != null) {
      CompoundTag blockEntityTag = itemStackTag.getCompound("BlockEntityTag");
      if (blockEntityTag != null) {
        CompoundTag referringBlockStateTag = blockEntityTag.getCompound("referringBlockState");
        if (referringBlockStateTag != null) {
          return new VerticalSlabItemBakedModel((VerticalSlabBakedModel) bakedModel, VerticalSlabUtils.buildModelData(NbtUtils.readBlockState(referringBlockStateTag)));
        }
      }
    }
    return super.resolve(bakedModel, itemStack, level, entity, integer);
  }
}
