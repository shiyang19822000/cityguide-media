package com.cy.cityguide.media.dao;

import com.cy.cityguide.media.dao.parameter.CreateResourceTagParameter;

public interface ResourceTagDao {

	void create(CreateResourceTagParameter parameter);

	void deleteByResourceId(Long resourceId);

}
