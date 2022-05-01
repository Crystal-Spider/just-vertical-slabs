package crystalspider.justverticalslabs.recipes;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

/**
 * TODO:
 * from block to vertical slab
 * from vertical slab to block
 * from vertical slab to slab
 * from slab to vertical slab
 * from slab to block
 */
// public class VerticalSlabRecipe implements CraftingRecipe {
//   @Override
//   public boolean matches(CraftingContainer craftingContainer, Level level) {
//     int itemCount = 0;
//     ItemStack slab = null;
//     for (int c = 0; c < craftingContainer.getContainerSize() && itemCount <= 1; c++) {
//       ItemStack itemStack = craftingContainer.getItem(c);
//       if (!itemStack.isEmpty()) {
//         itemCount++;
//         if (itemStack.is(ItemTags.SLABS)) {
//           slab = itemStack;
//         }
//       }
//     }
//     // Could be useful for other recipes (maybe):
//     // level.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING).stream().anyMatch(recipe -> {
//     //   // more checks
//     //   return recipe.getResultItem().is(ItemTags.SLABS);
//     // });
//     return itemCount == 1 && slab != null;
//   }

//   @Override
//   public ItemStack assemble(CraftingContainer craftingContainer) {
//     int itemCount = 0;
//     ItemStack slab = null;
//     for (int c = 0; c < craftingContainer.getContainerSize() && itemCount <= 1; c++) {
//       ItemStack itemStack = craftingContainer.getItem(c);
//       if (!itemStack.isEmpty()) {
//         itemCount++;
//         if (itemStack.is(ItemTags.SLABS)) {
//           slab = itemStack;
//         }
//       }
//     }
//     // return VerticalSlabBlockItem.getItemStackWithState(JustVerticalSlabsLoader.VERTICAL_SLAB_BLOCK.get(), referringBlockState);
//     return slab;
//   }

//   @Override
//   public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
//     // TODO Auto-generated method stub
//     return false;
//   }

//   @Override
//   public ItemStack getResultItem() {
//     // TODO Auto-generated method stub
//     return null;
//   }

//   @Override
//   public ResourceLocation getId() {
//     // TODO Auto-generated method stub
//     return null;
//   }

//   @Override
//   public RecipeSerializer<?> getSerializer() {
//     // TODO Auto-generated method stub
//     return null;
//   }
// }
