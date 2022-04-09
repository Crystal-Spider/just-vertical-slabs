package crystalspider.justverticalslabs;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import java.util.function.Supplier;

@Mod(JustVerticalSlabsLoader.MODID)
public class JustVerticalSlabsLoader {
  public static final String MODID = "justverticalslabs";
  public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
  public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
  public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, MODID);
  
  // TODO: Remove deferred block registring and instead add as many ItemStacks as needed to the creative tab by overriding fillItemCategory in your item class
  public static final RegistryObject<VerticalSlabBlock> VERTICAL_SLAB = registerBlock("vertical_slab", () -> new VerticalSlabBlock(BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
  public static final RegistryObject<BlockEntityType<VerticalSlabBlockEntity>> VERTICAL_SLAB_BLOCK_ENTITY = BLOCK_ENTITIES.register("vertical_slab", () -> BlockEntityType.Builder.of(VerticalSlabBlockEntity::new, new VerticalSlabBlock(BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(2.0F, 3.0F).sound(SoundType.WOOD))).build(null));

  public JustVerticalSlabsLoader() {
    IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
    BLOCKS.register(bus);
    ITEMS.register(bus);
    MinecraftForge.EVENT_BUS.register(this);
  }

  @SubscribeEvent
  public void onModelRegistryEvent(ModelRegistryEvent event) {
    ModelLoaderRegistry.registerLoader(new ResourceLocation(MODID, "vertical_slab_loader"), new VerticalSlabModelLoader());
  }

  private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
    RegistryObject<T> registeredBlock = BLOCKS.register(name, block);
    ITEMS.register(name, () -> new BlockItem(registeredBlock.get(), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)));
    return registeredBlock;
  }
}
