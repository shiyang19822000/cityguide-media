package com.cy.cityguide.media.service.impl;

import java.lang.reflect.Field;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.cy.cityguide.media.constant.Constant;
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
import com.cy.cityguide.media.service.UploadService;
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

	@Autowired
	private UploadService uploadService;

	private void validateCreateOrUpdateParameter(CreateResourceParameter resource, String operate)
			throws BadRequestBusinessException {
		if (operate.equals(Constant.VALIDATION_UPDATE)) {
			Integer type = resource.getType();
			if (type != null) {
				if (type != ResourceType.TEXT && type != ResourceType.PICTURE && type != ResourceType.AUDIO
						&& type != ResourceType.VIDEO) {
					throw new BadRequestBusinessException("失败," + JSON.toJSONString(resource) + ",资源类型错误");
				}
			}
			return;
		}
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
			throw new BadRequestBusinessException("失败," + JSON.toJSONString(resource) + ",路径为空");
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
			validateCreateOrUpdateParameter(resource, Constant.VALIDATION_CREATE);
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
		if (ids == null || ids.isEmpty() || ids.size() == 0) {
			throw new BadRequestBusinessException("失败,parameter为空");
		}
		for (String id : ids) {
			Resource res = resourceDao.findResById(id);
			String key = res.getKeyWord();
			boolean deleteStatus = uploadService.deleteFile(key);
			if (deleteStatus) {
				resourceTagDao.deleteByResourceId(id);
				resourceDao.delete(id);
			}
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
			if ((resource.getNodeId() == null || resource.getNodeId().trim().equals(""))
					&& (resource.getType() == null || resource.getType().toString().trim().equals(""))
					&& (resource.getName() == null || resource.getName().trim().equals(""))
					&& (resource.getKeyWord() == null || resource.getKeyWord().trim().equals(""))
					&& (resource.getBucket() == null || resource.getBucket().trim().equals(""))
					&& (resource.getTags() == null || resource.getTags().isEmpty())) {
				throw new BadRequestBusinessException("至少设置一个需要修改的参数");
			}
			validateCreateOrUpdateParameter(resource, Constant.VALIDATION_UPDATE);
		}
		for (UpdateResourceParameter resource : resources) {
			CreateResourceParameter createResourceParameter = new CreateResourceParameter();
			Field[] fileds = createResourceParameter.getClass().getDeclaredFields();
			boolean flag = false;
			for (Field field : fileds) {
				field.setAccessible(true);
				try {
					String propertiesName = field.getName();
					if (!"tags".equalsIgnoreCase(propertiesName) && !"id".equalsIgnoreCase(propertiesName)) {
						Object value = field.get(resource);
						if (value != null && !value.toString().trim().equals("")) {
							flag = true;
						}
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			if (flag) {
				resourceDao.update(resource);
			}
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
	public List<Resource> find(FindResourceParameter parameter, String limit) throws BadRequestBusinessException {
		logger.debug("parameter=" + parameter);
		if (parameter.getOffset() == null || parameter.getOffset().trim().equals("")) {
			parameter.setOffset("0");
		}
		Pattern pattern = Pattern.compile("[0-9]*");
		if (!pattern.matcher(parameter.getOffset().trim()).matches()) {
			throw new BadRequestBusinessException("失败,offset不是数字");
		}
		if (limit == null || limit.trim().equals("")) {
			limit = "10";
		}
		if (!pattern.matcher(limit.trim()).matches()) {
			throw new BadRequestBusinessException("失败,limit不是大于0的数字");
		}
		int numLimit = Integer.parseInt(limit);
		if (numLimit == 0) {
			throw new BadRequestBusinessException("失败,limit必须大于0");
		}
		List<Resource> res = resourceDao.find(parameter, numLimit);
		for (Resource re : res) {
			re.setResourceUrl(Constant.IMAGE_URL_COM + re.getKeyWord());
		}
		return res;
	}

	@Override
	public Resource findResById(String id) throws BadRequestBusinessException {
		if (id == null || "".equals(id.trim())) {
			throw new BadRequestBusinessException("失败,parameter为空");
		}
		Resource resource = resourceDao.findResById(id);
		resource.setResourceUrl(Constant.IMAGE_URL_COM + resource.getKeyWord());
		return resource;
	}

}
