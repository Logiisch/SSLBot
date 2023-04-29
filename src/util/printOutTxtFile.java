package util;

import java.io.BufferedWriter;
import java.util.List;

public class printOutTxtFile
{
  public printOutTxtFile() {}
  
  public static void write(String dest, List<String> content) throws java.io.IOException {
    //File f = new File(dest);

    java.io.FileWriter fw = new java.io.FileWriter(dest);
    BufferedWriter bw = new BufferedWriter(fw);
    
    for (String s : content) {
      bw.write(s);
      bw.newLine();
    }
    bw.close();
    fw.close();
  }
}
