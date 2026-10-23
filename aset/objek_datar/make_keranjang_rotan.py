# Memecah keranjang_datar.png jadi dua lapisan agar isinya terlihat dari sela anyaman:
#   keranjang_belakang.png = bayangan, gagang, dan bagian dalam gelap
#   keranjang_depan.png    = tepi atas + badan dengan kotak anyaman yang bolong (transparan)
import cv2, numpy as np, os
os.chdir(os.path.dirname(os.path.abspath(__file__)))
src = cv2.imread("keranjang_datar.png", cv2.IMREAD_UNCHANGED)
H, W = src.shape[:2]
bgr, a = src[..., :3].astype(int), src[..., 3]
solid = a > 200
yy = np.arange(H)[:, None] * np.ones((1, W))
# garis anyaman: warna oranye tua (sekitar #B8741F), hanya di badan
line = ((abs(bgr[..., 2] - 184) < 22) & (abs(bgr[..., 1] - 116) < 22) & (abs(bgr[..., 0] - 31) < 30) & solid & (yy > 280) & (yy < 520))
body = solid & (yy >= 276)
rim = solid & (yy >= 222) & (yy < 280)
k = lambda r: cv2.getStructuringElement(cv2.MORPH_ELLIPSE, (2 * r + 1, 2 * r + 1))
# garis mendatar dibuang dari batang: hanya batang tegak yang tersisa, bukaan jadi besar dan tinggi
vk = cv2.getStructuringElement(cv2.MORPH_RECT, (1, 45))
vert = cv2.morphologyEx(line.astype(np.uint8), cv2.MORPH_OPEN, vk)
lines_d = cv2.dilate(vert, k(8))
inner = cv2.erode(body.astype(np.uint8), k(14))
inner[:292] = 0
hole = (inner > 0) & (lines_d == 0)
hole = cv2.morphologyEx(hole.astype(np.uint8), cv2.MORPH_OPEN, k(7))
hole = cv2.morphologyEx(hole, cv2.MORPH_CLOSE, k(3))
# buang lubang yang terlalu kecil
n, lab, st, _ = cv2.connectedComponentsWithStats(hole)
for i in range(1, n):
    if st[i, 4] < 2500:
        hole[lab == i] = 0
soft = cv2.GaussianBlur(hole.astype(np.float32), (0, 0), 1.2)
# depan: tepi atas + badan, tanpa gagang dan bayangan, dengan lubang
front = src.copy()
keep = solid & (yy >= 222)
front[..., 3] = np.where(keep, 255, 0).astype(np.uint8)
front[..., 3] = np.clip(front[..., 3].astype(np.float32) * (1 - soft), 0, 255).astype(np.uint8)
cv2.imwrite("keranjang_depan.png", front)
# belakang: bayangan + gagang (semua yang bukan depan) + bagian dalam gelap
back = src.copy()
back[..., 3] = np.where(keep, 0, a)
inside = cv2.dilate(body.astype(np.uint8), k(2)).astype(bool)
dark = np.zeros_like(src)
dark[..., :3] = (18, 48, 92)  # BGR: cokelat tua #5C3012
dark[..., 3] = np.where(inside | rim, 255, 0)
out = back.copy()
m = dark[..., 3] > 0
out[m] = dark[m]
cv2.imwrite("keranjang_belakang.png", out)
print("lubang:", n - 1, "luas", int(hole.sum()))
