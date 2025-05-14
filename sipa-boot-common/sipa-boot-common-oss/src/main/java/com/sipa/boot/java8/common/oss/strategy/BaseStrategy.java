package com.sipa.boot.java8.common.oss.strategy;

import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;

/**
 * @author feizhihao
 * @date 2019-08-21 10:35
 */
public class BaseStrategy {
    protected static final Log LOGGER = LogFactory.get(BaseStrategy.class);

    public static final int OSS_FILE_EXPIRE_TIME = 1000 * 60 * 60 * 24;

    public static final String BMP = ".bmp";

    public static final String GIF = ".gif";

    public static final String JPEG = ".jpeg";

    public static final String JPG = ".jpg";

    public static final String PNG = ".png";

    public static final String HTML = ".html";

    public static final String TXT = ".txt";

    public static final String VSD = ".vsd";

    public static final String PPT = ".ppt";

    public static final String PPTX = "pptx";

    public static final String DOC = ".doc";

    public static final String DOCX = "docx";

    public static final String XML = ".xml";

    public static final String PDF = ".pdf";

    /**
     * 通过文件名判断并获取OSS服务文件上传时文件的contentType
     *
     * @param fileName
     *            文件名
     * @return 文件的contentType
     */
    public static String getContentType(String fileName) {
        String fileExtension = getFileSuffix(fileName);
        if (BMP.equalsIgnoreCase(fileExtension)) {
            return "image/bmp";
        } else if (GIF.equalsIgnoreCase(fileExtension)) {
            return "image/gif";
        } else if (JPEG.equalsIgnoreCase(fileExtension) || JPG.equalsIgnoreCase(fileExtension)
            || PNG.equalsIgnoreCase(fileExtension)) {
            return "image/jpeg";
        } else if (HTML.equalsIgnoreCase(fileExtension)) {
            return "text/html";
        } else if (TXT.equalsIgnoreCase(fileExtension)) {
            return "text/plain";
        } else if (VSD.equalsIgnoreCase(fileExtension)) {
            return "application/vnd.visio";
        } else if (PPT.equalsIgnoreCase(fileExtension) || PPTX.equalsIgnoreCase(fileExtension)) {
            return "application/vnd.ms-powerpoint";
        } else if (DOC.equalsIgnoreCase(fileExtension) || DOCX.equalsIgnoreCase(fileExtension)) {
            return "application/msword";
        } else if (XML.equalsIgnoreCase(fileExtension)) {
            return "text/xml";
        } else if (PDF.equalsIgnoreCase(fileExtension)) {
            return "application/pdf";
        }
        // 默认返回类型
        return "application/octet-stream";
    }

    /**
     * 获取文件后缀名
     *
     * @param fileName
     *            文件名称
     * @return 后缀名
     */
    private static String getFileSuffix(String fileName) {
        // 文件的后缀名
        if (fileName.lastIndexOf(".") != -1) {
            return fileName.substring(fileName.lastIndexOf("."));
        } else {
            // 上传文件无后缀
            return "";
        }
    }
}
