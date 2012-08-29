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
package org.tsukuba_bunko.lilac.web;

import java.util.List;
import java.util.Map;

import org.seasar.cubby.action.Accept;
import org.seasar.cubby.action.ActionClass;
import org.seasar.cubby.action.ActionResult;
import org.seasar.cubby.action.Json;
import org.seasar.cubby.action.Path;
import org.seasar.cubby.action.RequestMethod;
import org.seasar.cubby.action.RequestParameter;
import org.seasar.cubby.action.SendError;
import org.seasar.framework.beans.util.BeanMap;
import org.seasar.framework.beans.util.Beans;
import org.tsukuba_bunko.lilac.entity.BibAuthor;
import org.tsukuba_bunko.lilac.entity.Bibliography;
import org.tsukuba_bunko.lilac.entity.Book;
import org.tsukuba_bunko.lilac.service.BibliographyService;
import org.tsukuba_bunko.lilac.service.BookSearchCondition;
import org.tsukuba_bunko.lilac.service.SearchResult;
import org.tsukuba_bunko.lilac.web.converter.TextConverter;


/**
 * Bibliography API
 * @author ppoi
 * @version 2012.04
 */
@ActionClass
public class BibliographyAction {

	@RequestParameter
	public Integer id;

	@RequestParameter(converter=TextConverter.class)
	public String keyword;

	@RequestParameter
	public Integer authorId;

	@RequestParameter(converter=TextConverter.class)
	public String author;

	@RequestParameter(converter=TextConverter.class)
	public String label;

	@RequestParameter
	public boolean excludeRead = false;

	@RequestParameter
	public List<BookSearchCondition.OrderBy> orderBy;

	public BibliographyService bibliographyService;

	@RequestParameter
	public Integer page;

	public int itemsPerPage = 10;

	@Path("/bibliography")
	@Accept(RequestMethod.GET)
	public ActionResult index() throws Exception {
		page = 0;
		return list();
	}

	@Path("/bibliography/{id,\\d+}")
	@Accept(RequestMethod.GET)
	public ActionResult get() {
		Bibliography bib = bibliographyService.get(id, true);
		if(bib == null) {
			return new SendError(404);
		}

		BeanMap bibDto = Beans.createAndCopy(BeanMap.class, bib)
				.dateConverter("yyyy/MM/dd", "publicationDate")
				.excludes("authors", "books").execute();
		List<BeanMap> authors = new java.util.ArrayList<BeanMap>(); 
		for(BibAuthor bibauth : bib.authors) {
			BeanMap bibauthDto = new BeanMap();
			bibauthDto.put("id", bibauth.authorId);
			bibauthDto.put("name", bibauth.author.name);
			bibauthDto.put("role", bibauth.role);
			authors.add(bibauthDto);
		}
		bibDto.put("authors", authors);

		List<Map<String, Object>> books = new java.util.ArrayList<Map<String, Object>>();
		for(Book book : bib.books) {
			Map<String, Object> bookDto = new java.util.HashMap<String, Object>();
			Beans.copy(book, bookDto)
				.excludes("bibliography", "bibliographyId", "locationId")
				.dateConverter("yyyy/MM/dd", "acquisitionDate")
				.execute();
			books.add(bookDto);
		}
		bibDto.put("books", books);
		return new Json(bibDto);
	}

	@Path("/bibliography/list/{page,\\d*}")
	public ActionResult list() {
		if(page == null) {
			page = 0;
		}

		BookSearchCondition condition = new BookSearchCondition();
		condition.keyword = keyword;
		condition.authorId = authorId;
		condition.authorKeyword = author;
		condition.label = label;
		condition.orderBy = orderBy;
		condition.excludeRead = excludeRead;
		SearchResult<Bibliography> result = bibliographyService.list(condition, page * itemsPerPage, itemsPerPage); 

		BeanMap dto = new BeanMap();
		
		dto.put("count", result.count);

		List<BeanMap> bibDtoList = new java.util.ArrayList<BeanMap>();
		for(Bibliography bib : result.items) {
			BeanMap bibDto = Beans.createAndCopy(BeanMap.class, bib)
					.dateConverter("yyyy/MM/dd", "publicationDate")
					.excludes("authors", "books").execute();
			List<BeanMap> authors = new java.util.ArrayList<BeanMap>(); 
			for(BibAuthor bibauth : bib.authors) {
				BeanMap bibauthDto = new BeanMap();
				bibauthDto.put("id", bibauth.authorId);
				bibauthDto.put("name", bibauth.author.name);
				bibauthDto.put("role", bibauth.role);
				authors.add(bibauthDto);
			}
			bibDto.put("authors", authors);
			bibDtoList.add(bibDto);
		}
		dto.put("items", bibDtoList);

		return new Json(dto);
	}
}
