package com.sipa.boot.java8.common.oss.strategy;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.common.utils.IOUtils;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.ResponseHeaderOverrides;
import com.google.common.collect.Lists;
import com.sipa.boot.java8.common.oss.OssContext;
import com.sipa.boot.java8.common.oss.annotation.OssStrategy;
import com.sipa.boot.java8.common.oss.exception.MethodNotImplException;
import com.sipa.boot.java8.common.oss.property.OssProperties;

/**
 * @author feizhihao
 * @date 2019-08-20 11:31
 */
@OssStrategy
public class AliyunOssStrategy extends BaseStrategy implements IOssStrategy {
    private final OssProperties ossProperties;

    private OSS ossClient;

    private int version;

    public AliyunOssStrategy(OssProperties ossProperties) {
        this.ossProperties = ossProperties;
    }

    private OSS getOssClient() {
        if (ossClient == null || version != ossProperties.getVersion()) {
            ossClient = new OSSClientBuilder().build(ossProperties.getEndpoint(), ossProperties.getAccessKey(),
                ossProperties.getSecretKey());
            version = ossProperties.getVersion();
        }
        return ossClient;
    }

    @Override
    public URL generatePresignedUrl(String bucketName, String key, String fileName) {
        return doGeneratePreSignUrl(bucketName, key, fileName, true);
    }

    @Override
    public String generatePreSignUrl(String bucket, String sourceKey, String fileName, boolean https) {
        try {
            Date expiration = new Date(System.currentTimeMillis() + OSS_FILE_EXPIRE_TIME);

            GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(bucket, sourceKey, HttpMethod.GET);
            req.setExpiration(expiration);
            ResponseHeaderOverrides resp = new ResponseHeaderOverrides();
            resp.setContentDisposition("attachment; filename=" + fileName);
            req.setResponseHeaders(resp);

            URL url = getOssClient().generatePresignedUrl(req);
            String sUrl = url.toString();
            return https ? sUrl.replaceAll("http", "https") : sUrl;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private URL doGeneratePreSignUrl(String bucketName, String key, String fileName, boolean download) {
        Date expiration = new Date(System.currentTimeMillis() + OSS_FILE_EXPIRE_TIME);
        GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(bucketName, key, HttpMethod.GET);
        req.setExpiration(expiration);
        ResponseHeaderOverrides resp = new ResponseHeaderOverrides();
        if (download) {
            resp.setContentDisposition("attachment; filename=" + fileName);
        }
        req.setResponseHeaders(resp);
        return getOssClient().generatePresignedUrl(req);
    }

    @Override
    public void uploadObjectInput(File file, String bucketName, String folder, String fileName) {
        uploadObjectInput(getOssClient(), file, bucketName, folder, fileName);
    }

    private static void uploadObjectInput(OSS ossClient, File file, String bucketName, String folder, String fileName) {
        try {
            long fileSize = file.length();

            // 创建上传Object的Metadata
            ObjectMetadata metadata = new ObjectMetadata();
            // 设置md5自定义值 不支持自定义MD5加密
            // metadata.addUserMetadata(SELF_METADATA_MD5, md5);
            // 上传的文件的长度
            metadata.setContentLength(fileSize);
            // 指定该Object被下载时的网页的缓存行为
            metadata.setCacheControl("no-cache");
            // 指定该Object下设置Header
            metadata.setHeader("Pragma", "no-cache");
            // 指定该Object被下载时的内容编码格式
            metadata.setContentEncoding("utf-8");
            // 文件的MIME，定义文件的类型及网页编码，决定浏览器将以什么形式、什么编码读取文件。如果用户没有指定则根据Key或文件名的扩展名生成，
            // 如果没有扩展名则填默认值application/octet-stream
            metadata.setContentType(getContentType(fileName));
            // 指定该Object被下载时的名称（指示MINME用户代理如何显示附加的文件，打开或下载，及文件名称）
            metadata.setContentDisposition("filename/filesize=" + fileName + OssContext.SLASH + fileSize + "Byte.");

            String key = OssContext.generateKey(folder, fileName);
            ossClient.putObject(bucketName, key, file, metadata);
        } catch (Exception e) {
            LOGGER.error("AliyunOssStrategy.uploadObjectInput fail.", e);
        }
    }

    @Override
    public String uploadObjectInput(byte[] fileBytes, String bucketName, String folder, String fileName) {
        return uploadObjectInput(getOssClient(), new ByteArrayInputStream(fileBytes), bucketName, folder, fileName,
            fileBytes.length);
    }

    private static String uploadObjectInput(OSS ossClient, InputStream is, String bucketName, String folder,
        String fileName, int fileSize) {
        try {
            // 创建上传Object的Metadata
            ObjectMetadata metadata = new ObjectMetadata();
            // 设置md5
            byte[] bytes = IOUtils.readStreamAsByteArray(is);

            String md5 = BinaryUtil.toBase64String(BinaryUtil.calculateMd5(bytes));
            metadata.setContentMD5(md5);
            // 设置md5自定义值 不支持自定义MD5加密
            // metadata.addUserMetadata(SELF_METADATA_MD5, md5);
            // 上传的文件的长度
            metadata.setContentLength(bytes.length);
            // 指定该Object被下载时的网页的缓存行为
            metadata.setCacheControl("no-cache");
            // 指定该Object下设置Header
            metadata.setHeader("Pragma", "no-cache");
            // 指定该Object被下载时的内容编码格式
            metadata.setContentEncoding("utf-8");
            // 文件的MIME，定义文件的类型及网页编码，决定浏览器将以什么形式、什么编码读取文件。如果用户没有指定则根据Key或文件名的扩展名生成，
            // 如果没有扩展名则填默认值application/octet-stream
            metadata.setContentType(getContentType(fileName));
            // 指定该Object被下载时的名称（指示MINME用户代理如何显示附加的文件，打开或下载，及文件名称）
            metadata.setContentDisposition("filename/filesize=" + fileName + OssContext.SLASH + fileSize + "Byte.");

            String key = OssContext.generateKey(folder, fileName);
            ossClient.putObject(bucketName, key, new ByteArrayInputStream(bytes), metadata);

            return md5;
        } catch (Exception e) {
            LOGGER.error("AliyunOssStrategy.uploadObjectInput fail.", e);
        }
        return null;
    }

    @Override
    public InputStream getInputStream(String bucketName, String firstKey) throws Exception {
        OSSObject ossObject = getOssClient().getObject(bucketName, firstKey);
        return ossObject.getObjectContent();
    }

    @Override
    public List<String> moveObject(String sourceBucketName, String sourceKey, String targetBucketName,
        String targetFolder) {
        String newKey =
            StringUtils.isNotBlank(targetFolder) ? OssContext.generateKey(targetFolder, sourceKey) : sourceKey;
        // 获取md5
        String md5 = getOssClient().getObjectMetadata(sourceBucketName, sourceKey).getContentMD5();

        // 移动文件
        getOssClient().copyObject(sourceBucketName, sourceKey, targetBucketName, newKey);
        // 删除文件
        getOssClient().deleteObject(sourceBucketName, sourceKey);
        return Arrays.asList(newKey, new String(Hex.encodeHex(BinaryUtil.fromBase64String(md5))));
    }

    @Override
    public List<String> move(String sourceBucketName, String sourceKey, String targetBucketName, String targetKey) {
        String newKey = StringUtils.isNotBlank(targetKey) ? targetKey : sourceKey;
        // 获取md5
        ObjectMetadata objectMetadata = getOssClient().getObjectMetadata(sourceBucketName, sourceKey);
        String md5 = objectMetadata.getContentMD5();
        long length = objectMetadata.getContentLength();
        // 移动文件
        getOssClient().copyObject(sourceBucketName, sourceKey, targetBucketName, newKey);
        // 删除文件
        getOssClient().deleteObject(sourceBucketName, sourceKey);
        return Lists.newArrayList(String.valueOf(length), new String(Hex.encodeHex(BinaryUtil.fromBase64String(md5))));
    }

    @Override
    public void renameAndMove(String sourceBucketName, String sourceKey, String targetBucketName, String newKey) {
        // 移动文件
        getOssClient().copyObject(sourceBucketName, sourceKey, targetBucketName, newKey);
        // 删除文件
        getOssClient().deleteObject(sourceBucketName, sourceKey);
    }

    @Override
    public void deleteObject(String bucketName, String key) {
        // 移动文件
        getOssClient().deleteObject(bucketName, key);
    }

    @Override
    public List<String> copyObject(String sourceBucketName, String sourceKey, String targetBucketName,
        String targetFolder) {
        String newKey =
            StringUtils.isNotBlank(targetFolder) ? OssContext.generateKey(targetFolder, sourceKey) : sourceKey;
        // 获取md5
        String md5 = getOssClient().getObjectMetadata(sourceBucketName, sourceKey).getContentMD5();

        // 移动文件
        getOssClient().copyObject(sourceBucketName, sourceKey, targetBucketName, newKey);
        return Arrays.asList(newKey, new String(Hex.encodeHex(BinaryUtil.fromBase64String(md5))));
    }

    @Override
    public List<String> copy(String sourceBucketName, String sourceKey, String targetBucketName, String targetKey) {
        String newKey = StringUtils.isNotBlank(targetKey) ? targetKey : sourceKey;
        // 获取md5
        ObjectMetadata objectMetadata = getOssClient().getObjectMetadata(sourceBucketName, sourceKey);
        String md5 = objectMetadata.getContentMD5();
        long length = objectMetadata.getContentLength();
        // 移动文件
        getOssClient().copyObject(sourceBucketName, sourceKey, targetBucketName, newKey);
        return Lists.newArrayList(String.valueOf(length), new String(Hex.encodeHex(BinaryUtil.fromBase64String(md5))));
    }

    @Override
    public URL generatePresignedUrl(String bucketName, String key, String fileName, boolean download) {
        return doGeneratePreSignUrl(bucketName, key, fileName, download);
    }

    @Override
    public boolean existFile(String bucketName, String sourceKey) {
        return getOssClient().doesObjectExist(bucketName, sourceKey);
    }

    @Override
    public String uploadSignature(String bucket, String key) {
        throw new MethodNotImplException();
    }
}
