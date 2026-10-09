"""Generate 1.21 blockstates (and the item models that are missing) from the registry dump and the 1.12 blockstates.

usage: python gen_blockstates.py <dump.json> <1.12 blockstates dir> <assets/srparasites dir>
"""
import itertools
import json
import os
import sys

dump_path, old_dir, assets = sys.argv[1], sys.argv[2], sys.argv[3]
dump = json.load(open(dump_path, encoding='utf8'))
OUT = os.path.join(assets, 'blockstates')
os.makedirs(OUT, exist_ok=True)

block_models = set()
for root, _, files in os.walk(os.path.join(assets, 'models', 'block')):
    for f in files:
        block_models.add(os.path.relpath(os.path.join(root, f), os.path.join(assets, 'models', 'block')).replace(os.sep, '/')[:-5])
item_models = set(f[:-5] for f in os.listdir(os.path.join(assets, 'models', 'item')))
warn = []

VANILLA_RENAMES = {'grass_snowed': 'grass_block_snow'}


def fixmodel(m, block):
    if m.startswith('srparasites:'):
        name = m.split(':', 1)[1]
        if name in block_models:
            return 'srparasites:block/' + name
        warn.append('%s: model %s not found' % (block, m))
        return 'srparasites:block/' + name
    if m.startswith('minecraft:'):
        name = m.split(':', 1)[1]
        return 'minecraft:block/' + VANILLA_RENAMES.get(name, name)
    if m in block_models:
        return 'srparasites:block/' + m
    if m.startswith('forge:'):
        return None
    return 'minecraft:block/' + VANILLA_RENAMES.get(m, m)


def fixentry(e, block):
    if isinstance(e, list):
        out = [fixentry(x, block) for x in e]
        return [x for x in out if x]
    e = dict(e)
    if 'model' not in e:
        warn.append('%s: entry without model %s' % (block, e))
        return None
    m = fixmodel(e['model'], block)
    if m is None:
        return None
    e['model'] = m
    return e


def parse_key(k):
    if k in ('', 'normal'):
        return {}
    d = {}
    for part in k.split(','):
        a, _, b = part.partition('=')
        d[a] = b
    return d


def best(variants, t, block):
    """The entry of the 1.12 variants whose key matches the translated state t (most specific key wins)."""
    cands = []
    for k, v in variants.items():
        if k in ('inventory', 'fluid'):
            continue
        kd = parse_key(k)
        if all(t.get(a) == b for a, b in kd.items()):
            cands.append((len(kd), k, v))
    if cands:
        cands.sort(key=lambda c: -c[0])
        return cands[0][2]
    warn.append('%s: no 1.12 variant for %s' % (block, t))
    for k, v in variants.items():
        if k not in ('inventory', 'fluid'):
            return v
    return None


def load_old(name):
    p = os.path.join(old_dir, name + '.json')
    if not os.path.exists(p):
        return None
    j = json.load(open(p, encoding='utf8'))
    dflt = j.get('defaults')
    if dflt and 'variants' in j and 'model' in dflt:
        for k, v in list(j['variants'].items()):
            if isinstance(v, dict) and 'model' not in v and not any(isinstance(x, dict) for x in v.values()):
                j['variants'][k] = {**dflt, **v}
    return j


def double_name(bid):
    for a, b in (('slabhalf', 'slabdouble'), ('_slab', '_slab_double')):
        if a in bid:
            n = bid.replace(a, b)
            if os.path.exists(os.path.join(old_dir, n + '.json')):
                return n
    n = bid + '_double'
    return n if os.path.exists(os.path.join(old_dir, n + '.json')) else None


def sides(v):
    return v


first_model = {}


def entry_model(e):
    if isinstance(e, list):
        e = e[0]
    return e['model']


for b in dump['blocks']:
    bid = b['id']
    props = b['properties']
    old = load_old(bid)
    result = None
    classes = b['classes']
    if 'LiquidBlock' in classes or 'level' in props and 'FlowingFluid' in ' '.join(classes):
        result = {'variants': {'': {'model': 'srparasites:block/' + bid}}}
        mp = os.path.join(assets, 'models', 'block', bid + '.json')
        if not os.path.exists(mp):
            json.dump({'textures': {'particle': 'srparasites:block/' + bid + '_still'}}, open(mp, 'w'))
    elif old is not None and 'multipart' in old:
        parts = []
        is_wall = set(props.get('north', [])) == {'none', 'low', 'tall'}
        for part in old['multipart']:
            apply = fixentry(part['apply'], bid)
            if apply is None:
                continue
            when = part.get('when')
            if is_wall and when and any(k in ('north', 'east', 'south', 'west') for k in when):
                side = list(when)[0]
                a = dict(apply if isinstance(apply, dict) else apply[0])
                tall = dict(a)
                tall['model'] = a['model'] + '_tall'
                parts.append({'when': {side: 'low'}, 'apply': a})
                parts.append({'when': {side: 'tall'}, 'apply': tall})
                # tall side model next to the low one
                low_name = a['model'].split('block/', 1)[1]
                lp = os.path.join(assets, 'models', 'block', low_name + '.json')
                tp = os.path.join(assets, 'models', 'block', low_name + '_tall.json')
                if os.path.exists(lp) and not os.path.exists(tp):
                    low = json.load(open(lp, encoding='utf8'))
                    json.dump({'parent': 'minecraft:block/template_wall_side_tall', 'textures': low.get('textures', {})}, open(tp, 'w'), indent=2)
                continue
            if when:
                when = {k: str(v).lower() if isinstance(v, bool) else v for k, v in when.items()}
                part = dict(part)
                part['when'] = when
            nz = {'apply': apply}
            if when:
                nz['when'] = when
            parts.append(nz)
        result = {'multipart': parts}
    elif old is not None and old.get('forge_marker') and any(isinstance(v, dict) and 'textures' in next(iter(v.values()), {}) for v in old['variants'].values() if isinstance(v, dict)):
        # forge_marker with per-value texture variants of a default parent model (thornshade)
        prop, vals = next(iter(old['variants'].items()))
        parent = old['defaults']['model'].replace('minecraft:', 'minecraft:block/')
        out = {}
        for val, spec in vals.items():
            tex = spec['textures']['cross'].replace('srparasites:blocks/', 'srparasites:block/')
            mname = bid + '_' + val
            json.dump({'parent': parent, 'textures': {'cross': tex}}, open(os.path.join(assets, 'models', 'block', mname + '.json'), 'w'), indent=1)
            block_models.add(mname)
            out['%s=%s' % (prop, val)] = {'model': 'srparasites:block/' + mname}
        result = {'variants': out}
    elif old is not None and not props and all(parse_key(k) for k in old['variants'] if k not in ('inventory', 'normal', '')):
        # a 1.12 property that is a random texture choice (deadhead grass): weighted random models
        entries = [fixentry(v if isinstance(v, dict) else v[0], bid) for k, v in old['variants'].items() if k != 'inventory']
        result = {'variants': {'': [e for e in entries if e]}}
    elif old is not None:
        variants = old['variants']
        names = list(props)
        out = {}
        for combo in itertools.product(*[props[n] for n in names]):
            s = dict(zip(names, combo))
            t = dict(s)
            t.pop('waterlogged', None)
            if 'type' in s:  # slabs
                if s['type'] == 'double':
                    dn = double_name(bid)
                    dv = load_old(dn)['variants'] if dn else variants
                    t = {k: v for k, v in s.items() if k not in ('type', 'waterlogged')}
                    t['variant'] = t.get('variant', 'default')
                    t['half'] = 'bottom'
                    entry = best(dv, t, bid)
                    out[','.join('%s=%s' % kv for kv in s.items())] = fixentry(entry, bid)
                    continue
                t = {k: v for k, v in s.items() if k not in ('type', 'waterlogged')}
                t['half'] = s['type']
                t['variant'] = t.get('variant', 'default')
            if 'face' in s:  # buttons
                f = {'floor': 'up', 'ceiling': 'down'}.get(s['face'], s['facing'])
                t = {'facing': f, 'powered': s['powered']}
            entry = best(variants, t, bid)
            fe = fixentry(entry, bid) if entry else None
            if fe and 'face' in s and s['face'] != 'wall':
                rot = {'floor': {'north': 0, 'east': 90, 'south': 180, 'west': 270}, 'ceiling': {'north': 180, 'east': 270, 'south': 0, 'west': 90}}[s['face']][s['facing']]
                if isinstance(fe, dict):
                    fe['y'] = rot
            out[','.join('%s=%s' % kv for kv in s.items())] = fe
        if not names:
            e = best(variants, {}, bid)
            out = {'': fixentry(e, bid)}
        result = {'variants': {k: v for k, v in out.items() if v}}
    else:
        warn.append('%s: no 1.12 blockstate' % bid)
        continue
    json.dump(result, open(os.path.join(OUT, bid + '.json'), 'w', encoding='utf8'), indent=1)
    # default-state model per variant for the item models
    v = result.get('variants')
    if v is not None:
        first_model[bid] = v
    else:
        mpart = result['multipart']
        first_model[bid] = {'': mpart[0]['apply']} if mpart else None

# ---- item models for block items without a model
variant_values = {}
for b in dump['blocks']:
    if 'variant' in b['properties']:
        variant_values[b['id']] = b['properties']['variant']
block_ids = set(b['id'] for b in dump['blocks'])
defaults = {b['id']: b['default'] for b in dump['blocks']}
made = []
unmatched = []
for it in dump['items']:
    iid = it['id']
    if iid in item_models:
        continue
    state_variants = None
    base = None
    var = None
    if iid in block_ids:
        base = iid
    else:
        for bid, vals in variant_values.items():
            if iid.startswith(bid + '_') and iid[len(bid) + 1:] in vals:
                base, var = bid, iid[len(bid) + 1:]
                break
    if base is None or first_model.get(base) is None:
        unmatched.append(iid)
        continue
    d = dict(defaults[base])
    if var:
        d['variant'] = var
    if 'type' in d:
        d['type'] = 'bottom'
    v = first_model[base]
    key = ','.join('%s=%s' % kv for kv in d.items())
    if 'waterlogged' in d:
        pass
    e = v.get(key)
    if e is None:
        e = next(iter(v.values()))
    model = entry_model(e)
    json.dump({'parent': model}, open(os.path.join(assets, 'models', 'item', iid + '.json'), 'w'), indent=1)
    made.append(iid)

for it in dump['items']:
    if it['id'].endswith('_spawn_egg') and it['id'] not in item_models:
        json.dump({'parent': 'minecraft:item/template_spawn_egg'}, open(os.path.join(assets, 'models', 'item', it['id'] + '.json'), 'w'), indent=1)
        made.append(it['id'])

print('blockstates written:', len(os.listdir(OUT)))
print('item models made:', len(made))
print('items without model (not block items):', unmatched)
print('warnings:', len(warn))
for w in warn[:60]:
    print('  ', w)
