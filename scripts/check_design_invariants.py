#!/usr/bin/env python3
"""Static design checks, not screenshots, TalkBack, frame-time or device tests."""
from pathlib import Path
import re
from xml.etree import ElementTree as ET

root = Path(__file__).resolve().parents[1]
res = root / 'app/src/main/res'
xmls = list(res.rglob('*.xml'))
for path in xmls:
    ET.parse(path)
print(f'PASS: {len(xmls)} XML resources parsed')
for directory in (res / 'values', res / 'values-fa'):
    keys = []
    for path in directory.glob('*.xml'):
        keys += [(node.tag, node.attrib['name']) for node in ET.parse(path).getroot() if 'name' in node.attrib]
    assert len(keys) == len(set(keys)), f'Duplicate resources: {directory.name}'
print('PASS: no duplicate default/Persian resources')

def luminance(value):
    channels = [int(value[i:i+2], 16) / 255 for i in (0, 2, 4)]
    linear = [c / 12.92 if c <= 0.04045 else ((c + 0.055) / 1.055) ** 2.4 for c in channels]
    return sum(c * w for c, w in zip(linear, (0.2126, 0.7152, 0.0722)))

theme = (root / 'app/src/main/java/app/vpn8/Theme.kt').read_text()
for mode, end in [('Dark', 'private val Light'), ('Light', 'private val AppShapes')]:
    section = theme.split(f'private val {mode} =', 1)[1].split(end, 1)[0]
    colors = dict(re.findall(r'(\w+)\s*=\s*Color\(0xFF([0-9A-Fa-f]{6})\)', section))
    colors.update({name: 'FFFFFF' for name in re.findall(r'(\w+)\s*=\s*Color.White', section)})
    for foreground, background in [('onPrimary', 'primary'), ('onSecondary', 'secondary'),
                                  ('onPrimaryContainer', 'primaryContainer'),
                                  ('onSecondaryContainer', 'secondaryContainer'),
                                  ('onTertiary', 'tertiary'), ('onSurface', 'surface'),
                                  ('onSurfaceVariant', 'surfaceVariant'), ('onError', 'error')]:
        l1, l2 = sorted((luminance(colors[foreground]), luminance(colors[background])))
        ratio = (l2 + .05) / (l1 + .05)
        assert ratio >= 4.5, f'{mode} {foreground}/{background} contrast {ratio:.2f}:1'
        print(f'PASS: {mode} {foreground}/{background} {ratio:.2f}:1')
print('These checks cover static palette pairs only, not every composited screen or dynamic wallpaper palette.')
