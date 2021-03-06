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


/**
 * 書棚情報
 * @author ppoi
 * @version 2012.04
 */
@Entity
public class Bookshelf {

	/**
	 * 書棚ID
	 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Integer id;

	/**
	 * ラベル
	 */
	public String label;
	
	/**
	 * 所有者
	 */
	public String owner;

	/**
	 * 備考
	 */
	public String note;
}
