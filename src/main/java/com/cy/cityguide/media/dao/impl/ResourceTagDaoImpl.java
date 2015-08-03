package com.cy.cityguide.media.dao.impl;

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
	public void deleteByResourceId(Long resourceId) {
		sqlSessionTemplate.delete("ResourceTagDao.delete", resourceId);
	}

}
