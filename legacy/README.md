# Original game recovery

The source archive was `/home/drew/downloads/WBPA.zip`, dated January 2013. Its SHA-256 is
`dfd4e401bf6f5445ed681e95146fff3a936e1861eb8fa465139cb826a10993ef`.
The ZIP passed `unzip -t`. It contains no original `.java` source or standalone game JAR.

`java/` contains the game's 25 Java 6 class files decompiled into 23 source files by
[CFR 0.152](https://github.com/leibnitz27/cfr/releases/tag/0.152). The two anonymous
inner classes are folded into `Music.java` and `Sound.java`. CFR ran against a temporary
JAR assembled from the `WBPA/bin/` class tree, with the six bundled library
JARs supplied on its extra classpath. No third-party library code was decompiled or
committed. The CFR binary had SHA-256
`f686e8f3ded377d7bc87d216a90e9e9512df4156e75b06c655a16648ae8765b2`.

The assets in `../src/assets/` came from the top-level `WBPA/res/` directory. Its
images and WAVs were byte-identical to the duplicate files under
`WBPA/bin/org/drew739/WBPA/res/`. Source WAVs are kept in `audio-source/`; run
`scripts/convert-audio.sh` with FFmpeg to regenerate the smaller MP3s shipped by
the browser build. The ZIP's Windows launchers, native DLLs, library JARs, duplicate
assets, and serialized zero high score are omitted.

## Porting notes

- The original 800×600 window uses a 400×300 logical OpenGL projection; images for
  menus are 400×300. World geometry uses 15 pixels per meter, then a 0.35 scene scale.
- The Java loop schedules logic every 0.02 seconds while asking JBox2D to step
  1/60 second with 8 velocity and 3 position iterations. The port should preserve
  gameplay constants while using one stable fixed step for browser timing.
- The power meter rises by two per logic tick and stops at 100. No recovered code
  reads its numerical `power` value when applying a kick; only the charging state
  and kick timing affect the leg. This is an original behavior, not a CFR omission.
- CFR output is a reference for behavior and may not reproduce the exact formatting
  or every source-level expression of the lost Java files. No class failed to
  decompile, and the output contains no unresolved-class warning after supplying
  the original library JARs.
