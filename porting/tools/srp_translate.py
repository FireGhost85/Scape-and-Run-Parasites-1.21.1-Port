"""Draft translator: annotated 1.12.2 decompiled SRP source -> 1.21.1 NeoForge Java (Mojang names).

usage: python srp_translate.py <named_root> <out_root> [relative/path/File.java ...]
  <named_root> = folder containing com/dhanantry/scapeandrunparasites (output of annotate_names.py)
  <out_root>   = e.g. src/main/java (files are written to the same relative package, with package remaps)

The output is a DRAFT: it removes the mechanical 1.12 -> 1.21 differences (renames, field -> accessor calls, imports);
semantic differences (rendering, pathfinding internals, GL calls) are left with `/*TODO-PORT*/` markers or compile errors.
Collected per-class metadata (setSize, immune to fire) is written to <out_root>/../../../porting/spec/entity_meta.json.
"""
import json
import os
import re
import sys

from srp_rules import *  # noqa
from srp_rules2 import REGEX_RULES2

NAMED_ROOT = sys.argv[1]
OUT_ROOT = sys.argv[2]
ONLY = sys.argv[3:]
PKG_ROOT = 'com/dhanantry/scapeandrunparasites'
MCSRC = 'C:/srp_work/mcsrc'
META_PATH = os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', 'spec', 'entity_meta.json')

# ---------------------------------------------------------------- class index for imports
_index = {}


def build_index():
    prio = ['net/minecraft/world/', 'net/minecraft/core/', 'net/minecraft/network/', 'net/minecraft/util/', 'net/minecraft/nbt/',
            'net/minecraft/resources/', 'net/minecraft/sounds/', 'net/minecraft/tags/', 'net/minecraft/server/', 'net/minecraft/advancements/',
            'net/minecraft/', 'net/minecraft/client/']
    for dp, _, fs in os.walk(MCSRC + '/net/minecraft'):
        for f in fs:
            if not f.endswith('.java'):
                continue
            rel = os.path.relpath(os.path.join(dp, f), MCSRC).replace(os.sep, '/')[:-5]
            simple = f[:-5]
            fq = rel.replace('/', '.')
            # skip client / data / gametest packages unless nothing else
            weight = 9
            for i, p in enumerate(prio):
                if rel.startswith(p):
                    weight = i
                    break
            if rel.startswith('net/minecraft/client/') or rel.startswith('net/minecraft/data/') or rel.startswith('net/minecraft/gametest/'):
                weight = 20
            cur = _index.get(simple)
            if cur is None or weight < cur[0]:
                _index[simple] = (weight, fq)


build_index()
def build_project_index():
    root = os.path.join(NAMED_ROOT, 'com', 'dhanantry', 'scapeandrunparasites')
    for dp, _, fs in os.walk(root):
        for f in fs:
            if not f.endswith('.java'):
                continue
            rel = os.path.relpath(os.path.join(dp, f), NAMED_ROOT).replace(os.sep, '.')[:-5]
            rel = rel.replace('com.dhanantry.scapeandrunparasites.util.config.', 'com.dhanantry.scapeandrunparasites.config.')
            simple = f[:-5]
            if simple in ('SRPMain', 'SRPCoreMod'):
                continue
            _index[simple] = (-3, rel)


build_project_index()
for k, v in IMPORT_OVERRIDES.items():
    _index[k] = (-1, v)

JAVA_IMPORTS = {
    'List': 'java.util.List', 'ArrayList': 'java.util.ArrayList', 'Map': 'java.util.Map', 'HashMap': 'java.util.HashMap',
    'Set': 'java.util.Set', 'HashSet': 'java.util.HashSet', 'Arrays': 'java.util.Arrays', 'Objects': 'java.util.Objects',
    'Collections': 'java.util.Collections', 'Comparator': 'java.util.Comparator', 'Iterator': 'java.util.Iterator',
    'Collection': 'java.util.Collection', 'UUID': 'java.util.UUID', 'Locale': 'java.util.Locale', 'EnumSet': 'java.util.EnumSet',
    'Optional': 'java.util.Optional', 'Predicate': 'java.util.function.Predicate', 'Function': 'java.util.function.Function',
    'Consumer': 'java.util.function.Consumer', 'Supplier': 'java.util.function.Supplier', 'Nullable': 'javax.annotation.Nullable',
    'Nonnull': 'javax.annotation.Nonnull', 'Collectors': 'java.util.stream.Collectors', 'Stream': 'java.util.stream.Stream',
    'LinkedList': 'java.util.LinkedList', 'Random': 'java.util.Random', 'Iterable': None, 'Map.Entry': None,
}

# ---------------------------------------------------------------- de-SRG
SRG_RE = re.compile(r'\b((?:func|field)_\d+_[A-Za-z0-9_]+)(?:/\*([^*]*)\*/)?')


def desrg(text):
    def rep(m):
        srg, name = m.group(1), m.group(2)
        if srg in SRG_SPECIAL:
            return SRG_SPECIAL[srg]
        if name is None:
            return srg
        if '.' in name:
            # e.g. AxisAlignedBB.grow(double) / ItemStack.EMPTY / dataManager.get
            if name in DOTTED_SPECIAL:
                return DOTTED_SPECIAL[name]
            last = name.split('.')[-1]
            last = re.sub(r'\(.*\)$', '', last)
            return last
        return name
    return SRG_RE.sub(rep, text)


# ---------------------------------------------------------------- helpers
from srp_util import split_args, find_close, rewrite_calls  # noqa


# ---------------------------------------------------------------- main per-file translation
def translate(src, relpath, meta):
    # strip CFR header up to package
    i = src.find('package ')
    header_imports = src[:i]
    src = src[i:]
    src = desrg(src)
    text = src

    # package / import lines: handled separately
    lines = text.split('\n')
    pkg_line = lines[0]
    body_lines = []
    old_imports = []
    for ln in lines[1:]:
        if ln.startswith('import '):
            old_imports.append(ln)
        else:
            body_lines.append(ln)
    body = '\n'.join(body_lines)

    # remap project package names
    pkg = pkg_line.replace('package ', '').replace(';', '').strip()
    pkg = remap_pkg(pkg)

    # constants / macro rules
    body = apply_rules(body, meta, relpath)

    # imports
    imports = set()
    for imp in old_imports:
        fq = imp[len('import '):].rstrip(';').strip()
        if fq.startswith('com.dhanantry'):
            imports.add(remap_pkg(fq))
        elif fq.startswith('java.') or fq.startswith('javax.'):
            if fq in ('javax.annotation.Nonnull',):
                imports.add(fq)
            else:
                imports.add(fq)
    idents = set(re.findall(r'\b[A-Z][A-Za-z0-9_]*\b', body))
    for ident in sorted(idents):
        if ident in _index and ident not in DONT_IMPORT:
            fq = _index[ident][1]
            if fq and fq.rsplit('.', 1)[0] != pkg:
                imports.add(fq)
        elif ident in JAVA_IMPORTS and JAVA_IMPORTS[ident]:
            imports.add(JAVA_IMPORTS[ident])
    for k, v in EXTRA_IMPORT_TRIGGERS.items():
        if re.search(k, body):
            imports.add(v)
    # drop imports of classes that are no longer referenced / removed
    final = []
    for fq in sorted(imports):
        simple = fq.rsplit('.', 1)[-1]
        if fq.startswith('net.minecraftforge') or fq in REMOVED_IMPORTS or simple in REMOVED_SIMPLE:
            continue
        if re.search(r'\b' + re.escape(simple) + r'\b', body):
            final.append(fq)
    out = 'package ' + pkg + ';\n\n' + '\n'.join('import %s;' % f for f in final) + '\n' + body
    return out, pkg


def post_fix(body):
    # raw List declarations -> List<? extends T> using the first for-each over the variable
    def rawlist(m):
        name = m.group(2)
        fm = re.search(r'for \((\w+) \w+ : ' + re.escape(name) + r'\)', body[m.end():])
        if fm:
            return '%sList<? extends %s> %s%s' % (m.group(1), fm.group(1), name, m.group(3))
        return m.group(0)
    body = re.sub(r'(^|[\s(])List (\w+)(\s*[=;])', rawlist, body, flags=re.M)
    return body


def remap_pkg(s):
    s = s.replace('com.dhanantry.scapeandrunparasites.util.config.', 'com.dhanantry.scapeandrunparasites.config.')
    s = s.replace('com.dhanantry.scapeandrunparasites.util.config', 'com.dhanantry.scapeandrunparasites.config')
    return s


def apply_rules(body, meta, relpath):
    cls = os.path.basename(relpath)[:-5]
    # --- structural rewrites that need call parsing
    body = rewrite_structural(body, meta, cls)
    # --- type renames
    for old, new in TYPE_MAP.items():
        body = re.sub(r'(?<![\w.])' + re.escape(old) + r'\b', new, body)
    # --- plain regex rules
    for pat, rep in REGEX_RULES + REGEX_RULES2:
        body = re.sub(pat, rep, body)
    body = post_fix(body)
    # --- static constant maps
    for cls_name, (table, default_upper) in CONST_TABLES.items():
        def crep(m, table=table, default_upper=default_upper, cls_name=cls_name):
            n = m.group(1)
            if n in table:
                return table[n]
            return (cls_name + '.' + n.upper()) if default_upper else m.group(0)
        body = re.sub(r'\b' + cls_name + r'\.([A-Za-z_][A-Za-z0-9_]*)\b(?!\()', crep, body) if False else body
    return body


def rewrite_structural(body, meta, cls):
    from srp_struct import structural
    return structural(body, meta, cls, split_args, find_close, rewrite_calls)


def load_class_const():
    path = os.path.join(NAMED_ROOT, PKG_ROOT, 'init', 'SRPEntities.java')
    txt = open(path, encoding='utf8', errors='replace').read()
    for m in re.finditer(r'Create\w+\("(\w+)",\s*(\w+)\.class', txt):
        CLASS_CONST[m.group(2)] = m.group(1).upper()


def main():
    load_class_const()
    meta_all = {}
    if os.path.exists(META_PATH):
        meta_all = json.load(open(META_PATH, encoding='utf8'))
    files = []
    if ONLY:
        files = ONLY
    else:
        for dp, _, fs in os.walk(os.path.join(NAMED_ROOT, PKG_ROOT)):
            for f in fs:
                if f.endswith('.java'):
                    files.append(os.path.relpath(os.path.join(dp, f), os.path.join(NAMED_ROOT, PKG_ROOT)).replace(os.sep, '/'))
    for rel in files:
        path = os.path.join(NAMED_ROOT, PKG_ROOT, rel)
        src = open(path, encoding='utf8', errors='replace').read()
        meta = {}
        out, pkg = translate(src, rel, meta)
        newrel = remap_pkg(rel.replace('/', '.')).replace('.', '/')
        newrel = rel  # same layout, except util/config -> config
        if rel.startswith('util/config/'):
            newrel = 'config/' + rel[len('util/config/'):]
        dest = os.path.join(OUT_ROOT, PKG_ROOT, newrel)
        os.makedirs(os.path.dirname(dest), exist_ok=True)
        open(dest, 'w', encoding='utf8').write(out)
        if meta:
            meta_all[os.path.basename(rel)[:-5]] = meta
    json.dump(meta_all, open(META_PATH, 'w', encoding='utf8'), indent=1, sort_keys=True)
    print('translated', len(files), 'files')


if __name__ == '__main__':
    main()
