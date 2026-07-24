# Asset Governance

> Mirror PRD v2 Bab 16A.

Lihat: [`PRD_v2_Framework_Dan_Scalability.md`](../PRD_v2_Framework_Dan_Scalability.md)

## Aturan Wajib

1. **Komponen UI reusable** → SVG → VectorDrawable.
2. **Background / ilustrasi kompleks** → WebP.
3. **Artwork utama reusable lintas game** → **TIDAK BOLEH** Compose Canvas.
4. **Compose Canvas** → hanya untuk animasi stateful + overlay non-artwork.
5. **Semua asset** → wajib Design Tokens.

## Ringkasan

- Artwork = frozen, versioned, di-audit (SVG / VectorDrawable / WebP).
- Compose Canvas = logic, animasi, state, partikel (bukan artwork).
- Semua warna dari `AbatiQColors` via theme attribute.
