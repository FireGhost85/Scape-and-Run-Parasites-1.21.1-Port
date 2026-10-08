"""Copies selected static methods verbatim out of annotated decompiled classes into a new Java utility class.

usage: python extract_methods.py <named_root> <out_file> <package> <ClassName> <src.java:method1,method2> [<src2.java:m3>...]
Only for pure data tables (switch statements over config fields) that need no translation.
"""
import re
import sys

named, out_file, pkg, cls = sys.argv[1:5]
specs = sys.argv[5:]
blocks = []
imports = set()
for spec in specs:
    path, names = spec.split(':')
    src = open(named + '/com/dhanantry/scapeandrunparasites/' + path, encoding='utf8', errors='replace').read()
    for im in re.findall(r'^import (com\.dhanantry[^;]+);', src, flags=re.M):
        imports.add(im.replace('util.config.', 'config.'))
    for n in names.split(','):
        m = re.search(r'\n    public static [\w\[\]<>]+ ' + n + r'\(', src)
        if not m:
            raise SystemExit('method not found: ' + n)
        i = src.index('{', m.end())
        depth = 0
        j = i
        while j < len(src):
            if src[j] == '{':
                depth += 1
            elif src[j] == '}':
                depth -= 1
                if depth == 0:
                    break
            j += 1
        blocks.append(src[m.start() + 1:j + 1])
out = 'package %s;\n\n' % pkg
for im in sorted(imports):
    if im.split('.')[-1] in ('SRPConfigSystems', 'SRPConfig', 'SRPConfigWorld', 'SRPConfigMobs'):
        out += 'import %s;\n' % im
out += '\n/** Static tables copied verbatim from the 1.12.2 command classes (data only). */\npublic final class %s {\n    private %s() {}\n\n' % (cls, cls)
out += '\n\n'.join(blocks) + '\n}\n'
open(out_file, 'w', encoding='utf8').write(out)
print('wrote', out_file, len(blocks), 'methods')
