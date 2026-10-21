#!/usr/bin/env bash
set -euo pipefail
ROOT=$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)
cd "$ROOT"
for name in libaether.so libpsiphon.so liblyrebird.so libhev-socks5-tunnel.so; do
  test -s "app/src/main/jniLibs/arm64-v8a/$name" || { echo "ERROR: missing real ARM64 runtime: $name" >&2; exit 1; }
done
test -s app/libs/libbox.aar || { echo 'ERROR: missing real app/libs/libbox.aar' >&2; exit 1; }
for name in KEYSTORE_FILE KEYSTORE_PASSWORD KEY_ALIAS KEY_PASSWORD; do
  test -n "${!name:-}" || { echo "ERROR: missing signing environment variable: $name (no debug fallback)" >&2; exit 1; }
done
test -s "$KEYSTORE_FILE" || { echo 'ERROR: keystore file is missing' >&2; exit 1; }
mkdir -p verification
./gradlew :app:assembleRelease --no-daemon --console=plain --stacktrace 2>&1 | tee verification/assemble-release.log
