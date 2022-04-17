package crystalspider.justverticalslabs;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.mclanguageprovider.MinecraftModContainer;
import net.minecraftforge.fml.mclanguageprovider.MinecraftModLanguageProvider.MinecraftModTarget;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Mod(JustVerticalSlabsLoader.MODID)
public class JustVerticalSlabsLoader {
  public static final String MODID = "justverticalslabs";
  public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
  public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
  public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, MODID);
  
  public static final RegistryObject<VerticalSlabBlock> VERTICAL_SLAB = registerBlock("vertical_slab", () -> new VerticalSlabBlock(BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
  public static final RegistryObject<BlockEntityType<VerticalSlabBlockEntity>> VERTICAL_SLAB_BLOCK_ENTITY = BLOCK_ENTITIES.register("vertical_slab", () -> BlockEntityType.Builder.of(VerticalSlabBlockEntity::new, VERTICAL_SLAB.get()).build(null));

  public JustVerticalSlabsLoader() {
    IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
    BLOCKS.register(bus);
    ITEMS.register(bus);
    BLOCK_ENTITIES.register(bus);
    FMLJavaModLoadingContext.get().getModEventBus().register(this);
  }

  @SubscribeEvent
  public void onModelRegistryEvent(ModelRegistryEvent event) {
    ModelLoaderRegistry.registerLoader(VerticalSlabModelLoader.VERTICAL_SLAB_LOADER, new VerticalSlabModelLoader());
  }

  private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
    RegistryObject<T> registeredBlock = BLOCKS.register(name, block);
    // TODO: get proper list of block states.
    ITEMS.register(name, () -> new VerticalSlabBlockItem(registeredBlock.get(), new Item.Properties(), new ArrayList<BlockState>(List.of(Blocks.DARK_OAK_PLANKS.defaultBlockState()))));
    return registeredBlock;
  }
}
