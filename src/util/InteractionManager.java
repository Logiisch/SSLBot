package util;

import java.util.HashMap;
import java.util.Map;

public class InteractionManager {
    //<MessageID, UserID>
    private static final Map<String,String> messageOwners = new HashMap<>();

    public static void addMessage(String messageID, String userID) {
        messageOwners.put(messageID,userID);
    }
    public static void  removeMessage(String messageID) {
        messageOwners.remove(messageID);
    }
    public static String getMessageOwner(String messageID) {
        return messageOwners.get(messageID);
    }

    public static boolean hasMessage(String messageID) {
        return messageOwners.containsKey(messageID);
    }
}
