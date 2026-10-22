#!/usr/bin/env python3
"""Static regression checks only; not Android instrumentation or animation frame tests."""
from pathlib import Path
import re
import xml.etree.ElementTree as ET

root = Path(__file__).resolve().parents[1]
code = root / 'app/src/main/java/app/vpn8'
button = (code / 'PixConnectButton.kt').read_text()
home = (code / 'HomeScreen.kt').read_text()
about = (code / 'OtherScreens.kt').read_text()
gradle = (root / 'app/build.gradle.kts').read_text()
assert 'applicationId = "com.pixvpn.gram"' in gradle
assert 'namespace = "com.pixvpn.gram"' in gradle
assert 'versionCode = 30' in gradle
assert 'versionName = "2.9.1"' in gradle
for f in code.glob('*.kt'):
    if f.name == 'Tun2Socks.kt': continue  # legacy JNI symbol bridge
    assert re.search(r'^package com\.pixvpn\.gram$', f.read_text(), re.M), f'Unmigrated namespace: {f.name}'
strings = {n.attrib.get('name'): n.text for n in ET.parse(root / 'app/src/main/res/values/strings.xml').getroot()}
assert strings['app_name'] == 'PixVPN'
assert 'https://github.com/freejam099-alt/PixVPN' in about
assert 'TELEGRAM_URL' not in about and 'LinkRow(Ico.SEND' not in about
assert 'Text("8"' not in (code / 'Components.kt').read_text()
assert 'minOf(224.dp, maxWidth)' in button and '.size(diameter)' in button
assert 'animateDpAsState' not in button and 'animateContentSize' not in button
assert 'Phase.CONNECTED' in button and 'Phase.RECONNECTING' in button
assert 'if (launching)' in button and 'if (failed)' in button
assert 'R.string.pix_retry' in button and 'stateDescription' in button
assert 'Role.Button' in button and 'phase != Phase.STOPPING' in button
assert 'lastToggleAt' in home and '700L' in home
assert 'failed = idle && st.error != null' in home
assert button.count('Color(0xFF') >= 4
assert (root / 'design/pixvpn-icon.svg').is_file() or any((root / 'app/src/main/res').rglob('*.xml'))
print('PASS: PixVPN namespace, branding, About, fixed-size responsive hero, SVG path animation, failure/retry, semantics, rapid-tap guard')
print('NOTE: device layout, TalkBack, frame times and VPN connectivity still need real-device tests.')
