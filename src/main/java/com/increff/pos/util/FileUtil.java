package com.increff.pos.util;

import java.io.File;

public class FileUtil {
    public static File createDirectory(String dirName) {
        String rootPath = System.getProperty("user.dir");
        File dir = new File(rootPath + File.separator + dirName);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }
}
