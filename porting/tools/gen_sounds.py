import re,sys,os
src=open('/home/claude/work/src/com/dhanantry/scapeandrunparasites/init/SRPSounds.java').read()
pairs=re.findall(r'(\w+) = SRPSounds\.createSound\("([^"]+)"\)',src)
bad=[n for f,n in pairs if not re.fullmatch(r'[a-z0-9/._-]+',n)]
assert not bad,bad
# Original bug: QUAC_HURT assigned twice (quac.hurt, then quac.dig) and QUAC_DIG left null (used by EntityQuac). Fixed.
fixed=[];seen=set()
for f,n in pairs:
    if f=='QUAC_HURT' and n=='quac.dig': f='QUAC_DIG'
    fixed.append((f,n))
pairs=fixed
assert len(set(f for f,_ in pairs))==len(pairs)
out=['package com.dhanantry.scapeandrunparasites.init;','',
'import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;',
'import net.minecraft.core.registries.Registries;',
'import net.minecraft.resources.ResourceLocation;',
'import net.minecraft.sounds.SoundEvent;',
'import net.neoforged.neoforge.registries.DeferredHolder;',
'import net.neoforged.neoforge.registries.DeferredRegister;','',
'/** Sound events. Generated from SRP 1.10.9 SRPSounds by porting/tools/gen_sounds.py (names unchanged). */',
'public final class SRPSounds {',
'    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, ScapeAndRunParasites.MODID);','',
'    private static DeferredHolder<SoundEvent, SoundEvent> create(String name) {',
'        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, name)));',
'    }','']
for f,n in pairs:
    out.append(f'    public static final DeferredHolder<SoundEvent, SoundEvent> {f} = create("{n}");')
out+=['','    private SRPSounds() {}','}','']
d='/mnt/user-data/outputs/project/src/main/java/com/dhanantry/scapeandrunparasites/init/'
os.makedirs(d,exist_ok=True)
open(d+'SRPSounds.java','w').write('\n'.join(out))
print(len(pairs))
