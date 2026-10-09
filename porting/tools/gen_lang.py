"""Convert the 1.12 .lang files to 1.21 .json (keys without .name, tile -> block, itemGroup lower case) and fill the missing names.

usage: python gen_lang.py <1.12 lang dir> <dump.json> <assets/srparasites dir>
"""
import json
import os
import re
import sys

src, dump_path, assets = sys.argv[1], sys.argv[2], sys.argv[3]
dump = json.load(open(dump_path, encoding='utf8'))
out_dir = os.path.join(assets, 'lang')
os.makedirs(out_dir, exist_ok=True)


def read(path):
    d = {}
    for line in open(path, encoding='utf8', errors='replace'):
        line = line.rstrip('\r\n')
        if not line.strip() or line.lstrip().startswith('#') or '=' not in line:
            continue
        k, _, v = line.partition('=')
        d[k.strip()] = v
    return d


def convert_key(k):
    if k.startswith('tile.') and k.endswith('.name'):
        return 'block.' + k[5:-5]
    if (k.startswith('item.') or k.startswith('entity.')) and k.endswith('.name'):
        return k[:-5]
    if k == 'itemGroup.SRParasites':
        return 'itemGroup.srparasites'
    return k


def title(s):
    return ' '.join(w.capitalize() for w in re.split(r'[_\s]+', s) if w)


written = []
for f in sorted(os.listdir(src)):
    if not f.endswith('.lang'):
        continue
    code = f[:-5].lower()
    if code.endswith('21'):
        continue
    d = read(os.path.join(src, f))
    out = {}
    for k, v in d.items():
        out[convert_key(k)] = v
    if code == 'en_us':
        out.setdefault('itemGroup.srparasites', 'Scape and Run: Parasites')
        added = []
        # blocks without a name: the item of the same id, else the first variant item, else a title
        item_ids = [i['id'] for i in dump['items']]
        for b in dump['blocks']:
            key = 'block.srparasites.' + b['id']
            if key in out:
                continue
            name = out.get('item.srparasites.' + b['id'])
            if name is None:
                for iid in item_ids:
                    if iid.startswith(b['id'] + '_') and ('block.srparasites.' + iid) in out:
                        name = out['block.srparasites.' + iid]
                        break
            out[key] = name or title(b['id'])
            added.append(key)
        for e in dump['entities']:
            key = 'entity.srparasites.' + e['id']
            if key not in out:
                out[key] = title(e['id'])
                added.append(key)
        for i in dump['items']:
            if i['id'].endswith('_spawn_egg'):
                base = i['id'][:-len('_spawn_egg')]
                out['item.srparasites.' + i['id']] = out.get('entity.srparasites.' + base, title(base)) + ' Spawn Egg'
                added.append(i['id'])
                continue
            key = ('block.' if i['blockItem'] else 'item.') + 'srparasites.' + i['id']
            if key not in out and ('item.srparasites.' + i['id']) not in out:
                out[key] = title(i['id'])
                added.append(key)
        print('en_us: filled', len(added), added[:40])
    with open(os.path.join(out_dir, code + '.json'), 'w', encoding='utf8') as fp:
        json.dump(out, fp, ensure_ascii=False, indent=1, sort_keys=False)
    written.append(code)
print('written', len(written), written)
