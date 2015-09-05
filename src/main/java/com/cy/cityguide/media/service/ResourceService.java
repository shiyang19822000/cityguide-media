package com.cy.cityguide.media.service;

import java.util.List;

import com.cy.cityguide.media.exception.BadRequestBusinessException;
import com.cy.cityguide.media.parameter.CreateResourceParameter;
import com.cy.cityguide.media.parameter.FindResourceParameter;
import com.cy.cityguide.media.parameter.UpdateResourceParameter;
import com.cy.cityguide.media.result.Resource;

public interface ResourceService {

	void create(List<CreateResourceParameter> resources)
			throws BadRequestBusinessException;

	void delete(List<String> ids) 
			throws BadRequestBusinessException;

	void update(List<UpdateResourceParameter> resources)
			throws BadRequestBusinessException;

	List<Resource> find(FindResourceParameter parameter)
			throws BadRequestBusinessException;
	
	Resource findResById(String id) 
			throws BadRequestBusinessException;

}
