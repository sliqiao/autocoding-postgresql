package com.autocoding.xml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * @Description:  JDK原生：Dom 方式解析XML
 * @author: QiaoLi
 * @date:   Jan 25, 2021 9:55:14 AM
 */
public class DomDemo {

	public static void node(NodeList list) {
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			NodeList childNodes = node.getChildNodes();
			for (int j = 0; j < childNodes.getLength(); j++) {
				if (childNodes.item(j).getNodeType() == Node.ELEMENT_NODE) {
					System.out.print(childNodes.item(j).getNodeName() + ":");
					System.out.println(childNodes.item(j).getFirstChild().getNodeValue());
				}
			}
			System.out.println();
		}
	}

	public static void main(String[] args) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse("src/test/resources/students.xml");
			NodeList nodeList = document.getElementsByTagName("student");
			DomDemo.node(nodeList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}