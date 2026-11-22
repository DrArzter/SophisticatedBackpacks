package net.p3pp3rf1y.sophisticatedbackpacks.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackStorage;
import net.p3pp3rf1y.sophisticatedbackpacks.common.BackpackWrapperLookup;
import net.p3pp3rf1y.sophisticatedbackpacks.util.PlayerInventoryProvider;
import net.p3pp3rf1y.sophisticatedcore.inventory.InventoryHandler;
import net.p3pp3rf1y.sophisticatedcore.network.SimplePacketBase;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeHandler;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class RequestBackpackInventoryContentsMessage extends SimplePacketBase {
	private final UUID backpackUuid;

	public RequestBackpackInventoryContentsMessage(UUID backpackUuid) {
		this.backpackUuid = backpackUuid;
	}

	public RequestBackpackInventoryContentsMessage(FriendlyByteBuf buffer) { this(buffer.readUUID()); }

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeUUID(this.backpackUuid);
	}

	@Override
	public boolean handle(Context context) {
		context.enqueueWork(() -> {
			ServerPlayer player = context.getSender();
			if (player == null) {
				return;
			}

			if (!playerHasBackpackWithUuid(player)) {
				return;
			}

			// A request must not create an entry in the saved data.
			CompoundTag backpackContents = BackpackStorage.get().getBackpackContents(backpackUuid).orElseGet(CompoundTag::new);

			CompoundTag inventoryContents = new CompoundTag();
			Tag inventoryNbt = backpackContents.get(InventoryHandler.INVENTORY_TAG);
			if (inventoryNbt != null) {
				inventoryContents.put(InventoryHandler.INVENTORY_TAG, inventoryNbt);
			}
			Tag upgradeNbt = backpackContents.get(UpgradeHandler.UPGRADE_INVENTORY_TAG);
			if (upgradeNbt != null) {
				inventoryContents.put(UpgradeHandler.UPGRADE_INVENTORY_TAG, upgradeNbt);
			}

			SBPPacketHandler.sendToClient(player, new BackpackContentsMessage(backpackUuid, inventoryContents));
		});
		return true;
	}

	// The backpack must be one the player can see: carried, in the open menu or on the cursor.
	private boolean playerHasBackpackWithUuid(ServerPlayer player) {
		AtomicBoolean found = new AtomicBoolean(false);
		PlayerInventoryProvider.get().runOnBackpacks(player, (backpack, inventoryName, identifier, slot) -> {
			found.set(hasUuid(backpack));
			return found.get();
		});
		if (found.get() || hasUuid(player.containerMenu.getCarried())) {
			return true;
		}
		for (Slot menuSlot : player.containerMenu.slots) {
			if (hasUuid(menuSlot.getItem())) {
				return true;
			}
		}
		return false;
	}

	private boolean hasUuid(ItemStack stack) {
		return !stack.isEmpty() && BackpackWrapperLookup.get(stack).flatMap(wrapper -> wrapper.getContentsUuid()).filter(backpackUuid::equals).isPresent();
	}
}
