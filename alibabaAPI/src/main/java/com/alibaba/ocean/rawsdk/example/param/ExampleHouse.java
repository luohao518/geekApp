package com.alibaba.ocean.rawsdk.example.param;

public class ExampleHouse {

	private String location;

	/**
	 * @return
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * 设置 * 参数示例：
	 * 
	 * <pre></pre>
	 * 
	 * 此参数必填
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	private Integer areaSize;

	/**
	 * @return
	 */
	public Integer getAreaSize() {
		return areaSize;
	}

	/**
	 * 设置 * 参数示例：
	 * 
	 * <pre></pre>
	 * 
	 * 此参数必填
	 */
	public void setAreaSize(Integer areaSize) {
		this.areaSize = areaSize;
	}

	private Boolean rent;

	/**
	 * @return
	 */
	public Boolean getRent() {
		return rent;
	}

	/**
	 * 设置 * 参数示例：
	 * 
	 * <pre></pre>
	 * 
	 * 此参数必填
	 */
	public void setRent(Boolean rent) {
		this.rent = rent;
	}

	private Integer rooms;

	/**
	 * @return
	 */
	public Integer getRooms() {
		return rooms;
	}

	/**
	 * 设置 * 参数示例：
	 * 
	 * <pre></pre>
	 * 
	 * 此参数必填
	 */
	public void setRooms(Integer rooms) {
		this.rooms = rooms;
	}

	@Override
	public String toString() {
		return "ExampleHouse [location=" + location + ", areaSize=" + areaSize + ", rent=" + rent + ", rooms=" + rooms + "]";
	}

}
