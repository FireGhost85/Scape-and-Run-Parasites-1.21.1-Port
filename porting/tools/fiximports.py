"""Adds missing imports (and removes unused ones) in Java files of the port.

usage: python fiximports.py <file.java> [<file.java> ...]      or:  python fiximports.py --dir <folder>
Resolution order for a simple class name: java/javax table -> project classes (src/main/java) -> IMPORT_OVERRIDES ->
net.minecraft sources index (C:/srp_work/mcsrc) -> net.neoforged jar index (sources jar).
"""
import os
import re
import sys
import zipfile

from srp_rules import IMPORT_OVERRIDES

HERE = os.path.dirname(os.path.abspath(__file__))
PROJ = os.path.normpath(os.path.join(HERE, '..', '..'))
SRC = os.path.join(PROJ, 'src', 'main', 'java')
MCSRC = 'C:/srp_work/mcsrc'
NEO_SOURCES = os.path.join(PROJ, 'build', 'moddev', 'artifacts', 'neoforge-21.1.256-sources.jar')

JAVA = {
    'List': 'java.util.List', 'ArrayList': 'java.util.ArrayList', 'Map': 'java.util.Map', 'HashMap': 'java.util.HashMap',
    'LinkedHashMap': 'java.util.LinkedHashMap', 'Set': 'java.util.Set', 'HashSet': 'java.util.HashSet', 'Arrays': 'java.util.Arrays',
    'Objects': 'java.util.Objects', 'Collections': 'java.util.Collections', 'Comparator': 'java.util.Comparator',
    'Iterator': 'java.util.Iterator', 'Collection': 'java.util.Collection', 'UUID': 'java.util.UUID', 'Locale': 'java.util.Locale',
    'EnumSet': 'java.util.EnumSet', 'Optional': 'java.util.Optional', 'Predicate': 'java.util.function.Predicate',
    'Function': 'java.util.function.Function', 'Consumer': 'java.util.function.Consumer', 'Supplier': 'java.util.function.Supplier',
    'BiConsumer': 'java.util.function.BiConsumer', 'BooleanSupplier': 'java.util.function.BooleanSupplier',
    'Nullable': 'javax.annotation.Nullable', 'Nonnull': 'javax.annotation.Nonnull', 'Collectors': 'java.util.stream.Collectors',
    'LinkedList': 'java.util.LinkedList', 'Random': 'java.util.Random', 'Deque': 'java.util.ArrayDeque', 'ArrayDeque': 'java.util.ArrayDeque',
    'IOException': 'java.io.IOException', 'File': 'java.io.File', 'Stream': 'java.util.stream.Stream', 'TreeMap': 'java.util.TreeMap',
    'Queue': 'java.util.Queue', 'Iterable': None, 'Entry': None,
}
PREFER = ['net/minecraft/world/', 'net/minecraft/core/', 'net/minecraft/network/', 'net/minecraft/util/', 'net/minecraft/nbt/',
          'net/minecraft/resources/', 'net/minecraft/sounds/', 'net/minecraft/tags/', 'net/minecraft/server/', 'net/minecraft/advancements/',
          'net/minecraft/', 'net/minecraft/client/']
NEO_PREFER = ['net/neoforged/neoforge/event/', 'net/neoforged/neoforge/common/', 'net/neoforged/neoforge/', 'net/neoforged/bus/', 'net/neoforged/']

_index = {}
_proj = {}


def build():
    for dp, _, fs in os.walk(MCSRC + '/net/minecraft'):
        for f in fs:
            if not f.endswith('.java'):
                continue
            rel = os.path.relpath(os.path.join(dp, f), MCSRC).replace(os.sep, '/')[:-5]
            w = 9
            for i, p in enumerate(PREFER):
                if rel.startswith(p):
                    w = i
                    break
            if rel.startswith(('net/minecraft/client/', 'net/minecraft/data/', 'net/minecraft/gametest/')):
                w = 20
            name = f[:-5]
            cur = _index.get(name)
            if cur is None or w < cur[0]:
                _index[name] = (w, rel.replace('/', '.'))
    try:
        z = zipfile.ZipFile(NEO_SOURCES)
        for n in z.namelist():
            if n.startswith('net/neoforged/') and n.endswith('.java') and 'package-info' not in n:
                name = n.rsplit('/', 1)[-1][:-5]
                w = 30
                for i, p in enumerate(NEO_PREFER):
                    if n.startswith(p):
                        w = 30 + i
                        break
                if name not in _index or w < _index[name][0]:
                    if name in _index and _index[name][0] < 30:
                        continue
                    _index[name] = (w, n[:-5].replace('/', '.'))
    except OSError:
        pass
    for dp, _, fs in os.walk(SRC):
        for f in fs:
            if f.endswith('.java'):
                rel = os.path.relpath(os.path.join(dp, f), SRC).replace(os.sep, '.')[:-5]
                _proj.setdefault(f[:-5], []).append(rel)


build()


def resolve(name, pkg):
    if name in JAVA:
        return JAVA[name]
    if name in _proj:
        c = _proj[name]
        for fq in c:
            if fq.rsplit('.', 1)[0] == pkg:
                return None
        return c[0]
    if name in IMPORT_OVERRIDES:
        return IMPORT_OVERRIDES[name]
    if name in _index:
        return _index[name][1]
    return None


def strip(text):
    text = re.sub(r'/\*.*?\*/', ' ', text, flags=re.S)
    text = re.sub(r'//[^\n]*', ' ', text)
    text = re.sub(r'"(?:\\.|[^"\\])*"', '""', text)
    return text


def fix(path):
    src = open(path, encoding='utf8').read()
    m = re.search(r'^package ([\w.]+);', src, flags=re.M)
    pkg = m.group(1)
    body_start = src.index(';', m.end() - 1) + 1
    imports = re.findall(r'^import (static )?([\w.*]+);\s*$', src, flags=re.M)
    kept_static = [i for i in imports if i[0]]
    plain = [i[1] for i in imports if not i[0]]
    code = re.sub(r'^import [^\n]*\n', '', src[body_start:], flags=re.M)
    clean = strip(code)
    # declared names (classes / enums / interfaces / records / type params) inside the file
    declared = set(re.findall(r'\b(?:class|interface|enum|record)\s+([A-Z]\w*)', clean))
    declared |= set(re.findall(r'<\s*([A-Z]\w?)\s*(?:extends|,|>)', clean))
    used = set(re.findall(r'(?<![\w.])([A-Z][A-Za-z0-9_]*)\b', clean))
    # a name used only as a member access after a dot is not a class use; the regex already skips dotted
    have = {i.rsplit('.', 1)[-1]: i for i in plain}
    new = {}
    for name in sorted(used):
        if name in have or name in declared:
            continue
        r = resolve(name, pkg)
        if r:
            new[name] = r
    final = dict(have)
    final.update(new)
    # drop unused
    for name in list(final):
        if name == '*' or final[name].endswith('.*'):
            continue
        if not re.search(r'(?<![\w.])' + re.escape(name) + r'\b', clean):
            del final[name]
    lines = sorted(set(final.values()))
    out = src[:m.start()] + 'package %s;\n\n' % pkg + ''.join('import %s;\n' % l for l in lines)
    out += ''.join('import static %s;\n' % i[1] for i in kept_static)
    out += '\n' + code.lstrip('\n')
    if out != src:
        open(path, 'w', encoding='utf8').write(out)
    return sorted(new)


if __name__ == '__main__':
    args = sys.argv[1:]
    files = []
    if args and args[0] == '--dir':
        for dp, _, fs in os.walk(args[1]):
            for f in fs:
                if f.endswith('.java'):
                    files.append(os.path.join(dp, f))
    else:
        files = args
    for f in files:
        added = fix(f)
        if added:
            print(os.path.basename(f), '+', ','.join(added))
