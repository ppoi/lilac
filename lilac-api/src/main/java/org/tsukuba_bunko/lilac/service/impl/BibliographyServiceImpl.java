/*
 * All Rights Reserved.
 * Copyright (C) 2011-2012 Tsukuba Bunko.
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

import java.util.List;

import javax.annotation.Resource;

import org.seasar.extension.jdbc.AutoSelect;
import org.seasar.extension.jdbc.JdbcManager;
import org.seasar.framework.util.StringUtil;
import org.tsukuba_bunko.lilac.entity.Bibliography;
import org.tsukuba_bunko.lilac.service.BibliographyService;
import org.tsukuba_bunko.lilac.service.BookSearchCondition;
import org.tsukuba_bunko.lilac.service.BookSearchCondition.OrderBy;
import org.tsukuba_bunko.lilac.service.SearchResult;


/**
 * {@link BibliographyService} 実装
 * @author ppoi
 * @version 2012.05
 */
public class BibliographyServiceImpl implements BibliographyService {

	public static final List<OrderBy> DEFAULT_ORDERBY_LIST = java.util.Arrays.asList(OrderBy.titleAsc, OrderBy.publicationDateAsc);

	@Resource
	public JdbcManager jdbcManager;

	/**
	 * @see org.tsukuba_bunko.lilac.service.BibliographyService#get(int)
	 */
	@Override
	public Bibliography get(int id) {
		return get(id, false);
	}

	/**
	 * @see org.tsukuba_bunko.lilac.service.BibliographyService#get(int, boolean)
	 */
	@Override
	public Bibliography get(int id, boolean withBooks) {
		AutoSelect<Bibliography> select = jdbcManager.from(Bibliography.class)
				.innerJoin("authors")
				.innerJoin("authors.author");
		if(withBooks) {
			select.leftOuterJoin("books")
				.leftOuterJoin("books.location");
		}
		return select.where("id=?", id).getSingleResult();
	}

	/**
	 * @see org.tsukuba_bunko.lilac.service.BibliographyService#list(org.tsukuba_bunko.lilac.service.BookSearchCondition, int, int)
	 */
	@Override
	public SearchResult<Bibliography> list(BookSearchCondition condition, int offset, int limit) {
		SearchResult<Bibliography> result = new SearchResult<Bibliography>();
		result.count = count(condition);

		AutoSelect<Bibliography> select = jdbcManager.from(Bibliography.class)
				.innerJoin("authors")
				.innerJoin("authors.author");
		if(isIncludeBookProperty(condition)) {
			select.leftOuterJoin("books");
		}
		result.items = query(select, condition, offset, limit).getResultList();

		return result;
	}

	/**
	 * @see org.tsukuba_bunko.lilac.service.BibliographyService#count(org.tsukuba_bunko.lilac.service.BookSearchCondition)
	 */
	@Override
	public long count(BookSearchCondition condition) {
		SearchQuery query = createSearchQuery(condition, false);
		return jdbcManager.getCountBySql(query.sql, query.params.toArray());
	}

	public AutoSelect<Bibliography> query(AutoSelect<Bibliography> select, BookSearchCondition condition, int offset, int limit) {
		if(condition.orderBy == null || condition.orderBy.isEmpty()) {
			condition.orderBy = DEFAULT_ORDERBY_LIST;
		}

		SearchQuery query = createSearchQuery(condition, true);

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

		StringBuilder orderByClause = new StringBuilder();
		int count = 0;
		for(OrderBy orderBy : condition.orderBy) {
			if(orderBy == null) {
				continue;
			}
			if(count != 0) {
				orderByClause.append(", ");
			}
			if(orderBy == OrderBy.acquisitionDateAsc || orderBy == OrderBy.acquisitionDateDesc) {
				orderByClause.append(orderBy.getS2JDBCExpression("books"));
			}
			else {
				orderByClause.append(orderBy.getS2JDBCExpression());
			}
			count++;
		}
		select.orderBy(new String(orderByClause));

		return select;
	}

	class SearchQuery { 
		List<Object> params = new java.util.ArrayList<Object>();
		String sql;
	}
	protected SearchQuery createSearchQuery(BookSearchCondition condition, boolean withOrderByClause) {
		SearchQuery query = new SearchQuery();

		List<String> selectClause = new java.util.ArrayList<String>();
		List<String> fromClause = new java.util.ArrayList<String>();
		List<String> whereClause = new java.util.ArrayList<String>();
		List<String> orderByClause = new java.util.ArrayList<String>();

		fromClause.add("FROM bibliography AS s_b ");
		if(!StringUtil.isBlank(condition.label)) {
			whereClause.add("(s_b.label=?)");
			query.params.add(condition.label);
		}
		if(condition.publicationDateBegin != null) {
			whereClause.add("(s_b.publication_date>=?)");
			query.params.add(condition.publicationDateBegin);
		}
		if(condition.publicationDateEnd != null) {
			whereClause.add("(s_b.publication_date<=?)");
			query.params.add(condition.publicationDateEnd);
		}
		if(!StringUtil.isBlank(condition.keyword)) {
			fromClause.add("JOIN bib_author AS s_ba ON s_ba.bibliography_id=s_b.id ");
			whereClause.add("(s_b.title %% ? OR s_b.subtitle %% ? OR s_ba.author_id IN (SELECT DISTINCT s_a.id FROM author AS s_a JOIN author AS s_as ON s_a.id=s_as.id OR (s_as.synonym_key!=0 AND s_as.synonym_key=s_a.synonym_key) WHERE s_as.name %% ?))");
			query.params.add(condition.keyword);
			query.params.add(condition.keyword);
			query.params.add(condition.keyword);
		}
		if(condition.authorId != null) {
			if(StringUtil.isBlank(condition.keyword)) {
				fromClause.add("JOIN bib_author AS s_ba ON s_ba.bibliography_id=s_b.id ");
			}
			whereClause.add("(s_ba.author_id=?)");
			query.params.add(condition.authorId);				
		}
		if(isIncludeBookProperty(condition)) {
			fromClause.add("JOIN book AS s_bk ON s_b.id=s_bk.bibliography_id ");
			if(condition.acquisitionDateBegin != null) {
				whereClause.add("(s_bk.acquisition_date>=?)");
				query.params.add(condition.acquisitionDateBegin);
			}
			if(condition.acquisitionDateEnd != null) {
				whereClause.add("(s_bk.acquisition_date<=?)");
				query.params.add(condition.acquisitionDateEnd);
			}
		}
		if(condition.excludeRead) {
			whereClause.add("s_b.id IN (SELECT DISTINCT rr.bibliography_id FROM read_record AS rr WHERE rr.reader=?)");
			query.params.add(condition.owner);
		}

		if(withOrderByClause) {
			for(OrderBy orderBy : condition.orderBy) {
				if(orderBy == null) {
					continue;
				}
				if(orderBy == OrderBy.acquisitionDateAsc || orderBy == OrderBy.acquisitionDateDesc) {
					selectClause.add("s_bk." + orderBy.getColumnName());
					orderByClause.add(orderBy.getSql("s_bk"));
				}
				else {
					selectClause.add("s_b." + orderBy.getColumnName());
					orderByClause.add(orderBy.getSql("s_b"));
				}
			}
		}

		StringBuilder sink = new StringBuilder();

		sink.append("SELECT DISTINCT s_b.id __IDCOL");
		for(String column : selectClause) {
			sink.append(", ");
			sink.append(column);
		}

		sink.append(" ");
		for(String entry : fromClause) {
			sink.append(entry);
		}

		int whereClauseSize = whereClause.size();
		for(int i = 0; i < whereClauseSize; ++i) {
			if(i == 0) {
				sink.append("WHERE ");
			}
			else {
				sink.append(" AND ");
			}
			sink.append(whereClause.get(i));
		}

		int orderByClauseSize = orderByClause.size();
		if(orderByClauseSize > 0) {
			for(int i = 0; i < orderByClauseSize; ++i) {
				if(i == 0) {
					sink.append(" ORDER BY ");
				}
				else {
					sink.append(", ");
				}
				sink.append(orderByClause.get(i));
			}
		}

		query.sql = new String(sink);

		return query;
	}

	protected boolean isIncludeBookProperty(BookSearchCondition condition) {
		if(condition.acquisitionDateBegin != null) {
			return true;
		}
		else if(condition.acquisitionDateEnd != null) {
			return true;
		}
		else if(condition.owner != null) {
			return true;
		}
		else if(condition.orderBy == null || condition.orderBy.isEmpty()) {
			return false;
		}
		else if(condition.orderBy.contains(OrderBy.acquisitionDateAsc)) {
			return true;
		}
		else if(condition.orderBy.contains(OrderBy.acquisitionDateDesc)) {
			return true;
		}
		else {
			return false;
		}
	}
}
