package com.cy.cityguide.media.parameter;

import java.util.List;

public class CreateResourceParameter {

	private Long id;

	private Long nodeId;

	private Integer type;

	private String name;

	private String keyWord;

	private List<String> tags;
	

	public Long getNodeId() {
		return nodeId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setNodeId(Long nodeId) {
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

	public String getKeyWord() {
		return keyWord;
	}

	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}


	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

}
