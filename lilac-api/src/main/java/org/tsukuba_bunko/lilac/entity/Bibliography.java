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
package org.tsukuba_bunko.lilac.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


/**
 * 書誌情報
 * @author $Author: $
 * @version $Revision: $ $Date: $
 */
@Entity
public class Bibliography {

	/**
	 * 書誌情報ID
	 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Integer id;
	
	/**
	 * ISBN
	 */
	public String isbn;
	
	/**
	 * レーベル
	 */
	public String label;
	
	/**
	 * インデックス
	 */
	public String index;
	
	/**
	 * 著者情報
	 */
	@OneToMany(mappedBy="bibliography")
	public List<BibAuthor> authors;
	
	/**
	 * タイトル
	 */
	public String title;
	
	/**
	 * サブタイトル
	 */
	public String subtitle;

	/**
	 * 概要
	 */
	public String description;
	
	/**
	 * 価格
	 */
	public Integer price;
	
	/**
	 * 出版日
	 */
	@Temporal(TemporalType.DATE)
	public Date publicationDate;
	
	/**
	 * 備考
	 */
	public String note;

	/**
	 * 蔵書情報
	 */
	@OneToMany(mappedBy="bibliography")
	public List<Book> books;

	/**
	 * エンティティバージョン
	 */
	@Version
	public Integer version;
}
