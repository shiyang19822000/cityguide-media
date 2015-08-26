package com.cy.cityguide.media.result;

import java.util.Date;

public class Resource {

	private Long id;

	private Integer type;

	private String name;

	private String keyWord;

	private Date createTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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


	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Override
	public String toString() {
		return "Resource [id=" + id + ", type=" + type + ", name=" + name + ", keyWord=" + keyWord + ", createTime="
				+ createTime + "]";
	}
	

}
