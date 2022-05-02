package crystalspider.justverticalslabs.recipes;

import crystalspider.justverticalslabs.JustVerticalSlabsLoader;
import crystalspider.justverticalslabs.utils.VerticalSlabUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class ReferringBlockRecipe extends VerticalSlabRecipe {
  public static final String ID = "referring_block_recipe";
  private static final ResourceLocation RESOURCE_LOCATION = VerticalSlabUtils.getResourceLocation(ID);

  public ReferringBlockRecipe() {
    super(2, 1);
  }

  @Override
  public boolean matches(CraftingContainer craftingContainer, Level level) {
    return getMatchIndex(craftingContainer) != -1;
  }

  @Override
  public ItemStack assemble(CraftingContainer craftingContainer) {
    return VerticalSlabUtils.getReferringBlockState(craftingContainer.getItem(getMatchIndex(craftingContainer))).getBlock().asItem().getDefaultInstance();
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

  private int getMatchIndex(CraftingContainer craftingContainer) {
    boolean correctPattern = true;
    int matchIndex = -1, containerWidth = craftingContainer.getWidth();
    for (int h = 0; h < containerWidth && correctPattern; h++) {
      for (int w = 0; w < craftingContainer.getHeight() && correctPattern; w++) {
        int index = w + h * containerWidth;
        if (isVerticalSlab(craftingContainer.getItem(index))) {
          if (isVerticalSlab(craftingContainer.getItem(index + 1))) {
            if (matchIndex == -1) {
              matchIndex = index;
            } else {
              matchIndex = -1;
              correctPattern = false;
            }
          } else if (matchIndex != index - 1) {
            matchIndex = -1;
            correctPattern = false;
          }
        }
      }
    }
    return matchIndex;
  }

  public static class Serializer extends VerticalSlabRecipe.Serializer<ReferringBlockRecipe> {
    public static final String ID = ReferringBlockRecipe.ID + "_serializer";
    public static final ResourceLocation RESOURCE_LOCATION = VerticalSlabUtils.getResourceLocation(ID);

    public Serializer() {
      super(ReferringBlockRecipe::new);
    }
  }
}
