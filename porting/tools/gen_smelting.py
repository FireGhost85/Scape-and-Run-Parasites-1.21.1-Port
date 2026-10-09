"""Generates the furnace recipes of the original SRPSmelting.register() as data/srparasites/recipe/smelting_*.json."""
import json
import os
import re
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import convert_recipes as cr

LINES = [
    ('parasiterubble', 11, 'srparasites:parasiterubble', 13, 1, 0.1),
    ('infestedore', 0, 'minecraft:coal', None, 4, 0.1),
    ('infestedore', 1, 'minecraft:diamond', None, 2, 1.0),
    ('infestedore', 2, 'minecraft:emerald', None, 2, 1.0),
    ('infestedore', 3, 'minecraft:gold_ingot', None, 2, 0.7),
    ('infestedore', 4, 'minecraft:iron_ingot', None, 2, 0.7),
    ('infestedore', 5, 'minecraft:lapis_lazuli', None, 9, 0.2),
    ('infestedore', 6, 'minecraft:redstone', None, 7, 0.3),
    ('infestedore', 7, 'srparasites:lurecomponent6', 0, 1, 1.0),
    ('bloody_rod', 0, 'minecraft:blaze_rod', None, 1, 0.1),
    ('bloody_bone', 0, 'minecraft:bone', None, 1, 0.1),
    ('bloody_iron_ingot', 0, 'minecraft:iron_ingot', None, 1, 0.1),
    ('infested_cobblestone', 0, 'srparasites:infestedrubble', 0, 1, 0.1),
    ('parasitestain', 2, 'srparasites:cooked_flesh', 0, 1, 0.35),
    ('parasiterubble', 3, 'srparasites:hive_scrap', 0, 1, 0.1),
]
counts = {'parasiterubble:3': 2}
out = os.path.join(cr.OUT)
os.makedirs(out, exist_ok=True)
for n, (src, meta, dst, dmeta, count, xp) in enumerate(LINES):
    ing = cr.resolve({'item': 'srparasites:' + src, 'data': meta})
    if dst.startswith('srparasites:'):
        res = cr.resolve({'item': dst, 'data': dmeta})['item']
    else:
        res = dst
    name = 'smelting_%02d_%s.json' % (n, src)
    json.dump({'type': 'minecraft:smelting', 'ingredient': ing, 'result': {'id': res, **({'count': count} if count != 1 else {})},
               'experience': xp, 'cookingtime': 200}, open(os.path.join(out, name), 'w', encoding='utf8'), indent=2)
print('errors', cr.errors)
