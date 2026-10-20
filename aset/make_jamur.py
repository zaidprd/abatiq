# Kura-kura tampak atas (tanpa mata) - gaya glossy mendekati render 3D. Menghadap kanan (+x).
import json, math, random, subprocess, sys
CLI = r"C:\Program Files\VectorCraft\vectorcraft-cli.exe"
OUTDIR = "E:/latihan/AE/aset/"
W, H = 1000, 800
OX, OY = 500, 400
random.seed(7)
steps = [("file.new", {"width": W, "height": H, "units": "Pixels", "backgroundContents": "transparent", "name": "Jamur_TampakAtas"})]
PREVIEW = "--preview" in sys.argv
ONLY = next((a.split("=")[1] for a in sys.argv if a.startswith("--only=")), None)
CUR = None

def f(v): return "%.2f" % v
def P(x, y): return (OX + x, OY + y)

def ell_d(cx, cy, rx, ry, rot=0.0):
    k = 0.5523
    loc = [((rx, 0), (0, -k*ry), (0, k*ry)), ((0, ry), (k*rx, 0), (-k*rx, 0)),
           ((-rx, 0), (0, k*ry), (0, -k*ry)), ((0, -ry), (-k*rx, 0), (k*rx, 0))]
    c, s = math.cos(math.radians(rot)), math.sin(math.radians(rot))
    def T(p): return P(cx + p[0]*c - p[1]*s, cy + p[0]*s + p[1]*c)
    pts = []
    for p, hin, hout in loc:
        pts.append((T(p), T((p[0]+hin[0], p[1]+hin[1])), T((p[0]+hout[0], p[1]+hout[1]))))
    d = "M%s %s " % (f(pts[0][0][0]), f(pts[0][0][1]))
    for i in range(4):
        a = pts[i]; b = pts[(i+1) % 4]
        d += "C%s %s %s %s %s %s " % (f(a[2][0]), f(a[2][1]), f(b[1][0]), f(b[1][1]), f(b[0][0]), f(b[0][1]))
    return d + "Z"

def poly_d(pts):
    q = [P(*p) for p in pts]
    return "M" + " L".join("%s %s" % (f(x), f(y)) for x, y in q) + " Z"

def grad(kind, stops, angle=None):
    g = {"kind": kind, "stops": [{"offset": o, "color": c, **({"opacity": a} if a is not None else {})} for o, c, a in stops]}
    if angle is not None: g["angle"] = angle
    return g

def shape(name, d, fill, stroke=None, sw=2.0, opacity=None, blur=None):
    if ONLY and CUR != ONLY: return
    steps.append(("path.create", {"d": d}))
    if isinstance(fill, dict): steps.append(("paint.setFill", {"gradient": fill}))
    else: steps.append(("paint.setFill", {"color": fill}))
    if stroke:
        steps.append(("paint.setStroke", {"color": stroke}))
        steps.append(("stroke.set", {"weight": sw, "join": "round", "cap": "round"}))
    else:
        steps.append(("paint.setStroke", {"none": True}))
    props = {"name": name}
    if opacity is not None: props["opacity"] = opacity
    steps.append(("object.setProps", props))
    if blur: steps.append(("effect.apply", {"effect": "blur.gaussian", "params": {"radius": blur}}))

def lerp(a, b, t): return tuple(a[i] + (b[i]-a[i])*t for i in range(3))
def hx(h): return tuple(int(h[i:i+2], 16) for i in (1, 3, 5))
def tohex(c): return "#%02x%02x%02x" % tuple(max(0, min(255, int(round(v)))) for v in c)
SH = [hx("#c8741a"), hx("#f0a62e"), hx("#ffe07a")]
def shade(b):
    b = max(0.0, min(1.0, b))
    return tohex(lerp(SH[0], SH[1], b*2) if b < 0.5 else lerp(SH[1], SH[2], (b-0.5)*2))

if PREVIEW and not ONLY:
    steps.append(("shape.rectangle", {"x": 0, "y": 0, "width": W, "height": H}))
    steps.append(("paint.setFill", {"gradient": grad("radial", [(0, "#f4fbf1", None), (1, "#bfe6cf", None)])}))
    steps.append(("paint.setStroke", {"none": True}))
    steps.append(("object.setProps", {"name": "Latar_Preview"}))

LIME = [(0, "#f6fbb8", None), (0.5, "#cfe45c", None), (1, "#93b52d", None)]



WHITE0 = lambda a: grad("radial", [(0, "#ffffff", a), (1, "#ffffff", 0.0)])
CUR = None
shape("Bayangan", ell_d(10, 20, 192, 178), "#2a2a14", opacity=26, blur=16)
shape("Bibir_Bawah", ell_d(0, 9, 188, 174), grad("linear", [(0, "#fde3da", None), (1, "#e8a0a6", None)], 90))
shape("Tutup", ell_d(0, 0, 184, 170), grad("radial", [(0, "#ff6575", None), (0.62, "#e92c43", None), (1, "#a31429", None)]))
spots = [(-70,-62,42,36),(42,-84,30,26),(98,-18,36,31),(-102,22,28,24),(-30,32,46,40),(72,74,32,28),(-62,98,25,21),(8,-22,21,18),(124,64,17,14),(-18,-116,19,16),(-128,-40,18,15)]
for i, (x, y, rx, ry) in enumerate(spots):
    r = min(1.0, math.hypot(x/184.0, y/170.0)); k = 1 - 0.38*r*r
    shape("Bintik_Bayang%d" % (i+1), ell_d(x+4, y+6, rx*k, ry*k), "#6d0c1c", opacity=30)
    shape("Bintik%d" % (i+1), ell_d(x, y, rx*k, ry*k), grad("radial", [(0, "#ffffff", None), (0.7, "#fbeeee", None), (1, "#e9d4d6", None)]))
shape("Tutup_Kilau", ell_d(-62, -72, 104, 56, -32), WHITE0(0.45))
shape("Tepi_Kilau", ell_d(-40, -150, 112, 12, -14), WHITE0(0.8))

for ext in ("svg", "png", "vectorcraft"):
    if PREVIEW and ext != "png": continue
    steps.append(("__export__", ext))
tag = "_preview" if PREVIEW else ""
base = "jamur_tampak_atas" + tag
args = [CLI, "run"]
for c, p in steps:
    if c == "__export__": args += ["--export", OUTDIR + base + "." + p]
    else: args += ["--cmd", c, "--params", json.dumps(p)]
args += ["--scale", "1"]
r = subprocess.run(args, capture_output=True, text=True)
bad = [l for l in r.stdout.splitlines() if '"error"' in l]
print("steps:", len(steps), "errors:", len(bad))
for l in bad[:5]: print(l[:400])
print(r.stderr[:600])
