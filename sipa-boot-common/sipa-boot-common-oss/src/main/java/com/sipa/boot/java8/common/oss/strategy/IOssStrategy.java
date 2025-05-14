package com.sipa.boot.java8.common.oss.strategy;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

/**
 * @author feizhihao
 * @date 2019-08-20 11:25
 */
public interface IOssStrategy {
    /**
     * generate download url (signed url Y/N ) with specified style
     *
     * @param bucketName
     *            桶名称
     * @param key
     *            文件服务路径
     * @param fileName
     *            文件名称
     * @return 文件下载URL
     */
    URL generatePresignedUrl(String bucketName, String key, String fileName);

    /**
     * generate download url (signed url Y/N ) with specified style
     *
     * @param bucket
     *            桶名称
     * @param sourceKey
     *            文件服务路径
     * @param fileName
     *            文件名称
     * @param https
     *            是否使用https
     * @return 文件下载URL
     */
    String generatePreSignUrl(String bucket, String sourceKey, String fileName, boolean https);

    /**
     * 上传文件
     *
     * @param file
     *            文件
     * @param bucketName
     *            存储空间
     * @param folder
     *            模拟文件夹名 如"qj_nanjing/"
     * @param fileName
     *            线上显示文件名
     */
    void uploadObjectInput(File file, String bucketName, String folder, String fileName);

    /**
     * 上传文件
     *
     * @param fileBytes
     *            bytes文件
     * @param bucketName
     *            存储空间
     * @param folder
     *            模拟文件夹名 如"qj_nanjing/"
     * @param fileName
     *            线上显示文件名
     * @return String 返回的唯一MD5数字签名
     */
    String uploadObjectInput(byte[] fileBytes, String bucketName, String folder, String fileName);

    /**
     * 获取文件流
     */
    InputStream getInputStream(String bucketName, String firstKey) throws Exception;

    /**
     * 拷贝文件到指定文件夹
     *
     * @param sourceBucketName
     *            源文件桶
     * @param sourceKey
     *            源key
     * @param targetBucketName
     *            新文件桶
     * @param targetFolder
     *            新文件文件夹
     * @return [new key, md5]
     */
    List<String> moveObject(String sourceBucketName, String sourceKey, String targetBucketName, String targetFolder);

    /**
     * 拷贝文件到指定文件夹
     *
     * @param sourceBucketName
     *            源文件桶
     * @param sourceKey
     *            源key
     * @param targetBucketName
     *            新文件桶
     * @param targetKey
     *            新key
     * @return [contentLength, md5]
     */
    List<String> move(String sourceBucketName, String sourceKey, String targetBucketName, String targetKey);

    /**
     * 拷贝文件用新的名字
     *
     * @param sourceBucketName
     *            源文件桶
     * @param sourceKey
     *            源key
     * @param targetBucketName
     *            新文件桶
     * @param newKey
     *            新的key
     */
    void renameAndMove(String sourceBucketName, String sourceKey, String targetBucketName, String newKey);

    /**
     * 删除文件
     *
     * @param bucketName
     *            源文件桶
     * @param key
     *            key
     */
    void deleteObject(String bucketName, String key);

    /**
     * 拷贝文件到指定文件夹 源文件不删除
     *
     * @param sourceBucketName
     *            源文件桶
     * @param sourceKey
     *            源key
     * @param targetBucketName
     *            新文件桶
     * @param targetFolder
     *            新文件文件夹
     * @return [new key, md5]
     */
    List<String> copyObject(String sourceBucketName, String sourceKey, String targetBucketName, String targetFolder);

    /**
     * 拷贝文件到指定文件夹 源文件不删除
     *
     * @param sourceBucketName
     *            源文件桶
     * @param sourceKey
     *            源key
     * @param targetBucketName
     *            新文件桶
     * @param targetKey
     *            新key
     * @return [contentLength, md5]
     */
    List<String> copy(String sourceBucketName, String sourceKey, String targetBucketName, String targetKey);

    /**
     * 签名.
     *
     * @param bucketName
     *            桶名
     * @param key
     *            对象key
     * @param fileName
     *            文件名
     * @param download
     *            是否下载
     * @return url
     */
    URL generatePresignedUrl(String bucketName, String key, String fileName, boolean download);

    /**
     * 判断文件是否存在
     *
     * @param bucketName
     *            文件桶
     * @param sourceKey
     *            文件key
     * @return 是否存在
     */
    boolean existFile(String bucketName, String sourceKey);

    /**
     * 上传预签名.
     *
     * @param bucket
     *            桶名
     * @param key
     *            key名
     * @return 签名url
     */
    String uploadSignature(String bucket, String key);
}
