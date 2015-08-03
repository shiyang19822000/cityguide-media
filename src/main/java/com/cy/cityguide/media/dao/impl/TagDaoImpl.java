package com.cy.cityguide.media.dao.impl;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.cy.cityguide.media.dao.TagDao;
import com.cy.cityguide.media.parameter.CreateTagParameter;
import com.cy.cityguide.media.result.Tag;

@Repository
public class TagDaoImpl implements TagDao {

	@Autowired
	private SqlSessionTemplate sqlSessionTemplate;

	@Override
	public void create(CreateTagParameter parameter) {
		sqlSessionTemplate.insert("TagDao.create", parameter);
	}

	@Override
	public Tag findByName(String name) {
		return sqlSessionTemplate.selectOne("TagDao.count", name);
	}

}
