package com.cy.cityguide.media.parameter;

import java.util.Date;

public class FindResourceParameter {

	private String nodeId;

	private Integer type;

	private String name;

	private String tag;

	private Date startTime;

	private Date endTime;

	private String offset;

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}




	public String getOffset() {
		return offset;
	}

	public void setOffset(String offset) {
		this.offset = offset;
	}

	public FindResourceParameter(String nodeId, Integer type, String name, String tag, Date startTime, Date endTime,
		String offset) {
		this.nodeId = nodeId;
		this.type = type;
		this.name = name;
		this.tag = tag;
		this.startTime = startTime;
		this.endTime = endTime;
		this.offset = offset;
	}

	public FindResourceParameter() {
	}

	@Override
	public String toString() {
		return "FindResourceParameter [nodeId=" + nodeId + ", type=" + type + ", name=" + name + ", tag=" + tag
				+ ", startTime=" + startTime + ", endTime=" + endTime + ", offset=" + offset + "]";
	}
	
}
