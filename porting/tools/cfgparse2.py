import re,json,sys,os
sys.path.insert(0,'/home/claude/work/tools')
from cfgparse import *

class Unres(Exception): pass
BLOCKCONV=None
def _load_bc():
    global BLOCKCONV
    src=open(ROOT+'SRPConfigBlockConversions.java',encoding='utf-8').read()
    m=re.search(r'new String\[\]\{(.*?)\};',src,re.S)
    BLOCKCONV=[json.loads(x) for x in split_args(m.group(1))]
_load_bc()
ATTR={}
def _load_attr():
    src=open(ROOT+'../SRPAttributes.java',encoding='utf-8').read()
    for m in re.finditer(r'^\s+([A-Z][A-Z0-9_]+) = (-?[\d.]+(?:E-?\d+)?)[fFdD]?;',src,re.M):
        ATTR.setdefault(m.group(1),float(m.group(2)))
_load_attr()

def build_env(src):
    env={}
    dup=set()
    for m in re.finditer(r'^\s*(?:public |private )?(?:static )?(?:final )?(String|int|float|double|boolean|byte|short|long)(?:\[\])? (\w+) = ',src,re.M):
        end=find_stmt_end(src,m.end())
        v=src[m.end():end].strip()
        if m.group(2) in env and env[m.group(2)][1]!=v: dup.add(m.group(2))
        env[m.group(2)]=(m.group(1),v)
    # declared without initializer -> look for plain assignments
    for m in re.finditer(r'^\s*(?:public |private )?static (?:final )?(String|int|float|double|boolean|byte|short|long)(?:\[\])? (\w+);',src,re.M):
        n=m.group(2)
        if n in env: continue
        for a in re.finditer(r'^\s+'+re.escape(n)+r' = ',src,re.M):
            end=find_stmt_end(src,a.end())
            v=src[a.end():end].strip()
            if 'cfg.' in v or 'config' in v.lower() and 'Config' in v: continue
            env[n]=(m.group(1)+('[]' if '[]' in m.group(0) else ''),v);break
    return env,dup

def ev(expr,env,depth=0):
    e=expr.strip()
    if depth>20: raise Unres(e)
    # string concat at top-level
    parts=split_plus(e)
    if len(parts)>1:
        vals=[ev(p,env,depth+1) for p in parts]
        if any(isinstance(v,str) for v in vals):
            return ''.join(v if isinstance(v,str) else fmt(v) for v in vals)
        return sum(vals)
    m=re.fullmatch(r'\(\s*(float|int|byte|double|short|long)\s*\)\s*(.+)',e,re.S)
    if m:
        v=ev(m.group(2),env,depth+1)
        return cast(m.group(1),v)
    if e.startswith('(') and e.endswith(')') and balanced(e[1:-1]): return ev(e[1:-1],env,depth+1)
    if e.startswith('"'):
        return json.loads(e)
    if e in('true','false'): return e=='true'
    m=re.fullmatch(r'-?(0x[0-9a-fA-F]+|\d[\d_]*\.?\d*(?:[eE][-+]?\d+)?)[fFdDlL]?',e)
    if m:
        t=e.rstrip('fFdDlL') if not e.lower().startswith('0x') else e
        if e.lower().startswith(('0x','-0x')): return int(t,16)
        return float(t) if ('.' in t or 'e' in t.lower() or e[-1] in 'fFdD') else int(t.replace('_',''))
    m=re.fullmatch(r'new (String|byte|int|float|double|boolean)\[\s*\d*\s*\]\s*(\{.*\})?',e,re.S)
    if m:
        if m.group(2) is None: return []
        inner=m.group(2)[1:-1].strip()
        return [ev(a,env,depth+1) for a in split_args(inner)] if inner else []
    m=re.fullmatch(r'(?:Integer|Float|Double)\.(MAX_VALUE|MIN_VALUE)',e)
    if m: return 2147483647 if m.group(1)=='MAX_VALUE' else -2147483648
    if e=='Short.MAX_VALUE': return 32767
    if e=='SRPConfigBlockConversions.DEFAULT_INFESTATION_CONVERT_BLOCKS': return BLOCKCONV
    if re.fullmatch(r'\w+',e):
        if e in env: return cast(env[e][0],ev(env[e][1],env,depth+1))
        raise Unres(e)
    m=re.fullmatch(r'SRPAttributes\.(\w+)',e)
    if m and m.group(1) in ATTR: return ATTR[m.group(1)]
    raise Unres(e)

def fmt(v):
    if isinstance(v,float) and v==int(v) and abs(v)<1e15: return str(v) if False else repr(v)
    return str(v)

def split_plus(e):
    parts=[];depth=0;cur='';instr=False;i=0
    while i<len(e):
        c=e[i]
        if instr:
            cur+=c
            if c=='\\': cur+=e[i+1]; i+=1
            elif c=='"': instr=False
        else:
            if c=='"': instr=True; cur+=c
            elif c in '([{': depth+=1; cur+=c
            elif c in ')]}': depth-=1; cur+=c
            elif c=='+' and depth==0: parts.append(cur); cur=''
            else: cur+=c
        i+=1
    parts.append(cur)
    return [p for p in parts]

def balanced(s):
    d=0
    for c in s:
        if c=='(': d+=1
        elif c==')':
            d-=1
            if d<0: return False
    return d==0

def cast(t,v):
    if t in('float','double'): return float(v) if not isinstance(v,(list,str)) else v
    if t in('int','byte','short','long'): return int(v) if not isinstance(v,(list,str)) else v
    return v

def run():
    allspec={}
    unresolved=[]
    for name,fn in FILES.items():
        src,fields,entries=parse(name)
        env,dup=build_env(src)
        # categories: map by position -> nearest preceding cfg category usage unnecessary
        spec=[]
        for e in entries:
            a=e['args'];f=e['fn']
            try:
                key=ev(a[0],env)
                cat=ev(a[1],env)
            except Unres as ex:
                unresolved.append((name,a[0][:50],str(ex))); continue
            rec=dict(file=fn,cls=name,var=(a[2] if f=='getByteList' else e['var']),fn=f,category=cat,key=key,pos=e['pos'])
            try:
                if f in('getBoolean','getString','getStringList','get'):
                    rec['default']=ev(a[2],env)
                    rec['comment']=ev(a[3],env) if len(a)>3 else ''
                    if f=='getStringList' and len(a)>4: rec['validator']=a[4]
                    if f=='getString' and len(a)>4: rec['valid']=a[4]
                elif f in('getInt','getFloat'):
                    try: rec['default']=ev(a[2],env)
                    except Unres:
                        rec['default']=0 if f=='getInt' else 0.0; rec['implicit_default']=True
                    rec['min']=ev(a[3],env);rec['max']=ev(a[4],env)
                    if rec['default']<rec['min']: rec['default_clamped_from']=rec['default'];rec['default']=rec['min']
                    if rec['default']>rec['max']: rec['default_clamped_from']=rec['default'];rec['default']=rec['max']
                    rec['comment']=ev(a[5],env) if len(a)>5 else ''
                elif f=='getByteList':
                    rec['default']=ev(a[2],env); rec['comment']=ev(a[3],env) if len(a)>3 else ''
                    rec['elem']='byte'
                else:
                    rec['rawargs']=a
            except Unres as ex:
                unresolved.append((name,key,'val:'+str(ex))); rec['unres']=str(ex)
            spec.append(rec)
        catc=[]
        for m in re.finditer(r'cfg\.addCustomCategoryComment\(',src):
            end=find_stmt_end(src,m.end()-1)
            from cfgparse import call_inner
            aa=split_args(call_inner(src[m.end():end]))
            try: catc.append(dict(category=ev(aa[0],env),comment=ev(aa[1],env),pos=m.start()))
            except Unres as ex: print('catcomment unresolved',name,aa[0][:40],ex)
        allspec[name]=spec
        allspec[name+'__catcomments']=catc
        print(name,len(spec),'resolved; catcomments',len(catc))
    print('unresolved',len(unresolved))
    for u in unresolved[:25]: print(' ',u)
    os.makedirs('/home/claude/work/spec',exist_ok=True)
    json.dump(allspec,open('/home/claude/work/spec/config_spec.json','w'),indent=1)
if __name__=='__main__': run()
