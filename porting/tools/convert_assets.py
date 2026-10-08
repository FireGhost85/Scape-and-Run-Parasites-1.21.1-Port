"""Copy and convert the static client assets of the 1.12.2 jar to the 1.21.1 layout.

usage: python convert_assets.py <extracted_jar_root> <project_root>

Does: textures (blocks->block, items->item, rest unchanged, .mcmeta alongside), block and item models (texture and parent
renames, 1.12 vanilla parents -> 1.21 parents, forge:obj -> neoforge:obj), sounds (.ogg), sounds.json, logo.
Blockstates, lang, recipes, advancements, loot tables and structures have their own converters.
"""
import json
import os
import re
import shutil
import sys

src_root, proj = sys.argv[1], sys.argv[2]
src = os.path.join(src_root, 'assets', 'srparasites')
dst = os.path.join(proj, 'src', 'main', 'resources', 'assets', 'srparasites')

PARENT_RENAMES = {
    'block/half_slab': 'block/slab',
    'block/upper_slab': 'block/slab_top',
    'block/wall_post': 'block/template_wall_post',
    'block/wall_side': 'block/template_wall_side',
    'block/glass_pane_post': 'block/template_glass_pane_post',
    'block/glass_pane_side': 'block/template_glass_pane_side',
    'block/glass_pane_side_alt': 'block/template_glass_pane_side_alt',
    'block/glass_pane_noside': 'block/template_glass_pane_noside',
    'block/glass_pane_noside_alt': 'block/template_glass_pane_noside_alt',
    'block/glass_pane_post_ends': 'block/template_glass_pane_post',
}


def fix_ref(s):
    if not isinstance(s, str):
        return s
    if s.startswith('#'):
        return s
    ns, _, path = s.rpartition(':') if ':' in s else ('', '', s)
    ns = ns.lower()
    path = re.sub(r'^blocks/', 'block/', path)
    path = re.sub(r'^items/', 'item/', path)
    if ns in ('', 'minecraft'):
        path = PARENT_RENAMES.get(path, path)
    return (ns + ':' + path) if ns else path


def fix_model(j):
    if 'parent' in j:
        j['parent'] = fix_ref(j['parent'])
    if 'textures' in j:
        j['textures'] = {k: fix_ref(v) for k, v in j['textures'].items()}
    if j.get('loader') == 'forge:obj':
        j['loader'] = 'neoforge:obj'
    return j


def copy_file(a, b):
    os.makedirs(os.path.dirname(b), exist_ok=True)
    shutil.copy2(a, b)


counts = {'textures': 0, 'models': 0, 'sounds': 0, 'other': 0}

# textures
tex = os.path.join(src, 'textures')
for dp, _, files in os.walk(tex):
    for f in files:
        rel = os.path.relpath(os.path.join(dp, f), tex).replace(os.sep, '/')
        rel = re.sub(r'^blocks/', 'block/', rel)
        rel = re.sub(r'^items/', 'item/', rel)
        copy_file(os.path.join(dp, f), os.path.join(dst, 'textures', rel.lower() if False else rel))
        counts['textures'] += 1

# models (block + item)
for sub in ('block', 'item'):
    d = os.path.join(src, 'models', sub)
    for dp, _, files in os.walk(d):
        for f in files:
            a = os.path.join(dp, f)
            rel = os.path.relpath(a, os.path.join(src, 'models')).replace(os.sep, '/')
            b = os.path.join(dst, 'models', rel)
            if f.endswith('.json'):
                with open(a, encoding='utf8') as fh:
                    text = fh.read()
                os.makedirs(os.path.dirname(b), exist_ok=True)
                try:
                    j = fix_model(json.loads(text, strict=False))
                    text = json.dumps(j, indent=2)
                except ValueError as e:
                    print('lenient-text fallback:', rel, e)
                    text = re.sub(r'(?i)"(srparasites:)?blocks/', lambda m: '"' + (m.group(1) or '') + 'block/', text)
                    text = re.sub(r'(?i)"(srparasites:)?items/', lambda m: '"' + (m.group(1) or '') + 'item/', text)
                with open(b, 'w', encoding='utf8') as fh:
                    fh.write(text)
                counts['models'] += 1
            else:
                copy_file(a, b)
                counts['other'] += 1

# loose model files at models/ root (e.g. lodo.json is an entity model description, copy as-is)
for f in os.listdir(os.path.join(src, 'models')):
    p = os.path.join(src, 'models', f)
    if os.path.isfile(p):
        copy_file(p, os.path.join(dst, 'models', f))
        counts['other'] += 1

# sounds
sd = os.path.join(src, 'sounds')
for dp, _, files in os.walk(sd):
    for f in files:
        a = os.path.join(dp, f)
        copy_file(a, os.path.join(dst, 'sounds', os.path.relpath(a, sd)))
        counts['sounds'] += 1

for f in ('sounds.json', 'logo2.png'):
    if os.path.exists(os.path.join(src, f)):
        copy_file(os.path.join(src, f), os.path.join(dst, f))
        counts['other'] += 1

print(counts)
