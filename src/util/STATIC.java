package util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class STATIC {

    public static String getToken() {
        String TOKEN;
        try {
            TOKEN = readInTxtFile.read("TOKEN.txt").get(0);
        } catch (Exception e) {
            System.err.println("Could not find file \"TOKEN.txt\". Please make sure that its located in this folder!");
            System.exit(0);
            return "";
        }
        return TOKEN;
    }

    public static final String TESTSERVER_ID = "1099994702264156242";
    public static final String INVITE_LINK = "https://discord.com/oauth2/authorize?client_id=1101778469815336960&scope=bot&permissions=380104723520";
    public static final String GITHUB_LINK = "https://github.com/Logiisch/SSLBot";

    public static final int CACHE_EXPIRE_AFTER = 60; //mins


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
                    .append(STATIC.formatXp(le.getXp()))
                    .append("xp")
                    .append("\n");
        }
        return sb.toString();
    }

    public static List<MessageEmbed> formatLeaderboardEmbeds(List<LeaderboardEntry> lb) {
        return formatLeaderboardEmbeds(lb,false);
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
