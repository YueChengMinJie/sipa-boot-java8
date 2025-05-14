package com.sipa.boot.java8.common.utils;

import java.io.*;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.io.IOUtils;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.font.FontProvider;
import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

/**
 * @author feizhihao
 * @date 2019-09-02 13:43
 */
public class PdfUtils {
    public static final Log logger = LogFactory.get(PdfUtils.class);

    public static final String FONT_PATH = "fonts/Alibaba-PuHuiTi-Regular.otf";

    public static final String DIRECTION_HORIZONTAL = "horizontal";

    public static final String DIRECTION_VERTICAL = "vertical";

    private PdfUtils() {}

    private static PdfUtils instanse;

    private static Configuration configuration;

    private static FontProvider fontProvider;

    static {
        if (instanse == null) {
            synchronized (PdfUtils.class) {
                if (instanse == null) {
                    instanse = new PdfUtils();
                    instanse.initFont();
                }
                if (configuration == null) {
                    configuration = new Configuration(Configuration.VERSION_2_3_29);
                }
            }
        }
    }

    /**
     * freemarker 引擎渲染 html
     *
     * @param dataMap
     *            传入 html 模板的 Map 数据
     * @param ftlFilePath
     *            html 模板文件相对路径(相对于 resources路径,路径 + 文件名) eg: "templates/pdf_export_demo.ftl"
     * @param locale
     *            语言名称
     * @return html
     */
    public static String freemarkerRender(Map<String, Object> dataMap, String ftlFilePath, Locale locale) {
        Writer out = new StringWriter();
        BufferedWriter writer = new BufferedWriter(out);
        configuration.setDefaultEncoding("UTF-8");
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        try {
            configuration.setDirectoryForTemplateLoading(new File(ResourceFileUtils.getParent(ftlFilePath)));
            Template template = configuration.getTemplate(ResourceFileUtils.getFileName(ftlFilePath), locale);
            template.process(dataMap, writer);
            String htmlStr = out.toString();
            writer.flush();
            writer.close();
            return htmlStr;
        } catch (Exception e) {
            logger.info(e);
        } finally {
            try {
                out.close();
            } catch (Exception e) {
                logger.info(e);
            }
        }
        return null;
    }

    /**
     * 使用 iText 生成 PDF 文档
     *
     * @param html
     *            html 模板文件字符串
     */
    public static byte[] generatePdf(String html, String direction) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument doc = new PdfDocument(writer);
        doc.setDefaultPageSize(getPageSize(direction));
        doc.getDefaultPageSize().applyMargins(30, 30, 30, 30, true);

        ConverterProperties properties = new ConverterProperties();
        properties.setFontProvider(fontProvider);

        try {
            HtmlConverter.convertToPdf(html, doc, properties);
        } catch (Exception e) {
            logger.info(e);
        }
        return out.toByteArray();
    }

    private void initFont() {
        try {
            fontProvider = new FontProvider();

            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(FONT_PATH);

            fontProvider.addFont(IOUtils.toByteArray(Objects.requireNonNull(inputStream)));
        } catch (Exception e) {
            throw new RuntimeException("PdfUtils font init error:{}", e);
        }
    }

    private static PageSize getPageSize(String direction) {
        switch (direction) {
            case DIRECTION_HORIZONTAL:
                // 横向
                return new PageSize(842, 595);
            case DIRECTION_VERTICAL:
            default:
                // 纵向
                return new PageSize(595, 842);
        }
    }
}
