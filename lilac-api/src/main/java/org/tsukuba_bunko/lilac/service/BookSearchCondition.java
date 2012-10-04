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
	 * レーベル(完全一致)
	 */
	public String label;

	/**
	 * ISBN(前方一致)
	 */
	public String isbn;

	/**
	 * 読了書籍の除外フラグ
	 */
	public boolean excludesRead = false;

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
	 * 蔵書オーナー(完全一致)
	 */
	public String owner;

	/**
	 * ソート条件
	 */
	public List<OrderBy> orderBy;

	/**
	 * ソート式
	 * @author ppoi
	 * @version 2012.05
	 */
	public static enum OrderBy {
		/**
		 * レーベル(昇順)
		 */
		labelAsc("label", "label", "ASC"),

		/**
		 * レーベル(昇順)
		 */
		labelDesc("label", "label", "DESC"),

		/**
		 * タイトル(昇順)
		 */
		titleAsc("title", "title", "ASC"),

		/**
		 * タイトル(降順)
		 */
		titleDesc("title", "title",  "DESC"),

		/**
		 * 出版日(昇順)
		 */
		publicationDateAsc("publicationDate", "publication_date", "ASC"),

		/**
		 * 出版日(降順)
		 */
		publicationDateDesc("publicationDate", "publication_date", "DESC"),

		/**
		 * 購入日(昇順)
		 */
		acquisitionDateAsc("acquisitionDate", "acquisition_date", "ASC"),

		/**
		 * 購入日(降順)
		 */
		acquisitionDateDesc("acquisitionDate", "acquisition_date", "DESC");

		private String propertyName;
		private String columnName;
		private String direction;

		private OrderBy(String propertyName, String columnName, String direction) {
			this.propertyName = propertyName;
			this.columnName = columnName;
			this.direction = direction;
		}

		/**
		 * SQL式を取得します。
		 * @param tableName テーブル名
		 * @return SQL式
		 */
		public String getSql(String tableName) {
			return tableName + "." + columnName + " " + direction;
		}

		/**
		 * S2JDBC式を取得します。
		 * @return S2JDBC式
		 */
		public String getS2JDBCExpression() {
			return propertyName + " " + direction;
		}

		/**
		 * S2JDBC式を取得します。
		 * @param entityPropertyName プロパティをもっているエンティティプロパティ名
		 * @return S2JDBC式
		 */
		public String getS2JDBCExpression(String entityPropertyName) {
			return entityPropertyName + "." + propertyName + " " + direction;
		}

		/**
		 * プロパティ名を取得します。
		 * @return プロパティ名
		 */
		public String getPropertyName() {
			return propertyName;
		}

		/**
		 * カラム名を取得します。
		 * @return カラム名
		 */
		public String getColumnName() {
			return columnName;
		}

		/**
		 * ソート方向を取得します。
		 * @return ソート方向(ASC/DESC)
		 */
		public String getDirection() {
			return direction;
		}
	}
}
