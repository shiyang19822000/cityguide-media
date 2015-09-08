package com.cy.cityguide.media.service.impl;

import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.cy.cityguide.media.constant.Constant.ResourceType;
import com.cy.cityguide.media.dao.NodeDao;
import com.cy.cityguide.media.dao.ResourceDao;
import com.cy.cityguide.media.dao.ResourceTagDao;
import com.cy.cityguide.media.dao.TagDao;
import com.cy.cityguide.media.dao.parameter.CreateResourceTagParameter;
import com.cy.cityguide.media.exception.BadRequestBusinessException;
import com.cy.cityguide.media.parameter.CreateResourceParameter;
import com.cy.cityguide.media.parameter.CreateTagParameter;
import com.cy.cityguide.media.parameter.FindResourceParameter;
import com.cy.cityguide.media.parameter.UpdateResourceParameter;
import com.cy.cityguide.media.result.Resource;
import com.cy.cityguide.media.result.Tag;
import com.cy.cityguide.media.service.ResourceService;
import com.cychina.platform.id.IdGenerator;

@Service
@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
public class ResourceServiceImpl implements ResourceService {

	private Logger logger = Logger.getLogger(ResourceServiceImpl.class);

	@Autowired
	private NodeDao nodeDao;

	@Autowired
	private ResourceDao resourceDao;

	@Autowired
	private TagDao tagDao;

	@Autowired
	private ResourceTagDao resourceTagDao;
	
	@Autowired
	private IdGenerator idGenerator;

	private void validateCreateOrUpdateParameter(CreateResourceParameter resource) throws BadRequestBusinessException {
		String nodeId = resource.getNodeId();
		if (nodeId == null || nodeId.trim().equals("")) {
			throw new BadRequestBusinessException("失败," + JSON.toJSONString(resource) + ",nodeId为空");
		}
		if (nodeDao.findById(nodeId) == null || nodeDao.findById(nodeId).equals(null)) {
			throw new BadRequestBusinessException("失败," + JSON.toJSONString(resource) + ",节点不存在");
		}
		Integer type = resource.getType();
		if (type == null || type.toString().trim().equals("")) {
			throw new BadRequestBusinessException("失败," + JSON.toJSONString(resource) + ",type为空");
		}
		if (type != ResourceType.TEXT && type != ResourceType.PICTURE && type != ResourceType.AUDIO
				&& type != ResourceType.VIDEO) {
			throw new BadRequestBusinessException("失败," + JSON.toJSONString(resource) + ",资源类型错误");
		}
		String name = resource.getName();
		if (name == null || name.trim().equals("") || name.trim().length() == 0) {
			throw new BadRequestBusinessException("失败," + JSON.toJSONString(resource) + ",name为空");
		}
		String key = resource.getKeyWord();
		if (key == null || key.trim().equals("") || key.trim().length() == 0) {
			throw new BadRequestBusinessException("失败," + JSON.toJSONString(resource) + ",keyword为空");
		}
		List<String> tagNames = resource.getTags();
		if (tagNames != null && !tagNames.isEmpty() && tagNames.size() > 0) {
			Integer size = tagNames.size();
			for (int i = 0; i < size; i++) {
				String tagName = tagNames.get(i);
				if (tagName.trim().length() == 0 || tagName.trim().equals("")) {
					throw new BadRequestBusinessException("失败,tagName[" + (i + 1) + "]为空");
				}
			}
		}
	}

	@Override
	public void create(List<CreateResourceParameter> resources) throws BadRequestBusinessException {
		logger.debug("resources=" + resources);
		if (resources == null || resources.isEmpty() || resources.size() == 0) {
			throw new BadRequestBusinessException("失败,parameter为空");
		}
		for (CreateResourceParameter resource : resources) {
			validateCreateOrUpdateParameter(resource);
		}
		for (CreateResourceParameter resource : resources) {
			resource.setId(idGenerator.next().toString());
			resourceDao.create(resource);
			List<String> tagNames = resource.getTags();
			if (tagNames != null && !tagNames.isEmpty() && tagNames.size() > 0) {
				for (String tagName : tagNames) {
					CreateResourceTagParameter parameter = new CreateResourceTagParameter();
					parameter.setId(idGenerator.next().toString());
					parameter.setResourceId(resource.getId());
					Tag tag = tagDao.findByName(tagName);
					if (tag == null || tag.equals(null)) {
						CreateTagParameter createTagParameter = new CreateTagParameter(tagName);
						createTagParameter.setId(idGenerator.next().toString());
						tagDao.create(createTagParameter);
						parameter.setTagId(createTagParameter.getId());
					} else {
						parameter.setTagId(tag.getId());
					}
					resourceTagDao.create(parameter);
				}
			}
		}
	}

	@Override
	public void delete(List<String> ids) throws BadRequestBusinessException {
		logger.debug("ids" + ids);
		if (ids == null || ids.isEmpty() || ids.size()==0) {
			throw new BadRequestBusinessException("失败,parameter为空");
		}
		for (String id : ids) {
			resourceTagDao.deleteByResourceId(id);
			resourceDao.delete(id);
		}
	}

	@Override
	public void update(List<UpdateResourceParameter> resources) throws BadRequestBusinessException {
		logger.debug("resources=" + resources);
		if (resources == null || resources.isEmpty() || resources.size() == 0) {
			throw new BadRequestBusinessException("失败,parameter为空");
		}
		for (UpdateResourceParameter resource : resources) {
			String id = resource.getId();
			if (id == null || id.trim().equals("")) {
				throw new BadRequestBusinessException("失败," + JSON.toJSONString(resource) + ",id为空");
			}
			validateCreateOrUpdateParameter(resource);
		}
		for (UpdateResourceParameter resource : resources) {
			resourceDao.update(resource);
			List<String> tagNames = resource.getTags();
			if (tagNames != null && tagNames.size() > 0) {
				resourceTagDao.deleteByResourceId(resource.getId());
				CreateResourceTagParameter parameter = new CreateResourceTagParameter();
				parameter.setId(idGenerator.next().toString());
				parameter.setResourceId(resource.getId());
				for (String tagName : tagNames) {
					Tag tag = tagDao.findByName(tagName);
					if (tag == null || "".equals(tag)) {
						CreateTagParameter createTagParameter = new CreateTagParameter(tagName);
						createTagParameter.setId(idGenerator.next().toString());
						tagDao.create(createTagParameter);
						parameter.setTagId(createTagParameter.getId());
					} else {
						parameter.setTagId(tag.getId());
					}
					resourceTagDao.create(parameter);
				}
			}
		}
	}

	@Override
	public List<Resource> find(FindResourceParameter parameter) throws BadRequestBusinessException {
		logger.debug("parameter=" + parameter);
		if (parameter.getOffset() == null || parameter.getOffset().trim().equals("")) {
			throw new BadRequestBusinessException("失败,offset为空");
		}
		Pattern pattern = Pattern.compile("[0-9]*");
		if(!pattern.matcher(parameter.getOffset().trim()).matches()){
			throw new BadRequestBusinessException("失败,offset不是数字");
		}
		List<Resource> res = resourceDao.find(parameter);
		return res;
	}

	@Override
	public Resource findResById(String id) throws BadRequestBusinessException {
		if(id == null || "".equals(id.trim())){
			throw new BadRequestBusinessException("失败,parameter为空");
		}
		Resource resource = resourceDao.findResById(id);
		return resource;
	}
	
}
