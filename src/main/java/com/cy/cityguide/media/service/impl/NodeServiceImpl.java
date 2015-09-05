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
import com.cy.cityguide.media.exception.BadRequestBusinessException;
import com.cy.cityguide.media.parameter.CreateNodeParameter;
import com.cy.cityguide.media.parameter.UpdateNodeParameter;
import com.cy.cityguide.media.result.Node;
import com.cy.cityguide.media.service.NodeService;
import com.cychina.platform.id.IdGenerator;

@Service
@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
public class NodeServiceImpl implements NodeService {

	private Logger logger = Logger.getLogger(NodeServiceImpl.class);

	@Autowired
	private NodeDao nodeDao;

	@Autowired
	private ResourceDao resourceDao;
	
	@Autowired
	private IdGenerator idGenerator;

	private void validateCreateOrUpdateParameter(CreateNodeParameter node) 
			throws BadRequestBusinessException {
		String parentId = node.getParentId();
		if (parentId != null && nodeDao.findById(parentId) == null) {
			throw new BadRequestBusinessException("失败," + JSON.toJSONString(node) + ",父节点不存在");
		}
		String name = node.getName();
		if (name == null || name.trim().length() == 0) {
			throw new BadRequestBusinessException("失败," + JSON.toJSONString(node) + ",name为空");
		}
		
		if(node.getId()==null || "".equals(node.getId())){
			if(nodeDao.findByName(name).size()>0){
				throw new BadRequestBusinessException("失败，"+JSON.toJSONString(node)+",名称已存在");
			}
		}else{
			if(!name.equalsIgnoreCase(nodeDao.findById(node.getId()).getName())){
				if (!nodeDao.findByName(name).isEmpty() && nodeDao.findByName(name).size()>0) {
					throw new BadRequestBusinessException("失败," + JSON.toJSONString(node) + ",名称已存在");
				}
			}
		}
			
	}
	
	@Override
	public void create(List<CreateNodeParameter> nodes)
			throws BadRequestBusinessException {
		logger.debug("nodes=" + nodes);
		if (nodes == null || nodes.isEmpty() || nodes.size() == 0) {
			throw new BadRequestBusinessException("失败,parameter为空");
		}
		for (CreateNodeParameter node : nodes) {
			validateCreateOrUpdateParameter(node);
		}
		for (CreateNodeParameter node : nodes) {
			if (node.getParentId() == null || "".equals(node.getParentId().trim())) {
				node.setParentId(Constant.ROOT_NODE);
			}
			node.setId(idGenerator.next().toString());
			nodeDao.create(node);
		}
	}

	@Override
	public void delete(List<String> ids) 
			throws BadRequestBusinessException {
		logger.debug("ids" + ids);
		if (ids == null || ids.isEmpty() || ids.size() == 0) {
			throw new BadRequestBusinessException("失败,parameter为空");
		}
		for (String id : ids) {
			if(id ==null || "".equals(id.trim()) || id.trim().length()==0){
				throw new BadRequestBusinessException("失败,节点id为空");
			}
			if (id.equalsIgnoreCase(Constant.ROOT_NODE)) {
				throw new BadRequestBusinessException("失败,节点" + id + "为根节点");
			}
			if (nodeDao.countChildren(id) > 0) {
				throw new BadRequestBusinessException("失败,节点" + id + "有子节点");
			}
			if (resourceDao.countByNodeId(id) > 0) {
				throw new BadRequestBusinessException("失败,节点" + id + "有关联的资源");
			}
			if(nodeDao.findById(id)==null || nodeDao.findById(id).equals(null)){
				throw new BadRequestBusinessException("失败,节点"+id+"不存在");
			}
		}
		for (String id : ids) {
			nodeDao.delete(id);
		}
	}

	@Override
	public void update(List<UpdateNodeParameter> nodes)
			throws BadRequestBusinessException {
		logger.debug("nodes=" + nodes);
		if (nodes == null || nodes.isEmpty() || nodes.size() == 0) {
			throw new BadRequestBusinessException("失败,parameter为空");
		}
		for (UpdateNodeParameter node : nodes) {
			String id = node.getId();
			if (id == null || id.trim().equals("")) {
				throw new BadRequestBusinessException("失败," + JSON.toJSONString(node) + ",id为空");
			}
			if(nodeDao.findById(id)==null || nodeDao.findById(id).equals(null)){
				throw new BadRequestBusinessException("失败,"+JSON.toJSONString(node)+",节点id="+id+"不存在");
			}
			validateCreateOrUpdateParameter(node);
		}
		for (UpdateNodeParameter node : nodes) {
			if (node.getParentId() == null || node.getParentId().trim().equals("")) {
				node.setParentId(Constant.ROOT_NODE);
			}
			nodeDao.update(node);
		}
	}

	@Override
	public List<Node> findChildren(String id) 
			throws BadRequestBusinessException {
		logger.debug("id="+id);
		if (id == null || id.trim().equals("")) {
			throw new BadRequestBusinessException("失败,parameter为空");
		}
		return nodeDao.findChildren(id);
	}

}
