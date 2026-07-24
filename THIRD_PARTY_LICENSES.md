# Third-Party Licenses — AbatiQ Phase 1

This file lists the open-source components used by AbatiQ Phase 1 along with
their licenses. Per the PRD sourcing policy (Bab 7.2) we only ship libraries
under permissive licenses (MIT / Apache-2.0 / OFL / CC0) and we never bundle
third-party artwork, characters, voices, or raster images.

---

## AndroidX & Google libraries (Apache-2.0)

All AndroidX, Google Material, and AndroidX Compose modules are distributed
under the Apache License 2.0. The canonical text is at
<https://www.apache.org/licenses/LICENSE-2.0>. Copyright for each module is
held by The Android Open Source Project and Google LLC.

| Module                                            | Used for                                  |
|---------------------------------------------------|-------------------------------------------|
| `androidx.core:core-ktx`                          | Kotlin extensions for Android platform    |
| `androidx.lifecycle:lifecycle-runtime-ktx`        | Lifecycle-aware coroutines                |
| `androidx.lifecycle:lifecycle-viewmodel-compose` | ViewModel in Compose                      |
| `androidx.lifecycle:lifecycle-runtime-compose`   | Lifecycle-aware Compose collection       |
| `androidx.activity:activity-compose`              | `ComponentActivity.setContent` bridge     |
| `androidx.compose:compose-bom`                    | Compose BOM pinning                       |
| `androidx.compose.ui:ui`                          | Compose runtime + layout                  |
| `androidx.compose.ui:ui-graphics`                | Canvas / draw scope                       |
| `androidx.compose.ui:ui-tooling` (debug only)     | `@Preview` rendering                      |
| `androidx.compose.ui:ui-tooling-preview`          | `@Preview` annotation                     |
| `androidx.compose.material3:material3`            | M3 components (used sparingly)            |
| `androidx.compose.material:material-icons-extended` | Rounded geometric icons (no faces)      |
| `androidx.navigation:navigation-compose`          | Phase 1 nav graph                         |
| `androidx.room:room-runtime`, `room-ktx`          | SQLite persistence                        |
| `androidx.room:room-compiler` (KSP)               | Room annotation processor                 |
| `androidx.datastore:datastore-preferences`        | Preferences storage                       |

The full text of the Apache-2.0 license is reproduced below for convenience.

```
                                 Apache License
                           Version 2.0, January 2004
                        http://www.apache.org/licenses/

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0
```

---

## Kotlin & JetBrains libraries (Apache-2.0)

- `org.jetbrains.kotlinx:kotlinx-coroutines-android` — coroutine runtime on
  Android (Apache-2.0).
- `org.jetbrains.kotlinx:kotlinx-coroutines-test` — test dispatcher
  (Apache-2.0).
- `io.mockk:mockk` — Kotlin mocking framework (Apache-2.0) — **test only**.
- `app.cash.turbine:turbine` — Flow testing (Apache-2.0) — **test only**.
- `junit:junit` — JUnit 4 (EPL 1.0) — **test only**.

Junit is governed by the Eclipse Public License 1.0; we ship it only as a
test-scope dependency, not in the production APK.

---

## TextToSpeech voice data (system component)

The default audio engine is the device's bundled Android TextToSpeech
synthesiser. Voice data is provided by the OEM or by Google's on-device
speech package; no voice data is bundled inside the APK. When reviewed
human-voice recordings replace TTS they will be added to this file with their
own licence terms.

---

## Audio & visual assets

Phase 1 ships **no bundled audio or raster artwork**. All audio is generated
on-demand by the system TTS engine; all visual decoration is painted at
runtime with Compose `Canvas`. There is therefore no licence to attribute for
artwork at this phase.

---

## Fonts

Most of the UI uses the platform's bundled sans-serif family
(`FontFamily.SansSerif`) so we never depend on an external font for routine
text. For the "Tebak Huruf" playfield we additionally bundle **Sniglet** to
give the oversized letters inside the balloons a friendly, rounded display
look.

| Font    | Source                                       | Licence                  | Bundled at                                  | Used for                                  |
|---------|----------------------------------------------|--------------------------|---------------------------------------------|-------------------------------------------|
| Sniglet | https://fonts.google.com/specimen/Sniglet — original glyph design by Haley Fiegener, hosted via the [google/fonts](https://github.com/google/fonts/tree/main/ofl/sniglet) repository under `ofl/sniglet/`. | [SIL Open Font License 1.1](https://scripts.sil.org/OFL) — free for commercial use, no required attribution. | `app/src/main/res/font/sniglet_regular.ttf` (the Regular 400 weight; Sniglet ships with a single weight so we do not bundle ExtraBold). | Display-sized letters `A`–`J` inside the balloons on the Game screen. NOT used for buttons, timer/score chips, or other body UI. |

Per the OFL we are not required to credit the designer, but we do so here
for transparency.

---

## Bundled artwork (Phase 1 onwards)

| Asset                                              | Source / licence                                                | Bundled at                                                              | Used for                                                                                                              |
|----------------------------------------------------|-----------------------------------------------------------------|-------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------|
| `ic_background_tebak_huruf_konversi.webp`          | Original artwork created by the project owner in Canva, depicting a calm TK (taman kanak-kanak) classroom scene (mint wall, soft-blue lower wall, whiteboard with book shelf, round carpet, child-sized furniture, window with greenery, rainbow mural). Reviewed against PRD Bab 3: **no faces, no licensed characters, no musical instruments, no alcohol, no inappropriate apparel**. Delivered as WebP (~74 KB) instead of the original 3 MB PNG to keep the APK lean. | `app/src/main/res/drawable/ic_background_tebak_huruf_konversi.webp`       | Background of the `GameScreen` (landscape "Tebak Huruf" playfield), overlaid with a soft cream radial scrim so the balloons stay focal. |
| `background_tebak_huruf.png` & `style_yang_di_inginkan.png` (originals in repo root) | Internal Canva compositions kept outside `res/` purely as out-of-band visual references. **Not bundled inside the APK**; balloons are re-drawn with Compose `Canvas` referencing the style image. | (not bundled)                                                           | Documentation / hand-off reference for the design choices in `ui/components/BalloonView.kt`.                          |

---

## Build tools (development only, not shipped in APK)

- **Android Gradle Plugin** (Apache-2.0) — Google LLC.
- **Kotlin Gradle Plugin** (Apache-2.0) — JetBrains s.r.o.
- **Kotlin Compose Compiler plugin** (Apache-2.0) — JetBrains s.r.o.
- **KSP** (Apache-2.0) — Google LLC.

These are build-time only and are not packaged in the user-facing APK.