# Memecah kura-kura AI (hadap bawah) jadi kepala, tempurung, 4 kaki dgn perpanjangan tersembunyi.
import cv2, numpy as np, json, os
os.chdir(os.path.join(os.path.dirname(os.path.abspath(__file__)), "kura_ai"))
os.makedirs("bagian", exist_ok=True)
src = cv2.imread("kura_ai_hadap_bawah.png", cv2.IMREAD_UNCHANGED)
bgr, alpha = src[..., :3], src[..., 3]; h, w = alpha.shape
hsv = cv2.cvtColor(bgr, cv2.COLOR_BGR2HSV)
green = ((hsv[..., 0] >= 24) & (hsv[..., 0] <= 52) & (hsv[..., 1] >= 55) & (alpha > 128)).astype(np.uint8)
gc = (cv2.morphologyEx(green, cv2.MORPH_CLOSE, cv2.getStructuringElement(cv2.MORPH_ELLIPSE, (41, 41))) & (alpha > 128).astype(np.uint8)).astype(bool)
yy, xx = np.mgrid[0:h, 0:w]
ell = lambda cx, cy, rx, ry: (((xx - cx) / rx) ** 2 + ((yy - cy) / ry) ** 2 <= 1)
head0 = gc & ell(770, 812, 192, 175)
rest = gc & ~head0
n, lab, st, cen = cv2.connectedComponentsWithStats(rest.astype(np.uint8))
M = {"kepala": head0.copy()}
for i in range(1, n):
    if st[i, cv2.CC_STAT_AREA] < 800: continue
    c = lab == i; cx = cen[i][0]
    if abs(cx - 498) < 80 and cen[i][1] < 400: M["kaki_bb"] = c
    elif abs(cx - 1038) < 80 and cen[i][1] < 400: M["kaki_ba"] = c
    else:
        M["kaki_fb"] = c & (xx < 598); M["kaki_fa"] = c & (xx > 942); M["kepala"] |= c & (xx >= 598) & (xx <= 942)
shell = (alpha > 128) & ~gc
# serpihan kecil (titik krem tepi kaki) di luar tempurung utama -> ikut bagian hijau terdekat
n2, lab2, st2, _ = cv2.connectedComponentsWithStats(shell.astype(np.uint8))
big = 1 + np.argmax(st2[1:, cv2.CC_STAT_AREA])
small = shell & (lab2 != big)
sm = cv2.dilate(small.astype(np.uint8), np.ones((5, 5), np.uint8)).astype(bool) & (alpha > 0)
shell = shell & ~sm
dists = {}
for k, m in M.items():
    dists[k] = cv2.distanceTransform((~m).astype(np.uint8), cv2.DIST_L2, 5)
ids = list(M.keys()); stack = np.stack([dists[k] for k in ids]); near = np.argmin(stack, axis=0)
for j, k in enumerate(ids): M[k] = M[k] | (sm & (near == j))
def extend(mask, grow=70):
    inv = (~mask).astype(np.uint8)
    dist, labels = cv2.distanceTransformWithLabels(inv, cv2.DIST_L2, 5, labelType=cv2.DIST_LABEL_PIXEL)
    ys, xs = np.where(mask); lut = {int(l): (y, x) for l, y, x in zip(labels[ys, xs], ys, xs)}
    out = bgr.copy(); zone = (dist <= grow) & (shell | mask) & (alpha > 0)
    zy, zx = np.where(zone & ~mask)
    for y, x in zip(zy, zx):
        sy, sx = lut[int(labels[y, x])]; out[y, x] = bgr[sy, sx]
    return out, zone | mask
rotk = lambda a: cv2.rotate(a, cv2.ROTATE_90_COUNTERCLOCKWISE)
parts = {}
for k, m in M.items():
    col, full = extend(m)
    a = cv2.GaussianBlur(full.astype(np.float32), (0, 0), 1.0)
    a2 = np.where(m, alpha.astype(np.float32) / 255, 1.0) * np.clip((a - 0.1) / 0.8, 0, 1)
    parts[k] = np.dstack([col, (a2 * 255).astype(np.uint8)])
parts["cangkang"] = np.dstack([bgr, np.where(shell, alpha, 0)])
tot = rotk((alpha > 10).astype(np.uint8)); ys, xs = np.where(tot > 0); pad = 24
y0, y1, x0, x1 = max(0, ys.min()-pad), ys.max()+pad, max(0, xs.min()-pad), xs.max()+pad
info = {"ukuran": [int(x1-x0), int(y1-y0)]}
for k, p in parts.items(): cv2.imwrite("bagian/kura_%s.png" % k, rotk(p)[y0:y1, x0:x1])
def cent(mask):
    r = rotk(mask.astype(np.uint8))[y0:y1, x0:x1]; a, b = np.where(r > 0); return [float(b.mean()), float(a.mean())]
info["pusat_cangkang"] = cent(shell)
dil = cv2.dilate(rotk(shell.astype(np.uint8))[y0:y1, x0:x1], np.ones((31, 31), np.uint8))
for k in M:
    r = rotk(M[k].astype(np.uint8))[y0:y1, x0:x1]; info[k + "_pusat"] = cent(M[k])
    ov = (r > 0) & (dil > 0); a, b = np.where(ov); info[k + "_pivot"] = [float(b.mean()), float(a.mean())] if len(a) else info[k + "_pusat"]
json.dump(info, open("bagian/info.json", "w"), indent=1); print("selesai", info["ukuran"])
