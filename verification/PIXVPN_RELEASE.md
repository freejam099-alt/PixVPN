# PixVPN 2.9 — hasil release ARM64

APK: `PixVPN-2.9-arm64-v8a.apk` · package `com.pixvpn.gram` · versionCode `29`.

## Perubahan

Tombol split lama diganti hero bulat gradasi kuning, oranye, hijau, biru, maksimum 224 dp dan menyesuaikan ruang tersedia tanpa animasi ukuran. Animasi SVG-path native menampilkan roket meluncur saat connecting/reconnecting, mendarat dan berhenti saat connected, serta pecahan ketika error akhir muncul. Opsi sambungan dipisahkan dari tombol utama dan ketukan cepat diberi guard 700 ms. Ikon peluncur adaptive/legacy memakai identitas PixVPN tanpa angka 8.

About mengarah ke `freejam099-alt/PixVPN`; opsi Telegram dihapus. README menjelaskan 32 region Psiphon + Auto, 7 rute, 5 protokol, serta penggunaan gratis tanpa menjanjikan ketersediaan jaringan pihak ketiga.

## Perbaikan koneksi

- Generation guard mencegah hasil probe exit/speed sesi lama menimpa sesi baru.
- Counter drop berulang membatasi siklus reconnect dan mengeluarkan error akhir untuk Try again.
- Budget health-check meliputi connect, handshake, header, dan body, bukan hanya body.
- Handle process dipublikasikan lintas thread dengan `@Volatile`.
- Perbaikan sebelumnya pada fail-closed split tunneling, lifecycle HEV, dan redaksi argumen sensitif tetap dipertahankan.

Nama package Android/namespace sudah PixVPN. Kelas internal `app.vpn8.Tun2Socks` sengaja dipertahankan sebagai bridge JNI optional yang simbol C-nya memakai nama tersebut; ini bukan application ID APK.

## Hasil pemeriksaan

Build `assembleRelease` berhasil. APK ARM64 saja, kelima runtime native Android tersedia, manifest non-debuggable, nama/paket/versi benar, signature valid dan cocok keystore pemilik. ZIP alignment berhasil. Tes statis resource XML, warna tema, identitas UI, dan runtime invariant lulus. Token, keystore, dan password tidak dimasukkan ke source Git.

SHA-256 APK: `9736c9c47db67c8cfe58c547f9a018ed8f1c003faea3a6a990a6270c1078a6ce`.

## Yang belum diuji

Tidak ada perangkat Android terhubung. Belum menguji instalasi, izin VPN, koneksi internet di dalam tunnel, seluruh region, pergantian Wi-Fi/data, frame-time animasi, TalkBack, atau layout font besar secara langsung. Tes statis bukan bukti semua server dapat tersambung. Ketersediaan region dan pemblokiran operator harus diperiksa pada perangkat pengguna.

Native runtime mengikuti provenance dari tugas sebelumnya: empat binary upstream digunakan ulang, libbox dibangun dari sing-box v1.13.21. Pipeline native GitHub belum dieksekusi ulang dalam rilis lokal ini; signing secret GitHub belum diunggah. Lihat `native-provenance.json`.
