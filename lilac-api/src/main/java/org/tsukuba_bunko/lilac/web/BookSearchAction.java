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

import javax.annotation.Resource;

import org.seasar.cubby.action.Accept;
import org.seasar.cubby.action.ActionClass;
import org.seasar.cubby.action.ActionResult;
import org.seasar.cubby.action.Json;
import org.seasar.cubby.action.Path;
import org.seasar.cubby.action.RequestMethod;
import org.seasar.cubby.action.RequestParameter;
import org.seasar.framework.beans.util.BeanMap;
import org.seasar.framework.beans.util.Beans;
import org.tsukuba_bunko.lilac.entity.BibAuthor;
import org.tsukuba_bunko.lilac.entity.Bibliography;
import org.tsukuba_bunko.lilac.service.BibliographyService;
import org.tsukuba_bunko.lilac.service.BookSearchCondition;
import org.tsukuba_bunko.lilac.service.BookSearchCondition.OrderBy;
import org.tsukuba_bunko.lilac.service.SearchResult;
import org.tsukuba_bunko.lilac.web.converter.OrderByConverter;
import org.tsukuba_bunko.lilac.web.converter.TextConverter;


/**
 * Book Search API
 * @author ppoi
 * @version 2012.05
 */
@ActionClass
public class BookSearchAction {

	@Resource
	public BibliographyService bibliographyService;

	@RequestParameter(converter=TextConverter.class)
	public String keyword;

	@RequestParameter
	public Integer authorId;

	@RequestParameter(converter=TextConverter.class)
	public String label;

	@RequestParameter
	public Date publicationDateBegin;

	@RequestParameter
	public Date publicationDateEnd;

	@RequestParameter
	public Date acquisitionDateBegin;

	@RequestParameter
	public Date acquisitionDateEnd;

	@RequestParameter(converter=OrderByConverter.class)
	public OrderBy sort1;

	@RequestParameter(converter=OrderByConverter.class)
	public OrderBy sort2;

	@RequestParameter(converter=OrderByConverter.class)
	public OrderBy sort3;

	@RequestParameter
	public Integer page;

	public int itemsPerPage = 10;

	@Path("/booksearch")
	@Accept(RequestMethod.GET)
	public ActionResult index() throws Exception {
		page = 0;
		return list();
	}


	@Path("/booksearch/{page,\\d*}")
	public ActionResult list() {
		if(page == null) {
			page = 0;
		}

		List<OrderBy> orderBy = new java.util.ArrayList<OrderBy>();
		if(sort1 != null) {
			orderBy.add(sort1);
		}
		if(sort2 != null) {
			orderBy.add(sort2);
		}
		if(sort3 != null) {
			orderBy.add(sort3);
		}

		BookSearchCondition condition = new BookSearchCondition();
		condition.keyword = keyword;
		condition.authorId = authorId;
		condition.label = label;
		condition.publicationDateBegin = publicationDateBegin;
		condition.publicationDateEnd = publicationDateEnd;
		condition.acquisitionDateBegin = acquisitionDateBegin;
		condition.acquisitionDateEnd = acquisitionDateEnd;
		condition.orderBy = orderBy;
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
				bibauthDto.put("role", bibauth.authorRole);
				authors.add(bibauthDto);
			}
			bibDto.put("authors", authors);
			bibDtoList.add(bibDto);
		}
		dto.put("items", bibDtoList);

		return new Json(dto);
	}
}
