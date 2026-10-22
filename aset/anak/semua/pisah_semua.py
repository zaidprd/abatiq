# Memotong 12 pose: matte warna (menjaga jari) dibatasi area PhotoCraft, lalu kepala dipisah dari badan.
import cv2, numpy as np, json
names = ["jalan_1","jalan_2","jalan_3","jalan_4","jalan_5","jalan_6","menunjuk_atas","menunjuk_bawah","jempol","stop","mendengar","duduk_bersila"]
info = {}
for nm in names:
    o = cv2.imread("t_%s_asli.png" % nm); pc = cv2.imread("c_%s.png" % nm, cv2.IMREAD_UNCHANGED); h, w = o.shape[:2]
    mn = o.min(axis=2); ch = o.max(axis=2).astype(int) - mn
    neutral = ((mn >= 234) & (ch <= 14)).astype(np.uint8); bg = np.zeros((h, w), np.uint8)
    for s in [(0,0),(w-1,0),(0,h-1),(w-1,h-1),(w//2,0),(w//2,h-1)]:
        m2 = np.zeros((h+2, w+2), np.uint8); t = neutral.copy(); cv2.floodFill(t, m2, s, 2, flags=4); bg |= (t == 2).astype(np.uint8)
    col_m = (1 - bg).astype(np.uint8)
    near = cv2.dilate((pc[..., 3] > 128).astype(np.uint8), cv2.getStructuringElement(cv2.MORPH_ELLIPSE, (61, 61)))
    m = col_m & near
    hsv = cv2.cvtColor(o, cv2.COLOR_BGR2HSV); m[(hsv[...,1] < 28) & (hsv[...,2] > 215)] = 0          # halo putih
    n, lab, st, _ = cv2.connectedComponentsWithStats(m); big = [i for i in range(1, n) if st[i,4] > 400]; m = np.isin(lab, big).astype(np.uint8)
    cn, _ = cv2.findContours(m, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_NONE); f = np.zeros_like(m); cv2.drawContours(f, cn, -1, 1, -1)
    # isi lubang kecil di antara jari tidak diisi: pakai m asli + isi tubuh utama
    fm = np.maximum(m, cv2.erode(f, np.ones((9, 9), np.uint8)))
    alpha = np.clip((cv2.GaussianBlur(fm.astype(np.float32), (0,0), 0.9) - 0.3) / 0.4, 0, 1)
    inner = cv2.erode(fm, np.ones((5,5), np.uint8)); fill = cv2.inpaint(o, (1 - inner).astype(np.uint8) * 255, 3, cv2.INPAINT_TELEA)
    col = np.where(((fm == 1) & (inner == 0))[..., None], fill, o)
    im = np.dstack([col, (alpha * 255).astype(np.uint8)]); a = im[..., 3] > 128
    S, V, H = hsv[...,1].astype(int), hsv[...,2].astype(int), hsv[...,0].astype(int)
    hair = (o.max(axis=2) < 75) & a; ys, xs = np.where(hair); cx = int(np.median(xs)); ytop = int(ys.min())
    blue = (H >= 95) & (H <= 128) & (S >= 35) & (V >= 130) & a
    band = blue[:, max(0, cx-40):cx+41].sum(axis=1); cand = np.where(band >= 12)[0]; yb = int(cand[cand > ytop + 90].min())
    n, lab2 = cv2.connectedComponents(a.astype(np.uint8)); lh = np.bincount(lab2[hair]).argmax(); comp = lab2 == lh
    if int(comp.sum()) < 0.65 * int(a.sum()): head_m, body_m, mode = comp, a & ~comp, "terpisah"
    else:
        mm = a.copy(); mm[yb - 5:] = False; n2, l2 = cv2.connectedComponents(mm.astype(np.uint8)); head_m = l2 == np.bincount(l2[hair & mm]).argmax(); body_m = a & ~head_m; mode = "dipotong"
    skin = (H >= 2) & (H <= 26) & (S >= 22) & (V >= 150)
    neck = skin & body_m & (np.arange(h)[:, None] < yb + 8) & (np.abs(np.arange(w)[None, :] - cx) < 60)
    body = im.copy(); body[..., 3] = (im[..., 3] * body_m).astype(np.uint8); body[neck, :3] = (176, 138, 100); body[neck, 3] = 255
    head = im.copy(); head[..., 3] = (im[..., 3] * head_m).astype(np.uint8)
    hy, hx = np.where(head_m); chin = [float(np.median(hx[hy > hy.max() - 12])), float(hy.max())]
    by, bx = np.where(body_m); foot = [float(np.median(bx[by > by.max() - 10])), float(by.max())]
    cv2.imwrite("final/h_%s.png" % nm, head); cv2.imwrite("final/b_%s.png" % nm, body)
    info[nm] = {"ukuran": [w, h], "dagu": chin, "kaki": foot, "mode": mode}
json.dump(info, open("final/info.json", "w"), indent=1); print({k: v["mode"] for k, v in info.items()})
