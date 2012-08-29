/*
 * All Rights Reserved.
 * Copyright (C) 2012 Tsukuba Bunko.
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
 */
package org.tsukuba_bunko.lilac.service.impl;

import javax.annotation.Resource;

import org.seasar.extension.jdbc.JdbcManager;
import org.tsukuba_bunko.lilac.entity.Book;
import org.tsukuba_bunko.lilac.service.BookService;


/**
 * @author ppoi
 * @version 2012.04
 */
public class BookServiceImpl implements BookService {

	@Resource
	public JdbcManager jdbcManager;

	/**
	 * @see org.tsukuba_bunko.lilac.service.BookService#get(int)
	 */
	@Override
	public Book get(int id) {
		return jdbcManager.from(Book.class)
				.innerJoin("bibliography")
				.innerJoin("bibliography.authors")
				.innerJoin("bibliography.authors.author")
				.leftOuterJoin("location")
				.where("id=?", id).getSingleResult();
	}

}
