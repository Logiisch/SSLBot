package util;

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


    public static String formatXp(int xp) {
        return String.valueOf(xp);
    }

}
