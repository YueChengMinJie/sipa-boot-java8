package com.sipa.boot.java8.common.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author feizhihao
 * @date 2019-09-02 13:19
 */
public class ResourceFileUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceFileUtils.class);

    /**
     * 获取资源文件
     *
     * @param relativePath
     *            资源文件相对路径(相对于 resources路径,路径 + 文件名) eg: "templates/pdf_export_demo.ftl"
     * @return File
     * @throws FileNotFoundException
     */
    public static File getFile(String relativePath) throws FileNotFoundException {
        if (relativePath == null || relativePath.length() == 0) {
            return null;
        }
        if (relativePath.startsWith("/")) {
            relativePath = relativePath.substring(1);
        }

        InputStream inputStream = ResourceFileUtils.class.getClassLoader().getResourceAsStream(relativePath);

        File file = new File(relativePath);
        if (!file.exists()) {
            try {
                FileUtils.copyInputStreamToFile(inputStream, file);
            } catch (IOException e) {
                LOGGER.error("create file failed", e);
            }
        }
        return file;
    }

    /**
     * 获取资源绝对路径
     *
     * @param relativePath
     *            资源文件相对路径(相对于 resources路径,路径 + 文件名) eg: "templates/pdf_export_demo.ftl"
     * @return 资源绝对路径
     * @throws FileNotFoundException
     */
    public static String getAbsolutePath(String relativePath) throws FileNotFoundException {
        return getFile(relativePath).getAbsolutePath();
    }

    /**
     * 获取资源父级目录
     *
     * @param relativePath
     *            资源文件相对路径(相对于 resources路径,路径 + 文件名) eg: "templates/pdf_export_demo.ftl"
     * @return 资源父级目录
     * @throws FileNotFoundException
     */
    public static String getParent(String relativePath) throws FileNotFoundException {
        return getFile(relativePath).getParent();
    }

    /**
     * 获取资源文件名
     *
     * @param relativePath
     *            资源文件相对路径(相对于 resources路径,路径 + 文件名) eg: "templates/pdf_export_demo.ftl"
     * @return 资源文件名
     * @throws FileNotFoundException
     */
    public static String getFileName(String relativePath) throws FileNotFoundException {
        return getFile(relativePath).getName();
    }
}
