package util;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class readInTxtFile
{
  public readInTxtFile() {}
  
  public static ArrayList<String> read(String source) throws java.io.IOException {
    ArrayList<String> out = new ArrayList<>();
    java.io.FileReader fr = new java.io.FileReader(source);
    java.io.BufferedReader br = new java.io.BufferedReader(fr);
    
    String zeile = "";

    while ((zeile = br.readLine()) != null)
    {
      out.add(zeile);
    }
    
    br.close();
    fr.close();
    return out;
  }
}
