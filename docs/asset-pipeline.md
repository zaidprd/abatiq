# Asset Pipeline

> Mirror PRD v2 Bab 13.

Lihat: [`PRD_v2_Framework_Dan_Scalability.md`](../PRD_v2_Framework_Dan_Scalability.md)

## Pipeline

```
Designer → SVG / PNG / WAV / Lottie → VectorDrawable / WebP / res/raw / assets/lottie
```

## Aturan

- Semua aset baru wajib compliance check + sign-off.
- Open-source: hanya MIT / Apache-2.0 / OFL / CC0.
- Vektor → VectorDrawable Android.
- Raster → WebP (bukan PNG).
- Audio → WAV 44.1kHz 16-bit mono.
- Lottie → JSON di `assets/lottie/`.
- Font → TTF/OTF di `res/font/`, OFL.
- Setiap penambahan → commit `THIRD_PARTY_LICENSES.md`.
