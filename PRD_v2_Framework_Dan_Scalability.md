# PRD v2 — Framework & Scalability

> **Status:** ✅ **FINAL v2.0 — APPROVED sebagai Canonical Reference**
> **Tanggal approval:** 24 Juli 2026
> **Owner:** zaidprd
> **Dibangun di atas:** `PRD_Aplikasi_Belajar_Anak_Muslim.md` v1.0 (23 Juli 2026)
> **Sifat:** BAB TAMBAHAN, bukan replacement PRD v1.

## Daftar Isi

1. [Ringkasan & Verdict](#1-ringkasan--verdict)
2. [Target Arsitektur](#2-target-arsitektur)
3. [Bab 12 — Design System Tokens](#bab-12--design-system-tokens)
4. [Bab 12A — Design System Specification](#bab-12a--design-system-specification)
5. [Bab 13 — Asset Pipeline](#bab-13--asset-pipeline)
6. [Bab 13A — Asset Production Guide](#bab-13a--asset-production-guide)
7. [Bab 14 — Game Engine Framework](#bab-14--game-engine-framework)
8. [Bab 15 — Component Library](#bab-15--component-library)
9. [Bab 16 — Asset Library Structure](#bab-16--asset-library-structure)
10. [Bab 16A — Asset Governance](#bab-16a--asset-governance)
11. [Bab 17 — Module Playbook](#bab-17--module-playbook)
12. [Bab 18–23 — Ditunda (Trigger-Based)](#bab-1823--ditunda-trigger-based)
13. [Migration Plan & Definition of Done](#migration-plan--definition-of-done)
14. [Checklist Pra-Implementation](#checklist-pra-implementation)

---

## 1. Ringkasan & Verdict

### Verdict Arsitektur Saat Ini
Arsitektur Fase 1 BUKAN scalable ke 14 modul mini-game (Tebak Huruf, Tebak Angka, Tebak Warna, Hijaiyah, Buah, Sayur, Hewan, Transportasi, Puzzle, Memory, Matching, Ramadan, Doa, Mengaji). Codebase mengikat game "Tebak Huruf" di banyak tempat.

### Target
Refactor ke struktur `core/data/modules/ui/docs/` dengan engine generik, design tokens, component library, dan module plug-in pattern.

### Komitmen
- Tidak ada perubahan besar arsitektur setelah development dimulai kecuali owner meminta revisi eksplisit.
- PRD v2 ini adalah canonical reference — semua keputusan yang tidak ada di sini butuh klarifikasi eksplisit sebelum ditulis ke kode.

---

## 2. Target Arsitektur

```
abatiQ/
├── PRD_Aplikasi_Belajar_Anak_Muslim.md       # PRD v1, tidak diubah
├── PRD_v2_Framework_Dan_Scalability.md       # Dokumen ini
├── CONTENT_REVIEW_CHECKLIST.md
├── THIRD_PARTY_LICENSES.md
├── README.md
├── docs/                                      # Designer/dev wiki
│   ├── design-system.md
│   ├── asset-pipeline.md
│   ├── asset-production-guide.md
│   ├── asset-governance.md
│   ├── component-library.md
│   ├── game-engine.md
│   └── module-playbook.md
├── app/src/main/java/com/abatiq/app/
│   ├── AbatiQApplication.kt
│   ├── MainActivity.kt
│   ├── core/
│   │   ├── designsystem/
│   │   │   ├── tokens/                        # Color, Type, Shape, Spacing, Radius, Elevation, Motion, IconSize, ComponentSize, Shadow
│   │   │   ├── componentspec/                # ComponentSpec data classes
│   │   │   └── theme/
│   │   ├── component/                         # Header, AnswerCard, Buttons, RewardDialog, dll.
│   │   ├── engine/                            # GameSession, RoundFactory, FeedbackPolicy, RewardPolicy
│   │   ├── audio/
│   │   ├── module/                            # ModuleManifest, ModuleRegistry, ArenaLayout
│   │   └── nav/
│   ├── data/                                  # Room, DataStore, repositories, usecases
│   ├── modules/
│   │   └── tebak_huruf/                       # Modul pertama (wrapped)
│   ├── ui/                                    # Global: home, settings, result, parental
│   └── util/
└── res/
    ├── drawable/                              # VectorDrawable + minimal WebP
    ├── raw/                                   # audio
    ├── font/                                  # OFL fonts
    └── assets/                                # content/*.json, lottie/*.json
```

---

## Bab 12 — Design System Tokens

### Color, Typography, Shape (existing)
Pindah ke `core/designsystem/tokens/`. Struktur tetap, hanya lokasi folder.

### Spacing
```kotlin
object AbatiQSpacing {
    val xxs = 4.dp; val xs = 8.dp; val sm = 12.dp; val md = 16.dp
    val lg = 24.dp; val xl = 32.dp; val xxl = 48.dp
    val gapBetweenCards = md
    val gapInsideCard = sm
    val screenHorizontal = lg
    val screenVertical = lg
    val itemSpacingTight = xs
}
```

### Radius
```kotlin
object AbatiQRadius {
    val xs = 4.dp; val sm = 8.dp; val md = 14.dp; val lg = 20.dp; val xl = 28.dp
}
```

### Elevation
```kotlin
object AbatiQElevation {
    val none = 0.dp; val low = 2.dp; val medium = 4.dp; val high = 8.dp
}
```

### Shadow
```kotlin
data class AbatiQShadow(val radius: Dp, val offsetY: Dp, val alpha: Float)
object AbatiQShadows {
    val card    = AbatiQShadow(radius = 12.dp, offsetY = 6.dp,  alpha = 0.18f)
    val dialog  = AbatiQShadow(radius = 24.dp, offsetY = 12.dp, alpha = 0.28f)
    val balloon = AbatiQShadow(radius = 12.dp, offsetY = 6.dp,  alpha = 0.25f)
    val token   = AbatiQShadow(radius = 6.dp,  offsetY = 3.dp,  alpha = 0.16f)
}
```

### Motion
```kotlin
object AbatiQMotion {
    val fast = 150; val medium = 260; val slow = 500
    val dramatic = 900; val idle = 1200
    val staggerShort = 80L; val staggerLong = 200L
}
```

### IconSize
```kotlin
object AbatiQIconSize {
    val small  = 24.dp; val medium = 32.dp; val large  = 48.dp; val xl = 64.dp
    val touchTarget = 56.dp
}
```

### ComponentSize
```kotlin
object AbatiQComponentSize {
    val gameHeaderHeight       = 80.dp
    val homeHeaderHeight       = 64.dp
    val answerCardMinHeight    = 180.dp
    val answerCardAspectRatio  = 0.78f
    val balloonBodyAspectRatio = 0.85f
    val primaryButtonHeight    = 64.dp
    val secondaryButtonHeight  = 56.dp
    val iconButtonSizeSmall    = 40.dp
    val iconButtonSizeMedium   = 56.dp
    val iconButtonSizeLarge    = 72.dp
    val questionBoxMinHeight   = 80.dp
    val questionBoxMaxWidth    = 720.dp
    val topBarHeight           = 96.dp
    val rewardDialogMaxWidth   = 480.dp
    val rewardDialogPadding    = 32.dp
    val speakerButtonSize      = 56.dp
    val scoreChipMinWidth      = 96.dp
    val timerChipMinWidth      = 140.dp
    val badgeSmall             = 32.dp
    val badgeMedium            = 48.dp
    val badgeLarge             = 64.dp
    val moduleCardHeight       = 240.dp
    val progressBarHeight      = 8.dp
    val loaderSize             = 48.dp
    val toastMinHeight         = 48.dp
    val toastMaxWidth          = 560.dp
    val emptyStateMinHeight    = 240.dp
}
```

### Aturan Enforcement
> ⚠️ **TIDAK BOLEH** ada angka hardcoded (px, dp, sp, ms, f) di luar `core/designsystem/tokens/*.kt` atau di `assets/`.
>
> **Mekanisme**:
> 1. Code review checklist di PR template — wajib diisi.
> 2. Sprint scan rutin: `grep -nE "[0-9]+\.(dp|sp)" app/src/main/java/com/abatiq/app/core/component/` menghasilkan list pelanggaran → fix di sprint berikut.

---

## Bab 12A — Design System Specification

Setiap komponen UI Fase 1 didefinisikan secara eksplisit dengan format: Fungsi / Asset / Token / Variasi / State / Animation / Reusable.

### Komponen Wajib

| Komponen | Fungsi singkat | Asset | Token kunci | Reusable |
|---|---|---|---|---|
| **Header** (`GameHeader`) | Top bar back + score + timer + speaker | ops `header_game_default.xml` | `gameHeaderHeight`, `pill`, `Paper/CreamDim` | ✅ |
| **ScorePanel** | Chip skor ★ + angka | `ic_star.xml` | `Forest/ForestDark/Honey`, `pill`, `scoreChipMinWidth` | ✅ |
| **TimerPanel** | Chip countdown + progress bar mini | built | `Paper/CreamDim/HoneyDeep`, `timerChipMinWidth` | ✅ |
| **QuestionBox** | Inline instruksi visual (Fase 2+) | ops `icon_question.xml` | `Paper/CreamDim`, `lg`, `questionBoxMinHeight/MaxWidth` | ✅ |
| **Balloon** (`AnswerCard`) | Kartu jawaban mengambang | `balloon_{slot}_{state}.xml` + Canvas overlay animasi | `PastelBlue/Pink/Yellow`, `Sniglet font`, `balloonBodyAspectRatio`, `answerCardMinHeight`, `xl`, `balloon shadow`, `motion.idle` | ✅ |
| **AnswerCard** (generic) | Polymorphic payload `CardContent` | VectorDrawable silhouette + Compose overlay | sama dengan Balloon | ✅ |
| **ButtonPrimary** | Tombol aksi utama | built / ops `button_primary_default.xml` | `Forest/ForestDark/OnDark`, `lg`, `primaryButtonHeight`, `token shadow` | ✅ |
| **ButtonSecondary** | Tombol sekunder | ops | `Paper/CreamDim`, `lg`, `secondaryButtonHeight` | ✅ |
| **ButtonOutlined** | Tombol outline (parental, lewati) | ops | `ForestDark border 3dp`, `lg` | ✅ |
| **ButtonIcon** (`IconButton`) | Tombol bulat ikon | Material Icons / VectorDrawable | `pill`, `IconSize.{small/medium/large}` | ✅ |
| **ProgressBar** | Linear progress bar | ops | `CreamDim/HoneyDeep`, `pill`, `progressBarHeight` | ✅ |
| **RewardDialog** | Dialog sesi selesai / reward | `reward_celebrate_v1.json` (Lottie) atau VectorDrawable | `Paper/CreamDim`, `xl`, `rewardDialogMaxWidth/Padding`, `dialog shadow`, `motion.dramatic` | ✅ |
| **FeedbackBubble** | Toast popup reaksi benar/salah | `feedback_bubble_default.xml` | `Forest/TerracottaSoft/Cream`, `lg`, `dialog shadow`, `motion.fast/medium` | ✅ |
| **Badge** (Star/Ribbon/Check) | Badge kecil untuk achievement | `badge_star_v1.xml`, `badge_ribbon_v1.xml`, `badge_check_v1.xml` | `HoneyDeep/Honey/Terracotta/Forest`, `badgeSmall/Medium/Large` | ✅ |
| **Icon** (helper) | Komposisi ikon reusable | Material Icons (Apache-2.0) + VectorDrawable kustom | `IconSize.{small..xl}`, `tint token` | ✅ |
| **Background** | Latar belakang layar (WebP full-bleed) | `bg_{module}_{variant}.webp` + ops `bg_pattern_default.xml` | `Cream fallback` + ops `Cream.copy(alpha=0.55f)` scrim | ✅ |
| **Decoration** | Ornamen geometris di tepi | `decoration_*.xml` (VectorDrawable) | `MintSurface/Mint/Honey/Forest/Terracotta` | ✅ |
| **ModuleCard** | Kartu menu modul di Home | `module_card_default.xml` atau plain gradient + `ic_module_{id}.webp` | `Forest gradient`, `xl`, `moduleCardHeight`, `lg padding`, `Honey accent` | ✅ |
| **ModuleGrid** | Container list ModuleCard | built | `lg gap`, `screenHorizontal` | ✅ |
| **ModuleScaffold** | Layout 3-zone (header + arena + footer) | built | `gameHeaderHeight`, `screenHorizontal`, `lg` | ✅ |
| **Loader** | Spinner loading state | built / ops `loader_spinner.xml` (VectorDrawable) | `loaderSize`, `HoneyDeep/Honey`, `motion.medium` (rotation) | ✅ |
| **Toast / Snackbar** | Pesan transien (informasi/feedback singkat) | `toast_default.xml` atau built Box | `Forest/TerracottaSoft/Paper`, `lg`, `toastMinHeight`, `toastMaxWidth`, `token shadow`, `motion.medium` | ✅ |
| **EmptyState** | Empty list / "Belum ada data" | ops `ic_empty_default.xml` | `InkSoft/CreamDim`, `emptyStateMinHeight`, `lg` | ✅ |
| **ParentalChallenge** | Challenge matematika parental gate | built | `Paper/CreamDim`, `lg`, `primaryButtonHeight`, `displaySmall` | ⚪ Parsial |

### State yang Harus Di-cover (semua komponen interaktif)
`Default`, `Pressed`, `Disabled`, `Correct`, `Wrong`. Komponen tertentu (Balloon, FeedbackBubble) punya `Entering/Holding/Exiting`.

### ComponentSpec — Encoded as Kotlin
Setiap komponen punya `data class {Component}Spec` di `core/designsystem/componentspec/` yang mirror spec ini → type-safe untuk diskusi designer/developer.

---

## Bab 13 — Asset Pipeline

```
Designer (Figma / Inkscape / Illustrator / Canva)
  ├──► SVG ──────► Android Studio → VectorDrawable ─► res/drawable/{category}/
  ├──► AI/raster ──► cwebp -q 80 ─────────────────► res/drawable/backgrounds/*.webp
  ├──► WAV/OGG ───► compliance review ─────────────► res/raw/{moduleId}/
  ├──► TTF/OTF ───► res/font/
  └──► Lottie ─────► assets/lottie/{name}.json
```

### Aturan
1. Semua aset baru **wajib** lewat compliance check + sign off.
2. Open-source: hanya MIT / Apache-2.0 / OFL / CC0.
3. Vektor → VectorDrawable Android.
4. Raster → **WebP** (bukan PNG).
5. Audio → WAV production; OGG untuk sfx (Fase 2+).
6. Lottie → JSON di `assets/lottie/`.
7. Font → TTF/OTF di `res/font/`, OFL.
8. Setiap aset baru → commit `THIRD_PARTY_LICENSES.md` + checklist sign-off.

### Naming Convention
- VectorDrawable: `{category}_{module}_{purpose}_{variant}_{state}.xml`
- WebP: `{category}_{module}_{purpose}_{variant}.webp`
- Audio: `{prompt}_{variant}.wav` (lowercase_underscore)
- Lottie: `{scene}_{variant}.json`
- Font: `{family}_{weight}.ttf`
- ContentPack: `{module_id}.json`
- ModuleManifest: `{module_id}.yml`

---

## Bab 13A — Asset Production Guide

### Tools Default

| Tools | Peran | Default? | Alasan |
|---|---|---|---|
| **Inkscape** | Pembuatan + edit SVG | ✅ **DEFAULT** | Open-source, format SVG native, ringan. |
| **Adobe Illustrator** | Pembuatan SVG | ⚪ Opsional | Industri standar, output wajib `.svg` dengan metadata dibersihkan. |
| **Figma** | Wireframe + kolaborasi | ⚪ Opsional | Bagus untuk auto-export ke VectorDrawable. |
| **Canva** | Ilustrasi kompleks | ⚪ Opsional | Output → PNG → kompres WebP. |
| **After Effects + Bodymovin** | Lottie JSON | ⚪ Opsional | Animasi cinematic. |
| **Audacity** | Audio WAV | ✅ Wajib | Trim, normalize, export 44.1kHz 16-bit mono. |
| **cwebp** (libwebp) | WebP compression | ✅ Wajib | `cwebp -q 80 -m 6 input.png -o output.webp`. |
| **AAPT / Android Studio** | SVG → VectorDrawable | ✅ Wajib | Auto-import via Asset Studio. |
| **Git** | Versioning | ✅ Wajib | Tracked file di repo. |

### Kapan Pakai Apa

| Aset | Wujud | Kenapa |
|---|---|---|
| Komponen UI reusable | **SVG → VectorDrawable** | Reusable; scalable; tint via theme. |
| Ikon UI | Material Icons Extended ATAU VectorDrawable | Material cukup 90%. |
| Background / ilustrasi kompleks | **WebP** | Detail terlalu banyak untuk VectorDrawable. |
| Pattern / repeating motif | VectorDrawable | Scalable. |
| Animasi stateful (sway, bounce, shake) | **Compose Canvas + Animatable** | State-driven. |
| Animasi cinematic | Lottie JSON | Designer-driven. |
| Teks Latin/Arab | **Font OFL** via Text() Compose | Contextual shaping otomatis. |
| Audio bundled | **WAV 44.1kHz** | Production voice. |

### Struktur Folder Asset

```
app/src/main/
├── res/
│   ├── drawable/
│   │   ├── backgrounds/      # WebP full-bleed per modul
│   │   ├── cards/             # VectorDrawable kartu
│   │   ├── balloons/          # VectorDrawable balon
│   │   ├── icons/             # VectorDrawable ikon non-Material
│   │   ├── buttons/           # VectorDrawable tombol
│   │   ├── headers/           # VectorDrawable header
│   │   ├── badges/            # VectorDrawable badge
│   │   ├── ribbons/
│   │   ├── decorations/       # VectorDrawable geometris
│   │   ├── rewards/           # VectorDrawable reward statis
│   │   └── effects/
│   ├── raw/                   # Audio
│   ├── font/                  # OFL font
│   └── values/
└── assets/
    ├── content/               # ContentPack JSON
    ├── lottie/                # Lottie JSON
    └── modules/               # ModuleManifest YAML
```

### Export Convention

#### VectorDrawable (SVG → XML)
```bash
# 1. Clean SVG metadata (Inkscape: "File > Clean Document")
# 2. Save as SVG, viewBox diseragamkan: 24x24 (UI), 48x48 (modul), 80x80 (badge)
# 3. Android Studio: klik kanan res/drawable/ > New > Vector Asset > pilih SVG
# 4. Color: ?attr/colorOnSurface atau @color/abatiq_{role}
```

#### WebP
```bash
cwebp -q 80 -m 6 input.png -o app/src/main/res/drawable/backgrounds/{name}.webp
# Dimensi sumber: 2× render size untuk HiDPI
```

#### Audio (WAV)
```
Bit depth: 16-bit; Sample rate: 44.1 kHz; Channels: 1 (mono) untuk prompt
Length: maks 10 detik prompt; maks 30 detik feedback/narasi
```

#### Lottie
```
Canvas: 1080×1080 atau 1920×1080; Frame rate: 30fps; Duration: maks 5 detik
No audio in Lottie; No musik; Compliance Bab 3
```

### Resolusi & Density

| Densitas | Scale | Design resolution |
|---|---|---|
| mdpi | 1× | baseline |
| xxhdpi | 3× | **default design target** |

Konvensi: aset dirancang pada 3× density. Android auto-downsample.

### Versi Aset
- Source assets di-version di Figma / Git LFS.
- Export assets di-commit di Git + tag di CHANGELOG.
- File lama **JANGAN didelete**; rename ke `{name}_v{n}.{ext}` saat ada update.
- Setiap aset baru/replace tercatat di CONTENT_REVIEW_CHECKLIST.md.

### Cara Update Aset Tanpa Ubah Kotlin
1. Designer ganti SVG → export.
2. Replace file di `res/drawable/` (nama sama).
3. Rebuild → APK update otomatis.
4. (Opsional) Rename ke v2 jika struktural → update **1 baris** di Kotlin (`R.drawable.{nama}` → `R.drawable.{nama}_v2`).

**Prinsip**: nama resource adalah stable API. Update artwork boleh tanpa rename; Kotlin code tetap tidak perlu disentuh.

---

## Bab 14 — Game Engine Framework

### Struktur (ramping, bukan over-engineering)
```
core/engine/
├── session/   {GameSession, SessionConfig, ScoringRule}
├── round/     {Round, RoundFactory, strategies/StaticRandomRoundStrategy}
├── feedback/  {FeedbackPolicy, FeedbackEvent}
├── timer/     {CountdownTimer, TimerState}
├── progress/  ProgressTracker
├── reward/    {RewardPolicy, Reward}
├── react/     {ReactionEffect, ReactionRenderer}
└── result/    {SessionSummary, ResultBuilder}
```

### Yang DILAKUKAN
- `RoundFactory` interface + `StaticRandomRoundStrategy` default.
- `ModuleManifest` interface + `ModuleRegistry`.
- `CardContent` sealed type untuk polymorphism payload.

### Yang TIDAK DILAKUKAN (anti over-engineering)
- ❌ Multi-layered sealed interfaces berlapis.
- ❌ BaseGameViewModel abstract berlapis.
- ❌ ScoringRule sebagai sealed interface (cukup data class).
- ❌ IOC container (Hilt/Koin).
- ❌ Generics berlebihan untuk 1 modul saja.

### ScoringRule (data class, simple)
```kotlin
data class ScoringRule(
    val correct: Int = 10,
    val wrong: Int = 0,
    val streakBonus: Int? = null,
)
```

### RoundFactory (interface, 1 default impl)
```kotlin
interface RoundFactory<C : CardContent> {
    fun nextRound(target: C, candidates: List<C>): Round<C>
}
class StaticRandomRoundStrategy<C : CardContent>(
    private val candidates: List<C>,
    private val random: Random = Random.Default,
) : RoundFactory<C> { /* default */ }
```

---

## Bab 15 — Component Library

### Daftar komponen reusable (semua mini-game wajib pakai)

Lihat tabel di Bab 12A. Komponen-komponen:
1. Header, Score Panel, Timer Panel, Question Box
2. Answer Card (Balloon), Buttons (Primary/Secondary/Outlined/Icon)
3. Progress Bar, Reward Dialog, Feedback Bubble
4. Badge (Star/Ribbon/Check), Icon helper, Background, Decoration
5. Module Card, Module Grid, Module Scaffold
6. **Loader, Toast / Snackbar, Empty State** (tambahan per approval owner)
7. Parental Challenge

### Aturan
- Tiap komponen generic **hanya boleh pakai Design Tokens** — tidak ada hardcoded.
- Modul berikutnya **WAJIB reuse** komponen di atas.
- Modul DILARANG membuat komponen UI baru tanpa arsitektur review.

---

## Bab 16 — Asset Library Structure

Lihat Bab 13A "Struktur Folder Asset" untuk path lengkap.

### Skema `ContentPack JSON`
```jsonc
{
  "moduleId": "tebak_huruf",
  "version": 1,
  "schema": "abatiq.content.v1",
  "displayNameId": "@string/module_tebak_huruf_title",
  "levels": [
    {
      "id": "level_a_to_j",
      "order": 0,
      "durationSeconds": 120,
      "roundSize": 3,
      "scoring": { "correct": 10, "wrong": 0 },
      "alphabet": ["A","B","C","D","E","F","G","H","I","J"],
      "audioAssets": {
        "alphabet/{letter}": "raw/alphabet/{letter}.wav",
        "feedbackCorrect": "raw/feedback/correct.wav",
        "feedbackGentle": "raw/feedback/gentle.wav"
      }
    }
  ]
}
```

### Skema `ModuleManifest YAML`
```yaml
id: tebak_huruf
order: 1
category: bermain
titleId: "@string/module_tebak_huruf_title"
subtitleId: "@string/module_tebak_huruf_subtitle"
icon: ic_module_tebak_huruf
arenaLayout: ThreeAcrossLandscape
supportedOrientations: [landscape]
complianceNote: "Background TK di-bundel, owner-confirmed, no face."
contentPack: content/tebak_huruf.json
```

---

## Bab 16A — Asset Governance

### Aturan Wajib (5 poin)

| Aset | Wujud | Alasan |
|---|---|---|
| Komponen UI reusable | **SVG → VectorDrawable** | Reusable lintas modul; tint via theme. |
| Background / ilustrasi kompleks | **WebP** | WebP 5–10× lebih kecil dari PNG. |
| Artwork utama reusable lintas game | **TIDAK BOLEH Compose Canvas** | Bukan pipeline yang bisa di-version; bukan aset reusable. |
| Compose Canvas | Hanya untuk state-driven animation (sway, bounce, shake, particle) + overlay non-artwork. | Render logic, bukan artwork. |
| Semua asset | Wajib Design Tokens | Theme tint, no hardcoded color/dp. |

### Ringkasan Peran

```
┌─────────────────────────────────────────────────────────┐
│  Artwork (desain, ilustrasi, ikon, background)          │
│  → Frozen, versioned, di-audit                         │
│  → Format: SVG / VectorDrawable / WebP                 │
└─────────────────────────────────────────────────────────┘
                            ▲ dipakai oleh
                            │
┌─────────────────────────────────────────────────────────┐
│  Compose Canvas                                         │
│  → Logic, animasi, state, partikel                      │
│  → Bukan artwork; tidak boleh simpan artwork            │
│  → Selalu pakai Design Tokens                          │
└─────────────────────────────────────────────────────────┘
```

### Aturan Tambahan
1. **Semua artwork wajib mengikuti Design Tokens** — warna di VectorDrawable dari `AbatiQColors.*` (via theme attribute).
2. **Compose Canvas yang menggambar artwork karakter/figur dekoratif langsung DILARANG** kecuali untuk efek partikel atau transisi.
3. **Canvas yang menggambar kartu jawaban/balon DIIZINKAN** untuk state animation. Tapi shape dasar kartu **harus** VectorDrawable sebagai acuan (Asset Governance principle).
4. **Penamaan aset sama** antara `R.drawable.*` resource dan `painterResource(...)` Compose reference.
5. **Semua asset reusable harus melalui code review sebelum digunakan** (sesuai approval owner — tidak perlu designer-lead sign-off karena proyek single-owner).

---

## Bab 17 — Module Playbook

### Langkah menambah modul baru (untuk Fase 2+)

**1. Designer / product**:
- Wireframe + aset di Figma.
- Compliance review Bab 3 → sign off di PR description.
- Siapkan ContentPack JSON → `assets/content/{moduleId}.json`.
- Siapkan audio WAV (jika baru) → taruh di `res/raw/{moduleId}/`.

**2. Engineering**:
- Tambah folder `modules/{moduleId}/`.
- Implement `ModuleManifest`:
  - id, titleRes, icon
  - `register(registry)`: bind `RoundFactory` strategy + `ContentPack` path
- Tambah entry di `ModuleRegistry` (di `AbatiQApplication.onCreate`).
- Tambah string ids di `strings.xml`.
- Tambah icon VectorDrawable (atau WebP) ke `res/drawable/`.
- Update `THIRD_PARTY_LICENSES.md` jika ada aset baru.
- (Fase 2+) Tambah Arabic font ke `res/font/` jika modul butuh.

**3. QA / Testing**:
- Smoke test: home → modul baru → 1 sesi → result.
- Cek audio loads (jika ada).
- Cek progress tersimpan dengan moduleId yang benar.

**4. Release**:
- Modul otomatis muncul di `HomeScreen.ModuleGrid`.
- ParentalGate global harus unlocked.

**Estimasi kasar (referensi, BUKAN target)**:
- Modul berbasis ContentPack reuse (mis. Tebak Angka dari Tebak Huruf): beberapa jam.
- Modul dengan arena layout baru (mis. Puzzle, Memory): beberapa hari.
- Modul dengan content baru besar (mis. Doa, Mengaji): beberapa hari + compliance review.

---

## Bab 18–23 — Ditunda (Trigger-Based)

| Bab | Topik | Trigger aktivasi |
|---|---|---|
| 18 | Content Lifecycle & Versioning | 2+ modul dengan content pack berbeda dan butuh migrasi. |
| 19 | i18n & RTL | Fase 3+ (Hijaiyah/Doa/Mengaji). |
| 20 | Accessibility | Rilis publik Play Store. |
| 21 | Testing Strategy | Sprint rate naik, butuh coverage target. |
| 22 | Performance Budget | APK > 30 MB atau cold start > 2s. |
| 23 | Module-specific PRD (Doa/Hijaiyah/Mengaji) | Modul-modul tsb dimulai, compliance syar'i penuh. |

---

## Migration Plan & Definition of Done

### Steps (urutan eksekusi)

| Step | Deliverable | DoD |
|---|---|---|
| 1 | Setup folder `core/`, `data/`, `modules/`, `docs/`. Migrasi file existing. | Build sukses; smoke test tebak huruf identik dengan iterasi 2. |
| 2 | Build Design Tokens. | File token exist; diekspos via Theme.kt. |
| 3 | `core/component/` — generic `AnswerCard`, `CardContent`, `Header`, `SpeakerButton`, `RewardDialog`, `FeedbackBubble`, `ScorePanel`, `TimerPanel`, `BackButton`, `ModuleCard`, `ModuleGrid`, `Ribbon`, `StarBadge`, `QuestionBox`, `ModuleScaffold`, `ParentalChallenge`, **`Loader`**, **`Toast`**, **`EmptyState`**. | 0 hardcoded dp/sp/ms/alpha; smoke test tebak huruf identik. |
| 4 | `core/engine/` — generic `GameSession`, `RoundFactory` + `StaticRandomRoundStrategy`, `CountdownTimer`, `ProgressTracker`, data classes `ScoringRule`/`FeedbackPolicy`/`RewardPolicy`. | Unit test pass; tebak huruf reuse engine baru. |
| 5 | `core/audio/` — `Channel {Prompt, Feedback, Sfx}`, refactor `GameAudioPlayer`. | Audio masih jalan seperti iterasi 2. |
| 6 | `core/module/` — `ModuleManifest`, `ModuleRegistry`, `ArenaLayout`, `ContentPackLoader`. Wrap `tebak_huruf` jadi modul pertama. | Home menampilkan 1 modul; tebak huruf dimulai dari module registry. |
| 7 | `data/usecase/` — `StartSessionUseCase`, `TapAnswerUseCase`, `EndSessionUseCase`, `ResetProgressUseCase`, `ObserveProgressUseCase`. | Repository dipanggil via UseCase; ViewModel tidak pegang DAO langsung. |
| 8 | `assets/content/tebak_huruf.json` ContentPack. | Parser valid; tebak huruf consume content pack. |
| 9 | VectorDrawable untuk silhouette balon (Asset Governance). `BalloonView` pakai `painterResource(R.drawable.ic_balloon_blue)` + Canvas overlay animasi. | Visual identik dengan iterasi 2. |
| 10 | **Plug-in test** — tambah modul dummy `tebak_angka_skeleton` yang me-register dirinya. | Modul muncul di home, **tanpa edit `core/`**. |
| 11 | Dokumentasi `docs/*.md` lengkap. | 7 file exist. |
| 12 | Update `README.md` dengan struktur baru + link ke `docs/`. | README reflect refactor. |

### DoD Keseluruhan Refactor
1. Build `app:assembleDebug` sukses tanpa error.
2. Smoke test "home → tebak huruf → 1 full sesi → result" identik dengan iterasi 2.
3. APK size tidak naik > 2 MB dari baseline (19.16 MB).
4. Unit test `GameSession` + `RoundFactory` lulus.
5. **Plug-in test** Step 10 lulus.
6. 0 hardcoded `dp/sp/ms` di `core/component/`.
7. 0 artwork utama reusable yang masih Compose Canvas.
8. PRD v2 ini disetujui owner sebagai canonical reference. ✅

---

## Checklist Pra-Implementation

### A. Persetujuan Owner
- ✅ PRD v2 APPROVED sebagai canonical reference (24 Juli 2026).

### B. Hard Constraints (tidak boleh dilanggar saat development)
- ❌ **TIDAK BOLEH** perubahan besar pada:
  - Project Structure (`core/data/modules/ui/docs/`)
  - Design Tokens
  - Asset Pipeline
  - Component Library
  - Gameplay Framework
  - Asset Library
- kecuali owner meminta revisi eksplisit.
- ❌ **TIDAK BOLEH** menambah angka hardcoded di komponen UI.
- ❌ **TIDAK BOLEH** gambar artwork utama reusable pakai Compose Canvas.
- ✅ Jika menemukan kebutuhan tidak tercantum di PRD v2 → **STOP** dan minta approval owner.

### C. Commit Conventions
Commit kecil dan terpisah per step:
- `docs(prd): finalize architecture v2 before implementation`
- `refactor(core): introduce design tokens`
- `refactor(ui): reusable component library`
- `feat(engine): implement gameplay framework`
- `feat(module): migrate tebak huruf`
- `test(core): validate plugin architecture`

Setiap step commit terpisah, memenuhi DoD step tersebut.

---

## Lampiran — Referensi Tambahan

- `docs/design-system.md` — mirror Bab 12 & 12A
- `docs/asset-pipeline.md` — mirror Bab 13
- `docs/asset-production-guide.md` — mirror Bab 13A
- `docs/asset-governance.md` — mirror Bab 16A
- `docs/component-library.md` — mirror Bab 12A & 15
- `docs/game-engine.md` — mirror Bab 14
- `docs/module-playbook.md` — mirror Bab 17

---

**END OF PRD v2.0 — FINAL APPROVED**
