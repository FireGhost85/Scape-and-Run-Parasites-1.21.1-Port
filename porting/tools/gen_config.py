import re,json,os,sys
sys.path.insert(0,'/home/claude/work/tools')
from cfgparse import find_stmt_end,split_args
SRC='/home/claude/work/src/com/dhanantry/scapeandrunparasites/util/config/'
OUT=sys.argv[1] if len(sys.argv)>1 else '/home/claude/work/project/src/main/java/com/dhanantry/scapeandrunparasites/config/'
os.makedirs(OUT,exist_ok=True)
spec=json.load(open('/home/claude/work/spec/config_spec.json'))
PKG='com.dhanantry.scapeandrunparasites.config'
def js(s): return json.dumps(s,ensure_ascii=True)
def fnum(v,t):
    if t=='float': 
        r=repr(float(v)); return r+'f'
    r=repr(float(v)); return r
def lit(v,t):
    if t=='boolean': return 'true' if v else 'false'
    if t=='int': return str(int(v))
    if t=='byte': return f'(byte){int(v)}'
    if t=='short': return f'(short){int(v)}'
    if t=='long': return str(int(v))+'L'
    if t in('float','double'): return fnum(v,t)
    if t=='String': return js(v)
    if t=='String[]': return 'new String[]{'+', '.join(js(x) for x in v)+'}'
    if t=='int[]': return 'new int[]{'+', '.join(str(int(x)) for x in v)+'}'
    if t=='byte[]': return 'new byte[]{'+', '.join(f'(byte){int(x)}' for x in v)+'}'
    raise Exception(t)
stats={}
for cls in ['SRPConfig','SRPConfigMobs','SRPConfigSystems','SRPConfigWorld']:
    src=open(SRC+cls+'.java',encoding='utf-8').read()
    entries=spec[cls]; catc=spec[cls+'__catcomments']
    # declared static fields
    decls=[]  # (mods,type,name,init)
    for m in re.finditer(r'^    ((?:public |private |protected )?static (?:final )?)([\w]+(?:\[\])?) (\w+)(?: = |;)',src,re.M):
        init=None
        if src[m.end()-3:m.end()]==' = ':
            end=find_stmt_end(src,m.end()); init=src[m.end():end].strip()
        decls.append([m.group(1),m.group(2),m.group(3),init])
    dmap={d[2]:d for d in decls}
    bound={}
    for e in entries: bound[e['var']]=e
    # categories in order
    cats=[]
    for e in entries:
        if e['category'] not in cats: cats.append(e['category'])
    cc={c['category']:c['comment'] for c in catc}
    fieldlines=[]
    for mods,t,name,init in decls:
        if name in bound:
            e=bound[name]
            mods2=mods.replace('final ','')
            if e['fn']=='get':
                fieldlines.append(f'    {mods2}String[] {name} = {lit([str(x) for x in e["default"]],"String[]")};')
            else:
                fieldlines.append(f'    {mods2}{t} {name} = {lit(e["default"],t)};')
        else:
            fieldlines.append(f'    {mods}{t} {name}'+(f' = {init};' if init is not None else ';'))
    vals=[]; baked=[]
    meth=[]
    idx=0
    for ci,cat in enumerate(cats):
        lines=[f'    private static void c{ci}(ModConfigSpec.Builder b) {{']
        if cat in cc: lines.append(f'        b.comment({js(cc[cat])});')
        lines.append(f'        b.push(List.of({js(cat)}));')
        blines=[f'    private static void b{ci}() {{']
        for e in [x for x in entries if x['category']==cat]:
            v=f'k{idx}'; idx+=1
            fn=e['fn']; key=js(e['key']); com=e.get('comment') or ''
            t=dmap[e['var']][1]
            if fn=='get': com=(com+' \n [Port] Entries are dimension ids: legacy numbers (0=overworld, -1=nether, 1=end) or resource locations such as minecraft:overworld.').strip()
            pre=f'        b.comment({js(com)});' if com else ''
            if pre: lines.append(pre)
            if fn=='getBoolean':
                vals.append(f'    private static ModConfigSpec.BooleanValue {v};')
                lines.append(f'        {v} = b.define(List.of({key}), {lit(e["default"],"boolean")});')
                blines.append(f'        {e["var"]} = {v}.get();')
            elif fn in('getInt','getFloat'):
                isint=fn=='getInt'
                if isint:
                    vals.append(f'    private static ModConfigSpec.IntValue {v};')
                    lines.append(f'        {v} = b.defineInRange(List.of({key}), {int(e["default"])}, {int(e["min"])}, {int(e["max"])});')
                    acc='' if t=='int' else f'.{t}Value()'
                    blines.append(f'        {e["var"]} = {v}.get(){acc};')
                else:
                    vals.append(f'    private static ModConfigSpec.DoubleValue {v};')
                    lines.append(f'        {v} = b.defineInRange(List.of({key}), {fnum(e["default"],"double")}, {fnum(e["min"],"double")}, {fnum(e["max"],"double")});')
                    acc='' if t=='double' else f'.{t}Value()'
                    blines.append(f'        {e["var"]} = {v}.get(){acc};')
            elif fn=='getString':
                vals.append(f'    private static ModConfigSpec.ConfigValue<String> {v};')
                lines.append(f'        {v} = b.define(List.of({key}), {js(e["default"])});')
                blines.append(f'        {e["var"]} = {v}.get();')
            elif fn=='getStringList':
                vals.append(f'    private static ModConfigSpec.ConfigValue<List<? extends String>> {v};')
                lines.append(f'        {v} = b.defineListAllowEmpty(List.of({key}), List.of({", ".join(js(x) for x in e["default"])}), () -> "", o -> o instanceof String);')
                blines.append(f'        {e["var"]} = {v}.get().toArray(new String[0]);')
            elif fn=='get':
                vals.append(f'    private static ModConfigSpec.ConfigValue<List<? extends String>> {v};')
                lines.append(f'        {v} = b.defineListAllowEmpty(List.of({key}), List.of({", ".join(js(str(x)) for x in e["default"])}), () -> "", o -> o instanceof String);')
                blines.append(f'        {e["var"]} = {v}.get().toArray(new String[0]);')
            elif fn=='getByteList':
                vals.append(f'    private static ModConfigSpec.ConfigValue<List<? extends Integer>> {v};')
                lines.append(f'        {v} = b.defineListAllowEmpty(List.of({key}), List.of({", ".join(str(int(x)) for x in e["default"])}), () -> 0, o -> o instanceof Number);')
                bt='byte'
                blines.append(f'        {{ List<? extends Integer> l = {v}.get(); {bt}[] a = new {bt}[l.size()]; for (int i = 0; i < a.length; i++) a[i] = ({bt}) ((Number) l.get(i)).intValue(); {e["var"]} = a; }}')
            else: raise Exception(fn)
        lines.append('        b.pop();'); lines.append('    }')
        blines.append('    }')
        meth.append('\n'.join(lines)); baked.append('\n'.join(blines))
    n=len(cats)
    extras=''
    if cls=='SRPConfigSystems':
        m=re.search(r'    public static int getScannerCooldownTicks\(\) \{.*?\n    \}\n',src,re.S); extras=m.group(0)
    if cls=='SRPConfigWorld':
        m=re.search(r'    public static boolean isCelestialEventBlacklisted\(String id\) \{.*?\n    \}\n',src,re.S); extras=m.group(0)
    body=f'''package {PKG};

import java.util.List;
import net.neoforged.neoforge.common.ModConfigSpec;

/** Generated from SRP 1.10.9 {cls} by porting/tools/gen_config.py. Field names, categories, keys, defaults and comments are the original ones. */
public class {cls} {{
    public static final ModConfigSpec SPEC;
{chr(10).join(fieldlines)}

{chr(10).join(vals)}

    static {{
        ModConfigSpec.Builder b = new ModConfigSpec.Builder();
{chr(10).join(f"        c{i}(b);" for i in range(n))}
        SPEC = b.build();
    }}

    /** Copies loaded values into the static fields (call on config load / reload). */
    public static void bake() {{
{chr(10).join(f"        b{i}();" for i in range(n))}
    }}

{extras}
{chr(10).join(meth)}

{chr(10).join(baked)}
}}
'''
    open(OUT+cls+'.java','w',encoding='utf-8').write(body)
    stats[cls]=(len(entries),len(decls),n)
print(stats)
