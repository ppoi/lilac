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

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.seasar.extension.jdbc.JdbcManager;
import org.seasar.extension.jdbc.where.SimpleWhere;
import org.seasar.framework.beans.util.Beans;
import org.seasar.framework.log.Logger;
import org.seasar.framework.util.StringUtil;
import org.tsukuba_bunko.lilac.entity.Bibliography;
import org.tsukuba_bunko.lilac.entity.Book;
import org.tsukuba_bunko.lilac.entity.Bookshelf;


/**
 * @author $Author: $
 * @version $Revision: $ $Date: $
 */
public class ImportBookHelperImpl extends ImportDataHelperBase {

	private static final Logger log = Logger.getLogger(ImportBookHelperImpl.class);

	protected static final Map<String, String> propertyNameMap = new java.util.HashMap<String, String>();
	static {
		propertyNameMap.put("S", "status");
		propertyNameMap.put("ID", "id");
		propertyNameMap.put("ISBN", "isbn");
		propertyNameMap.put("書棚", "bookshelf");
		propertyNameMap.put("購入日", "acquisitionDate");
		propertyNameMap.put("購入店舗", "purchaseShop");
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

	private static final Pattern BOOKSHELF_PATTERN = Pattern.compile("(?:\\((\\d+)\\))?(.+)");
	protected Bookshelf parseBookshelf(String entry) {
		Matcher matcher = BOOKSHELF_PATTERN.matcher(entry);
		if(!matcher.matches()) {
			log.log("EAPI2902", new Object[]{entry});
			return null;
		}

		Bookshelf bookshelf = new Bookshelf();
		if(matcher.group(1) != null) {
			bookshelf.id = Integer.parseInt(matcher.group(1));
		}
		bookshelf.label = matcher.group(2).trim();
		return bookshelf;
	}

	protected Book recordToEntity(Map<String, String> record) {
		Book entity = new Book();
		Beans.copy(record, entity).excludes("isbn", "bookshelf")
			.dateConverter("yyyy/MM/dd", "acquisitionDate")
			.excludesNull().execute();

		if(StringUtil.isNotBlank(record.get("bookshelf"))) {
			Bookshelf bookshelf = parseBookshelf(record.get("bookshelf"));
			if(bookshelf == null) {
				throw new IllegalArgumentException(record.toString());
			}
			entity.location = jdbcManager.from(Bookshelf.class)
					.where(new SimpleWhere()
						.eq("id", bookshelf.id)
						.eq("label", bookshelf.label)
					).disallowNoResult().getSingleResult();
			entity.locationId = entity.location.id;
		}
		entity.bibliography = jdbcManager.from(Bibliography.class)
				.where(new SimpleWhere()
					.eq("isbn", record.get("isbn"))
				).disallowNoResult().getSingleResult();
		entity.bibliographyId = entity.bibliography.id;
		return entity;
	}

	/**
	 * @see org.tsukuba_bunko.lilac.helper.port.impl.ImportDataHelperBase#insertRecord(java.util.Map)
	 */
	@Override
	protected void insertRecord(Map<String, String> record) {
		Book entity = recordToEntity(record);
		entity.id = null;
		entity.owner = getCurrentSessionUser();
		jdbcManager.insert(entity).execute();
	}

	/**
	 * @see org.tsukuba_bunko.lilac.helper.port.impl.ImportDataHelperBase#updateRecord(java.util.Map)
	 */
	@Override
	protected void updateRecord(Map<String, String> record) {
		Book entity = recordToEntity(record);
		entity.owner = getCurrentSessionUser();
		jdbcManager.update(entity).execute();
	}

	/**
	 * @see org.tsukuba_bunko.lilac.helper.port.impl.ImportDataHelperBase#deleteRecord(java.util.Map)
	 */
	@Override
	protected void deleteRecord(Map<String, String> record) {
		Book entity = recordToEntity(record);
		jdbcManager.delete(entity).execute();
	}

}
