package com.cy.cityguide.media.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cy.cityguide.media.exception.BadRequestBusinessException;
import com.cy.cityguide.media.parameter.CreateResourceParameter;
import com.cy.cityguide.media.parameter.FindResourceParameter;
import com.cy.cityguide.media.parameter.UpdateResourceParameter;
import com.cy.cityguide.media.result.Resource;
import com.cy.cityguide.media.service.ResourceService;

@Controller
public class ResourceController {

	@Autowired
	private ResourceService resourceService;

	@RequestMapping(value = "resource", method = RequestMethod.POST)
	public void create(@RequestBody List<CreateResourceParameter> resources)
			throws BadRequestBusinessException {
		resourceService.create(resources);
	}

	@RequestMapping(value = "resource", method = RequestMethod.DELETE)
	public void delete(@RequestBody List<Long> ids)
			throws BadRequestBusinessException {
		resourceService.delete(ids);
	}

	@RequestMapping(value = "resource", method = RequestMethod.PUT)
	public void update(@RequestBody List<UpdateResourceParameter> resources)
			throws BadRequestBusinessException {
		resourceService.update(resources);
	}

	@RequestMapping(value = "resource", method = RequestMethod.GET)
	public @ResponseBody List<Resource> find(@RequestBody FindResourceParameter parameter)
			throws BadRequestBusinessException {
		return resourceService.find(parameter);
	}

}
