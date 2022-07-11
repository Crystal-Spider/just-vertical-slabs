package crystalspider.justverticalslabs;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import crystalspider.justverticalslabs.blocks.CutoutVerticalSlabBlock;
import crystalspider.justverticalslabs.blocks.TranslucentVerticalSlabBlock;
import crystalspider.justverticalslabs.blocks.VerticalSlabBlock;
import crystalspider.justverticalslabs.blocks.VerticalSlabBlockEntity;
import crystalspider.justverticalslabs.handlers.FMLClientSetupEventHandler;
import crystalspider.justverticalslabs.handlers.ModelRegistryEventHandler;
import crystalspider.justverticalslabs.handlers.RecipesUpdateEventHandler;
import crystalspider.justverticalslabs.handlers.RightClickBlockHandler;
import crystalspider.justverticalslabs.handlers.ServerAboutToStartEventHandler;
import crystalspider.justverticalslabs.items.CutoutVerticalSlabBlockItem;
import crystalspider.justverticalslabs.items.TranslucentVerticalSlabBlockItem;
import crystalspider.justverticalslabs.items.VerticalSlabBlockItem;
import crystalspider.justverticalslabs.loot.VerticalSlabLootModifier;
import crystalspider.justverticalslabs.recipes.crafting.recipes.BlockToVerticalSlabCraftingRecipe;
import crystalspider.justverticalslabs.recipes.crafting.recipes.SlabToBlockCraftingRecipe;
import crystalspider.justverticalslabs.recipes.crafting.recipes.SlabToVerticalSlabCraftingRecipe;
import crystalspider.justverticalslabs.recipes.crafting.recipes.VerticalSlabToBlockCraftingRecipe;
import crystalspider.justverticalslabs.recipes.crafting.recipes.VerticalSlabToSlabCraftingRecipe;
import crystalspider.justverticalslabs.recipes.crafting.recipes.WaxedVerticalSlabCraftingRecipe;
import crystalspider.justverticalslabs.recipes.stonecutter.recipes.BlockToVerticalSlabStonecutterRecipe;
import crystalspider.justverticalslabs.utils.VerticalSlabUtils;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * JustVerticalSlabs mod loader.
 */
@Mod(JustVerticalSlabsLoader.MODID)
public class JustVerticalSlabsLoader {
  /**
   * Logger.
   */
  public static final Logger LOGGER = LogUtils.getLogger();

  /**
   * Network channel protocol version.
   */
  public static final String PROTOCOL_VERSION = "1";
  /**
   * {@link SimpleChannel} instance for compatibility client-server.
   */
  public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(VerticalSlabUtils.getResourceLocation("main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

  /**
   * JustVerticalSlab mod ID.
   */
  public static final String MODID = "justverticalslabs";
  /**
   * Vertical Slab resource ID.
   */
  public static final String VERTICAL_SLAB_ID = "vertical_slab";

  /**
   * {@link CreativeModTab} for {@link VerticalSlabBlockItem Vertical Slab Items}.
   */
  public static final CreativeModeTab TAB_JUST_VERTICAL_SLABS = new CreativeModeTab("justVerticalSlabs") {
    public ItemStack makeIcon() {
      return VerticalSlabUtils.getDefaultInstance();
    }
  }.setRecipeFolderName(VERTICAL_SLAB_ID);

  /**
   * {@link Block Blocks} {@link DeferredRegister deferred register}.
   */
  public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
  /**
   * Block Entities {@link DeferredRegister deferred register}.
   */
  public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, MODID);
  /**
   * {@link Item Items} {@link DeferredRegister deferred register}.
   */
  public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
  /**
   * {@link GlobalLootModifierSerializer} {@link DeferredRegister deferred register}.
   */
  public static final DeferredRegister<GlobalLootModifierSerializer<?>> LOOT_MODIFIERS = DeferredRegister.create(ForgeRegistries.Keys.LOOT_MODIFIER_SERIALIZERS, MODID);
  /**
   * {@link RecipeSerializer Recipe Serializer} {@link DeferredRegister deferred register}.
   */
  public static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);

  /**
   * {@link RegistryObject} for {@link CutoutVerticalSlabBlock}.
   */
  public static final RegistryObject<VerticalSlabBlock> CUTOUT_VERTICAL_SLAB_BLOCK = BLOCKS.register("cutout_" + VERTICAL_SLAB_ID, CutoutVerticalSlabBlock::new);
  /**
   * {@link RegistryObject} for {@link TranslucentVerticalSlabBlock}.
   */
  public static final RegistryObject<VerticalSlabBlock> TRANSLUCENT_VERTICAL_SLAB_BLOCK = BLOCKS.register("translucent_" + VERTICAL_SLAB_ID, TranslucentVerticalSlabBlock::new);
  /**
   * {@link RegistryObject} for {@link VerticalSlabBlockEntity}.
   */
  public static final RegistryObject<BlockEntityType<VerticalSlabBlockEntity>> VERTICAL_SLAB_BLOCK_ENTITY = BLOCK_ENTITIES.register(VERTICAL_SLAB_ID + "_block_entity", () -> BlockEntityType.Builder.of(VerticalSlabBlockEntity::new, CUTOUT_VERTICAL_SLAB_BLOCK.get(), TRANSLUCENT_VERTICAL_SLAB_BLOCK.get()).build(null));
  /**
   * {@link RegistryObject} for {@link CutoutVerticalSlabBlockItem}.
   */
  public static final RegistryObject<VerticalSlabBlockItem> CUTOUT_VERTICAL_SLAB_ITEM = ITEMS.register("cutout_" + VERTICAL_SLAB_ID, CutoutVerticalSlabBlockItem::new);
  /**
   * {@link RegistryObject} for {@link TranslucentVerticalSlabBlockItem}.
   */
  public static final RegistryObject<VerticalSlabBlockItem> TRANSLUCENT_VERTICAL_SLAB_ITEM = ITEMS.register("translucent_" + VERTICAL_SLAB_ID, TranslucentVerticalSlabBlockItem::new);
  /**
   * {@link RegistryObject} for {@link VerticalSlabLootModifier} {@link VerticalSlabLootModifier.Serializer Serializer}.
   */
  public static final RegistryObject<VerticalSlabLootModifier.Serializer> VERTICAL_SLAB_LOOT_MODIFIER = LOOT_MODIFIERS.register(VERTICAL_SLAB_ID + "_loot_modifier", VerticalSlabLootModifier.Serializer::new);
  /**
   * {@link RegistryObject} for {@link VerticalSlabToBlockCraftingRecipe} {@link VerticalSlabToBlockCraftingRecipe.Serializer Serializer}.
   */
  public static final RegistryObject<VerticalSlabToBlockCraftingRecipe.Serializer> VERTICAL_SLAB_TO_BLOCK_CRAFTING_RECIPE_SERIALIZER = RECIPES.register(VerticalSlabToBlockCraftingRecipe.Serializer.ID, VerticalSlabToBlockCraftingRecipe.Serializer::new);
  /**
   * {@link RegistryObject} for {@link VerticalSlabToSlabCraftingRecipe} {@link VerticalSlabToSlabCraftingRecipe.Serializer Serializer}.
   */
  public static final RegistryObject<VerticalSlabToSlabCraftingRecipe.Serializer> VERTICAL_SLAB_TO_SLAB_CRAFTING_RECIPE_SERIALIZER = RECIPES.register(VerticalSlabToSlabCraftingRecipe.Serializer.ID, VerticalSlabToSlabCraftingRecipe.Serializer::new);
  /**
   * {@link RegistryObject} for {@link SlabToVerticalSlabCraftingRecipe} {@link SlabToVerticalSlabCraftingRecipe.Serializer Serializer}.
   */
  public static final RegistryObject<SlabToVerticalSlabCraftingRecipe.Serializer> SLAB_TO_VERTICAL_SLAB_CRAFTING_RECIPE_SERIALIZER = RECIPES.register(SlabToVerticalSlabCraftingRecipe.Serializer.ID, SlabToVerticalSlabCraftingRecipe.Serializer::new);
  /**
   * {@link RegistryObject} for {@link BlockToVerticalSlabCraftingRecipe} {@link BlockToVerticalSlabCraftingRecipe.Serializer Serializer}.
   */
  public static final RegistryObject<BlockToVerticalSlabCraftingRecipe.Serializer> BLOCK_TO_VERTICAL_SLAB_CRAFTING_RECIPE_SERIALIZER = RECIPES.register(BlockToVerticalSlabCraftingRecipe.Serializer.ID, BlockToVerticalSlabCraftingRecipe.Serializer::new);
  /**
   * {@link RegistryObject} for {@link SlabToBlockCraftingRecipe} {@link SlabToBlockCraftingRecipe.Serializer Serializer}.
   */
  public static final RegistryObject<SlabToBlockCraftingRecipe.Serializer> SLAB_TO_BLOCK_CRAFTING_RECIPE_SERIALIZER = RECIPES.register(SlabToBlockCraftingRecipe.Serializer.ID, SlabToBlockCraftingRecipe.Serializer::new);
  /**
   * {@link RegistryObject} for {@link WaxedVerticalSlabCraftingRecipe} {@link WaxedVerticalSlabCraftingRecipe.Serializer Serializer}.
   */
  public static final RegistryObject<WaxedVerticalSlabCraftingRecipe.Serializer> WAXED_VERTICAL_SLAB_CRAFTING_RECIPE_SERIALIZER = RECIPES.register(WaxedVerticalSlabCraftingRecipe.Serializer.ID, WaxedVerticalSlabCraftingRecipe.Serializer::new);
  /**
   * {@link RegistryObject} for {@link BlockToVerticalSlabStonecutterRecipe} {@link BlockToVerticalSlabStonecutterRecipe.Serializer Serializer}.
   */
  public static final RegistryObject<BlockToVerticalSlabStonecutterRecipe.Serializer> BLOCK_TO_VERTICAL_SLAB_STONECUTTER_RECIPE_SERIALIZER = RECIPES.register(BlockToVerticalSlabStonecutterRecipe.Serializer.ID, BlockToVerticalSlabStonecutterRecipe.Serializer::new);

  public JustVerticalSlabsLoader() {
    IEventBus minecraftEventBus = MinecraftForge.EVENT_BUS;
    minecraftEventBus.register(new ServerAboutToStartEventHandler());
    minecraftEventBus.register(new RecipesUpdateEventHandler());
    minecraftEventBus.register(new RightClickBlockHandler());
    IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
    BLOCKS.register(modEventBus);
    BLOCK_ENTITIES.register(modEventBus);
    ITEMS.register(modEventBus);
    LOOT_MODIFIERS.register(modEventBus);
    RECIPES.register(modEventBus);
    modEventBus.register(new ModelRegistryEventHandler());
    modEventBus.register(new FMLClientSetupEventHandler());
  }
}
