# Aset objek huruf a: apel, anggur, keranjang, siluet ayam (faceless). Dibuat lewat VectorCraft CLI.
import json, math, subprocess, sys, os
CLI = r"C:\Program Files\VectorCraft\vectorcraft-cli.exe"; OUT = os.path.dirname(os.path.abspath(__file__)).replace("\\", "/") + "/"
def f(v): return "%.1f" % v
def smooth(pts, closed=True, t=1/6.0):
    n = len(pts); d = "M%s %s " % (f(pts[0][0]), f(pts[0][1]))
    for i in range(n if closed else n-1):
        p0, p1, p2, p3 = pts[(i-1) % n], pts[i], pts[(i+1) % n], pts[(i+2) % n]
        if not closed: p0 = pts[max(i-1, 0)]; p3 = pts[min(i+2, n-1)]
        c1 = (p1[0]+(p2[0]-p0[0])*t, p1[1]+(p2[1]-p0[1])*t); c2 = (p2[0]-(p3[0]-p1[0])*t, p2[1]-(p3[1]-p1[1])*t)
        d += "C%s %s %s %s %s %s " % (f(c1[0]), f(c1[1]), f(c2[0]), f(c2[1]), f(p2[0]), f(p2[1]))
    return d + ("Z" if closed else "")
def rad(c1, c2, a=None): return {"kind": "radial", "stops": [{"offset": 0, "color": c1}, {"offset": 1, "color": c2}]}
def lin(c1, c2, ang=90): return {"kind": "linear", "angle": ang, "stops": [{"offset": 0, "color": c1}, {"offset": 1, "color": c2}]}
def build(name, drawer, w=600, h=600):
    steps = [("file.new", {"width": w, "height": h, "units": "Pixels", "backgroundContents": "transparent", "name": name})]
    def path(d, fill, stroke=None, sw=0, op=None, nm=""):
        steps.append(("path.create", {"d": d})); steps.append(("paint.setFill", {"none": True} if fill is None else ({"gradient": fill} if isinstance(fill, dict) else {"color": fill})))
        if stroke: steps.append(("paint.setStroke", {"color": stroke})); steps.append(("stroke.set", {"weight": sw, "join": "round", "cap": "round"}))
        else: steps.append(("paint.setStroke", {"none": True}))
        p = {"name": nm or "bentuk"}
        if op is not None: p["opacity"] = op
        steps.append(("object.setProps", p))
    def ell(cx, cy, rx, ry, fill, op=None, rot=0, nm="", stroke=None, sw=0):
        steps.append(("shape.ellipse", {"x": cx-rx, "y": cy-ry, "width": 2*rx, "height": 2*ry, "rotation": rot}))
        steps.append(("paint.setFill", {"gradient": fill} if isinstance(fill, dict) else {"color": fill}))
        if stroke: steps.append(("paint.setStroke", {"color": stroke})); steps.append(("stroke.set", {"weight": sw}))
        else: steps.append(("paint.setStroke", {"none": True}))
        p = {"name": nm or "oval"}
        if op is not None: p["opacity"] = op
        steps.append(("object.setProps", p))
    drawer(path, ell)
    args = [CLI, "run"]
    for c, p in steps: args += ["--cmd", c, "--params", json.dumps(p)]
    for e in ("png", "svg"): args += ["--export", OUT + name + "." + e]
    r = subprocess.run(args + ["--scale", "1"], capture_output=True, text=True)
    bad = [l for l in r.stdout.splitlines() if '"error"' in l]; print(name, "langkah", len(steps), "error", len(bad), r.stderr[:200])
def apel(path, ell, col=("#ff6b5e", "#c4221a", "#8e1410"), nm=""):
    ell(300, 548, 175, 28, "#3a2a1a", op=18, nm="Bayangan")
    body = [(300, 170), (400, 150), (480, 205), (500, 310), (470, 430), (390, 520), (300, 505), (210, 520), (130, 430), (100, 310), (120, 205), (200, 150)]
    path(smooth(body), rad(col[0], col[1]), nm="Badan"); ell(215, 260, 38, 70, "#ffffff", op=38, rot=18, nm="Kilau"); ell(380, 450, 80, 40, col[2], op=30, rot=-20, nm="Bayangan_badan")
    path(smooth([(292, 170), (300, 118), (322, 70), (338, 72), (318, 125), (316, 172)]), "#6b3f1d", nm="Tangkai")
    path(smooth([(325, 120), (385, 70), (450, 80), (470, 112), (410, 150), (352, 138)]), lin("#6fcf5a", "#2f8f3a", 45), nm="Daun")
def anggur(path, ell):
    ell(300, 560, 170, 22, "#2a1a3a", op=15, nm="Bayangan")
    path(smooth([(300, 70), (330, 40), (380, 36), (384, 50), (340, 70), (312, 110)], True, 0.12), "#6b4a2a", nm="Tangkai")
    path(smooth([(330, 60), (400, 20), (470, 38), (486, 80), (420, 92), (350, 90)]), lin("#79d160", "#2f8f3a", 45), nm="Daun")
    pts = [(300, 170), (390, 170), (210, 170), (345, 250), (255, 250), (435, 250), (165, 250), (300, 330), (390, 330), (210, 330), (345, 410), (255, 410), (300, 490)]
    for i, (x, y) in enumerate(pts):
        r = 52 - (y - 170) * 0.03
        ell(x, y, r, r, rad("#b565d6", "#6a1f8f"), nm="Buah_%d" % i)
        ell(x - r * 0.35, y - r * 0.38, r * 0.22, r * 0.3, "#ffffff", op=45, rot=25, nm="Kilau_%d" % i)
def keranjang(path, ell):
    ell(300, 548, 230, 24, "#3a2a1a", op=16, nm="Bayangan")
    path(smooth([(60, 250), (540, 250), (500, 480), (430, 530), (170, 530), (100, 480)], True, 0.12), lin("#e0b05c", "#a8742a", 90), "#7a4f1c", 6, nm="Badan")
    for i in range(1, 7): path("M%s 255 L%s 525" % (f(60 + i * 80), f(70 + i * 72)), None, "#8a5a22", 4, op=70, nm="Anyaman_%d" % i)
    for j in (330, 410): path("M80 %d L520 %d" % (j, j), None, "#8a5a22", 4, op=70, nm="Anyaman_h%d" % j)
    path(smooth([(40, 235), (560, 235), (560, 268), (40, 268)], True, 0.1), lin("#b8803a", "#7a4f1c", 90), "#6a4016", 5, nm="Bibir")
    path("M110 240 C110 60 490 60 490 240", None, "#7a4f1c", 20, nm="Gagang")
def ayam(path, ell):  # siluet datar menghadap kanan, tanpa mata/wajah
    S = "#2a2438"
    ell(300, 566, 190, 16, "#2a2438", op=14, nm="Bayangan")
    for (x, y, rx, ry, rot) in [(120, 250, 34, 120, -35), (92, 300, 30, 105, -62), (150, 215, 32, 112, -12)]:
        ell(x, y, rx, ry, S, rot=rot, nm="Bulu_ekor")
    ell(290, 330, 160, 130, S, rot=-8, nm="Badan")
    ell(392, 205, 62, 72, S, nm="Kepala")
    path(smooth([(280, 330), (350, 290), (400, 300), (385, 350), (330, 400)], True, 0.12), S, nm="Dada")
    for (x, y, r) in [(372, 128, 20), (398, 118, 22), (424, 128, 18)]: ell(x, y, r, r * 1.2, S, nm="Jengger")
    path(smooth([(436, 200), (500, 222), (436, 244)], True, 0.1), S, nm="Paruh")
    ell(425, 262, 14, 26, S, nm="Pial")
    path(smooth([(290, 330), (210, 300), (170, 360), (230, 420)], True, 0.12), S, nm="Sayap")
    path("M270 455 L262 548 M335 455 L346 548", None, S, 14, nm="Kaki")
    path("M240 550 L292 550 M322 550 L376 550", None, S, 14, nm="Jari")
which = sys.argv[1:] or ["apel", "apel_hijau", "anggur", "keranjang", "ayam"]
if "apel" in which: build("apel_merah", lambda p, e: apel(p, e))
if "apel_hijau" in which: build("apel_hijau", lambda p, e: apel(p, e, ("#b6e86a", "#4f9e2a", "#2f6e1a")))
if "anggur" in which: build("anggur", anggur)
if "keranjang" in which: build("keranjang", keranjang)
if "ayam" in which: build("ayam_siluet", ayam)
