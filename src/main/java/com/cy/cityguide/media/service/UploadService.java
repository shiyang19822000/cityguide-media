package com.cy.cityguide.media.service;

import com.cy.cityguide.media.exception.BadRequestBusinessException;
import com.cy.cityguide.media.parameter.FinishUploadParameter;
import com.cy.cityguide.media.result.FinishUploadResult;
import com.cy.cityguide.media.result.InitUploadResult;

public interface UploadService {

	InitUploadResult initiateMultipartUpload(String filePath, String contentType) throws BadRequestBusinessException;

	FinishUploadResult finishMultipartUpload(FinishUploadParameter parameter) throws BadRequestBusinessException;

	void abortMultipartUpload(String uploadId) throws BadRequestBusinessException;
	
	Integer getUploadMessage(String uploadId); 
	
	boolean deleteFile(String key);

}
