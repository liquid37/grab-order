package com.huazan.utils;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class EasyPoiSheetNameUtil {

    public static Map<String,Integer> convertSheetName(InputStream fileInputStream){
        Map<String,Integer> results = new HashMap<>();
        OPCPackage opcPackage = null;
        try {
            opcPackage = OPCPackage.open(fileInputStream);
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        XSSFReader xssfReader = null;
        try {
            xssfReader = new XSSFReader(opcPackage);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OpenXML4JException e) {
            e.printStackTrace();
        }
        Iterator sheets = null;
        try {
            sheets = xssfReader.getSheetsData();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }
        int index = 0;
        if (sheets instanceof XSSFReader.SheetIterator) {
            XSSFReader.SheetIterator sheetIterator = (XSSFReader.SheetIterator) sheets;
            while (sheetIterator.hasNext()) {
                InputStream inputStream = sheetIterator.next();
                //list.add(sheetIterator.getSheetName());
                results.put(sheetIterator.getSheetName(),index);
                index++;
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            opcPackage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return results;
    }
}
