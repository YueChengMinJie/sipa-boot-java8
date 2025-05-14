package com.sipa.boot.java8.common.utils;

import java.io.InputStream;
import java.util.Objects;

import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.metadata.BaseRowModel;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.sipa.boot.java8.common.archs.excel.ExcelImportExecute;
import com.sipa.boot.java8.common.archs.excel.ExcelResult;
import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;

/**
 * @Author zhfei
 * @Date 2019/7/30 10:04 PM
 */
public class ExcelUtils {
    private static final Log LOGGER = LogFactory.get(ExcelUtils.class);

    public static final int DEFAULT_SHEET_NO = 1;

    public static final int DEFAULT_HEADER_NO = 1;

    public static final int EXCEL_MAX_ROWS = 1048575;

    /**
     * 读取某个 sheet 的 Excel
     *
     * @param fileName
     *            文件名
     * @param inputStream
     *            文件流
     * @param rowModel
     *            实体类映射，继承 BaseRowModel 类
     * @param sheetNo
     *            sheet 的序号 从1开始
     * @return Excel 数据 list
     */
    public static <T extends BaseRowModel> ExcelResult<T> readExcel(String fileName, InputStream inputStream,
        Class<T> rowModel, int sheetNo) throws Exception {
        return readExcel(fileName, inputStream, rowModel, sheetNo, DEFAULT_HEADER_NO, null);
    }

    /**
     * 读取某个 sheet 的 Excel
     *
     * @param fileName
     *            文件名
     * @param inputStream
     *            文件流
     * @param rowModel
     *            实体类映射，继承 BaseRowModel 类
     * @param sheetNo
     *            sheet 的序号 从1开始
     * @return Excel 数据 list
     */
    public static <T extends BaseRowModel> ExcelResult<T> readExcel(String fileName, InputStream inputStream,
        Class<T> rowModel, int sheetNo, ExcelImportExecute excelImportExecute) throws Exception {
        return readExcel(fileName, inputStream, rowModel, sheetNo, DEFAULT_HEADER_NO, excelImportExecute);
    }

    /**
     * 读取某个 sheet 的 Excel
     *
     * @param fileName
     *            文件名
     * @param inputStream
     *            文件流
     * @param rowModel
     *            实体类映射，继承 BaseRowModel 类
     * @param sheetNo
     *            sheet 的序号 从1开始
     * @param headLineNum
     *            表头行数，默认为1
     * @return Excel 数据 list
     */
    public static <T extends BaseRowModel> ExcelResult<T> readExcel(String fileName, InputStream inputStream,
        Class<T> rowModel, int sheetNo, int headLineNum, ExcelImportExecute excelImportExecute) throws Exception {
        if (Objects.isNull(excelImportExecute)) {
            excelImportExecute = new ExcelImportExecute();
        }
        ExcelReader reader = getReader(fileName, inputStream, excelImportExecute);
        reader.read(new Sheet(sheetNo, headLineNum, rowModel));
        return excelImportExecute.getExcelData();
    }

    /**
     * 返回 ExcelReader
     *
     * @param fileName
     *            文件名
     * @param inputStream
     *            文件流
     * @param excelImportExecute
     *            new ExcelImportExecute()
     */
    private static ExcelReader getReader(String fileName, InputStream inputStream,
        ExcelImportExecute excelImportExecute) throws Exception {
        LOGGER.info("fileName [{}]", fileName);
        if (fileName == null) {
            throw new Exception("文件格式错误！");
        }
        if (!fileName.toLowerCase().endsWith(ExcelTypeEnum.XLS.getValue())
            && !fileName.toLowerCase().endsWith(ExcelTypeEnum.XLSX.getValue())) {
            throw new Exception("文件格式错误！");
        }
        return new ExcelReader(inputStream, null, excelImportExecute);
    }
}
