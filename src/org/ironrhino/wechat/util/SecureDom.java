package org.ironrhino.wechat.util;

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

public final class SecureDom {

	private static final Logger LOG = Logger.getLogger(SecureDom.class.getName());

	private static final String DISALLOW_DOCTYPE = "http://apache.org/xml/features/disallow-doctype-decl";
	private static final String EXTERNAL_GENERAL_ENTITIES = "http://xml.org/sax/features/external-general-entities";
	private static final String EXTERNAL_PARAMETER_ENTITIES = "http://xml.org/sax/features/external-parameter-entities";
	private static final String LOAD_EXTERNAL_DTD = "http://apache.org/xml/features/nonvalidating/load-external-dtd";

	private static final DocumentBuilderFactory FACTORY = createFactory();

	private SecureDom() {
	}

	private static DocumentBuilderFactory createFactory() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		setRequiredFeature(factory, DISALLOW_DOCTYPE, true);
		setRequiredFeature(factory, XMLConstants.FEATURE_SECURE_PROCESSING, true);
		factory.setXIncludeAware(false);
		factory.setNamespaceAware(true);
		trySetAttribute(factory, XMLConstants.ACCESS_EXTERNAL_DTD, "");
		trySetAttribute(factory, XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
		trySetFeature(factory, EXTERNAL_GENERAL_ENTITIES, false);
		trySetFeature(factory, EXTERNAL_PARAMETER_ENTITIES, false);
		trySetFeature(factory, LOAD_EXTERNAL_DTD, false);
		factory.setExpandEntityReferences(false);
		return factory;
	}

	private static void setRequiredFeature(DocumentBuilderFactory factory, String name, boolean value) {
		try {
			factory.setFeature(name, value);
		} catch (Exception e) {
			throw new IllegalStateException("DOM 主防线配置失败: " + name, e);
		}
	}

	private static void trySetFeature(DocumentBuilderFactory factory, String name, boolean value) {
		try {
			factory.setFeature(name, value);
		} catch (Exception e) {
			LOG.log(Level.FINE, "DOM 纵深特性不支持，已忽略: {0}", name);
		}
	}

	private static void trySetAttribute(DocumentBuilderFactory factory, String name, Object value) {
		try {
			factory.setAttribute(name, value);
		} catch (Exception e) {
			LOG.log(Level.FINE, "DOM 纵深属性不支持，已忽略: {0}", name);
		}
	}

	public static Document parse(InputStream input) {
		try {
			DocumentBuilder builder = FACTORY.newDocumentBuilder();
			return builder.parse(input);
		} catch (Exception e) {
			throw new IllegalStateException("DOM 安全解析失败", e);
		}
	}
}