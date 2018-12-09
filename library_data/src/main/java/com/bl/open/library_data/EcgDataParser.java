package com.bl.open.library_data;

import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EcgDataParser {

  private static final String TAG = "EcgDataParser";
  private static Gson sGson = new Gson();

  public static EcgBean parse(String json) {
    EcgBean bean = sGson.fromJson(json, EcgBean.class);
    Log.d(TAG, "parse() bean = " + bean);
    if (bean == null) {
      return null;
    }
    float[] exgMaxR = new float[bean.getWaveData().size()];
    int index = 0;
    for(EcgBean.Data data : bean.getWaveData()) {
      Float[] value = data.getValue();
      if (value == null || value.length < 100) {
        continue;
      }
      List<Float> ecgList = Arrays.asList(value);
      float maxData = 0;
      float minData = 0;
      ArrayList<Float> newDataList = new ArrayList<>(ecgList.size());
      for (int i = 45; i < ecgList.size() - 45; i++) {
        ArrayList<Float> sortList = new ArrayList<Float>(ecgList.subList(i - 45, i + 45));
        Collections.sort(sortList);
        float mid = sortList.get(sortList.size() / 2);
        float ecg = (ecgList.get(i) - mid) / 2;
        if (ecg > maxData) {
          maxData = ecg;
        }
        if (ecg < minData) {
          minData = ecg;
        }
        newDataList.add(ecg);
      }
      if (minData < 0) {
        maxData -= minData;
      }
      Float[] ecgData = new Float[newDataList.size()];
      newDataList.toArray(ecgData);
      data.setValue(ecgData);
      exgMaxR[index] = maxData;
      index ++;
    }
    bean.setEcgMaxR(exgMaxR);
    return bean;
  }
}
