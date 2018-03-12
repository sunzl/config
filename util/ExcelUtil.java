package com.namibank.df.fl.util;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

/**
 * @explain: 释义：数据导出到Excel
 * @version: 日期：2012-9-14 下午05:50:06
 */
@SuppressWarnings("deprecation")
public class ExcelUtil {
    @SuppressWarnings("rawtypes")
    public static void main(String[] args) throws Exception {

        FileOutputStream os = new FileOutputStream(new File("d:/test.xls"));
        List<String[]> headNames = new ArrayList<String[]>();
        headNames.add(new String[]{"用户名", "密码", "电子邮件", "类型", "角色"});
        List<String[]> fieldNames = new ArrayList<String[]>();
        fieldNames.add(new String[]{"userName", "pwd", "email", "typeStr", "roleStr"});


        LinkedHashMap<String, List> objsMap = new LinkedHashMap<String, List>();
        List<Map<String, Object>> obj = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userName", "张三");
        map.put("pwd", "123");
        map.put("email", "123@qq.com");
        map.put("typeStr", "typeStr");
        map.put("roleStr", "roleStr");
        obj.add(map);

        objsMap.put("sheet01", obj);

        ExportSetInfo setInfo = new ExportSetInfo();
        setInfo.setTitles(new String[]{"馋八戒后台用户信息"});
        setInfo.setObjsMap(objsMap);
        setInfo.setFieldNames(fieldNames);
        setInfo.setHeadNames(headNames);
        setInfo.setOut(os);

        // 将需要导出的数据输出到baos
        try {
            ExcelUtil.export2Excel(setInfo);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static HSSFWorkbook wb;

    private static CellStyle titleStyle;        // 标题行样式
    private static Font titleFont;              // 标题行字体        
    private static CellStyle dateStyle;         // 日期行样式
    private static Font dateFont;               // 日期行字体
    private static CellStyle headStyle;         // 表头行样式
    private static Font headFont;               // 表头行字体
    private static CellStyle contentStyle;     // 内容行样式
    private static Font contentFont;            // 内容行字体

    /**
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @Description: 将Map里的集合对象数据输出Excel数据流
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void export2Excel(ExportSetInfo setInfo) throws
            IOException, IllegalArgumentException, IllegalAccessException {
        init();
        Set<Entry<String, List>> set = setInfo.getObjsMap().entrySet();
        String[] sheetNames = new String[setInfo.getObjsMap().size()];
        int sheetNameNum = 0;
        for (Entry<String, List> entry : set) {
            sheetNames[sheetNameNum] = entry.getKey();
            sheetNameNum++;
        }
        HSSFSheet[] sheets = getSheets(setInfo.getObjsMap().size(), sheetNames);
        int sheetNum = 0;
        for (Entry<String, List> entry : set) {
//        	createTableTitleRow(setInfo, sheets, sheetNum);
            // Sheet
            List objs = entry.getValue();
            // 表头
            creatTableHeadRow(setInfo, sheets, sheetNum);
            // 表体
            String[] fieldNames = setInfo.getFieldNames().get(sheetNum);
            int rowNum = 1;
            for (Object obj : objs) {
                HSSFRow contentRow = sheets[sheetNum].createRow(rowNum);
                contentRow.setHeight((short) 300);
                HSSFCell[] cells = getCells(contentRow, setInfo.getFieldNames().get(sheetNum).length);
                int cellNum = 0;                    // 去掉一列序号，因此从1开始
                if (fieldNames != null) {
                    for (int num = 0; num < fieldNames.length; num++) {
                        // Object value = ReflectionUtils.invokeGetterMethod(obj, fieldNames[num]);

                        Map map = (Map<String, Object>) obj;
                        Object value = map.get(fieldNames[num]);

                        cells[cellNum].setCellValue(value == null ? "" : value.toString());
                        cellNum++;
                    }
                }
                rowNum++;
            }
            adjustColumnSize(sheets, sheetNum, fieldNames); // 自动调整列宽
            sheetNum++;
        }
        wb.write(setInfo.getOut());
    }

    /**
     * @Description: 初始化
     */
    private static void init() {
        wb = new HSSFWorkbook();

        titleFont = wb.createFont();
        titleStyle = wb.createCellStyle();
        dateStyle = wb.createCellStyle();
        dateFont = wb.createFont();
        headStyle = wb.createCellStyle();
        headFont = wb.createFont();
        contentStyle = wb.createCellStyle();
        contentFont = wb.createFont();

        initTitleCellStyle();
        initTitleFont();
        initDateCellStyle();
        initDateFont();
        initHeadCellStyle();
        initHeadFont();
        initContentCellStyle();
        initContentFont();
    }

    /**
     * @Description: 自动调整列宽
     */
    private static void adjustColumnSize(HSSFSheet[] sheets, int sheetNum,
                                         String[] fieldNames) {
        for (int i = 0; i < fieldNames.length + 1; i++) {
            sheets[sheetNum].autoSizeColumn(i, true);
        }
    }

    /**
     * @Description: 创建标题行(需合并单元格)
     */
    private static void createTableTitleRow(ExportSetInfo setInfo,
                                            HSSFSheet[] sheets, int sheetNum) {
        CellRangeAddress titleRange = new CellRangeAddress(0, 0, 0,
                setInfo.getFieldNames().get(sheetNum).length - 1);
        sheets[sheetNum].addMergedRegion(titleRange);
        HSSFRow titleRow = sheets[sheetNum].createRow(0);
        titleRow.setHeight((short) 800);
        HSSFCell titleCell = titleRow.createCell(0);
        titleCell.setCellStyle(titleStyle);
        titleCell.setCellValue(setInfo.getTitles()[sheetNum]);
    }

    /**
     * @Description: 创建日期行(需合并单元格)
     */
    @SuppressWarnings("unused")
    private static void createTableDateRow(ExportSetInfo setInfo,
                                           HSSFSheet[] sheets, int sheetNum) {
        CellRangeAddress dateRange = new CellRangeAddress(1, 1, 0,
                setInfo.getFieldNames().get(sheetNum).length - 1);
        sheets[sheetNum].addMergedRegion(dateRange);
        HSSFRow dateRow = sheets[sheetNum].createRow(1);
        dateRow.setHeight((short) 350);
        HSSFCell dateCell = dateRow.createCell(0);
        dateCell.setCellStyle(dateStyle);
        dateCell.setCellValue(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
    }

    /**
     * @Description: 创建表头行(需合并单元格)
     */
    private static void creatTableHeadRow(ExportSetInfo setInfo,
                                          HSSFSheet[] sheets, int sheetNum) {
        // 表头
        HSSFRow headRow = sheets[sheetNum].createRow(0);
        headRow.setHeight((short) 350);
        // 列头名称
        for (int num = 0, len = setInfo.getHeadNames().get(sheetNum).length; num < len; num++) {
            HSSFCell headCell = headRow.createCell(num);
            headCell.setCellStyle(headStyle);
            headCell.setCellValue(setInfo.getHeadNames().get(sheetNum)[num]);
        }
    }

    /**
     * @Description: 创建所有的Sheet
     */
    private static HSSFSheet[] getSheets(int num, String[] names) {
        HSSFSheet[] sheets = new HSSFSheet[num];
        for (int i = 0; i < num; i++) {
            sheets[i] = wb.createSheet(names[i]);
        }
        return sheets;
    }

    /**
     * @Description: 创建内容行的每一列(附加一列序号)
     */
    private static HSSFCell[] getCells(HSSFRow contentRow, int num) {
        HSSFCell[] cells = new HSSFCell[num];

        for (int i = 0, len = cells.length; i < len; i++) {
            cells[i] = contentRow.createCell(i);
            cells[i].setCellStyle(contentStyle);
        }
        return cells;
    }

    /**
     * @Description: 初始化标题行样式
     */
    private static void initTitleCellStyle() {
        titleStyle.setAlignment(CellStyle.ALIGN_CENTER);
        titleStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        titleStyle.setFont(titleFont);
        titleStyle.setFillBackgroundColor(IndexedColors.SKY_BLUE.index);
    }

    /**
     * @Description: 初始化日期行样式
     */
    private static void initDateCellStyle() {
        dateStyle.setAlignment(CellStyle.ALIGN_CENTER_SELECTION);
        dateStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        dateStyle.setFont(dateFont);
        dateStyle.setFillBackgroundColor(IndexedColors.SKY_BLUE.index);
    }

    /**
     * @Description: 初始化表头行样式
     */
    private static void initHeadCellStyle() {
        headStyle.setAlignment(CellStyle.ALIGN_CENTER);
        headStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        headStyle.setFont(headFont);
        headStyle.setFillBackgroundColor(IndexedColors.YELLOW.index);
        headStyle.setBorderTop(CellStyle.BORDER_MEDIUM);
        headStyle.setBorderBottom(CellStyle.BORDER_THIN);
        headStyle.setBorderLeft(CellStyle.BORDER_THIN);
        headStyle.setBorderRight(CellStyle.BORDER_THIN);
        headStyle.setTopBorderColor(IndexedColors.BLUE.index);
        headStyle.setBottomBorderColor(IndexedColors.BLUE.index);
        headStyle.setLeftBorderColor(IndexedColors.BLUE.index);
        headStyle.setRightBorderColor(IndexedColors.BLUE.index);
    }

    /**
     * @Description: 初始化内容行样式
     */
    private static void initContentCellStyle() {
        contentStyle.setAlignment(CellStyle.ALIGN_CENTER);
        contentStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        contentStyle.setFont(contentFont);
        contentStyle.setBorderTop(CellStyle.BORDER_THIN);
        contentStyle.setBorderBottom(CellStyle.BORDER_THIN);
        contentStyle.setBorderLeft(CellStyle.BORDER_THIN);
        contentStyle.setBorderRight(CellStyle.BORDER_THIN);
        contentStyle.setTopBorderColor(IndexedColors.BLUE.index);
        contentStyle.setBottomBorderColor(IndexedColors.BLUE.index);
        contentStyle.setLeftBorderColor(IndexedColors.BLUE.index);
        contentStyle.setRightBorderColor(IndexedColors.BLUE.index);
        contentStyle.setWrapText(true); // 字段换行
    }

    /**
     * @Description: 初始化标题行字体
     */
    private static void initTitleFont() {
        titleFont.setFontName("华文楷体");
        titleFont.setFontHeightInPoints((short) 20);
        titleFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        titleFont.setCharSet(Font.DEFAULT_CHARSET);
        titleFont.setColor(IndexedColors.BLUE_GREY.index);
    }

    /**
     * @Description: 初始化日期行字体
     */
    private static void initDateFont() {
        dateFont.setFontName("隶书");
        dateFont.setFontHeightInPoints((short) 10);
        dateFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        dateFont.setCharSet(Font.DEFAULT_CHARSET);
        dateFont.setColor(IndexedColors.BLUE_GREY.index);
    }

    /**
     * @Description: 初始化表头行字体
     */
    private static void initHeadFont() {
        headFont.setFontName("宋体");
        headFont.setFontHeightInPoints((short) 10);
        headFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        headFont.setCharSet(Font.DEFAULT_CHARSET);
        headFont.setColor(IndexedColors.BLUE_GREY.index);
    }

    /**
     * @Description: 初始化内容行字体
     */
    private static void initContentFont() {
        contentFont.setFontName("宋体");
        contentFont.setFontHeightInPoints((short) 10);
        contentFont.setBoldweight(Font.BOLDWEIGHT_NORMAL);
        contentFont.setCharSet(Font.DEFAULT_CHARSET);
        contentFont.setColor(IndexedColors.BLUE_GREY.index);
    }


    /**
     * @Description: 封装Excel导出的设置信息
     * @author: 谭又中
     */
    public static class ExportSetInfo {
        @SuppressWarnings({"rawtypes"})
        private LinkedHashMap<String, List> objsMap;

        private String[] titles;

        private List<String[]> headNames;

        private List<String[]> fieldNames;

        private OutputStream out;


        @SuppressWarnings("rawtypes")
        public LinkedHashMap<String, List> getObjsMap() {
            return objsMap;
        }

        /**
         * @param objMap 导出数据
         *               <p>
         *               泛型
         *               String : 代表sheet名称
         *               List : 代表单个sheet里的所有行数据
         */
        @SuppressWarnings({"rawtypes"})
        public void setObjsMap(LinkedHashMap<String, List> objsMap) {
            this.objsMap = objsMap;
        }

        public List<String[]> getFieldNames() {
            return fieldNames;
        }

        /**
         * @param clazz 对应每个sheet里的每行数据的对象的属性名称
         */
        public void setFieldNames(List<String[]> fieldNames) {
            this.fieldNames = fieldNames;
        }

        public String[] getTitles() {
            return titles;
        }

        /**
         * @param titles 对应每个sheet里的标题，即顶部大字
         */
        public void setTitles(String[] titles) {
            this.titles = titles;
        }

        public List<String[]> getHeadNames() {
            return headNames;
        }

        /**
         * @param headNames 对应每个页签的表头的每一列的名称
         */
        public void setHeadNames(List<String[]> headNames) {
            this.headNames = headNames;
        }

        public OutputStream getOut() {
            return out;
        }

        /**
         * @param out Excel数据将输出到该输出流
         */
        public void setOut(OutputStream out) {
            this.out = out;
        }
    }
}