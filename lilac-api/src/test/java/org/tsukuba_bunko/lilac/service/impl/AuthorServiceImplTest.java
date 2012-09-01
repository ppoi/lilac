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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seasar.framework.unit.Seasar2;
import org.tsukuba_bunko.lilac.entity.Author;

import static org.seasar.framework.unit.S2Assert.*;


/**
 * {@link AuthorServiceImpl}のテストケース
 * @author ppoi
 * @version 2012.05
 */
@RunWith(Seasar2.class)
public class AuthorServiceImplTest {

	@Resource
	private AuthorServiceImpl service;

	@Test
	public void synonym_0() {
		Author author = service.list("著者C").get(0);
		assertNotNull(author);

		List<Author> synonym = service.synonym(author.id);
		assertNotNull(synonym);
		assertEquals(0, synonym.size());
	}

	@Test
	public void synonym_1() {
		Author author = service.list("著者B").get(0);
		assertNotNull(author);

		List<Author> synonym = service.synonym(author.id);
		assertNotNull(synonym);
		assertEquals(1, synonym.size());
		Author synonymAuthor = synonym.get(0);
		assertEquals("著者B'", synonymAuthor.name);
		assertEquals("websiteB", synonymAuthor.website);
		assertEquals("twitterB", synonymAuthor.twitter);
		assertEquals("noteB", synonymAuthor.note);
		assertEquals(author.synonymKey, synonymAuthor.synonymKey);
	}

	@Test
	public void synonym_3() {
		Author author = service.list("著者A").get(0);
		assertNotNull(author);

		List<Author> synonym = service.synonym(author.id);
		assertNotNull(synonym);
		assertEquals(2, synonym.size());
	}

}
