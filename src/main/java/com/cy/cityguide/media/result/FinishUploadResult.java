package com.cy.cityguide.media.result;

public class FinishUploadResult {
	
	private String resourceId;
	private String key;
	private Long fileSize;
	private String resourceUrl;
	
	public String getResourceId() {
		return resourceId;
	}
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	public Long getFileSize() {
		return fileSize;
	}
	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getResourceUrl() {
		return resourceUrl;
	}
	public void setResourceUrl(String resourceUrl) {
		this.resourceUrl = resourceUrl;
	}
	@Override
	public String toString() {
		return "FinishUploadResult [resourceId=" + resourceId + ", key=" + key + ", fileSize=" + fileSize
				+ ", resourceUrl=" + resourceUrl + "]";
	}
	
}
