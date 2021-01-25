package com.autocoding.xml;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SaxDemo {

	public static void main(String[] args) throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();
		MyHandler handler = new MyHandler();
		saxParser.parse("src/test/resources/students.xml", handler);
	}

	private static class MyHandler extends DefaultHandler {
		// 遍历xml文件开始标签
		@Override
		public void startDocument() throws SAXException {
			super.startDocument();
			System.out.println("sax解析开始");
		}

		// 遍历xml文件结束标签
		@Override
		public void endDocument() throws SAXException {
			super.endDocument();
			System.out.println("sax解析结束");
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes)
				throws SAXException {
			super.startElement(uri, localName, qName, attributes);
			if (qName.equals("student")) {
				System.err.println("============开始遍历student=============");
			} else if (!qName.equals("student") && !qName.equals("students")) {
				log.info("uri:{},localName:{},qName:{}", uri, localName, qName);
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			super.endElement(uri, localName, qName);
			if (qName.equals("student")) {
				log.info("uri:{},localName:{},qName:{}", uri, localName, qName);
				System.err.println("============结束遍历student=============");
			}
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			super.characters(ch, start, length);
			String value = new String(ch, start, length).trim();
			if (!value.equals("")) {
				System.out.println(value);
			}
		}
	}
}
