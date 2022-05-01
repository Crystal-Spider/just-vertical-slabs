package crystalspider.justverticalslabs.recipes;

import crystalspider.justverticalslabs.JustVerticalSlabsLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class ReferringBlockRecipe extends VerticalSlabRecipe {
  public static final String ID = "referring_block_recipe";
  private static final ResourceLocation RESOURCE_LOCATION = new ResourceLocation(JustVerticalSlabsLoader.MODID, ID);

  private int firstIndex = 0;

  public ReferringBlockRecipe() {
    super(2, 1);
  }

  @Override
  public boolean matches(CraftingContainer craftingContainer, Level level) {
    boolean matchFound = false;
    int containerWidth = craftingContainer.getWidth();
    for (int w = 0; w < containerWidth - 1; w++) {
      for (int h = 0; h < craftingContainer.getHeight(); h++) {
        int index = w + h * containerWidth;
        if (isVerticalSlab(craftingContainer.getItem(index)) && isVerticalSlab(craftingContainer.getItem(index + 1))) {
          if (matchFound) {
            matchFound = false;
            break;
          }
          matchFound = true;
          firstIndex = index;
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

  public static class Serializer extends VerticalSlabRecipe.Serializer<ReferringBlockRecipe> {
    public static final String ID = ReferringBlockRecipe.ID + "_serializer";
    public static final ResourceLocation RESOURCE_LOCATION = new ResourceLocation(JustVerticalSlabsLoader.MODID, ID);

    public Serializer() {
      super(ReferringBlockRecipe::new);
    }
  }
}
