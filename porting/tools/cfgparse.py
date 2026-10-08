import re,json,sys,os
ROOT='/home/claude/work/src/com/dhanantry/scapeandrunparasites/util/config/'
FILES={'SRPConfig':'SRParasites.cfg','SRPConfigMobs':'SRParasitesMobs.cfg','SRPConfigSystems':'SRParasitesSystems.cfg','SRPConfigWorld':'SRParasitesWorld.cfg'}

def split_args(s):
    args=[];depth=0;cur='';instr=False;i=0
    while i<len(s):
        c=s[i]
        if instr:
            cur+=c
            if c=='\\': cur+=s[i+1]; i+=1
            elif c=='"': instr=False
        else:
            if c=='"': instr=True; cur+=c
            elif c in '([{': depth+=1; cur+=c
            elif c in ')]}': depth-=1; cur+=c
            elif c==',' and depth==0: args.append(cur.strip()); cur=''
            else: cur+=c
        i+=1
    if cur.strip(): args.append(cur.strip())
    return args

def find_stmt_end(src,start):
    depth=0;instr=False;i=start
    while i<len(src):
        c=src[i]
        if instr:
            if c=='\\': i+=1
            elif c=='"': instr=False
        else:
            if c=='"': instr=True
            elif c in '([{': depth+=1
            elif c in ')]}': depth-=1
            elif c==';' and depth==0: return i
        i+=1
    return -1

def call_inner(s):
    depth=1;instr=False;i=0
    while i<len(s):
        ch=s[i]
        if instr:
            if ch=='\\': i+=1
            elif ch=='"': instr=False
        else:
            if ch=='"': instr=True
            elif ch in '([{': depth+=1
            elif ch in ')]}':
                depth-=1
                if depth==0: return s[:i]
        i+=1
    return s

def parse(name):
    src=open(ROOT+name+'.java',encoding='utf-8').read()
    fields={}
    for m in re.finditer(r'^\s*(?:public|private)?\s*static\s+(?:final\s+)?([\w\[\]<>]+)\s+(\w+)\s*(?:=\s*)?',src,re.M):
        pass
    # field declarations with initializer
    for m in re.finditer(r'^\s*(?:public |private )?static (?:final )?([\w\[\]]+) (\w+) = ',src,re.M):
        end=find_stmt_end(src,m.end())
        fields[m.group(2)]=(m.group(1),src[m.end():end].strip())
    entries=[]
    for m in re.finditer(r'(?:(\w+)\s*=\s*)?(?:\([\w\[\]]+\)\s*)?cfg\.(getBoolean|getFloat|getInt|getString|getStringList|get)\(',src):
        end=find_stmt_end(src,m.end()-1)
        inner=src[m.end():end]
        inner=call_inner(inner)
        args=split_args(inner)
        if m.group(2)=='get': args=[args[1],args[0]]+args[2:]
        entries.append(dict(var=m.group(1),fn=m.group(2),args=args,pos=m.start()))
    for m in re.finditer(r'getByteVal\(cfg,',src):
        end=find_stmt_end(src,m.end()-1)
        inner=src[m.end():end]
        inner=call_inner(inner)
        aa=split_args(inner); entries.append(dict(var=None,fn='getByteList',args=[aa[1],aa[0]]+aa[2:],pos=m.start()))
    cats=[(m.start(),m.group(1)) for m in re.finditer(r'cfg\.addCustomCategoryComment\((\w+|"[^"]*")',src)]
    return src,fields,entries

def unq(s):
    s=s.strip()
    if s.startswith('"') and s.endswith('"'): return json.loads(s)
    return None

if __name__=='__main__':
    out={}
    for name in FILES:
        src,fields,entries=parse(name)
        print(name,len(fields),'fields',len(entries),'entries')
        bad=0
        for e in entries:
            a=e['args']
            if unq(a[0]) is None: bad+=1; print('  key not literal',a[0][:60])
        print(' nonliteral keys',bad)
