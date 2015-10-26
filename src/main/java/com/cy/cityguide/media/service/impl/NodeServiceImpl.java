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

	private void validateCreateOrUpdateParameter(CreateNodeParameter node, String operate)
			throws BadRequestBusinessException {
		String parentId = node.getParentId();
		if (operate.equals(Constant.VALIDATION_UPDATE)) {
			if (parentId != null && nodeDao.findById(parentId) == null) {
				throw new BadRequestBusinessException("失败," + JSON.toJSONString(node) + ",父节点不存在");
			}
			String name = node.getName();
			if (nodeDao.findByName(name).size() > 0) {
				throw new BadRequestBusinessException("失败，" + JSON.toJSONString(node) + ",名称已存在");
			}
			if (node.getPath() != null) {
				Node nodeTestPath = new Node();
				nodeTestPath.setPath(node.getPath());
				Node nodeReturn = nodeDao.findNodeByParrmeter(nodeTestPath);
				if (nodeReturn != null) {
					throw new BadRequestBusinessException("失败," + JSON.toJSONString(node) + ",path已经存在");
				}
			}
			return;
		}
		String nodePath = node.getPath();
		if ((parentId == null || parentId.trim().equals("")) && (nodePath == null || nodePath.trim().equals(""))) {
			throw new BadRequestBusinessException("失败," + JSON.toJSONString(node) + ",parentId和path不能同时为空");
		}
		if (parentId != null && nodeDao.findById(parentId) == null) {
			throw new BadRequestBusinessException("失败," + JSON.toJSONString(node) + ",父节点不存在");
		}
		if (nodePath != null && !nodePath.equals("")) {
			if (!nodePath.contains("/")) {
				throw new BadRequestBusinessException("失败," + JSON.toJSONString(node) + ",节点path必须能够以/进行拆分");
			} else {
				String parentNodePath = nodePath.split("/")[0];
				Node vnode = new Node();
				vnode.setPath(parentNodePath);
				Node nodeParent = nodeDao.findNodeByParrmeter(vnode);
				if (nodeParent == null) {
					throw new BadRequestBusinessException("失败," + JSON.toJSONString(node) + ",与path匹配的父节点不存在");
				} else {
					if (parentId != null) {
						if (!nodeParent.getId().equals(parentId)) {
							throw new BadRequestBusinessException(
									"失败," + JSON.toJSONString(node) + ",通过path查询出来的父节点Id与parentId不匹配");
						}
					}
				}
			}
		}
		String name = node.getName();
		if (name == null || name.trim().length() == 0) {
			throw new BadRequestBusinessException("失败," + JSON.toJSONString(node) + ",name为空");
		}
		if (nodeDao.findByName(name).size() > 0) {
			throw new BadRequestBusinessException("失败，" + JSON.toJSONString(node) + ",名称已存在");
		}
		if (node.getPath() != null && !node.getPath().equals("")) {
			Node nodeTestPath = new Node();
			nodeTestPath.setPath(node.getPath());
			Node nodeReturn = nodeDao.findNodeByParrmeter(nodeTestPath);
			if (nodeReturn != null) {
				throw new BadRequestBusinessException("失败," + JSON.toJSONString(node) + ",path已经存在");
			}
		}
	}

	@Override
	public void create(List<CreateNodeParameter> nodes) throws BadRequestBusinessException {
		logger.debug("nodes=" + nodes);
		if (nodes == null || nodes.isEmpty() || nodes.size() == 0) {
			throw new BadRequestBusinessException("失败,parameter为空");
		}
		for (CreateNodeParameter node : nodes) {
			validateCreateOrUpdateParameter(node, Constant.VALIDATION_CREATE);
		}
		for (CreateNodeParameter node : nodes) {
			String nodePath = node.getPath();
			if (node.getParentId() == null || "".equals(node.getParentId().trim())) {
				if (nodePath != null && !"".equals(nodePath)) {
					String parentNodePath = nodePath.split("/")[0];
					Node vnode = new Node();
					vnode.setPath(parentNodePath);
					String parentId = nodeDao.findNodeByParrmeter(vnode).getId();
					node.setParentId(parentId);
				}
			}
			if (nodePath == null || nodePath.trim().equals("")) {
				String parentId = node.getParentId();
				Node parentNode = nodeDao.findById(parentId);
				if (parentNode != null) {
					node.setPath(parentNode.getName() + "/" + node.getName());
				}
			}
			node.setId(idGenerator.next().toString());
			nodeDao.create(node);
		}
	}

	@Override
	public void delete(List<String> ids) throws BadRequestBusinessException {
		logger.debug("ids" + ids);
		if (ids == null || ids.isEmpty() || ids.size() == 0) {
			throw new BadRequestBusinessException("失败,parameter为空");
		}
		for (String id : ids) {
			if (id == null || "".equals(id.trim()) || id.trim().length() == 0) {
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
			if (nodeDao.findById(id) == null || nodeDao.findById(id).equals(null)) {
				throw new BadRequestBusinessException("失败,节点" + id + "不存在");
			}
		}
		for (String id : ids) {
			nodeDao.delete(id);
		}
	}

	@Override
	public void update(List<UpdateNodeParameter> nodes) throws BadRequestBusinessException {
		logger.debug("nodes=" + nodes);
		if (nodes == null || nodes.isEmpty() || nodes.size() == 0) {
			throw new BadRequestBusinessException("失败,parameter为空");
		}
		for (UpdateNodeParameter node : nodes) {
			String id = node.getId();
			if (id == null || id.trim().equals("")) {
				throw new BadRequestBusinessException("失败," + JSON.toJSONString(node) + ",id为空");
			}
			if (nodeDao.findById(id) == null || nodeDao.findById(id).equals(null)) {
				throw new BadRequestBusinessException("失败," + JSON.toJSONString(node) + ",节点id=" + id + "不存在");
			}
			validateCreateOrUpdateParameter(node, Constant.VALIDATION_UPDATE);
		}
		for (UpdateNodeParameter node : nodes) {
			if ((node.getParentId() == null || node.getParentId().trim().equals(""))
					&& (node.getName() == null || node.getName().trim().equals(""))
					&& ((node.getPath() == null || node.getPath().trim().equals("")))) {
				throw new BadRequestBusinessException("至少传出一个修改的参数");
			}
			nodeDao.update(node);
		}
	}

	@Override
	public List<Node> findChildren(String id) throws BadRequestBusinessException {
		logger.debug("id=" + id);
		if (id == null || id.trim().equals("")) {
			throw new BadRequestBusinessException("失败,parameter为空");
		}
		return nodeDao.findChildren(id);
	}

	@Override
	public Node findNodeByParrmeter(String id, String path) throws BadRequestBusinessException {
		if ((id == null || id.equals("")) && (path == null || path.equals(""))) {
			throw new BadRequestBusinessException("id 和 path 不能同时为空");
		}
		Node node = new Node();
		node.setId(id);
		node.setPath(path);
		Node nodeReturn = nodeDao.findNodeByParrmeter(node);
		return nodeReturn;
	}

}
