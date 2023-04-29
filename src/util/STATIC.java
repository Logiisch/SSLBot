package util;

import java.io.FileNotFoundException;
import java.io.IOException;

public class STATIC {

    public static String getToken() {
        String TOKEN = "";
        try {
            TOKEN = readInTxtFile.read("TOKEN.txt").get(0);
        } catch (Exception e) {
            System.err.println("Could not find file \"TOKEN.txt\". Please make sure that its located in this folder!");
            System.exit(0);
            return "";
        }
        return TOKEN;
    }

}
