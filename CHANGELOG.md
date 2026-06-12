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
- Compatibility layer is now fully data-driven. Seeds, saplings, soils, fertilizers and their values are defined via recipes and NeoForge datamaps — fully packdev-accessible via datapacks.
- Composter now uses vanilla compostable values. No more hardcoded entries. Anything that can be composted in a vanilla composter can also be used in the ATE composter
### Added
- Tooltip injection on all valid soil blocks and fertilizers showing their planter growth modifiers
### Bug Fixes
- Fixed fertilizer item duplicating when right-clicking the Advanced Planter
- Fixed a bug that allowed more than one upgrade module to be inserted into the Advanced Planter module slots