# Memecah badan anak baru (ab_baru.png, 530x617) jadi bagian rig. Semua potongan sebesar kanvas asli,
# jadi posisi di EffectCraft sama untuk semua bagian dan hanya titik putarnya yang berbeda.
# L = sisi kiri layar, R = sisi kanan layar (cermin terhadap x=265).
import cv2, numpy as np, os, json
os.chdir(os.path.dirname(os.path.abspath(__file__)))
src = cv2.imread("ab_baru.png", cv2.IMREAD_UNCHANGED)
H, W = src.shape[:2]
A = src[..., 3] > 8
CX = 265
OVER = 14


def poly(pts):
    m = np.zeros((H, W), np.uint8)
    cv2.fillPoly(m, [np.array(pts, np.int32)], 1)
    return m.astype(bool)


def mirror(pts):
    return [(2 * CX - x, y) for x, y in pts]


# lengan atas: di atas garis manset, di luar jahitan lengan baju
UP_L = [(172, 30), (162, 92), (152, 140), (143, 190), (135, 225), (131, 252), (140, 264), (30, 236), (0, 236), (0, 0), (172, 0)]
# lengan bawah + tangan: di bawah garis manset, kiri x=138
LOW_L = [(0, 236), (30, 236), (136, 262), (130, 286), (126, 412), (0, 412)]
# kaki: di bawah pinggang, kiri/kanan pangkal paha x=268
LEG_L = [(100, 296), (268, 296), (268, H), (100, H)]

parts = {}
up = {"L": poly(UP_L) & A}
up["R"] = poly(mirror(UP_L)) & A
low = {"L": poly(LOW_L) & A & ~up["L"]}
low["R"] = poly(mirror(LOW_L)) & A & ~up["R"]
leg = {"L": poly(LEG_L) & A & ~low["L"] & ~low["R"] & ~up["L"] & ~up["R"]}
leg["R"] = poly(mirror(LEG_L)) & A & ~leg["L"] & ~low["L"] & ~low["R"] & ~up["L"] & ~up["R"]
torso = A & ~up["L"] & ~up["R"] & ~low["L"] & ~low["R"] & ~(leg["L"] & (np.arange(H)[:, None] >= 308)) & ~(leg["R"] & (np.arange(H)[:, None] >= 308))


def extend(mask, behind):
    """tambah piksel di bawah bagian induk (ikut gambar asli) agar sambungan tidak bolong saat berputar"""
    d = cv2.dilate(mask.astype(np.uint8), cv2.getStructuringElement(cv2.MORPH_ELLIPSE, (OVER * 2 + 1, OVER * 2 + 1))).astype(bool)
    return mask | (d & behind & A)


out = {
    "badan": torso,
    "lengan_atas_L": extend(up["L"], torso),
    "lengan_atas_R": extend(up["R"], torso),
    "lengan_bawah_L": extend(low["L"], up["L"]),
    "lengan_bawah_R": extend(low["R"], up["R"]),
    "kaki_L": extend(leg["L"], torso),
    "kaki_R": extend(leg["R"], torso),
}
os.makedirs("rig", exist_ok=True)
for k, m in out.items():
    a = cv2.GaussianBlur(m.astype(np.float32), (0, 0), 0.7) * (src[..., 3].astype(np.float32) / 255)
    p = src.copy()
    p[..., 3] = np.clip(a * 255, 0, 255).astype(np.uint8)
    cv2.imwrite("rig/ab_%s.png" % k, p)
    print(k, int(m.sum()))
# pratinjau: semua bagian ditumpuk sesuai urutan gambar
order = ["lengan_bawah_L", "lengan_bawah_R", "lengan_atas_L", "lengan_atas_R", "kaki_L", "kaki_R", "badan"]
canvas = np.full((H, W, 3), 255, np.float32)
for k in order:
    p = cv2.imread("rig/ab_%s.png" % k, cv2.IMREAD_UNCHANGED).astype(np.float32)
    al = p[..., 3:4] / 255
    canvas = p[..., :3] * al + canvas * (1 - al)
cv2.imwrite("rig/_tumpuk.png", canvas.astype(np.uint8))
json.dump({"ukuran": [W, H], "pusat_x": CX,
           "pivot": {"bahu_L": [150, 85], "bahu_R": [380, 85], "siku_L": [92, 248], "siku_R": [438, 248],
                     "pinggul_L": [205, 305], "pinggul_R": [325, 305]}}, open("rig/info_rig.json", "w"), indent=1)
