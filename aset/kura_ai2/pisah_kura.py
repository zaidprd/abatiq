# Memecah kura-kura tampak atas (sumber.png, kepala menghadap bawah) jadi kepala, tempurung, 4 kaki.
# Keluaran: bagian/kura_<nama>.png (diputar agar kepala menghadap kanan, kanvas sama) + bagian/info.json
import cv2, numpy as np, json, os
os.chdir(os.path.dirname(os.path.abspath(__file__)))
o = cv2.imread("sumber.png")
src = cv2.imread("potong.png", cv2.IMREAD_UNCHANGED)
bgr, alpha = src[..., :3], src[..., 3]
h, w = alpha.shape
A = alpha > 128
hsv = cv2.cvtColor(bgr, cv2.COLOR_BGR2HSV)
H, S, V = [hsv[..., i].astype(int) for i in range(3)]
green = ((H >= 28) & (H <= 52) & (S >= 55) & A).astype(np.uint8)
gc = (cv2.morphologyEx(green, cv2.MORPH_CLOSE, cv2.getStructuringElement(cv2.MORPH_ELLIPSE, (41, 41))) & A.astype(np.uint8)).astype(bool)
seed = cv2.erode(gc.astype(np.uint8), cv2.getStructuringElement(cv2.MORPH_ELLIPSE, (45, 45)))
n, lab, st, cen = cv2.connectedComponentsWithStats(seed)
seeds = {}
for i in range(1, n):
    if st[i, 4] < 300:
        continue
    cx, cy = cen[i]
    if st[i, 4] > 30000:
        name = "kepala"
    else:
        name = ("kaki_atas_" if cy < h / 2 else "kaki_bawah_") + ("kiri" if cx < w / 2 else "kanan")
    seeds[name] = (lab == i)
# cangkang = coklat yang ditutup + tepi krem
brown = ((H >= 5) & (H <= 22) & (S >= 100) & (V >= 60) & A).astype(np.uint8)
shell_core = cv2.morphologyEx(brown, cv2.MORPH_CLOSE, cv2.getStructuringElement(cv2.MORPH_ELLIPSE, (61, 61)))
shell_mask = cv2.dilate(shell_core, cv2.getStructuringElement(cv2.MORPH_ELLIPSE, (81, 81))).astype(bool) & A & ~gc
names = list(seeds.keys())
dist = np.stack([cv2.distanceTransform((~seeds[k]).astype(np.uint8), cv2.DIST_L2, 5) for k in names])
near = np.argmin(dist, axis=0)
M = {}
for j, k in enumerate(names):
    M[k] = gc & (near == j)
# jari krem pada kaki: piksel bukan hijau, bukan cangkang, dekat kaki
rest = A & ~gc & ~shell_mask
for j, k in enumerate(names):
    M[k] = M[k] | (rest & (near == j) & (dist[j] < 70))
M["cangkang"] = A & ~np.any([M[k] for k in names], axis=0)


def extend(mask, grow):
    inv = (~mask).astype(np.uint8)
    d, labels = cv2.distanceTransformWithLabels(inv, cv2.DIST_L2, 5, labelType=cv2.DIST_LABEL_PIXEL)
    ys, xs = np.where(mask)
    lut = {int(l): (y, x) for l, y, x in zip(labels[ys, xs], ys, xs)}
    out = bgr.copy()
    zone = (d <= grow) & shell_mask & ~mask
    zy, zx = np.where(zone)
    for y, x in zip(zy, zx):
        sy, sx = lut[int(labels[y, x])]
        out[y, x] = bgr[sy, sx]
    return out, mask | zone


rot = lambda a: cv2.rotate(a, cv2.ROTATE_90_COUNTERCLOCKWISE)
parts = {}
for k in names:
    col, full = extend(M[k], 70)
    a = cv2.GaussianBlur(full.astype(np.float32), (0, 0), 1.0)
    a2 = np.where(M[k], alpha.astype(np.float32) / 255, 1.0) * np.clip((a - 0.1) / 0.8, 0, 1)
    parts[k] = np.dstack([col, (a2 * 255).astype(np.uint8)])
parts["cangkang"] = np.dstack([bgr, np.where(M["cangkang"], alpha, 0)])
tot = rot(A.astype(np.uint8))
ys, xs = np.where(tot > 0)
pad = 24
y0, y1, x0, x1 = max(0, ys.min() - pad), ys.max() + pad, max(0, xs.min() - pad), xs.max() + pad
os.makedirs("bagian", exist_ok=True)
for k, p in parts.items():
    cv2.imwrite("bagian/kura_%s.png" % k, rot(p)[y0:y1, x0:x1])


def cent(mask):
    r = rot(mask.astype(np.uint8))[y0:y1, x0:x1]
    a, b = np.where(r > 0)
    return [float(b.mean()), float(a.mean())]


info = {"ukuran": [int(x1 - x0), int(y1 - y0)], "pusat_cangkang": cent(M["cangkang"])}
dil = cv2.dilate(rot(M["cangkang"].astype(np.uint8))[y0:y1, x0:x1], np.ones((31, 31), np.uint8))
for k in names:
    r = rot(M[k].astype(np.uint8))[y0:y1, x0:x1]
    info[k + "_pusat"] = cent(M[k])
    ov = (r > 0) & (dil > 0)
    a, b = np.where(ov)
    info[k + "_pivot"] = [float(b.mean()), float(a.mean())] if len(a) else info[k + "_pusat"]
json.dump(info, open("bagian/info.json", "w"), indent=1)
print(info["ukuran"], names)
