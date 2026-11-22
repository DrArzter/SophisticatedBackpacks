# Changelog

## Unreleased - sp fork

Modified version of Sophisticated Backpacks (Fabric port), GPL-3.0. Notice of modification under GPL-3.0 section 5(a).

### Fixes
- Fix a backpack inside an Inception backpack losing its contents on death, relog or restart when it was never opened outside first. The parent now saves the new contents id of the inner backpack (upstream #167, #186). The idea is from Forge commit 78e3f547, written again for this code base.
- Fix the backpack duplicate check removing the contents id from the wrong copy, so that a full backpack looked empty. Of two backpacks with the same contents id, the one with more inventory slots now keeps it. A stack is no longer compared with itself, in the tick check and in the dropped item check (upstream #187, #119). Backport of Forge commit 3667ac84. The removed copy opens empty. The shared contents stay with the kept copy and are not deleted.
- Check on the server that a block is loaded and within 8 blocks of the player before a backpack upgrade acts on it. Before, the server used the block position from the client without a check. This is a new fix, not from upstream.
- Fix the server sending or creating backpack contents for any contents id that a client asked for. The server now answers only for a backpack that the player carries, holds on the cursor or sees in the open menu. It no longer creates an empty entry for an unknown id. Adapted from Forge commit 588c2c67, written again for this code base without the item reference class.
- Check that the player is alive and within 8 blocks before the server opens the backpack of another player. Adapted from Forge commit 588c2c67. The same alive check is added to the menu check, adapted from Forge commit 2dc0f0e2.
- Fix the refill upgrade deleting the item in the off hand when a mod such as Better Combat changes how the off hand is read. The upgrade now reads the off hand slot of the inventory. Backport of Forge commit c6bd200a.
- Fix the advanced jukebox upgrade losing the disc when it is crafted from the jukebox upgrade. The recipe now uses the next tier recipe type, like the other upgrades. Backport of Forge commit 2f0cf03a. The Core part of that commit is not ported.
- Fix crashes in the upgrade cache. The inventory handler and the upgrade handler are now set through local variables, and the Inception upgrade cache uses concurrent maps. Backport of Forge commits 8928d8b8 and aaa2bf75.
- Fix a leak of event listeners. Each placed backpack block added two global listeners that were never removed. They are now registered once. The chunk unload listener also marks only the block entities of the chunk that unloads, not all backpack block entities. This is a new fix, not from upstream.
- Fix a placed backpack next to a controller that stopped reporting stack changes to the controller after an upgrade change, which could lose items. The block now registers the stack listeners again when the upgrade caches refresh. Adapted from Forge commit 5f85eb9b, without the Core hook that Forge uses.

## 1.20.1-3.23.4.5+sp.3 (2026-11-22) - sp fork

The same code as sp.1, published as an immutable release. No functional change.
The sp.2 tag never built (the Gradle wrapper lost its execute bit), so it has no release.

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
