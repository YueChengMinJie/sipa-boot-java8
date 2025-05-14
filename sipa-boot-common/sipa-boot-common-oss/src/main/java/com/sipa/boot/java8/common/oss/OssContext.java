package com.sipa.boot.java8.common.oss;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.sipa.boot.java8.common.oss.property.OssProperties;
import com.sipa.boot.java8.common.oss.strategy.AliyunOssStrategy;
import com.sipa.boot.java8.common.oss.strategy.IOssStrategy;

/**
 * @author feizhihao
 * @date 2019-08-20 11:34
 */
public class OssContext {
    public static final String SLASH = "/";

    public static final String FOLDER_FORMAT = "yyyy-MM-dd";

    public static final String URL_MARK = "iov-saas.oss-cn-hangzhou.aliyuncs.com";

    public static final String URL_REDIRECT_MARK = "iov-saas.oss.ivehcore.com";

    private static OssProperties ossProperties;

    private static IOssStrategy ossStrategy;

    private OssContext() {
    }

    public static OssProperties getOssProperties() {
        return ossProperties;
    }

    public static IOssStrategy getOssStrategy() {
        return ossStrategy;
    }

    public static void init(OssProperties ossProperties, IOssStrategy ossStrategy) {
        OssContext.ossProperties = ossProperties;
        OssContext.ossStrategy = ossStrategy;
    }

    public static URL generatePresignedUrl(String bucketName, String key, String fileName, boolean download) {
        return ossStrategy.generatePresignedUrl(bucketName, key, fileName, download);
    }

    public static boolean existFile(String bucketName, String sourceKey) {
        return ossStrategy.existFile(bucketName, sourceKey);
    }

    public static URL generatePresignedUrl(String bucketName, String key, String fileName) {
        return ossStrategy.generatePresignedUrl(bucketName, key, fileName);
    }

    public static String generatePreSignUrl(String bucket, String sourceKey, String fileName, boolean https) {
        return ossStrategy.generatePreSignUrl(bucket, sourceKey, fileName, https);
    }

    public static void uploadObjectInput(File file, String bucketName, String folder, String fileName) {
        ossStrategy.uploadObjectInput(file, bucketName, folder, fileName);
    }

    public static String uploadObjectInput(byte[] fileBytes, String bucketName, String folder, String fileName) {
        return ossStrategy.uploadObjectInput(fileBytes, bucketName, folder, fileName);
    }

    public static InputStream getInputStream(String bucketName, String firstKey) throws Exception {
        return ossStrategy.getInputStream(bucketName, firstKey);
    }

    public static List<String> moveObject(String sourceBucketName, String sourceKey, String targetBucketName,
        String targetFolder) {
        return ossStrategy.moveObject(sourceBucketName, sourceKey, targetBucketName, targetFolder);
    }

    public static List<String> move(String sourceBucketName, String sourceKey, String targetBucketName,
        String targetKey) {
        return ossStrategy.move(sourceBucketName, sourceKey, targetBucketName, targetKey);
    }

    public static void renameAndMove(String sourceBucketName, String sourceKey, String targetBucketName,
        String newKey) {
        ossStrategy.renameAndMove(sourceBucketName, sourceKey, targetBucketName, newKey);
    }

    public static void deleteObject(String bucketName, String key) {
        ossStrategy.deleteObject(bucketName, key);
    }

    public static List<String> copyObject(String sourceBucketName, String sourceKey, String targetBucketName,
        String targetFolder) {
        return ossStrategy.copyObject(sourceBucketName, sourceKey, targetBucketName, targetFolder);
    }

    public static List<String> copy(String sourceBucketName, String sourceKey, String targetBucketName,
        String targetKey) {
        return ossStrategy.copy(sourceBucketName, sourceKey, targetBucketName, targetKey);
    }

    /**
     * 替换车网url.
     *
     * @param url
     *            车网url
     * @return 替换后的url
     */
    public static String changeURL(String url) {
        return url.replaceAll(URL_MARK, URL_REDIRECT_MARK);
    }

    /**
     * 随机文件名.
     */
    public static String randomFileName() {
        return String.valueOf(System.currentTimeMillis());
    }

    /**
     * 随机文件名.
     *
     * @param fileName
     *            文件名
     * @param suffix
     *            后缀
     * @return 随机文件名
     */
    public static String timeUnitFileName(String fileName, String suffix) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd_HH_mm_ss");
        return String.format("%s(%s)%s", fileName, df.format(LocalDateTime.now()), suffix);
    }

    /**
     * 生成服务访问KEY.
     *
     * @param path
     *            路径地址
     * @return key
     */
    public static String generateKey(String... path) {
        StringBuilder sb = new StringBuilder();
        List<String> paths = Arrays.asList(path);
        Iterator<String> it = paths.iterator();
        while (it.hasNext()) {
            String singlePath = it.next();
            if (StringUtils.isNotBlank(singlePath)) {
                sb.append(singlePath);
                if (it.hasNext()) {
                    sb.append(SLASH);
                }
            }
        }
        return sb.toString();
    }

    /**
     * 去除文件后缀名.
     *
     * @param fileName
     *            文件名
     * @return 文件的contentType
     */
    public static String getFileNamePre(String fileName) {
        // 文件名字
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    /**
     * 按照原始文件名 拼接文件名称.
     *
     * @param originName
     *            原始文件名称
     * @param fileName
     *            文件名
     * @return 文件的contentType
     */
    public static String combineNewFileName(String originName, String fileName) {
        String suffix = getFileSuffix(originName);
        // 文件名字
        return String.format("%s.%s", fileName, suffix);
    }

    /**
     * 获取文件后缀名.
     *
     * @param fileName
     *            文件名称
     * @return 后缀名
     */
    private static String getFileSuffix(String fileName) {
        // 文件的后缀名
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    /**
     * 获取文件名.
     *
     * @param key
     *            object key
     * @return 文件名
     */
    public static String getFileName(String key) {
        return key.substring(key.lastIndexOf(SLASH) + 1);
    }

    /**
     * 签名.
     *
     * @param bucket
     *            桶名
     * @param key
     *            文件key
     * @return url
     */
    public static String signatureUrl(String bucket, String key) {
        if (StringUtils.isBlank(bucket) || StringUtils.isBlank(key)) {
            return "";
        }
        URL url = OssContext.generatePresignedUrl(bucket, key, OssContext.getFileName(key));
        if (Objects.nonNull(url)) {
            if (ossStrategy instanceof AliyunOssStrategy) {
                return url.toString().replaceFirst("http://", "https://");
            } else {
                return url.toString();
            }
        }
        return "";
    }

    /**
     * 上传预签名.
     *
     * @param bucket
     *            桶名
     * @param key
     *            key名
     * @return 签名文件
     */
    public static String uploadSignature(String bucket, String key) {
        return ossStrategy.uploadSignature(bucket, key);
    }
}
