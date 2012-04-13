/*
 * All Rights Reserved.
 * Copyright (C) 2011 Tsukuba Bunko.
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
package org.tsukuba_bunko.lilac.service;

import org.seasar.extension.jdbc.where.AbstractWhere;


/**
 * textsearch_senna対応の{@link org.seasar.extension.jdbc.Where}実装です。
 * @author $Author: $
 * @version $Revision: $ $Date: $
 */
public class TextSearchWhere extends AbstractWhere<TextSearchWhere> {

	/**
	 * <code>%%</code>の条件を追加します。
	 * @param property プロパティ名
	 * @param queryText 条件テキスト
	 * @return　このインスタンス自身
	 */
	public TextSearchWhere textsearch(CharSequence property, CharSequence queryText) {
		assertPropertyName(property);
		Object value = normalize(queryText);
		if(value != null) {
	        if (criteriaSb.length() > 0) {
	            criteriaSb.append(" and ");
	        }
	        criteriaSb.append(property).append(" %% ?");
	        paramList.add(value);
	        propertyNameList.add(property.toString());
		}
		return this;
	}

}
