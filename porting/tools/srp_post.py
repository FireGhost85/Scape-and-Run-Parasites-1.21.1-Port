"""Text-level fixes that can be re-applied to already translated (and partly hand edited) Java files.

usage: python srp_post.py <file-or-dir> [...]
"""
import os
import re
import sys

from srp_util import split_args, rewrite_calls

RULES = [
    (r'\.getDamageType\(\)', '.getMsgId()'),
    (r'\.getRegistryNamePlaceholder\(\)', '.builtInRegistryHolder().key().location()'),
    (r'\.getAbilities\(\)\.disableDamage\b', '.getAbilities().invulnerable'),
    (r'(?<=track)\.getCooldown\(', '.getCooldownPercent('),
    (r'(?<=[\w)])\.setCooldown\(', '.addCooldown('),
    (r'DefaultRandomPos\.findRandomTargetBlockAwayFrom\(', 'DefaultRandomPos.getPosAway('),
    (r'\.sendSystemMessage\(\(Component\)new Component\(([^;]*)\)\);', r'.sendSystemMessage(Component.literal(\1));'),
    (r'new TextComponentString\(', 'Component.literal('),
    (r'TheEndGatewayBlock', 'EndGatewayBlock'),
    (r'(?:\b\w+\.)?(?:level\(\)\.)?removeEntity\(\(Entity\)\s*([\w.]+)\)', r'\1.discard()'),
    (r'\(Component\)\s*Component\.', 'Component.'),
    (r'\.setItemStackToSlot\(', '.setItemSlot('),
    (r'\.getEntityItem\(\)', '.getItem()'),
    (r'\.addExperience\(', '.giveExperiencePoints('),
    (r'\b(playerIn|player|pl)\.experience\b', r'\1.experienceProgress'),
    (r'entityData\.set\(HIT, (\d+)\)', r'entityData.set(HIT, (byte) \1)'),
    (r'((?:this\.level\(\))|\b\w+)\.getBlockState\(([^()]*(?:\([^()]*\)[^()]*)*)\)\.isFullCube\(\)', r'\1.getBlockState(\2).isCollisionShapeFullBlock(\1, \2)'),
    (r'super\.causeFallDamage\((\w+),\s*([\w.]+)\);', r'return super.causeFallDamage(\1, \2, damageSource);'),
    (r'\n\s*int meta = block\.getMetaFromStatePlaceholder\(iblockstate\);', ''),
    (r'this\.addToBlockInv\(name \+ ";" \+ meta\);', 'this.addToBlockInv(BlockIds.stateString(iblockstate));'),
    (r'!block\.canEntityDestroy\(iblockstate, \(BlockGetter\)this\.level\(\), blockpos, \(Entity\)this\)', '!iblockstate.canEntityDestroy(this.level(), blockpos, this)'),
    (r'(?<!Despawn)Event\.Result result;', 'MobDespawnEvent.Result result;'),
    (r'\(result = EventHooks\.canEntityDespawn\(\(Mob\)this\)\) != Event\.Result\.DEFAULT', '(result = SRPEntityUtil.despawnResult(this)) != MobDespawnEvent.Result.DEFAULT'),
    (r'(?<!Despawn)Event\.Result\.DENY', 'MobDespawnEvent.Result.DENY'),
    (r'\.isUnblockable\(\)', '.is(DamageTypeTags.BYPASSES_ARMOR)'),
    (r'protected void checkDespawn\(\)', 'public void checkDespawn()'),
    (r'\.hitVec\b', '.getLocation()'),
    (r'\.velocityChanged\b', '.hurtMarked'),
    (r'\.isOnSameTeam\(', '.isAlliedTo('),
    (r'\.toLong\(\)', '.asLong()'),
    (r'DefaultRandomPos\.findRandomTarget\(', 'DefaultRandomPos.getPos('),
    (r'DefaultRandomPos\.getLandPos\(', 'LandRandomPos.getPos('),
    (r'EntitySelector\.NOT_SPECTATING', 'EntitySelector.NO_SPECTATORS'),
    (r'EntitySelector\.CAN_AI_TARGET', 'EntitySelector.NO_CREATIVE_OR_SPECTATOR'),
    (r'\.attackedAtYaw\b', '.hurtDir'),
    (r'\.maxHurtTime\b', '.hurtDuration'),
    (r'\.getAge\(\)', '.getNoActionTime()'),
    (r'\.isWithinHomeDistanceFromPosition\(', '.isWithinRestriction('),
    (r'\.getFinalPathPoint\(\)', '.getEndNode()'),
    (r'\.getArmorVisibility\(\)', '.getArmorCoverPercentage()'),
    (r'\.sendBlockBreakProgress\(', '.destroyBlockProgress('),
    (r'\.distanceSqXYZ\(', '.distToLowCornerSqr('),
    (r'(\.getNavigation\(\))\.setSpeed\(', r'\1.setSpeedModifier('),
    (r'\.shrink\(([0-9]*\.[0-9]+)\)(?=[;)\s])', r'.deflate(\1)'),
    (r'(\w+)\.getDistanceSqToCenter\(([^()]+)\)', r'\1.distanceToSqr(Vec3.atCenterOf(\2))'),
    (r'Predicates\.alwaysTrue\(\)', '(e -> true)'),
    (r'\.getEntityId\(\)', '.getId()'),
    (r'Entity::getEntityId', 'Entity::getId'),
    (r'\.canAttackClass\((\w+)\.getClass\(\)\)', r'.canAttackType(\1.getType())'),
    (r'TargetGoal\.isSuitableTarget\(', 'SRPEntityUtil.isSuitableTarget('),
    (r'\.isSwingInProgress\b', '.swinging'),
    (r'\.swingProgressInt\b', '.swingTime'),
    (r'\.getVerticalFaceSpeed\(\)', '.getMaxHeadXRot()'),
    (r'\.getPathPriority\(', '.getPathfindingMalus('),
    (r'(?<=[\w)])\.getBrightness\(\)', '.getLightLevelDependentMagicValue()'),
    (r'(SRPBlocks\.\w+\.get\(\))\.getStateFromMeta\((\d+)\)', r'BlockIds.legacyState(\1, \2)'),
    (r'\.getLightValue\(\)', '.getLightEmission()'),
    (r'(\w+)\.getMaterial\(\) == Material\.portal', r'\1.getBlock() instanceof EndPortalBlock'),
    (r'(\w+)\.getMaterial\(\) != Material\.circuits', r'!SRPEntityUtil.isCircuits(\1)'),
    (r'(\w+)\.getMaterial\(\) == Material\.circuits', r'SRPEntityUtil.isCircuits(\1)'),
    (r'(\w+(?:\.\w+\([^()]*\))*)\.getMaterialPlaceholder\(\)\.isSolid\(\)', r'\1.isSolid()'),
    (r'EnderTeleportEvent (\w+) = new EnderTeleportEvent\(\(LivingEntity\)this, (\w+), (\w+), (\w+), 0\.0f\);\s*if \(MinecraftForge\.EVENT_BUS\.post\(\(Event\)\1\)\) \{',
     r'EntityTeleportEvent.EnderEntity \1 = new EntityTeleportEvent.EnderEntity((LivingEntity)this, \2, \3, \4);\n        if (NeoForge.EVENT_BUS.post(\1).isCanceled()) {'),
    (r'\.getMaterial\(\)\.blocksMovement\(\)', '.blocksMotion()'),
    (r'\.isAnyLiquid\(', '.containsAnyLiquid('),
    (r'\.hasModifier\(([A-Z][A-Z_0-9]*)\)', r'.hasModifier(\1.id())'),
    (r'\.applyModifier\(([A-Z][A-Z_0-9]*)\)', r'.addTransientModifier(\1)'),
    (r'\.removeModifier\(([A-Z][A-Z_0-9]*)\)', r'.removeModifier(\1.id())'),
    (r'\bresult\.entityHit\b', 'SRPEntityUtil.hitEntity(result)'),
    (r'protected ParticleTypes getParticleType\(\)', 'protected ParticleOptions getTrailParticle()'),
    (r'(\(EntityType<[^>]*> (\w+), Level (\w+)[^)]*\) \{\s*)this\(\3\b', r'\1this(\2, \3'),
    (r'Lists\.newArrayList\(\)', 'new ArrayList<>()'),
    (r'Maps\.newHashMap\(\)', 'new HashMap<>()'),
    (r'\.getDoubleAt\(', '.getDouble('),
    (r'this\.newDoubleNBTList\(new double\[\]\{([^}]*)\}\)', r'this.newDoubleList(\1)'),
    (r'\.distanceSqPos\(', '.distSqr('),
    (r'\bthis\.getParticleType\(\)', 'this.getTrailParticle()'),
    (r'public boolean isInRangeToRenderDist\(double (\w+)\)', r'@Override\n    public boolean shouldRenderAtSqrDistance(double \1)'),
    (r'\bthis\.setBeenAttacked\(\)', 'this.markHurt()'),
    (r'\.fallingBlock\(\)', '.fallingBlock(this)'),
    (r'\.entityDropItem\(', '.spawnAtLocation('),
    (r'protected float getVoicePitch\(\)', 'public float getVoicePitch()'),
    (r'\bthis\.hasNoGravity\(\)', 'this.isNoGravity()'),
    (r'\bthis\.handleWaterMovement\(\)', 'this.updateInWaterStateAndDoFluidPushing()'),
    (r'\n\s*this\.preventEntitySpawning = true;', ''),
    (r'\.getMaterialPlaceholder\(\) != Material\.water', '.getFluidState().is(FluidTags.WATER) == false'),
    (r'\.getMaterialPlaceholder\(\) == Material\.water', '.getFluidState().is(FluidTags.WATER)'),
    (r'\bthis\.inWater\b(?!\s*=)', 'this.isInWater()'),
    (r'\.isPassenger\((?=[^)])', '.hasPassenger('),
    (r'\bthis\.getVectorForRotation\(', 'this.calculateViewVector('),
    (r'\.getLeashed\(\)', '.isLeashed()'),
    (r'f \+= EnchantmentHelper\.getModifierForCreature\(\(ItemStack\)this\.getMainHandItem\(\), \(EnumCreatureAttribute\)\(\(LivingEntity\)entityIn\)\.getMobTypePlaceholder\(\)\);',
     'if (this.level() instanceof ServerLevel serverLevel) {\n                        f = EnchantmentHelper.modifyDamage(serverLevel, this.getMainHandItem(), entityIn, this.damageSources().mobAttack(this), f);\n                    }'),
    (r'(public (\w+)\()Level worldIn\) \{(\s*)super\(worldIn\);', r'\1EntityType<? extends \2> type, Level worldIn) {\3super(type, worldIn);'),
    (r'this\.finalizeSpawn\(levelAccessor, difficulty, spawnType, livingdata\);',
     'if (this.level() instanceof ServerLevelAccessor accessor) {\n            this.finalizeSpawn(accessor, difficulty, MobSpawnType.NATURAL, livingdata);\n        }'),
    (r'\}, ([0-9.]+)f, ([0-9.]+), ([0-9.]+)\)\);(?<=\)\)\);)', r'}, \1f, \2, \3, (Predicate<LivingEntity>)(e -> true)));'),
    (r'(return new (\w+)\()this\.level\(\)\);', r'\1(EntityType<? extends \2>)this.getType(), this.level());'),
    (r'new ResourceLocation\(', 'ResourceLocation.parse('),
    (r'(\w+)\.finalizeSpawn\(\(ServerLevel\) this\.level\(\),', r'\1.finalizeSpawn((ServerLevel) \1.level(),'),
    (r'EntitySelector\.selectAnything', '(e -> e.isAlive())'),
    (r'\.getEntitiesInAABBexcluding\(', '.getEntities('),
    (r'\.getActualHeight\(\)', '.getMaxBuildHeight()'),
    (r'import com\.dhanantry\.scapeandrunparasites\.network\.MsgQlipShake;', 'import com.dhanantry.scapeandrunparasites.network.QlipShakePayload;'),
    (r'(\w+)\.getBlock\(\)\.isAir\(\1, \(BlockGetter\)\w+, \w+\)', r'\1.isAir()'),
    (r'(\w+)\.getBlock\(\)\.isLeaves\(\1, \(BlockGetter\)\w+, \w+\)', r'\1.is(BlockTags.LEAVES)'),
    (r'(\w+)\.getBlock\(\)\.isWood\(\(BlockGetter\)\w+, \w+\)', r'\1.is(BlockTags.LOGS)'),
    (r'(\w+)\.getBlock\(\)\.isLeaves\((\w+), \(BlockGetter\)\w+, \w+\)', r'\2.is(BlockTags.LEAVES)'),
    (r'(SRPBlocks\.\w+\.get\(\))\.canBlockStay\((\w+), ([^,]+), [^()]*\)', r'\1.defaultBlockState().canSurvive(\2, \3)'),
    (r'\bIProperty\b', 'Property'),
    (r'\bBlockStairs\.EnumHalf\.', 'Half.'),
    (r'\bBlockStairs\.EnumShape\.', 'StairsShape.'),
    (r'\bBlockStairs\.', 'StairBlock.'),
    (r'\bBlockSlab\.EnumBlockHalf\.', 'SlabType.'),
    (r'\bBlockSlab\.HALF\b', 'SlabBlock.TYPE'),
    (r'BossEvent\.Color\b', 'BossEvent.BossBarColor'),
    (r'BossEvent\.Overlay\b', 'BossEvent.BossBarOverlay'),
    (r'public void addTrackingPlayer\(ServerPlayer (\w+)\) \{\s*super\.addTrackingPlayer\(', r'public void startSeenByPlayer(ServerPlayer \1) {\n        super.startSeenByPlayer('),
    (r'public void removeTrackingPlayer\(ServerPlayer (\w+)\) \{\s*super\.removeTrackingPlayer\(', r'public void stopSeenByPlayer(ServerPlayer \1) {\n        super.stopSeenByPlayer('),
    (r'SRPEntityUtil\.setCustomNameTag\(super,', 'SRPEntityUtil.setCustomNameTag(this,'),
    (r'\bthis\.distToCenterSqr\(([^()]+)\)', r'this.distanceToSqr(Vec3.atCenterOf(\1))'),
    (r'\.hasUniqueId\(', '.hasUUID('),
    (r'\.getDyeDamage\(\)', '.getId()'),
    (r'\.checkNoEntityCollision\(this\.getBoundingBox\(\), \(Entity\)this\)', '.isUnobstructed((Entity)this)'),
    (r'\bthis\.setAir\(', 'this.setAirSupply('),
    (r'\bthis\.getAir\(\)', 'this.getAirSupply()'),
    (r'\w+\.canEntitySpawn\(\(Entity\)this\)', 'true'),
    (r'public void onUpdateMoveHelper\(\)', 'public void tick()'),
    (r'\bentitymovehelper\.get([XYZ])\(\)', r'entitymovehelper.getWanted\1()'),
    (r'\bthis\.get([XYZ])\(\) - (\w+)\.this\.get\1\(\)', r'this.getWanted\1() - \2.this.get\1()'),
    (r'super\.causeFallDamage\((\w+), (\w+ \* [0-9.]+f)\)', r'super.causeFallDamage(\1, \2, source)'),
    (r'this\.level\(\)\.addWeatherEffect\(\(Entity\)new LightningBolt\(this\.level\(\), \(double\)([\w.()]+), \(double\)([\w.()]+), \(double\)([\w.()]+), true\)\)', r'SRPEntityUtil.lightning(this.level(), \1, \2, \3, true)'),
]


RECV = r'((?:\w+\.)*\w+(?:\(\))?(?:\.\w+(?:\(\))?)*)'


def fix_calls(text):
    # world.rayTraceBlocks(a, b, false, true, false) -> SRPEntityUtil.rayTraceBlocks(world, a, b)
    out = []
    pos = 0
    pat = re.compile(RECV + r'\.rayTraceBlocks\(')
    while True:
        m = pat.search(text, pos)
        if not m:
            out.append(text[pos:])
            break
        op = m.end() - 1
        # find matching paren
        depth = 0
        i = op
        while i < len(text):
            if text[i] == '(':
                depth += 1
            elif text[i] == ')':
                depth -= 1
                if depth == 0:
                    break
            i += 1
        args = split_args(text[op + 1:i])
        out.append(text[pos:m.start()])
        if len(args) == 5:
            out.append('SRPEntityUtil.rayTraceBlocks(%s, %s, %s)' % (m.group(1), args[0], args[1]))
        else:
            out.append(text[m.start():i + 1])
        pos = i + 1
    text = ''.join(out)

    def tele(m, args):
        if len(args) == 3:
            return '.randomTeleport(%s, %s, %s, true)' % tuple(args)
        return None
    text = rewrite_calls(text, r'\.attemptTeleport\(', tele)

    def expl(m, args):
        if len(args) == 7:
            return '.explode(%s, %s, %s, %s, %s, %s, %s ? Level.ExplosionInteraction.MOB : Level.ExplosionInteraction.NONE)' % tuple(args)
        return None
    text = rewrite_calls(text, r'\.newExplosion\(', expl)

    def cpath(m, args):
        if len(args) == 1 and not args[0].startswith('Vec3'):
            return '.createPath(%s, 0)' % args[0]
        if len(args) == 3:
            return '.createPath(%s, %s, %s, 0)' % tuple(args)
        return None
    text = rewrite_calls(text, r'\.getNavigation\(\)\.createPath\(', lambda m, a: ('.getNavigation()' + cpath(m, a)) if cpath(m, a) else None)

    def dxyz(m, args):
        if len(args) == 3:
            return 'Math.sqrt(%s.distanceToSqr(%s, %s, %s))' % (m.group(1), args[0], args[1], args[2])
        return None
    text = rewrite_calls(text, RECV + r'\.distanceToXYZ\(', dxyz)

    def coll(m, args):
        if len(args) == 2:
            return '%s.noCollision(%s, %s)#ISEMPTY#' % (m.group(1), args[0], args[1])
        return None
    text = rewrite_calls(text, RECV + r'\.getCollisionBoxes\(', coll)
    text = text.replace('#ISEMPTY#.isEmpty()', '')

    def kb(m, args):
        if len(args) == 4:
            return '%s.knockback(%s, %s, %s)' % (m.group(1), args[1], args[2], args[3])
        return None
    text = rewrite_calls(text, RECV + r'\.knockback\(', kb)

    def elc(m, args):
        if len(args) == 2:
            a0 = re.sub(r'^\(ResourceLocation\)', '', args[0].strip())
            a1 = re.sub(r'^\(Level\)', '', args[1].strip())
            return 'SRPEntityUtil.create(%s, %s)' % (a0, a1)
        return None
    text = rewrite_calls(text, r'EntityList\.createEntityByIDFromName\(', elc)

    def net(m, args):
        if len(args) == 2:
            mm = re.match(r'\(IMessage\)new Msg(\w+)\((.*)\)$', args[0].strip(), re.S)
            if mm:
                return 'PacketDistributor.sendToPlayer((ServerPlayer)%s, new %sPayload(%s))' % (re.sub(r'^\(ServerPlayer\)', '', args[1].strip()), mm.group(1), mm.group(2))
        return None
    text = rewrite_calls(text, r'SRPNetwork\.CHANNEL\.sendTo\(', net)

    def spp(m, args):
        args = [a for a in args if a.strip()]
        if len(args) >= 10 and args[1] in ('true', 'false'):
            recv = m.group(1)
            if not recv.startswith('((ServerLevel)') and recv != 'worldServer':
                recv = '((ServerLevel)%s)' % recv
            return '%s.sendParticles(%s, %s)' % (recv, args[0], ', '.join(args[2:10]))
        return None
    text = rewrite_calls(text, RECV + r'\.spawnParticle\(', spp)
    text = rewrite_calls(text, r'(\(\(ServerLevel\)[\w.]+(?:\(\))?\))\.spawnParticle\(', spp)

    OPS = {'0': 'ADD_VALUE', '1': 'ADD_MULTIPLIED_BASE', '2': 'ADD_MULTIPLIED_TOTAL'}

    def attrmod(m, args):
        if len(args) == 4 and args[1].startswith('"') and args[3] in OPS:
            snake = re.sub(r'[^a-z0-9_]+', '_', args[1].strip('"').lower()).strip('_')
            return ('new AttributeModifier(ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "%s"), %s, AttributeModifier.Operation.%s)'
                    % (snake, args[2], OPS[args[3]]))
        return None
    text = rewrite_calls(text, r'new AttributeModifier\(', attrmod)
    text = re.sub(r'(AttributeModifier\.Operation\.\w+\))\.setSaved\(false\)', r'\1', text)

    def mv(m, args):
        if len(args) == 4 and args[0].startswith('MoverType'):
            return '%s.move(%s, new Vec3(%s, %s, %s))' % (m.group(1), args[0], args[1], args[2], args[3])
        return None
    text = rewrite_calls(text, RECV + r'\.move\(', mv)
    return text



def fix_block_particles(text):
    """int id = Block.getId((BlockState)X); ... addParticle(ParticleTypes.BLOCK, ... -> BlockParticleOption"""
    pat = re.compile(r'int (\w+) = Block\.getId\(\(BlockState\)\s*([^;]+?)\);')
    out = []
    pos = 0
    while True:
        m = pat.search(text, pos)
        if not m:
            out.append(text[pos:])
            break
        tail_end = min(len(text), m.end() + 1500)
        tail = text[m.end():tail_end]
        if 'ParticleTypes.BLOCK,' not in tail:
            out.append(text[pos:m.end()])
            pos = m.end()
            continue
        out.append(text[pos:m.start()])
        out.append('BlockState %s = %s;' % (m.group(1), m.group(2)))
        tail = tail.replace('ParticleTypes.BLOCK,', 'new BlockParticleOption(ParticleTypes.BLOCK, %s),' % m.group(1))
        out.append(tail)
        pos = tail_end
    return ''.join(out)



def fix_fall(text):
    """public boolean causeFallDamage(...) { ... } without a return gets `return false;` (the 1.12 method was void)"""
    out = []
    pos = 0
    pat = re.compile(r'public boolean causeFallDamage\(float (\w+), float (\w+), DamageSource (\w+)\) \{')
    while True:
        m = pat.search(text, pos)
        if not m:
            out.append(text[pos:])
            break
        start = m.end() - 1
        depth = 0
        i = start
        while i < len(text):
            if text[i] == '{':
                depth += 1
            elif text[i] == '}':
                depth -= 1
                if depth == 0:
                    break
            i += 1
        body = text[start:i]
        if 'return' not in body:
            body = body + '    return false;\n    '
        out.append(text[pos:start])
        out.append(body)
        pos = i
    return ''.join(out)



def fix_mob_interact(text):
    """protected boolean mobInteract(...) { ... return true/false ... } -> InteractionResult"""
    m = re.search(r'protected boolean mobInteract\(Player (\w+), InteractionHand (\w+)\) \{', text)
    if not m:
        return text
    start = m.end() - 1
    depth = 0
    i = start
    while i < len(text):
        if text[i] == '{':
            depth += 1
        elif text[i] == '}':
            depth -= 1
            if depth == 0:
                break
        i += 1
    body = text[start:i + 1]
    body = re.sub(r'return true;', 'return InteractionResult.SUCCESS;', body)
    body = re.sub(r'return false;', 'return InteractionResult.PASS;', body)
    head = 'protected InteractionResult mobInteract(Player %s, InteractionHand %s) ' % (m.group(1), m.group(2))
    return text[:m.start()] + head + body + text[i + 1:]



SET_SIZE = """
    private net.minecraft.world.entity.EntityDimensions srpSize;

    /** The 1.12 setSize(width, height): the entity dimensions are replaced and the bounding box refreshed. */
    protected void setSize(float width, float height) {
        this.srpSize = net.minecraft.world.entity.EntityDimensions.scalable(width, height);
        this.refreshDimensions();
    }

    @Override
    public net.minecraft.world.entity.EntityDimensions getDimensions(net.minecraft.world.entity.Pose pose) {
        return this.srpSize != null ? this.srpSize : super.getDimensions(pose);
    }
"""

SET_SIZE_LIVING = SET_SIZE.replace('public net.minecraft.world.entity.EntityDimensions getDimensions(net.minecraft.world.entity.Pose pose) {', 'protected net.minecraft.world.entity.EntityDimensions getDefaultDimensions(net.minecraft.world.entity.Pose pose) {').replace('super.getDimensions(pose)', 'super.getDefaultDimensions(pose)')


def fix_set_size(text, living=False):
    if not re.search(r'\bthis\.setSize\(', text) or 'void setSize(' in text:
        return text
    i = text.rstrip().rfind('}')
    return text[:i].rstrip('\n') + '\n' + (SET_SIZE_LIVING if living else SET_SIZE) + '}\n'


def fix_move_sig(text):
    m = re.search(r'public void move\(MoverType (\w+), double x, double y, double z\) \{', text)
    if m:
        text = text.replace(m.group(0), '@Override\n    public void move(MoverType %s, Vec3 movement) {' % m.group(1), 1)
        text = text.replace('super.move(%s, x, y, z)' % m.group(1), 'super.move(%s, movement)' % m.group(1))
    return text


def fix_render_fields(text):
    """Entities that copy the render state of a living entity into their own prevRenderYawOffset/... fields."""
    if 'public float prevRenderYawOffset;' not in text:
        return text
    for a, b in (('yBodyRotO', 'prevRenderYawOffset'), ('yBodyRot', 'renderYawOffset'), ('yHeadRotO', 'prevRotationYawHead'), ('yHeadRot', 'rotationYawHead')):
        text = re.sub(r'\bthis\.%s = ' % a, 'this.%s = ' % b, text)
    text = re.sub(r'= ([\w.]+)\.prevLimbSwingAmount;', r'= \1.walkAnimation.speed(0.0f);', text)
    text = re.sub(r'= ([\w.]+)\.limbSwingAmount;', r'= \1.walkAnimation.speed();', text)
    text = re.sub(r'= ([\w.]+)\.limbSwing;', r'= \1.walkAnimation.position();', text)
    return text


def fix_object_lists(text):
    """List<? extends Object> x = level.getEntitiesOfClass(Foo.class, ...) / for (Object m : x) -> typed"""
    out = text
    for m in list(re.finditer(r'List<\? extends Object> (\w+) = [^;\n]*?getEntitiesOfClass\((\w+)\.class,', text)):
        name, cls = m.group(1), m.group(2)
        out = out.replace(m.group(0), m.group(0).replace('List<? extends Object>', 'List<? extends %s>' % cls), 1)
        out = re.sub(r'for \(Object (\w+) : %s\)' % name, r'for (%s \1 : %s)' % (cls, name), out)
    return out


def fix_text(text, living=False):
    for pat, rep in RULES:
        text = re.sub(pat, rep, text)
    text = fix_mob_interact(text)
    text = fix_fall(text)
    text = fix_move_sig(text)
    text = fix_calls(text)
    text = fix_block_particles(text)
    text = fix_set_size(text, living)
    text = fix_object_lists(text)
    text = fix_render_fields(text)
    return text


def main():
    files = []
    for a in sys.argv[1:]:
        if os.path.isdir(a):
            for dp, _, fs in os.walk(a):
                files += [os.path.join(dp, f) for f in fs if f.endswith('.java')]
        else:
            files.append(a)
    n = 0
    for f in files:
        s = open(f, encoding='utf8').read()
        t = fix_text(s, '/monster/' in f.replace(chr(92), '/') or f.endswith('EntityParasiteBase.java'))
        if t != s:
            open(f, 'w', encoding='utf8').write(t)
            n += 1
    print('fixed', n, 'files')


if __name__ == '__main__':
    main()
