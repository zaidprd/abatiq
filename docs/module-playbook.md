# Module Playbook

> Mirror PRD v2 Bab 17 — cara menambah modul baru tanpa refactor besar.

Lihat: [`PRD_v2_Framework_Dan_Scalability.md`](../PRD_v2_Framework_Dan_Scalability.md)

## Langkah

**1. Designer / product**:
- Wireframe + aset di Figma.
- Compliance review Bab 3 → sign off.
- ContentPack JSON: `assets/content/{moduleId}.json`.
- Audio: `res/raw/{moduleId}/`.

**2. Engineering**:
- Folder `modules/{moduleId}/`.
- `ModuleManifest` impl + register di `ModuleRegistry`.
- string ids, icon, manifest.yml.

**3. QA**:
- Smoke test.
- Audio loads.
- Progress by moduleId.

**4. Release**:
- Modul otomatis muncul di Home.

## Estimasi Kasar (Referensi, Bukan Target)

- Modul ContentPack reuse: beberapa jam.
- Modul arena layout baru: beberapa hari.
- Modul Doa/Hijaiyah/Mengaji: beberapa hari + compliance review.
