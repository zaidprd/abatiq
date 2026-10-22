# Rencana Produksi: Video Belajar Menulis Huruf (a sampai z)

Status: draf untuk disetujui. Belum ada yang dieksekusi dari dokumen ini.

## 1. Diagnosis jujur kondisi sekarang

| Masalah | Penyebab | Perbaikan |
|---|---|---|
| Foto/karakter tampak pecah | Gambar generator berukuran sekitar 450 px per pose lalu diperbesar sekitar 2x | Minta generator satu pose per gambar, resolusi tinggi. Skala di video jangan melebihi 100% ukuran asli |
| Garis putih terpotong di dagu (pose melambai dan melompat) | Sisa halo putih saat kepala dipotong dari badan secara otomatis | Minta generator memberi kepala dan badan terpisah dari awal (kepala melayang), jangan dipotong otomatis |
| Karakter tidak nyambung | Gaya beda: anak 2D bergaris tepi vs kura-kura semi-3D mengilap | Pilih SATU gaya untuk semua aset (lihat keputusan 1) |
| Gerak kaku | Pose diganti dengan memudar, tanpa gerak peralihan | Pakai sedikit gerak antisipasi dan squash and stretch, dan batasi jumlah pose |
| Isi belum terarah | Dikerjakan per aset, belum ada naskah | Dokumen ini |

## 2. Keputusan

**Sudah diputuskan:**
- Gaya visual: **memakai karakter yang sudah ada** (tidak dibuat ulang). Anak laki-laki sekarang dan kura-kura tetap dipakai apa adanya.
- Nama channel: **ABATI_Q** (Abati = ayah pemilik channel). Sapaan penonton: **anak-anak soleh dan solehah**.
- Akibat: tidak ada pembuatan ulang karakter. Karena sapaannya soleh dan solehah, perlu **satu karakter tambahan**: anak perempuan berhijab (wajah polos) dengan gaya yang sama dengan anak laki-laki sekarang.

**Masih perlu diputuskan (nomor 2 sampai 5):**

1. **Satu gaya visual untuk semua**: (a) semi-3D mengilap (kura-kura sudah begini, anak perlu dibuat ulang di gaya itu), atau (b) 2D datar bergaris tepi (kura-kura perlu dibuat ulang). Saran: (a), karena kura-kura dan jalur huruf sudah di sana.
2. **Karakter pengajar**: anak (sekarang), atau hanya suaramu dengan kura-kura sebagai maskot tunggal. Opsi kedua paling hemat aset: satu karakter saja, tidak perlu rig anak.
3. **Durasi per huruf**: Marbel memakai 12 sampai 20 detik per huruf. Saran kita 20 detik.
4. **Pembuka dan penutup**: apakah dibuka dengan basmalah, dan penutup berisi apa (lihat bagian 3).
5. **Objek tiap huruf**: pakai daftar di bagian 5, dengan huruf Q ditentukan khusus.

## 3. Struktur video (diadaptasi dari naskah Marbel)

Naskah Marbel dipecah menjadi: pembuka (4 detik pertama), 26 segmen huruf (12 sampai 20 detik tiap huruf), dan penutup berisi ajakan mengunduh aplikasi. Pasar kita beda: kita tidak menjual aplikasi, penontonnya anak dengan orang tua yang mengawasi, dan tanpa musik.

| Bagian Marbel | Diadaptasi untuk kita |
|---|---|
| "Halo teman-teman, ayo belajar huruf kecil a sampai z bersama Marbel" | Pembuka pendek dengan suaramu dan salam (isi sesuai kebiasaanmu), tanpa nama merek |
| "Huruf a. Ayo ikuti garisnya dan tulis huruf a" | Sama, ditambah nama objek ("a untuk apel") |
| Instruksi gerak ("tarik garis lurus ke bawah, lalu lengkung") | Dipertahankan: ini inti pembelajaran, dan diselaraskan dengan arah tulis kura-kura |
| "Download dan mainkan Marbel Huruf di Play Store" | Diganti ajakan menulis di buku dan menonton huruf berikutnya |
| Ada game tebak/cocokkan (huruf b dan c, f) | Opsional: segmen "temukan huruf" setelah beberapa huruf |

## 4. Template satu segmen huruf (20 detik)

| Detik | Visual | Suara | SFX |
|---|---|---|---|
| 0,0 sampai 1,0 | Fade masuk. Huruf muncul perlahan | (hening) | swoosh pendek |
| 1,0 sampai 3,5 | Anak masuk berjalan, berhenti di kiri | "Huruf [x]. Ayo ikuti garisnya." | langkah sepatu |
| 3,5 sampai 12,0 | Kura-kura menulis huruf mengikuti jalur, apel muncul dan dimakan di sepanjang jalur. Anak berpose mendengar lalu menunjuk | Instruksi gerak tulis, satu kalimat per goresan | pop saat objek muncul, kunyahan saat dimakan, ting saat goresan selesai |
| 12,0 sampai 16,0 | Huruf lengkap, kura-kura pindah ke depan anak (tampak depan) | "[x] untuk [objek]. Hebat!" | boing, sparkle |
| 16,0 sampai 19,0 | Teks "[x] untuk [objek]" muncul. Anak jempol | (jeda hening) | ting |
| 19,0 sampai 20,0 | Fade keluar | (hening) | |

## 5. Daftar 26 huruf: objek dan instruksi tulis

Instruksi tulis menyesuaikan jalur tengah huruf yang akan dibuat. Objek yang bernyawa dibuat tanpa mata dan wajah. Kolom objek boleh kamu ganti.

| Huruf | Objek | Instruksi gerak |
|---|---|---|
| a | apel | lingkaran berlawanan arah jarum jam, lalu garis lurus ke bawah |
| b | bola | garis lurus ke bawah, lalu lengkung ke kanan |
| c | cangkir | lengkung terbuka ke kanan |
| d | dadu | lengkung lalu garis lurus ke bawah di kanan |
| e | es krim | garis ke kanan, lalu lengkung berputar |
| f | foto | lengkung di atas, garis lurus ke bawah, garis silang |
| g | gelas | lingkaran lalu garis turun melengkung |
| h | helm | garis lurus ke bawah, lalu lengkung |
| i | istana | garis ke bawah, lalu titik |
| j | jamur | garis ke bawah melengkung, lalu titik |
| k | kunci | garis lurus, lalu dua garis miring |
| l | lampu | garis lurus dari atas ke bawah |
| m | mangga | garis, lalu dua lengkungan |
| n | nanas | garis, lalu satu lengkungan |
| o | oven | lingkaran penuh |
| p | pensil | garis ke bawah, lalu lengkung di atas |
| q | (ditentukan) | lengkung bulat dan garis ke bawah. Jangan pakai objek yang dimakan atau dihilangkan jika terkait mushaf |
| r | roti | garis pendek, lalu lengkung kecil |
| s | sepatu | garis bergelombang dari atas ke bawah |
| t | topi | garis tegak, lalu garis melintang |
| u | ubi | lengkung ke bawah seperti mangkuk, lalu garis |
| v | vas | dua garis miring bertemu di bawah |
| w | wortel | zig-zag turun-naik |
| x | xilofon | dua garis silang |
| y | yoyo | garis menyerong, lalu garis panjang ke bawah |
| z | zaitun | garis mendatar, miring, lalu mendatar |

## 6. Naskah suara (contoh huruf a, lalu pola untuk lainnya)

Pembuka (salam dan redaksinya kamu tentukan sendiri, ini hanya kerangka):
"[Salam]. Halo anak-anak soleh dan solehah! Ini ABATI_Q. Ayo kita belajar menulis huruf kecil dari a sampai z."

Huruf a:
- "Huruf a."
- "Ayo ikuti garisnya. Putar lingkaran, lalu tarik garis lurus ke bawah."
- "A untuk apel. Hebat!"

Pola huruf lain: "Huruf [x]." / "[instruksi gerak dari tabel]." / "[x] untuk [objek]. Hebat!"

Penutup:
"Hebat! Adik-adik sudah belajar menulis huruf dari a sampai z. Ayo latihan menulis di bukumu. Sampai jumpa."

Catatan: naskah yang kamu ucapkan adalah milikmu. Dokumen ini hanya kerangka.

## 7. Peta SFX (semua dari Freesound CC0, lihat CREDITS.md)

| Momen | SFX |
|---|---|
| Fade masuk, kura-kura mulai | swoosh |
| Anak berjalan | langkah sepatu |
| Objek muncul | pop |
| Objek dimakan | kunyahan |
| Goresan selesai | ting xilofon |
| Pindah ke depan, melompat | boing |
| Huruf lengkap | sparkle |
| Teks muncul | ting |
Tanpa musik, hanya SFX dan suaramu.

## 8. Pembagian kerja

| Siapa | Mengerjakan |
|---|---|
| Kamu | Rekam suara, tentukan gaya dan objek, buat aset gambar lewat generator (prompt disediakan), cek hasil |
| Claude + EffectCraft | Jalur tengah huruf a sampai z, animasi kura-kura menulis, penempatan objek dan apel, pose anak, transisi, penempatan SFX, ekspor MP4 |
| PhotoCraft | Membuang latar dan membersihkan tepi aset gambar |

## 9. Aset yang dibutuhkan (satu kali, dipakai ulang di 26 huruf)

1. Kura-kura: tampak atas (kepala, tempurung, 4 kaki) dan tampak depan (kepala besar, 4 kaki). Sudah ada, perlu satu gaya dengan anak.
2. Dua karakter: **anak laki-laki (sudah ada)** dan **anak perempuan berhijab (baru, gaya sama dengan anak laki-laki)**, wajah polos (tanpa mata, hidung, mulut), pipi merah muda. Pose masing-masing: berdiri, berjalan (6 gambar), melambai, mendengar, menunjuk, jempol, bertepuk, melompat. Kepala melayang terpisah dari badan. Resolusi tinggi, satu gambar per pose. Hijab dan gamis menutup badan, jadi gerak kaki tidak terlihat: pose berjalan perempuan cukup meluncur ringan atau berganti pose.
3. 26 objek (apel, bola, cangkir, dan seterusnya) dalam satu gaya, tampak atas, latar bersih. Prioritas: yang dimakan kura-kura (apel, mangga, nanas, roti, wortel).
4. Latar dan lantai: sudah ada, mungkin dua variasi warna agar tidak monoton.
5. SFX: sudah ada. Tambah bila perlu (tepuk, koreksi lembut).

## 10. Urutan kerja yang disarankan

1. Kamu putuskan keputusan di bagian 2.
2. Rapikan aset karakter satu gaya (kamu lewat generator, prompt dari Claude).
3. Claude merombak huruf "a" memakai aset baru dan template segmen bagian 4.
4. Kamu rekam suara huruf a, Claude memasangnya. Cek dan koreksi bersama.
5. Template jadi, baru Claude mengerjakan jalur dan objek huruf lainnya, dalam kelompok kecil, bukan sekaligus 26.

## 11. Keputusan final (menggantikan bagian 2 dan tabel objek bagian 5)

- Gaya: pakai karakter yang sudah ada, tanpa dibuat ulang. Channel: ABATI_Q. Sapaan: anak-anak soleh dan solehah.
- Karakter: anak laki-laki dan anak perempuan berhijab (wajah polos).
- Durasi per huruf: **20 detik**.
- **Tanpa penutup dulu** (bagian penutup naskah ditunda).
- Objek huruf: **prioritas buah**. Kalau tidak ada buah yang pas, baru makanan. **Tidak memakai hewan.**
- Hewan bernyawa tetap hanya maskot kura-kura (tanpa mata dan wajah).

### Objek huruf (usulan, urutan: buah, lalu makanan; tanda ? = perlu kamu pilih)
| Huruf | Usulan | Jenis |
|---|---|---|
| a | apel | buah |
| b | belimbing | buah |
| c | ceri | buah |
| d | durian | buah |
| e | es krim | makanan (buah belum ada) |
| f | fuji (apel fuji) | buah |
| g | gandaria | buah |
| h | honeydew (melon honeydew) | buah |
| i | irisan semangka | buah |
| j | jeruk | buah |
| k | kelapa | buah |
| l | leci | buah |
| m | mangga | buah |
| n | nanas | buah |
| o | oren (jeruk oren) | buah |
| p | pepaya | buah |
| q | quince (buah quince) | buah |
| r | rambutan | buah |
| s | semangka | buah |
| t | tomat | buah/sayur |
| u | ubi | makanan |
| v | vla | makanan |
| w | wortel | sayur |
| x | buah eksotis campur (tanpa mengeja) | buah |
| y | yoghurt | makanan |
| z | zaitun | buah |
Huruf f, h, i, o, q, x sudah diisi usulan (boleh kata yang mengandung huruf itu atau campuran buah, tidak harus dieja dari huruf pertama). Perlu dikoreksi kalau kurang pas. Saya belum mengecek ejaan dan kelaziman tiap kata, jadi tolong koreksi kalau ada yang kurang pas.

## 12. Editing: Premiere Pro atau FilmCraft
Lihat jawaban di percakapan. Singkatnya: untuk 26 video pendek ini FilmCraft cukup.

## 13. Prinsip gerak karakter (keputusan)
Anak hanya pendamping: **diam dengan napas halus dan sedikit gerak kepala**, tanpa ganti pose, melompat, atau melambai besar, supaya perhatian anak tertuju ke huruf. Gerak utama ada pada kura-kura yang menulis dan memakan objek. Aset pose tambahan (menunjuk, jempol, jalan, dan sebagainya) disimpan untuk keperluan tertentu saja.

## 14. Pembaruan aturan (22 Okt)
- **Hewan boleh** (sama dengan manusia: makhluk bernyawa), asal **faceless**: siluet datar, atau 2D dengan kepala terpisah dan wajah polos. Tanpa mata, hidung, mulut.
- Objek huruf **tidak harus buah**. Yang utama: tidak melanggar aturan faceless. Urutan pilihan tetap buah, makanan, benda, hewan faceless.
- Aset sederhana (buah, keranjang, siluet hewan) dibuat lewat VectorCraft. Karakter organik (anak, kura-kura) lewat generator gambar, dirapikan dengan PhotoCraft.
