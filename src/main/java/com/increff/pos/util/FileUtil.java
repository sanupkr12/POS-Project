package com.increff.pos.util;

import java.io.File;

public class FileUtil {
    public static File createDirectory(String dirName) {
        String rootPath = "/home/sanupkumar/Downloads";
        File dir = new File(rootPath + File.separator + dirName);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }
}
