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
package org.tsukuba_bunko.lilac.web;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.seasar.cubby.action.Accept;
import org.seasar.cubby.action.ActionClass;
import org.seasar.cubby.action.ActionResult;
import org.seasar.cubby.action.Json;
import org.seasar.cubby.action.Path;
import org.seasar.cubby.action.RequestMethod;
import org.seasar.cubby.action.RequestParameter;
import org.seasar.framework.beans.util.Beans;
import org.seasar.framework.util.StringUtil;
import org.tsukuba_bunko.lilac.annotation.Auth;
import org.tsukuba_bunko.lilac.entity.BibAuthor;
import org.tsukuba_bunko.lilac.entity.ReadingRecord;
import org.tsukuba_bunko.lilac.service.ReadingRecordSearchCondition;
import org.tsukuba_bunko.lilac.service.ReadingRecordService;
import org.tsukuba_bunko.lilac.service.SearchResult;


/**
 * 読書履歴API
 * @author ppoi
 * @version 2012.06
 */
@ActionClass
public class ReadingRecordAction {

	@Resource
	public ReadingRecordService readingRecordService;

	@RequestParameter
	public String isbn;

	@RequestParameter
	public Date comeleteDateBegin;

	@RequestParameter
	public Date comeleteDateEnd;

	@RequestParameter
	public Integer page;

	@Path("/readrecord$")
	@Accept(RequestMethod.GET)
	public ActionResult listFirstPage() {
		this.page = 0;
		return list();
	}

	@Path("/readrecord/list/{page,\\d+}")
	@Accept(RequestMethod.GET)
	public ActionResult list() {
		ReadingRecordSearchCondition condition = new ReadingRecordSearchCondition();
		condition.isbn = StringUtil.isNotBlank(isbn) ? isbn : null;
		condition.completionDateBegin = comeleteDateBegin;
		condition.completionDateEnd = comeleteDateEnd;
		SearchResult<ReadingRecord> searchResult = readingRecordService.list(condition, page * 10, 10);

		Map<String, Object> dto = new java.util.HashMap<String, Object>();
		dto.put("count", searchResult.count);
		List<Map<String, Object>> itemsDto = new java.util.ArrayList<Map<String, Object>>();
		for(ReadingRecord entity : searchResult.items) {
			Map<String, Object> entityDto = new java.util.HashMap<String, Object>();
			Beans.copy(entity, entityDto).excludes("bibliography")
				.dateConverter("yyyy/MM/dd", "startDate", "completionDate").execute();
			Map<String, Object> bibDto = new java.util.HashMap<String, Object>();
			Beans.copy(entity.bibliography, bibDto)
					.dateConverter("yyyy/MM/dd", "publicationDate")
					.excludes("authors", "books").execute();
			List<Map<String, Object>> authors = new java.util.ArrayList<Map<String, Object>>(); 
			for(BibAuthor bibauth : entity.bibliography.authors) {
				Map<String, Object> bibauthDto = new java.util.HashMap<String, Object>();
				bibauthDto.put("id", bibauth.authorId);
				bibauthDto.put("name", bibauth.author.name);
				bibauthDto.put("role", bibauth.authorRole);
				authors.add(bibauthDto);
			}
			bibDto.put("authors", authors);
			entityDto.put("bibliography", bibDto);
			itemsDto.add(entityDto);
		}
		dto.put("items", itemsDto);

		return new Json(dto);
	}

	@Path("/readrecord/incomplete")
	@Accept(RequestMethod.GET)
	public ActionResult listIncompleted() {
		return new Json(null);
	}

	@Path("/readrecord/{recordId,\\d+}")
	@Accept(RequestMethod.GET)
	public ActionResult get() {
		return new Json(null);
	}

	@Path("/readrecord")
	@Auth
	@Accept(RequestMethod.PUT)
	public ActionResult create() {
		return new Json(null);
	}

	@Path("/readrecord/{recordId,\\d+}$")
	@Auth
	@Accept(RequestMethod.POST)
	public ActionResult update() {
		return null;
	}

	@Path("/readrecord/start$")
	@Auth
	@Accept(RequestMethod.PUT)
	public ActionResult start() {
		return null;
	}

	@Path("/readrecord/{recordId,\\d+}/complete$")
	@Auth
	@Accept(RequestMethod.POST)
	public ActionResult complete() {
		return null;
	}
}
