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
 *
 * $Id:　$
 */
package org.tsukuba_bunko.lilac.helper.port.impl;

import java.text.SimpleDateFormat;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seasar.framework.unit.Seasar2;
import org.tsukuba_bunko.lilac.entity.AuthorRole;
import org.tsukuba_bunko.lilac.entity.BibAuthor;
import org.tsukuba_bunko.lilac.entity.Bibliography;

import static org.seasar.framework.unit.S2Assert.*;


/**
 * @author $Author: $
 * @version $Revision: $ $Date: $
 */
@RunWith(Seasar2.class)
public class ImportBibliographyHelperImplTest {

	private ImportBibliographyHelperImpl helper;

	@Test
	public void testRecordToBook() {
		Map<String, String> record = new java.util.HashMap<String, String>();
		record.put("status", "M");
		record.put("id", "39");
		record.put("isbn", "827-3939393939");
		record.put("title", "miku");
		record.put("subtitle", null);
		record.put("price", "390");
		record.put("publicationDate", "2007/08/27");
		record.put("authors", "(170)[author]むらさきゆきや\r\n(169)[illustrator]むにゅう\r\n(104)[illustrator]しゅがーピコラ");
		record.put("bookshelfs", "[ppoi]S99");

		Bibliography bibliography = helper.recordToEntity(record);
		assertNotNull("bibliography", bibliography);
		assertEquals("id", (Integer)39, bibliography.id);
		assertEquals("isbn", "827-3939393939", bibliography.isbn);
		assertEquals("title", "miku", bibliography.title);
		assertNull("subtitle", bibliography.subtitle);
		assertEquals("price", (Integer)390, bibliography.price);
		assertEquals("isbn", "2007/08/27", new SimpleDateFormat("yyyy/MM/dd").format(bibliography.publicationDate));

		assertNotNull(bibliography.authors);
		assertEquals(3, bibliography.authors.size());
		BibAuthor bibauthor = bibliography.authors.get(0);
		assertEquals(AuthorRole.author, bibauthor.authorRole);
		assertNotNull(bibauthor.author);
		assertEquals((Integer)170, bibauthor.author.id);
		assertEquals("むらさきゆきや", bibauthor.author.name);
		bibauthor = bibliography.authors.get(1);
		assertEquals(AuthorRole.illustrator, bibauthor.authorRole);
		assertNotNull(bibauthor.author);
		assertEquals((Integer)169, bibauthor.author.id);
		assertEquals("むにゅう", bibauthor.author.name);
		bibauthor = bibliography.authors.get(2);
		assertEquals(AuthorRole.illustrator, bibauthor.authorRole);
		assertNotNull(bibauthor.author);
		assertEquals((Integer)104, bibauthor.author.id);
		assertEquals("しゅがーピコラ", bibauthor.author.name);
	}

	@Test
	public void testRecordToBook_noauthorid() {
		Map<String, String> record = new java.util.HashMap<String, String>();
		record.put("status", "M");
		record.put("id", "39");
		record.put("isbn", "827-3939393939");
		record.put("title", "miku");
		record.put("subtitle", "negi");
		record.put("price", "390");
		record.put("publicationDate", "2007/08/27");
		record.put("authors", "[author]むらさきゆきや\r\n(169)[illustrator]むにゅう\r\n[illustrator]しゅがーピコラ");
		record.put("bookshelfs", "[ppoi]S99");

		Bibliography bibliography = helper.recordToEntity(record);
		assertNotNull("bibliography", bibliography);
		assertEquals("id", (Integer)39, bibliography.id);
		assertEquals("isbn", "827-3939393939", bibliography.isbn);
		assertEquals("title", "miku", bibliography.title);
		assertEquals("subtitle", "negi", bibliography.subtitle);
		assertEquals("price", (Integer)390, bibliography.price);
		assertEquals("isbn", "2007/08/27", new SimpleDateFormat("yyyy/MM/dd").format(bibliography.publicationDate));

		assertNotNull(bibliography.authors);
		assertEquals(3, bibliography.authors.size());
		BibAuthor bibauthor = bibliography.authors.get(0);
		assertEquals(AuthorRole.author, bibauthor.authorRole);
		assertNotNull(bibauthor.author);
		assertNull(bibauthor.author.id);
		assertEquals("むらさきゆきや", bibauthor.author.name);
		bibauthor = bibliography.authors.get(1);
		assertEquals(AuthorRole.illustrator, bibauthor.authorRole);
		assertNotNull(bibauthor.author);
		assertEquals((Integer)169, bibauthor.author.id);
		assertEquals("むにゅう", bibauthor.author.name);
		bibauthor = bibliography.authors.get(2);
		assertEquals(AuthorRole.illustrator, bibauthor.authorRole);
		assertNotNull(bibauthor.author);
		assertNull(bibauthor.author.id);
		assertEquals("しゅがーピコラ", bibauthor.author.name);
	}

	@Test
	public void testRecordToBook_authorRoles() {
		Map<String, String> record = new java.util.HashMap<String, String>();
		record.put("status", "M");
		record.put("id", "39");
		record.put("isbn", "827-3939393939");
		record.put("title", "miku");
		record.put("subtitle", "negi");
		record.put("price", "390");
		record.put("publicationDate", "2007/08/27");
		record.put("authors", "[author]むらさきゆきや\r\n(169)[illustrator]むにゅう\r\n[originator]原作C\r\n[editor]編集A\r\n[supervisor]監修B");
		record.put("bookshelfs", "[ppoi]S99");

		Bibliography bibliography = helper.recordToEntity(record);
		assertNotNull("bibliography", bibliography);
		assertEquals("id", (Integer)39, bibliography.id);
		assertEquals("isbn", "827-3939393939", bibliography.isbn);
		assertEquals("title", "miku", bibliography.title);
		assertEquals("subtitle", "negi", bibliography.subtitle);
		assertEquals("price", (Integer)390, bibliography.price);
		assertEquals("isbn", "2007/08/27", new SimpleDateFormat("yyyy/MM/dd").format(bibliography.publicationDate));

		assertNotNull(bibliography.authors);
		assertEquals(5, bibliography.authors.size());
		BibAuthor bibauthor = bibliography.authors.get(0);
		assertEquals(AuthorRole.author, bibauthor.authorRole);
		assertNotNull(bibauthor.author);
		assertNull(bibauthor.author.id);
		assertEquals("むらさきゆきや", bibauthor.author.name);
		bibauthor = bibliography.authors.get(1);
		assertEquals(AuthorRole.illustrator, bibauthor.authorRole);
		assertNotNull(bibauthor.author);
		assertEquals((Integer)169, bibauthor.author.id);
		assertEquals("むにゅう", bibauthor.author.name);
		bibauthor = bibliography.authors.get(2);
		assertEquals(AuthorRole.originator, bibauthor.authorRole);
		assertNotNull(bibauthor.author);
		assertNull(bibauthor.author.id);
		assertEquals("原作C", bibauthor.author.name);
		bibauthor = bibliography.authors.get(3);
		assertEquals(AuthorRole.editor, bibauthor.authorRole);
		assertNotNull(bibauthor.author);
		assertNull(bibauthor.author.id);
		assertEquals("編集A", bibauthor.author.name);
		bibauthor = bibliography.authors.get(4);
		assertEquals(AuthorRole.supervisor, bibauthor.authorRole);
		assertNotNull(bibauthor.author);
		assertNull(bibauthor.author.id);
		assertEquals("監修B", bibauthor.author.name);
	}

}
