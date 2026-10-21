# PixVPN

Klien VPN Android dengan tampilan **Material 3 Expressive**, tombol koneksi bulat gradasi kuning–oranye–hijau–biru, dan animasi roket. Roket meluncur saat menghubungkan, berhenti di bulan saat tersambung, dan pecah ketika koneksi gagal sehingga Anda bisa mencoba lagi.

**Nama aplikasi:** PixVPN · **Paket:** `com.pixvpn.gram` · **Versi:** 2.9 · **Android:** 8.0+ · **Arsitektur APK:** ARM64-v8a saja.

[Unduh APK dari Releases](https://github.com/freejam099-alt/PixVPN/releases) · [Source code](https://github.com/freejam099-alt/PixVPN)

## Gratis atau berbayar?

PixVPN tidak memasang pembayaran, langganan, iklan, atau akun wajib di aplikasi. Koneksi memakai jaringan pihak ketiga melalui Aether/WARP, Psiphon, dan Tor, bukan armada server premium milik PixVPN. Biaya data operator tetap berlaku. Kebijakan, kapasitas, kecepatan, dan ketersediaan layanan pihak ketiga berada di luar kendali aplikasi. Gratis bukan jaminan semua region selalu aktif atau koneksi tanpa batas dari penyedia.

## Ada berapa pilihan server?

Ada **32 pilihan negara/region Psiphon, ditambah Auto**. Ini daftar pilihan lokasi keluar, **bukan hitungan 32 mesin server yang sudah dipastikan online**. Lokasi yang benar-benar tersedia mengikuti jaringan Psiphon. Pilihan region berlaku pada rute yang menggunakan Psiphon; WARP memakai pemindaian gateway dan Tor menentukan relaynya sendiri.

Austria, Australia, Belgia, Bulgaria, Brasil, Kanada, Swiss, Ceko, Jerman, Denmark, Estonia, Spanyol, Finlandia, Prancis, Britania Raya, Kroasia, Hungaria, Irlandia, India, Italia, Jepang, Latvia, Belanda, Norwegia, Polandia, Rumania, Serbia, Swedia, Singapura, Slovakia, Ukraina, dan Amerika Serikat.

## Fitur utama

- Dua mode: **VPN perangkat** dan **proxy lokal** SOCKS5, dengan HTTP proxy opsional.
- **7 rute:** Aether, Aether → Psiphon, Aether → Tor, Psiphon saja, Psiphon → Aether, Tor saja, dan Tor → Aether.
- **5 protokol Aether:** WireGuard, MASQUE, WARP-in-MASQUE, WARP-in-WARP, dan MASQUE-in-MASQUE. Dukungan transport berbeda sesuai protokol.
- Reconnect, retry, pemindaian gateway, pemilihan region Psiphon, dan pengaturan bridge Tor.
- Statistik ping, download, upload, kecepatan uji, serta log diagnosis.
- Split tunneling, Quick Settings tile, tema gelap/terang, dynamic color Android 12+, serta bahasa Inggris dan Persia/RTL.
- Mesin TUN sing-box dengan opsi hev. Animasi dan label mengikuti status dari service; bukan simulasi keberhasilan koneksi.

## Memulai

1. Pasang APK ARM64 dari Releases. Perangkat harus mendukung `arm64-v8a`.
2. Pilih rute. Jika memakai Psiphon, mulai dari **Auto** sebelum mencoba negara tertentu.
3. Ketuk tombol bulat dan izinkan permintaan VPN Android.
4. Jika gagal, periksa internet awal, coba ulang atau ganti rute/protokol, lalu lihat log. Tidak semua operator mengizinkan protokol yang sama; WireGuard/QUIC dapat diblokir saat TCP masih berfungsi.

Paket PixVPN berbeda dari aplikasi lama `app.vpn8`, sehingga dapat dipasang berdampingan. Pengaturan aplikasi lama tidak otomatis dipindahkan. Jangan menghapus aplikasi lama bila masih membutuhkan datanya.

## Build sendiri

Gunakan JDK 17, Android SDK 35, dan Gradle wrapper yang tersedia. Dependensi native tidak disimpan sebagai binary di source Git:

- `app/src/main/jniLibs/arm64-v8a/`: `libaether.so`, `libpsiphon.so`, `liblyrebird.so`, `libhev-socks5-tunnel.so`.
- `app/libs/libbox.aar`: sing-box Android ARM64.

Asal dan hash native yang dipakai release lokal dicatat di [`verification/native-provenance.json`](verification/native-provenance.json). Empat runtime tersebut digunakan ulang tanpa modifikasi dari APK upstream 8VPN v2.8; libbox dibangun dari sing-box v1.13.21. Workflow di `.github/workflows/main.yml` mendokumentasikan pipeline native dan dapat dijalankan secara manual; **keberhasilan workflow build-ulang native belum diverifikasi oleh release lokal ini**.

Set variabel lingkungan `KEYSTORE_FILE`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, dan `KEY_PASSWORD` pada komputer build, lalu jalankan:

```sh
./scripts/build-release.sh
python3 scripts/check_design_invariants.py
python3 scripts/test_runtime_invariants.py
python3 scripts/verify_release.py app/build/outputs/apk/release/app-arm64-v8a-release.apk
```

Jangan simpan token, keystore, atau password di Git. Build lokal memakai keystore pemilik melalui environment; tidak memakai debug signing fallback. Untuk CI bertanda tangan, pemilik perlu menyiapkan secret repository yang disebut di workflow. Keystore pemilik tidak diunggah oleh proses publikasi ini.

## Pengujian dan batasan

Build, resource, source invariant, ABI/native runtime, manifest, dan signature APK diperiksa di lingkungan build. Tidak ada perangkat Android tersambung: **koneksi VPN nyata, kondisi seluruh region, kehalusan animasi, dan performa pada ponsel belum diuji langsung**. Perbaikan retry dan pemeriksaan koneksi mengurangi kegagalan yang berasal dari aplikasi, tetapi tidak menghidupkan server pihak ketiga yang sedang tidak tersedia.

## Kredit dan lisensi

PixVPN merupakan turunan [8VPN oleh Mrmmd2004](https://github.com/Mrmmd2004/8vpn). Lisensi proyek tetap **GNU AGPL-3.0**, lihat [LICENSE](LICENSE). Nama dan desain PixVPN tidak menyatakan afiliasi resmi dengan penyedia berikut:

| Komponen | Sumber |
| --- | --- |
| Aether | [CluvexStudio/Aether](https://github.com/CluvexStudio/Aether), v2.3.0 |
| Psiphon tunnel core | [Psiphon-Labs/psiphon-tunnel-core](https://github.com/Psiphon-Labs/psiphon-tunnel-core) |
| Tor / Lyrebird | [Tor Project](https://www.torproject.org/) / [sumber Lyrebird](https://gitlab.torproject.org/tpo/anti-censorship/pluggable-transports/lyrebird) |
| sing-box / libbox | [SagerNet/sing-box](https://github.com/SagerNet/sing-box), v1.13.21 |
| hev-socks5-tunnel | [heiher/hev-socks5-tunnel](https://github.com/heiher/hev-socks5-tunnel), 2.19.0 |

Komponen pihak ketiga mempertahankan lisensi masing-masing. Lihat sumber upstream dan pipeline build sebelum mendistribusikan ulang.
