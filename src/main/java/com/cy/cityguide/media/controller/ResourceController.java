package com.cy.cityguide.media.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cy.cityguide.media.exception.BadRequestBusinessException;
import com.cy.cityguide.media.parameter.CreateResourceParameter;
import com.cy.cityguide.media.parameter.FindResourceParameter;
import com.cy.cityguide.media.parameter.FinishUploadParameter;
import com.cy.cityguide.media.parameter.UpdateResourceParameter;
import com.cy.cityguide.media.result.FinishUploadResult;
import com.cy.cityguide.media.result.InitUploadResult;
import com.cy.cityguide.media.result.Resource;
import com.cy.cityguide.media.service.ResourceService;
import com.cy.cityguide.media.service.UploadService;

@Controller
@RequestMapping(value = "/v1/media-service/")
public class ResourceController {

	@Autowired
	private ResourceService resourceService;
	@Autowired
	private UploadService uploadService;

	@ResponseBody
	@RequestMapping(value = "resource", method = RequestMethod.POST)
	public void create(@RequestBody List<CreateResourceParameter> resources) throws BadRequestBusinessException {
		resourceService.create(resources);
	}

	@ResponseBody
	@RequestMapping(value = "resource", method = RequestMethod.DELETE)
	public void delete(@RequestBody List<String> ids) throws BadRequestBusinessException {
		resourceService.delete(ids);
	}

	@ResponseBody
	@RequestMapping(value = "resource", method = RequestMethod.PUT)
	public void update(@RequestBody List<UpdateResourceParameter> resources) throws BadRequestBusinessException {
		resourceService.update(resources);
	}

	@RequestMapping(value = "resource", method = RequestMethod.GET)
	public @ResponseBody List<Resource> find(@RequestParam String nodeId, @RequestParam Integer type,
			@RequestParam String name, @RequestParam String tag,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startTime,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endTime, @RequestParam String offset,
			@RequestParam String limit) throws BadRequestBusinessException {
		FindResourceParameter parameter = new FindResourceParameter(nodeId, type, name, tag, startTime, endTime,
				offset);
		List<Resource> res = resourceService.find(parameter, limit);
		return res;
	}

	@RequestMapping(value = "res", method = RequestMethod.GET)
	public @ResponseBody Resource findSingleResource(@RequestParam(value = "res-id") String id)
			throws BadRequestBusinessException {
		Resource resource = resourceService.findResById(id);
		return resource;
	}

	@RequestMapping(value = "res-upload", method = RequestMethod.GET)
	public @ResponseBody InitUploadResult initUpload(@RequestParam String path, @RequestParam String contenttype)
			throws BadRequestBusinessException {
		InitUploadResult init = uploadService.initiateMultipartUpload(path, contenttype);
		return init;
	}

	@RequestMapping(value = "res-upload", method = RequestMethod.POST)
	public @ResponseBody FinishUploadResult finishUpload(@RequestBody FinishUploadParameter finishUploadParameters)
			throws BadRequestBusinessException {
		FinishUploadResult result = uploadService.finishMultipartUpload(finishUploadParameters);
		return result;
	}

	@RequestMapping(value = "res-upload", method = RequestMethod.DELETE)
	@ResponseBody
	public void AbortUpload(@RequestParam String uploadId) throws BadRequestBusinessException {
		uploadService.abortMultipartUpload(uploadId);
	}

	@RequestMapping(value = "res-upload-status", method = RequestMethod.GET)
	public @ResponseBody Integer getUploadStatus(@RequestParam String uploadId) {
		return uploadService.getUploadMessage(uploadId);
	}

}
