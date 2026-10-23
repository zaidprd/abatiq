# Memetakan kalimat ke detik di tiap blok suara: deteksi jeda dengan ffmpeg, lalu kelompokkan potongan
# bicara ke kalimat dengan pemrograman dinamis (panjang kelompok ~ jumlah suku kata).
import subprocess, re, json, os, sys
os.chdir(os.path.join(os.path.dirname(os.path.abspath(__file__)), "final"))

S = {
 "blok1_005.0_pembuka": ["Halo anak-anak soleh dan soleha.", "Kembali lagi di channel Abatiku.", "Hari ini kita belajar huruf a, yuk.", "Tok tok...", "eh, ada apa ya di dalam keranjang?", "Wah, keranjangnya digembok.", "Gemboknya bergambar huruf a."],
 "blok2_015.5_bunyi": ["Nah, ini huruf A besar.", "Dan ini huruf a kecil.", "Yuk, kita buka gemboknya dengan bunyinya.", "Bunyinya...", "aaa.", "Ayo ikuti,", "aaa.", "Pintar sekali.", "Aaa. Yeay, satu kunci."],
 "blok3_041.5_buah": ["A... apel.", "A... anggur.", "A... alpukat.", "Yeay, dua kunci.", "Yuk, kita hitung apelnya.", "Satu... dua... tiga.", "Ada tiga apel."],
 "blok4_087.0_menulis": ["Gemboknya minta kata sandi, yaitu huruf a.", "Yuk, kita tulis bersama.", "Satu: putar membentuk lingkaran.", "Dua: tarik garis lurus ke bawah.", "Sekarang kita ulangi, ya.", "Tulis di udara dengan jari kecilmu.", "Pintar sekali.", "Yeay, tiga kunci."],
 "blok5_139.0_tebak": ["Mana huruf a, ya?", "Yuk, kita tebak.", "Betul, ini huruf a.", "Yeay, empat kunci.", "Sekali lagi, ya.", "Mana huruf a?", "Betul, pintar sekali.", "Yeay, lima kunci."],
 "blok6_166.5_penutup": ["Wah, kita sudah punya semua kunci.", "Yuk, kita buka gemboknya.", "Tadaa.", "Ada apel, anggur, dan alpukat.", "A untuk apel, anggur, alpukat.", "Hebat, anak-anak soleh dan soleha.", "Eh, ada keranjang lagi.", "Tok tok...", "isinya apa ya?", "Kita buka nanti, ya."],
}


def syl(t):
    return max(1, len(re.findall(r"[aeiou]", t.lower())))


def pieces(f, d=0.22, db=-38):
    out = subprocess.run(["ffmpeg", "-hide_banner", "-nostats", "-i", f + ".mp3", "-af", "silencedetect=noise=%ddB:d=%s" % (db, d), "-f", "null", "-"], capture_output=True, text=True).stderr
    dur = float(subprocess.run(["ffprobe", "-v", "error", "-show_entries", "format=duration", "-of", "csv=p=0", f + ".mp3"], capture_output=True, text=True).stdout)
    st = [float(x) for x in re.findall(r"silence_start: ([0-9.]+)", out)]
    en = [float(x) for x in re.findall(r"silence_end: ([0-9.]+)", out)]
    segs, t = [], 0.0
    for a, b in zip(st, en + [None] * 5):
        if a - t > 0.12:
            segs.append((t, a))
        t = b if b is not None else dur
    if dur - t > 0.12 and (len(en) >= len(st)):
        segs.append((t, dur))
    return segs, dur


def group(segs, sents):
    n, m = len(segs), len(sents)
    if n < m:
        return None
    total = sum(b - a for a, b in segs)
    k = total / sum(syl(s) for s in sents)
    INF = 1e9
    best = [[INF] * (m + 1) for _ in range(n + 1)]
    back = [[0] * (m + 1) for _ in range(n + 1)]
    best[0][0] = 0
    for i in range(1, n + 1):
        for j in range(1, min(i, m) + 1):
            for p in range(j - 1, i):
                a, b = segs[p][0], segs[i - 1][1]
                exp = k * syl(sents[j - 1])
                c = best[p][j - 1] + abs((b - a) - exp) / (exp + 0.3)
                if c < best[i][j]:
                    best[i][j], back[i][j] = c, p
    res, i = [], n
    for j in range(m, 0, -1):
        p = back[i][j]
        res.append((round(segs[p][0], 2), round(segs[i - 1][1], 2)))
        i = p
    return res[::-1], best[n][m]


out = {}
for f, sents in S.items():
    segs, dur = pieces(f)
    g = group(segs, sents)
    print("==", f, "durasi %.2f" % dur, "potongan", len(segs), "kalimat", len(sents))
    if g is None:
        print("  potongan kurang dari kalimat:", [(round(a, 2), round(b, 2)) for a, b in segs])
        continue
    for (a, b), s in zip(g[0], sents):
        print("  %6.2f - %6.2f  %s" % (a, b, s))
    out[f] = {"durasi": dur, "kalimat": [{"teks": s, "mulai": a, "selesai": b} for (a, b), s in zip(g[0], sents)]}
json.dump(out, open("peta_kalimat.json", "w"), indent=1, ensure_ascii=False)
