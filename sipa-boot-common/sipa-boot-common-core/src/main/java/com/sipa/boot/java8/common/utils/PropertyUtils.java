package com.sipa.boot.java8.common.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

/**
 * @author zhouxiajie
 * @date 2019-01-16
 */
public class PropertyUtils {
    private static volatile Properties props;

    public static synchronized Properties getPropertiesFrom(String path) throws IOException {
        if (props == null) {
            props = new Properties();

            InputStream in = PropertyUtils.class.getClassLoader().getResourceAsStream(path);

            if (in != null) {
                props.load(in);
            }
        }

        return props;
    }

    private static void forceMkdir(final File directory) throws IOException {
        if (directory.exists()) {
            if (!directory.isDirectory()) {
                final String message =
                    "File " + directory + " exists and is " + "not a directory. Unable to create directory.";
                throw new IOException(message);
            }
        } else {
            if (!directory.mkdirs()) {
                // Double-check that some other thread or process hasn't made
                // the directory in the background
                if (!directory.isDirectory()) {
                    final String message = "Unable to create directory " + directory;
                    throw new IOException(message);
                }
            }
        }
    }

    private static void forceMkdirParent(final File file) throws IOException {
        final File parent = file.getParentFile();
        if (parent == null) {
            return;
        }
        forceMkdir(parent);
    }

    public static synchronized String getJarResourcePath(String from, String to) throws IOException {
        InputStream in = PropertyUtils.class.getClassLoader().getResourceAsStream(from);

        if (in != null) {
            forceMkdirParent(new File(to));

            IOUtils.copy(in, new FileOutputStream(new File(to)));
        }

        return to;
    }
}
