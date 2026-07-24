# Asset Production Guide

> Mirror PRD v2 Bab 13A — detail tools, export convention, naming, resolusi, versi.

Lihat: [`PRD_v2_Framework_Dan_Scalability.md`](../PRD_v2_Framework_Dan_Scalability.md)

## Tools Default

- **Inkscape** — DEFAULT untuk SVG.
- **Audacity** — audio editing.
- **cwebp** (libwebp) — WebP compression.
- **AAPT / Android Studio** — SVG → VectorDrawable.
- **Git** — versioning.

Opsional: Adobe Illustrator, Figma, Canva, After Effects + Bodymovin.

## Kapan Pakai Apa

- Komponen UI reusable → SVG → VectorDrawable.
- Background / ilustrasi kompleks → WebP.
- Animasi stateful → Compose Canvas + Animatable (bukan artwork).
- Animasi cinematic → Lottie JSON.
- Teks Latin/Arab → Font OFL via Text() Compose.
- Audio → WAV.

## Naming

- VectorDrawable: `{category}_{module}_{purpose}_{variant}_{state}.xml`
- WebP: `{category}_{module}_{purpose}_{variant}.webp`
- Audio: `{prompt}_{variant}.wav`
- Lottie: `{scene}_{variant}.json`
- Font: `{family}_{weight}.ttf`

## Export

Lihat PRD v2 untuk command-line examples per format.
