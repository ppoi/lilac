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

import java.text.SimpleDateFormat;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seasar.extension.jdbc.JdbcManager;
import org.seasar.framework.beans.util.Beans;
import org.seasar.framework.log.Logger;
import org.seasar.framework.unit.Seasar2;
import org.tsukuba_bunko.lilac.entity.AuthorRole;
import org.tsukuba_bunko.lilac.entity.ReadingRecord;
import org.tsukuba_bunko.lilac.service.ReadingRecordSearchCondition;
import org.tsukuba_bunko.lilac.service.SearchResult;

import static org.seasar.framework.unit.S2Assert.*;


/**
 * {@link ReadingRecordServiceImpl} のテストケース
 * @author ppoi
 * @version 2012.06
 */
@RunWith(Seasar2.class)
public class ReadingRecordServiceImplTest {

	@Resource
	private ReadingRecordServiceImpl service;

	@Resource
	private JdbcManager jdbcManager;

	@Test
	public void listAll() {
		printAllRecord();
		ReadingRecordSearchCondition condition = new ReadingRecordSearchCondition();
		SearchResult<ReadingRecord> result = service.list(condition, -1, -1);
		printSearchResult(result);
		assertEquals(6, result.count);
		assertEquals(6, result.items.size());
		assertEquals((Integer)390001, result.items.get(0).id);
		assertEquals("999-0000000001", result.items.get(0).bibliography.isbn);
		assertEquals(2, result.items.get(0).bibliography.authors.size());
		assertEquals(AuthorRole.author, result.items.get(0).bibliography.authors.get(0).authorRole);
		assertEquals("author1", result.items.get(0).bibliography.authors.get(0).author.name);
		assertEquals(AuthorRole.illustrator, result.items.get(0).bibliography.authors.get(1).authorRole);
		assertEquals("author2", result.items.get(0).bibliography.authors.get(1).author.name);
		assertEquals((Integer)390003, result.items.get(1).id);
		assertEquals((Integer)390002, result.items.get(2).id);
		assertEquals(3, result.items.get(2).bibliography.authors.size());
		assertEquals(AuthorRole.author, result.items.get(2).bibliography.authors.get(0).authorRole);
		assertEquals("author3", result.items.get(2).bibliography.authors.get(0).author.name);
		assertEquals(AuthorRole.illustrator, result.items.get(2).bibliography.authors.get(1).authorRole);
		assertEquals("author2", result.items.get(2).bibliography.authors.get(1).author.name);
		assertEquals(AuthorRole.illustrator, result.items.get(2).bibliography.authors.get(1).authorRole);
		assertEquals("author4", result.items.get(2).bibliography.authors.get(2).author.name);
		assertEquals((Integer)390004, result.items.get(3).id);
		assertEquals((Integer)390005, result.items.get(4).id);
		assertEquals((Integer)390006, result.items.get(5).id);
	}

	@Test
	public void listISBN() {
		ReadingRecordSearchCondition condition = new ReadingRecordSearchCondition();
		condition.isbn = "999-0000000001";
		SearchResult<ReadingRecord> result = service.list(condition, -1, -1);
		printSearchResult(result);
		assertEquals(3, result.count);
		assertEquals(3, result.items.size());
		assertEquals((Integer)390001, result.items.get(0).id);
		assertEquals((Integer)390003, result.items.get(1).id);
		assertEquals((Integer)390005, result.items.get(2).id);
	}

	@Test
	public void listReader() {
		ReadingRecordSearchCondition condition = new ReadingRecordSearchCondition();
		condition.reader = "user1";
		SearchResult<ReadingRecord> result = service.list(condition, -1, -1);
		printSearchResult(result);
		assertEquals(4, result.count);
		assertEquals(4, result.items.size());
		assertEquals((Integer)390001, result.items.get(0).id);
		assertEquals((Integer)390002, result.items.get(1).id);
		assertEquals((Integer)390004, result.items.get(2).id);
		assertEquals((Integer)390005, result.items.get(3).id);
	}

	@Test
	public void listCompletionDateBegin() throws Exception {
		ReadingRecordSearchCondition condition = new ReadingRecordSearchCondition();
		condition.completionDateBegin = new SimpleDateFormat("yyyy/MM/dd").parse("2012/09/02");
		SearchResult<ReadingRecord> result = service.list(condition, -1, -1);
		printSearchResult(result);
		assertEquals(4, result.count);
		assertEquals(4, result.items.size());
		assertEquals((Integer)390003, result.items.get(0).id);
		assertEquals((Integer)390002, result.items.get(1).id);
		assertEquals((Integer)390004, result.items.get(2).id);
		assertEquals((Integer)390005, result.items.get(3).id);
	}

	@Test
	public void listCompletionDateEnd() throws Exception {
		ReadingRecordSearchCondition condition = new ReadingRecordSearchCondition();
		condition.completionDateEnd = new SimpleDateFormat("yyyy/MM/dd").parse("2012/09/02");
		SearchResult<ReadingRecord> result = service.list(condition, -1, -1);
		printSearchResult(result);
		assertEquals(2, result.count);
		assertEquals(2, result.items.size());
		assertEquals((Integer)390001, result.items.get(0).id);
		assertEquals((Integer)390003, result.items.get(1).id);
	}

	@Test
	public void listCompletionDateBeginEnd() throws Exception {
		ReadingRecordSearchCondition condition = new ReadingRecordSearchCondition();
		condition.completionDateBegin = new SimpleDateFormat("yyyy/MM/dd").parse("2012/09/01");
		condition.completionDateEnd = new SimpleDateFormat("yyyy/MM/dd").parse("2012/09/30");
		SearchResult<ReadingRecord> result = service.list(condition, -1, -1);
		printSearchResult(result);
		assertEquals(3, result.count);
		assertEquals(3, result.items.size());
		assertEquals((Integer)390001, result.items.get(0).id);
		assertEquals((Integer)390003, result.items.get(1).id);
		assertEquals((Integer)390002, result.items.get(2).id);
	}

	@Test
	public void listCompletionDateBeginEndSame() throws Exception {
		ReadingRecordSearchCondition condition = new ReadingRecordSearchCondition();
		condition.completionDateBegin = new SimpleDateFormat("yyyy/MM/dd").parse("2012/09/01");
		condition.completionDateEnd = new SimpleDateFormat("yyyy/MM/dd").parse("2012/09/01");
		SearchResult<ReadingRecord> result = service.list(condition, -1, -1);
		printSearchResult(result);
		assertEquals(1, result.count);
		assertEquals(1, result.items.size());
		assertEquals((Integer)390001, result.items.get(0).id);
	}

	@Test
	public void listMultiCondition() throws Exception {
		ReadingRecordSearchCondition condition = new ReadingRecordSearchCondition();
		condition.isbn = "999-0000000001";
		condition.completionDateBegin = new SimpleDateFormat("yyyy/MM/dd").parse("2012/09/01");
		condition.completionDateEnd = new SimpleDateFormat("yyyy/MM/dd").parse("2012/09/30");
		condition.reader = "user1";
		SearchResult<ReadingRecord> result = service.list(condition, -1, -1);
		printSearchResult(result);
		assertEquals(1, result.count);
		assertEquals(1, result.items.size());
		assertEquals((Integer)390001, result.items.get(0).id);
	}

	@Test
	public void listOffset() {
		ReadingRecordSearchCondition condition = new ReadingRecordSearchCondition();
		SearchResult<ReadingRecord> result = service.list(condition, 2, -1);
		printSearchResult(result);
		assertEquals(6, result.count);
		assertEquals(4, result.items.size());
		assertEquals((Integer)390002, result.items.get(0).id);
		assertEquals((Integer)390004, result.items.get(1).id);
		assertEquals((Integer)390005, result.items.get(2).id);
		assertEquals((Integer)390006, result.items.get(3).id);
	}

	@Test
	public void listLimmit() {
		ReadingRecordSearchCondition condition = new ReadingRecordSearchCondition();
		SearchResult<ReadingRecord> result = service.list(condition, -1, 2);
		printSearchResult(result);
		assertEquals(6, result.count);
		assertEquals(2, result.items.size());
		assertEquals((Integer)390001, result.items.get(0).id);
		assertEquals((Integer)390003, result.items.get(1).id);
	}

	@Test
	public void listOffsetLimmit() {
		ReadingRecordSearchCondition condition = new ReadingRecordSearchCondition();
		SearchResult<ReadingRecord> result = service.list(condition, 2, 2);
		printSearchResult(result);
		assertEquals(6, result.count);
		assertEquals(2, result.items.size());
		assertEquals((Integer)390002, result.items.get(0).id);
		assertEquals((Integer)390004, result.items.get(1).id);
	}

	@Test
	public void listOffsetZero() {
		ReadingRecordSearchCondition condition = new ReadingRecordSearchCondition();
		SearchResult<ReadingRecord> result = service.list(condition, 0, -1);
		printSearchResult(result);
		assertEquals(6, result.count);
		assertEquals(6, result.items.size());
		assertEquals((Integer)390001, result.items.get(0).id);
		assertEquals((Integer)390003, result.items.get(1).id);
		assertEquals((Integer)390002, result.items.get(2).id);
		assertEquals((Integer)390004, result.items.get(3).id);
		assertEquals((Integer)390005, result.items.get(4).id);
		assertEquals((Integer)390006, result.items.get(5).id);
	}

	@Test
	public void get() {
		ReadingRecord entity = service.get(390002);
		assertNotNull(entity);
		assertEquals("999-0000000002", entity.bibliography.isbn);
		assertEquals(3, entity.bibliography.authors.size());
		assertEquals(AuthorRole.author, entity.bibliography.authors.get(0).authorRole);
		assertEquals("author3", entity.bibliography.authors.get(0).author.name);
		assertEquals(AuthorRole.illustrator, entity.bibliography.authors.get(1).authorRole);
		assertEquals("author2", entity.bibliography.authors.get(1).author.name);
		assertEquals(AuthorRole.illustrator, entity.bibliography.authors.get(1).authorRole);
		assertEquals("author4", entity.bibliography.authors.get(2).author.name);
	}

	@Test
	public void getNotFound() {
		ReadingRecord entity = service.get(393939);
		assertNull(entity);
	}

	@Test
	public void create() throws Exception {
		ReadingRecord entity = new ReadingRecord();
		entity.reader = "user1";
		entity.startDate = new SimpleDateFormat("yyyy/MM/dd").parse("2012/10/30");
		entity.completionDate = new SimpleDateFormat("yyyy/MM/dd").parse("2012/10/31");
		entity.bibliographyId = 390001;
		service.create(entity);
		assertNotNull(entity.id);
		assertNotNull(service.get(entity.id));
	}

	@Test
	public void createWithoutDate() throws Exception {
		ReadingRecord entity = new ReadingRecord();
		entity.reader = "user2";
		entity.bibliographyId = 390001;
		service.create(entity);
		assertNotNull(entity.id);
		assertNotNull(service.get(entity.id));
	}

	@Test
	public void update() throws Exception {
		ReadingRecord entity = service.get(390006);
		entity.startDate = new SimpleDateFormat("yyyy/MM/dd").parse("2012/10/30");
		entity.completionDate = new SimpleDateFormat("yyyy/MM/dd").parse("2012/10/31");
		service.update(entity);

		ReadingRecord refetched = service.get(390006);
		assertEquals(new SimpleDateFormat("yyyy/MM/dd").parse("2012/10/30"), refetched.startDate);
		assertEquals(new SimpleDateFormat("yyyy/MM/dd").parse("2012/10/31"), refetched.completionDate);
	}

	@Test
	public void delete() throws Exception {
		assertNotNull(service.get(390001));
		service.delete(390001);
		assertNull(service.get(390001));
	}

	private static final Logger log = Logger.getLogger(ReadingRecordServiceImplTest.class);
	private void printSearchResult(SearchResult<ReadingRecord> result) {
		StringBuilder sink = new StringBuilder();
		sink.append("count=");
		sink.append(result.count);
		sink.append(", ");
		sink.append("[");
		int itemsLength = result.items.size();
		for(int i = 0; i < itemsLength; ++i) {
			ReadingRecord entity = result.items.get(i);
			if(i > 0) {
				sink.append(",");
			}
			sink.append(entity.id);
		}
		sink.append("]");
		log.debug(new String(sink));
	}
	private void printAllRecord() {
		StringBuilder sink = new StringBuilder();
		for(ReadingRecord entity : jdbcManager.from(ReadingRecord.class).orderBy("completionDate ASC").getResultList()) {
			sink.append(Beans.createAndCopy(java.util.HashMap.class, entity).execute().toString());
			sink.append(", ");
		}
		log.debug(new String(sink));
	}
}
