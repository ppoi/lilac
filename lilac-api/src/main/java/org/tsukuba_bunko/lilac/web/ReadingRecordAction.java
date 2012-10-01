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
import org.seasar.cubby.action.SendError;
import org.seasar.framework.beans.util.Beans;
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
	public Integer recordId;

	@RequestParameter
	public Integer bibliographyId;

	@RequestParameter
	public Date completionDateBegin;

	@RequestParameter
	public Date completionDateEnd;

	@RequestParameter
	public Integer page;

	@RequestParameter
	public boolean incomplete;

	@RequestParameter
	public Object requestData;

	@Path("/reading-record")
	@Accept(RequestMethod.GET)
	public ActionResult index() {
		return listFirstPage();
	}

	@Path("/reading-record/list")
	@Accept(RequestMethod.GET)
	public ActionResult listFirstPage() {
		this.page = 0;
		return list();
	}

	@Path("/reading-record/list/{page,\\d+}")
	@Accept(RequestMethod.GET)
	public ActionResult list() {
		ReadingRecordSearchCondition condition = new ReadingRecordSearchCondition();
		condition.bibliogprahyId = bibliographyId;
		condition.completionDateBegin = completionDateBegin;
		condition.completionDateEnd = completionDateEnd;
		condition.incomplete = incomplete;
		SearchResult<ReadingRecord> searchResult = readingRecordService.list(condition, page * 10, 10);

		Map<String, Object> dto = new java.util.HashMap<String, Object>();
		dto.put("count", searchResult.count);
		List<Map<String, Object>> itemsDto = new java.util.ArrayList<Map<String, Object>>();
		for(ReadingRecord entity : searchResult.items) {
			itemsDto.add(entityToDto(entity));
		}
		dto.put("items", itemsDto);

		return new Json(dto);
	}

	@Path("/reading-record/{recordId,\\d+}")
	@Accept(RequestMethod.GET)
	public ActionResult get() {
		if(recordId == null) {
			return new SendError(400);
		}
		ReadingRecord entity = readingRecordService.get(recordId);
		if(entity == null) {
			return new SendError(404);
		}
		return new Json(entityToDto(entity));
	}

	@Path("/reading-record")
	@Auth
	@Accept(RequestMethod.PUT)
	public ActionResult create() {
		ReadingRecord entity = Beans.createAndCopy(ReadingRecord.class, requestData)
				.dateConverter("yyyy-MM-dd", "startDate", "completionDate").execute();
		readingRecordService.create(entity);
		return new Json(entityToDto(readingRecordService.get(entity.id)));
	}

	@Path("/reading-record/{recordId,\\d+}")
	@Auth
	@Accept(RequestMethod.POST)
	public ActionResult update() {
		if(recordId == null) {
			return new SendError(400);
		}
		ReadingRecord entity = readingRecordService.get(recordId);
		if(entity == null) {
			return new SendError(404);
		}
		Beans.copy(requestData, entity).dateConverter("yyyy-MM-dd", "completionDate", "startDate").execute();
		if(!recordId.equals(entity.id)) {
			return new SendError(400);
		}
		readingRecordService.update(entity);
		return new Json(entityToDto(entity));
	}

	@Path("/reading-record/{recordId,\\d+}")
	@Auth
	@Accept(RequestMethod.DELETE)
	public ActionResult delete() {
		if(recordId == null) {
			return new SendError(400);
		}
		readingRecordService.delete(recordId);
		return new Json(Boolean.TRUE);
	}

	public Map<String, Object> entityToDto(ReadingRecord entity) {
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

		return entityDto;
	}
}
