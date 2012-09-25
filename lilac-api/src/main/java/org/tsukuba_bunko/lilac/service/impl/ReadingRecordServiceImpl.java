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

import java.util.List;

import javax.annotation.Resource;

import org.seasar.extension.jdbc.JdbcManager;
import org.seasar.extension.jdbc.where.SimpleWhere;
import org.seasar.framework.util.StringUtil;
import org.tsukuba_bunko.lilac.entity.ReadingRecord;
import org.tsukuba_bunko.lilac.service.ReadingRecordSearchCondition;
import org.tsukuba_bunko.lilac.service.ReadingRecordService;
import org.tsukuba_bunko.lilac.service.SearchResult;


/**
 * {@link ReadingRecordService} 実装
 * @author ppoi
 * @version 2012.06
 */
public class ReadingRecordServiceImpl implements ReadingRecordService {

	@Resource
	public JdbcManager jdbcManager;

	/**
	 * @see org.tsukuba_bunko.lilac.service.ReadingRecordService#list(org.tsukuba_bunko.lilac.service.ReadingRecordSearchCondition, int, int)
	 */
	@Override
	public SearchResult<ReadingRecord> list(ReadingRecordSearchCondition condition, int offset, int limit) {
		SearchResult<ReadingRecord> result = new SearchResult<ReadingRecord>();
		result.count = jdbcManager.from(ReadingRecord.class)
				.innerJoin("bibliography", false)
				.where(new SimpleWhere()
					.eq("bibliography.isbn", condition.isbn)
					.ge("completionDate", condition.completionDateBegin)
					.le("completionDate", condition.completionDateEnd)
					.eq("reader", condition.reader)
				).getCount();

		List<String> fromClause = new java.util.ArrayList<String>();
		List<String> whereClause = new java.util.ArrayList<String>();
		List<Object> params = new java.util.ArrayList<Object>();
		fromClause.add("FROM reading_record AS s_r ");
		if(StringUtil.isNotBlank(condition.isbn)) {
			fromClause.add("JOIN \"bibliography\" AS s_b ON s_r.bibliography_id=s_b.id ");
			whereClause.add("s_b.isbn=?");
			params.add(condition.isbn);
		}
		if(condition.completionDateBegin != null) {
			whereClause.add("s_r.completion_date>=?");
			params.add(condition.completionDateBegin);
		}
		if(condition.completionDateEnd != null) {
			whereClause.add("s_r.completion_date<=?");
			params.add(condition.completionDateEnd);
		}
		if(StringUtil.isNotBlank(condition.reader)) {
			whereClause.add("s_r.reader=?");
			params.add(condition.reader);
		}

		StringBuilder query = new StringBuilder();
		query.append("id IN (SELECT RRID_ FROM (SELECT DISTINCT s_r.id RRID_, s_r.completion_date ");
		for(String part : fromClause) {
			query.append(part);
		}

		int whereClauseSize = whereClause.size();
		for(int i = 0; i < whereClauseSize; ++i) {
			if(i == 0) {
				query.append("WHERE ");
			}
			else {
				query.append(" AND ");
			}
			query.append('(');
			query.append(whereClause.get(i));
			query.append(')');
		}

		query.append(" ORDER BY s_r.completion_date ASC, s_r.id ASC");
		if(offset != -1) {
			query.append(" OFFSET ");
			query.append(offset);
			query.append(" ROWS");
		}
		if(limit != -1) {
			query.append(" FETCH FIRST ");
			query.append(limit);
			query.append(" ROWS ONLY");
		}
		query.append(") SELECTED_)");

		result.items = jdbcManager.from(ReadingRecord.class)
				.innerJoin("bibliography")
				.leftOuterJoin("bibliography.authors")
				.leftOuterJoin("bibliography.authors.author")
				.where(query.toString(), params.toArray())
				.orderBy("completionDate ASC, id ASC, bibliography.authors.authorRole ASC, bibliography.authors.author.name ASC")
				.getResultList();
		result.limit = limit;
		result.offset = offset;
		return result;
	}

	/**
	 * @see org.tsukuba_bunko.lilac.service.ReadingRecordService#get(int)
	 */
	@Override
	public ReadingRecord get(int id) {
		return jdbcManager.from(ReadingRecord.class)
				.leftOuterJoin("bibliography")
				.leftOuterJoin("bibliography.authors")
				.leftOuterJoin("bibliography.authors.author")
				.orderBy("bibliography.authors.authorRole ASC, bibliography.authors.author.name ASC")
				.where("id=?", id).getSingleResult();
	}

	/**
	 * @see org.tsukuba_bunko.lilac.service.ReadingRecordService#create(org.tsukuba_bunko.lilac.entity.ReadingRecord)
	 */
	@Override
	public void create(ReadingRecord entity) {
		jdbcManager.insert(entity).execute();
	}

	/**
	 * @see org.tsukuba_bunko.lilac.service.ReadingRecordService#update(org.tsukuba_bunko.lilac.entity.ReadingRecord)
	 */
	@Override
	public void update(ReadingRecord entity) {
		jdbcManager.update(entity).execute();
	}

	/**
	 * @see org.tsukuba_bunko.lilac.service.ReadingRecordService#delete(int)
	 */
	@Override
	public void delete(int id) {
		jdbcManager.updateBySql("DELETE FROM reading_record WHERE id=?", Integer.class)
			.params(id).execute();
	}

}
