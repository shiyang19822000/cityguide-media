package com.cy.cityguide.media.dao;

import java.util.List;

import com.cy.cityguide.media.parameter.CreateResourceParameter;
import com.cy.cityguide.media.parameter.FindResourceParameter;
import com.cy.cityguide.media.parameter.UpdateResourceParameter;
import com.cy.cityguide.media.parameter.UploadMsgParameter;
import com.cy.cityguide.media.result.Resource;
import com.cy.cityguide.media.result.UploadMsgResult;

public interface ResourceDao {

	void create(CreateResourceParameter resource);

	void delete(String id);

	void update(UpdateResourceParameter resource);

	Integer countByNodeId(String nodeId);

	List<Resource> find(FindResourceParameter parameter,int limit);
	
	public Resource findResById(String id);
	
	void createUploadMsg(UploadMsgParameter uploadMsg);
	
	UploadMsgResult getUploadMsg(String uploadId);
	
	void modifyUploadMsgStatus(UploadMsgParameter parameter);
}
