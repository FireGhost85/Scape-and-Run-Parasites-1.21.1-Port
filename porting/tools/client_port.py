"""Ports the 1.12 entity models and renderers of SRP onto the client/legacy compatibility layer.

usage: python client_port.py   (needs C:/srp_work/named_all, the annotated decompile, and srp_translate.py)
Runs srp_translate.py on client/model/** and client/renderer/entity/**, then applies the fixes below and writes the files into
src/main/java (existing files with the same name are NOT overwritten: hand written classes such as RenderSRP stay).
"""
import os
import re
import subprocess
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
ROOT = os.path.dirname(os.path.dirname(HERE))
SRC = os.path.join(ROOT, 'src/main/java/com/dhanantry/scapeandrunparasites')
NAMED = 'C:/srp_work/named_all'
OUT = 'C:/srp_work/cl'
BASE = os.path.join(NAMED, 'com/dhanantry/scapeandrunparasites')
LEGACY = 'com.dhanantry.scapeandrunparasites.client.legacy.'
LEGACY_TYPES = ['ModelBase', 'ModelRenderer', 'GlStateManager', 'OpenGlHelper', 'RenderManager', 'RenderLiving', 'RenderLivingBase', 'Render', 'LayerRenderer', 'GlContext']
SKIP = ('SRPModelBiped', 'ModelMobilityArmor', 'RenderRelayController', 'RenderTrophyTESR', 'RenderDistortedSign',
        'SRPLayerBipedArmor', 'MobilityArmorFirstPersonHandler', 'PearlHeldGlowRenderer', 'ScreenOverlayRenderer', 
        'RenderSRP', 'RenderMalleable')

files = []
for sub in ('client/model', 'client/renderer'):
    for dp, _, fs in os.walk(os.path.join(BASE, sub)):
        for f in fs:
            if f.endswith('.java') and f[:-5] not in SKIP:
                files.append(os.path.relpath(os.path.join(dp, f), BASE).replace(os.sep, '/'))
print(len(files), 'files')
subprocess.check_call([sys.executable, os.path.join(HERE, 'srp_translate.py'), NAMED, OUT] + files)

written = 0
for rel in files:
    src = os.path.join(OUT, 'com/dhanantry/scapeandrunparasites', rel)
    dst = os.path.join(SRC, rel)
    if not os.path.exists(src) or os.path.exists(dst):
        continue
    s = open(src, encoding='utf8').read()
    s = s.replace('(EntityModel)this', 'this').replace('(EntityModel) this', 'this')
    s = re.sub(r'\bEntityModel\b', 'ModelBase', s)
    s = re.sub(r'import net\.minecraft\.client\.model\.ModelBase;\n', '', s)
    imports = []
    for t in LEGACY_TYPES:
        if re.search(r'\b%s\b' % t, s) and ('import %s%s;' % (LEGACY, t)) not in s and not re.search(r'import [\w.]+\.%s;' % t, s):
            imports.append('import %s%s;' % (LEGACY, t))
    if imports:
        s = re.sub(r'(package [\w.]+;\n)', lambda m: m.group(1) + '\n' + '\n'.join(imports), s, count=1)
    os.makedirs(os.path.dirname(dst), exist_ok=True)
    open(dst, 'w', encoding='utf8').write(s)
    written += 1
print('written', written)
