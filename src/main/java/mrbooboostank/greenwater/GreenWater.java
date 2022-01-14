package mrbooboostank.greenwater;

import java.awt.Color;

import mrbooboostank.greenwater.blocks.GreenWaterBlock;
import mrbooboostank.greenwater.fluids.GreenWaterFluid;
import mrbooboostank.greenwater.items.GreenWaterBottle;
import mrbooboostank.greenwater.particles.CustomDripParticle;
import mrbooboostank.greenwater.particles.CustomSuspendedParticle;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.SplashParticle;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
// import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogColors;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(GreenWater.MODID)
public class GreenWater {

    public static final String MODID = "greenwater";

    // [[[Register Fluids]]]
    // Every fluid needs an _flowing, bucket, and block
    private static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, MODID);
    
    public static RegistryObject<FlowingFluid> green_water_still = FLUIDS.register("green_water_still", () ->
		new GreenWaterFluid.Source()
    );

    public static RegistryObject<FlowingFluid> green_water_flowing = FLUIDS.register("green_water_flowing", () ->
    	new GreenWaterFluid.Flowing()
    );

    // [[[Register blocks]]]
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    
    public static RegistryObject<LiquidBlock> green_water = BLOCKS.register("green_water", () ->
    	new GreenWaterBlock(green_water_still, BlockBehaviour.Properties.of(Material.WATER, MaterialColor.COLOR_GREEN)
    		.noCollission()
    		.strength(100.0F)
    		.noDrops()
    	)	
    );

	// TODO temporary so I can jump here
    // [[[Register Particles]]]
    private static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MODID);
    
    public static RegistryObject<SimpleParticleType> dripping_green_water = PARTICLES.register("dripping_green_water", () ->
		new SimpleParticleType(false)
    );
    
    public static RegistryObject<SimpleParticleType> falling_green_water = PARTICLES.register("falling_green_water", () ->
		new SimpleParticleType(false)
    );
    
    public static RegistryObject<SimpleParticleType> splashing_green_water = PARTICLES.register("splashing_green_water", () ->
		new SimpleParticleType(false)
    );
    
    public static RegistryObject<SimpleParticleType> suspended_green_water = PARTICLES.register("suspended_green_water", () ->
		new SimpleParticleType(false)
    );
    
    
    // [[[Register Items]]]
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    
    public static RegistryObject<Item> green_water_bucket = ITEMS.register("green_water_bucket", () ->
        new BucketItem(green_water_still, new Item.Properties()
        	.stacksTo(1)
        	.tab(CreativeModeTab.TAB_MISC)
        )
    );
    
    public static RegistryObject<Item> green_water_bottle = ITEMS.register("green_water_bottle", () ->
    	new GreenWaterBottle(new Item.Properties()
        	.stacksTo(1)
        	.tab(CreativeModeTab.TAB_MISC)
        )
    );

    // makeColor(98, 108, 139, 255)
    public GreenWater() {
    	// TODO temporary so I can jump here
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

    	//printColor(6450315);

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        FLUIDS.register(modEventBus);
        PARTICLES.register(modEventBus);
        
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
		
        // Register ourselves for server and other game events we are interested in
    }
    
	// convenience helper methods
	public static int makeColor(int R, int G, int B, int A) {
		return new Color(R, G, B, A).getRGB();
	}
	
	public static void printColor(int value) {
		System.out.println("Color is:");
		System.out.println(new Color(value));
	}

	// TODO temporary so I can jump here
	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
	// should make particles work
	public static class ParticleRegister {
		// needs to be a static method also?
		@SubscribeEvent
		public static void registerParticle(ParticleFactoryRegisterEvent event) {
			Minecraft minecraft = Minecraft.getInstance();
			minecraft.particleEngine.register(GreenWater.dripping_green_water.get(), CustomDripParticle.GreenWaterHangProvider::new);
			minecraft.particleEngine.register(GreenWater.falling_green_water.get(), CustomDripParticle.GreenWaterFallProvider::new);
			minecraft.particleEngine.register(GreenWater.splashing_green_water.get(), SplashParticle.Provider::new);
			minecraft.particleEngine.register(GreenWater.suspended_green_water.get(), CustomSuspendedParticle.GreenWaterSuspendedProvider::new);
			System.out.println("Particles registered successfully!");
		}
	}
    
    public static final Tag.Named<Block> GREEN_WATER = BlockTags.createOptional( new ResourceLocation(MODID, "green_water"));
	
	private void commonSetup(final FMLCommonSetupEvent event) {// where pre-init stuff should go
	}	
	
	private void clientSetup(final FMLClientSetupEvent event) {// where client stuff should go
		event.enqueueWork(() -> {
			RenderType renderType = RenderType.translucent();
			ItemBlockRenderTypes.setRenderLayer(GreenWater.green_water_still.get(), renderType);
			ItemBlockRenderTypes.setRenderLayer(GreenWater.green_water_flowing.get(), renderType);
			System.out.println("Client setup finished!");
		});
	}

	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
	public static class RealTimeEvents {
		
		@SubscribeEvent(receiveCanceled = true)
		public static void greenWaterBottleCreate(RightClickItem event) {
			//if (event.getSide().equals(LogicalSide.SERVER)) {
				Player playerIn = event.getPlayer();
				EquipmentSlot handEquip = event.getHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
				if (playerIn.getItemBySlot(handEquip).getItem().equals(Items.GLASS_BOTTLE)) {
					event.setCanceled(true);
					System.out.println(event.getCancellationResult());
					System.out.println("Glass bottle detected!");
				}
				// event.setCanceled(true);
				System.out.println("Event fired!");
			//}
		}
		
		
		// fog colors event
		@SubscribeEvent
		@OnlyIn(Dist.CLIENT)
		public static void onFogColors(FogColors event) {
			Camera camera = event.getCamera();
			if (camera.getFluidInCamera() == FogType.WATER && camera.getBlockAtCamera().is(GREEN_WATER)) {
				event.setRed(0F);
				event.setGreen(1F);
				event.setBlue(0F);
			}
		}
	}
}