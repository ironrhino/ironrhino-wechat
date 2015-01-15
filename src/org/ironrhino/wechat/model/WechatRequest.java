package org.ironrhino.wechat.model;

import java.io.Serializable;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.beans.BeanWrapperImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

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

	public WechatRequest() {

	}

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
							bwi.setPropertyValue(StringUtils
									.uncapitalize(subTag.getTagName()), subTag
									.getTextContent());
						}
					}
					continue;
				} else if (name.equals("Location_X"))
					name = "Latitude";
				else if (name.equals("Location_Y"))
					name = "Longitude";
				String value = tag.getTextContent();
				bwi.setPropertyValue(StringUtils.uncapitalize(name), value);
			}
		}
	}

	public String getMessageBody() {
		return messageBody;
	}

	public void setMessageBody(String messageBody) {
		this.messageBody = messageBody;
	}

	public Long getMsgId() {
		return msgId;
	}

	public void setMsgId(Long msgId) {
		this.msgId = msgId;
	}

	public String getToUserName() {
		return toUserName;
	}

	public void setToUserName(String toUserName) {
		this.toUserName = toUserName;
	}

	public String getFromUserName() {
		return fromUserName;
	}

	public void setFromUserName(String fromUserName) {
		this.fromUserName = fromUserName;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public WechatRequestType getMsgType() {
		return msgType;
	}

	public void setMsgType(WechatRequestType msgType) {
		this.msgType = msgType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	public String getMediaId() {
		return mediaId;
	}

	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getRecognition() {
		return recognition;
	}

	public void setRecognition(String recognition) {
		this.recognition = recognition;
	}

	public String getThumbMediaId() {
		return thumbMediaId;
	}

	public void setThumbMediaId(String thumbMediaId) {
		this.thumbMediaId = thumbMediaId;
	}

	public Integer getScale() {
		return scale;
	}

	public void setScale(Integer scale) {
		this.scale = scale;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public WechatEventType getEvent() {
		return event;
	}

	public void setEvent(WechatEventType event) {
		this.event = event;
	}

	public String getEventKey() {
		return eventKey;
	}

	public void setEventKey(String eventKey) {
		this.eventKey = eventKey;
	}

	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	public String getScanType() {
		return scanType;
	}

	public void setScanType(String scanType) {
		this.scanType = scanType;
	}

	public String getScanResult() {
		return scanResult;
	}

	public void setScanResult(String scanResult) {
		this.scanResult = scanResult;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getPrecision() {
		return precision;
	}

	public void setPrecision(Double precision) {
		this.precision = precision;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
