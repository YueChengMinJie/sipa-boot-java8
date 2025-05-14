package com.sipa.boot.java8.common.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.alibaba.fastjson.util.IOUtils;
import com.sipa.boot.java8.common.dtos.ZipFile;
import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;

/**
 * zip压缩 解压缩
 *
 * @author feizhihao
 * @date 2019-10-25 13:32
 */
public class ZipUtils {
    private static final Log LOGGER = LogFactory.get(ZipUtils.class);

    /**
     * 单个文件压缩
     *
     * @param fileName
     *            文件名
     * @param data
     *            数据
     * @return zip byte
     */
    public static byte[] compress(String fileName, byte[] data) {
        byte[] zipByte;
        ZipOutputStream zip = null;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            zip = new ZipOutputStream(bos);

            ZipEntry entry = new ZipEntry(fileName);
            entry.setSize(data.length);
            zip.putNextEntry(entry);
            zip.write(data);
            zip.closeEntry();

            zip.close();

            zipByte = bos.toByteArray();
        } catch (Exception ex) {
            IOUtils.close(zip);
            throw new RuntimeException(ex);
        }
        return zipByte;
    }

    public static byte[] compress(List<String> fileNames, List<byte[]> dataList) {
        byte[] zipByte = null;
        if (fileNames.size() == dataList.size()) {
            ZipOutputStream zip = null;
            try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                zip = new ZipOutputStream(bos);

                for (int i = 0; i < fileNames.size(); i++) {
                    String fileName = fileNames.get(i);
                    byte[] data = dataList.get(i);
                    ZipEntry entry = new ZipEntry(fileName);
                    // 返回条目数据的未压缩大小；如果未知，则返回 -1。
                    entry.setSize(data.length);
                    // 开始写入新的 ZIP 文件条目并将流定位到条目数据的开始处
                    zip.putNextEntry(entry);
                    // 将字节数组写入当前 ZIP 条目数据。
                    zip.write(data);
                    zip.closeEntry();
                }

                zip.close();

                zipByte = bos.toByteArray();
            } catch (Exception ex) {
                IOUtils.close(zip);
                throw new RuntimeException(ex);
            }
        }
        return zipByte;
    }

    /**
     * 多个个文件压缩
     *
     * @param zipFiles
     *            文件对象
     * @return zip byte
     */
    public static byte[] compress(List<ZipFile> zipFiles) {
        byte[] zipByte = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            try (ZipOutputStream zip = new ZipOutputStream(bos)) {
                for (ZipFile zipFile : zipFiles) {
                    ZipEntry entry = new ZipEntry(zipFile.getFileName());
                    // 返回条目数据的未压缩大小；如果未知，则返回 -1。
                    entry.setSize(zipFile.getData().length);
                    // 开始写入新的 ZIP 文件条目并将流定位到条目数据的开始处
                    zip.putNextEntry(entry);
                    // 将字节数组写入当前 ZIP 条目数据。
                    zip.write(zipFile.getData());
                    zip.closeEntry();
                }
            } catch (Exception ex) {
                LOGGER.error(ex);
            }
            zipByte = bos.toByteArray();
        } catch (Exception ex) {
            LOGGER.error(ex);
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                LOGGER.error(e);
            }
        }
        return zipByte;
    }

    /**
     * 单个文件压缩包解压
     *
     * @param data
     *            数据
     * @return zip byte
     */
    public static List<byte[]> uncompress(byte[] data) {
        List<byte[]> fileBytes = new ArrayList<>();
        ZipInputStream zip = null;
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data)) {
            zip = new ZipInputStream(bis);

            while (zip.getNextEntry() != null) {
                byte[] buf = new byte[1024];
                int num;
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    while ((num = zip.read(buf, 0, buf.length)) != -1) {
                        baos.write(buf, 0, num);
                    }
                    fileBytes.add(baos.toByteArray());
                }
            }

            zip.close();
        } catch (Exception ex) {
            IOUtils.close(zip);
            throw new RuntimeException(ex);
        }
        return fileBytes;
    }

    public static void unZipFile(String zipFilePath, String unZipDirectory) throws IOException {
        org.apache.tools.zip.ZipFile zipFile = new org.apache.tools.zip.ZipFile(zipFilePath);
        Enumeration<?> entries = zipFile.getEntries();

        while (entries.hasMoreElements()) {
            org.apache.tools.zip.ZipEntry zipEntry = (org.apache.tools.zip.ZipEntry)entries.nextElement();
            File f = new File(unZipDirectory + org.apache.commons.io.IOUtils.DIR_SEPARATOR + zipEntry.getName());
            if (zipEntry.isDirectory()) {
                if (!f.exists() && !f.mkdirs())
                    throw new IOException("Couldn't create directory: " + f);
            } else {
                try (BufferedInputStream is = new BufferedInputStream(zipFile.getInputStream(zipEntry));
                    BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(f));) {
                    File destDir = f.getParentFile();
                    if (!destDir.exists() && !destDir.mkdirs()) {
                        throw new IOException("Couldn't create dir " + destDir);
                    }
                    int b = -1;
                    while ((b = is.read()) != -1) {
                        os.write(b);
                    }
                }
            }
        }
        zipFile.close();
    }
}
