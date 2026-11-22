package net.p3pp3rf1y.sophisticatedbackpacks.compat;

import net.fabricmc.loader.api.FabricLoader;

public class CompatModIds {
	private CompatModIds() {}

	public static final String BOTANIA = "botania";
	public static final String TRINKETS = "trinkets";
	public static final String ACCESSORIES = "accessories";
	public static final String QUARK = "quark";
	public static final String CHIPPED = "chipped";
	public static final String MKB = "mkb";

	/**
	 * True only for the real Trinkets mod. The Accessories Trinket Compat Layer also provides
	 * the id "trinkets", so the plain isModLoaded check cannot tell them apart.
	 */
	public static boolean isRealTrinketsLoaded() {
		return FabricLoader.getInstance().getModContainer(TRINKETS)
				.map(container -> TRINKETS.equals(container.getMetadata().getId()))
				.orElse(false);
	}
}
