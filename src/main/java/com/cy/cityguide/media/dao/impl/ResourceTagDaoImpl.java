package com.cy.cityguide.media.dao.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.cy.cityguide.media.dao.ResourceTagDao;
import com.cy.cityguide.media.dao.parameter.CreateResourceTagParameter;

@Repository
public class ResourceTagDaoImpl implements ResourceTagDao {

	@Autowired
	private SqlSessionTemplate sqlSessionTemplate;
	
	@Override
	public void create(CreateResourceTagParameter parameter) {
		sqlSessionTemplate.insert("ResourceTagDao.create", parameter);
	}

	@Override
	public void deleteByResourceId(String resourceId) {
		sqlSessionTemplate.delete("ResourceTagDao.deleteByResourceId", resourceId);
	}

	@Override
	public List<String> findTagIdsByResourceId(String resourceId) {
		List<String> tagIds = sqlSessionTemplate.selectList("ResourceTagDao.findByResourceId", resourceId);
		return tagIds;
	}

}
