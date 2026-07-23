# Product Requirements Document (PRD)
# Aplikasi Belajar & Bermain Anak Muslim — "AbatiQ"

**Versi Dokumen:** 1.0
**Tanggal:** 23 Juli 2026
**Status:** Draft — Fokus Pengembangan Fase 1
**Referensi kompetitor:** Solite Kids – Belajar TK dan PAUD (Google Play)

---

## 1. Ringkasan Eksekutif

Aplikasi ini adalah aplikasi edukasi anak usia dini (PAUD/TK, 3–6 tahun) berbasis Android native (Kotlin), terinspirasi dari struktur fitur aplikasi kompetitor (Solite Kids), namun **dibangun ulang dari nol** dengan filosofi konten yang disesuaikan untuk target pasar keluarga Muslim yang berpegang pada pemahaman Salafy (mengikuti manhaj Ahlus Sunnah wal Jama'ah). Aplikasi ini **bukan tiruan/plagiat** — struktur kategori permainan boleh terinspirasi, tetapi seluruh pendekatan visual, audio, dan interaksi dibangun dengan aturan konten yang berbeda secara fundamental (lihat Bab 3).

Pengembangan akan dilakukan **bertahap (phased release)**. Fase 1 (MVP) akan fokus penuh pada satu modul permainan: **"Tebak Huruf"** (game tebak huruf hijaiyah/Latin berbasis audio), untuk memvalidasi arsitektur teknis, pipeline audio/voice-over, dan sistem reward sebelum modul lain dibangun.

---

## 2. Tujuan Produk

- Membantu anak usia dini belajar huruf, angka, warna, doa, dan adab dengan cara interaktif.
- Menyediakan lingkungan digital yang **aman secara syar'i** untuk keluarga yang menghindari musik dan gambar wajah/makhluk bernyawa secara utuh, sesuai pemahaman sebagian ulama Salafy tentang hukum musik dan gambar.
- Menjadi alternatif halal dari aplikasi edukasi anak mainstream yang umumnya memakai musik latar dan karakter kartun berwajah.

## 3. Prinsip Desain & Batasan Konten (WAJIB — Non-Negotiable)

Ini adalah pembeda utama dari aplikasi referensi, dan menjadi *guardrail* yang harus dipatuhi oleh seluruh tim (desainer, audio engineer, developer):

| Aspek | Aturan |
|---|---|
| **Musik** | **Tidak ada musik/instrumen dalam bentuk apa pun** — tidak ada backsound, tidak ada jingle, tidak ada lagu anak bernada musik. Umpan balik audio hanya berupa: suara manusia (voice over polos/a cappella tanpa nada musik terstruktur), efek suara non-musikal (mis. "klik", "tap", nada lonceng tunggal sederhana jika dianggap perlu — perlu ditinjau ulang dengan pihak yang paham fiqih terkait). |
| **Karakter/Ilustrasi** | Karakter maskot **faceless** (tanpa wajah) — tidak ada mata, hidung, mulut yang digambar pada figur manusia/hewan. Karakter direpresentasikan lewat siluet, objek, atau figur dengan wajah tertutup/terpotong/diabstraksi (mis. tertutup topi, buku, atau hanya tampak dari belakang), mengikuti praktik ilustrasi anak yang sudah umum di kalangan penerbit buku anak Muslim konservatif. |
| **Karakter berlisensi/IP** | Tidak boleh menggunakan karakter berhak cipta (mis. Doraemon, dsb. seperti pada aplikasi referensi). Semua aset ilustrasi original. |
| **Konten ibadah** | Materi doa, huruf hijaiyah, dan adab harus direview oleh pihak yang memiliki kapasitas keilmuan syar'i (ustadz/asatidzah rujukan) sebelum publish — bukan sekadar hasil scraping/kompilasi umum. |
| **Representasi makhluk bernyawa** | Hewan boleh digambarkan namun disarankan gaya sederhana/kartun minim detail wajah, atau diganti dengan pendekatan ikon/siluet bila memungkinkan — kebijakan final menyesuaikan arahan pemilik produk. |
| **Nada suara feedback** | Tidak "menghakimi" secara kasar saat anak salah. Bahasa tetap lembut dan mendidik (lihat Bab 6.4). |

> **Catatan penting:** Aturan gambar makhluk bernyawa dan hukum musik adalah area yang ada perbedaan pendapat (khilafiyah) di kalangan ulama. Dokumen ini mengikuti preferensi konservatif yang diminta pemilik produk. Tim disarankan tetap berkonsultasi dengan pihak yang memiliki otoritas keilmuan untuk memastikan implementasi akhir sesuai standar yang dituju sebelum rilis publik.

---

## 4. Target Pengguna

- **Pengguna utama:** Anak usia 3–6 tahun (PAUD/TK).
- **Pengguna sekunder (pengambil keputusan unduh):** Orang tua Muslim, khususnya yang mengikuti manhaj Salafy, mencari media belajar tanpa musik dan tanpa gambar wajah.
- **Persona orang tua:** "Ummu Fulan", 28–40 tahun, aktif di komunitas kajian, sudah menyaring konten anak (YouTube Kids, dst.), mencari aplikasi lokal yang sejalan dengan nilai keluarganya.

---

## 5. Katalog Fitur Lengkap (Referensi dari Kompetitor — Dicatat untuk Roadmap Jangka Panjang)

Seluruh modul di bawah ini **dicatat sebagai backlog produk penuh**, disusun ulang ke dalam dua kategori besar seperti aplikasi referensi: **Belajar** (mode edukasi tenang) dan **Bermain** (mode game dengan skor/waktu). Modul yang beririsan dengan aturan Bab 3 (musik, karakter berlisensi, gambar wajah) ditandai dengan catatan adaptasi.

### 5.1 Kategori "Belajar"
| # | Modul | Deskripsi Singkat | Catatan Adaptasi |
|---|---|---|---|
| 1 | Huruf (ABC) | Pengenalan huruf besar/kecil | Voice over tanpa musik |
| 2 | Angka | Pengenalan angka 1–10+ | — |
| 3 | Membaca (Suku Kata) | Belajar membaca suku kata (a-bu, dst.) | — |
| 4 | Menulis Huruf | Trace huruf di layar | — |
| 5 | Penjumlahan & Pengurangan | Matematika dasar dengan visual buah/benda | — |
| 6 | Mewarnai | Mewarnai objek/hewan | Ganti buku gambar bertema karakter lisensi dengan ilustrasi original faceless |
| 7 | Warna & Bentuk | Pengenalan warna dan bentuk benda | — |
| 8 | Belajar Buah & Sayur | Pengenalan nama buah/sayur | — |
| 9 | Belajar Hewan | Pengenalan nama & suara hewan | Ilustrasi hewan gaya sederhana, minim ekspresi wajah |
| 10 | Belajar Alat Transportasi | Pengenalan kendaraan | — |
| 11 | Belajar Piano | Modul musik alat piano | **Dihapus/diganti total** — bertentangan dengan aturan no-musik. Bisa diganti "Belajar Adzan/Iqamah" atau dihilangkan dari roadmap |
| 12 | Belajar Bahasa Inggris | Kosakata dasar bahasa Inggris | — |
| 13 | Belajar Bahasa Arab | Kosakata dasar bahasa Arab | Prioritas tinggi, selaras dengan target pasar |
| 14 | Belajar Doa Anak Muslim | Kumpulan doa harian anak | Wajib review konten oleh ustadz rujukan |
| 15 | Belajar Mengaji | Pengenalan huruf hijaiyah & cara baca | Prioritas tinggi |
| 16 | Menyanyi / Lagu Anak | Lagu anak Indonesia/Muslim/Nasional/Daerah | **Dihapus total** (mengandung musik) — diganti modul "Murottal/Nasyid Tanpa Musik" atau "Hafalan Doa & Surat Pendek" jika sesuai arahan produk |
| 17 | Belajar Membandingkan | Konsep besar-kecil, banyak-sedikit | — |
| 18 | Pondok Ramadan / Petualangan Ramadan | Modul musiman: Bangun Sahur, Niat Puasa, Doa Berbuka, Zakat, Idul Fitri, dll. | Konten ibadah wajib direview syar'i; tanpa musik perayaan |
| 19 | Belajar Cerita Anak | Cerita anak interaktif | Cerita bertema adab & kisah nabi/sahabat (bukan dongeng fiksi bermuatan sihir/khurafat) |

### 5.2 Kategori "Bermain" (Game dengan skor/timer)
| # | Modul | Deskripsi Singkat | Catatan Adaptasi |
|---|---|---|---|
| 1 | **Tebak Huruf** | Dengar audio target, pilih huruf A–J pada balon yang terus melayang selama 60 detik | **→ FASE 1 (lihat Bab 6)** |
| 2 | Cocokkan Huruf Besar & Kecil | Mencocokkan pasangan huruf | Fase 2 |
| 3 | Tebak Bacaan | Dengar kata, pilih gambar yang sesuai | Fase 2 |
| 4 | Tebak Balon Angka | Tebak angka dari balon | Fase 2 |
| 5 | Tebak Transportasi | Tebak nama alat transportasi | Fase 2 |
| 6 | Tebak Buah & Sayur | Tebak nama buah/sayur | Fase 2 |
| 7 | Tebak Hewan / Bacaan Hewan | Tebak suara/nama hewan | Fase 2 |
| 8 | Arsitek Kereta Api | Game puzzle jalur rel | Fase 3 |
| 9 | Petualangan Hamster (maze) | Game maze arah | Ganti karakter hamster faceless, atau ganti tema non-hewan (mis. "Petualangan di Pasar") |
| 10 | Bermain Konsentrasi | Game memory/ingatan | Fase 3 |
| 11 | Bermain Perbandingan | Game logika >,<,= | Fase 3 |
| 12 | Bermain Dressup | Ganti pakaian karakter | **Perlu tinjauan khusus** — figur karakter faceless, pakaian menutup aurat, sesuai adab berpakaian |
| 13 | Mencocokkan Kartu | Memory matching kartu | Fase 3 |
| 14 | Puzzle Animasi / Puzzle Umum | Puzzle gambar | Ganti tema puzzle lisensi (Doraemon dll.) dengan ilustrasi original |
| 15 | Tempel Puzzle | Drag & drop potongan puzzle | Fase 3 |
| 16 | Ujian Mengaji | Tes bacaan hijaiyah | Prioritas tinggi, terhubung ke modul Mengaji |
| 17 | Menggambar / Papan Kreasi | Kanvas gambar bebas | Fase 3 |

### 5.3 Mode Permainan (Struktur Navigasi)
Mengikuti pola aplikasi referensi:
- **Mode Peta:** Peta dunia interaktif, tiap modul direpresentasikan sebagai "pos" yang bisa diklik (world map).
- **Mode Basic:** Tampilan grid/list sederhana berisi ikon menu tiap modul — direkomendasikan sebagai default untuk Fase 1 (lebih cepat dibangun, lebih ringan).

---

## 6. FASE 1 (MVP) — Detail Spesifikasi: Modul "Tebak Huruf"

### 6.1 Tujuan Modul
Melatih anak mengenali bentuk huruf (Latin A–Z dan/atau Hijaiyah, dikonfirmasi ke Bab 6.7) melalui asosiasi audio-visual, dengan sistem reward yang positif dan mendidik.

### 6.2 User Story
> Sebagai anak usia 3–6 tahun, saya ingin mendengar instruksi suara yang menyebutkan sebuah huruf, lalu memilih huruf yang benar dari beberapa pilihan yang tampil di layar, sehingga saya belajar mengenali huruf sambil bermain dan mendapat pujian saat benar.

### 6.3 Alur Permainan (Game Flow)

> **Keputusan pemilik produk (revisi 23 Juli 2026):** spesifikasi di bagian ini menggantikan konsep awal tiga kartu, maksimal dua percobaan, dan sesi 5–10 soal. Fase 1 menggunakan permainan balon huruf berbasis waktu selama 60 detik.

1. **Layar Mulai Modul** — Anak menekan modul "Tebak Huruf" dari menu utama.
2. **Instruksi Audio Otomatis** — Begitu sesi dimulai, sistem menyebutkan huruf target dalam Bahasa Indonesia. Tidak ada teks pertanyaan di layar permainan karena pengalaman harus dapat digunakan anak yang belum bisa membaca.
3. **Arena Balon Huruf** — Tepat tiga balon huruf kapital A–J muncul hampir sejajar dan bergerak perlahan ke atas. Selama anak belum memilih salah satunya, tidak ada huruf baru yang muncul. Jika mencapai batas atas, tiga huruf tersebut menunggu dan tetap dapat dipilih.
4. **Interaksi Anak** — Anak mendengarkan target lalu menekan salah satu balon. Tombol speaker selalu tersedia untuk mengulang suara target.
5. **Evaluasi Jawaban dan kelanjutan permainan:**
   - **Jika benar:** balon mendapat indikator visual positif, skor bertambah **10 poin**, sistem langsung memilih dan menyebutkan target berikutnya, lalu permainan terus berjalan.
   - **Jika belum tepat:** balon mendapat indikator visual lembut, skor tidak bertambah dan tidak berkurang, sistem tetap langsung memilih dan menyebutkan target berikutnya. Tidak ada retry, reveal, atau jeda yang menghentikan aliran permainan.
   - Pada kedua hasil, tap menyelesaikan ronde: ketiga huruf lama langsung hilang bersama dan diganti satu kelompok baru berisi tepat tiga huruf A–J acak dari bawah. Tidak ada huruf dari ronde lama dan baru yang tampil bersamaan.
   - Selama anak belum memilih salah satu huruf pada ronde aktif, kelompok berikutnya tidak boleh dibuat atau ditampilkan.
   - Baik jawaban benar maupun belum tepat dicatat sebagai attempt lokal untuk ringkasan progres.
6. **Durasi dan akhir sesi** — Sesi berlangsung tepat **60 detik**, bukan berdasarkan jumlah soal. Saat waktu habis, arena berhenti dan layar ringkasan menampilkan skor akhir, jumlah tap benar, skor terbaik, serta audio penutup: *"Alhamdulillah, sesi belajar selesai!"*
7. **Aturan skor** — Jawaban benar bernilai `+10`; jawaban belum tepat bernilai `0`; tidak ada pengurangan skor dan tidak ada batas skor maksimum selain durasi sesi.

### 6.4 Prinsip Copywriting Audio Feedback
- Selalu positif dan membangun, tidak pernah menyalahkan anak secara langsung ("Kamu salah!" dihindari) — fokus pada tindakan ("belum tepat", "coba lagi").
- Ucapan syukur/pujian Islami digunakan secara tepat dan tidak berlebihan (MasyaAllah, Barakallah, Alhamdulillah) — perlu direview agar penggunaannya sesuai konteks dan tidak terkesan "template kosong".
- Semua audio direkam oleh voice talent manusia (anak-anak atau dewasa dengan nada ramah anak), **bukan text-to-speech robotic** bila memungkinkan, untuk kehangatan emosional — namun TTS bisa dipakai sebagai fallback tahap awal development/testing.
- Tidak ada elemen musik dalam file audio manapun — hanya suara vokal dan efek suara non-musikal sederhana (mis. "ting" nada tunggal untuk transisi, perlu ditinjau ulang).

### 6.5 Progresi & Level (Fase 1 scope minimum)
- **Level 1:** Huruf kapital A–J (10 huruf pertama) — untuk MVP awal, batasi dulu ke subset huruf agar development lebih fokus.
- **Level 2 (fase 1.1, opsional jika waktu memungkinkan):** Huruf kapital K–Z.
- Struktur data dibuat modular agar mudah menambah level/huruf hijaiyah di fase berikutnya tanpa refactor besar.

### 6.6 Wireframe (Deskripsi Layar)
```text
┌─────────────────────────────────────┐
│ [Kembali]      48 dtk    ★ 120   🔊 │
│                                     │
│      (ornamen geometris tipis)      │
│       ◯ A      ◯ D      ◯ J         │
│                                     │
│                                     │
│                                     │
│                                     │
│       (bukit/daun dekoratif)        │
└─────────────────────────────────────┘
```
- Area permainan didominasi latar kosong berwarna hangat agar balon mudah dilacak.
- Ornamen geometris, botani, lengkung, bintang, titik, dan bukit hanya ditempatkan di tepi serta tidak menghalangi balon.
- Balon dibuat dari shape/Canvas Compose: warna bulat, huruf besar, dan tali sederhana; tanpa wajah atau ilustrasi makhluk bernyawa.
- Tidak ada teks pertanyaan pada layar inti. Top bar hanya berisi navigasi, timer, skor, dan tombol speaker.
- Tombol speaker wajib ada agar anak bisa memutar ulang instruksi kapan saja.

### 6.7 Keputusan Scope Fase 1
1. Huruf: **Latin kapital A–J**.
2. Bahasa instruksi: **Bahasa Indonesia**.
3. Pengguna: **single-user local progress**, tanpa akun atau multi-profil.
4. Target usia: **3–6 tahun**, dengan UI audio-first dan target sentuh besar.
5. Audio development: Android Text-to-Speech boleh digunakan sementara. Untuk rilis, pemilik produk akan menggantinya dengan rekaman suara sendiri melalui abstraksi audio yang sama.
6. Aset visual: tidak membutuhkan SVG eksternal untuk MVP karena balon dan ornamen dapat digambar menggunakan Compose Canvas/vector shapes. Jika kelak membutuhkan ilustrasi kompleks, lisensi dan compliance wajib direview lebih dahulu.

---

## 7. Spesifikasi Teknis

### 7.1 Platform & Stack
- **Platform:** Android native murni.
- **Bahasa:** **Kotlin 100%** — tidak menggunakan Flutter, React Native, Unity, atau cross-platform framework lain.
- **UI Toolkit:** Jetpack Compose (direkomendasikan untuk kecepatan development modern) — alternatif: XML layout tradisional jika tim lebih familiar. *(Perlu konfirmasi preferensi tim dev.)*
- **Arsitektur:** MVVM (Model-View-ViewModel) + Repository Pattern.
- **Audio Playback:** abstraksi `GameAudioPlayer`; implementasi development memakai Android `TextToSpeech` Bahasa Indonesia. Rekaman suara lokal pemilik produk akan menggantikan implementasi ini sebelum rilis tanpa mengubah ViewModel/UI.
- **Local Storage:** Room Database (progress anak, skor, level selesai) + DataStore (preferensi pengaturan, mis. volume, profil aktif).
- **Aset gambar:** shape dan Canvas Jetpack Compose untuk balon serta ornamen geometris agar ringan dan scalable. SVG/`VectorDrawable` hanya digunakan jika kebutuhan visual tidak dapat dicapai secara wajar melalui primitive Compose.
- **Minimum SDK:** Disarankan API 24 (Android 7.0) ke atas untuk menjangkau perangkat orang tua kelas menengah di Indonesia — perlu dikonfirmasi berdasarkan data target device.
- **Tanpa koneksi internet wajib:** Modul Fase 1 harus bisa dimainkan 100% offline (semua aset audio/gambar bundled di APK atau di-download sekali saat instalasi awal).

### 7.2 Strategi Sourcing Aset Visual — Open Source First

**Kebijakan wajib untuk seluruh tim (desain & dev):** sebelum membuat aset baru dari nol, **cari dulu library open-source gratis di GitHub/npm** yang lisensinya jelas mengizinkan penggunaan komersial (MIT, Apache-2.0, OFL, CC0). Ini menghemat waktu development dan biaya desain, terutama untuk aset huruf/ikon yang sifatnya generik (bukan konten Islami spesifik yang tetap butuh desain custom + review syar'i).

**Daftar kandidat library yang sudah dicek (per Juli 2026):**

| Kebutuhan Aset | Library/Sumber | Lisensi | Catatan Penggunaan |
|---|---|---|---|
| Bentuk huruf Latin (A–Z, 0–9) sebagai path SVG | [`tscircuit/alphabet`](https://github.com/tscircuit/alphabet) — juga di npm sebagai `@tscircuit/alphabet` | MIT | Library TypeScript yang menyediakan **SVG path data, line segments, dan glyph outline** untuk huruf & angka, dinormalisasi ke kotak 1x1 unit. Cocok untuk: (a) referensi bentuk huruf saat desainer membuat versi bergaya "anak-anak" di Figma/Illustrator, atau (b) di-generate langsung jadi `VectorDrawable` Android via script konversi jika gaya font default sudah dianggap cukup menarik tanpa perlu didesain ulang. **Perlu evaluasi visual dulu** — gaya defaultnya dibuat untuk kebutuhan elektronik/PCB (tscircuit), bukan didesain khusus untuk anak, jadi kemungkinan tetap perlu di-restyle (ketebalan stroke, sudut membulat/rounded) agar terasa ramah anak. |
| Bentuk huruf Hijaiyah (ا-ي) | **Tidak disarankan cari SVG per-huruf statis.** Gunakan font Arab open-source (lihat baris di bawah) dan render sebagai teks. | — | Huruf Hijaiyah punya bentuk berbeda tergantung posisi (awal/tengah/akhir/lepas) — kalau pakai SVG statis per huruf, harus bikin 4x lipat aset dan gampang salah bentuk. Lebih aman & akurat pakai font Arab yang sudah menangani *contextual shaping* otomatis oleh sistem Android. |
| Font Arab untuk huruf Hijaiyah | Google Fonts: **Noto Naskh Arabic**, **Amiri**, **Lateef**, atau **Scheherazade New** | OFL (SIL Open Font License) — gratis, boleh komersial | Tinggal bundling file `.ttf`/`.otf` ke folder `res/font/` Android, dipakai lewat `Typeface` API. Perlu uji keterbacaan untuk anak (disarankan pilih yang goresan jelas, bukan gaya kaligrafi rumit). |
| Ikon UI (tombol kembali, speaker/volume, bintang, checklist, dsb.) | [`google/material-design-icons`](https://github.com/google/material-design-icons) (Material Symbols) | Apache-2.0 | Sangat lengkap, resmi dari Google, bentuk geometris tanpa wajah — aman dari sisi Bab 3. Tersedia format SVG siap convert ke `VectorDrawable`. |
| Alternatif ikon (gaya lebih playful/rounded) | [`phosphor-icons/core`](https://github.com/phosphor-icons/core) atau [`tabler/tabler-icons`](https://github.com/tabler/tabler-icons) atau [`lucide-icons/lucide`](https://github.com/lucide-icons/lucide) | MIT / ISC | Semua geometris, tanpa elemen wajah, cocok untuk ikon pendukung (mis. ikon "ulangi", "lanjut", "bintang", "kembali ke menu"). |
| Ilustrasi karakter/figur faceless | **Tidak ada library siap-pakai yang otomatis sesuai** — mayoritas illustration kit (Open Peeps, unDraw, Humaaans, dll.) menggambar wajah manusia. | — | Ini area yang **tetap butuh desain custom** oleh ilustrator internal/freelance sesuai brief Bab 3, bukan sourcing dari library umum. Boleh pakai illustration kit di atas sebagai referensi pose/proporsi tubuh saja, lalu wajah dihilangkan/ditutup saat proses desain ulang. |
| Efek suara non-musikal (transisi, klik) | [freesound.org](https://freesound.org) (filter lisensi CC0) | CC0 (per file, wajib dicek satu-satu) | Untuk efek suara sederhana seperti "ting"/"klik" — tetap wajib direview agar tidak menyerupai nada musik/instrumen. |

**Alur kerja yang wajib diikuti tim untuk setiap kebutuhan aset baru:**
1. Cek dulu apakah kebutuhan aset generik (huruf, angka, ikon UI umum) → cari di daftar/library di atas atau riset serupa di GitHub/npm dengan keyword yang relevan.
2. Jika ditemukan, **cek lisensi dengan teliti** (harus MIT/Apache-2.0/OFL/CC0 — hindari lisensi "free for personal use" atau CC-BY-NC yang tidak boleh dipakai untuk produk komersial).
3. Jika aset butuh konten Islami spesifik (Hijaiyah, ilustrasi doa, dsb.) atau butuh kepatuhan Bab 3 (faceless, dsb.) yang tidak tersedia di library umum → desain custom internal, tidak sourcing dari luar.
4. Semua aset yang di-source dari luar dicatat di **daftar atribusi/lisensi** (`THIRD_PARTY_LICENSES.md` di root proyek) untuk keperluan audit hukum sebelum rilis ke Play Store.

### 7.3 Struktur Data (Contoh Model Kotlin)
```kotlin
data class FloatingLetter(
    val id: Long,
    val letter: String,      // A–J
    val xFraction: Float,
    val topY: Float,
    val speed: Float
)

data class GameSession(
    val id: String,
    val durationSeconds: Int = 60,
    var targetLetter: String,
    var score: Int = 0,
    var correctTaps: Int = 0,
    var totalTaps: Int = 0
)

data class AttemptResult(
    val sessionId: String,
    val targetLetter: String,
    val tappedLetter: String,
    val isCorrect: Boolean,
    val pointsAwarded: Int, // 10 atau 0
    val timestamp: Long
)
```

### 7.4 Non-Functional Requirements
- **Tanpa iklan pihak ketiga** yang tidak sesuai konten (perlu kebijakan monetisasi terpisah — lihat Bab 9).
- **Tanpa auto-play video/musik eksternal.**
- **Kontrol orang tua (Parental Gate):** Sederhana di Fase 1 (mis. soal matematika ringan) sebelum masuk ke pengaturan aplikasi, agar anak tidak sengaja mengubah setting.
- **Aksesibilitas suara jelas:** Audio direkam dengan kualitas studio minimum 44.1kHz, noise-free, artikulasi jelas untuk anak usia dini.
- **Performa:** Waktu load antar soal < 300ms (aset lokal, bukan network call).

---

## 8. Metrik Keberhasilan (Fase 1)

| Metrik | Target Awal |
|---|---|
| Completion rate sesi Tebak Huruf (anak menyelesaikan 1 sesi penuh) | ≥ 70% |
| Rata-rata jumlah sesi per pengguna per minggu | ≥ 3x |
| Crash-free session rate | ≥ 99% |
| Rating Play Store awal | ≥ 4.5 |
| Retensi 7 hari | ≥ 30% |

---

## 9. Di Luar Cakupan Fase 1 (Out of Scope)

- Seluruh modul "Belajar" selain yang terkait langsung (huruf/hijaiyah).
- Mode Peta (world map) — Fase 1 pakai Mode Basic (grid menu) saja.
- Sistem multi-profil anak.
- Fitur sosial/leaderboard online.
- Monetisasi (iklan/in-app purchase) — model bisnis dibahas terpisah di dokumen strategi monetisasi.
- Modul musik/lagu apa pun (permanen di luar cakupan seluruh produk, bukan hanya Fase 1, sesuai Bab 3).

---

## 10. Roadmap Ringkas Pasca-Fase 1

1. **Fase 2:** Modul "Bermain" tambahan — Cocokkan Huruf, Tebak Bacaan, Tebak Angka, Tebak Buah & Sayur.
2. **Fase 3:** Modul "Belajar" inti — Mengaji/Hijaiyah lengkap, Doa Anak Muslim, Bahasa Arab.
3. **Fase 4:** Mode Peta (world map), multi-profil anak, laporan progress untuk orang tua.
4. **Fase 5:** Modul kreatif (mewarnai, menggambar) dan modul musiman (Ramadan) dengan review konten syar'i penuh.

---

## 11. Catatan Penutup untuk Tim Development

- Setiap aset audio dan visual **wajib melalui checklist compliance** (Bab 3) sebelum dimasukkan ke build — disarankan dibuat sebagai *content review checklist* terpisah.
- Karena aplikasi ini menyasar ceruk pasar spesifik (keluarga Salafy), diferensiasi utama bukan pada jumlah fitur, tapi pada **kepercayaan orang tua terhadap kepatuhan konten** — kualitas kontrol konten lebih penting daripada kecepatan menambah fitur.
