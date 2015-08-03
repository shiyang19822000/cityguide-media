package com.cy.cityguide.media.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cy.cityguide.media.exception.BadRequestBusinessException;
import com.cy.cityguide.media.parameter.CreateNodeParameter;
import com.cy.cityguide.media.parameter.UpdateNodeParameter;
import com.cy.cityguide.media.result.Node;
import com.cy.cityguide.media.service.NodeService;

@Controller
public class NodeController {

	@Autowired
	private NodeService nodeService;

	@RequestMapping(value = "node", method = RequestMethod.POST)
	public void create(@RequestBody List<CreateNodeParameter> nodes)
			throws BadRequestBusinessException {
		nodeService.create(nodes);
	}

	@RequestMapping(value = "node", method = RequestMethod.DELETE)
	public void delete(@RequestBody List<Long> ids)
			throws BadRequestBusinessException {
		nodeService.delete(ids);
	}

	@RequestMapping(value = "node", method = RequestMethod.PUT)
	public void update(@RequestBody List<UpdateNodeParameter> nodes)
			throws BadRequestBusinessException {
		nodeService.update(nodes);
	}

	@RequestMapping(value = "node/{id}/children", method = RequestMethod.GET)
	public @ResponseBody List<Node> findChildren(@PathVariable(value = "id") Long id)
			throws BadRequestBusinessException {
		return nodeService.findChildren(id);
	}

}
