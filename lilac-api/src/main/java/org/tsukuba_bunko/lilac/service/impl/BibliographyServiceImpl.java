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

import org.seasar.extension.jdbc.AutoSelect;
import org.seasar.extension.jdbc.JdbcManager;
import org.seasar.extension.jdbc.where.SimpleWhere;
import org.seasar.framework.util.StringUtil;
import org.tsukuba_bunko.lilac.entity.Bibliography;
import org.tsukuba_bunko.lilac.service.BibliographyService;
import org.tsukuba_bunko.lilac.service.BookSearchCondition;
import org.tsukuba_bunko.lilac.service.SearchResult;


/**
 * @author $Author: $
 * @version $Revision: $ $Date: $
 */
public class BibliographyServiceImpl implements BibliographyService {

	public JdbcManager jdbcManager;

	/**
	 * @see org.tsukuba_bunko.lilac.service.BibliographyService#get(int)
	 */
	@Override
	public Bibliography get(int id) {
		return jdbcManager.from(Bibliography.class)
			.innerJoin("authors")
			.innerJoin("authors.author")
			.where(new SimpleWhere()
				.eq("id", id)
			).getSingleResult();
	}

	/**
	 * @see org.tsukuba_bunko.lilac.service.BibliographyService#list(org.tsukuba_bunko.lilac.service.BookSearchCondition, int, int)
	 */
	@Override
	public SearchResult<Bibliography> list(BookSearchCondition condition, int offset, int limit) {
		SearchResult<Bibliography> result = new SearchResult<Bibliography>();
		result.count = count(condition);
		result.items = prepare(
				jdbcManager.from(Bibliography.class)
						.innerJoin("authors")
						.innerJoin("authors.author"),
				condition, offset, limit
			).getResultList();
		return result;
	}

	/**
	 * @see org.tsukuba_bunko.lilac.service.BibliographyService#count(org.tsukuba_bunko.lilac.service.BookSearchCondition)
	 */
	@Override
	public long count(BookSearchCondition condition) {
		SearchQuery query = createSearchQuery(condition);
		return jdbcManager.getCountBySql(query.sql, query.params.toArray());
	}

	public AutoSelect<Bibliography> prepare(AutoSelect<Bibliography> select, BookSearchCondition condition, int offset, int limit) {
		SearchQuery query = createSearchQuery(condition);

		StringBuilder sink = new StringBuilder();
		sink.append("id IN (SELECT __IDCOL FROM (");

		sink.append(query.sql);
		if(offset != -1) {
			sink.append(" OFFSET ");
			sink.append(offset);
			sink.append(" ROWS");
		}
		if(limit != -1) {
			sink.append(" FETCH FIRST ");
			sink.append(limit);
			sink.append(" ROWS ONLY");
		}

		sink.append(") SELECT_)");

		select.where(new String(sink), query.params.toArray());
		
		if(condition.orderBy == null || condition.orderBy.isEmpty()) {
			select.orderBy(BookSearchCondition.OrderBy.titleAsc.getS2JDBCExpression());
		}
		else {
			StringBuilder orderByClause = new StringBuilder();
			int count = 0;
			for(BookSearchCondition.OrderBy orderBy : condition.orderBy) {
				if(orderBy == null) {
					continue;
				}
				if(count != 0) {
					orderByClause.append(", ");
				}
				orderByClause.append(orderBy.getS2JDBCExpression());
				count++;
			}
			select.orderBy(new String(orderByClause));
		}

		return select;
	}

	class SearchQuery { 
		List<Object> params = new java.util.ArrayList<Object>();
		String sql;
	}
	protected SearchQuery createSearchQuery(BookSearchCondition condition) {
		SearchQuery query = new SearchQuery();

		List<String> fromClause = new java.util.ArrayList<String>();
		List<String> whereClause = new java.util.ArrayList<String>();

		fromClause.add("FROM bibliography AS s_b ");
		if(!StringUtil.isBlank(condition.label)) {
			whereClause.add("(s_b.label=?)");
			query.params.add(condition.label);
		}
		if(!StringUtil.isBlank(condition.keyword)) {
			whereClause.add("(s_b.title %% ? OR s_b.subtitle %% ?)");
			query.params.add(condition.keyword);
			query.params.add(condition.keyword);
		}
		if(!StringUtil.isBlank(condition.authorKeyword) || (condition.authorId != null)) {
			fromClause.add("JOIN bib_author AS s_ba ON s_ba.bibliography_id=s_b.\"id\" ");
			if(condition.authorId != null) {
				whereClause.add("(s_ba.author_id=?)");
				query.params.add(condition.authorId);				
			}
			else {
				fromClause.add("JOIN author AS s_a ON s_ba.author_id=s_a.\"id\" ");
				whereClause.add("(s_a.name %% ?)");
				query.params.add(condition.authorKeyword);
			}
		}
		if(condition.excludeRead) {
			whereClause.add("s_b.id IN (SELECT DISTINCT rr.bibliography_id FROM read_record AS rr WHERE rr.\"user\"=?)");
			query.params.add(condition.user);
		}
		
		StringBuilder sink = new StringBuilder();
		sink.append("SELECT DISTINCT s_b.\"id\" __IDCOL");
		if(condition.orderBy == null || condition.orderBy.isEmpty()) {
			sink.append(", s_b.title");
		}
		else {
			for(BookSearchCondition.OrderBy orderBy : condition.orderBy) {
				if(orderBy == null) {
					continue;
				}
				sink.append(", ");
				sink.append("s_b." + orderBy.getColumnName());
			}
		}
		sink.append(" ");

		for(String entry : fromClause) {
			sink.append(entry);
		}
		int count = 0;
		for(String entry : whereClause) {
			if(count == 0) {
				sink.append("WHERE ");
			}
			else {
				sink.append(" AND ");
			}
			sink.append(entry);
			count++;
		}

		sink.append(" ORDER BY ");
		if(condition.orderBy == null || condition.orderBy.isEmpty()) {
			sink.append(BookSearchCondition.OrderBy.titleAsc.getSql("s_b"));
		}
		else {
			count = 0;
			for(BookSearchCondition.OrderBy orderBy : condition.orderBy) {
				if(orderBy == null) {
					continue;
				}
				if(count > 0 ) {
					sink.append(", ");
				}
				sink.append(orderBy.getSql("s_b"));
				count++;
			}
		}

		query.sql = new String(sink);

		return query;
	}
}
