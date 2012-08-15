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
import org.seasar.extension.jdbc.where.SimpleWhere;
import org.seasar.framework.beans.util.Beans;
import org.tsukuba_bunko.lilac.entity.Label;


/**
 * @author $Author: $
 * @version $Revision: $ $Date: $
 */
public class ImportLabelHelperImpl extends ImportDataHelperBase {

	protected static final Map<String, String> propertyNameMap = new java.util.HashMap<String, String>();
	static {
		propertyNameMap.put("S", "status");
		propertyNameMap.put("レーベル名", "name");
		propertyNameMap.put("備考", "note");
		propertyNameMap.put("Webサイト", "website");
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
		Label entity = new Label();
		Beans.copy(record, entity).excludesNull().execute();
		jdbcManager.insert(entity).execute();
	}

	/**
	 * @see org.tsukuba_bunko.lilac.helper.port.impl.ImportDataHelperBase#updateRecord(java.util.Map)
	 */
	@Override
	protected void updateRecord(Map<String, String> record) {
		Label entity = new Label();
		Beans.copy(record, entity).excludesNull().execute();
		Label current = jdbcManager.from(Label.class).where(new SimpleWhere()
					.eq("name", entity.name)
				).disallowNoResult().getSingleResult();
		entity.version = current.version;
		jdbcManager.update(entity).execute();
	}

	/**
	 * @see org.tsukuba_bunko.lilac.helper.port.impl.ImportDataHelperBase#deleteRecord(java.util.Map)
	 */
	@Override
	protected void deleteRecord(Map<String, String> record) {
		Label entity = new Label();
		Beans.copy(record, entity).excludesNull().execute();
		Label current = jdbcManager.from(Label.class).where(new SimpleWhere()
					.eq("name", entity.name)
				).disallowNoResult().getSingleResult();
		entity.version = current.version;
		jdbcManager.delete(entity).execute();
	}

}
