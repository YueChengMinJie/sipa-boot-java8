// package com.sipa.boot.java8.common.archs.excel;
//
// import java.lang.reflect.Field;
//
// import org.apache.commons.lang3.StringUtils;
//
// import com.alibaba.excel.annotation.ExcelProperty;
// import com.alibaba.excel.context.AnalysisContext;
// import com.alibaba.excel.event.AnalysisEventListener;
// import com.alibaba.excel.metadata.BaseRowModel;
// import com.sipa.boot.java8.common.log.Log;
// import com.sipa.boot.java8.common.log.LogFactory;
//
/// **
// * excel导入执行器
// *
// * @author feizhihao
// * @date 2019-07-31
// */
// public class ExcelImportExecute<T extends BaseRowModel> extends AnalysisEventListener<T> {
// private static final Log logger = LogFactory.get(ExcelImportExecute.class);
//
// private static final String SERIAL_VERSION_UID = "serialVersionUID";
//
// private ExcelResult<T> excelData = new ExcelResult<T>();
//
// /**
// * 通过 AnalysisContext 对象还可以获取当前 sheet，当前行等数据
// */
// @Override
// public void invoke(T object, AnalysisContext context) {
// logger.info("sheet no [{}], head num [{}], total count [{}]", context.getCurrentSheet().getSheetNo(),
// context.getCurrentSheet().getHeadLineMun(),
// context.getTotalCount() - context.getCurrentSheet().getHeadLineMun());
// if (!checkObjAllFieldsIsNull(object)) {
// excelData.getResultList().add(object);
// }
// }
//
// @Override
// public void doAfterAllAnalysed(AnalysisContext context) {
// // 获取最大数量
// excelData.setTotalCount(context.getTotalCount() - context.getCurrentSheet().getHeadLineMun());
// }
//
// /**
// * 判断对象中属性值是否全为空
// */
// public static boolean checkObjAllFieldsIsNull(Object object) {
// if (null == object) {
// return true;
// }
// try {
// for (Field f : object.getClass().getDeclaredFields()) {
// f.setAccessible(true);
// // 只校验带ExcelProperty注解的属性
// ExcelProperty property = f.getAnnotation(ExcelProperty.class);
// if (property == null || SERIAL_VERSION_UID.equals(f.getName())) {
// continue;
// }
// if (f.get(object) != null && StringUtils.isNotBlank(f.get(object).toString())) {
// return false;
// }
// }
// } catch (Exception e) {
// logger.warn("ExcelImportExecute.checkObjAllFieldsIsNull error", e);
// }
// return true;
// }
//
// public ExcelResult<T> getExcelData() {
// return excelData;
// }
// }
