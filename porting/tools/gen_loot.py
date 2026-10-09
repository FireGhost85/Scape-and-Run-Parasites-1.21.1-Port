"""Block loot tables: every block drops its own item (1.12 default), variant blocks drop the item of the variant, slabs drop two for the
double state, doors only from the lower half.   usage: python gen_loot.py <dump.json> <resources dir>"""
import json
import os
import sys

dump = json.load(open(sys.argv[1], encoding='utf8'))
out = os.path.join(sys.argv[2], 'data', 'srparasites', 'loot_table', 'blocks')
os.makedirs(out, exist_ok=True)
items = set(i['id'] for i in dump['items'])
made = skipped = 0


def entry(item, bid, func=None, cond=None):
    e = {'type': 'minecraft:item', 'name': 'srparasites:' + item}
    if cond:
        e['conditions'] = [{'condition': 'minecraft:block_state_property', 'block': 'srparasites:' + bid, 'properties': cond}]
    if func:
        e['functions'] = func
    return e


for b in dump['blocks']:
    bid = b['id']
    props = b['properties']
    slab_func = None
    if 'type' in props and 'double' in props['type']:
        slab_func = [{'function': 'minecraft:set_count', 'count': 2, 'add': False,
                      'conditions': [{'condition': 'minecraft:block_state_property', 'block': 'srparasites:' + bid, 'properties': {'type': 'double'}}]}]
    pool_conditions = [{'condition': 'minecraft:survives_explosion'}]
    if set(props) >= {'half', 'hinge', 'open', 'powered'}:
        pool_conditions.append({'condition': 'minecraft:block_state_property', 'block': 'srparasites:' + bid, 'properties': {'half': 'lower'}})
    if set(props) == {'part'}:
        skipped += 1
        continue
    entries = []
    if 'variant' in props:
        for v in props['variant']:
            name = bid + '_' + v
            if name in items:
                entries.append(entry(name, bid, slab_func, {'variant': v}))
    elif bid in items:
        entries.append(entry(bid, bid, slab_func))
    if not entries:
        skipped += 1
        continue
    table = {'type': 'minecraft:block', 'pools': [{'rolls': 1, 'bonus_rolls': 0, 'conditions': pool_conditions, 'entries': entries}]}
    if len(entries) > 1:
        table['pools'][0]['entries'] = entries
    json.dump(table, open(os.path.join(out, bid + '.json'), 'w'), indent=1)
    made += 1
print('loot tables', made, 'skipped (no item / no drop)', skipped)
