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

import org.tsukuba_bunko.lilac.entity.Bibliography;


/**
 * 書誌情報管理Service
 * @author ppoi
 * @version 2012.04
 */
public interface BibliographyService {

	public Bibliography get(int id);

	public Bibliography get(int id, boolean withBooks);

	public SearchResult<Bibliography> list(BookSearchCondition condition, int offset, int limit);
	
	public long count(BookSearchCondition condition);
}
