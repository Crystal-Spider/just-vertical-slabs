# Change Log

All notable changes to the "just-vertical-slabs" Minecraft mod will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Forge Semantic Versioning](https://mcforge.readthedocs.io/en/1.18.x/gettingstarted/versioning/#versioning).

## [Unreleased]
- Nothing new.

## [1.18.2-3.2.0.1] - 2022/07/18
- Fixed a rare bug that could cause game crash.
- Updated in game mod image.

## [1.18.2-3.2.0.0] - 2022/07/11
- Fixed [1.18.2-3.2.0.0-beta1] double vertical slabs bugs.
- Changed project structure to prepare a Fabric port.
- Improved translucent vertical slabs rendering.
- Added images to documentation.
- Fixed tinted glass light down propagation.
- Fixed a combining bug that would prevent vertical slabs facing north from creating certain angles when side by side with double vertical slabs.
- Fixed tint of grass vertical slabs particles.
- Improved double vertical slabs.

## [1.18.2-3.2.0.0-beta1] - 2022/06/04
- Added double vertical slabs.
- BUG: Double Vertical Slabs that only have a referredSlabState (so no referredBlockState) don't render correctly.
- BUG: Double Vertical Slabs that should emit light don't emit it.
- BUG: (from previous versions) honey and slime vertical slabs don't render correctly.

## [1.18.2-3.1.0.1] - 2022/05/23
- Fixed client-server interactions and small bug fix.

## [1.18.2-3.1.0.0] - 2022/05/22
- Fixed crash when this mod is put only on server.
- Fixed client-server compatibility.
- Improved light for translucent vertical slabs.

## [1.18.2-3.0.0.0] - 2022/05/16
- Fixed dynamic colors, like for grass and foliage.
- Fixed overlayed textures.
- Improved drops.
- Added support for blocks that are not full height, like Dirt Path.
- Fixed not full height vertical slabs occluding other blocks when they shouldn't.
- Improved Inner Vertical Slab ("big corner") model.
- Added support for transparent (translucent) vertical slabs.
- Improved dynamic models.
- Now, when possible, most properties refer to the block rather than the slab to improve coherence.
- Fixed broken textures when using external shaders.

## [1.18.2-2.0.0.0] - 2022/05/10
- Refractored code to base Vertical Slabs on Slabs rather than Blocks.
- Improved dynamic model code.
- Updated readme.
- Removed requirement for crafting recipe of a Slab to be considered a valid Slab. Now it's sufficient to have the *minecraft:slabs* tag. 

## [1.18.2-1.0.0.0] - 2022/05/09
- Added vertical slab variants for any block having a slab variant, vanilla or not.
- Added "stair combining logic" to vertical slabs.
- Added crafting recipes involving vertical slabs.
- Added stonecutter recipes involving vertical slabs.
- Added in-world waxing recipes for vertical slabs that can be waxed.
- Added creative tab with all vertical slabs.

[Unreleased]: https://github.com/Nyphet/just-vertical-slabs
[1.18.2-3.2.0.0]: https://github.com/Nyphet/just-vertical-slabs/releases/tag/v1.18.2-3.2.0.0
[1.18.2-3.2.0.0-beta1]: https://github.com/Nyphet/just-vertical-slabs/releases/tag/v1.18.2-3.2.0.0-beta1
[1.18.2-3.1.0.1]: https://github.com/Nyphet/just-vertical-slabs/releases/tag/v1.18.2-3.1.0.1
[1.18.2-3.1.0.0]: https://github.com/Nyphet/just-vertical-slabs/releases/tag/v1.18.2-3.1.0.0
[1.18.2-3.0.0.0]: https://github.com/Nyphet/just-vertical-slabs/releases/tag/v1.18.2-3.0.0.0
[1.18.2-2.0.0.0]: https://github.com/Nyphet/just-vertical-slabs/releases/tag/v1.18.2-2.0.0.0
[1.18.2-1.0.0.0]: https://github.com/Nyphet/just-vertical-slabs/releases/tag/v1.18.2-1.0.0.0
