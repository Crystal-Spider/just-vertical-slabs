# Just Vertical Slabs
### Finally build with Vertical Slabs in Vanilla or with any mod you've installed!

## Features
- **Works out of the box with any mod!**
- Vertical Slab variants for any block with slab variants, Vanilla or not!
- Vertical Slabs combine like stairs do!
- Vertical Slabs craftings! See next section for more details.
- Creative Tab with all available Vertical Slabs!

***Important note:***  
*All and only blocks that already have slab variants will also get vertical slab variants, this to keep a Vanilla-like game style that nicely adapts to any eventual mod.  
If you want more vertical slabs it's sufficient to add more slabs. Then, automatically, vertical slabs will be added to the game.*

## Craftings
- 3 of the same block in a column to craft a vertical slab.
- 2 of the same vertical slab in a row to craft a full block.
- 2 of the same slab in a column to craft a full block.
- Vertical slabs can be crafted into slabs and vice versa just by putting them alone in the crafting.
- Waxing works as usual, both in-world and in-crafting.
- Stonecutter recipes are available.  
  It must be noted that, due to some implementation restrictions, a block can be stonecut only in its main vertical slab. To obtain the other vertical slab variants of that block the crafting recipes above must be used.

## Downloads
This is the FORGE version, a port to FABRIC is planned but won't happen any time soon.  
Downloads are available [here](https://www.curseforge.com/minecraft/mc-mods/just-vertical-slabs/files).

## Issues
To open a new issue or search an existing one visit the [issues tab](https://github.com/Nyphet/just-vertical-slabs/issues).  
Before opening a new issue please verify that no other issue about the same topic already exists, either open or closed.  
If you find a mod that breaks with this mod or that does not get vertical slabs as it should, please open an issue.
If you find a weird or incorrect behavior of any vertical slab, please open an issue.

## Credits
A very big thank you goes to the Forge team that allowed anyone to mod Minecraft for free and their support in the Forge Forums.  
In particular I want to thank diesieben07 that followed me throughout most of the development, without him none of this would have been possible.

## Technical details
For this mod to automatically add vertical slabs depending on the slabs present in the game two assumptions must be made:
- All slab items have the "minecraft:slabs" tag.
- All slab items have at least 1 crafting recipe using at least 1 block and, if more blocks are used, they are all the same.

These assumptions work 99% of cases, however in the remote case that for some reason a mod adds slabs that don't have the correct tag or can't be crafted from their block variant, the corresponding vertical slabs can't and won't be added.

## Mod Improvement
**Help wanted!**  
There are a few points this mod is lacking in and I'd like to improve it, however I don't have nearly enough time nor energy to do so.  
For this reason I'm looking for someone that would be available to help improve this project (for free of course).  
If you think you are interested in becoming a co-author, help improve this mod and expand its ecosystem, please contact me with a message on [CurseForge](https://www.curseforge.com/private-messages/send?recipient=crystal_spider_).

## License and right of use
Feel free to use this mod for any modpack or video, just be sure to give credit and possibly link [here](https://github.com/Nyphet/just-vertical-slabs#readme).  
This project is published under the [GNU General Public License v3.0](https://github.com/Nyphet/just-vertical-slabs/blob/master/LICENSE).
