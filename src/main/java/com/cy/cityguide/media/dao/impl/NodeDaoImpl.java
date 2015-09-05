package com.cy.cityguide.media.dao.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.cy.cityguide.media.dao.NodeDao;
import com.cy.cityguide.media.parameter.CreateNodeParameter;
import com.cy.cityguide.media.parameter.UpdateNodeParameter;
import com.cy.cityguide.media.result.Node;

@Repository
public class NodeDaoImpl implements NodeDao {

	@Autowired
	private SqlSessionTemplate sqlSessionTemplate;

	@Override
	public void create(CreateNodeParameter node) {
		sqlSessionTemplate.insert("NodeDao.create", node);
	}

	@Override
	public void delete(String id) {
		sqlSessionTemplate.delete("NodeDao.delete", id);
	}

	@Override
	public void update(UpdateNodeParameter node) {
		sqlSessionTemplate.update("NodeDao.update", node);
	}

	@Override
	public Integer countChildren(String id) {
		return sqlSessionTemplate.selectOne("NodeDao.countChildren", id);
	}

	@Override
	public Node findById(String id) {
		return sqlSessionTemplate.selectOne("NodeDao.findById", id);
	}

	@Override
	public List<Node> findChildren(String id) {
		return sqlSessionTemplate.selectList("NodeDao.findChildren", id);
	}

	@Override
	public List<Node> findByName(String name) {
		return sqlSessionTemplate.selectList("NodeDao.findByName",name);
	}


}
