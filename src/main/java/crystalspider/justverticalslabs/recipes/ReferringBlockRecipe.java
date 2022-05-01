package crystalspider.justverticalslabs.recipes;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;

import crystalspider.justverticalslabs.JustVerticalSlabsLoader;
import crystalspider.justverticalslabs.items.VerticalSlabBlockItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class ReferringBlockRecipe implements Recipe<CraftingContainer> {
  public static final String ID = "referring_block_recipe";
  private static final ResourceLocation RESOURCE_LOCATION = new ResourceLocation(JustVerticalSlabsLoader.MODID, ID);
  private int firstIndex = 0;

  @Override
  public boolean matches(CraftingContainer craftingContainer, Level level) {
    boolean matchFound = false;
    int containerWidth = craftingContainer.getWidth();
    for (int w = 0; w < containerWidth - 1; w++) {
      for (int h = 0; h < craftingContainer.getHeight(); h++) {
        if (isVerticalSlab(craftingContainer.getItem(w + h * containerWidth)) && isVerticalSlab(craftingContainer.getItem(w + h * containerWidth + 1))) {
          if (matchFound) {
            matchFound = false;
            break;
          }
          matchFound = true;
        }
      }
    }
    return matchFound;
  }

  @Override
  public ItemStack assemble(CraftingContainer craftingContainer) {
    return getReferringBlockState(craftingContainer.getItem(firstIndex)).getBlock().asItem().getDefaultInstance();
  }

  @Override
  public boolean canCraftInDimensions(int width, int height) {
    return width >= 2 && height >= 1;
  }

  @Override
  public ItemStack getResultItem() {
    return Items.OAK_PLANKS.getDefaultInstance();
  }

  @Override
  public ResourceLocation getId() {
    return RESOURCE_LOCATION;
  }

  @Override
  public RecipeSerializer<ReferringBlockRecipe> getSerializer() {
    return JustVerticalSlabsLoader.REFERRING_BLOCK_RECIPE_SERIALIZER.get();
  }

  @Override
  public RecipeType<CraftingRecipe> getType() {
    return RecipeType.CRAFTING;
  }

  private boolean isVerticalSlab(ItemStack itemStack) {
    return itemStack.getItem() instanceof VerticalSlabBlockItem && itemStack.getCount() > 0 && getReferringBlockState(itemStack) != null;
  }

  @Nullable
  private BlockState getReferringBlockState(ItemStack itemStack) {
    CompoundTag compoundTag = itemStack.getTagElement("BlockEntityTag");
    if (compoundTag != null) {
      compoundTag = compoundTag.getCompound("referringBlockState");
      if (compoundTag != null) {
        return NbtUtils.readBlockState(compoundTag);
      }
    }
    return null;
  }

  public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<ReferringBlockRecipe> {
    public static final String ID = ReferringBlockRecipe.ID + "_serializer";
    public static final ResourceLocation RESOURCE_LOCATION = new ResourceLocation(JustVerticalSlabsLoader.MODID, ID);
  
    @Override
    public ReferringBlockRecipe fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
      return new ReferringBlockRecipe();
    }
  
    @Override
    public ReferringBlockRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf friendlyByteBuf) {
      return new ReferringBlockRecipe();
    }
  
    @Override
    public void toNetwork(FriendlyByteBuf friendlyByteBuf, ReferringBlockRecipe referringBlockRecipe) {}
  }
}
