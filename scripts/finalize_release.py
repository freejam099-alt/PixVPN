#!/usr/bin/env python3
"""Validate one explicit PixVPN APK and prepare its delivery copy, without rebuilding."""
from pathlib import Path
import argparse
import hashlib
import shutil
import subprocess
import sys

p = argparse.ArgumentParser()
p.add_argument('apk', type=Path)
p.add_argument('--expected-cert-sha256', required=True)
a = p.parse_args()
root = Path(__file__).resolve().parents[1]
subprocess.run([sys.executable, str(root / 'scripts/verify_release.py'), str(a.apk),
                '--expected-cert-sha256', a.expected_cert_sha256], check=True)
out = root / 'out'
out.mkdir(exist_ok=True)
target = out / 'PixVPN-2.9-arm64-v8a.apk'
shutil.copy2(a.apk, target)
digest = hashlib.file_digest(target.open('rb'), 'sha256').hexdigest()
target.with_suffix('.apk.sha256').write_text(f'{digest}  {target.name}\n')
print('READY:', target, target.stat().st_size, 'bytes')
