package com.cy.cityguide.media.dao.impl;

import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.cy.cityguide.media.dao.ResourceDao;
import com.cy.cityguide.media.parameter.CreateResourceParameter;
import com.cy.cityguide.media.parameter.FindResourceParameter;
import com.cy.cityguide.media.parameter.UpdateResourceParameter;
import com.cy.cityguide.media.parameter.UploadMsgParameter;
import com.cy.cityguide.media.result.Resource;
import com.cy.cityguide.media.result.UploadMsgResult;

@Repository
public class ResourceDaoImpl implements ResourceDao {

	@Autowired
	private SqlSessionTemplate sqlSessionTemplate;

	@Override
	public void create(CreateResourceParameter resource) {
		sqlSessionTemplate.insert("ResourceDao.create", resource);
	}

	@Override
	public void delete(String id) {
		sqlSessionTemplate.delete("ResourceDao.delete", id);
	}

	@Override
	public void update(UpdateResourceParameter resource) {
		sqlSessionTemplate.update("ResourceDao.update", resource);
	}

	@Override
	public Integer countByNodeId(String nodeId) {
		return sqlSessionTemplate.selectOne("ResourceDao.countByNodeId", nodeId);
	}

	@Override
	public List<Resource> find(FindResourceParameter parameter, int limit) {
		Integer offSet = Integer.parseInt(parameter.getOffset().trim());
		RowBounds rowBounds = new RowBounds(offSet, limit);
		return sqlSessionTemplate.selectList("ResourceDao.find", parameter, rowBounds);
	}

	@Override
	public Resource findResById(String id) {
		Resource res = sqlSessionTemplate.selectOne("ResourceDao.findById", id);
		return res;
	}

	@Override
	public void createUploadMsg(UploadMsgParameter uploadMsg) {
		sqlSessionTemplate.insert("ResourceDao.createUploadMsg", uploadMsg);
	}

	@Override
	public UploadMsgResult getUploadMsg(String uploadId) {
		UploadMsgResult result = sqlSessionTemplate.selectOne("ResourceDao.findUploadMsgById", uploadId);
		return result;
	}

	@Override
	public void modifyUploadMsgStatus(UploadMsgParameter parameter) {
		sqlSessionTemplate.update("ResourceDao.updateUploadMsgById", parameter);
	}

}
