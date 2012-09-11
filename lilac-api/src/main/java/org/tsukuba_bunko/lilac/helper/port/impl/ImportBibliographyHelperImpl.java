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
package org.tsukuba_bunko.lilac.helper.port.impl;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.seasar.extension.jdbc.JdbcManager;
import org.seasar.extension.jdbc.where.SimpleWhere;
import org.seasar.framework.beans.util.Beans;
import org.seasar.framework.log.Logger;
import org.seasar.framework.util.StringUtil;
import org.tsukuba_bunko.lilac.entity.Author;
import org.tsukuba_bunko.lilac.entity.AuthorRole;
import org.tsukuba_bunko.lilac.entity.BibAuthor;
import org.tsukuba_bunko.lilac.entity.Bibliography;
import org.tsukuba_bunko.lilac.entity.Label;
import org.tsukuba_bunko.lilac.helper.port.ImportDataHelper;


/**
 * 書誌情報インポート用 {@link ImportDataHelper} 実装
 * @author ppoi
 * @version 2012.05
 */
public class ImportBibliographyHelperImpl extends ImportDataHelperBase {

	private static final Logger log = Logger.getLogger(ImportBibliographyHelperImpl.class);

	protected static final Map<String, String> propertyNameMap = new java.util.HashMap<String, String>();
	static {
		propertyNameMap.put("S", "status");
		propertyNameMap.put("ID", "id");
		propertyNameMap.put("ISBN", "isbn");
		propertyNameMap.put("レーベル", "label");
		propertyNameMap.put("タイトル", "title");
		propertyNameMap.put("サブタイトル", "subtitle");
		propertyNameMap.put("著者", "authors");
		propertyNameMap.put("定価", "price");
		propertyNameMap.put("出版日", "publicationDate");
		propertyNameMap.put("備考", "note");
	}

	@Resource
	public JdbcManager jdbcManager;

	/**
	 * @see org.tsukuba_bunko.lilac.helper.port.impl.ImportDataHelperBase#getPropertyNameMap()
	 */
	@Override
	protected Map<String, String> getPropertyNameMap() {
		return propertyNameMap;
	}

	private static final Pattern AUTHOR_PATTERN = Pattern.compile("(?:\\((\\d+)\\))?\\[(\\w+)\\](.+)");
	public Bibliography recordToEntity(Map<String, String> record) {
		Bibliography bibliography = new Bibliography();
		Beans.copy(record, bibliography).excludes("authors", "bookshelfs")
				.dateConverter("yyyy/MM/dd", "publicationDate")
				.excludesNull().execute();

		bibliography.authors = new java.util.ArrayList<BibAuthor>();
		String authors = record.get("authors");
		if(StringUtil.isNotBlank(authors)) {
			for(String line : authors.split("[\\r\\n]+")) {
				if(StringUtil.isBlank(line)) {
					log.log("WAPI2901", new Object[]{line});
					continue;
				}
				Matcher m = AUTHOR_PATTERN.matcher(line);
				if(m.matches()) {
					BibAuthor bibauthor = new BibAuthor();
					bibauthor.authorRole = AuthorRole.valueOf(m.group(2));
					if(StringUtil.isNotBlank(m.group(1))) {
						bibauthor.authorId = Integer.parseInt(m.group(1));
					}
					bibauthor.author = new Author();
					bibauthor.author.id = bibauthor.authorId;
					bibauthor.author.name = m.group(3);
					bibliography.authors.add(bibauthor);
				}
			}
		}

		return bibliography;
	}

	protected void ensureLabel(Bibliography bibliography) {
		Label label = jdbcManager.from(Label.class)
								.where(new SimpleWhere()
									.eq("name", bibliography.label)
								).getSingleResult();
		if(label == null) {
			label = new Label();
			label.name = bibliography.label;
			jdbcManager.insert(label).execute();
		}
	}

	protected void registerBibAuthors(Bibliography bibliography) {
		jdbcManager.updateBySql("DELETE FROM bib_author WHERE bibliography_id=?", Integer.class)
				.params(bibliography.id)
				.execute();
		for(BibAuthor bibauthor : bibliography.authors) {
			List<Author> current = jdbcManager.from(Author.class)
					.where(new SimpleWhere()
						.eq("id", bibauthor.author.id)
						.eq("name", bibauthor.author.name)
					).getResultList();

			if(current.isEmpty()) {
				jdbcManager.insert(bibauthor.author).excludesNull().execute();
				bibauthor.authorId = bibauthor.author.id;
			}
			else {
				bibauthor.authorId = current.get(0).id;
			}
			bibauthor.bibliographyId = bibliography.id;
		}
		jdbcManager.insertBatch(bibliography.authors).execute();
	}

	/**
	 * @see org.tsukuba_bunko.lilac.helper.port.impl.ImportDataHelperBase#insertRecord(java.util.Map)
	 */
	@Override
	protected void insertRecord(Map<String, String> record) {
		Bibliography bibliography = recordToEntity(record);

		ensureLabel(bibliography);
		jdbcManager.insert(bibliography).execute();
		registerBibAuthors(bibliography);
	}

	/**
	 * @see org.tsukuba_bunko.lilac.helper.port.impl.ImportDataHelperBase#updateRecord(java.util.Map)
	 */
	@Override
	protected void updateRecord(Map<String, String> record) {
		Bibliography bibliography = recordToEntity(record);
		Bibliography current = jdbcManager.from(Bibliography.class)
									.where(new SimpleWhere()
										.eq("id", bibliography.id)
									).disallowNoResult().getSingleResult();
		bibliography.version = current.version;

		ensureLabel(bibliography);
		jdbcManager.update(bibliography).execute();
		registerBibAuthors(bibliography);
	}

	/**
	 * @see org.tsukuba_bunko.lilac.helper.port.impl.ImportDataHelperBase#deleteRecord(java.util.Map)
	 */
	@Override
	protected void deleteRecord(Map<String, String> record) {
		Bibliography bibliography = recordToEntity(record);
		Bibliography current = jdbcManager.from(Bibliography.class)
				.where(new SimpleWhere()
					.eq("id", bibliography.id)
				).getSingleResult();
		if(current != null) {
			bibliography.version = current.version;
			jdbcManager.delete(bibliography).execute();
		}
	}

}
