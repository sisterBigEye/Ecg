package com.bl.open.library_data;

public class EcgInfo {

  public Float[][] ecgDataArray;

  public float[] ecgMaxR;

  public int ecgLeadNum;

  private EcgInfo(Float[][] ecgDataArray, float[] ecgMaxR, int ecgLeadNum) {
    this.ecgDataArray = ecgDataArray;
    this.ecgMaxR = ecgMaxR;
    this.ecgLeadNum = ecgLeadNum;
  }

  public static class Builder {

    private Float[][] data;

    private float[] maxR;

    private int leadNum;

    public Builder setData(Float[]... data) {
      this.data = data;
      return this;
    }

    public Builder setRArray(float... maxR) {
      this.maxR = maxR;
      return this;
    }

    public Builder setLeadNum(int leadNum) {
      this.leadNum = leadNum;
      return this;
    }

    public EcgInfo build() {
      return new EcgInfo(data, maxR, leadNum);
    }
  }

  public boolean haveData() {
    if(ecgDataArray != null && ecgDataArray.length > 0) {
      return true;
    }
    return false;
  }

}
