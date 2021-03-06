package com.cy.cityguide.media.service;

import java.util.List;

import com.cy.cityguide.media.exception.BadRequestBusinessException;
import com.cy.cityguide.media.parameter.CreateNodeParameter;
import com.cy.cityguide.media.parameter.UpdateNodeParameter;
import com.cy.cityguide.media.result.Node;

public interface NodeService {

	void create(List<CreateNodeParameter> nodes) throws BadRequestBusinessException;

	void delete(List<String> ids) throws BadRequestBusinessException;

	void update(List<UpdateNodeParameter> nodes) throws BadRequestBusinessException;

	Node findNodeByParrmeter(String id, String path) throws BadRequestBusinessException;

	List<Node> findChildren(String id) throws BadRequestBusinessException;

}
