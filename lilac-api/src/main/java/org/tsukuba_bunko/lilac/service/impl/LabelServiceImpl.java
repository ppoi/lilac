/*
 * All Rights Reserved.
 * Copyright (C) 2011 Tsukuba Bunko.
 *
 * Licensed under the BSD License ("the License"); you may not use
 * this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.tsukuba-bunko.org/licenses/LICENSE.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * $Id:　$
 */
package org.tsukuba_bunko.lilac.service.impl;

import java.util.List;

import org.seasar.extension.jdbc.JdbcManager;
import org.seasar.extension.jdbc.where.SimpleWhere;
import org.tsukuba_bunko.lilac.entity.Label;
import org.tsukuba_bunko.lilac.service.LabelService;


/**
 * @author $Author: $
 * @version $Revision: $ $Date: $
 */
public class LabelServiceImpl implements LabelService {

	public JdbcManager jdbcManager;

	/**
	 * @see org.tsukuba_bunko.lilac.service.LabelService#get(java.lang.String)
	 */
	@Override
	public Label get(String name) {
		return jdbcManager.from(Label.class).where(new SimpleWhere()
			.eq("name", name)
		).getSingleResult();
	}

	/**
	 * @see org.tsukuba_bunko.lilac.service.LabelService#create(java.awt.Label)
	 */
	@Override
	public void create(Label label) {
		jdbcManager.insert(label);

	}

	/**
	 * @see org.tsukuba_bunko.lilac.service.LabelService#update(java.awt.Label)
	 */
	@Override
	public void update(Label label) {
		jdbcManager.update(label);
	}

	/**
	 * @see org.tsukuba_bunko.lilac.service.LabelService#delete(java.lang.String)
	 */
	@Override
	public void delete(String name) {
		Label entiy = new Label();
		entiy.name = name;
		jdbcManager.delete(entiy);
	}

	/**
	 * @see org.tsukuba_bunko.lilac.service.LabelService#list()
	 */
	@Override
	public List<Label> list() {
		return jdbcManager.from(Label.class).orderBy("name ASC").getResultList();
	}

}
