# AbatiQ — Fase 1: Tebak Huruf

Aplikasi Android native, offline-first, untuk membantu anak Muslim usia 3–6 tahun mengenali huruf kapital Latin A–J melalui permainan balon berbasis audio.

Spesifikasi utama ada di [`PRD_Aplikasi_Belajar_Anak_Muslim.md`](PRD_Aplikasi_Belajar_Anak_Muslim.md). Bab Fase 1 telah diperbarui mengikuti keputusan pemilik produk terbaru.

## Gameplay Fase 1

- Satu sesi berlangsung **2 menit (120 detik)**, bukan berdasarkan jumlah soal.
- Tepat **tiga** kartu huruf A–J besar berjajar dalam bingkai; kartu muncul bergilir cepat sejak ronde dimulai.
- Aplikasi otomatis mengunci orientasi ke **landscape** selama permainan; Beranda/Hasil tetap mendukung portrait.
- Instruksi disampaikan melalui suara; layar permainan **tidak menampilkan teks pertanyaan**.
- Tombol speaker dapat mengulang target saat ini.
- Tap huruf benar memberi **+10 poin** dan kartu bereaksi dengan *zoom pulse* singkat.
- Tap huruf belum tepat memberi **0 poin**, kartu bergetar pelan ke kiri-kanan; tidak ada pengurangan skor.
- Benar maupun belum tepat mengunci input sejenak, lalu ronde baru muncul dengan tiga huruf acak baru.
- Hasil sesi dan setiap tap disimpan secara lokal.

## Tampilan

Arena menggunakan latar krem yang lapang dengan ornamen geometris/botani di tepi, bukit lembut, lengkung, titik, dan bintang. Seluruh balon serta ornamen digambar dengan Jetpack Compose Canvas/vector primitives:

- tidak memerlukan SVG eksternal untuk MVP;
- tidak menggunakan gambar raster atau layanan gambar eksternal;
- tidak memiliki wajah, maskot, manusia, atau hewan;
- tidak menggunakan musik, jingle, atau instrumen.

## Stack dan arsitektur

- Kotlin 100%
- Jetpack Compose + Material 3
- MVVM + Repository
- Room untuk sesi dan attempt
- DataStore untuk volume suara
- Android Text-to-Speech Bahasa Indonesia sebagai fallback untuk feedback sementara
- Rekaman WAV huruf A–Z milik proyek di `app/src/main/res/raw/alphabet_a.wav` sampai `alphabet_z.wav`
- `minSdk 24`, `compileSdk 35`, `targetSdk 34`
- Tidak ada permission `INTERNET`

Struktur utama:

```text
app/src/main/java/com/abatiq/app/
├── audio/                 # GameAudioPlayer, prompt, implementasi TTS
├── data/domain/           # GameSession dan LetterBatchFactory (3 huruf/round)
├── data/local/            # Room dan DataStore
├── data/repository/       # akses data melalui repository
└── ui/                    # home, game, result, settings, theme
```

## Audio huruf A–Z

Folder sumber `alphabet/` berisi 26 rekaman WAV terpisah. Semuanya sudah dibundel ke Android resources:

```text
app/src/main/res/raw/
├── alphabet_a.wav ... alphabet_z.wav
```

`RecordedLetterAudioPlayer` memetakan huruf target langsung ke file tersebut. Jadi saat audio target A, aplikasi memutar `alphabet_a.wav`, bukan TTS. TTS hanya digunakan sementara untuk kalimat feedback gabungan, tes suara, dan penutup sesi yang belum memiliki file rekaman terpisah.

Jika nanti Anda menyiapkan rekaman feedback, tambahkan resource baru di `res/raw/` dan ubah pemetaan di `RecordedLetterAudioPlayer.kt`; alur game tidak perlu diubah.


## Build dan test

Prasyarat:

- Android Studio dengan JDK bawaan
- Android SDK platform 35 atau lebih baru
- Perangkat Android API 24+ atau emulator

Windows/Git Bash:

```bash
JAVA_HOME='C:/Program Files/Android/Android Studio/jbr' \
ANDROID_HOME='C:/Users/ZAID/AppData/Local/Android/Sdk' \
./gradlew :app:testDebugUnitTest :app:assembleDebug
```

APK debug dihasilkan di:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Menjalankan melalui kabel USB

1. Aktifkan **Developer options** dan **USB debugging** di HP.
2. Hubungkan kabel dalam mode transfer data.
3. Terima dialog sidik jari RSA pada HP jika muncul.
4. Pastikan perangkat terlihat:

```bash
'C:/Users/ZAID/AppData/Local/Android/Sdk/platform-tools/adb.exe' devices -l
```

5. Install:

```bash
'C:/Users/ZAID/AppData/Local/Android/Sdk/platform-tools/adb.exe' install -r app/build/outputs/apk/debug/app-debug.apk
```

Atau buka proyek di Android Studio, pilih perangkat fisik pada device selector, lalu tekan **Run**.

## Test yang tersedia

- durasi sesi tepat 120 detik;
- jawaban benar memberi +10;
- jawaban belum tepat tidak mengurangi skor;
- setiap batch berisi tepat tiga huruf A–J dengan target yang selalu muncul;
- reaksi benar/salah dikunci sebelum ronde berikutnya muncul;
- parental gate memvalidasi penjumlahan.

## Kontrol konten

- [`CONTENT_REVIEW_CHECKLIST.md`](CONTENT_REVIEW_CHECKLIST.md)
- [`THIRD_PARTY_LICENSES.md`](THIRD_PARTY_LICENSES.md)

Semua rekaman suara final wajib melalui review konten sebelum rilis publik.
