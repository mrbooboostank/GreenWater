package mrbooboostank.greenwater.particles;

import mrbooboostank.greenwater.GreenWater;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CustomDripParticle extends TextureSheetParticle {
	private final Fluid type;
	protected boolean isGlowing;

	CustomDripParticle(ClientLevel level, double x, double y, double z, Fluid fluid) {
		super(level, x, y, z);
		this.setSize(0.01F, 0.01F);
		this.gravity = 0.06F;
		this.type = fluid;
	}

	protected Fluid getType() {
		return this.type;
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}

	@Override
	public int getLightColor(float color) {
		return this.isGlowing ? 240 : super.getLightColor(color);
	}

	@Override
	public void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;
		this.preMoveUpdate();
		if (!this.removed) {
			this.yd -= (double)this.gravity;
			this.move(this.xd, this.yd, this.zd);
			this.postMoveUpdate();
			if (!this.removed) {
				this.xd *= (double)0.98F;
				this.yd *= (double)0.98F;
				this.zd *= (double)0.98F;
				BlockPos blockpos = new BlockPos(this.x, this.y, this.z);
				FluidState fluidstate = this.level.getFluidState(blockpos);
				if (fluidstate.getType() == this.type && this.y < (double)((float)blockpos.getY() + fluidstate.getHeight(this.level, blockpos))) {
					this.remove();
				}
			}
		}
	}

	protected void preMoveUpdate() {
		if (this.lifetime-- <= 0) {
			this.remove();
		}
	}

	protected void postMoveUpdate() {
	}

	@OnlyIn(Dist.CLIENT)
	static class GreenWaterHangParticle extends CustomDripParticle {
		private final ParticleOptions fallingParticle;
	
		GreenWaterHangParticle(ClientLevel level, double x, double y, double z, Fluid fluid, ParticleOptions options) {
			super(level, x, y, z, fluid);
			this.fallingParticle = options;
			this.gravity *= 0.02F;
			this.lifetime = 40;
		}

		@Override
		protected void preMoveUpdate() {
			if (this.lifetime-- <= 0) {
				this.remove();
				this.level.addParticle(this.fallingParticle, this.x, this.y, this.z, this.xd, this.yd, this.zd);
			}
		}
	
		@Override
		protected void postMoveUpdate() {
			this.xd *= 0.02D;
			this.yd *= 0.02D;
			this.zd *= 0.02D;
		}
	}

	@OnlyIn(Dist.CLIENT)
	static class FallAndLandParticle extends CustomDripParticle.FallingParticle {
		protected final ParticleOptions landParticle;
	
		FallAndLandParticle(ClientLevel level, double x, double y, double z, Fluid fluid, ParticleOptions options) {
			super(level, x, y, z, fluid);
			this.landParticle = options;
			// System.out.println("FallingAndLandParticle called!");
		}
	
		@Override
		protected void postMoveUpdate() {
			if (this.onGround) {
				this.remove();
				this.level.addParticle(this.landParticle, this.x, this.y, this.z, 0.0D, 0.0D, 0.0D);
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	static class FallingParticle extends CustomDripParticle {
		FallingParticle(ClientLevel level, double x, double y, double z, Fluid fluid) {
			this(level, x, y, z, fluid, (int)(64.0D / (Math.random() * 0.8D + 0.2D)));
			// System.out.println("FallingParticle called!");
		}

		FallingParticle(ClientLevel level, double x, double y, double z, Fluid fluid, int lifeTime) {
			super(level, x, y, z, fluid);
			this.lifetime = lifeTime;
		}
	
		@Override
		protected void postMoveUpdate() {
			if (this.onGround) {
				this.remove();
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static class GreenWaterFallProvider implements ParticleProvider<SimpleParticleType> {
		protected final SpriteSet sprite;
	
		public GreenWaterFallProvider(SpriteSet sprite) {
			this.sprite = sprite;
		}
	
		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			CustomDripParticle dripparticle = new CustomDripParticle.FallAndLandParticle(level, x, y, z, GreenWater.green_water_still.get(), GreenWater.splashing_green_water.get());
			// TODO store this color somewhere and access it part 2
			dripparticle.setColor(0F, 1F, 0F);
			dripparticle.pickSprite(this.sprite);
			return dripparticle;
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static class GreenWaterHangProvider implements ParticleProvider<SimpleParticleType> {
		protected final SpriteSet sprite;
	
		public GreenWaterHangProvider(SpriteSet sprite) {
			this.sprite = sprite;
		}
	
		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			CustomDripParticle dripparticle = new CustomDripParticle.GreenWaterHangParticle(level, x, y, z, GreenWater.green_water_still.get(), GreenWater.falling_green_water.get());
			// TODO store this color somewhere and access it part 1
			dripparticle.setColor(0F, 1F, 0F);
			dripparticle.pickSprite(this.sprite);
			return dripparticle;
		}
	}
}