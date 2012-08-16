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
 * @author $Author: $
 * @version $Revision: $ $Date: $
 */
@Entity
public class ReadingRecord {

	/**
	 * 履歴ID
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
	 * 読者
	 */
	public String reader;

	/**
	 * 開始日
	 */
	@Temporal(TemporalType.DATE)
	public Date beginDate;

	/**
	 * 読了日
	 */
	@Temporal(TemporalType.DATE)
	public Date completionDate;

	/**
	 * 備考
	 */
	public String note;
}
