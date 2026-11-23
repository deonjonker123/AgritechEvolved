# Changelog

All notable changes to this project will be documented here.

## [1.1.0+mc-1.21.1] - 2026-06-23
### Changed
- Compatibility layer is now fully data-driven. Seeds, saplings, soils, fertilizers and their values are defined via recipes and NeoForge data maps, so they can be changed with datapacks and KubeJS
- Composter now uses vanilla compostable values. Anything that can be composted in a vanilla composter can also be used in the ATE composter
### Added
- Tooltips on all valid soil blocks and fertilizers showing their planter growth modifiers
### Performance
- Planters no longer scan the full recipe list every tick. Recipe lookups are cached per seed and refreshed on datapack reload
- Valid soil items are cached per datapack reload instead of being rescanned on every inventory interaction
- Block update packets now only fire when the growth stage changes instead of every second per planter
### Fixed
- Fixed fertilizer duplicating when right-clicking the Advanced Planter
- Fixed more than one upgrade module being insertable into a single Advanced Planter module slot
- Mystical Agriculture farmlands can no longer be downgraded in the planter
### Removed
- Removed the `/agritechevolved reload` command, which no longer applies with the data-driven setup

## [1.1.0.2+mc-1.21.1] - 2026-07-07
### Added
- Biomes O' Plenty, Oh The Biomes We've Gone, Regions Unexplored and Silent's Gems compatibility

## [1.1.0.3+mc-1.21.1] - 2026-07-09
### Removed
- Removed the firefly bush from the planter recipes

## [1.1.0.4+mc-1.21.1] - 2026-08-29
### Added
- New terracotta planters (all 17 variants)
- New vanilla plantables: all corals, short grass and ferns

## [1.1.0.5+mc-1.21.1] - 2026-09-12
### Fixed
- Fixed a planter recipe ordering issue

## [1.1.0.6+mc-1.21.1] - 2026-11-03
### Maint
- Version bump

## [1.1.0.7+mc-1.21.1] - 2026-11-23
### Fixed
- Fixed planters stalling when their output is full

## [1.1.0.8+mc-1.21.1] - 2026-11-23
### Added
- Backported the silo and fertilizer spreader from 26.x

### Fixed
- Energy bars and tooltips in the Composter, Advanced Planter, Biomass Burner, Silo and Fertilizer Spreader GUIs no longer wrap around above 32,767 RF
- Items can no longer be placed by hand into the Basic Planter's output slots
- Right-clicking the Advanced Planter with an upgrade module when both module slots are full now opens the GUI
- Clicking a progress bar to view recipes no longer crashes the game when JEI isn't installed