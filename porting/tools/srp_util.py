"""argument-aware text helpers shared by the translator tools"""
import re


def split_args(s):
    """split a top-level comma separated argument string"""
    out, depth, cur, instr = [], 0, '', False
    i = 0
    while i < len(s):
        c = s[i]
        if instr:
            cur += c
            if c == '\\':
                i += 1
                cur += s[i]
            elif c == '"':
                instr = False
        elif c == '"':
            instr = True
            cur += c
        elif c in '([{':
            depth += 1
            cur += c
        elif c in ')]}':
            depth -= 1
            cur += c
        elif c == ',' and depth == 0:
            out.append(cur.strip())
            cur = ''
        else:
            cur += c
        i += 1
    if cur.strip():
        out.append(cur.strip())
    return out


def find_close(s, start):
    """index of the paren matching the '(' at s[start]"""
    depth = 0
    instr = False
    i = start
    while i < len(s):
        c = s[i]
        if instr:
            if c == '\\':
                i += 1
            elif c == '"':
                instr = False
        elif c == '"':
            instr = True
        elif c == '(':
            depth += 1
        elif c == ')':
            depth -= 1
            if depth == 0:
                return i
        i += 1
    return -1


def rewrite_calls(text, pattern, fn):
    """for each match of `pattern` (which must end right before the '(' of a call), call fn(m, args)->replacement for
    the whole call expression."""
    out = []
    pos = 0
    rx = re.compile(pattern)
    while True:
        m = rx.search(text, pos)
        if not m:
            out.append(text[pos:])
            break
        op = text.find('(', m.end() - 1) if text[m.end() - 1] == '(' else m.end()
        if text[op] != '(':
            out.append(text[pos:m.end()])
            pos = m.end()
            continue
        cl = find_close(text, op)
        if cl < 0:
            out.append(text[pos:])
            break
        args = split_args(text[op + 1:cl])
        rep = fn(m, args)
        if rep is None:
            out.append(text[pos:cl + 1])
        else:
            out.append(text[pos:m.start()])
            out.append(rep)
        pos = cl + 1
    return ''.join(out)
