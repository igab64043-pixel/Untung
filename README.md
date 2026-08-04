# PerpusAI-Expo — Aplikasi Perpustakaan Offline + AI Guru Baca Buku

PerpusAI-Expo adalah aplikasi mobile perpustakaan offline modern yang dilengkapi dengan fitur AI Lokal / Offline ("GuruAI") untuk mempermudah pemahaman buku, pembuatan kuis otomatis dari PDF, serta fitur **Mode Pembaca AI** dengan penyederhanaan gaya bahasa anak SMP dan Text-to-Speech (TTS) tersinkronisasi.

Aplikasi ini siap di-build menjadi **APK Android** secara **100% Cloud** menggunakan **GitHub Actions** dan **EAS Build** (tanpa perlu install Android Studio di laptop lokal).

---

## 🚀 FITUR UTAMA

### 1. 📚 Perpustakaan Offline
- **CRUD Buku**: Tambah, edit, dan hapus katalog buku digital.
- **Pencarian & Kategori**: Cari berdasarkan judul, pengarang, atau subjek.
- **PDF Reader Built-in**: Pembaca PDF internal dengan dukungan bookmark dan navigasi halaman.
- **Sistem Peminjaman**: Catat pinjam dan pengembalian buku beserta riwayat.

### 2. 🧠 AI Offline "GuruAI" (Phi-3 GGUF / Gemini Offline Fallback)
- **Chat Q&A**: Bertanya seputar buku secara offline tanpa koneksi internet.
- **Generator Kuis Otomatis**: Menghasilkan 10 Pilihan Ganda + 5 Soal Esai langsung dari konten halaman/bab PDF.
- **Ringkasan Buku Instant**: Ringkasan poin-poin penting isi buku.

### 3. 🎙️ Mode Pembaca AI (Guru Baca Buku)
- **Pilih Materi**: Pilih halaman / bab tertentu dari buku.
- **AI Simplifier**: Mengubah kalimat buku yang kaku menjadi narasi santai berkonsep "anak SMP" (*"jadi gini"*, *"paham ya"*, *"contohnya"*).
- **TTS Offline Tersinkronisasi**: Pemutaran suara Piper/Android TTS dengan 3 pilihan suara & 3 pilihan kecepatan (0.8x, 1x, 1.5x).
- **Text-Highlight Player**: Teks ter-highlight per kalimat secara sinkron mengikuti pemutaran suara.
- **Kontrol Audio Player**: Play, Pause, Repeat Paragraph, dan Simpan/Download Teks Narasi Audio.

### 4. ⚙️ Admin & NAS Synchronization
- **Import Data ZIP**: Restore katalog & PDF dari arsip .zip.
- **Backup Database**: Ekspor database SQLite.
- **NasSyncService**: Service sinkronisasi otomatis ke Network Attached Storage (NAS) lokal sekolah/perpustakaan.

---

## 🛠️ PANDUAN CI/CD: BUILD APK VIA GITHUB ACTIONS & EAS BUILD

### 1. Cara Membuat `EAS_TOKEN` di Expo.dev
1. Buka [https://expo.dev](https://expo.dev) dan buat akun/login.
2. Pergi ke halaman **Account Settings** -> **Access Tokens**.
3. Klik **Create Token**, berikan nama token (misal: `github-actions-eas`), lalu klik **Create**.
4. Salin token rahasia yang dihasilkan.

### 2. Cara Menyimpan `EAS_TOKEN` di GitHub Secrets
1. Buka repositori GitHub aplikasi Anda.
2. Masuk ke tab **Settings** -> **Secrets and variables** -> **Actions**.
3. Klik tombol **New repository secret**.
4. Masukkan Name: `EAS_TOKEN`.
5. Masukkan Secret: (Tempel token dari expo.dev).
6. Klik **Add secret**.

### 3. Panduan Upload Model `.gguf` (Git LFS / Batas File Git)
Model LLM GGUF (seperti `Phi-3-mini-4k-instruct-q4.gguf`) berukuran ~2.2 GB. GitHub membatasi upload file biasa maks 100MB.

**Pilihan A: Menggunakan Git LFS (Large File Storage)**
```bash
git lfs install
git lfs track "assets/models/*.gguf"
git add .gitattributes
git add assets/models/Phi-3-mini-4k-instruct-q4.gguf
git commit -m "Add Phi-3 GGUF model via LFS"
git push origin main
```

**Pilihan B: Auto-Download saat App First Launch (Direkomendasikan)**
Simpan model di server lokal NAS perpustakaan atau CDN, lalu panggil `FileDownload` service aplikasi untuk mengunduh model saat pertama kali aplikasi dibuka dan menyimpannya di `FileSystem.documentDirectory + 'models/'`.

### 4. Cara Mengunduh APK Hasil Build dari EAS
1. Setelah Anda melakukan `git push origin main`, GitHub Actions workflow `.github/workflows/build-apk.yml` akan berjalan secara otomatis.
2. Buka dashboard [https://expo.dev](https://expo.dev) -> pilih project **perpusai-expo** -> tab **Builds**.
3. Anda akan melihat build status `In Progress` -> `Finished`.
4. Klik tombol **Download APK** atau scan **QR Code** langsung dari HP Anda untuk menginstall aplikasi!

---

## 📄 LISENSI & KONTRIBUSI
Dibuat untuk memperluas akses literasi & edukasi berteknologi AI offline di seluruh pelosok perpustakaan Indonesia.
