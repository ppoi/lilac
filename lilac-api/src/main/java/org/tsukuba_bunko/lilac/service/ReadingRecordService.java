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

import org.tsukuba_bunko.lilac.entity.ReadingRecord;


/**
 * 読書履歴Service
 * @author ppoi
 * @version 2012.06
 */
public interface ReadingRecordService {

	public SearchResult<ReadingRecord>list(ReadingRecordSearchCondition condition, int offset, int count);

	public ReadingRecord get(int id);

	public void create(ReadingRecord entity);

	public void update(ReadingRecord entity);

	public void delete(int id);
}
