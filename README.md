# mc-NBT-mod

Client-side Fabric mod for Minecraft 1.21.4 that copies an item's NBT (data components) to the clipboard.

## Usage
- Hover over an item in any inventory/container screen and press **C** (rebindable in Controls, category "Misc").
- Outside screens, **C** copies the item in your main hand.
- The result is an SNBT string with `id`, `count` and `components`.

## Build
Requires Java 21. Run `./gradlew build`; the jar is written to `build/libs/`.

## Notes
- Client-side only, no server installation needed.
- Tested only by compilation against Fabric API 0.119.4+1.21.4 (not run in-game yet).

## License
MIT
