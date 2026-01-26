# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
./gradlew lwjgl3:run           # Run desktop application
./gradlew lwjgl3:jar           # Build runnable JAR (output: lwjgl3/build/libs/)
./gradlew build                # Build all subprojects
./gradlew clean                # Clean build folders
./gradlew test                 # Run unit tests
./gradlew android:lint         # Android project validation
```

Platform-specific JARs: `./gradlew lwjgl3:jarMac`, `./gradlew lwjgl3:jarLinux`, `./gradlew lwjgl3:jarWin`

## Architecture

This is a **libGDX** cross-platform game project with a multi-module Gradle structure:

```
core/       # Shared game logic (all platforms use this)
lwjgl3/     # Desktop launcher (LWJGL3 backend)
android/    # Android launcher
ios/        # iOS launcher (RoboVM)
assets/     # Shared assets (images, fonts, etc.)
```

**Key pattern**: All game logic goes in `core/`. Platform modules only contain launcher configuration that instantiates `ape.flatbonk.Main`.

**Entry point**: `core/src/main/java/ape/flatbonk/Main.java` extends `ApplicationAdapter` with `create()`, `render()`, and `dispose()` lifecycle methods.

## Dependencies

Managed via `gradle.properties`:
- libGDX: 1.13.1
- Box2D Lights: 1.5
- GDX-AI: 1.8.2
- Java source compatibility: 8

## Code Style

EditorConfig enforces: 4-space indentation for Java, LF line endings, UTF-8 encoding.
