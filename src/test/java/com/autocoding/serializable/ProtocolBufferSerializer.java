package com.autocoding.serializable;

import java.io.Serializable;
import java.util.Arrays;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

public class ProtocolBufferSerializer {

	public static <T> byte[] serialize(T t, Class<T> clazz) {
		return ProtobufIOUtil.toByteArray(t, RuntimeSchema.createFrom(clazz),
				LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
	}

	public static <T> T deserialize(byte[] data, Class<T> clazz) {
		RuntimeSchema<T> runtimeSchema = RuntimeSchema.createFrom(clazz);
		T t = runtimeSchema.newMessage();
		ProtobufIOUtil.mergeFrom(data, t, runtimeSchema);
		return t;
	}

	public static void main(String[] args) {
		Student student = new Student();
		student.setId("1000");
		student.setName("李桥");
		try {
			// 序列化
			byte[] bytes = ProtocolBufferSerializer.serialize(student, Student.class);
			System.out.println("序列化之后：" + Arrays.toString(bytes));
			// 反序列化
			Student serializeStudent = ProtocolBufferSerializer.deserialize(bytes, Student.class);
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