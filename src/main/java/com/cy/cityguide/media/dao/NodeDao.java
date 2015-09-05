package com.cy.cityguide.media.dao;

import java.util.List;

import com.cy.cityguide.media.parameter.CreateNodeParameter;
import com.cy.cityguide.media.parameter.UpdateNodeParameter;
import com.cy.cityguide.media.result.Node;

public interface NodeDao {

	void create(CreateNodeParameter node);

	void delete(String id);

	void update(UpdateNodeParameter node);

	Integer countChildren(String id);

	Node findById(String id);

	List<Node> findChildren(String id);
	
	List<Node> findByName(String name);
	
}
