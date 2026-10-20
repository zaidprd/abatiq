# Video Belajar Huruf (anak-anak)

Animasi belajar menulis huruf untuk anak: sebuah kura-kura menyesot mengikuti jalur huruf, memakan apel di sepanjang jalur, sementara karakter anak (tanpa wajah) menonton lalu melompat senang saat huruf selesai. Tanpa musik, hanya efek suara.

Dibuat dengan **EffectCraft** (alternatif After Effects) dan **VectorCraft**.

## Isi
- `Belajar_Huruf_a.ecproj`: proyek EffectCraft, huruf "a" (7,2 detik, 1920x1080, 30 fps).
- `aset/kura_ai/`: kura-kura (gambar utuh dan hasil pecahan di `bagian/`).
- `aset/make_kura_ai_bagian.py`: skrip pemecah kura-kura (butuh Python, OpenCV, NumPy).
- `aset/jamur_*` dan `aset/make_jamur.py`: aset jamur untuk huruf "j" (butuh VectorCraft CLI).
- `sfx/`: efek suara CC0, lihat `CREDITS.md`.

## Catatan
- Proyek `.ecproj` menyimpan **path absolut** (`E:/latihan/AE/...`). Taruh repo ini di `E:\latihan\AE` agar footage terbaca, atau hubungkan ulang footage di EffectCraft.
- Animasi disusun lewat ekspresi: huruf ditulis oleh jalur tunggal yang dipakai bersama garis, isian, dan kura-kura.
