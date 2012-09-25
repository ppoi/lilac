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
 */
package org.tsukuba_bunko.lilac.service;

import java.io.Serializable;
import java.util.Date;


/**
 * 読書履歴検索条件
 * @author ppoi
 * @version 2012.06
 */
public class ReadingRecordSearchCondition implements Serializable {

	/**
	 * serial version UID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 読者
	 */
	public String reader;

	/**
	 * ISBN
	 */
	public String isbn;

	/**
	 * 読了日(範囲開始)
	 */
	public Date completionDateBegin;

	/**
	 * 読了日(範囲終了)
	 */
	public Date completionDateEnd;
}
