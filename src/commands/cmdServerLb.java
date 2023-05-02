package commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
import util.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class cmdServerLb extends ListenerAdapter {

    private static final Map<String,List<LeaderboardEntry>> sentMessages = new HashMap<>();

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        SlashCommandInteraction sce = event.getInteraction();
        if (!sce.getName().equalsIgnoreCase("serverlb")) return;

        Guild guild = sce.getGuild();
        if (guild==null) {
            event.replyEmbeds(DiscordFormatter.error("This Command only works on servers!")).setEphemeral(true).queue();
            return;
        }
        event.deferReply().queue();

        List<String> allIDs = new ArrayList<>();

        for (Member mem:sce.getGuild().getMembers()) {
            String discordID = mem.getUser().getId();
            String steamID = SteamConnector.getSteamID(discordID);
            if (steamID.length()>0) allIDs.add(steamID);
        }
        List<LeaderboardEntry> entries = new ArrayList<>();
        while (!allIDs.isEmpty()) {
            String steamID = allIDs.get(0);
            allIDs.remove(0);
            List<LeaderboardEntry> les;
            try {
                les = SteamConnector.getFriendListOfPlayer(steamID);
            } catch (Exception e) {
                e.printStackTrace();
                event.getHook().sendMessageEmbeds(DiscordFormatter.error("Error: Something went wrong while collecting Data. See console.")).setEphemeral(true).queue();
                return;
            }
            for (LeaderboardEntry le:les) {
                if (allIDs.contains(le.getSteamID())) {
                    allIDs.remove(le.getSteamID());
                    entries.add(le);
                }
                if (le.getSteamID().equalsIgnoreCase(steamID)) entries.add(le);
            }
        }
        entries = sortLeaderboard(entries);
        if (entries.size()<11) {
            String msgID = event.getHook().sendMessageEmbeds(DiscordFormatter.formatLeaderboardEmbeds(entries,true)).addActionRow(
                    Button.secondary("serverlb-reload",Emoji.fromUnicode("U+1F504")),
                    Button.secondary("serverlb-close",Emoji.fromUnicode("U+274C"))).complete().getId();
            InteractionManager.addMessage(msgID,event.getUser().getId());
            return;
        }
        List<LeaderboardEntry> sublist = entries.subList(0,10);
        String msgID = event.getHook().sendMessageEmbeds(DiscordFormatter.formatLeaderboardEmbeds(sublist,true)).addActionRow(
                Button.primary("serverlb-p0", Emoji.fromUnicode("U+2B05")).asDisabled(),
                Button.primary("serverlb-p2", Emoji.fromUnicode("U+27A1")),
                Button.secondary("serverlb-reload",Emoji.fromUnicode("U+1F504")),
                Button.secondary("serverlb-close",Emoji.fromUnicode("U+274C"))
        ).complete().getId();
        sentMessages.put(msgID,entries);
        InteractionManager.addMessage(msgID,event.getUser().getId());

    }

    private static List<LeaderboardEntry> sortLeaderboard(List<LeaderboardEntry> entries) {
        List<LeaderboardEntry> out = new ArrayList<>();
        out.add(entries.get(0));
        entries.remove(0);
        for (LeaderboardEntry le:entries) {
            int pos = 0;

            while (true) {
                if (pos==out.size()) {
                    out.add(le);
                    break;
                }
                LeaderboardEntry comp = out.get(pos);
                if (comp.getRank()>le.getRank()) {
                    out.add(pos,le);
                    break;
                }
                pos++;
            }
        }

        return out;
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        String btnName = event.getButton().getId();
        assert btnName!=null;
        if (!btnName.startsWith("serverlb")) return;
        Message msg = event.getMessage();

        if (InteractionManager.hasMessage(msg.getId())) {
            if (!InteractionManager.getMessageOwner(msg.getId()).equalsIgnoreCase(event.getUser().getId())) {
                event.replyEmbeds(DiscordFormatter.error("You dont have Permission to do that!")).setEphemeral(true).queue();
                return;
            }
        }

        if (btnName.equalsIgnoreCase("serverlb-close")) {
            sentMessages.remove(msg.getId());
            InteractionManager.removeMessage(msg.getId());
            msg.delete().queue();
            return;
        }
        event.deferEdit().queue();
        if (btnName.equalsIgnoreCase("serverlb-reload")) {
            Guild guild = event.getGuild();
            assert  guild!=null;

            List<String> allIDs = new ArrayList<>();

            for (Member mem:guild.getMembers()) {
                String discordID = mem.getUser().getId();
                String steamID = SteamConnector.getSteamID(discordID);
                if (steamID.length()>0) allIDs.add(steamID);
            }
            List<LeaderboardEntry> entries = new ArrayList<>();
            while (!allIDs.isEmpty()) {
                String steamID = allIDs.get(0);
                allIDs.remove(0);
                List<LeaderboardEntry> les;
                try {
                    les = SteamConnector.getFriendListOfPlayer(steamID);
                } catch (Exception e) {
                    e.printStackTrace();
                    event.getHook().sendMessageEmbeds(DiscordFormatter.error("Error: Something went wrong while collecting Data. See console.")).setEphemeral(true).queue();
                    return;
                }
                for (LeaderboardEntry le:les) {
                    if (allIDs.contains(le.getSteamID())) {
                        allIDs.remove(le.getSteamID());
                        entries.add(le);
                    }
                    if (le.getSteamID().equalsIgnoreCase(steamID)) entries.add(le);
                }
            }
            entries = sortLeaderboard(entries);
            if (entries.size()<11) {
                event.getMessage().editMessageEmbeds(DiscordFormatter.formatLeaderboardEmbeds(entries,true)).setActionRow(
                        Button.secondary("serverlb-reload",Emoji.fromUnicode("U+1F504")),
                        Button.secondary("serverlb-close",Emoji.fromUnicode("U+274C"))).queue();
                return;
            }
            List<LeaderboardEntry> sublist = entries.subList(0,10);
            event.getMessage().editMessageEmbeds(DiscordFormatter.formatLeaderboardEmbeds(sublist,true)).setActionRow(
                    Button.primary("serverlb-p0", Emoji.fromUnicode("U+2B05")).asDisabled(),
                    Button.primary("serverlb-p2", Emoji.fromUnicode("U+27A1")),
                    Button.secondary("serverlb-reload",Emoji.fromUnicode("U+1F504")),
                    Button.secondary("serverlb-close",Emoji.fromUnicode("U+274C"))
            ).queue();
            sentMessages.put(event.getMessage().getId(),entries);
            return;
        }
        if (btnName.startsWith("serverlb-p")) {
            int pagenum = Integer.parseInt(btnName.replace("serverlb-p",""));
            int lowerBound = 10*pagenum;
            int upperBound = lowerBound+10;
            List<LeaderboardEntry> les = sentMessages.get(event.getMessage().getId());
            List<LeaderboardEntry> sublist = les.subList(lowerBound,upperBound);
            Button left = Button.primary("serverlb-p"+(pagenum-1), Emoji.fromUnicode("U+2B05"));
            Button right = Button.primary("serverlb-p"+(pagenum+1), Emoji.fromUnicode("U+27A1"));
            event.getMessage().editMessageEmbeds(DiscordFormatter.formatLeaderboardEmbeds(sublist,pagenum==1)).setActionRow(
                    (pagenum==0?left.asDisabled():left.asEnabled()),
                    (pagenum*10>=les.size()?right.asDisabled():right.asEnabled()),
                    Button.secondary("serverlb-reload",Emoji.fromUnicode("U+1F504")),
                    Button.secondary("serverlb-close",Emoji.fromUnicode("U+274C"))
            ).queue();

        }
    }
}
