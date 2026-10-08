"""Converts the 1.12 recipes of the original jar to 1.21 recipe JSONs (data/srparasites/recipe).

usage: python convert_recipes.py [jar_recipes_dir]
Item ids are resolved against the registered items (parsed from SRPBlocks / SRPItems); metadata of variant blocks becomes the
variant item <block>_<variant>. Unresolved ids are printed and the recipe is skipped.
"""
import glob
import json
import os
import re
import sys

ROOT = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
SRC = os.path.join(ROOT, 'src/main/java/com/dhanantry/scapeandrunparasites')
OUT = os.path.join(ROOT, 'src/main/resources/data/srparasites/recipe')
JAR = sys.argv[1] if len(sys.argv) > 1 else 'C:/srp_work/jarfull/assets/srparasites/recipes'

blocks = open(os.path.join(SRC, 'init/SRPBlocks.java'), encoding='utf8').read()
items_src = open(os.path.join(SRC, 'init/SRPItems.java'), encoding='utf8').read()

simple = set(n for f, n in re.findall(r'=\s*(\w+)\("([^"]+)"', blocks) if f not in ('regNoItem', 'regVariants'))
simple |= set(re.findall(r'\bITEMS\.register\("([^"]+)"', items_src))
variants = {}
for name, rest in re.findall(r'regVariants\("([^"]+)"(.*?\.values\(\))\);', blocks, re.S):
    cls = re.findall(r'(\w+)\.EnumType\.values\(\)', rest)[-1]
    text = open(glob.glob(os.path.join(SRC, 'block', '**', cls + '.java'), recursive=True)[0], encoding='utf8').read()
    body = re.search(r'enum EnumType[^{]*\{(.*?);', text, re.S).group(1)
    variants[name] = [c.strip().split('(')[0].lower() for c in body.split(',') if c.strip()]

DYE = ['black', 'red', 'green', 'brown', 'blue', 'purple', 'cyan', 'light_gray', 'gray', 'pink', 'lime', 'yellow', 'light_blue', 'magenta', 'orange', 'white']
COLORS = ['white', 'orange', 'magenta', 'light_blue', 'yellow', 'lime', 'pink', 'gray', 'light_gray', 'cyan', 'purple', 'blue', 'brown', 'green', 'red', 'black']
VANILLA = {'web': 'cobweb', 'hardened_clay': 'terracotta', 'sea_lantern': 'sea_lantern', 'stone': 'stone'}
ORE = {'plankWood': {'tag': 'minecraft:planks'}, 'plankParasiteWood': {'tag': 'srparasites:parasite_planks'}, 'blockGlass': {'tag': 'c:glass_blocks'}}
errors = []


def vanilla(path, data):
    if path == 'dye':
        return {'item': 'minecraft:' + {'black': 'ink_sac', 'brown': 'cocoa_beans', 'blue': 'lapis_lazuli', 'white': 'bone_meal'}.get(DYE[data or 0], DYE[data or 0] + '_dye')}
    if path == 'stonebrick':
        return {'item': 'minecraft:' + ['stone_bricks', 'mossy_stone_bricks', 'cracked_stone_bricks', 'chiseled_stone_bricks'][data or 0]}
    if path == 'sapling':
        return {'tag': 'minecraft:saplings'}
    if path == 'fish':
        return [{'item': 'minecraft:' + n} for n in ('cod', 'salmon', 'tropical_fish', 'pufferfish')]
    if path == 'stained_glass':
        return [{'item': 'minecraft:%s_stained_glass' % c} for c in COLORS]
    if path == 'stone' and data not in (None, 0):
        return {'item': 'minecraft:' + ['stone', 'granite', 'polished_granite', 'diorite', 'polished_diorite', 'andesite', 'polished_andesite'][data]}
    return {'item': 'minecraft:' + VANILLA.get(path, path)}


def resolve(ing):
    """1.12 ingredient object -> 1.21 ingredient (object or list)."""
    if isinstance(ing, list):
        out = []
        for i in ing:
            r = resolve(i)
            out += r if isinstance(r, list) else [r]
        return out
    if 'ore' in ing:
        ore = ing['ore']
        if ore in ORE:
            return ORE[ore]
        if ore.startswith('dye'):
            return {'tag': 'c:dyes/' + re.sub(r'(?<!^)([A-Z])', r'_\1', ore[3:]).lower()}
        errors.append('ore ' + ore)
        return {'item': 'minecraft:air'}
    ns, path = ing['item'].split(':')
    data = ing.get('data')
    if ns == 'minecraft':
        return vanilla(path, data)
    if path in variants:
        if data in (None, 32767):
            return [{'item': 'srparasites:%s_%s' % (path, v)} for v in variants[path]]
        if 'slab' in path and data >= 8:
            data -= 8
        if data >= len(variants[path]):
            # the original recipe gives an invalid meta; the block falls back to its default state (first variant)
            print('note: %s data %s is out of range, using the first variant' % (path, data))
            data = 0
        return {'item': 'srparasites:%s_%s' % (path, variants[path][data])}
    if path in simple:
        return {'item': 'srparasites:' + path}
    errors.append('unknown item ' + ing['item'])
    return {'item': 'minecraft:air'}


def result(res):
    r = resolve({'item': res['item'], 'data': res.get('data')})
    if isinstance(r, list) or 'item' not in r:
        errors.append('bad result ' + json.dumps(res))
        return {'id': 'minecraft:air', 'count': 1}
    out = {'id': r['item']}
    if res.get('count', 1) != 1:
        out['count'] = res['count']
    return out


os.makedirs(OUT, exist_ok=True)
done = 0
for f in sorted(glob.glob(os.path.join(JAR, '*.json'))):
    d = json.load(open(f, encoding='utf8'))
    errors.clear()
    typ = d['type'].split(':')[1]
    out = {}
    if typ in ('crafting_shaped', 'ore_shaped'):
        out['type'] = 'minecraft:crafting_shaped'
        if 'group' in d:
            out['group'] = d['group']
        out['pattern'] = d['pattern']
        out['key'] = {k: resolve(v) for k, v in d['key'].items()}
    else:
        out['type'] = 'minecraft:crafting_shapeless'
        if 'group' in d:
            out['group'] = d['group']
        out['ingredients'] = [resolve(i) for i in d['ingredients']]
    out['result'] = result(d['result'])
    if errors:
        print(os.path.basename(f), errors)
        continue
    json.dump(out, open(os.path.join(OUT, os.path.basename(f)), 'w', encoding='utf8'), indent=2)
    done += 1
print('written', done)
