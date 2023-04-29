package util;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SteamConnector {

    private static final Map<String,SteamProfile> profileCache = new HashMap<>();

    //<DiscordID,SteamID>
    private static final Map<String,String> linkedAccounts = new HashMap<>();

    public static List<LeaderboardEntry> getTopPlayers(int from, int to) throws Exception {
        String url = "https://steamcommunity.com/stats/326460/leaderboards/743177/?xml=1&start=%START%&end=%END%";
        url = url.replace("%START%", String.valueOf(from)).replace("%END%", String.valueOf(to));
        Document doc;
        try {
            doc = parseUrl(url);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
            throw new Exception("Cannot parse leaderboard. Please check the variables!");
        }
        return parseDocument(doc);
    }

    public static List<LeaderboardEntry> getFriendListOfPlayer(String steamID) throws Exception {
        String url = "https://steamcommunity.com/stats/326460/leaderboards/743177/?xml=1&steamid=" + steamID;
        Document doc;
        try {
            doc = parseUrl(url);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Cannot parse leaderboard. Please check the variables!");
        }
        return parseDocument(doc);
    }


    public static boolean isValidSteamID(String steamID) {
        if (steamID.length()!=17) {
            return false;
        }
        for (char c:steamID.toCharArray()) {
            if (!Character.isDigit(c)) {

                return false;
            }
        }
        return true;
    }






    private static Document parseUrl(String url) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(url);
        doc.getDocumentElement().normalize();
        return doc;
    }

    private static List<LeaderboardEntry> parseDocument(Document doc) throws Exception {
        List<LeaderboardEntry> out = new ArrayList<>();

        NodeList entries = doc.getElementsByTagName("entry");

        for (int i=0;i<entries.getLength();i++) {
            Node entry = entries.item(i);
            NodeList elements = entry.getChildNodes();
            String steamid = "";
            int xp = -1;
            int rank = -1;
            for (int j=0;j<elements.getLength();j++) {
                Node element =elements.item(j);
                switch (element.getNodeName()) {
                    case "steamid" -> steamid = element.getTextContent();
                    case "score" -> xp = Integer.parseInt(element.getTextContent());
                    case "rank" -> rank = Integer.parseInt(element.getTextContent());
                }
            }
            if (steamid.equalsIgnoreCase("") || xp==-1 ||rank == -1) {
                throw new Exception("Cannot parse following Node: "+ entry);
            }
            LeaderboardEntry le = new LeaderboardEntry(steamid,rank,xp);
            out.add(le);

        }
        return out;
    }

    private static SteamProfile getProfileUC(String steamid) throws Exception{
        String url = "https://steamcommunity.com/profiles/%STEAMID%/?xml=1".replace("%STEAMID%",steamid);
        Document doc;
        doc = parseUrl(url);
        NodeList lstName = doc.getElementsByTagName("steamID");
        if (lstName.getLength() !=1) {
            System.err.println("Error: len(NameList) is "+lstName.getLength());
            System.out.println(lstName);
            throw new Exception("Profile is probably private!");
        }
        String username = lstName.item(0).getTextContent();
        NodeList lstAvatar = doc.getElementsByTagName("avatarIcon");
        if (lstAvatar.getLength() <1) {
            System.err.println("Error: len(AvatarList) is "+lstAvatar.getLength());
            System.out.println(lstName);
            throw new Exception("Profile is probably private!");
        }
        String avatar = lstAvatar.item(0).getTextContent();
        NodeList lstMPG = doc.getElementsByTagName("mostPlayedGame");
        int hoursSSL = -1;
        for (int i=0;i<lstMPG.getLength();i++) {
            Node mpg = lstMPG.item(i);
            NodeList childs = mpg.getChildNodes();
            boolean isSSL = false;
            for (int j=0;j<childs.getLength();j++) {
                Node child = childs.item(j);
                if (child.getTextContent().equalsIgnoreCase("ShellShock Live")) isSSL = true;
            }
            if (isSSL) {
                for (int j=0;j<childs.getLength();j++) {
                    Node child = childs.item(j);
                    if (child.getNodeName().equalsIgnoreCase("hoursOnRecord")) hoursSSL = Integer.parseInt(child.getTextContent().strip().replaceAll(",",""));
                }
            }
        }
        return new SteamProfile(username,avatar,hoursSSL);
    }
    public static String getDescription(String steamid) throws Exception {
        String url = "https://steamcommunity.com/profiles/%STEAMID%/?xml=1".replace("%STEAMID%",steamid);
        Document doc;
        try {
            doc = parseUrl(url);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
            throw new Exception("Cannot parse provided URL!");
        }
        NodeList lst = doc.getElementsByTagName("summary");
        if (lst.getLength() <1) {
            throw new Exception("Error: len(NodeList) is \"+lst.getLength()");
        }
        return lst.item(0).getTextContent();
    }


    public static String getName(String steamid, boolean forceRefresh) {
        checkExpired(steamid);
        if (profileCache.containsKey(steamid) && !forceRefresh) return profileCache.get(steamid).getUsername();
        SteamProfile sp;
        try {
            sp = getProfileUC(steamid);
        } catch (Exception e) {
            e.printStackTrace();
            return "[ERROR: See console]";
        }
        profileCache.put(steamid,sp);
        return sp.getUsername();
    }

    public static String getName(String steamid) {
        return getName(steamid,false);
    }

    public static int getHours(String steamid){
        return getHours(steamid,false);
    }

    public static int getHours(String steamid, boolean forceRefresh) {
        checkExpired(steamid);
        if (profileCache.containsKey(steamid) && !forceRefresh) return profileCache.get(steamid).getHoursSSL();
        SteamProfile sp;
        try {
            sp = getProfileUC(steamid);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        profileCache.put(steamid,sp);
        return sp.getHoursSSL();
    }

    public static String getAvatar(String steamid){
        return getAvatar(steamid,false);
    }

    public static String getAvatar(String steamid, boolean forceRefresh) {
        checkExpired(steamid);
        if (profileCache.containsKey(steamid) && !forceRefresh) return profileCache.get(steamid).getAvatarIcon();
        SteamProfile sp;
        try {
            sp = getProfileUC(steamid);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        profileCache.put(steamid,sp);
        return sp.getAvatarIcon();
    }

    private static void checkExpired(String steamID) {
        if (!profileCache.containsKey(steamID)) return;
        if (!profileCache.get(steamID).isExpired()) return;
        try {
            SteamProfile sp = getProfileUC(steamID);
            profileCache.put(steamID,sp);
        } catch (Exception e) {
            profileCache.remove(steamID);
        }

    }

    public static void addLinking(String discordID, String steamID) {
        linkedAccounts.put(discordID,steamID);
        saveLinks();
    }

    public static String getSteamID(String discordID){
        return linkedAccounts.getOrDefault(discordID,"");
    }
    public static String getDiscordID(String steamID) {
        for (String discordID:linkedAccounts.keySet()) {
            if (linkedAccounts.get(discordID).equalsIgnoreCase(steamID)) return discordID;
        }
        return "";
    }

    public static void unlink(String discordID) {
        linkedAccounts.remove(discordID);
        saveLinks();
    }


    private static void saveLinks() {
        List<String> out = new ArrayList<>();
        for(String discordID:linkedAccounts.keySet()) {
            String line = discordID+":"+linkedAccounts.get(discordID);
            out.add(line);
        }
        try {
            printOutTxtFile.write("linkedAccounts.txt",out);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void loadLinks() {
        try {
            List<String> in = readInTxtFile.read("linkedAccounts.txt");

            for (String line:in) {
                String[] split = line.split(":");
                linkedAccounts.put(split[0],split[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}
