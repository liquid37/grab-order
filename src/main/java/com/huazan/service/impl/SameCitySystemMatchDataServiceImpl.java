package com.huazan.service.impl;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.huazan.pojo.deep.DeepMatchData;
import com.huazan.pojo.deep.DeepMatchParam;
import com.huazan.pojo.deep.DeepMatchRule;
import com.huazan.pojo.samecity.SameCityMatchData;
import com.huazan.pojo.samecity.SameCityMatchParam;
import com.huazan.pojo.samecity.SameCityMatchRule;
import com.huazan.service.IGrabOrderMatchDataService;
import com.huazan.utils.EasyPoiSheetNameUtil;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Service("sameCitySystemMatchDataService")
public class SameCitySystemMatchDataServiceImpl extends AbstractSystemMatchDataServiceImpl<SameCityMatchRule> implements IGrabOrderMatchDataService {

    @Override
    void loadMatchData() {
        if(matchDatas.size() == 0){
            InputStream inputStream = null;
            try {
                inputStream = new FileInputStream("./matchs/common-match-data.xlsx");
                Map<String, Integer> stringIntegerMap = EasyPoiSheetNameUtil.convertSheetName(inputStream);
                stringIntegerMap.forEach((k,v) ->{
                    ImportParams params = new ImportParams();
                    SameCityMatchRule rule = new SameCityMatchRule();
                    SameCityMatchParam param = convertParam(k);
                    rule.setRuleParam(param);
                    params.setStartSheetIndex(v);
                    try {
                        InputStream inputStream2 = new FileInputStream("./matchs/common-match-data.xlsx");
                        List<SameCityMatchData> dataList = ExcelImportUtil.importExcel(inputStream2, SameCityMatchData.class, params);
                        rule.setMatchDataList(dataList);
                        matchDatas.add(rule);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private SameCityMatchParam convertParam(String sheetName){
        SameCityMatchParam param = new SameCityMatchParam();
        if(sheetName.contains("以上")){
            String minAmountStr = sheetName.replace("以上", "");
            param.setMinAmount(Integer.valueOf(minAmountStr));
        }else if(sheetName.contains("以下")){
            String maxAmountStr = sheetName.replace("以下", "");
            param.setMaxAmount(Integer.valueOf(maxAmountStr));
        }else if(sheetName.contains("-")){
            String[] amounts = sheetName.split("-");
            param.setMinAmount(Integer.valueOf(amounts[0]));
            param.setMaxAmount(Integer.valueOf(amounts[1]));
        }
        return param;
    }
}
