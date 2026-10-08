"""Generates porting/spec/blocks_registry.json from the registry (SRPBlocks.java) and the annotated original sources.

usage: python gen_blocks_spec.py <named_src_root> [project_root]
<named_src_root> is the folder with com/dhanantry/scapeandrunparasites (annotated CFR output of the original jar).

For every registered block: registry name, port class, original class, harvest tool and level (setHarvestLevel in the class chain
of the original and in the original registry), whether it has a block item and the variant list. The data step (tags, loot
tables, models) reads this file.
"""
import json
import os
import re
import sys

named = sys.argv[1]
root = sys.argv[2] if len(sys.argv) > 2 else os.path.join(os.path.dirname(__file__), '..', '..')
blocks_dir = os.path.join(named, 'com', 'dhanantry', 'scapeandrunparasites', 'block')
srp_blocks_old = os.path.join(named, 'com', 'dhanantry', 'scapeandrunparasites', 'init', 'SRPBlocks.java')
srp_blocks_new = os.path.join(root, 'src', 'main', 'java', 'com', 'dhanantry', 'scapeandrunparasites', 'init', 'SRPBlocks.java')

ANN = re.compile(r'(?:func|field)_\d+_\w+/\*([A-Za-z_0-9]+)\*/')


def read(p):
    return ANN.sub(lambda m: m.group(1), open(p, encoding='utf8', errors='ignore').read())


# --- original class chain harvest info
old_src = {}
for dp, _, fs in os.walk(blocks_dir):
    for f in fs:
        if f.endswith('.java'):
            old_src[f[:-5]] = read(os.path.join(dp, f))


def chain(cls):
    out = []
    while cls in old_src:
        out.append(cls)
        m = re.search(r'class\s+' + re.escape(cls) + r'\s*(?:\n\s*)?extends\s+(\w+)', old_src[cls])
        if not m:
            break
        cls = m.group(1)
    return out


def harvest_of(cls):
    for c in chain(cls):
        m = re.search(r'setHarvestLevel\("(\w+)",\s*(-?\d+)\)', old_src[c])
        if m:
            return m.group(1), int(m.group(2))
    return None, None


# --- original registry: explicit harvest calls on fields (walls) and slab parameters
old_reg = read(srp_blocks_old)
field_harvest = {}
for m in re.finditer(r'(\w+)\.setHarvestLevel\("(\w+)",\s*(-?\d+)\)', old_reg):
    field_harvest[m.group(1)] = (m.group(2), int(m.group(3)))
slab_params = {}
for m in re.finditer(r'(\w+) = new BlockHarleskinnSlab\((?:true|false), "(\w+)", "(\w+)", (\d+), ([\w.]+), ([\d.]+)f, ([\d.]+)f, (?:true|false)', old_reg):
    slab_params[m.group(2)] = (m.group(3), int(m.group(4)))

# --- new registry
new_src = open(srp_blocks_new, encoding='utf8').read()
entries = []
field_re = re.compile(r'public static final DeferredBlock<(\w+)> (\w+) = (\w+)\((.*?)\);\n', re.S)
for m in field_re.finditer(new_src):
    typ, field, fn, args = m.groups()
    name_m = re.match(r'\s*"([^"]+)"', args)
    if not name_m:
        continue
    name = name_m.group(1)
    cls_m = re.search(r'new (\w+)\(', args)
    cls = cls_m.group(1) if cls_m else None
    if cls is None:
        mm = re.search(r',\s*(\w+)::new', args)
        cls = mm.group(1) if mm else None
    if fn == 'slab':
        cls = 'BlockHarleskinnSlab'
    if fn == 'harleskinnStairs':
        cls = 'BlockHarleskinnStairs'
    if fn == 'rubbleStairs':
        cls = 'BlockStairBase'
    variants = None
    vm = re.search(r'(\w+(?:\.\w+)?)\.EnumType\.values\(\)', args)
    if fn == 'regVariants' and vm:
        variants = vm.group(0).replace('.values()', '')
    entries.append({'name': name, 'field': field, 'registrar': fn, 'class': cls, 'variants': variants})

out = []
for e in entries:
    cls = e['class']
    tool, level = harvest_of(cls) if cls else (None, None)
    if e['field'] in field_harvest:
        tool, level = field_harvest[e['field']]
    if e['name'] in slab_params:
        tool, level = slab_params[e['name']]
    if cls in ('BlockSlabRubble',):
        tool, level = 'pickaxe', 1
    if cls in ('BlockSlabStain',):
        tool, level = 'shovel', 0
    e['tool'] = tool
    e['level'] = level
    e['item'] = e['registrar'] != 'regNoItem'
    out.append(e)

path = os.path.join(root, 'porting', 'spec', 'blocks_registry.json')
with open(path, 'w', encoding='utf8') as f:
    json.dump(out, f, indent=1)
print(len(out), 'blocks;', sum(1 for e in out if e['tool']), 'with a harvest tool')
