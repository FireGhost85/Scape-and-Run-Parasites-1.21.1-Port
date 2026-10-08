"""Field sets scanned from the project sources (so translated code gets `.get()` on registry holders)."""
import os
import re

_here = os.path.dirname(os.path.abspath(__file__))
_proj = os.path.normpath(os.path.join(_here, '..', '..'))
_blocks = os.path.join(_proj, 'src', 'main', 'java', 'com', 'dhanantry', 'scapeandrunparasites', 'init', 'SRPBlocks.java')

BLOCK_FIELDS = set()
try:
    with open(_blocks, encoding='utf8') as f:
        for line in f:
            m = re.search(r'public static final DeferredBlock<[^>]*> (\w+)\s*=', line)
            if m:
                BLOCK_FIELDS.add(m.group(1))
except OSError:
    pass
