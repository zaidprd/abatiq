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

We deliberately use the platform's bundled sans-serif family
(`FontFamily.SansSerif`). No third-party font files are bundled, so no
attribution is owed. When an open-source rounded font is introduced later
(MIT or OFL) it will be listed here.

---

## Build tools (development only, not shipped in APK)

- **Android Gradle Plugin** (Apache-2.0) — Google LLC.
- **Kotlin Gradle Plugin** (Apache-2.0) — JetBrains s.r.o.
- **Kotlin Compose Compiler plugin** (Apache-2.0) — JetBrains s.r.o.
- **KSP** (Apache-2.0) — Google LLC.

These are build-time only and are not packaged in the user-facing APK.