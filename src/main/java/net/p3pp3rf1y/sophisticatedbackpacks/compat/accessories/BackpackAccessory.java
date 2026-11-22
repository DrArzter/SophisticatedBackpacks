package net.p3pp3rf1y.sophisticatedbackpacks.compat.accessories;

import io.wispforest.accessories.api.Accessory;
import io.wispforest.accessories.api.slot.SlotReference;

import net.minecraft.world.item.ItemStack;

public class BackpackAccessory implements Accessory {
	@Override
	public void tick(ItemStack stack, SlotReference reference) {
		// Same as the Trinkets path: a slot index > -1 marks the backpack as worn.
		stack.inventoryTick(reference.entity().level(), reference.entity(), reference.slot(), true);
	}
}
