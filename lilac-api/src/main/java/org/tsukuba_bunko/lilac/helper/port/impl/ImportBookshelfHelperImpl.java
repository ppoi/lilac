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

import javax.annotation.Resource;

import org.seasar.extension.jdbc.JdbcManager;
import org.seasar.framework.beans.util.Beans;
import org.tsukuba_bunko.lilac.entity.Bookshelf;


/**
 * @author $Author: $
 * @version $Revision: $ $Date: $
 */
public class ImportBookshelfHelperImpl extends ImportDataHelperBase {

	protected static final Map<String, String> propertyNameMap = new java.util.HashMap<String, String>();
	static {
		propertyNameMap.put("S", "status");
		propertyNameMap.put("ID", "id");
		propertyNameMap.put("ラベル", "label");
		propertyNameMap.put("所有者", "owner");
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

	/**
	 * @see org.tsukuba_bunko.lilac.helper.port.impl.ImportDataHelperBase#insertRecord(java.util.Map)
	 */
	@Override
	protected void insertRecord(Map<String, String> record) {
		Bookshelf bookshelf = new Bookshelf();
		Beans.copy(record, bookshelf).excludes("id").excludesNull().execute();
		jdbcManager.insert(bookshelf).execute();
	}

	/**
	 * @see org.tsukuba_bunko.lilac.helper.port.impl.ImportDataHelperBase#updateRecord(java.util.Map)
	 */
	@Override
	protected void updateRecord(Map<String, String> record) {
		Bookshelf bookshelf = new Bookshelf();
		Beans.copy(record, bookshelf).excludesNull().execute();
		jdbcManager.update(bookshelf).execute();
	}

	/**
	 * @see org.tsukuba_bunko.lilac.helper.port.impl.ImportDataHelperBase#deleteRecord(java.util.Map)
	 */
	@Override
	protected void deleteRecord(Map<String, String> record) {
		Bookshelf bookshelf = new Bookshelf();
		Beans.copy(record, bookshelf).excludesNull().execute();
		jdbcManager.delete(bookshelf).execute();
	}

}
