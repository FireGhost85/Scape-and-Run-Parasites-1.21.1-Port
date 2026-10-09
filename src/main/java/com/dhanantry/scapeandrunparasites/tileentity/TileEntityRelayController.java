package com.dhanantry.scapeandrunparasites.tileentity;

import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.init.SRPBlockEntities;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.item.ItemModule;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import com.dhanantry.scapeandrunparasites.command.EvolutionCommand;
import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanHaveBodies;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.item.ReportData;
import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

/**
 * Block entity of the relay controller: the formed flag, the 38 node blocks of the multiblock, the module slot and the scan
 * cooldown. The scanning itself (reports on vectors, phase, dislodgements and the module scans, the scan registry) is ported
 * with the Relay in M6 together with the module and report items; see PORTING_NOTES.
 */
public class TileEntityRelayController extends BlockEntity {
    public boolean formed = false;
    private long nextScanTick = 0L;
    private static final String NBT_NEXT_SCAN = "NextScanTick";
    private final List<BlockPos> childPositions = new ArrayList<>();
    private boolean dismantling = false;
    private static final int SCAN_RADIUS = 8;
    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return stack != null && !stack.isEmpty() && stack.getItem() instanceof ItemModule;
        }

        @Override
        protected void onContentsChanged(int slot) {
            TileEntityRelayController.this.setChanged();
        }
    };

    public TileEntityRelayController(BlockPos pos, BlockState state) {
        super(SRPBlockEntities.RELAY_CONTROLLER.get(), pos, state);
    }

    public void setChildPositions(List<BlockPos> positions) {
        this.childPositions.clear();
        if (positions != null) {
            this.childPositions.addAll(positions);
        }
        this.setChanged();
    }

    private int scannerCooldownTicks() {
        return SRPConfigSystems.getScannerCooldownTicks();
    }

    public boolean canScan() {
        return this.level == null || this.level.getGameTime() >= this.nextScanTick;
    }

    public int getCooldownRemainingTicks() {
        if (this.level == null) {
            return 0;
        }
        long t = this.nextScanTick - this.level.getGameTime();
        return t > 0L ? (int) t : 0;
    }

    public int getCooldownTotalTicks() {
        return this.scannerCooldownTicks();
    }

    public void startCooldown() {
        if (this.level == null) {
            return;
        }
        this.nextScanTick = this.level.getGameTime() + this.scannerCooldownTicks();
        this.setChanged();
        if (!this.level.isClientSide) {
            BlockState s = this.level.getBlockState(this.worldPosition);
            this.level.sendBlockUpdated(this.worldPosition, s, s, 3);
        }
    }

    public void addChild(BlockPos p) {
        if (p != null && !this.childPositions.contains(p)) {
            this.childPositions.add(p);
            this.setChanged();
        }
    }

    public ItemStackHandler getHandler() {
        return this.itemHandler;
    }

    public void dropContents() {
        if (this.level == null || this.level.isClientSide) {
            return;
        }
        ItemStack stack = this.itemHandler.getStackInSlot(0);
        if (!stack.isEmpty()) {
            Containers.dropItemStack(this.level, this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5, stack.copy());
            this.itemHandler.setStackInSlot(0, ItemStack.EMPTY);
            this.setChanged();
        }
    }

    public void setFormed(boolean val) {
        if (this.formed == val) {
            return;
        }
        this.formed = val;
        this.setChanged();
        if (this.level != null && !this.level.isClientSide) {
            BlockState s = this.level.getBlockState(this.worldPosition);
            this.level.sendBlockUpdated(this.worldPosition, s, s, 3);
        }
    }

    // ---------------------------------------------------------------- scanning (ported from 1.10.9)
    private boolean scanPending = false;
    private int scanTicks = 0;
    private UUID scanPlayer = null;
    private ItemModule.Kind scanKind = null;

    public boolean isFormed() {
        return this.formed;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TileEntityRelayController te) {
        te.tickScan();
    }

    private void tickScan() {
        if (this.level == null || this.level.isClientSide || !this.scanPending) {
            return;
        }
        if (--this.scanTicks > 0) {
            return;
        }
        this.scanPending = false;
        ServerPlayer player = this.level.getServer().getPlayerList().getPlayer(this.scanPlayer);
        if (player == null) {
            this.setChanged();
            return;
        }
        if (SRPConfigSystems.relayScannerDebugGlow) {
            this.debugGlowParasites();
        }
        if (this.scanKind == ItemModule.Kind.VECTORS) {
            this.giveVectorPaper(player);
        } else if (this.scanKind == ItemModule.Kind.PHASE) {
            this.givePhasePaper(player);
        } else if (this.scanKind == ItemModule.Kind.DISLODGEMENT) {
            this.giveDislodgementPaper(player);
        } else {
            this.giveScanPaper(player, this.scanKind);
        }
        this.level.playSound(null, this.worldPosition, SRPSounds.RELAY_PAPER.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
        this.setChanged();
    }

    private void debugGlowParasites() {
        for (Entity e : ((ServerLevel)this.level).getAllEntities()) {
            if (e instanceof LivingEntity le && "srparasites".equals(BuiltInRegistries.ENTITY_TYPE.getKey(le.getType()).getNamespace())) {
                le.addEffect(new MobEffectInstance(MobEffects.GLOWING, 200, 0, false, false));
            }
        }
    }

    private List<LivingEntity> allLiving() {
        List<LivingEntity> out = new ArrayList<>();
        for (Entity e : ((ServerLevel)this.level).getAllEntities()) {
            if (e instanceof LivingEntity le) {
                out.add(le);
            }
        }
        return out;
    }

    private static int gcd(int a, int b) {
        a = Math.abs(a);
        b = Math.abs(b);
        while (b != 0) {
            int t = a % b;
            a = b;
            b = t;
        }
        return a == 0 ? 1 : a;
    }

    private static ChatFormatting tierColor(String tierId) {
        return switch (tierId) {
            case "inborn" -> ChatFormatting.GREEN;
            case "assimilated" -> ChatFormatting.AQUA;
            case "assimara" -> ChatFormatting.DARK_AQUA;
            case "hijacked" -> ChatFormatting.DARK_RED;
            case "feral" -> ChatFormatting.RED;
            case "crude" -> ChatFormatting.DARK_GRAY;
            case "primitive" -> ChatFormatting.GRAY;
            case "adapted" -> ChatFormatting.GOLD;
            case "nexus" -> ChatFormatting.LIGHT_PURPLE;
            case "deterrent" -> ChatFormatting.DARK_PURPLE;
            case "pure" -> ChatFormatting.BLUE;
            case "preeminent" -> ChatFormatting.DARK_GREEN;
            case "ancient" -> ChatFormatting.DARK_PURPLE;
            case "derived" -> ChatFormatting.DARK_BLUE;
            default -> ChatFormatting.WHITE;
        };
    }

    private void giveScanPaper(ServerPlayer player, ItemModule.Kind kind) {
        ScanRegistry.ModuleProfile profile = ScanRegistry.getProfileFor(kind);
        if (profile == null) {
            return;
        }
        List<LivingEntity> all = this.allLiving();
        Map<ScanRegistry.Tier, Integer> tierCounts = new LinkedHashMap<>();
        int parasitesTotal = 0;
        for (ScanRegistry.Tier tier : profile.tiers) {
            int c = 0;
            for (LivingEntity e : all) {
                if (e instanceof Mob && tier.matches(e)) {
                    ++c;
                }
            }
            tierCounts.put(tier, c);
            parasitesTotal += c;
        }
        int mobTotal = 0;
        for (LivingEntity e : all) {
            if (e instanceof Mob) {
                ++mobTotal;
            }
        }
        int other = Math.max(0, mobTotal - parasitesTotal);
        String percent = mobTotal == 0 ? "0.0%" : String.format(Locale.ROOT, "%.1f%%", 100.0 * (double)parasitesTotal / (double)Math.max(1, mobTotal));
        String ratio;
        if (parasitesTotal == 0 && other == 0) {
            ratio = "0:0";
        } else {
            int g = gcd(parasitesTotal, other);
            ratio = parasitesTotal / g + ":" + other / g;
        }
        String dim = DimKeys.of(this.level);
        List<Component> lore = new ArrayList<>();
        lore.add(Component.translatable("item.srparasites.scan_report.header").withStyle(ChatFormatting.DARK_PURPLE));
        lore.add(Component.literal(" "));
        lore.add(Component.translatable("item.srparasites.scan_report.dimension", Component.literal(dim).withStyle(ChatFormatting.GOLD)).withStyle(ChatFormatting.GRAY));
        lore.add(Component.translatable("item.srparasites.scan_report.total_mobs", Component.literal(String.valueOf(mobTotal)).withStyle(ChatFormatting.WHITE)).withStyle(ChatFormatting.GRAY));
        lore.add(Component.translatable("item.srparasites.scan_report.total_parasites", Component.literal(String.valueOf(parasitesTotal)).withStyle(ChatFormatting.RED)).withStyle(ChatFormatting.GRAY));
        lore.add(Component.translatable("item.srparasites.scan_report.percent", Component.literal(percent).withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.GRAY));
        lore.add(Component.translatable("item.srparasites.scan_report.ratio", Component.literal(ratio).withStyle(ChatFormatting.AQUA)).withStyle(ChatFormatting.GRAY));
        lore.add(Component.literal(" "));
        lore.add(Component.translatable("item.srparasites.scan_report.tiers").withStyle(ChatFormatting.DARK_GRAY));
        for (Map.Entry<ScanRegistry.Tier, Integer> e : tierCounts.entrySet()) {
            ScanRegistry.Tier t = e.getKey();
            lore.add(Component.translatable(t.langKey()).withStyle(tierColor(t.getIdLower()))
                    .append(Component.literal(" : ").withStyle(ChatFormatting.GRAY))
                    .append(Component.literal(String.valueOf(e.getValue())).withStyle(ChatFormatting.WHITE)));
        }
        ItemStack paper = new ItemStack(Items.PAPER);
        paper.set(DataComponents.CUSTOM_NAME, Component.translatable("item.srparasites.scan_report").withStyle(style -> style.withItalic(false)));
        paper.set(DataComponents.LORE, new ItemLore(lore));
        if (!player.getInventory().add(paper)) {
            player.drop(paper, false);
        }
    }

    public boolean performScan(ServerPlayer player) {
        if (this.level == null || this.level.isClientSide) {
            return false;
        }
        if (!this.formed) {
            player.sendSystemMessage(Component.translatable("chat.srparasites.relay.not_formed"));
            return false;
        }
        ItemStack stack = this.itemHandler.getStackInSlot(0);
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemModule module)) {
            player.sendSystemMessage(Component.translatable("chat.srparasites.relay.insert_module"));
            return false;
        }
        ItemModule.Kind kind = module.getKind(stack);
        if (kind == ItemModule.Kind.VECTORS) {
            if (!SRPConfigWorld.originActivated) {
                player.sendSystemMessage(Component.translatable("chat.srparasites.vectors.not_activated"));
                return false;
            }
        } else if (kind != ItemModule.Kind.PHASE && kind != ItemModule.Kind.DISLODGEMENT) {
            ScanRegistry.ModuleProfile profile = ScanRegistry.getProfileFor(kind);
            if (profile == null || profile.tiers.isEmpty()) {
                player.sendSystemMessage(Component.translatable("chat.srparasites.relay.no_profile", kind.name()));
                return false;
            }
        }
        if (this.scanPending) {
            player.sendSystemMessage(Component.translatable("chat.srparasites.relay.scan_busy"));
            return false;
        }
        this.scanPending = true;
        this.scanTicks = 110;
        this.scanPlayer = player.getUUID();
        this.scanKind = kind;
        this.level.playSound(null, this.worldPosition, SRPSounds.RELAY_ACTIVATE.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
        player.sendSystemMessage(Component.translatable("chat.srparasites.relay.scan_started"));
        this.startCooldown();
        this.setChanged();
        return true;
    }

    private void giveVectorPaper(ServerPlayer player) {
        if (!SRPConfigWorld.originActivated) {
            player.sendSystemMessage(Component.translatable("chat.srparasites.vectors.disabled"));
            return;
        }
        SRPWorldData data = SRPWorldData.get(this.level);
        if (data == null) {
            return;
        }
        List<Integer> xs = data.getorigins("x");
        List<Integer> ys = data.getorigins("y");
        List<Integer> zs = data.getorigins("z");
        List<Integer> rs = data.getorigins("a");
        List<Integer> hs = data.getorigins("h");
        int n = Integer.MAX_VALUE;
        for (List<Integer> l : new List[]{xs, ys, zs, rs, hs}) {
            if (l != null) {
                n = Math.min(n, l.size());
            }
        }
        if (n == Integer.MAX_VALUE) {
            n = 0;
        }
        long now = this.level.getDayTime();
        int day = (int)(now / 24000L);
        int printTime = (int)(now % 24000L);
        int cx = player.blockPosition().getX();
        int cz = player.blockPosition().getZ();
        int total = n <= 0 ? 1 : n;
        int count = n <= 0 ? 1 : n;
        for (int i = 0; i < count; ++i) {
            ItemStack map = new ItemStack(SRPItems.VECTOR_MAP.get());
            final int idx = i;
            final boolean empty = n <= 0;
            final List<Integer> fxs = xs;
            final List<Integer> fzs = zs;
            final List<Integer> frs = rs;
            ReportData.update(map, t -> {
                t.putInt("PrintDay", day);
                t.putInt("PrintTime", printTime);
                t.putInt("CenterX", cx);
                t.putInt("CenterZ", cz);
                t.putInt("VectorX", empty ? cx : fxs.get(idx));
                t.putInt("VectorZ", empty ? cz : fzs.get(idx));
                t.putInt("Radius", empty ? 0 : frs.get(idx));
                t.putInt("Day", day);
                t.putInt("Index", idx + 1);
                t.putInt("Total", total);
            });
            if (!player.getInventory().add(map)) {
                player.drop(map, false);
            }
        }
    }

    private void givePhasePaper(ServerPlayer player) {
        int parasiteCount = 0;
        int cothCount = 0;
        int totalMobCount = 0;
        List<LivingEntity> all = this.allLiving();
        for (LivingEntity ent : all) {
            if (!(ent instanceof Player)) {
                ++totalMobCount;
            }
            if (ent instanceof EntityParasiteBase) {
                if (ent instanceof EntityCanHaveBodies bodies) {
                    if (bodies.getBodyNumber() != 0) {
                        continue;
                    }
                    ++parasiteCount;
                    continue;
                }
                ++parasiteCount;
            }
        }
        for (LivingEntity ent : all) {
            if (!(ent instanceof EntityParasiteBase) && ent.hasEffect(SRPPotions.COTH_E)) {
                ++cothCount;
            }
        }
        String dim = DimKeys.of(this.level);
        SRPSaveData data = SRPSaveData.get(this.level);
        int players = ((ServerLevel)this.level).players().size();
        players *= SRPConfig.worldMobCapPlusPlayer;
        byte phase = data.getEvolutionPhase(dim);
        int totalPoints = data.getTotalKills(dim);
        int nextPoints = EvolutionCommand.getNeededPoints((byte)(phase + 1));
        double progress = nextPoints <= 0 ? 0.0 : (double)totalPoints / (double)nextPoints * 100.0;
        String progStr = String.format(Locale.ROOT, "%.1f", progress);
        ItemStack report = new ItemStack(SRPItems.phase_report.get());
        long worldTime = this.level.getDayTime();
        final int p = players;
        final int pc = parasiteCount;
        final int cc = cothCount;
        final int tm = totalMobCount;
        ReportData.update(report, tag -> {
            tag.putInt("PrintDay", (int)(worldTime / 24000L));
            tag.putInt("PrintTime", (int)(worldTime % 24000L));
            tag.putString("PhaseDimension", dim);
            tag.putInt("PhaseValue", (int)phase);
            tag.putInt("PhaseTotalPoints", totalPoints);
            tag.putInt("PhasePointsNext", nextPoints);
            tag.putString("PhaseProgress", progStr);
            tag.putInt("PhaseCooldown", data.getCooldown(this.level, dim));
            tag.putBoolean("PhaseCanGain", data.getCanGain(dim));
            tag.putBoolean("PhaseCanLoss", data.getCanLoss(dim));
            tag.putInt("PhaseMobcap", SRPConfig.worldMobCap + p);
            tag.putInt("PhaseGeneration", (int)data.getGeneration(dim));
            tag.putInt("PhaseGenTicks", data.getGenerationNeededTime(this.level, dim));
            tag.putInt("PhaseParasiteCount", pc);
            tag.putInt("PhaseCothCount", cc);
            tag.putInt("PhaseTotalMobs", tm);
        });
        if (!player.getInventory().add(report)) {
            player.drop(report, false);
        }
    }

    private void giveDislodgementPaper(ServerPlayer player) {
        String dim = DimKeys.of(this.level);
        SRPSaveData data = SRPSaveData.get(this.level);
        String raw = data.getCurrentCodeU(dim);
        String code = "";
        if (raw != null && !raw.trim().isEmpty()) {
            String[] vals = raw.split(";");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < vals.length; ++i) {
                int v;
                try {
                    v = Integer.parseInt(vals[i].trim());
                } catch (Throwable t) {
                    v = 0;
                }
                if (v == 0) {
                    continue;
                }
                int timeSec = data.getCurrentCodeDuration(dim, i);
                if (sb.length() > 0) {
                    sb.append(";");
                }
                sb.append(dim).append(";").append(i).append(";").append(v).append(";").append(timeSec);
            }
            code = sb.toString();
        }
        ItemStack report = new ItemStack(SRPItems.DISLODGEMENT_REPORT.get());
        long worldTime = this.level.getDayTime();
        final String fcode = code;
        ReportData.update(report, t -> {
            t.putString("DislodgementCode", fcode);
            t.putInt("PrintDay", (int)(worldTime / 24000L));
            t.putInt("PrintTime", (int)(worldTime % 24000L));
        });
        if (!player.getInventory().add(report)) {
            player.drop(report, false);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);
        nbt.putBoolean("Formed", this.formed);
        nbt.putLong(NBT_NEXT_SCAN, this.nextScanTick);
        ListTag list = new ListTag();
        for (BlockPos p : this.childPositions) {
            if (p != null) {
                list.add(NbtUtils.writeBlockPos(p));
            }
        }
        nbt.put("Children", list);
        nbt.put("Inv", this.itemHandler.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);
        this.nextScanTick = nbt.getLong(NBT_NEXT_SCAN);
        this.formed = nbt.getBoolean("Formed");
        this.childPositions.clear();
        ListTag list = nbt.getList("Children", Tag.TAG_INT_ARRAY);
        for (int i = 0; i < list.size(); ++i) {
            int[] a = list.getIntArray(i);
            if (a.length == 3) {
                this.childPositions.add(new BlockPos(a[0], a[1], a[2]));
            }
        }
        if (nbt.contains("Inv", Tag.TAG_COMPOUND)) {
            this.itemHandler.deserializeNBT(registries, nbt.getCompound("Inv"));
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (this.level == null || this.level.isClientSide) {
            return;
        }
        this.relinkChildrenToMe();
        if (this.nextScanTick < 0L) {
            this.nextScanTick = 0L;
            this.setChanged();
        }
        BlockState s = this.level.getBlockState(this.worldPosition);
        this.level.sendBlockUpdated(this.worldPosition, s, s, 3);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    /** Removes the node blocks, drops the module and breaks the controller (re-entry safe). */
    public void dismantle() {
        if (this.level == null || this.level.isClientSide) {
            return;
        }
        if (this.dismantling) {
            return;
        }
        this.dismantling = true;
        try {
            Level level = this.level;
            List<BlockPos> targets = new ArrayList<>(this.childPositions);
            if (targets.isEmpty()) {
                targets = this.findChildrenByScan();
            }
            this.formed = false;
            this.dropContents();
            for (BlockPos p : targets) {
                if (p == null || !level.isLoaded(p) || !level.getBlockState(p).is(SRPBlocks.NODE_RELAY.get())) {
                    continue;
                }
                level.removeBlock(p, false);
            }
            this.childPositions.clear();
            this.setChanged();
            if (level.isLoaded(this.worldPosition) && level.getBlockState(this.worldPosition).is(SRPBlocks.RELAY_CONTROLLER.get())) {
                level.destroyBlock(this.worldPosition, true);
            }
        } finally {
            this.dismantling = false;
        }
    }

    private void relinkChildrenToMe() {
        if (this.level == null || this.level.isClientSide) {
            return;
        }
        for (BlockPos p : this.childPositions) {
            if (p != null && this.level.isLoaded(p) && this.level.getBlockEntity(p) instanceof TileEntityNodeRelay node) {
                node.setControllerPos(this.worldPosition);
            }
        }
    }

    private List<BlockPos> findChildrenByScan() {
        List<BlockPos> found = new ArrayList<>();
        if (this.level == null) {
            return found;
        }
        BlockPos origin = this.worldPosition;
        for (int dx = -SCAN_RADIUS; dx <= SCAN_RADIUS; ++dx) {
            for (int dy = -SCAN_RADIUS; dy <= SCAN_RADIUS; ++dy) {
                for (int dz = -SCAN_RADIUS; dz <= SCAN_RADIUS; ++dz) {
                    BlockPos p = origin.offset(dx, dy, dz);
                    if (!p.equals(origin) && this.level.isLoaded(p) && this.level.getBlockState(p).is(SRPBlocks.NODE_RELAY.get())) {
                        found.add(p);
                    }
                }
            }
        }
        return found;
    }
}
