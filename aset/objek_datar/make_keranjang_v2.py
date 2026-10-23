# Keranjang gaya datar tebal dengan bukaan besar membulat, dua lapisan (belakang dan depan) agar isi terlihat.
import cv2, numpy as np, os
from PIL import Image
os.chdir(os.path.dirname(os.path.abspath(__file__)))
S = 4                      # supersample
W = 600 * S
OR, OR_D, OR_L, IN = (59, 168, 242), (46, 146, 217), (92, 190, 252), (31, 116, 184)  # BGR: oranye, tua, terang, bagian dalam


def canvas():
    return np.zeros((W, W, 4), np.uint8)


def poly(img, pts, col, k=S):
    p = np.array([(x * k, y * k) for x, y in pts], np.int32)
    cv2.fillPoly(img, [p], col + (255,) if len(col) == 3 else col, lineType=cv2.LINE_AA)


def rrect(img, x0, y0, x1, y1, r, col):
    x0, y0, x1, y1, r = [int(v * S) for v in (x0, y0, x1, y1, r)]
    cv2.rectangle(img, (x0 + r, y0), (x1 - r, y1), col + (255,), -1, cv2.LINE_AA)
    cv2.rectangle(img, (x0, y0 + r), (x1, y1 - r), col + (255,), -1, cv2.LINE_AA)
    for cx, cy in ((x0 + r, y0 + r), (x1 - r, y0 + r), (x0 + r, y1 - r), (x1 - r, y1 - r)):
        cv2.circle(img, (cx, cy), r, col + (255,), -1, cv2.LINE_AA)


def left(y):  return 56 + (116 - 56) * ((y - 284) / 236.0)
def right(y): return 544 - (116 - 56) * ((y - 284) / 236.0)


body_pts = [(left(284), 284), (right(284), 284), (right(500), 500), (right(520) - 22, 520), (left(520) + 22, 520), (left(500), 500)]
# ---------- belakang ----------
back = canvas()
cv2.ellipse(back, (300 * S, 556 * S), (230 * S, 20 * S), 0, 0, 360, (70, 90, 60, 60), -1, cv2.LINE_AA)   # bayangan
cv2.ellipse(back, (300 * S, 236 * S), (178 * S, 168 * S), 0, 180, 360, OR_D + (255,), 30 * S, cv2.LINE_AA)  # gagang
poly(back, body_pts, IN)
rrect(back, 24, 236, 576, 292, 26, OR_D)
# ---------- depan ----------
front = canvas()
poly(front, body_pts, OR)
bottom = [(left(500), 500), (right(500), 500), (right(520) - 22, 520), (left(520) + 22, 520)]
poly(front, bottom, OR_D)
rrect(front, 20, 232, 580, 290, 26, OR)
rrect(front, 34, 272, 566, 292, 8, OR_D)
cv2.ellipse(front, (300 * S, 236 * S), (178 * S, 168 * S), 0, 200, 340, OR_L + (255,), 8 * S, cv2.LINE_AA)  # kilau tipis di gagang (hanya untuk tepi atas rim, tertutup belakang)
front[:236 * S] = 0
# bukaan: 5 bukaan tinggi, batang tebal
hole = np.zeros((W, W), np.uint8)
n, bar = 5, 36
y0, y1 = 306, 492
for i in range(n):
    def x_at(y, edge):
        l, r = left(y) + 30, right(y) - 30
        span = (r - l) - bar * (n - 1)
        w = span / n
        return l + i * (w + bar) + (w if edge else 0)
    quad = [(x_at(y0, 0), y0), (x_at(y0, 1), y0), (x_at(y1, 1), y1), (x_at(y1, 0), y1)]
    p = np.array([(x * S, y * S) for x, y in quad], np.int32)
    cv2.fillPoly(hole, [p], 255, lineType=cv2.LINE_AA)
r = 11 * S
hole = cv2.morphologyEx(hole, cv2.MORPH_OPEN, cv2.getStructuringElement(cv2.MORPH_ELLIPSE, (2 * r + 1, 2 * r + 1)))
front[..., 3] = (front[..., 3].astype(np.float32) * (1 - hole.astype(np.float32) / 255)).astype(np.uint8)
for name, img in (("keranjang_v2_belakang", back), ("keranjang_v2_depan", front)):
    out = cv2.resize(img, (600, 600), interpolation=cv2.INTER_AREA)
    cv2.imwrite(name + ".png", out)
# pratinjau dengan buah
def load(n):
    return Image.open(n).convert("RGBA")
b, f = load("keranjang_v2_belakang.png"), load("keranjang_v2_depan.png")
bg = Image.new("RGBA", (600, 600), (233, 247, 218, 255))
bg.alpha_composite(b)
for nm, x, y, sc in (("apel_merah_datar.png", 205, 400, 0.30), ("anggur_datar.png", 395, 400, 0.30), ("alpukat_datar.png", 300, 330, 0.30)):
    im = load(nm)
    im = im.resize((int(600 * sc * 1.7), int(600 * sc * 1.7)), Image.LANCZOS)
    bg.alpha_composite(im, (int(x - im.width / 2), int(y - im.height / 2)))
bg.alpha_composite(f)
bg.save("pratinjau_keranjang_v2.png")
print("selesai")
