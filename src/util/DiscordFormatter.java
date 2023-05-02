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
        EmbedBuilder eb = new EmbedBuilder().setTitle(name, "https://steamcommunity.com/profiles/" + le.getSteamID());
        eb.setColor(color);
        eb.addField("Current Rank", formatXp(le.getRank()), true);
        eb.addField("Current XP", formatXp(le.getXp()), true);
        String thumb = SteamConnector.getAvatar(le.getSteamID());
        eb.setThumbnail(thumb);
        return eb.build();
    }
}
