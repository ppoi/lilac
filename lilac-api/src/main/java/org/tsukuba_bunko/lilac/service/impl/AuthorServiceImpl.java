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
import org.tsukuba_bunko.lilac.entity.Author;
import org.tsukuba_bunko.lilac.service.AuthorService;
import org.tsukuba_bunko.lilac.service.TextSearchWhere;

/**
 * @author $Author: $
 * @version $Revision: $ $Date: $
 */
public class AuthorServiceImpl implements AuthorService {

	/**
	 * S2JDBC JdbcManager
	 */
	public JdbcManager	jdbcManager;

	/**
	 * @see org.tsukuba_bunko.lilac.service.AuthorService#get(int)
	 */
	@Override
	public Author get(int id) {
		return jdbcManager.from(Author.class)
				.where(new SimpleWhere()
					.eq("id", id)
				).getSingleResult();
	}

	/**
	 * @see org.tsukuba_bunko.lilac.service.AuthorService#synonym(int)
	 */
	@Override
	public List<Author> synonym(int id) {
		return jdbcManager.selectBySql(Author.class,
				"SELECT DISTINCT a.* FROM author a JOIN author b ON a.synonym_key=b.synonym_key AND a.id!=b.id AND b.id=?", id)
				.getResultList();
	}

	/**
	 * @see org.tsukuba_bunko.lilac.service.AuthorService#list()
	 */
	@Override
	public List<Author> list() {
		return list(null);
	}

	/**
	 * @see org.tsukuba_bunko.lilac.service.AuthorService#list(java.lang.String)
	 */
	@Override
	public List<Author> list(String nameCondition) {
		return jdbcManager.from(Author.class)
				.where(new TextSearchWhere()
					.textsearch("name", nameCondition)
				)
				.orderBy("name")
				.getResultList();
	}

}
