package net.p3pp3rf1y.sophisticatedbackpacks.backpack;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.IBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.common.BackpackWrapperLookup;
import net.p3pp3rf1y.sophisticatedbackpacks.util.PlayerInventoryProvider;

import java.util.UUID;

/**
 * This class has logic for deduplicating backpack UUIDs because people duplicating backpack including its UUID seems to happen fairly regularly
 * as well as some mods also add enchants / legit ways to duplicate loot when mobs are killed which can again include backpack
 */

public class UUIDDeduplicator {
	private UUIDDeduplicator() {}

	public static void checkForDuplicateBackpacksAndRemoveTheirUUID(Player player, UUID backpackUuid, ItemStack backpack) {
		BackpackWrapperLookup.get(backpack).ifPresent(backpackWrapper ->
				PlayerInventoryProvider.get().runOnBackpacks(player, (otherBackpack, inventoryHandlerName, identifier, slot) -> {
					if (otherBackpack != backpack) {
						BackpackWrapperLookup.get(otherBackpack)
								.ifPresent(wrapper -> wrapper.getContentsUuid().ifPresent(uuid -> {
									if (uuid.equals(backpackUuid)) {
										dedupeBackpackWrappers(backpackWrapper, wrapper);
									}
								}));
					}
					return false;
				}));
	}

	public static void dedupeBackpackItemEntityInArea(ItemEntity newBackpackItemEntity) {
		BackpackWrapperLookup.get(newBackpackItemEntity.getItem())
				.ifPresent(newBackpackWrapper -> newBackpackWrapper.getContentsUuid()
						.ifPresent(backpackId -> dedupeBackpackItemEntityInArea(newBackpackWrapper, newBackpackItemEntity, backpackId))
				);
	}

	private static void dedupeBackpackItemEntityInArea(IBackpackWrapper newBackpackWrapper, ItemEntity newBackpackItemEntity, UUID backpackId) {
		for (ItemEntity entity : newBackpackItemEntity.level().getEntitiesOfClass(ItemEntity.class, newBackpackItemEntity.getBoundingBox().inflate(10), Entity::isAlive)) {
			// The new entity can be in the result. It must not be compared with itself.
			if (entity != newBackpackItemEntity) {
				checkEntityBackpackIdMatchAndRemoveIfItDoes(newBackpackWrapper, backpackId, entity);
			}
			// Without a UUID the new stack has no contents to protect. Stop before the size check creates a new UUID.
			if (newBackpackWrapper.getContentsUuid().isEmpty()) {
				break;
			}
		}
	}

	private static void checkEntityBackpackIdMatchAndRemoveIfItDoes(IBackpackWrapper newBackpackWrapper, UUID newBackpackId, ItemEntity entity) {
		BackpackWrapperLookup.get(entity.getItem()).ifPresent(wrapper ->
				wrapper.getContentsUuid().ifPresent(backpackId -> {
					if (backpackId.equals(newBackpackId)) {
						dedupeBackpackWrappers(newBackpackWrapper, wrapper);
					}
				}));
	}

	/**
	 * Two backpacks share one contents UUID. The one with more inventory slots keeps it, because it is the one that was upgraded.
	 * On a tie the first one keeps it. The other one loses only its UUID tag. Its stored contents are not deleted.
	 *
	 * @return the wrapper that kept the UUID
	 */
	public static IBackpackWrapper dedupeBackpackWrappers(IBackpackWrapper firstBackpackWrapper, IBackpackWrapper secondBackpackWrapper) {
		if (isFirstBackpackPreferred(firstBackpackWrapper, secondBackpackWrapper)) {
			removeUuid(secondBackpackWrapper);
			return firstBackpackWrapper;
		}

		removeUuid(firstBackpackWrapper);
		return secondBackpackWrapper;
	}

	public static boolean isFirstBackpackPreferred(IBackpackWrapper firstBackpackWrapper, IBackpackWrapper secondBackpackWrapper) {
		return getInventorySize(firstBackpackWrapper) >= getInventorySize(secondBackpackWrapper);
	}

	private static int getInventorySize(IBackpackWrapper backpackWrapper) {
		return backpackWrapper.getInventoryHandler().getSlotCount();
	}

	private static void removeUuid(IBackpackWrapper backpackWrapper) {
		backpackWrapper.removeContentsUUIDTag();
		backpackWrapper.onContentsNbtUpdated();
	}
}
