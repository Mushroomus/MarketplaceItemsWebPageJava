package com.marketplace.MarketplaceItems.components;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import com.marketplace.MarketplaceItems.entity.Sale;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFHyperlink;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

@Component
public class ExcelGenerator {

    public void generateExcelFile(List<Sale> sales, HttpServletResponse response) throws IOException {
        // create the workbook and sheet
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet 1");

        Font headerFont = workbook.createFont();
        headerFont.setBold(true);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        CellStyle dateCellStyle = workbook.createCellStyle();
        dateCellStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("yyyy-mm-dd hh:mm"));


        CellStyle linkStyle = workbook.createCellStyle();
        Font linkFont = workbook.createFont();
        linkFont.setUnderline(Font.U_SINGLE);
        linkFont.setColor(IndexedColors.BLUE.getIndex());
        linkStyle.setFont(linkFont);

        DataFormat format = workbook.createDataFormat();
        CellStyle currencyStyle = workbook.createCellStyle();
        currencyStyle.setDataFormat(format.getFormat("#,##0.00$"));

        // create the header row
        Row headerRow = sheet.createRow(0);
        Cell headerOrderId = headerRow.createCell(0);
        headerOrderId.setCellValue("Order ID");
        headerOrderId.setCellStyle(headerCellStyle);

        Cell headerItemSku = headerRow.createCell(1);
        headerItemSku .setCellValue("Item Sku");
        headerItemSku .setCellStyle(headerCellStyle);

        Cell headerPrice = headerRow.createCell(2);
        headerPrice.setCellValue("Price");
        headerPrice.setCellStyle(headerCellStyle);

        Cell headerDate = headerRow.createCell(3);
        headerDate.setCellValue("Date");
        headerDate.setCellStyle(headerCellStyle);

        Hyperlink link = workbook.getCreationHelper().createHyperlink(HyperlinkType.URL);
        int rowNum = 1;

        for (Sale sale : sales) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(sale.getOrderId());

            String sku = sale.getAssignSku();
            link.setAddress("https://marketplace.tf/items/tf2/" + sku);
            Cell cell = row.createCell(1);
            cell.setCellStyle(linkStyle);
            String formula = "HYPERLINK(\"" + link.getAddress() + "\",\"" + sku + "\")";
            cell.setCellFormula(formula);

            cell = row.createCell(2);
            cell.setCellValue(sale.getPrice());
            cell.setCellStyle(currencyStyle);

            row.createCell(3).setCellValue(sale.getDate());
            row.getCell(3).setCellStyle(dateCellStyle);
        }

        int lastRowIndex = sheet.getLastRowNum();
        Row totalRow = sheet.createRow(lastRowIndex + 2);
        Cell totalLabelCell = totalRow.createCell(0);
        totalLabelCell.setCellValue("Total");
        totalLabelCell.setCellStyle(headerCellStyle);
        CellRangeAddress range = CellRangeAddress.valueOf("C2:C" + lastRowIndex);
        Cell totalCell = totalRow.createCell(2);
        totalCell.setCellFormula("SUM(" + range.formatAsString() + ")");
        totalCell.setCellStyle(currencyStyle);

        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        evaluator.evaluateFormulaCell(totalCell);

        for (int i = 0; i < 4; i++) {
            sheet.autoSizeColumn(i);
        }

        // set the response headers and content type
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=data.xlsx");

        // write the workbook to the response output stream
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);

        // close the streams and workbook
        outputStream.close();
        workbook.close();
    }
}