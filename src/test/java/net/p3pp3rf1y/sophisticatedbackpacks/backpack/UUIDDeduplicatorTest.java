package net.p3pp3rf1y.sophisticatedbackpacks.backpack;

import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.IBackpackWrapper;
import net.p3pp3rf1y.sophisticatedcore.inventory.InventoryHandler;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UUIDDeduplicatorTest {
	private static IBackpackWrapper wrapperWithSlots(int slots) {
		InventoryHandler inventoryHandler = mock(InventoryHandler.class);
		when(inventoryHandler.getSlotCount()).thenReturn(slots);
		IBackpackWrapper wrapper = mock(IBackpackWrapper.class);
		when(wrapper.getInventoryHandler()).thenReturn(inventoryHandler);
		return wrapper;
	}

	@Test
	void keepsTheBackpackWithMoreSlotsWhenItIsFirst() {
		IBackpackWrapper larger = wrapperWithSlots(120);
		IBackpackWrapper smaller = wrapperWithSlots(81);

		assertSame(larger, UUIDDeduplicator.dedupeBackpackWrappers(larger, smaller));

		verify(smaller).removeContentsUUIDTag();
		verify(larger, never()).removeContentsUUIDTag();
	}

	@Test
	void keepsTheBackpackWithMoreSlotsWhenItIsSecond() {
		IBackpackWrapper smaller = wrapperWithSlots(81);
		IBackpackWrapper larger = wrapperWithSlots(120);

		assertSame(larger, UUIDDeduplicator.dedupeBackpackWrappers(smaller, larger));

		verify(smaller).removeContentsUUIDTag();
		verify(larger, never()).removeContentsUUIDTag();
	}

	@Test
	void keepsTheFirstBackpackOnTie() {
		IBackpackWrapper first = wrapperWithSlots(81);
		IBackpackWrapper second = wrapperWithSlots(81);

		assertSame(first, UUIDDeduplicator.dedupeBackpackWrappers(first, second));

		verify(second).removeContentsUUIDTag();
		verify(first, never()).removeContentsUUIDTag();
	}

	@Test
	void refreshesTheHandlersOfTheBackpackThatLostItsUuid() {
		IBackpackWrapper larger = wrapperWithSlots(120);
		IBackpackWrapper smaller = wrapperWithSlots(81);

		UUIDDeduplicator.dedupeBackpackWrappers(larger, smaller);

		verify(smaller).onContentsNbtUpdated();
		verify(larger, never()).onContentsNbtUpdated();
		assertTrue(UUIDDeduplicator.isFirstBackpackPreferred(larger, smaller));
	}
}
