package com.dhanantry.scapeandrunparasites.command;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;

/** {@code /srphelp [topic]}: usage of the commands of the mod (lang keys {@code srphelp.*}). */
public class HelpCommand extends ArgCommand {
    private static final Map<String, List<Entry>> TOPICS = new LinkedHashMap<>();

    private record Entry(String key, String usageKey) {}

    public HelpCommand() {
        super("srphelp");
    }

    @Override
    protected List<String> words() {
        return List.copyOf(TOPICS.keySet());
    }

    @Override
    protected void execute(CommandSourceStack src, ServerLevel world, String[] args) {
        if (args.length == 0) {
            tr(src, "srphelp.header.warning");
            tr(src, "srphelp.header.topics");
            for (String t : TOPICS.keySet()) {
                tr(src, "srphelp.topic.item", t);
            }
            tr(src, "srphelp.footer.hint");
            return;
        }
        String topic = args[0].toLowerCase(Locale.ROOT);
        if (!TOPICS.containsKey(topic)) {
            tr(src, "srphelp.error.unknown", topic);
            tr(src, "srphelp.header.topics_inline", String.join(", ", TOPICS.keySet()));
            return;
        }
        tr(src, "srphelp.header.warning");
        tr(src, "srphelp.topic.header", topic);
        for (Entry e : TOPICS.get(topic)) {
            tr(src, e.usageKey());
            tr(src, e.key());
        }
    }

    static {
        TOPICS.put("srparasites", List.of(new Entry("srphelp.srparasites.setgeneration.desc", "srphelp.srparasites.setgeneration.usage"), new Entry("srphelp.srparasites.getgeneration.desc", "srphelp.srparasites.getgeneration.usage"), new Entry("srphelp.srparasites.readconfigurationfile.desc", "srphelp.srparasites.readconfigurationfile.usage"), new Entry("srphelp.srparasites.toggle_dotiledrops.desc", "srphelp.srparasites.toggle_dotiledrops.usage"), new Entry("srphelp.srparasites.toggle_domobevolution.desc", "srphelp.srparasites.toggle_domobevolution.usage"), new Entry("srphelp.srparasites.resetdatafile.desc", "srphelp.srparasites.resetdatafile.usage"), new Entry("srphelp.srparasites.parasites.desc", "srphelp.srparasites.parasites.usage")));
        TOPICS.put("srpgeneration", List.of(new Entry("srphelp.srpgeneration.setgeneration.desc", "srphelp.srpgeneration.setgeneration.usage"), new Entry("srphelp.srpgeneration.getgeneration.desc", "srphelp.srpgeneration.getgeneration.usage")));
        TOPICS.put("srpevolution", List.of(new Entry("srphelp.srpevolution.getphase.desc", "srphelp.srpevolution.getphase.usage"), new Entry("srphelp.srpevolution.addpoints.desc", "srphelp.srpevolution.addpoints.usage"), new Entry("srphelp.srpevolution.setcooldown.desc", "srphelp.srpevolution.setcooldown.usage"), new Entry("srphelp.srpevolution.addcooldown.desc", "srphelp.srpevolution.addcooldown.usage"), new Entry("srphelp.srpevolution.setphase.desc", "srphelp.srpevolution.setphase.usage"), new Entry("srphelp.srpevolution.set_evolutiongaining.desc", "srphelp.srpevolution.set_evolutiongaining.usage"), new Entry("srphelp.srpevolution.set_evolutionloss.desc", "srphelp.srpevolution.set_evolutionloss.usage"), new Entry("srphelp.srpevolution.evolutionlock_getlist.desc", "srphelp.srpevolution.evolutionlock_getlist.usage"), new Entry("srphelp.srpevolution.evolutionlock_reset.desc", "srphelp.srpevolution.evolutionlock_reset.usage"), new Entry("srphelp.srpevolution.evolutionlock_unlockall.desc", "srphelp.srpevolution.evolutionlock_unlockall.usage")));
        TOPICS.put("srpudevelopment", List.of(new Entry("srphelp.srpudevelopment.getlevel.desc", "srphelp.srpudevelopment.getlevel.usage"), new Entry("srphelp.srpudevelopment.setlevel.desc", "srphelp.srpudevelopment.setlevel.usage"), new Entry("srphelp.srpudevelopment.viewalldims.desc", "srphelp.srpudevelopment.viewalldims.usage")));
        TOPICS.put("srpnodes", List.of(new Entry("srphelp.srpnodes.viewall.desc", "srphelp.srpnodes.viewall.usage"), new Entry("srphelp.srpnodes.setnode.desc", "srphelp.srpnodes.setnode.usage"), new Entry("srphelp.srpnodes.removenode.desc", "srphelp.srpnodes.removenode.usage"), new Entry("srphelp.srpnodes.clearworld.desc", "srphelp.srpnodes.clearworld.usage")));
        TOPICS.put("srpcolonies", List.of(new Entry("srphelp.srpcolonies.viewall.desc", "srphelp.srpcolonies.viewall.usage"), new Entry("srphelp.srpcolonies.setcolony.desc", "srphelp.srpcolonies.setcolony.usage"), new Entry("srphelp.srpcolonies.removecolony.desc", "srphelp.srpcolonies.removecolony.usage"), new Entry("srphelp.srpcolonies.clearworld.desc", "srphelp.srpcolonies.clearworld.usage"), new Entry("srphelp.srpcolonies.resetglobaladaptation.desc", "srphelp.srpcolonies.resetglobaladaptation.usage"), new Entry("srphelp.srpcolonies.viewallglobaladaptation.desc", "srphelp.srpcolonies.viewallglobaladaptation.usage")));
        TOPICS.put("srpvectors", List.of(new Entry("srphelp.srpvectors.viewall.desc", "srphelp.srpvectors.viewall.usage"), new Entry("srphelp.srpvectors.setvector.desc", "srphelp.srpvectors.setvector.usage"), new Entry("srphelp.srpvectors.removevector.desc", "srphelp.srpvectors.removevector.usage"), new Entry("srphelp.srpvectors.clearworld.desc", "srphelp.srpvectors.clearworld.usage")));
        TOPICS.put("srpdislodgment", List.of(new Entry("srphelp.srpdislodgment.random_code.desc", "srphelp.srpdislodgment.random_code.usage"), new Entry("srphelp.srpdislodgment.set_code.desc", "srphelp.srpdislodgment.set_code.usage"), new Entry("srphelp.srpdislodgment.codes_reset.desc", "srphelp.srpdislodgment.codes_reset.usage")));
        TOPICS.put("srpguide", List.of(new Entry("srphelp.srpguide.unlockall.desc", "srphelp.srpguide.unlockall.usage"), new Entry("srphelp.srpguide.restore.desc", "srphelp.srpguide.restore.usage"), new Entry("srphelp.srpguide.clearall.desc", "srphelp.srpguide.clearall.usage")));
        TOPICS.put("srpguideclear", List.of(new Entry("srphelp.srpguideclear.self.desc", "srphelp.srpguideclear.self.usage")));
        TOPICS.put("harlequin_here", List.of(new Entry("srphelp.harlequin_here.convert.desc", "srphelp.harlequin_here.convert.usage")));
        TOPICS.put("harlequin_convert", List.of(new Entry("srphelp.harlequin_convert.blotch.desc", "srphelp.harlequin_convert.blotch.usage")));
        TOPICS.put("harlequin_scatter", List.of(new Entry("srphelp.harlequin_scatter.ruins.desc", "srphelp.harlequin_scatter.ruins.usage")));
        TOPICS.put("conjure", List.of(new Entry("srphelp.conjure.desc", "srphelp.conjure.usage")));
        TOPICS.put("srpguidistortion", List.of(new Entry("srphelp.srpguidistortion.desc", "srphelp.srpguidistortion.usage")));
        TOPICS.put("srpbestiarystats", List.of(new Entry("srphelp.srpbestiarystats.clear.desc", "srphelp.srpbestiarystats.clear.usage")));
        TOPICS.put("srp_breathe", List.of(new Entry("srphelp.srp_breathe.desc", "srphelp.srp_breathe.usage")));
        TOPICS.put("srp_celestial", List.of(new Entry("srphelp.srp_celestial.desc", "srphelp.srp_celestial.usage")));
    }
}
