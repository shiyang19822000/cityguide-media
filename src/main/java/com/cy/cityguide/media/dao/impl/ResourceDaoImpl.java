package com.cy.cityguide.media.dao.impl;

import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.cy.cityguide.media.constant.Constant;
import com.cy.cityguide.media.dao.ResourceDao;
import com.cy.cityguide.media.parameter.CreateResourceParameter;
import com.cy.cityguide.media.parameter.FindResourceParameter;
import com.cy.cityguide.media.parameter.UpdateResourceParameter;
import com.cy.cityguide.media.result.Resource;

@Repository
public class ResourceDaoImpl implements ResourceDao {

	@Autowired
	private SqlSessionTemplate sqlSessionTemplate;

	@Override
	public void create(CreateResourceParameter resource) {
		sqlSessionTemplate.insert("ResourceDao.create", resource);
	}

	@Override
	public void delete(Long id) {
		sqlSessionTemplate.delete("ResourceDao.delete", id);
	}

	@Override
	public void update(UpdateResourceParameter resource) {
		sqlSessionTemplate.update("ResourceDao.update", resource);
	}

	@Override
	public Integer countByNodeId(Long nodeId) {
		return sqlSessionTemplate.selectOne("ResourceDao.countByNodeId", nodeId);
	}

	@Override
	public List<Resource> find(FindResourceParameter parameter) {
		Integer offSet = Integer.parseInt(parameter.getOffset().trim());
		RowBounds rowBounds = new RowBounds(offSet, Constant.LIMIT);
		return sqlSessionTemplate.selectList("ResourceDao.find", parameter, rowBounds);
	}

}
