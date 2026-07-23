# AbatiQ — Content Review Checklist (Phase 1)

This checklist operationalises the **content guardrails** in PRD Bab 3 and Bab
6.4. It is split into four sections so an internal reviewer (product,
syar'i, design, accessibility) can sign off one area at a time. Every row is
either **passing** today, **pending review**, or **explicitly out of scope
for Phase 1**. Reviewers should fill the **Status** column after their audit.

---

## 1. Audio (syar'i review + sound design)

| # | Item                                                                                                          | Status |
|---|---------------------------------------------------------------------------------------------------------------|--------|
| 1 | No music, jingles, musical instruments, or autoplay media anywhere in the app.                                | ✅ Passing — only TTS speech prompts, no media files. |
| 2 | Setiap prompt **positif dan konstruktif** — tidak pernah mengatakan "kamu salah". Semua naskah terpusat di `PromptCatalog.kt`. | ✅ Passing — target, feedback lembut, pujian, dan penutup tersentralisasi. |
| 3 | Islamic phrases (MasyaAllah, Barakallah, Alhamdulillah) are used sparingly and in context.                     | ✅ Passing — two phrases only, both on positive feedback / completion. |
| 4 | Audio target huruf memakai rekaman WAV yang dibundel (`alphabet_a.wav` ... `alphabet_z.wav`). Feedback sementara memakai TTS sampai rekaman feedback tersedia. | ✅ Passing — `RecordedLetterAudioPlayer.kt`. |
| 5 | Rekaman target A–Z berasal dari folder `alphabet/` dan perlu dikonfirmasi pemilik sebagai aset milik proyek sebelum rilis. | ⏳ Pending — konfirmasi sumber/hak penggunaan rekaman. |
| 6 | Feedback dan penutup yang belum memiliki file rekaman memakai TTS Indonesia sebagai fallback sementara. | ✅ Passing — target huruf tetap memakai rekaman WAV. |
| 7 | TTS fallback memakai Bahasa Indonesia (`Locale("id", "ID")`); bila engine tidak mendukungnya, game tetap dapat dimainkan tanpa crash. | ✅ Passing — `RecordedLetterAudioPlayer` memeriksa status engine. |
| 8 | Volume slider in Settings is persisted in DataStore and applied to the audio engine live.                     | ✅ Passing — `PreferencesRepository` + `AbatiQApplication.onCreate` mirror value. |

---

## 2. Visual identity (no faces, no copyrighted characters)

| # | Item                                                                                                  | Status |
|---|-------------------------------------------------------------------------------------------------------|--------|
| 1 | No faces, characters, mascots, or living creatures appear anywhere in the UI.                          | ✅ Passing — decoration is geometric (`Canvas` arches, leaves, dots, eight-pointed stars) only. |
| 2 | No copyrighted characters (commercial or fan-art) are referenced or implied.                          | ✅ Passing. |
| 3 | All icons are either Compose Material icons (Apache-2.0, geometric) or hand-built vector primitives (this repo). | ✅ Passing. |
| 4 | No raster images, photographs, or external image services are bundled or hotlinked.                    | ✅ Passing — `grep` against `app/src/main/` finds zero image URLs and zero `BitmapFactory` usage. |
| 5 | Backgrounds use a warm "sunlit learning garden" palette (cream / forest / terracotta / honey / mint).  | ✅ Passing — palette centralised in `ui/theme/Color.kt`. |
| 6 | Animasi hanya mendukung gameplay dan feedback; tidak ada flashing atau celebration reel. | ✅ Passing — balon bergerak lambat ke atas dan feedback tap berupa border/scale singkat. |
| 7 | Preferensi reduced-motion dihormati sejauh praktis. | ⏳ Perlu pengujian perangkat — kecepatan balon saat ini dibuat lambat dan tidak berkedip. |
| 8 | Launcher icon is faceless: arch + honey star.                                                          | ✅ Passing — `mipmap-anydpi-v26/ic_launcher.xml`. |

---

## 3. Copywriting (gentle, accurate, age-appropriate)

| # | Item                                                                                                          | Status |
|---|---------------------------------------------------------------------------------------------------------------|--------|
| 1 | Indonesian copy is grammatically natural and uses vocabulary children 3–6 will hear from their parents.        | ✅ Passing — short