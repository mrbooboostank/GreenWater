package mrbooboostank.greenwater.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CustomSuspendedParticle extends TextureSheetParticle {
	CustomSuspendedParticle(ClientLevel level, SpriteSet sprite, double x, double y, double z) {
		super(level, x, y - 0.125D, z);
		this.setSize(0.01F, 0.01F);
		this.pickSprite(sprite);
		this.quadSize *= this.random.nextFloat() * 0.6F + 0.2F;
		this.lifetime = (int)(16.0D / (Math.random() * 0.8D + 0.2D));
		this.hasPhysics = false;
		this.friction = 1.0F;
		this.gravity = 0.0F;
	}

	CustomSuspendedParticle(ClientLevel level, SpriteSet sprite, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
		super(level, x, y - 0.125D, z, xSpeed, ySpeed, zSpeed);
		this.setSize(0.01F, 0.01F);
		this.pickSprite(sprite);
		this.quadSize *= this.random.nextFloat() * 0.6F + 0.6F;
		this.lifetime = (int)(16.0D / (Math.random() * 0.8D + 0.2D));
		this.hasPhysics = false;
		this.friction = 1.0F;
		this.gravity = 0.0F;
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}

	@OnlyIn(Dist.CLIENT)
	public static class GreenWaterSuspendedProvider implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet sprite;
		
		public GreenWaterSuspendedProvider(SpriteSet sprite) {
			this.sprite = sprite;
		}
		
		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			CustomSuspendedParticle suspendedparticle = new CustomSuspendedParticle(level, this.sprite, x, y, z);
			suspendedparticle.setColor(0F, 1F, 0F);
			//  suspendedparticle.setColor(0.4F, 0.4F, 0.7F);
			return suspendedparticle;
		}
	}
}