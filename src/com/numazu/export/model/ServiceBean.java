package com.numazu.export.model;

import java.io.Serializable;

public class ServiceBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String serviceId;
	private String serviceType;
	private String serviceValue;
	private String serviceValue2;
	private String serviceValue3;
	private String serviceValue4;
	private String serviceValue5;
	
	public String getServiceType() {
		return serviceType;
	}
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	public String getServiceValue() {
		return serviceValue;
	}
	public void setServiceValue(String serviceValue) {
		this.serviceValue = serviceValue;
	}
	public String getServiceValue2() {
		return serviceValue2;
	}
	public void setServiceValue2(String serviceValue2) {
		this.serviceValue2 = serviceValue2;
	}
	public String getServiceValue3() {
		return serviceValue3;
	}
	public void setServiceValue3(String serviceValue3) {
		this.serviceValue3 = serviceValue3;
	}
	public String getServiceValue4() {
		return serviceValue4;
	}
	public void setServiceValue4(String serviceValue4) {
		this.serviceValue4 = serviceValue4;
	}
	public String getServiceValue5() {
		return serviceValue5;
	}
	public void setServiceValue5(String serviceValue5) {
		this.serviceValue5 = serviceValue5;
	}
	public String getServiceId() {
		return serviceId;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
}
