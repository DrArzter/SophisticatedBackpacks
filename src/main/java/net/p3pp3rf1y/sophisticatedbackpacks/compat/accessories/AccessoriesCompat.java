package net.p3pp3rf1y.sophisticatedbackpacks.compat.accessories;

import io.wispforest.accessories.api.AccessoriesAPI;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.AccessoriesContainer;
import io.wispforest.accessories.api.menu.AccessoriesBasedSlot;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.compat.CompatModIds;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;
import net.p3pp3rf1y.sophisticatedbackpacks.util.PlayerInventoryProvider;
import net.p3pp3rf1y.sophisticatedcore.compat.ICompat;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * Native support for the Accessories mod (io.wispforest). The identifier is the Accessories slot name.
 */
public class AccessoriesCompat implements ICompat {
	private static final BackpackAccessory BACKPACK_ACCESSORY = new BackpackAccessory();

	public static <T> T getFromContainer(Player player, String identifier, Function<AccessoriesContainer, T> getter, T defaultValue) {
		AccessoriesCapability capability = AccessoriesCapability.get(player);
		if (capability == null) {
			return defaultValue;
		}
		AccessoriesContainer container = capability.getContainers().get(identifier);
		return container == null ? defaultValue : getter.apply(container);
	}

	/**
	 * Returns the slot name when the slot is a normal (not cosmetic) Accessories slot.
	 */
	public static Optional<String> getSlotName(Slot slot) {
		if (slot instanceof AccessoriesBasedSlot accessoriesSlot && slot.container == accessoriesSlot.accessoriesContainer.getAccessories()) {
			return Optional.of(accessoriesSlot.accessoriesContainer.getSlotName());
		}
		return Optional.empty();
	}

	public static boolean isAccessoriesContainer(Slot slot) {
		return getSlotName(slot).isPresent();
	}

	@Override
	public void init() {
		for (BackpackItem backpack : ModItems.BACKPACKS) {
			AccessoriesAPI.registerAccessory(backpack, BACKPACK_ACCESSORY);
		}

		// Rendered by the shared backpack layer. No Accessories renderer is registered, so nothing is drawn twice.
		PlayerInventoryProvider.get().addPlayerInventoryHandler(CompatModIds.ACCESSORIES, AccessoriesCompat::getSlotNames,
				(player, identifier) -> getFromContainer(player, identifier, AccessoriesContainer::getSize, 0),
				(player, identifier, slot) -> getFromContainer(player, identifier, container -> container.getAccessories().getItem(slot), ItemStack.EMPTY),
				false, true, false, true);
	}

	@Override
	public void setup() {
		// noop
	}

	// Not cached: the handler is called for many players, so a shared cache would be wrong.
	private static Set<String> getSlotNames(Player player, long gameTime) {
		AccessoriesCapability capability = AccessoriesCapability.get(player);
		return capability == null ? Collections.emptySet() : capability.getContainers().keySet();
	}
}
