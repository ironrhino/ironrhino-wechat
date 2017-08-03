package org.ironrhino.wechat.model;

import java.io.Serializable;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.NotWritablePropertyException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WechatRequest implements Serializable {

	private static final long serialVersionUID = -7515159900178523381L;

	private String messageBody;
	private Long msgId;
	private String toUserName;
	private String fromUserName;
	private long createTime;
	private WechatRequestType msgType;
	private String content;
	private String picUrl;
	private String mediaId;
	private String format;
	private String recognition;
	private String thumbMediaId;
	private Integer scale;
	private String label;
	private WechatEventType event;
	private String eventKey;
	private String ticket;
	private String scanType;
	private String scanResult;
	private Double latitude;
	private Double longitude;
	private Double precision;
	private String status;

	public WechatRequest(String xml) {
		this.messageBody = xml;
		BeanWrapperImpl bwi = new BeanWrapperImpl(this);
		Document doc;
		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.parse(new InputSource(new StringReader(xml)));
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		Element element = doc.getDocumentElement();
		NodeList nl = element.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node node = nl.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element tag = (Element) node;
				String name = tag.getTagName();
				if (name.equals("ScanCodeInfo")) {
					NodeList subNodeList = tag.getChildNodes();
					for (int j = 0; j < subNodeList.getLength(); j++) {
						Node subNode = subNodeList.item(j);
						if (subNode.getNodeType() == Node.ELEMENT_NODE) {
							Element subTag = (Element) subNode;
							try {
								bwi.setPropertyValue(StringUtils.uncapitalize(subTag.getTagName()),
										subTag.getTextContent());
							} catch (NotWritablePropertyException e) {
								// ignore
							}
						}
					}
					continue;
				} else if (name.equals("Location_X"))
					name = "Latitude";
				else if (name.equals("Location_Y"))
					name = "Longitude";
				String value = tag.getTextContent();
				try {
					bwi.setPropertyValue(StringUtils.uncapitalize(name), value);
				} catch (NotWritablePropertyException e) {
					// ignore
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
