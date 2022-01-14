package mrbooboostank.greenwater.fluids;

import java.util.Optional;
import java.util.Random;

import javax.annotation.Nullable;

import mrbooboostank.greenwater.GreenWater;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.FluidAttributes;

public abstract class GreenWaterFluid extends FlowingFluid{
	
	// Mod fluids must override createAttributes.
	@Override
	public FluidAttributes createAttributes() {
		return net.minecraftforge.fluids.FluidAttributes.builder(
			new ResourceLocation("block/water_still"),
			new ResourceLocation("block/water_flow"))
			.overlay(new ResourceLocation("block/water_overlay"))
			.translationKey("block.greenwater.green_water")
			.color(GreenWater.makeColor(0, 255, 0, 255))
			.sound(SoundEvents.BUCKET_FILL, SoundEvents.BUCKET_EMPTY)
			.build(this);
	}
	
	public Fluid getFlowing() {
		return GreenWater.green_water_flowing.get();
	}

	public Fluid getSource() {
		return GreenWater.green_water_still.get();
	}

	public Item getBucket() {
		return GreenWater.green_water_bucket.get();
	}

	public void animateTick(Level level, BlockPos pos, FluidState state, Random random) {
		if (!state.isSource() && !state.getValue(FALLING)) {
			if (random.nextInt(64) == 0) {
				level.playLocalSound((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, SoundEvents.WATER_AMBIENT, SoundSource.BLOCKS, random.nextFloat() * 0.25F + 0.75F, random.nextFloat() + 0.5F, false);
			}
		} 
		else if (random.nextInt(10) == 0) {
			level.addParticle(GreenWater.suspended_green_water.get(), (double)pos.getX() + random.nextDouble(), (double)pos.getY() + random.nextDouble(), (double)pos.getZ() + random.nextDouble(), 0.0D, 0.0D, 0.0D);
		}
	}

	@Nullable
	public ParticleOptions getDripParticle() {
		return GreenWater.dripping_green_water.get();
	}

	// Can the fluid infinite pool?
	protected boolean canConvertToSource() {
		return false;
	}

	protected void beforeDestroyingBlock(LevelAccessor accessor, BlockPos pos, BlockState state) {
		BlockEntity blockentity = state.hasBlockEntity() ? accessor.getBlockEntity(pos) : null;
		Block.dropResources(state, accessor, pos, blockentity);
	}

	public int getSlopeFindDistance(LevelReader reader) {
		return 4;
	}

	public BlockState createLegacyBlock(FluidState state) {
		return GreenWater.green_water.get().defaultBlockState().setValue(LiquidBlock.LEVEL, Integer.valueOf(getLegacyLevel(state)));
	}

	public boolean isSame(Fluid fluid) {
		return fluid == GreenWater.green_water_still.get() || fluid == GreenWater.green_water_flowing.get();
	}

	public int getDropOff(LevelReader reader) {
		return 1;
	}

	public int getTickDelay(LevelReader reader) {
		return 5;
	}

	public boolean canBeReplacedWith(FluidState state, BlockGetter getter, BlockPos pos, Fluid fluid, Direction direction) {
		return direction == Direction.DOWN && !fluid.is(FluidTags.WATER);
	}

	protected float getExplosionResistance() {
		return 100.0F;
	}

	public Optional<SoundEvent> getPickupSound() {
		return Optional.of(SoundEvents.BUCKET_FILL);
	}

	// Flowing class
	public static class Flowing extends GreenWaterFluid {
		protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> state) {
			super.createFluidStateDefinition(state);
			state.add(LEVEL);
		}
	
		public int getAmount(FluidState state) {
			return state.getValue(LEVEL);
		}
	
		public boolean isSource(FluidState state) {
			return false;
			}
		}
	
	// Source class
	public static class Source extends GreenWaterFluid {
		public int getAmount(FluidState state) {
			return 8;
		}

		public boolean isSource(FluidState state) {
			return true;
		}
	}
}