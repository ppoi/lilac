/*
 * All Rights Reserved.
 * Copyright (C) 2011-2012 Tsukuba Bunko.
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
package org.tsukuba_bunko.lilac.service;

import java.util.Date;
import java.util.List;


/**
 * 蔵書検索条件
 * @author ppoi
 * @version 2012.05
 */
public class BookSearchCondition {

	/**
	 * 検索キーワード(タイトル/サブタイトル/著者名)
	 */
	public String keyword;

	/**
	 * 著者(ID)
	 */
	public Integer authorId;

	/**
	 * レーベル
	 */
	public String label;

	/**
	 * ISBN
	 */
	public String isbn;

	/**
	 * 読了書籍の除外フラグ
	 */
	public boolean excludeRead;

	/**
	 * 出版日(範囲開始)
	 */
	public Date publicationDateBegin;

	/**
	 * 出版日(範囲終了)
	 */
	public Date publicationDateEnd;

	/**
	 * 購入日(範囲開始)
	 */
	public Date acquisitionDateBegin;

	/**
	 * 購入日(範囲終了)
	 */
	public Date acquisitionDateEnd;

	/**
	 * 蔵書オーナー
	 */
	public String owner;

	/**
	 * ソート条件
	 */
	public List<OrderBy> orderBy;

	public static enum OrderBy {
		titleAsc("title", "title", "ASC"),
		titleDesc("title", "title",  "DESC"),
		publicationDateAsc("publicationDate", "publication_date", "ASC"),
		publicationDateDesc("publicationDate", "publication_date", "DESC");

		private String propertyName;
		private String columnName;
		private String direction;

		private OrderBy(String propertyName, String columnName, String direction) {
			this.propertyName = propertyName;
			this.columnName = columnName;
			this.direction = direction;
		}

		public String getSql(String tableName) {
			return tableName + "." + columnName + " " + direction;
		}
		
		public String getS2JDBCExpression() {
			return propertyName + " " + direction;
		}

		public String getPropertyName() {
			return propertyName;
		}
		
		public String getColumnName() {
			return columnName;
		}
		
		public String getDirection() {
			return direction;
		}
	}
}
