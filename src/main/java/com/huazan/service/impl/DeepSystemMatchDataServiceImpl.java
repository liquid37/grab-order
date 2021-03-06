package com.huazan.service.impl;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.huazan.pojo.deep.DeepMatchData;
import com.huazan.pojo.deep.DeepMatchParam;
import com.huazan.pojo.deep.DeepMatchRule;
import com.huazan.service.IGrabOrderMatchDataService;
import com.huazan.utils.EasyPoiSheetNameUtil;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Service("deepSystemMatchDataService")
public class DeepSystemMatchDataServiceImpl extends AbstractSystemMatchDataServiceImpl<DeepMatchRule> implements IGrabOrderMatchDataService {

    @Override
    public void loadMatchData() throws IOException {
        if(matchDatas.size() == 0){
            InputStream inputStream = null;
            try {
                inputStream = new FileInputStream("./matchs/common-match-data.xlsx");
                Map<String, Integer> stringIntegerMap = EasyPoiSheetNameUtil.convertSheetName(inputStream);
                stringIntegerMap.forEach((k,v) ->{
                    ImportParams params = new ImportParams();
                    DeepMatchRule rule = new DeepMatchRule();
                    DeepMatchParam param = convertParam(k);
                    rule.setRuleParam(param);
                    params.setStartSheetIndex(v);
                    try {
                        InputStream inputStream2 = new FileInputStream("./matchs/common-match-data.xlsx");
                        List<DeepMatchData> dataList = ExcelImportUtil.importExcel(inputStream2, DeepMatchData.class, params);
                        rule.setMatchDataList(dataList);
                        matchDatas.add(rule);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                });
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(inputStream!=null){
                    inputStream.close();
                }
            }
        }
    }

    private DeepMatchParam convertParam(String sheetName){
        DeepMatchParam param = new DeepMatchParam();
        if(sheetName.contains("??????")){
            String minAmountStr = sheetName.replace("??????", "");
            param.setMinAmount(Integer.valueOf(minAmountStr));
        }else if(sheetName.contains("??????")){
            String maxAmountStr = sheetName.replace("??????", "");
            param.setMaxAmount(Integer.valueOf(maxAmountStr));
        }else if(sheetName.contains("-")){
            String[] amounts = sheetName.split("-");
            param.setMinAmount(Integer.valueOf(amounts[0]));
            param.setMaxAmount(Integer.valueOf(amounts[1]));
        }
        return param;
    }

}
