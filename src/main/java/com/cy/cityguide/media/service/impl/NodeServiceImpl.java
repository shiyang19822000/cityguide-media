package com.cy.cityguide.media.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.cy.cityguide.media.constant.Constant;
import com.cy.cityguide.media.dao.NodeDao;
import com.cy.cityguide.media.dao.ResourceDao;
import com.cy.cityguide.media.dao.parameter.FindNodeByParentIdAndNameParameter;
import com.cy.cityguide.media.exception.BadRequestBusinessException;
import com.cy.cityguide.media.parameter.CreateNodeParameter;
import com.cy.cityguide.media.parameter.UpdateNodeParameter;
import com.cy.cityguide.media.result.Node;
import com.cy.cityguide.media.service.NodeService;

@Service
@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
public class NodeServiceImpl implements NodeService {

	private Logger logger = Logger.getLogger(NodeServiceImpl.class);

	@Autowired
	private NodeDao nodeDao;

	@Autowired
	private ResourceDao resourceDao;

	private void validateCreateOrUpdateParameter(CreateNodeParameter node) 
			throws BadRequestBusinessException {
		Long parentId = node.getParentId();
		if (parentId != null && nodeDao.findById(parentId) == null) {
			throw new BadRequestBusinessException("失败," + JSON.toJSONString(node) + ",父节点不存在");
		}
		String name = node.getName();
		if (name == null) {
			throw new BadRequestBusinessException("失败," + JSON.toJSONString(node) + ",name=null");
		}
		if (name.trim().length() == 0) {
			throw new BadRequestBusinessException("失败," + JSON.toJSONString(node) + ",name.length=0");
		}
		if (nodeDao.findByParentIdAndName(new FindNodeByParentIdAndNameParameter(parentId, name)) != null) {
			throw new BadRequestBusinessException("失败," + JSON.toJSONString(node) + ",名称已存在");
		}
	}
	
	@Override
	public void create(List<CreateNodeParameter> nodes)
			throws BadRequestBusinessException {
		logger.debug("nodes=" + nodes);
		if (nodes == null) {
			throw new BadRequestBusinessException("失败,parameter=null");
		}
		if (nodes.size() == 0) {
			throw new BadRequestBusinessException("失败,parameter.size==0");
		}
		for (CreateNodeParameter node : nodes) {
			validateCreateOrUpdateParameter(node);
		}
		for (CreateNodeParameter node : nodes) {
			if (node.getParentId() == null) {
				node.setParentId(Constant.ROOT_NODE);
			}
			nodeDao.create(node);
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
			if (id == Constant.ROOT_NODE) {
				throw new BadRequestBusinessException("失败,节点" + id + "为根节点");
			}
			if (nodeDao.countChildren(id) > 0) {
				throw new BadRequestBusinessException("失败,节点" + id + "有子节点");
			}
			if (resourceDao.countByNodeId(id) > 0) {
				throw new BadRequestBusinessException("失败,节点" + id + "有关联的资源");
			}
		}
		for (Long id : ids) {
			nodeDao.delete(id);
		}
	}

	@Override
	public void update(List<UpdateNodeParameter> nodes)
			throws BadRequestBusinessException {
		logger.debug("nodes=" + nodes);
		if (nodes == null) {
			throw new BadRequestBusinessException("失败,parameter=null");
		}
		if (nodes.size() == 0) {
			throw new BadRequestBusinessException("失败,parameter.size==0");
		}
		for (UpdateNodeParameter node : nodes) {
			Long id = node.getId();
			if (id == null) {
				throw new BadRequestBusinessException("失败," + JSON.toJSONString(node) + ",id=null");
			}
			validateCreateOrUpdateParameter(node);
		}
		for (UpdateNodeParameter node : nodes) {
			if (node.getParentId() == null) {
				node.setParentId(Constant.ROOT_NODE);
			}
			nodeDao.update(node);
		}
	}

	@Override
	public List<Node> findChildren(Long id) 
			throws BadRequestBusinessException {
		logger.debug(new StringBuilder().append("id=").append(id).toString());
		if (id == null) {
			throw new BadRequestBusinessException("失败,parameter=null");
		}
		return nodeDao.findChildren(id);
	}

}
