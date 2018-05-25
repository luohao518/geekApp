package com.alibaba.ocean.rawsdk.example.param;

import java.util.Arrays;


public class ExampleFamily {

	private Integer familyNumber;

	/**
	 * @return 家庭编号
	 */
	public Integer getFamilyNumber() {
		return familyNumber;
	}

	/**
	 * 设置家庭编号 * 参数示例：
	 * 
	 * <pre></pre>
	 */
	public void setFamilyNumber(Integer familyNumber) {
		this.familyNumber = familyNumber;
	}

	private ExamplePerson father;

	/**
	 * @return 父亲对象，可以为空
	 */
	public ExamplePerson getFather() {
		return father;
	}

	/**
	 * 设置父亲对象，可以为空 * 参数示例：
	 * 
	 * <pre></pre>
	 * 
	 * 此参数必填
	 */
	public void setFather(ExamplePerson father) {
		this.father = father;
	}

	private ExamplePerson mother;

	/**
	 * @return 母亲对象，可以为空
	 */
	public ExamplePerson getMother() {
		return mother;
	}

	/**
	 * 设置母亲对象，可以为空 * 参数示例：
	 * 
	 * <pre></pre>
	 * 
	 * 此参数必填
	 */
	public void setMother(ExamplePerson mother) {
		this.mother = mother;
	}

	private ExamplePerson[] children;

	/**
	 * @return 孩子列表
	 */
	public ExamplePerson[] getChildren() {
		return children;
	}

	/**
	 * 设置孩子列表 * 参数示例：
	 * 
	 * <pre></pre>
	 * 
	 * 此参数必填
	 */
	public void setChildren(ExamplePerson[] children) {
		this.children = children;
	}

	private ExampleCar[] ownedCars;

	/**
	 * @return 拥有的汽车信息
	 */
	public ExampleCar[] getOwnedCars() {
		return ownedCars;
	}

	/**
	 * 设置拥有的汽车信息 * 参数示例：
	 * 
	 * <pre></pre>
	 * 
	 * 此参数必填
	 */
	public void setOwnedCars(ExampleCar[] ownedCars) {
		this.ownedCars = ownedCars;
	}

	private ExampleHouse myHouse;

	/**
	 * @return 所住的房屋信息
	 */
	public ExampleHouse getMyHouse() {
		return myHouse;
	}

	/**
	 * 设置所住的房屋信息 * 参数示例：
	 * 
	 * <pre></pre>
	 * 
	 * 此参数必填
	 */
	public void setMyHouse(ExampleHouse myHouse) {
		this.myHouse = myHouse;
	}

	@Override
	public String toString() {
		return "ExampleFamily [familyNumber=" + familyNumber + ", father=" + father + ", mother=" + mother + ", children=" + Arrays.toString(children)
				+ ", ownedCars=" + Arrays.toString(ownedCars) + ", myHouse=" + myHouse + "]";
	}

}
