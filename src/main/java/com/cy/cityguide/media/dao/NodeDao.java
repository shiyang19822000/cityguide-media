package com.cy.cityguide.media.dao;

import java.util.List;

import com.cy.cityguide.media.dao.parameter.FindNodeByParentIdAndNameParameter;
import com.cy.cityguide.media.parameter.CreateNodeParameter;
import com.cy.cityguide.media.parameter.UpdateNodeParameter;
import com.cy.cityguide.media.result.Node;

public interface NodeDao {

	void create(CreateNodeParameter node);

	void delete(Long id);

	void update(UpdateNodeParameter node);

	Integer countChildren(Long id);

	Node findById(Long id);

	Node findByParentIdAndName(FindNodeByParentIdAndNameParameter parameter);

	List<Node> findChildren(Long id);
	
	List<Node> findByName(String name);
	
}
