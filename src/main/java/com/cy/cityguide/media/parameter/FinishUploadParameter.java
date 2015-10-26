package com.cy.cityguide.media.parameter;

import java.util.List;

public class FinishUploadParameter {
	
	private String uploadId;
	private List<String> etags;
	
	public String getUploadId() {
		return uploadId;
	}
	public void setUploadId(String uploadId) {
		this.uploadId = uploadId;
	}
	
	public List<String> getEtags() {
		return etags;
	}
	public void setEtags(List<String> etags) {
		this.etags = etags;
	}
	
	@Override
	public String toString() {
		return "FinishUploadParameter [uploadId=" + uploadId + ", etags=" + etags + "]";
	}
	
}
