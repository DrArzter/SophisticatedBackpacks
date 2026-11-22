# Changelog

## 1.20.1-3.23.4.5+sp.2 (2026-11-22) - sp fork

The same code as sp.1, published again as an immutable release. No functional change.

## 1.20.1-3.23.4.5+sp.1 (2026-11-22) - sp fork

This is a modified version of Sophisticated Backpacks (Fabric port), released under GPL-3.0.
The original work is by P3pp3rF1y, Ridanisaurus and Salandora. Notice of modification under GPL-3.0 section 5(a).
The upstream state is branch `1.20.1-fabric` at commit cd8ad37.
The mod id `sophisticatedbackpacks` is unchanged, so existing backpacks keep working.

### Changes
- Add native support for the Accessories mod (io.wispforest, 1.0.0-beta.48 for 1.20.1). A backpack worn in an Accessories slot now opens with the B key, and the upgrades that look for worn backpacks work from that slot.
- Add the item tag `accessories:back` for all backpacks.
- Load the Trinkets compatibility only for the real Trinkets mod. The Accessories Trinket Compat Layer provides the id "trinkets", but its slots are Accessories slots.
- Build: take Sophisticated Core from Modrinth Maven, pin Loom to 1.10.5, compile against Accessories without bundling it.
- Version no longer uses the CI run number.
- Remove the publishing to Modrinth, CurseForge and GitHub Packages. CI now builds, and builds a release on a `v*` tag.
- Show the fork name, description and issue tracker in the mod metadata.

## Upstream changelog (before the fork)

### General
- Update to version 3.20.6
- Code cleanup
- Remove custom ItemStackHandler implementation

### Fixes
- Fix a compatibility issue with repurposed structures
- Sorting keybind was not working with keys only with mouse buttons
- Trinket Tags should not be validated against tooltip predicates but validator predicates
- Fix a bug where in certain situations the refill upgrade was able to delete items
- Fix open backpack keybinding, it was not possible to properly bind it to a mouse key
- Fixes incompatibility with IBEEditor
- Implement onDroppedByPlayer
