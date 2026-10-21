#!/usr/bin/env python3
"""Source/control-flow checks; NOT Android, VPN, JNI, or native runtime tests."""
from pathlib import Path
import re
import sys

ROOT = Path(__file__).resolve().parents[1]
TUNNEL = ROOT / "app/src/main/java/app/vpn8/TunnelService.kt"
CORE = ROOT / "app/src/main/java/app/vpn8/CoreProcess.kt"
SOCKS = ROOT / "app/src/main/java/app/vpn8/Socks.kt"

def check(condition, message):
    if not condition:
        raise AssertionError(message)

def main():
    tunnel = TUNNEL.read_text(encoding="utf-8")
    core = CORE.read_text(encoding="utf-8")
    socks = SOCKS.read_text(encoding="utf-8")

    include_guard = 'if (s.splitMode == "include" && apps.isEmpty())'
    check(include_guard in tunnel, "missing empty include-mode fail-closed guard")
    guard_at = tunnel.index(include_guard)
    establish_at = tunnel.index("b.establish()", guard_at)
    check(guard_at < establish_at, "include empty-set guard occurs after establish")
    include_start = tunnel.index('if (s.splitMode == "include" && apps.isNotEmpty())')
    include_end = tunnel.index("} else {", include_start)
    include = tunnel[include_start:include_end]
    check("addAllowedApplication(app)" in include, "include packages are not individually validated")
    check("catch (e: Exception)" in include and "return false" in include, "invalid include package is not fail-closed")
    check("runCatching" not in include, "include package exception is swallowed")

    restart_start = tunnel.index("Stops and restarts hev")
    teardown_start = tunnel.index("// --------------------------------------------------------------- teardown")
    restart = tunnel[restart_start:teardown_start]
    check("hevOn = false" in restart, "HEV restart does not clear state before restart")
    check("hevRestartFailed = true" in restart, "HEV restart failure marker missing")
    run_failure = tunnel.index("if (hevRestartFailed)")
    check("stopTunSide()" in tunnel[run_failure:run_failure + 500], "failed HEV restart does not tear down TUN")
    attempt = tunnel.index("val attempt = failures + 1")
    check("hevRestartFailed = false" in tunnel[attempt:tunnel.index("Engine.log", attempt)], "HEV failure flag is not reset per attempt")

    check('Engine.log("exec: aether ${args.joinToString' not in core, "CoreProcess logs secret arguments")
    check("arguments redacted" in core, "redacted CoreProcess log missing")
    check("@Volatile private var proc" in core, "native process handle is not safely published")
    check("private var sessionGeneration" in tunnel, "session generation guard missing")
    check("fetchExit(port, generation)" in tunnel and "checkSpeed(port, generation)" in tunnel,
          "background health jobs are not tied to the connection session")
    check("val deadline = t0 + budgetMs" in socks and "remainingTimeout()" in socks,
          "download health-check budget does not cover socket reads")
    print("PASS: runtime source invariants (not Android/VPN runtime tests)")

if __name__ == "__main__":
    try:
        main()
    except (AssertionError, FileNotFoundError) as exc:
        print(f"FAIL: {exc}", file=sys.stderr)
        raise SystemExit(1)
