package com.cy.cityguide.media.controller;

import java.util.List;

import javax.websocket.server.PathParam;

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
@RequestMapping(value = "/v1/media-service/")
public class NodeController {

	@Autowired
	private NodeService nodeService;

	@RequestMapping(value = "node", method = RequestMethod.POST)
	@ResponseBody
	public void create(@RequestBody List<CreateNodeParameter> nodes) throws BadRequestBusinessException {
		nodeService.create(nodes);
	}

	@ResponseBody
	@RequestMapping(value = "node", method = RequestMethod.DELETE)
	public void delete(@RequestBody List<String> ids) throws BadRequestBusinessException {
		nodeService.delete(ids);
	}

	@ResponseBody
	@RequestMapping(value = "node", method = RequestMethod.PUT)
	public void update(@RequestBody List<UpdateNodeParameter> nodes) throws BadRequestBusinessException {
		nodeService.update(nodes);
	}

	@RequestMapping(value = "node", method = RequestMethod.GET)
	public @ResponseBody Node findNodeByParrmeter(@PathParam(value = "id") String id,
			@PathParam(value = "path") String path) throws BadRequestBusinessException {
		Node node = nodeService.findNodeByParrmeter(id, path);
		return node;
	}

	@RequestMapping(value = "node/{id}/children", method = RequestMethod.GET)
	public @ResponseBody List<Node> findChildren(@PathVariable(value = "id") String id)
			throws BadRequestBusinessException {
		return nodeService.findChildren(id);
	}

}
