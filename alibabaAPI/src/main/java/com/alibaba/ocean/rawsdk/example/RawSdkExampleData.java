/**
 * 
 */
package com.alibaba.ocean.rawsdk.example;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.alibaba.ocean.rawsdk.example.param.ExampleCar;
import com.alibaba.ocean.rawsdk.example.param.ExampleFamily;
import com.alibaba.ocean.rawsdk.example.param.ExampleHouse;
import com.alibaba.ocean.rawsdk.example.param.ExamplePerson;

/**
 * @author hongbang.hb
 *
 */
public class RawSdkExampleData {

	public static final String DATE_FORMAT_STR = "yyyy-MM-dd HH:mm:ss";
	public static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT_STR);

	public static ExampleFamily createFirstFamily() {
		ExampleFamily family = new ExampleFamily();

		ExamplePerson father = new ExamplePerson();
		father.setAge(38);
		father.setBirthday(createDate("1978-03-12 10:10:10"));
		father.setMobileNumber("27088888888");
		father.setName("张进则");
		family.setFather(father);

		ExamplePerson mother = new ExamplePerson();
		mother.setAge(33);
		mother.setBirthday(createDate("1983-06-17 10:10:10"));
		mother.setMobileNumber("27099999999");
		mother.setName("周煜清");
		family.setMother(mother);

		ExamplePerson xiaoming = new ExamplePerson();
		xiaoming.setBirthday(createDate("2010-10-17 10:10:10"));
		xiaoming.setName("张小明");

		ExamplePerson xiaogang = new ExamplePerson();
		xiaogang.setBirthday(createDate("2010-10-17 10:20:10"));
		xiaogang.setName("张小刚");
		ExamplePerson[] children = new ExamplePerson[] { xiaoming, xiaogang };
		family.setChildren(children);

		family.setFamilyNumber(1);

		family.setMyHouse(createHouse("中国上海市浦东新区", false));

		ExampleCar[] ownedCars = new ExampleCar[] { createCar("BMW", 370000.12), createCar("Ford", 150000.27) };
		family.setOwnedCars(ownedCars);
		return family;
	}

	public static ExampleFamily createSecondFamily() {
		ExampleFamily family = new ExampleFamily();

		ExamplePerson mother = new ExamplePerson();
		mother.setAge(33);
		mother.setBirthday(createDate("1983-06-17 10:10:10"));
		mother.setMobileNumber("27099999999");
		mother.setName("叶涵");
		family.setMother(mother);

		ExamplePerson xiaoming = new ExamplePerson();
		xiaoming.setBirthday(createDate("2010-10-17 10:10:10"));
		xiaoming.setName("叶子涵");
		xiaoming.setAge(6);

		ExamplePerson[] children = new ExamplePerson[] { xiaoming };
		family.setChildren(children);

		family.setFamilyNumber(2);

		family.setMyHouse(createHouse("中国浙江省杭州市滨江区", null));

		ExampleCar[] ownedCars = new ExampleCar[] { createCar("XXXX", 1370000.12) };
		family.setOwnedCars(ownedCars);
		return family;
	}

	public static ExampleCar createCar(String name, Double price) {
		ExampleCar car = new ExampleCar();
		car.setBoughtDate(createDate("2008-10-17 10:20:10"));
		car.setBuiltArea("中国");
		car.setBuiltDate(createDate("2007-10-17 10:20:10"));
		car.setCarNumber("HZ-H-9875");
		car.setName(name);
		car.setPrice(price);
		car.setSeats(4);
		return car;
	}

	public static ExampleHouse createHouse(String address, Boolean rent) {
		ExampleHouse house = new ExampleHouse();
		house.setAreaSize(120);
		house.setLocation(address);
		house.setRent(rent);
		return house;
	}

	public static Date createDate(String input) {
		try {
			return simpleDateFormat.parse(input);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] getPicture(String fileName) {
		URL fatherURL = RawSdkExampleData.class.getClassLoader().getResource(fileName);
		try {
			InputStream inputStream = fatherURL.openStream();
			byte[] bs = new byte[1024 * 1024];
			int readCount = inputStream.read(bs);
			byte[] content = new byte[readCount];
			System.arraycopy(bs, 0, content, 0, readCount);
			return content;
		} catch (IOException e) {
		}
		return null;
	}
}
