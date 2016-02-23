package com.micron.vmware.cim.client.model;

public class SMARTAttribute {
	private Integer           smartAttrId;
	private String            smartAttrDesc;
	private Integer           smartAttrValue; //CIM API returns UnsignedInteger32
	private Long              smartAttrRawData; //CIM API returns UnsignedInteger64
	private Integer           smartAttrWorst; //CIM API returns UnsignedInteger32
	private Integer smartAttrThreshold; //CIM API returns UnsignedInteger32
	private String  smartAttrUnits;

	public SMARTAttribute(Integer smartAttrId, String smartAttrDesc, Integer smartAttrValue,
			Long smartAttrRawData, Integer smartAttrWorst, Integer smartAttrThreshold,
			String smartAttrUnits) {
		this.smartAttrId = smartAttrId;
		this.smartAttrDesc = smartAttrDesc;
		this.smartAttrValue = smartAttrValue;
		this.smartAttrRawData = smartAttrRawData;
		this.smartAttrWorst = smartAttrWorst;
		this.smartAttrThreshold = smartAttrThreshold;
		this.smartAttrUnits = smartAttrUnits;
	}

	public Integer getSmartAttrId() {
		return smartAttrId;
	}

	public void setSmartAttrId(Integer smartAttrId) {
		this.smartAttrId = smartAttrId;
	}

	public String getSmartAttrDesc() {
		return smartAttrDesc;
	}

	public void setSmartAttrDesc(String smartAttrDesc) {
		this.smartAttrDesc = smartAttrDesc;
	}

	public Integer getSmartAttrValue() {
		return smartAttrValue;
	}

	public void setSmartAttrValue(Integer smartAttrValue) {
		this.smartAttrValue = smartAttrValue;
	}

	public Long getSmartAttrRawData() {
		return smartAttrRawData;
	}

	public void setSmartAttrRawData(Long smartAttrRawData) {
		this.smartAttrRawData = smartAttrRawData;
	}

	public Integer getSmartAttrWorst() {
		return smartAttrWorst;
	}

	public void setSmartAttrWorst(Integer smartAttrWorst) {
		this.smartAttrWorst = smartAttrWorst;
	}

	public Integer getSmartAttrThreshold() {
		return smartAttrThreshold;
	}

	public void setSmartAttrThreshold(Integer smartAttrThreshold) {
		this.smartAttrThreshold = smartAttrThreshold;
	}

	public String getSmartAttrUnits() {
		return smartAttrUnits;
	}

	public void setSmartAttrUnits(String smartAttrUnits) {
		this.smartAttrUnits = smartAttrUnits;
	}
}
