# Memecah karakter anak semi-3D (A-pose, 1024x1536) jadi 9 bagian dgn perpanjangan tersembunyi.
import cv2, numpy as np, json, os
os.chdir(os.path.join(os.path.dirname(os.path.abspath(__file__)), "anak")); os.makedirs("bagian", exist_ok=True)
src = cv2.imread("anak_utuh.png", cv2.IMREAD_UNCHANGED); bgr, alpha = src[..., :3], src[..., 3]; h, w = alpha.shape
A = alpha > 128
hsv = cv2.cvtColor(bgr, cv2.COLOR_BGR2HSV); H, S, V = hsv[..., 0].astype(int), hsv[..., 1].astype(int), hsv[..., 2].astype(int)
yy, xx = np.mgrid[0:h, 0:w]; XC = 506
shirt = (H >= 95) & (H <= 130) & (S >= 25) & A
skin = (H >= 2) & (H <= 25) & (S >= 28) & (V >= 140) & A
pants = (V < 150) & (S < 45) & A & (yy > 990)
# ---- garis lengan: tepi dalam lengan kiri (jahitan bahu -> ketiak), lalu sisi badan ----
seam = np.array([[640, 345], [650, 348], [790, 372], [880, 360]], float)       # (y, x)
def xs_left(y):
    return np.where(y < 880, np.interp(y, seam[:, 0], seam[:, 1]), 337.0)
armL_region = (xx < xs_left(yy)) & (yy > 630) & (yy < 1110)
armR_region = (xx > 2 * XC - xs_left(yy)) & (yy > 630) & (yy < 1110)
head = A & (yy < 596)
neck = skin & (yy >= 596) & (yy < 670) & (xx > 420) & (xx < 600)
hands = skin & (yy > 900) & (yy < 1110) & ((xx < 340) | (xx > 684))
feet = skin & (yy > 1380)
armL = (shirt | hands) & armL_region & ~head; armR = (shirt | hands) & armR_region & ~head
dada = (shirt & ~armL & ~armR & ~head) | neck
celana = pants
feetL = feet & (xx < XC); feetR = feet & (xx >= XC)
# ---- siku: potong tegak lurus sumbu lengan, tumpang-tindih 26 px ----
def split_arm(arm, shoulder, wrist, elbow):
    ax = np.array(wrist, float) - np.array(shoulder, float); u = ax / np.linalg.norm(ax)
    d = (xx - elbow[0]) * u[0] + (yy - elbow[1]) * u[1]
    return arm & (d < 26), arm & (d > -26), d
ELB_L, ELB_R = (280, 812), (2 * XC - 280, 812)
upL, foL, dL = split_arm(armL, (352, 668), (215, 965), ELB_L)
upR, foR, dR = split_arm(armR, (2 * XC - 352, 668), (2 * XC - 215, 965), ELB_R)
M = {"kepala": head, "dada": dada, "celana": celana, "lengan_atas_kiri": upL, "lengan_bawah_kiri": foL,
     "lengan_atas_kanan": upR, "lengan_bawah_kanan": foR, "kaki_kiri": feetL, "kaki_kanan": feetR}
# piksel tepi tak terklasifikasi -> bagian terdekat
cover = np.zeros((h, w), bool)
for m in M.values(): cover |= m
orphan = A & ~cover; ids = list(M.keys())
stack = np.stack([cv2.distanceTransform((~M[k]).astype(np.uint8), cv2.DIST_L2, 3) for k in ids]); near = np.argmin(stack, axis=0)
for j, k in enumerate(ids): M[k] = M[k] | (orphan & (near == j))
print({k: int(v.sum()) for k, v in M.items()})
# ---- perpanjangan warna terdekat ke area tersembunyi ----
def extend(mask, region, grow):
    inv = (~mask).astype(np.uint8)
    dist, lab = cv2.distanceTransformWithLabels(inv, cv2.DIST_L2, 5, labelType=cv2.DIST_LABEL_PIXEL)
    ys, xs = np.where(mask); lut = {int(l): (y, x) for l, y, x in zip(lab[ys, xs], ys, xs)}
    out = bgr.copy(); zone = (dist <= grow) & region & ~mask
    zy, zx = np.where(zone)
    for y, x in zip(zy, zx): sy, sx = lut[int(lab[y, x])]; out[y, x] = bgr[sy, sx]
    return out, mask | zone
ext_region = {"dada": (M["lengan_atas_kiri"] | M["lengan_atas_kanan"] | M["lengan_bawah_kiri"] | M["lengan_bawah_kanan"]), "celana": M["dada"]}
parts = {}
for k, m in M.items():
    if k in ext_region: col, full = extend(m, ext_region[k] & A & ((yy < 830) if k == "dada" else (yy > 0)), 24 if k == "dada" else 50)
    else: col, full = bgr, m
    af = alpha.astype(np.float32) / 255
    if k in ext_region:   # tepi perpanjangan dibuat lembut agar tidak berbentuk kotak
        zone = (full & ~m).astype(np.float32); soft = np.clip(cv2.GaussianBlur(zone, (0, 0), 9) * 1.7, 0, 1) * zone
        a = af * (m.astype(np.float32) + soft)
    else: a = af * full
    if k.startswith("lengan_atas"):            # halus di ujung siku
        d = dL if k.endswith("kiri") else dR
        a *= np.clip((26 - d) / 14.0, 0, 1)
    parts[k] = np.dstack([col, (np.clip(a, 0, 1) * 255).astype(np.uint8)])
ys, xs = np.where(A); y0, y1, x0, x1 = ys.min() - 16, ys.max() + 16, xs.min() - 16, xs.max() + 16
x0, y0, x1, y1 = int(x0), int(y0), int(x1), int(y1)
info = {"ukuran": [x1 - x0, y1 - y0], "offset": [x0, y0], "lantai": [XC - x0, int(ys.max()) - y0],
        "pivot": {"leher": [XC - x0, 600 - y0], "pinggang": [XC - x0, 1030 - y0], "pinggul": [XC - x0, 1060 - y0],
                  "bahu_kiri": [352 - x0, 668 - y0], "bahu_kanan": [2 * XC - 352 - x0, 668 - y0],
                  "siku_kiri": [ELB_L[0] - x0, ELB_L[1] - y0], "siku_kanan": [ELB_R[0] - x0, ELB_R[1] - y0],
                  "pergelangan_kiri": [360 - x0, 1420 - y0], "pergelangan_kanan": [2 * XC - 360 - x0, 1420 - y0]}}
for k, p in parts.items(): cv2.imwrite("bagian/anak_%s.png" % k, p[y0:y1, x0:x1])
json.dump(info, open("bagian/info.json", "w"), indent=1); print(info["ukuran"], info["lantai"])
