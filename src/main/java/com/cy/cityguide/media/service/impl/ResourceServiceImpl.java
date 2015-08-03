package com.cy.cityguide.media.service.impl;

import java.util.List;

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
	
	private void validateCreateOrUpdateParameter(CreateResourceParameter resource) throws BadRequestBusinessException {
		Long nodeId = resource.getNodeId();
		if (nodeId == null) {
			throw new BadRequestBusinessException("失败," + JSON.toJSONString(resource) + ",nodeId=null");
		}
		if (nodeDao.findById(nodeId) == null) {
			throw new BadRequestBusinessException("失败," + JSON.toJSONString(resource) + ",节点不存在");
		}
		Integer type = resource.getType();
		if (type == null) {
			throw new BadRequestBusinessException("失败," + JSON.toJSONString(resource) + ",type=null");
		}
		if (type != ResourceType.TEXT || type != ResourceType.PICTURE 
				|| type != ResourceType.AUDIO || type != ResourceType.VIDEO) {
			throw new BadRequestBusinessException("失败," + JSON.toJSONString(resource) + ",资源类型错误");
		}
		String name = resource.getName();
		if (name == null) {
			throw new BadRequestBusinessException("失败," + JSON.toJSONString(resource) + ",name=null");
		}
		if (name.trim().length() == 0) {
			throw new BadRequestBusinessException("失败," + JSON.toJSONString(resource) + ",name.length=0");
		}
		String key = resource.getKey();
		if (key == null) {
			throw new BadRequestBusinessException("失败," + JSON.toJSONString(resource) + ",key=null");
		}
		if (key.trim().length() == 0) {
			throw new BadRequestBusinessException("失败," + JSON.toJSONString(resource) + ",key.length=0");
		}
		List<String> tagNames = resource.getTags();
		if (tagNames != null && tagNames.size() > 0) {
			Integer size = tagNames.size();
			for (int i = 0; i < size; i++) {
				String tagName = tagNames.get(0);
				if (tagName.trim().length() == 0) {
					throw new BadRequestBusinessException("失败,tagName[" + (i + 1) + "].length=0");
				}
			}
		}
	}

	@Override
	public void create(List<CreateResourceParameter> resources)
			throws BadRequestBusinessException {
		logger.debug("resources=" + resources);
		if (resources == null) {
			throw new BadRequestBusinessException("失败,parameter=null");
		}
		if (resources.size() == 0) {
			throw new BadRequestBusinessException("失败,parameter.size==0");
		}
		for (CreateResourceParameter resource : resources) {
			validateCreateOrUpdateParameter(resource);
		}
		for (CreateResourceParameter resource : resources) {
			resourceDao.create(resource);
			List<String> tagNames = resource.getTags();
			if (tagNames != null && tagNames.size() > 0) {
				for (String tagName : tagNames) {
					CreateResourceTagParameter parameter = new CreateResourceTagParameter();
					parameter.setResourceId(resource.getId());
					Tag tag = tagDao.findByName(tagName);
					if (tag == null) {
						CreateTagParameter createTagParameter = new CreateTagParameter(tagName);
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
	public void delete(List<Long> ids) 
			throws BadRequestBusinessException {
		logger.debug("ids" + ids);
		if (ids == null) {
			throw new BadRequestBusinessException("失败,parameter=null");
		}
		if (ids.size() == 0) {
			throw new BadRequestBusinessException("失败,parameter.size==0");
		}
		for (Long id : ids) {
			resourceTagDao.deleteByResourceId(id);
			resourceDao.delete(id);
		}
	}

	@Override
	public void update(List<UpdateResourceParameter> resources)
			throws BadRequestBusinessException {
		logger.debug("resources=" + resources);
		if (resources == null) {
			throw new BadRequestBusinessException("失败,parameter=null");
		}
		if (resources.size() == 0) {
			throw new BadRequestBusinessException("失败,parameter.size==0");
		}
		for (UpdateResourceParameter resource : resources) {
			Long id = resource.getId();
			if (id == null) {
				throw new BadRequestBusinessException("失败," + JSON.toJSONString(resource) + ",id=null");
			}
			validateCreateOrUpdateParameter(resource);
		}
		for (UpdateResourceParameter resource : resources) {
			resourceDao.update(resource);
			List<String> tagNames = resource.getTags();
			if (tagNames != null && tagNames.size() > 0) {
				for (String tagName : tagNames) {
					resourceTagDao.deleteByResourceId(resource.getId());
					CreateResourceTagParameter parameter = new CreateResourceTagParameter();
					parameter.setResourceId(resource.getId());
					Tag tag = tagDao.findByName(tagName);
					if (tag == null) {
						CreateTagParameter createTagParameter = new CreateTagParameter(tagName);
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
	public List<Resource> find(FindResourceParameter parameter)
			throws BadRequestBusinessException {
		logger.debug("parameter=" + parameter);
		if (parameter.getOffset() == null) {
			throw new BadRequestBusinessException("失败,offset=null");
		}
		return resourceDao.find(parameter);
	}

}
