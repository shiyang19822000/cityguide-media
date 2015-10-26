package com.cy.cityguide.media.parameter;

import java.util.Date;

public class UploadMsgParameter {

	private String uploadId;
	private Integer status;
	private String initPath;
	private Integer type;
	private Date createTime;

	public String getUploadId() {
		return uploadId;
	}

	public void setUploadId(String uploadId) {
		this.uploadId = uploadId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public UploadMsgParameter(String uploadId, Integer status) {
		this.uploadId = uploadId;
		this.status = status;
	}

	public UploadMsgParameter() {
	}

	public String getInitPath() {
		return initPath;
	}

	public void setInitPath(String initPath) {
		this.initPath = initPath;
	}

	@Override
	public String toString() {
		return "UploadMsgParameter [uploadId=" + uploadId + ", status=" + status + ", initPath=" + initPath + ", type="
				+ type + ", createTime=" + createTime + "]";
	}

	

}
