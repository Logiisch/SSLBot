package webserver;

import java.util.HashMap;
import java.util.Map;

public class CodeHandler {

    //<code, userid>
    private static final Map<String,String> codes = new HashMap<>();


    public static String addCode(String userID) {

        if (codes.containsValue(userID)) {
            for (String key:codes.keySet()) {
                if (codes.get(key).equalsIgnoreCase(userID)) return key;
            }
        }

        String code = generateCode();

        while (codes.containsKey(code)) code = generateCode();

        codes.put(code,userID);
        return code;

    }

    private static String generateCode() {
        double rnd =Math.random();
        long code = (long) Math.floor(rnd*100000000);
        StringBuilder out = new StringBuilder(String.valueOf(code));
        while (out.length()<8) out.insert(0, "0");

        return out.toString();

    }

    public static String getAndRemove(String code) throws Exception{
        if (!codes.containsKey(code)) throw new Exception("Code not found!");
        String userid = codes.get(code);
        codes.remove(code);
        return userid;
    }

}
