package com.my.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class Dog {
	public Dog() {
	}

	String name;
	int age;
	
	//@DateTimeFormat(pattern="yyyy-MM-dd HH:mm") // spring的注解
	@JsonFormat(pattern="yyyy-MM-dd HH",timezone = "GMT+8")  // 由于使用jackson的json和xml，所以都会生效
	Date birth;
	
	

	//@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	//@JsonFormat(pattern="yyyy-MM-dd HH",timezone = "GMT+8")  
	public Date getBirth() {
		return birth;
	}

	public void setBirth(Date birth) {
		this.birth = birth;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	@Override
	public String toString() {
		return "Dog [name=" + name + ", age=" + age + ", birth=" + birth + "]";
	}
	

}