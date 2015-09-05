package com.cy.cityguide.media.dao;

import java.util.List;

import com.cy.cityguide.media.dao.parameter.CreateResourceTagParameter;

public interface ResourceTagDao {

	void create(CreateResourceTagParameter parameter);

	void deleteByResourceId(String resourceId);
	
	List<String> findTagIdsByResourceId(String resourceId);
}
