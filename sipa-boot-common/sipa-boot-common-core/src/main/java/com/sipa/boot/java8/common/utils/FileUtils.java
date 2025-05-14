package com.sipa.boot.java8.common.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;

import com.alibaba.excel.util.IoUtils;
import com.google.common.base.Throwables;
import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;

/**
 * @author zhouxiajie
 * @date 2021/4/15
 */
public class FileUtils {
    private static final Log LOGGER = LogFactory.get(FileUtils.class);

    public static String copyFileFromClassPathToFileSystem(String resourcePath, String fileSystemPath) {
        try {
            File file = new File(fileSystemPath);
            IoUtils.copy(new ClassPathResource(resourcePath).getInputStream(), new FileOutputStream(file));
            return file.getAbsolutePath();
        } catch (IOException e) {
            LOGGER.error(e);
            Throwables.throwIfUnchecked(e);
        }
        return StringUtils.EMPTY;
    }
}
