package mrbooboostank.greenwater.items;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.core.NonNullList;

public class GreenWaterBottle extends PotionItem {

	public GreenWaterBottle(Properties p_42979_) {
		super(p_42979_);
	}
	
	@Override
	public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> list) {
		if (this.allowdedIn(tab)) {
			list.add(PotionUtils.setPotion(new ItemStack(this), Potions.HEALING));
		}
	}
	
	@Override
	public ItemStack finishUsingItem(ItemStack itemStack, Level worldIn, LivingEntity entity) {
		return super.finishUsingItem(itemStack, worldIn, entity);
	}
	
	@Override
	public boolean isFoil(ItemStack p_42999_) {
		return false;
	}
}