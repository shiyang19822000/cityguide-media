package com.cy.cityguide.media.result;

public class InitUploadResult {
	
	private String uploadId;
	private int blockSize;
	private String authorization;
	private String contentType;
	private String bucketName;
	private String uploadPartUrl;
	
	public String getUploadId() {
		return uploadId;
	}
	public void setUploadId(String uploadId) {
		this.uploadId = uploadId;
	}
	public int getBlockSize() {
		return blockSize;
	}
	public void setBlockSize(int blockSize) {
		this.blockSize = blockSize;
	}
	
	public String getAuthorization() {
		return authorization;
	}
	public void setAuthorization(String authorization) {
		this.authorization = authorization;
	}

	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getUploadPartUrl() {
		return uploadPartUrl;
	}
	public void setUploadPartUrl(String uploadPartUrl) {
		this.uploadPartUrl = uploadPartUrl;
	}
	public String getBucketName() {
		return bucketName;
	}
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}
	@Override
	public String toString() {
		return "InitUploadResult [uploadId=" + uploadId + ", blockSize=" + blockSize + ", authorization="
				+ authorization + ", contentType=" + contentType + ", bucketName=" + bucketName + ", uploadPartUrl="
				+ uploadPartUrl + "]";
	}
	
}
