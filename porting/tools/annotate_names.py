"""Writes an annotated copy of the decompiled SRP jar sources: every SRG id (func_123_a, field_123_b)
gets its MCP 1.12 name as a comment, e.g. func_70097_a/*attackEntityFrom*/.

usage: python annotate_names.py <decompiled_src_root> <out_root> [porting/spec/srg_names.csv]
<decompiled_src_root> is the folder that contains com/dhanantry/scapeandrunparasites (CFR output).
"""
import csv
import glob
import os
import re
import sys

src_root, out_root = sys.argv[1], sys.argv[2]
csv_path = sys.argv[3] if len(sys.argv) > 3 else os.path.join(os.path.dirname(__file__), "..", "spec", "srg_names.csv")

names = {}
with open(csv_path, encoding="utf8") as f:
    for row in csv.reader(f):
        if len(row) == 2 and row[0] != "srg":
            names[row[0]] = row[1]

pattern = re.compile(r"\b((?:func|field)_\d+_\w+)\b")
count = 0
for path in glob.glob(os.path.join(src_root, "**", "*.java"), recursive=True):
    with open(path, encoding="utf8", errors="ignore") as f:
        text = f.read()
    text = pattern.sub(lambda m: f"{m.group(1)}/*{names[m.group(1)]}*/" if m.group(1) in names else m.group(1), text)
    rel = os.path.relpath(path, src_root)
    dest = os.path.join(out_root, rel)
    os.makedirs(os.path.dirname(dest), exist_ok=True)
    with open(dest, "w", encoding="utf8") as f:
        f.write(text)
    count += 1
print(f"annotated {count} files with {len(names)} known names")
