package com.autocoding.xml;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * 
 *  Dom4J 解析XML
 * @author: QiaoLi
 * @date:   Jan 25, 2021 10:36:23 AM
 */
public class Dom4JDemo {
	public static void main(String[] args) throws Exception {
		SAXReader reader = new SAXReader();
		Document document = reader.read(new File("src/test/resources/students.xml"));
		Element rootElement = document.getRootElement();
		Iterator<Element> iterator = rootElement.elementIterator();
		while (iterator.hasNext()) {
			Element stu = iterator.next();
			List<Attribute> attributes = stu.attributes();
			for (Attribute attribute : attributes) {
				System.err.println(attribute.getName()+":"+attribute.getValue());
			}
			System.out.println("======遍历子节点======");
			Iterator<Element> stuChildren = stu.elementIterator();
			while (stuChildren.hasNext()) {
				Element stuChild = stuChildren.next();
				System.out.println("节点名：" + stuChild.getName() + "---节点值：" + stuChild.getStringValue());
			}
			System.out.println();
		}
	}
}