package com.alibaba.ocean.rawsdk.example.param;

import java.util.Date;

public class ExampleCar {

	private Date builtDate;

	/**
	 * @return
	 */
	public Date getBuiltDate() {
		return builtDate;
	}

	/**
	 * 设置 * 参数示例：
	 * 
	 * <pre></pre>
	 * 
	 * 此参数必填
	 */
	public void setBuiltDate(Date builtDate) {
		this.builtDate = builtDate;
	}

	private Date boughtDate;

	/**
	 * @return
	 */
	public Date getBoughtDate() {
		return boughtDate;
	}

	/**
	 * 设置 * 参数示例：
	 * 
	 * <pre></pre>
	 * 
	 * 此参数必填
	 */
	public void setBoughtDate(Date boughtDate) {
		this.boughtDate = boughtDate;
	}

	private String name;

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置 * 参数示例：
	 * 
	 * <pre></pre>
	 * 
	 * 此参数必填
	 */
	public void setName(String name) {
		this.name = name;
	}

	private String builtArea;

	/**
	 * @return
	 */
	public String getBuiltArea() {
		return builtArea;
	}

	/**
	 * 设置 * 参数示例：
	 * 
	 * <pre></pre>
	 * 
	 * 此参数必填
	 */
	public void setBuiltArea(String builtArea) {
		this.builtArea = builtArea;
	}

	private String carNumber;

	/**
	 * @return
	 */
	public String getCarNumber() {
		return carNumber;
	}

	/**
	 * 设置 * 参数示例：
	 * 
	 * <pre></pre>
	 * 
	 * 此参数必填
	 */
	public void setCarNumber(String carNumber) {
		this.carNumber = carNumber;
	}

	private Double price;

	/**
	 * @return
	 */
	public Double getPrice() {
		return price;
	}

	/**
	 * 设置 * 参数示例：
	 * 
	 * <pre></pre>
	 * 
	 * 此参数必填
	 */
	public void setPrice(Double price) {
		this.price = price;
	}

	private Integer seats;

	/**
	 * @return
	 */
	public Integer getSeats() {
		return seats;
	}

	/**
	 * 设置 * 参数示例：
	 * 
	 * <pre></pre>
	 * 
	 * 此参数必填
	 */
	public void setSeats(Integer seats) {
		this.seats = seats;
	}

	@Override
	public String toString() {
		return "ExampleCar [builtDate=" + builtDate + ", boughtDate=" + boughtDate + ", name=" + name + ", builtArea=" + builtArea + ", carNumber=" + carNumber
				+ ", price=" + price + ", seats=" + seats + "]";
	}

	
}
