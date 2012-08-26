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

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * 蔵書情報
 * @author ppoi
 * @version 2012.04
 */
@Entity
public class Book {

	/**
	 * 蔵書情報ID
	 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Integer id;

	/**
	 * 書誌情報ID
	 */
	public Integer bibliographyId;

	/**
	 * 書誌情報
	 */
	@ManyToOne
	public Bibliography bibliography;

	/**
	 * 所蔵書棚ID
	 */
	public Integer locationId;

	/**
	 * 所蔵書棚
	 */
	@ManyToOne
	public Bookshelf location;

	/**
	 * 購入日
	 */
	@Temporal(TemporalType.DATE)
	public Date acquisitionDate;

	/**
	 * 購入店舗
	 */
	public String purchaseShop;

	/**
	 * 所有者
	 */
	public String owner;

	/**
	 * 備考
	 */
	public String note;
}
