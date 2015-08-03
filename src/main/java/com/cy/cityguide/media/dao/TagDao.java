package com.cy.cityguide.media.dao;

import com.cy.cityguide.media.parameter.CreateTagParameter;
import com.cy.cityguide.media.result.Tag;

public interface TagDao {

	void create(CreateTagParameter parameter);

	Tag findByName(String name);

}
