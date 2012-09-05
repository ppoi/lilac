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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seasar.framework.unit.Seasar2;
import org.tsukuba_bunko.lilac.entity.Bibliography;
import org.tsukuba_bunko.lilac.service.BookSearchCondition;
import org.tsukuba_bunko.lilac.service.BookSearchCondition.OrderBy;
import org.tsukuba_bunko.lilac.service.SearchResult;

import static org.seasar.framework.unit.S2Assert.*;


/**
 * {@link BibliographyServiceImpl}のテストケース
 * @author ppoi
 * @version 2012.05
 */
@RunWith(Seasar2.class)
public class BibliographyServiceImplTest {

	@Resource
	private BibliographyServiceImpl service;

	@Test
	public void listWithSynonym() {
		BookSearchCondition condition = new BookSearchCondition();
		condition.keyword = "著者ABC";
		condition.orderBy = java.util.Arrays.asList(OrderBy.titleAsc);
		SearchResult<Bibliography> result = service.list(condition, 0, 100);
		assertNotNull(result);
		assertEquals(3, result.count);
		assertEquals((Integer)390001, result.items.get(0).id);
		assertEquals((Integer)390002, result.items.get(1).id);
		assertEquals((Integer)390003, result.items.get(2).id);
	}

	@Test
	public void listWithoutSynonym() {
		BookSearchCondition condition = new BookSearchCondition();
		condition.keyword = "著者DEF";
		condition.orderBy = java.util.Arrays.asList(OrderBy.titleAsc);
		SearchResult<Bibliography> result = service.list(condition, 0, 100);
		assertNotNull(result);
		assertEquals(2, result.count);
		assertEquals((Integer)390001, result.items.get(0).id);
		assertEquals((Integer)390002, result.items.get(1).id);
	}
}
