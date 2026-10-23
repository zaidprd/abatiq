# Potong tiap kalimat, percepat (atempo, nada tetap), simpan ke final/klip2 dan tulis jadwal penempatan.
# Awal tiap kalimat tetap di detik video yang sama seperti jadwal sebelumnya.
import subprocess, json, os, sys
SPEED = float(sys.argv[1]) if len(sys.argv) > 1 else 1.15
base = os.path.join(os.path.dirname(os.path.abspath(__file__)), "final")
os.chdir(base)
os.makedirs("klip2", exist_ok=True)
peta = json.load(open("peta_kalimat.json", encoding="utf-8"))
# klip lama: (blok, a, t) -> awal klip di blok, detik video untuk a
KLIP = [
 ("blok1_005.0_pembuka", 0, 5.0), ("blok2_015.5_bunyi", 0, 28.3), ("blok2_015.5_bunyi", 13.56, 44.86),
 ("blok3_041.5_buah", 0, 55.5), ("blok3_041.5_buah", 2.09, 67.4), ("blok3_041.5_buah", 4.28, 79.4), ("blok3_041.5_buah", 6.20, 88.6),
 ("blok3_041.5_buah", 8.12, 90.7), ("blok3_041.5_buah", 10.6, 93.2), ("blok3_041.5_buah", 12.99, 96.2),
 ("blok4_087.0_menulis", 0, 103.0), ("blok4_087.0_menulis", 10.66, 117.3), ("blok4_087.0_menulis", 14.24, 125.3), ("blok4_087.0_menulis", 20.15, 137.3),
 ("blok5_139.0_tebak", 0, 145.1), ("blok5_139.0_tebak", 4.14, 153.9), ("blok5_139.0_tebak", 9.06, 162.1), ("blok5_139.0_tebak", 12.34, 171.1),
 ("blok6_166.5_penutup", 0, 178.5),
]
B5 = [(0, 1.68, "Mana huruf a, ya?"), (2.18, 3.4, "Yuk, kita tebak."), (4.24, 6.53, "Betul, ini huruf a."), (6.87, 8.73, "Yeay, empat kunci."), (9.16, 11.7, "Sekali lagi, ya. Mana huruf a?"), (12.44, 14.43, "Betul, pintar sekali."), (14.69, 16.5, "Yeay, lima kunci.")]
sents = {k: [(x["mulai"], x["selesai"], x["teks"]) for x in v["kalimat"]] for k, v in peta.items()}
sents["blok5_139.0_tebak"] = B5


def dur(f):
    return float(subprocess.run(["ffprobe", "-v", "error", "-show_entries", "format=duration", "-of", "csv=p=0", f], capture_output=True, text=True).stdout)


jadwal = []
i = 0
for blok, lst in sents.items():
    D = dur(blok + ".mp3")
    for s, e, teks in lst:
        # klip lama yang memuat kalimat ini: klip terakhir dengan a <= s
        cand = [(a, t) for b, a, t in KLIP if b == blok and a <= s + 0.01]
        a, t = max(cand)
        new_start = t + (s - a)                       # detik video tempat kalimat dimulai
        cs, ce = max(0, s - 0.06), min(D, e + 0.12)
        i += 1
        n = "%02d_%s.mp3" % (i, blok[:5])
        d = ce - cs
        dn = d / SPEED
        af = "atempo=%s,afade=t=in:st=0:d=0.03,afade=t=out:st=%.3f:d=0.05" % (SPEED, max(0, dn - 0.05))
        subprocess.run(["ffmpeg", "-y", "-hide_banner", "-loglevel", "error", "-ss", "%.3f" % cs, "-t", "%.3f" % d, "-i", blok + ".mp3", "-af", af, "-c:a", "libmp3lame", "-q:a", "2", "klip2/" + n], check=True)
        jadwal.append({"berkas": n, "teks": teks, "mulai_video": round(new_start - (s - cs), 3), "kalimat_mulai": round(new_start, 3), "kalimat_selesai": round(new_start + (e - s) / SPEED, 3), "durasi": round(dn, 3)})
json.dump(jadwal, open("klip2/jadwal.json", "w"), indent=1, ensure_ascii=False)
for j in jadwal:
    print(j["berkas"], j["mulai_video"], j["kalimat_mulai"], "-", j["kalimat_selesai"], j["teks"])
