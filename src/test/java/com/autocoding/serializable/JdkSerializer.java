package com.autocoding.serializable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class JdkSerializer {

	public static void serialize(Student student) throws FileNotFoundException, IOException {
		ObjectOutputStream oo = new ObjectOutputStream(new FileOutputStream(new File("D:\\student.txt")));
		oo.writeObject(student);
		oo.close();
	}

	public static Student deserialize() throws IOException, Exception {
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File("D:\\student.txt")));
		Student student = (Student) ois.readObject();
		return student;
	}

	public static void main(String[] args) {
		Student student = new Student();
		student.setId("1000");
		student.setName("李桥");
		try {
			// 序列化
			JdkSerializer.serialize(student);
			// 反序列化
			Student serializeStudent = JdkSerializer.deserialize();
			System.out.println(serializeStudent.getName());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static class Student implements Serializable {

		private static final long serialVersionUID = 8888L;

		private String id;

		private String name;

		private Student() {
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
}