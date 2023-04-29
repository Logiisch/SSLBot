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
import java.util.List;

public class SteamConnector {

    public static List<LeaderboardEntry> getTopPlayers(int from, int to) throws Exception {
        String url = "https://steamcommunity.com/stats/326460/leaderboards/743177/?xml=1&start=%START%&end=%END%";
        url = url.replace("%START%", String.valueOf(from)).replace("%END%", String.valueOf(to));
        Document doc;
        try {
            doc = parseUrl(url);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new Exception("Cannot parse leaderboard. Please check the variables!");
        }
        return parseDocument(doc);


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


}
