"""Small text fixes for the ported client classes (run after client_port.py / srp_post.py, safe to repeat)."""
import os
import re
import sys

ROOT = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
CLIENT = os.path.join(ROOT, 'src/main/java/com/dhanantry/scapeandrunparasites/client')
LEG = 'com.dhanantry.scapeandrunparasites.client.legacy.'
n = 0
for dp, _, fs in os.walk(CLIENT):
    for f in fs:
        if not f.endswith('.java'):
            continue
        p = os.path.join(dp, f)
        if os.sep + 'legacy' in dp or (os.sep + 'model' not in dp and os.sep + 'renderer' not in dp):
            continue
        s = open(p, encoding='utf8').read()
        t = s
        t = re.sub(r'[ \t]*Minecraft\.getInstance\(\)\.entityRenderer\.setupFogColor\((?:true|false)\);\n', '', t)
        if os.sep + 'misc' in dp or os.sep + 'projectile' in dp:
            t = t.replace('.yBodyRotO', '.prevRenderYawOffset').replace('.yBodyRot', '.renderYawOffset').replace('.yHeadRotO', '.prevRotationYawHead').replace('.yHeadRot', '.rotationYawHead')
        t = t.replace('entitylivingbase.prevRenderYawOffset', 'entitylivingbase.yBodyRotO').replace('entitylivingbase.renderYawOffset', 'entitylivingbase.yBodyRot')
        t = t.replace('isInvisibleToPlayer(', 'isInvisibleTo(').replace('.getRenderViewEntity()', '.getCameraEntity()').replace('.isVecInside(', '.contains(')
        t = re.sub(r'[^\n]*BlendProfile[^\n]*\n', '', t)
        t = t.replace('new DynamicTexture(16, 16)', 'null').replace('TEXTURE_BRIGHTNESS.getGlTextureId()', '0').replace('GLAllocation.createDirectFloatBuffer((int)4)', 'java.nio.FloatBuffer.allocate(4)')
        t = t.replace('DefaultVertexFormats.','Tessellator.VertexFormat.').replace('ICamera', 'Frustum')
        t = re.sub(r'(?<![\w.])BufferBuilder\b', 'Tessellator.BufferBuilder', t)
        if 'Tessellator' in t and ('import %sTessellator;' % LEG) not in t and 'package com.dhanantry.scapeandrunparasites.client.legacy' not in t:
            t = re.sub(r'(package [\w.]+;\n)', lambda m: m.group(1) + '\nimport %sTessellator;' % LEG, t, count=1)
        if t != s:
            open(p, 'w', encoding='utf8').write(t)
            n += 1
print('client_fix changed', n)
