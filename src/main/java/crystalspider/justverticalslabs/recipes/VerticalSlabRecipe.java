package crystalspider.justverticalslabs.recipes;

import javax.annotation.Nullable;

import com.google.common.base.Supplier;
import com.google.gson.JsonObject;

import crystalspider.justverticalslabs.items.VerticalSlabBlockItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistryEntry;

public abstract class VerticalSlabRecipe implements CraftingRecipe {
  private final int width;
  private final int height;

  public VerticalSlabRecipe(int width, int height) {
    this.width = width;
    this.height = height;
  }

  @Override
  public boolean canCraftInDimensions(int width, int height) {
    return width >= this.width && height >= this.height;
  }

  @Override
  public abstract RecipeSerializer<? extends VerticalSlabRecipe> getSerializer();

  protected boolean isVerticalSlab(ItemStack itemStack) {
    return itemStack.getItem() instanceof VerticalSlabBlockItem && itemStack.getCount() > 0 && getReferringBlockState(itemStack) != null;
  }

  @Nullable
  protected BlockState getReferringBlockState(ItemStack itemStack) {
    CompoundTag compoundTag = itemStack.getTagElement("BlockEntityTag");
    if (compoundTag != null) {
      compoundTag = compoundTag.getCompound("referringBlockState");
      if (compoundTag != null) {
        return NbtUtils.readBlockState(compoundTag);
      }
    }
    return null;
  }

  public static abstract class Serializer<T extends VerticalSlabRecipe> extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<T> {
    private final Supplier<T> recipeSupplier;

    protected Serializer(Supplier<T> recipeSupplier) {
      this.recipeSupplier = recipeSupplier;
    }

    @Override
    public final T fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
      return recipeSupplier.get();
    }

    @Override
    public final T fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf friendlyByteBuf) {
      return recipeSupplier.get();
    }

    @Override
    public final void toNetwork(FriendlyByteBuf friendlyByteBuf, T referringBlockRecipe) {}
  }
}
