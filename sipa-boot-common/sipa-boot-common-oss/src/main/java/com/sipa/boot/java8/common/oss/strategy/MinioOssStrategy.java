package com.sipa.boot.java8.common.oss.strategy;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.aliyun.oss.common.utils.BinaryUtil;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sipa.boot.java8.common.oss.OssContext;
import com.sipa.boot.java8.common.oss.annotation.OssStrategy;
import com.sipa.boot.java8.common.oss.property.OssProperties;

import io.minio.MinioClient;
import io.minio.ObjectStat;

/**
 * @author feizhihao
 * @date 2019-08-20 11:32
 */
@OssStrategy("minio")
public class MinioOssStrategy extends BaseStrategy implements IOssStrategy {
    private final OssProperties ossProperties;

    private MinioClient minioClient;

    private int version;

    public MinioOssStrategy(OssProperties ossProperties) {
        this.ossProperties = ossProperties;
    }

    private MinioClient getOssClient() {
        if (minioClient == null || version != ossProperties.getVersion()) {
            try {
                minioClient = new MinioClient(ossProperties.getEndpoint(), ossProperties.getAccessKey(),
                    ossProperties.getSecretKey());
                version = ossProperties.getVersion();
                return minioClient;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return minioClient;
    }

    @Override
    public URL generatePresignedUrl(String bucketName, String key, String fileName) {
        return doGeneratePreSignUrl(bucketName, key, fileName, true);
    }

    @Override
    public String generatePreSignUrl(String bucket, String sourceKey, String fileName, boolean https) {
        try {
            Map<String, String> reqParams = Maps.newHashMap();
            reqParams.put("response-content-disposition", "attachment; filename=" + fileName);
            String url = getOssClient().presignedGetObject(bucket, sourceKey, OSS_FILE_EXPIRE_TIME / 1000, reqParams);
            if (https) {
                return url;
            } else {
                return url.replaceAll("https", "http");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private URL doGeneratePreSignUrl(String bucketName, String key, String fileName, boolean download) {
        try {
            if (!getOssClient().bucketExists(bucketName)) {
                return null;
            }
            Map<String, String> reqParams = Maps.newHashMap();
            if (download) {
                reqParams.put("response-content-disposition", "attachment; filename=" + fileName);
            }
            return new URL(getOssClient().presignedGetObject(bucketName, key, OSS_FILE_EXPIRE_TIME / 1000, reqParams)
                .replaceAll("https", "http"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void uploadObjectInput(File file, String bucketName, String folder, String fileName) {
        uploadObjectInput(getOssClient(), file, bucketName, folder, fileName);
    }

    private static void uploadObjectInput(MinioClient minioClient, File file, String bucketName, String folder,
        String fileName) {
        try {
            long fileSize = file.length();
            // create object
            Map<String, String> headerMap = Maps.newHashMap();
            headerMap.put("Content-Type", getContentType(fileName));
            headerMap.put("Cache-Control", "no-cache");
            headerMap.put("Pragma", "no-cache");
            headerMap.put("Content-Encoding", "utf-8");
            headerMap.put("Content-Length", String.valueOf(fileSize));
            headerMap.put("Content-Disposition", "filename/filesize="
                + URLEncoder.encode(fileName, Charsets.UTF_8.name()) + OssContext.SLASH + fileSize + "Byte.");
            String key = OssContext.generateKey(folder, fileName);

            minioClient.putObject(bucketName, key, new FileInputStream(file), fileSize, headerMap, null, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String uploadObjectInput(byte[] fileBytes, String bucketName, String folder, String fileName) {
        return uploadObjectInput(getOssClient(), new ByteArrayInputStream(fileBytes), bucketName, folder, fileName,
            fileBytes.length);
    }

    private static String uploadObjectInput(MinioClient minioClient, InputStream is, String bucketName, String folder,
        String fileName, long fileSize) {
        try {
            byte[] bytes = IOUtils.toByteArray(is);
            String md5 = BinaryUtil.toBase64String(BinaryUtil.calculateMd5(bytes));
            // create object
            Map<String, String> headerMap = Maps.newHashMap();
            headerMap.put("Content-MD5", md5);
            headerMap.put("Content-Type", getContentType(fileName));
            headerMap.put("Cache-Control", "no-cache");
            headerMap.put("Pragma", "no-cache");
            headerMap.put("Content-Encoding", "utf-8");
            headerMap.put("Content-Length", String.valueOf(fileSize));
            headerMap.put("Content-Disposition", "filename/filesize="
                + URLEncoder.encode(fileName, Charsets.UTF_8.name()) + OssContext.SLASH + fileSize + "Byte.");
            String key = OssContext.generateKey(folder, fileName);

            minioClient.putObject(bucketName, key, new ByteArrayInputStream(bytes), fileSize, headerMap, null, null);

            return md5;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public InputStream getInputStream(String bucketName, String firstKey) throws Exception {
        return getOssClient().getObject(bucketName, firstKey);
    }

    @Override
    public List<String> moveObject(String sourceBucketName, String sourceKey, String targetBucketName,
        String targetFolder) {
        try {
            String newKey =
                StringUtils.isNotBlank(targetFolder) ? OssContext.generateKey(targetFolder, sourceKey) : sourceKey;
            // 获取md5
            String md5 = DigestUtils.md5Hex(IOUtils.toByteArray(getOssClient().getObject(sourceBucketName, sourceKey)));
            // 移动文件
            getOssClient().copyObject(sourceBucketName, sourceKey, targetBucketName, newKey);
            // 删除文件
            getOssClient().removeObject(sourceBucketName, sourceKey);
            return Arrays.asList(newKey, md5);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> move(String sourceBucketName, String sourceKey, String targetBucketName, String targetKey) {
        try {
            String newKey = StringUtils.isNotBlank(targetKey) ? targetKey : sourceKey;
            // 获取md5
            String md5 = DigestUtils.md5Hex(IOUtils.toByteArray(getOssClient().getObject(sourceBucketName, sourceKey)));
            String contentLength =
                getOssClient().statObject(sourceBucketName, sourceKey).httpHeaders().get("content-length").get(0);
            // 移动文件
            getOssClient().copyObject(sourceBucketName, sourceKey, targetBucketName, newKey);
            // 删除文件
            getOssClient().removeObject(sourceBucketName, sourceKey);
            return Lists.newArrayList(contentLength, md5);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void renameAndMove(String sourceBucketName, String sourceKey, String targetBucketName, String newKey) {
        // 移动文件
        try {
            getOssClient().copyObject(sourceBucketName, sourceKey, targetBucketName, newKey);
            // 删除文件
            getOssClient().removeObject(sourceBucketName, sourceKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteObject(String bucketName, String key) {
        // 移动文件
        try {
            getOssClient().removeObject(bucketName, key);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> copyObject(String sourceBucketName, String sourceKey, String targetBucketName,
        String targetFolder) {
        try {
            String newKey =
                StringUtils.isNotBlank(targetFolder) ? OssContext.generateKey(targetFolder, sourceKey) : sourceKey;
            // 获取md5
            String md5 = DigestUtils.md5Hex(IOUtils.toByteArray(getOssClient().getObject(sourceBucketName, sourceKey)));
            // 移动文件
            getOssClient().copyObject(sourceBucketName, sourceKey, targetBucketName, newKey);
            return Arrays.asList(newKey, md5);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> copy(String sourceBucketName, String sourceKey, String targetBucketName, String targetKey) {
        try {
            String newKey = StringUtils.isNotBlank(targetKey) ? targetKey : sourceKey;
            // 获取md5
            ObjectStat objectStat = getOssClient().statObject(sourceBucketName, sourceKey);
            String md5 = DigestUtils.md5Hex(IOUtils.toByteArray(getOssClient().getObject(sourceBucketName, sourceKey)));
            String contentLength = objectStat.httpHeaders().get("content-length").get(0);
            // 移动文件
            getOssClient().copyObject(sourceBucketName, sourceKey, targetBucketName, newKey);
            return Lists.newArrayList(contentLength, md5);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public URL generatePresignedUrl(String bucketName, String key, String fileName, boolean download) {
        return doGeneratePreSignUrl(bucketName, key, fileName, download);
    }

    @Override
    public boolean existFile(String bucketName, String sourceKey) {
        try (InputStream inputStream = getOssClient().getObject(bucketName, sourceKey)) {
            return inputStream != null;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String uploadSignature(String bucket, String key) {
        try {
            return getOssClient().presignedPutObject(bucket, key);
        } catch (Exception e) {
            LOGGER.error(e);
            return null;
        }
    }
}
