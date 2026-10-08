"""Generates the tag files: mineable/<tool> and needs_<tier>_tool block tags from porting/spec/blocks_registry.json
(1.12 harvestTool / harvestLevel), and the item tag srparasites:parasite_planks (the plankParasiteWood ore dictionary entry)."""
import json
import os

ROOT = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
RES = os.path.join(ROOT, 'src/main/resources/data')
reg = json.load(open(os.path.join(ROOT, 'porting/spec/blocks_registry.json'), encoding='utf8'))


def write(path, values):
    full = os.path.join(RES, path)
    os.makedirs(os.path.dirname(full), exist_ok=True)
    json.dump({'replace': False, 'values': sorted(values)}, open(full, 'w', encoding='utf8'), indent=2)


mineable = {}
needs = {1: 'needs_stone_tool', 2: 'needs_iron_tool', 3: 'needs_diamond_tool'}
needs_blocks = {}
for e in reg:
    if not e['tool']:
        continue
    bid = 'srparasites:' + e['name']
    mineable.setdefault(e['tool'], []).append(bid)
    if e['level'] in needs:
        needs_blocks.setdefault(needs[e['level']], []).append(bid)
for tool, vals in mineable.items():
    write('minecraft/tags/block/mineable/%s.json' % tool, vals)
for tag, vals in needs_blocks.items():
    write('minecraft/tags/block/%s.json' % tag, vals)

# plankParasiteWood: ParasitePlank (every variant) and the other parasite plank blocks
planks = ['srparasites:parasiteplank_deadhead', 'srparasites:parasiteplank_deadheads']
planks += ['srparasites:' + n for n in ('infested_planks', 'cooked_flesh_planks', 'flesh_planks', 'goth_planks', 'brusewood_planks', 'consumed_planks')]
write('srparasites/tags/item/parasite_planks.json', planks)
print({k: len(v) for k, v in mineable.items()}, {k: len(v) for k, v in needs_blocks.items()})
