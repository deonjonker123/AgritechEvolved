# Changelog

All notable changes to this project will be documented here.

## [2.0.15.3+neoforge-mc26.1.2] - 2026-05-24
### Added
- Japanese translation (ja_jp) - thanks hamu6251ren0725-hue!

## [2.0.15.4+neoforge-mc26.1.2] - 2026-05-26
### Fixed
- Fallback recipe for planters

## [2.0.15.5+neoforge-mc26.1.2] - 2026-06-04
### Maint
- Version Bump

## [2.1.0+neoforge-mc26.1.2] - 2026-06-12
### Changed
- Compatibility layer is now fully data-driven. Seeds, saplings, soils, fertilizers and their values are defined via recipes and NeoForge datamaps — fully packdev-accessible via datapacks and KubeJS.
- Composter now uses vanilla compostable values. No more hardcoded entries. Anything that can be composted in a vanilla composter can also be used in the ATE composter
### Added
- Tooltip injection on all valid soil blocks and fertilizers showing their planter growth modifiers
### Bug Fixes
- Fixed fertilizer item duplicating when right-clicking the Advanced Planter
- Fixed a bug that allowed more than one upgrade module to be inserted into the Advanced Planter module slots

## [2.1.1+neoforge-mc26.1.2] - 2026-06-14
### Performance
- Planters no longer scan the full recipe list every tick. Recipe lookups are now cached per seed item and invalidated on datapack reload, reducing server tick time significantly at scale
- Valid soil items are now cached per datapack revision, eliminating repeated recipe scans on inventory interaction
- Block update packets now only fire on growth stage change instead of every second per planter
- Output item transfer now runs only on harvest instead of every tick

## [2.1.2+neoforge-mc26.1.2] - 2026-06-16
### Added
- New compat API for planters: `PlanterProcessingTimeEvent`, `PlanterPreHarvestEvent`, `PlanterPostHarvestEvent` (in `com.misterd.agritechevolved.integration`). Allows other mods to read/modify seed stats, override growth time, and adjust harvest output via NeoForge events. Fired by the basic and advanced planter
- `PlanterPreHarvestEvent` writes the returned seed back into the planter's seed slot, allowing persistent stat changes on the planted seed itself without needing to cycle harvested seeds back through the output

### Removed
- Removed the `/agritechevolved reload` command as it no longer makes sense nor works with the new data-driven approach

## [2.1.2.1+neoforge-mc26.1.2] - 2026-06-16
### Fixed
- Fixed data components (NBT) being stripped from items when extracted through menus or scaled through yield modifiers