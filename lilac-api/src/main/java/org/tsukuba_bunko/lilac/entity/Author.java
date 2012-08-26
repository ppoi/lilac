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
package org.tsukuba_bunko.lilac.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;


/**
 * 著者情報
 * @author ppoi
 * @version 2012.04
 */
@Entity
public class Author {

	/**
	 * 作者ID
	 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Integer id;

	/**
	 * 作者名
	 */
	public String name;

	/**
	 * 作者WebサイトURL
	 */
	public String website;

	/**
	 * 作者Twitterアカウント
	 */
	public String twitter;

	/**
	 * シノニムキー
	 */
	public Integer synonymKey;

	/**
	 * 備考
	 */
	public String note;

	/**
	 * エンティティバージョン
	 */
	@Version
	public Integer version;
}
