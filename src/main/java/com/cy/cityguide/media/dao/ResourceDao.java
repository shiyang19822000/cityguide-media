package com.cy.cityguide.media.dao;

import java.util.List;

import com.cy.cityguide.media.parameter.CreateResourceParameter;
import com.cy.cityguide.media.parameter.FindResourceParameter;
import com.cy.cityguide.media.parameter.UpdateResourceParameter;
import com.cy.cityguide.media.result.Resource;

public interface ResourceDao {

	void create(CreateResourceParameter resource);

	void delete(Long id);

	void update(UpdateResourceParameter resource);

	Integer countByNodeId(Long nodeId);

	List<Resource> find(FindResourceParameter parameter);

}
