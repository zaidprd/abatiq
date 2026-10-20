# Catatan serah-terima (untuk melanjutkan di komputer lain)

Terakhir diperbarui: 20 Oktober 2026. Baca bersama `README.md` dan `CREDITS.md`.

## Tujuan
Video belajar huruf untuk anak. Satu karakter anak (wajah polos) yang mengajar, dan satu maskot kura-kura yang menulis huruf mengikuti jalur sambil memakan apel. Terinspirasi gaya video tracing huruf yang populer, tapi pasar dan konten disesuaikan.

## Aturan konten (jangan dilanggar)
- **Tanpa musik.** Hanya efek suara (SFX) dan suara pengajar (direkam sendiri oleh pemilik).
- **Karakter tanpa wajah** (tanpa mata dan mulut). Kepala dan badan **terpisah** (kepala sendiri), mudah dianimasikan.
- Hewan (kura-kura) tetap **tanpa mata dan wajah**. Utamakan objek benda mati atau makanan.
- Sesuai pemahaman sunnah. Untuk huruf Q: jangan jadikan Al-Qur'an sebagai benda yang dimakan, hilang, atau diinjak.

## Status sekarang
Huruf **"a"** sudah jadi sebagai prototipe di `Belajar_Huruf_a.ecproj` (komposisi `Belajar_Menulis_Huruf_a`, 1920x1080, 30 fps, 7,2 detik).

Alur animasi:
- 0 sampai 1,5 dtk: anak muncul dan melambai, 6 apel muncul satu per satu.
- 1,2 sampai 3,6 dtk: kura-kura menulis lingkaran (berlawanan jarum jam), memakan 4 apel.
- 3,6 sampai 4,0 dtk: melompat ke atas batang.
- 4,0 sampai 5,4 dtk: menulis batang ke bawah, memakan 2 apel.
- 5,6 dtk ke atas: anak mengangkat tangan dan melompat dua kali.

SFX (semua CC0, lihat `CREDITS.md`): apel muncul, gerak lompat, ting tiap goresan selesai, boing saat anak melompat, sparkle saat huruf lengkap, kunyahan saat makan.

## Struktur proyek EffectCraft
- `Letter_Outline`, `Letter_Base`, `Letter_Reveal`: huruf dari **satu jalur tengah** (`Bowl` dan `Stem`). Outline, isian (trim path), kura-kura, dan apel semuanya memakai jalur yang sama, jadi selalu sejajar.
- Geometri huruf "a": pusat lingkaran (1100, 585), radius 180, tebal goresan 118, batang di x=1280. Lingkaran mulai dari sudut 45 derajat berlawanan jarum jam, lalu batang ke bawah.
- `Mobil` (disembunyikan): penggerak posisi, rotasi, dan skala. `KuraP_Root` mengikuti `Mobil`. Jangan dihapus.
- `KuraP_*`: bagian kura-kura (kepala, tempurung, 4 kaki) dari `aset/kura_ai/bagian/`.
- Karakter anak: `Char_Root`, `Torso`, `Head`, `Hair`, lengan, kaki. Dibuat dari bentuk bezier terpisah (parent-child).
- Waktu penting: lingkaran 1,2 sampai 3,6; lompat sampai 4,0; batang 4,0 sampai 5,4; perayaan mulai 5,6. Detik apel dimakan: 1,788; 2,16; 2,424; 2,702; 4,586; 4,905.

## Pelajaran teknis (EffectCraft lewat skrip)
- `layer.parent = x` **mempertahankan posisi dunia**, jadi atur posisi lokal sesudah parenting.
- Null object punya anchor default (50,50). Set ke (0,0) sebelum dipakai sebagai induk.
- Ekspresi yang diawali minus tepat sesudah deklarasi fungsi bisa salah terbaca. Pakai `var r=...; r;`.
- `replaceSource` belum didukung. Hapus dan tambah ulang layer.
- Akses file skrip dibatasi ke folder proyek. File footage harus di dalam folder proyek.
- Impor PNG dan MP3 jalan. Impor model GLB sebagai layer 3D juga jalan (belum dipakai).

## Yang belum beres / langkah berikutnya
1. **Tonton hasilnya dan koreksi.** Gerak dan SFX baru dicek lewat frame diam, belum ditonton penuh.
2. **Perlambat seluruh animasi.** Lingkaran 2,4 detik masih terlalu cepat untuk kura-kura. Ini berarti mengatur ulang waktu: trim isian, ekspresi `Mobil`, kunci apel, daftar waktu makan, SFX, dan perayaan anak.
3. Periksa ukuran kura-kura (skala di `KuraP_Root`, saat ini 0,22) dan kontras warna isian (aprikot).
4. **Jalur tengah huruf a-z** dengan urutan goresan yang benar, sebagai template. Satu skrip pembuat supaya 26 huruf tinggal ganti data.
5. **Objek per huruf** (kata Indonesia, utamakan makanan yang dimakan kura-kura): A apel, B bola, C cangkir, D dadu, E es krim, F foto, G gelas, H helm, I istana, J jamur (aset ada), K kunci, L lampu, M mangga, N nanas, O oven, P pensil, Q (hati-hati, lihat aturan), R roti, S sepatu, T topi, U ubi, V vas, W wortel, X xilofon, Y yoyo, Z zaitun.
6. Teks penutup ("a untuk apel"), ruang untuk suara pengajar, intro dan outro, ekspor MP4.
7. Bagian maskot lain (huruf kapital, hijaiyah) belum dibahas.

## Alat yang dipakai
- **EffectCraft** (alternatif After Effects, bisa dikendalikan Claude lewat MCP).
- **VectorCraft** (alternatif Illustrator, ada CLI `vectorcraft-cli run --cmd ... --export ...`).
- **Freesound API** untuk SFX. Kuncinya ada **hanya di komputer pemilik** (jangan diunggah ke repo).

## Pembaruan: karakter anak (belum dipasang)
- Proyek `Belajar_Huruf_a.ecproj` di repo masih memakai **anak gaya flat lama** (utuh, jalan). Perakitan anak semi-3D sempat dicoba tapi rusak dan **tidak disimpan**.
- Keputusan sementara: anak **semi-3D** sekeluarga dengan kura-kura. Dasar rig: `aset/anak/referensi/lembar_bagian_v2.png` (kepala, badan tanpa lengan, lengan atas/bawah, celana, kaki). Tambahan: `lembar_tangan.png` (8 pose tangan), `lembar_kepala.png`.
- Kekurangan lembar v2: kepala agak kecil (skalakan), celana satu bagian dengan penyambung kulit di pergelangan.
- `aset/make_anak_bagian.py` memecah gambar A-pose utuh (`aset/anak/anak_utuh.png`), tapi hasil bahu/siku kasar. Lebih baik memecah lembar v2.
- Pelajaran: setelah `parent`, skala komposisi anak ikut berubah (skala anak harus 100 kecuali akar). Akar `Anak_Root` skala ~37,5%.
- Ide: karakter pengajar "Abati" dewasa, wajah polos, dibedakan lewat peci/jenggot/baju. Keputusan belum final (aturan tanpa wajah dipertahankan).
