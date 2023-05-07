package util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DiscordFormatter {
    public static String formatXp(int xp) {
        return String.format("%,d", xp);
    }

    //This function stays here for testing purposes.
    public static String formatLeaderboard(List<LeaderboardEntry> lb) {
        StringBuilder sb = new StringBuilder();
        sb.append("Rank, ID, XP").append("\n");
        for (LeaderboardEntry le : lb) {

            String playerName = SteamConnector.getName(le.getSteamID());

            sb
                    .append("**")
                    .append(le.getRank())
                    .append("** __")
                    .append(playerName)
                    .append("__ ")
                    .append(formatXp(le.getXp()))
                    .append("xp")
                    .append("\n");
        }
        return sb.toString();
    }

    public static List<MessageEmbed> formatLeaderboardEmbeds(List<LeaderboardEntry> lb) {
        return formatLeaderboardEmbeds(lb,false);
    }

    public static List<MessageEmbed> formatLeaderboardEmbeds(List<LeaderboardEntry> lb, String markSteamID, Color markWith) {
        List<MessageEmbed> out = new ArrayList<>();
        for (LeaderboardEntry le:lb) {
            if (le.getSteamID().equalsIgnoreCase(markSteamID)) out.add(formatLeaderboardEntry(le,markWith)); else out.add(formatLeaderboardEntry(le));
        }
        return out;
    }

    public static List<MessageEmbed> formatLeaderboardEmbeds(List<LeaderboardEntry> lb,boolean useColor) {
        List<MessageEmbed> out = new ArrayList<>();

        int pos = 1;

        for (LeaderboardEntry le : lb) {
            if (pos==1 && useColor) out.add(formatLeaderboardEntry(le,new Color(218,165,32)));
            if (pos==2 && useColor) out.add(formatLeaderboardEntry(le,new Color(192,192,192)));
            if (pos==3 && useColor) out.add(formatLeaderboardEntry(le,new Color(205,127,50)));
            if (pos>3 || !useColor) out.add(formatLeaderboardEntry(le));
            pos++;
        }
        return out;
    }

    public static MessageEmbed formatLeaderboardEntry(LeaderboardEntry le) {
        return formatLeaderboardEntry(le,Color.green);
    }

    public static MessageEmbed formatLeaderboardEntry(LeaderboardEntry le, Color color) {
        String name = SteamConnector.getName(le.getSteamID());
        EmbedBuilder eb = new EmbedBuilder().setTitle(name, getURL(le.getSteamID()));
        eb.setColor(color);
        eb.addField("Current Rank", formatXp(le.getRank()), true);
        eb.addField("Current XP", formatXp(le.getXp()), true);
        String thumb = SteamConnector.getAvatar(le.getSteamID());
        if (thumb.length()>0) eb.setThumbnail(thumb);
        return eb.build();
    }

    public static MessageEmbed error(String message) {
        return error(message,true);
    }
    private static MessageEmbed error(String message, boolean useFooter) {
        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("Error")
                .setColor(Color.RED)
                .setDescription(message);
        if (useFooter) eb.setFooter("If you think this should work, please create an issue on Github. Use \"/github\".");
        return eb.build();
    }

    public static String formatLeaderboardCodeBlock(List<LeaderboardEntry> list,String title) {
        StringBuilder out = new StringBuilder("```diff\n").append("- ").append(title).append("\n");
        for (LeaderboardEntry le:list) {
            out.append("+").append(fillSpaces(formatXp(le.getRank()),12,false)).append(SteamConnector.getName(le.getSteamID())).append("\n");
            out.append(fillSpaces(formatXp(le.getXp()),24,true)).append(" xp\n");
        }
        out.append("```");
        return out.toString();
    }

    private static String fillSpaces(String st, int minlen, boolean infront) {
        while (st.length()<minlen) st = (infront?" "+st:st+" ");
        return st;
    }

    public static String getURL(String steamid) {
        return "https://steamcommunity.com/profiles/"+steamid;
    }
}
