# Objek huruf a gaya datar tebal (prinsip desain app belajar): warna jenuh tanpa gradasi, bentuk bulat tebal, tepi bawah lebih gelap.
import json, subprocess, sys, os
CLI = r"C:\Program Files\VectorCraft\vectorcraft-cli.exe"
OUT = os.path.dirname(os.path.abspath(__file__)).replace("\\", "/") + "/"
BROWN, SHADOW = "#4B3425", "#2d3a1a"


def f(v):
    return "%.1f" % v


def smooth(pts, t=1 / 6.0):
    n = len(pts)
    d = "M%s %s " % (f(pts[0][0]), f(pts[0][1]))
    for i in range(n):
        p0, p1, p2, p3 = pts[(i - 1) % n], pts[i], pts[(i + 1) % n], pts[(i + 2) % n]
        c1 = (p1[0] + (p2[0] - p0[0]) * t, p1[1] + (p2[1] - p0[1]) * t)
        c2 = (p2[0] - (p3[0] - p1[0]) * t, p2[1] - (p3[1] - p1[1]) * t)
        d += "C%s %s %s %s %s %s " % (f(c1[0]), f(c1[1]), f(c2[0]), f(c2[1]), f(p2[0]), f(p2[1]))
    return d + "Z"


def fp(c):
    return {"none": True} if c is None else {"color": c}


def build(name, drawer, w=600, h=600):
    steps = [("file.new", {"width": w, "height": h, "units": "Pixels", "backgroundContents": "transparent", "name": name})]

    def path(d, fill, nm="", stroke=None, sw=0, op=None):
        steps.append(("path.create", {"d": d}))
        steps.append(("paint.setFill", fp(fill)))
        if stroke:
            steps.append(("paint.setStroke", {"color": stroke}))
            steps.append(("stroke.set", {"weight": sw, "join": "round", "cap": "round"}))
        else:
            steps.append(("paint.setStroke", {"none": True}))
        p = {"name": nm or "bentuk"}
        if op is not None:
            p["opacity"] = op
        steps.append(("object.setProps", p))

    def ell(cx, cy, rx, ry, fill, nm="", rot=0, op=None):
        steps.append(("shape.ellipse", {"x": cx - rx, "y": cy - ry, "width": 2 * rx, "height": 2 * ry, "rotation": rot}))
        steps.append(("paint.setFill", fp(fill)))
        steps.append(("paint.setStroke", {"none": True}))
        p = {"name": nm or "oval"}
        if op is not None:
            p["opacity"] = op
        steps.append(("object.setProps", p))

    def star(cx, cy, r1, r2, fill, nm="bintang"):
        steps.append(("shape.star", {"cx": cx, "cy": cy, "radius1": r1, "radius2": r2, "points": 5, "rotation": 0}))
        steps.append(("paint.setFill", fp(fill)))
        steps.append(("paint.setStroke", {"color": fill}))
        steps.append(("stroke.set", {"weight": 22, "join": "round"}))
        steps.append(("object.setProps", {"name": nm}))

    def text(x, y, s, size, color):
        steps.append(("text.create", {"x": x, "y": y, "text": s, "size": size, "color": color, "font": "Arial", "style": "Bold"}))

    drawer(path, ell, star, text)
    args = [CLI, "run"]
    for c, p in steps:
        args += ["--cmd", c, "--params", json.dumps(p)]
    for e in ("png", "svg"):
        args += ["--export", OUT + name + "." + e]
    r = subprocess.run(args + ["--scale", "1"], capture_output=True, text=True)
    bad = [l for l in r.stdout.splitlines() if "error" in l.lower()][:2]
    print(name, "langkah", len(steps), "error", len(bad), [b[:140] for b in bad], r.stderr[:100])


def apel(color, dark, leaf="#58CC02", leaf_d="#46A302"):
    def d(path, ell, star, text):
        ell(300, 560, 180, 22, SHADOW, "Bayangan", op=18)
        body = [(300, 175), (400, 150), (485, 215), (505, 320), (470, 435), (390, 520), (300, 505), (210, 520), (130, 435), (95, 320), (115, 215), (200, 150)]
        path(smooth(body), dark, "Badan_bawah")
        path(smooth([(x, y - 15) for x, y in body]), color, "Badan")
        ell(205, 262, 26, 58, "#FFFFFF", "Kilau", rot=18, op=45)
        path(smooth([(292, 168), (298, 118), (320, 72), (340, 76), (320, 126), (318, 170)], 0.12), BROWN, "Tangkai")
        path(smooth([(322, 120), (385, 66), (455, 78), (476, 116), (412, 152), (350, 140)]), leaf_d, "Daun_bawah")
        path(smooth([(322, 112), (385, 58), (455, 70), (476, 108), (412, 144), (350, 132)]), leaf, "Daun")
    return d


def anggur(path, ell, star, text):
    ell(300, 568, 170, 20, SHADOW, "Bayangan", op=16)
    path(smooth([(300, 70), (330, 40), (380, 36), (384, 52), (340, 72), (312, 112)], 0.12), BROWN, "Tangkai")
    path(smooth([(332, 66), (402, 24), (472, 40), (488, 82), (422, 96), (352, 92)]), "#46A302", "Daun_bawah")
    path(smooth([(332, 58), (402, 16), (472, 32), (488, 74), (422, 88), (352, 84)]), "#58CC02", "Daun")
    pts = [(300, 175), (390, 175), (210, 175), (345, 255), (255, 255), (435, 255), (165, 255), (300, 335), (390, 335), (210, 335), (345, 415), (255, 415), (300, 495)]
    for i, (x, y) in enumerate(pts):
        r = 52 - (y - 175) * 0.03
        ell(x, y + 10, r, r, "#7A3FD1", "Buah_bawah_%d" % i)
    for i, (x, y) in enumerate(pts):
        r = 52 - (y - 175) * 0.03
        ell(x, y, r, r, "#A56EFF", "Buah_%d" % i)
        ell(x - r * 0.34, y - r * 0.36, r * 0.2, r * 0.28, "#FFFFFF", "Kilau_%d" % i, rot=25, op=50)


def keranjang(path, ell, star, text):
    ell(300, 552, 230, 22, SHADOW, "Bayangan", op=16)
    path(smooth([(60, 260), (540, 260), (500, 486), (430, 536), (170, 536), (100, 486)], 0.12), "#B8741F", "Badan_bawah")
    path(smooth([(60, 250), (540, 250), (500, 474), (430, 524), (170, 524), (100, 474)], 0.12), "#F2A83B", "Badan")
    for i in range(1, 6):
        path("M%s 262 L%s 514" % (f(60 + i * 80), f(72 + i * 71)), None, "Anyaman_%d" % i, "#B8741F", 8)
    for j in (340, 420):
        path("M85 %d L515 %d" % (j, j), None, "Anyaman_h%d" % j, "#B8741F", 8)
    path(smooth([(40, 236), (560, 236), (560, 276), (40, 276)], 0.1), "#8E5714", "Bibir_bawah")
    path(smooth([(40, 226), (560, 226), (560, 266), (40, 266)], 0.1), "#D9922E", "Bibir")
    path("M110 232 C110 56 490 56 490 232", None, "Gagang", "#8E5714", 26)


def bintang(on):
    def d(path, ell, star, text):
        star(300, 312, 190, 98, "#E5B000" if on else "#C9CED6", "Bintang_bawah")
        star(300, 296, 190, 98, "#FFC93C" if on else "#E5E8EE", "Bintang")
        if on:
            ell(235, 215, 26, 14, "#FFFFFF", "Kilau", rot=-35, op=60)
    return d


def nomor(n, color, dark):
    def d(path, ell, star, text):
        ell(300, 322, 220, 220, dark, "Lencana_bawah")
        ell(300, 300, 220, 220, color, "Lencana")
        text(232 if n == 1 else 250, 395, str(n), 280, "#FFFFFF")
    return d


def alpukat(path, ell, star, text):
    ell(300, 568, 170, 20, SHADOW, "Bayangan", op=16)
    body = [(300, 60), (370, 130), (430, 250), (470, 370), (430, 480), (300, 530), (170, 480), (130, 370), (170, 250), (230, 130)]
    path(smooth(body), "#2F7D1B", "Kulit_bawah")
    path(smooth([(x, y - 14) for x, y in body]), "#46A302", "Kulit")
    inner = [(300, 104), (350, 164), (405, 262), (435, 355), (405, 437), (300, 472), (195, 437), (165, 355), (195, 262), (250, 164)]
    path(smooth([(x, y + 6) for x, y in inner]), "#F4F0A0", "Daging_bawah")
    path(smooth(inner), "#CDEB6E", "Daging")
    ell(300, 372, 78, 84, "#8E5714", "Biji_bawah")
    ell(300, 360, 78, 84, "#B8741F", "Biji")
    ell(272, 332, 18, 26, "#FFFFFF", "Kilau_biji", rot=20, op=45)


JOBS = {
    "apel_merah": lambda: build("apel_merah_datar", apel("#FF4B4B", "#CC2F2F")),
    "apel_hijau": lambda: build("apel_hijau_datar", apel("#9BE23A", "#6DB31C")),
    "anggur": lambda: build("anggur_datar", anggur),
    "keranjang": lambda: build("keranjang_datar", keranjang),
    "bintang_aktif": lambda: build("bintang_aktif", bintang(True)),
    "bintang_kosong": lambda: build("bintang_kosong", bintang(False)),
    "nomor_1": lambda: build("nomor_1", nomor(1, "#FF4B4B", "#CC2F2F")),
    "nomor_2": lambda: build("nomor_2", nomor(2, "#1CB0F6", "#1387BB")),
    "nomor_3": lambda: build("nomor_3", nomor(3, "#58CC02", "#46A302")),
    "alpukat": lambda: build("alpukat_datar", alpukat),
}
for k in (sys.argv[1:] or list(JOBS)):
    JOBS[k]()
