package me.elhoussam.util.sys;

import java.io.File;
import java.util.ArrayList;

public class StringHandler {
  public  static String fixEndingOf(String str) {
    if (str.endsWith(File.separator)) {
      return str;
    } else {
      return str.concat(File.separator);
    }
  }

  public static Boolean checkIfExist(String e, ArrayList<String> arr) {
    for (byte i = 0; i < arr.size(); i++) {
      if (arr.get(i).equals(e))
        return true;
    }
    return false;
  }

  public static String separatorsToSystem(String res) {
    if (res == null)
      return null;
    if (File.separatorChar == '\\') {
      // From Windows to Linux/Mac
      return res.replace('/', File.separatorChar);
    } else {
      // From Linux/Mac to Windows
      return res.replace('\\', File.separatorChar);
    }
  }
}
