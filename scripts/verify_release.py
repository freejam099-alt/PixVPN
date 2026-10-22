#!/usr/bin/env python3
"""Verify the real release APK; this does not test a VPN session or UI performance."""
import argparse
import hashlib
from pathlib import Path
import re
import struct
import subprocess
import tempfile
import zipfile

p = argparse.ArgumentParser()
p.add_argument('apk', type=Path)
p.add_argument('--sdk', type=Path, default=Path('/opt/android-sdk'))
p.add_argument('--expected-cert-sha256', help='Owner certificate fingerprint, with or without colons')
a = p.parse_args()
assert a.apk.is_file() and a.apk.stat().st_size > 0, 'APK is missing or empty'
tools = sorted((a.sdk / 'build-tools').glob('*'), key=lambda x: [int(n) for n in re.findall(r'\d+', x.name)])[-1]

def run(*cmd):
    return subprocess.check_output([str(x) for x in cmd], stderr=subprocess.STDOUT, text=True)

with zipfile.ZipFile(a.apk) as z:
    native = [n for n in z.namelist() if n.startswith('lib/') and n.endswith('.so')]
    assert native, 'APK has no native runtime libraries'
    abis = {n.split('/')[1] for n in native}
    assert abis == {'arm64-v8a'}, f'Unexpected ABIs: {abis}'
    required = {'libaether.so', 'libpsiphon.so', 'liblyrebird.so', 'libhev-socks5-tunnel.so', 'libbox.so'}
    actual = {Path(n).name for n in native}
    assert required <= actual, f'Missing runtime libraries: {required - actual}'
    for n in native:
        data = z.read(n)
        assert data[:4] == b'\x7fELF', f'{n} is not ELF'
        assert data[4] == 2 and data[5] == 1, f'{n} is not ELF64 little-endian'
        assert struct.unpack_from('<H', data, 18)[0] == 183, f'{n} is not AArch64'
        with tempfile.NamedTemporaryFile(suffix='.so') as f:
            f.write(data)
            f.flush()
            elf = run('readelf', '-l', '-d', f.name)
        assert '/lib/ld-linux' not in elf and 'libc.so.6' not in elf, f'{n} targets GNU/Linux, not Android'
    print('PASS: ARM64 only; all five real native runtimes present; ELF headers checked')

badging = run(tools / 'aapt', 'dump', 'badging', a.apk)
assert 'application-debuggable' not in badging, 'APK is debuggable'
assert "package: name='com.pixvpn.gram'" in badging, 'Wrong application ID'
assert "versionCode='30'" in badging and "versionName='2.9.1'" in badging, 'Wrong release version'
assert "application-label:'PixVPN'" in badging, 'Wrong app label'
print('PASS: PixVPN / com.pixvpn.gram / 2.9.1 (30)')
print('PASS: manifest is not debuggable')
signing = run(tools / 'apksigner', 'verify', '--verbose', '--print-certs', a.apk)
if a.expected_cert_sha256:
    expected = a.expected_cert_sha256.replace(':', '').lower()
    certs = re.findall(r'certificate SHA-256 digest:\s*([0-9a-fA-F]+)', signing)
    assert expected in [c.lower() for c in certs], 'Signer does not match owner certificate'
    print('PASS: signature verified and matches owner certificate')
else:
    print('PASS: signature verified (owner fingerprint not supplied)')
print('SHA256:', hashlib.file_digest(a.apk.open('rb'), 'sha256').hexdigest())
print('APK:', a.apk.resolve())
