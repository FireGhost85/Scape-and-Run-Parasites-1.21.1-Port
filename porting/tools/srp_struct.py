"""Structural (argument-aware) rewrites for srp_translate.py"""
import re

from srp_rules import *  # noqa
from srp_fields import BLOCK_FIELDS

RECV = r'(?:this|[A-Za-z_]\w*)(?:\([^()]*\))?(?:\.[A-Za-z_]\w*(?:\([^()]*\))?)*'

METHOD_RENAMES = {
    'onLivingUpdate': 'aiStep', 'onUpdate': 'tick', 'attackEntityFrom': 'hurt', 'attackEntityAsMob': 'doHurtTarget', 'onDeath': 'die',
    'playLivingSound': 'playAmbientSound', 'updateAITasks': 'customServerAiStep', 'initEntityAI': 'registerGoals',
    'writeEntityToNBT': 'addAdditionalSaveData', 'readEntityFromNBT': 'readAdditionalSaveData', 'isEntityInvulnerable': 'isInvulnerableTo',
    'canBePushed': 'isPushable', 'collideWithEntity': 'doPush', 'applyEntityCollision': 'push', 'getCollisionBorderSize': 'getPickRadius',
    'onDeathUpdate': 'tickDeath', 'setRevengeTarget': 'setLastHurtByMob', 'isOnLadder': 'onClimbable', 'getSoundPitch': 'getVoicePitch',
    'getTalkInterval': 'getAmbientSoundInterval', 'getNewNavigator': 'createNavigation', 'isPotionApplicable': 'canBeAffected',
    'handleStatusUpdate': 'handleEntityEvent', 'canTriggerWalking': 'isMovementNoisy', 'processInteract': 'mobInteract',
    'canBeLeashedTo': 'canBeLeashed', 'getBlockPathWeight': 'getWalkTargetValue', 'despawnEntity': 'checkDespawn',
    'isPushedByWater': 'isPushedByFluid', 'onCollideWithPlayer': 'playerTouch', 'setSwingingArms': 'setAggressive',
    'attackEntityWithRangedAttack': 'performRangedAttack', 'shouldExecute': 'canUse', 'shouldContinueExecuting': 'canContinueToUse',
    'continueExecuting': 'canContinueToUse', 'startExecuting': 'start', 'resetTask': 'stop', 'updateTask': 'tick',
    'isInterruptible': 'isInterruptable', 'getAmbientSound': 'getAmbientSound', 'getHurtSound': 'getHurtSound',
    'getDeathSound': 'getDeathSound', 'getSoundVolume': 'getSoundVolume', 'playStepSound': 'playStepSound',
    'getEyeHeight': 'getEyeHeight', 'canBreatheUnderwater': 'canBreatheUnderwater', 'isNotColliding': 'checkSpawnObstruction',
    'setPositionAndRotation2': 'lerpTo', 'getMaxSpawnedInChunk': 'getMaxSpawnClusterSize', 'onImpact': 'onHit',
    'getRenderSizeModifier': 'getScale', 'fallEntity': 'causeFallDamage', 'moveEntity': 'move', 'onEntityUpdate': 'baseTick',
    'getDataManager': 'getEntityData', 'setAlwaysRenderNameTag': 'setCustomNameVisible', 'getAlwaysRenderNameTag': 'isCustomNameVisible',
}

# methods that only exist as goal overrides (so rename them everywhere in goal files)
MUTEX = {1: 'MOVE', 2: 'LOOK', 4: 'JUMP', 8: 'TARGET'}


def mutex_flags(n):
    names = [v for k, v in MUTEX.items() if n & k]
    if not names:
        return 'EnumSet.noneOf(Goal.Flag.class)'
    return 'EnumSet.of(' + ', '.join('Goal.Flag.' + x for x in names) + ')'


_rewrite = None


def structural(body, meta, cls, split_args, find_close, rewrite_calls):
    global _rewrite
    _rewrite = rewrite_calls
    body = fix_chain_assign(body)
    body = fix_dimension(body)
    body = fix_assignments(body, meta)
    body = fix_reads(body)
    body = fix_ctor(body, meta, cls, rewrite_calls)
    body = fix_attributes(body, cls, split_args, find_close)
    body = fix_datamanager(body, split_args, find_close)
    body = fix_decls(body)
    body = fix_constants(body)
    body = fix_goals(body)
    body = fix_misc_calls(body, split_args, find_close, rewrite_calls)
    return body


# --------------------------------------------------------------------------------------------------
def fix_chain_assign(body):
    """a.renderYawOffset = a.rotationYaw = expr;  ->  a.rotationYaw = expr; a.renderYawOffset = a.rotationYaw;"""
    pat = re.compile(r'(?P<r1>' + RECV + r')\.(?P<f1>renderYawOffset|rotationYawHead)\s*=\s*(?P<r2>' + RECV + r')\.(?P<f2>rotationYaw|rotationPitch)\s*=\s*(?P<e>[^;]+);')
    return pat.sub(lambda m: '%s.%s = %s;\n        %s.%s = %s.%s;' % (m.group('r2'), m.group('f2'), m.group('e'), m.group('r1'), m.group('f1'), m.group('r2'), m.group('f2')), body)


def fix_dimension(body):
    body = re.sub(r'(' + RECV + r')\.provider\.getDimension\(\)', r'DimKeys.of(\1)', body)
    return body


def fix_assignments(body, meta):
    stmt = re.compile(r'(?P<recv>' + RECV + r')\.(?P<f>motionX|motionY|motionZ|rotationYaw|rotationPitch|posX|posY|posZ)\s*(?P<op>[-+*/]?=)(?!=)\s*(?P<expr>[^;]+);')

    def rep(m):
        r, f, op, e = m.group('recv'), m.group('f'), m.group('op'), m.group('expr').strip()
        if f.startswith('motion'):
            a = f[-1]
            if op == '=':
                return 'Mot.set%s(%s, %s);' % (a, r, e)
            if op == '+=':
                return 'Mot.add%s(%s, %s);' % (a, r, e)
            if op == '-=':
                return 'Mot.add%s(%s, -(%s));' % (a, r, e)
            if op == '*=':
                return 'Mot.mul%s(%s, %s);' % (a, r, e)
            if op == '/=':
                return 'Mot.mul%s(%s, 1.0 / (%s));' % (a, r, e)
        if f in ('rotationYaw', 'rotationPitch'):
            g, s = ('getYRot', 'setYRot') if f == 'rotationYaw' else ('getXRot', 'setXRot')
            if op == '=':
                return '%s.%s(%s);' % (r, s, e)
            return '%s.%s(%s.%s() %s (%s));' % (r, s, r, g, op[0], e)
        if f.startswith('pos'):
            a = f[-1]
            if op == '=':
                return 'Mot.setPos%s(%s, %s);' % (a, r, e)
            return 'Mot.setPos%s(%s, %s.get%s() %s (%s));' % (a, r, r, a, op[0], e)
        return m.group(0)
    return stmt.sub(rep, body)


def fix_reads(body):
    body = re.sub(r'\.motion([XYZ])\b', lambda m: '.getDeltaMovement().' + m.group(1).lower(), body)
    body = re.sub(r'\.pos([XYZ])\b', lambda m: '.get' + m.group(1) + '()', body)
    body = re.sub(r'\.prevPos([XYZ])\b', lambda m: '.' + m.group(1).lower() + 'o', body)
    body = re.sub(r'\.lastTickPos([XYZ])\b', lambda m: '.' + m.group(1).lower() + 'Old', body)
    body = re.sub(r'\.rotationYaw\b', '.getYRot()', body)
    body = re.sub(r'\.rotationPitch\b', '.getXRot()', body)
    body = re.sub(r'\.height\b(?!\s*[=(])', '.getBbHeight()', body)
    body = re.sub(r'\.width\b(?!\s*[=(])', '.getBbWidth()', body)
    return body


def fix_ctor(body, meta, cls, rewrite_calls):
    # constructor (World) -> (EntityType, Level)
    registered = cls in CLASS_CONST or re.search(r'\babstract class ' + re.escape(cls) + r'\b', body) is not None
    ctor_pat = re.compile(r'(public|protected) ' + re.escape(cls) + r'\(\s*(?:World|Level) (\w+)\s*((?:,[^)]*)?)\)\s*\{')
    mm = ctor_pat.search(body) if registered else None
    while mm:
        wname = mm.group(2)
        start = mm.start()
        new_head = '%s %s(EntityType<? extends %s> type, Level %s%s) {' % (mm.group(1), cls, cls, wname, mm.group(3))
        body = body[:start] + new_head + body[mm.end():]
        # first super( call after the header: insert the type argument
        after = start + len(new_head)
        sm = re.compile(r'super\(').search(body, after)
        if sm:
            body = body[:sm.end()] + 'type, ' + body[sm.end():]
            body = body.replace('super(type, )', 'super(type)')
        mm = ctor_pat.search(body, after)
    # entity constructions of registered types get their EntityType
    def mk(m, args):
        c = m.group(1)
        if c not in CLASS_CONST:
            return None
        return 'new %s(%s)' % (c, ', '.join(['SRPEntities.%s.get()' % CLASS_CONST[c]] + args))
    body = rewrite_calls(body, r'\bnew (Entity\w+)\(', mk)

    def ctor_bodies(text):
        spans = []
        for cm in re.finditer(r'(?:public|protected) ' + re.escape(cls) + r'\([^)]*\)\s*\{', text):
            depth = 0
            i = cm.end() - 1
            while i < len(text):
                if text[i] == '{':
                    depth += 1
                elif text[i] == '}':
                    depth -= 1
                    if depth == 0:
                        break
                i += 1
            spans.append((cm.end(), i))
        return spans

    for (a, b) in reversed(ctor_bodies(body)):
        seg = body[a:b]

        def size(m):
            meta['size'] = [m.group(1).strip(), m.group(2).strip()]
            return ''
        seg = re.sub(r'\n\s*this\.setSize\(([^,;]+),\s*([^;]+)\);', size, seg)

        def fire(m):
            meta['fireImmune'] = True
            return ''
        seg = re.sub(r'\n\s*this\.isImmuneToFire\s*=\s*true;', fire, seg)
        body = body[:a] + seg + body[b:]
    body = re.sub(r'this\.stepHeight\s*=\s*([^;]+);', r'this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(\1);', body)
    body = re.sub(r'\.setPathPriority\(', '.setPathfindingMalus(', body)
    body = body.replace('PathNodeType.', 'PathType.')
    return body


def fix_attributes(body, cls, split_args, find_close):
    m = re.search(r'(?:@Override\s*)?(?:protected|public) void applyEntityAttributes\(\)\s*\{', body)
    if not m:
        return body
    start = m.end() - 1
    # find matching brace
    depth = 0
    i = start
    while i < len(body):
        if body[i] == '{':
            depth += 1
        elif body[i] == '}':
            depth -= 1
            if depth == 0:
                break
        i += 1
    block = body[start + 1:i]
    sm = re.search(r'extends\s+(\w+)', body)
    parent = sm.group(1) if sm else 'EntityMob'
    base = {'EntityMob': 'Monster.createMonsterAttributes()', 'EntityCreature': 'PathfinderMob.createMobAttributes()',
            'EntityLiving': 'Mob.createMobAttributes()', 'EntityLivingBase': 'LivingEntity.createLivingAttributes()',
            'EntityFlying': 'Mob.createMobAttributes()'}.get(parent, parent + '.createAttributes()')
    out = []
    pos = 0
    pat = re.compile(r'this\.getEntityAttribute\(')
    while True:
        mm = pat.search(block, pos)
        if not mm:
            out.append(block[pos:])
            break
        op = mm.end() - 1
        cl = find_close(block, op)
        arg = block[op + 1:cl].strip()
        rest = block[cl + 1:]
        m2 = re.match(r'\s*\.setBaseValue\(', rest)
        if not m2:
            out.append(block[pos:cl + 1])
            pos = cl + 1
            continue
        op2 = cl + 1 + m2.end() - 1
        cl2 = find_close(block, op2)
        val = block[op2 + 1:cl2].strip()
        # statement end
        semi = block.find(';', cl2)
        out.append(block[pos:mm.start()])
        out.append('builder.add(%s, %s)' % (arg, val))
        pos = cl2 + 1
    block = ''.join(out)
    block = re.sub(r'super\.applyEntityAttributes\(\);', '', block)
    new = 'public static AttributeSupplier.Builder createAttributes() {\n        AttributeSupplier.Builder builder = %s;%s        return builder;\n    }' % (base, block.rstrip() + '\n')
    # strip a preceding @Override
    pre = body[:m.start()]
    pre = re.sub(r'@Override\s*$', '', pre)
    return pre + new + body[i + 1:]


def fix_datamanager(body, split_args, find_close):
    # static keys: collect java types
    types = {}
    for m in re.finditer(r'(?:public |private |protected )?static final DataParameter<(\w+)> (\w+)\s*=', body):
        types[m.group(2)] = m.group(1)
    for m in re.finditer(r'EntityDataAccessor<(\w+)> (\w+)\s*=', body):
        types[m.group(2)] = m.group(1)

    def conv_type(t):
        return {'Byte': 'byte', 'Integer': 'int', 'Float': 'float', 'Boolean': 'boolean', 'Short': 'short', 'Long': 'long', 'Double': 'double'}.get(t)

    def cast_for(key, expr):
        t = types.get(key)
        c = conv_type(t) if t else None
        e = expr.strip()
        if not c:
            return e
        if re.fullmatch(r'-?\d+', e) and c in ('byte', 'short'):
            return '(%s) %s' % (c, e)
        if re.fullmatch(r'-?[\d.]+f?', e) and c == 'float':
            return e if e.endswith('f') else e + 'f'
        if re.fullmatch(r'(true|false)', e):
            return e
        return '(%s) (%s)' % (c, e) if c in ('byte', 'short', 'float') else e

    for kind, repl in (('__define', 'builder.define'), ('__set', None)):
        out = []
        pos = 0
        pat = re.compile(r'([\w\.\(\)]*)\.' + kind + r'\(')
        while True:
            m = pat.search(body, pos)
            if not m:
                out.append(body[pos:])
                break
            op = m.end() - 1
            cl = find_close(body, op)
            args = split_args(body[op + 1:cl])
            out.append(body[pos:m.start()])
            if kind == '__define':
                out.append('builder.define(%s, %s)' % (args[0], cast_for(args[0], args[1])))
            else:
                out.append('%s.set(%s, %s)' % (m.group(1), args[0], cast_for(args[0], args[1])))
            pos = cl + 1
        body = ''.join(out)
    body = re.sub(r'\.__get\(', '.get(', body)
    body = body.replace('EntityDataManager.__defineId(', 'SynchedEntityData.defineId(')
    body = body.replace('SynchedEntityData.__defineId(', 'SynchedEntityData.defineId(')
    body = re.sub(r'(\w+)\.__defineId\(', r'\1.defineId(', body)
    # parameter casts like (Byte)
    body = re.sub(r'\bDataParameter<', 'EntityDataAccessor<', body)
    for k, v in (('VARINT', 'INT'), ('OPTIONAL_UNIQUE_ID', 'OPTIONAL_UUID'), ('FACING', 'DIRECTION'), ('ITEM_STACK', 'ITEM_STACK')):
        body = body.replace('EntityDataSerializers.' + k, 'EntityDataSerializers.' + v)
    body = re.sub(r'\bDataSerializers\.', 'EntityDataSerializers.', body)
    body = body.replace('EntityEntityDataSerializers', 'EntityDataSerializers')
    return body


def fix_decls(body):
    body = re.sub(r'(?:protected|public) void entityInit\(\)', 'protected void defineSynchedData(SynchedEntityData.Builder builder)', body)
    body = body.replace('super.entityInit();', 'super.defineSynchedData(builder);')
    body = re.sub(r'(?:public|protected) IEntityLivingData onInitialSpawn\((?:@Nonnull )?DifficultyInstance (\w+), (?:@Nullable )?IEntityLivingData (\w+)\)',
                  r'public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance \1, MobSpawnType spawnType, @Nullable SpawnGroupData \2)', body)
    body = re.sub(r'super\.onInitialSpawn\((\w+),\s*(\w+)\)', r'super.finalizeSpawn(levelAccessor, \1, spawnType, \2)', body)
    body = re.sub(r'\bonInitialSpawn\(', 'finalizeSpawn(', body)

    def fs(m, args):
        if len(args) != 2:
            return None
        head = m.string[max(0, m.start() - 6):m.start()]
        if head.endswith('this') or head.endswith('super'):
            return '.finalizeSpawn(levelAccessor, %s, spawnType, %s)' % (args[0], args[1])
        rm = re.search(r'(' + RECV + r')$', m.string[:m.start()])
        recv = rm.group(1) if rm else 'this'
        return '.finalizeSpawn((ServerLevel) %s.level(), %s, MobSpawnType.MOB_SUMMONED, %s)' % (recv, args[0], args[1])
    body = _rewrite(body, r'\.finalizeSpawn\(', fs)
    body = re.sub(r'public boolean canDespawn\(\)', 'public boolean removeWhenFarAway(double distanceToClosestPlayer)', body)
    body = re.sub(r'protected void playStepSound\(BlockPos (\w+), Block (\w+)\)', r'protected void playStepSound(BlockPos \1, BlockState \2)', body)
    body = re.sub(r'public boolean canAttackClass\(Class<\? extends LivingEntity> (\w+)\)', r'public boolean canAttackType(EntityType<?> \1)', body)
    body = re.sub(r'(?:public|protected) void fallEntity\(float (\w+), float (\w+)\)', r'public boolean causeFallDamage(float \1, float \2, DamageSource damageSource)', body)
    body = re.sub(r'super\.fallEntity\((\w+),\s*(\w+)\)', r'super.causeFallDamage(\1, \2, damageSource)', body)
    body = re.sub(r'(public|protected) float getEyeHeight\(\)', r'protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions)', body)
    for old, new in METHOD_RENAMES.items():
        if old != new:
            body = re.sub(r'\b' + old + r'\s*\(', new + '(', body)
    # replace remaining placeholders
    body = body.replace('.defineSynchedDataPlaceholder()', '.defineSynchedData(builder)')
    return body


def fix_constants(body):
    sound_holders = SOUND_HOLDERS

    def blocks(m):
        n = m.group(1)
        return 'Blocks.' + BLOCK_CONST.get(n, n.upper())
    body = re.sub(r'\bBlocks\.([A-Za-z_]\w*)\b', blocks, body)

    def items(m):
        n = m.group(1)
        return 'Items.' + ITEM_CONST.get(n, n.upper())
    body = re.sub(r'\bItems\.([A-Za-z_]\w*)\b', items, body)

    def sounds(m):
        n = m.group(1)
        if n in SOUND_CONST:
            n = SOUND_CONST[n]
        else:
            n = re.sub(r'^(ENTITY|BLOCK|ITEM)_', '', n)
        r = 'SoundEvents.' + n
        if n in sound_holders:
            r += '.value()'
        return r
    body = re.sub(r'\bSoundEvents\.([A-Z0-9_]+)\b(?!\.value)', sounds, body)

    def particles(m):
        n = m.group(1)
        return 'ParticleTypes.' + PARTICLE_CONST.get(n, n)
    body = re.sub(r'\bEnumParticleTypes\.([A-Z0-9_]+)\b', particles, body)
    body = re.sub(r'\bParticleTypes\.([A-Z0-9_]+)\b', lambda m: 'ParticleTypes.' + PARTICLE_CONST.get(m.group(1), m.group(1)), body)

    body = re.sub(r'\bMobEffects\.([A-Za-z_]\w*)\b', lambda m: 'MobEffects.' + MOBEFFECT_CONST.get(m.group(1), m.group(1).upper()), body)
    body = re.sub(r'\b(?:SharedMonsterAttributes|Attributes)\.([A-Za-z_]\w*)\b',
                  lambda m: 'Attributes.' + ATTRIBUTE_CONST.get(m.group(1), m.group(1) if m.group(1).isupper() else m.group(1).upper()), body)
    body = body.replace('Attributes.ARMOR_TOUGHNESS', 'Attributes.ARMOR_TOUGHNESS')
    body = re.sub(r'\bEnumFacing\.(\w+)', lambda m: 'Direction.' + m.group(1), body)
    body = re.sub(r'\bEnumDifficulty\.(\w+)', lambda m: 'Difficulty.' + m.group(1), body)
    body = re.sub(r'\bEnumHand\.(\w+)', lambda m: 'InteractionHand.' + m.group(1), body)
    body = re.sub(r'\bEntityEquipmentSlot\.(\w+)', lambda m: 'EquipmentSlot.' + m.group(1), body)
    body = re.sub(r'\bSoundCategory\.(\w+)', lambda m: 'SoundSource.' + m.group(1), body)
    return body


def fix_goals(body):
    def mut(m):
        return 'this.setFlags(%s);' % mutex_flags(int(m.group(1)))
    body = re.sub(r'this\.setMutexBits\((\d+)\);', mut, body)
    body = re.sub(r'new HurtByTargetGoal\(this, (?:true|false)\)', 'new HurtByTargetGoal(this)', body)
    return body


DAMAGE_TYPES = {
    'generic': ('GENERIC', 'generic'), 'magic': ('MAGIC', 'magic'), 'inFire': ('IN_FIRE', 'inFire'), 'onFire': ('ON_FIRE', 'onFire'),
    'outOfWorld': ('FELL_OUT_OF_WORLD', 'fellOutOfWorld'), 'inWall': ('IN_WALL', 'inWall'), 'drown': ('DROWN', 'drown'), 'fall': ('FALL', 'fall'),
    'cactus': ('CACTUS', 'cactus'), 'lava': ('LAVA', 'lava'), 'wither': ('WITHER', 'wither'), 'starve': ('STARVE', 'starve'),
    'anvil': ('FALLING_ANVIL', 'fallingAnvil'), 'fallingBlock': ('FALLING_BLOCK', 'fallingBlock'), 'lightningBolt': ('LIGHTNING_BOLT', 'lightningBolt'),
    'dragonBreath': ('DRAGON_BREATH', 'dragonBreath'), 'flyIntoWall': ('FLY_INTO_WALL', 'flyIntoWall'),
}


def fix_misc_calls(body, split_args, find_close, rewrite_calls):
    # damage sources
    names = '|'.join(DAMAGE_TYPES)
    body = re.sub(r'([\w\.\(\)]+?)\s*==\s*DamageSource\.(' + names + r')\b', lambda m: '%s.is(DamageTypes.%s)' % (m.group(1), DAMAGE_TYPES[m.group(2)][0]), body)
    body = re.sub(r'([\w\.\(\)]+?)\s*!=\s*DamageSource\.(' + names + r')\b', lambda m: '!%s.is(DamageTypes.%s)' % (m.group(1), DAMAGE_TYPES[m.group(2)][0]), body)
    body = re.sub(r'DamageSource\.(' + names + r')\b', lambda m: 'this.damageSources().%s()' % DAMAGE_TYPES[m.group(1)][1], body)
    body = _rewrite(body, r'DamageSource\.causeMobDamage\(', lambda m, a: '%s.damageSources().mobAttack(%s)' % (re.sub(r'^\(\w+\)\s*', '', a[0]), re.sub(r'^\(\w+\)\s*', '', a[0])))
    body = re.sub(r'DamageSource\.causeThrownDamage\(', 'this.damageSources().thrown(', body)
    body = re.sub(r'DamageSource\.causePlayerDamage\(', 'this.damageSources().playerAttack(', body)

    body = re.sub(r'(' + RECV + r')\.loadedEntityList\b', r'SRPEntityUtil.allEntities(\1)', body)
    body = re.sub(r'(' + RECV + r')\.getCustomNameTag\(\)', r'SRPEntityUtil.getCustomNameTag(\1)', body)
    body = re.sub(r'(' + RECV + r')\.setCustomNameTag\(', r'SRPEntityUtil.setCustomNameTag(\1, ', body)
    # isFullBlock
    out = []
    pos = 0
    pat = re.compile(r'(' + RECV + r')\.getBlockState\(')
    while True:
        m = pat.search(body, pos)
        if not m:
            out.append(body[pos:])
            break
        op = m.end() - 1
        cl = find_close(body, op)
        if cl > 0 and body[cl + 1:cl + 1 + len('.isFullBlock()')] == '.isFullBlock()':
            out.append(body[pos:m.start()])
            out.append('%s.getBlockState(%s).isCollisionShapeFullBlock(%s, %s)' % (m.group(1), body[op + 1:cl], m.group(1), body[op + 1:cl]))
            pos = cl + 1 + len('.isFullBlock()')
        else:
            out.append(body[pos:op + 1])
            pos = op + 1
    body = ''.join(out)

    def sp(m, args):
        if len(args) < 9:
            return None
        return '.sendParticles(%s)' % ', '.join(args[:9])
    body = rewrite_calls(body, r'\.sendParticlesServer\(', sp)

    def ap(m, args):
        if len(args) < 7:
            return None
        return '.addParticle(%s)' % ', '.join(args[:7])
    body = rewrite_calls(body, r'\.addParticleWorld\(', ap)

    def bp(m, args):
        if len(args) == 3:
            return 'BlockPos.containing(%s)' % ', '.join(args)
        if len(args) == 1:
            if args[0].startswith('(Entity)'):
                a = re.sub(r'^\(Entity\)\s*', '', args[0])
                return '%s.blockPosition()' % a
            return 'BlockPos.containing(%s)' % args[0]
        return None
    body = rewrite_calls(body, r'\bnew BlockPos\(', bp)

    body = re.sub(r'EntityList\.getKeyEntity\(\(Entity\)\s*([^()]+)\)', r'BuiltInRegistries.ENTITY_TYPE.getKey(\1.getType())', body)
    body = re.sub(r'EntityList\.getKeyEntity\(([^()]+)\)', r'BuiltInRegistries.ENTITY_TYPE.getKey(\1.getType())', body)
    body = re.sub(r'EntityList\.getKeyClass\(([^()]+)\)', r'EntityListPlaceholder.getKeyClass(\1)', body)
    body = _rewrite(body, r'EntityList\.createEntityByIDFromName\(', lambda m, a: 'SRPEntityUtil.create(%s, %s)' % (a[0], a[1]))
    body = _rewrite(body, r'(?:MobEffect|Potion)\.getPotionFromResourceLocation\(', lambda m, a: 'SRPEntityUtil.effect(%s)' % re.sub(r'^\(String\)\s*', '', a[0]))
    body = re.sub(r'\bPotion (\w+) = SRPEntityUtil\.effect', r'Holder<MobEffect> \1 = SRPEntityUtil.effect', body)
    body = re.sub(r'\bMobEffect (\w+) = SRPEntityUtil\.effect', r'Holder<MobEffect> \1 = SRPEntityUtil.effect', body)
    body = re.sub(r'new TextComponentString\(', 'Component.literal(', body)
    body = re.sub(r'new TextComponentTranslation\(', 'Component.translatable(', body)
    body = re.sub(r'\(Component\)\s*(Component\.(?:literal|translatable))', r'\1', body)

    def rl(m, a):
        if len(a) == 1:
            return 'ResourceLocation.parse(%s)' % a[0]
        if len(a) == 2:
            return 'ResourceLocation.fromNamespaceAndPath(%s, %s)' % (a[0], a[1])
        return None
    body = _rewrite(body, r'\bnew ResourceLocation\(', rl)

    # registry holders of the mod's own blocks / items need .get()
    body = re.sub(r'\bSRPBlocks\.([A-Za-z_]\w*)\b(?!\s*\.get\(\))(?!\w)', lambda m: ('SRPBlocks.%s.get()' % m.group(1)) if m.group(1) in BLOCK_FIELDS else m.group(0), body)
    body = re.sub(r'\bSRPItems\.([A-Za-z_]\w*)\b(?!\s*\.get\(\))(?!\w)', lambda m: ('SRPItems.%s.get()' % m.group(1)) if m.group(1) not in ('ITEMS',) else m.group(0), body)

    def mv(m, args):
        if len(args) == 4 and 'MoverType' in args[0]:
            return '.move(%s, new Vec3(%s, %s, %s))' % (args[0], args[1], args[2], args[3])
        return None
    body = rewrite_calls(body, r'\.move\(', mv)

    def gd(m, args):
        if len(args) == 3:
            return '.distanceToSqrt(%s, %s, %s)' % tuple(args)
        return None
    body = rewrite_calls(body, r'\.getDistance\(', gd)
    body = body.replace('.distanceToSqrt(', '.distanceToXYZ(')

    # --- network: SRPMain.network.sendX / SRPNetwork.CHANNEL.sendTo -> PacketDistributor
    pkt = {'SRPPacketParticle': 'ParticlePayload', 'SRPPacketMovingSound': 'MovingSoundPayload', 'SRPPacketEntityBodyDead': 'EntityBodyDeadPayload',
           'SRPPacketEntityBodyHit': 'EntityBodyHitPayload', 'MsgQlipShake': 'QlipShakePayload'}

    def fixpkt(a):
        a = re.sub(r'^\(IMessage\)\s*', '', a.strip())
        for k, v in pkt.items():
            a = a.replace('new ' + k + '(', 'new ' + v + '(')
        return a
    body = _rewrite(body, r'\bSRPMain\.network\.sendToAll\(', lambda m, a: 'PacketDistributor.sendToAllPlayers(%s)' % fixpkt(a[0]))
    body = _rewrite(body, r'\bSRPMain\.network\.sendToServer\(', lambda m, a: 'PacketDistributor.sendToServer(%s)' % fixpkt(a[0]))
    body = _rewrite(body, r'\bSRPNetwork\.CHANNEL\.sendToServer\(', lambda m, a: 'PacketDistributor.sendToServer(%s)' % fixpkt(a[0]))
    body = _rewrite(body, r'\bSRPNetwork\.CHANNEL\.sendTo\(', lambda m, a: 'PacketDistributor.sendToPlayer(%s, %s)' % (a[1], fixpkt(a[0])))

    def sd(m, a):
        lv = re.sub(r'^DimKeys\.of\((.*)\)$', r'\1', a[1])
        return 'PacketDistributor.sendToPlayersInDimension((ServerLevel) %s, %s)' % (lv, fixpkt(a[0]))
    body = _rewrite(body, r'\bSRPMain\.network\.sendToDimension\(', sd)
    return body
