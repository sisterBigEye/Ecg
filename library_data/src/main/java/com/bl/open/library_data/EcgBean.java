package com.bl.open.library_data;

import java.util.Arrays;
import java.util.List;

public class EcgBean {

  private String patientName;

  private String patientAge;

  private String patientSex;

  private String[] leadNameList;

  private int leadNum;

  private List<Data> waveData;

  public float[] ecgMaxR;

  public static class Data {

    private String code;

    private Float[] value;

    public String getCode() {
      return code;
    }

    public void setCode(String code) {
      this.code = code;
    }

    public Float[] getValue() {
      return value;
    }

    public void setValue(Float[] value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return "Data{" +
              "code='" + code + '\'' +
              ", value=" + Arrays.toString(value) +
              '}';
    }
  }

  public String getPatientName() {
    return patientName;
  }

  public void setPatientName(String patientName) {
    this.patientName = patientName;
  }

  public String getPatientAge() {
    return patientAge;
  }

  public void setPatientAge(String patientAge) {
    this.patientAge = patientAge;
  }

  public String getPatientSex() {
    return patientSex;
  }

  public void setPatientSex(String patientSex) {
    this.patientSex = patientSex;
  }

  public String[] getLeadNameList() {
    return leadNameList;
  }

  public void setLeadNameList(String[] leadNameList) {
    this.leadNameList = leadNameList;
  }

  public int getLeadNum() {
    return leadNum;
  }

  public void setLeadNum(int leadNum) {
    this.leadNum = leadNum;
  }

  public List<Data> getWaveData() {
    return waveData;
  }

  public void setWaveData(List<Data> waveData) {
    this.waveData = waveData;
  }

  public float[] getEcgMaxR() {
    return ecgMaxR;
  }

  public void setEcgMaxR(float[] ecgMaxR) {
    this.ecgMaxR = ecgMaxR;
  }

  public boolean haveData() {
    return waveData != null && waveData.size() > 0 && waveData.get(0).value != null;
  }

  @Override
  public String toString() {
    return "EcgBean{" +
            "patientName='" + patientName + '\'' +
            ", patientAge='" + patientAge + '\'' +
            ", patientSex='" + patientSex + '\'' +
            ", leadNameList=" + Arrays.toString(leadNameList) +
            ", leadNum=" + leadNum +
            ", waveData=" + waveData +
            ", ecgMaxR=" + Arrays.toString(ecgMaxR) +
            '}';
  }
}
