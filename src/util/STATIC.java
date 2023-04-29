package util;

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
    public static final int MAX_REQUEST_SIZE = 30;
    public static final String GITHUB_LINK = "https://github.com/Logiisch/SSLBot";


    public static String formatXp(int xp) {
        return String.format("%,d",xp);
    }

    public static String formatLeaderboard(List<LeaderboardEntry> lb) {
        StringBuilder sb = new StringBuilder();
        sb.append("Rank, ID, XP").append("\n");
        for (LeaderboardEntry le:lb) {

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


}
