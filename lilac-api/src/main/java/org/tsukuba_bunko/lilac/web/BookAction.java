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

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.seasar.cubby.action.Accept;
import org.seasar.cubby.action.ActionClass;import org.seasar.cubby.action.ActionResult;
import org.seasar.cubby.action.Json;
import org.seasar.cubby.action.RequestMethod;
import org.seasar.cubby.action.RequestParameter;
import org.seasar.cubby.action.SendError;

import org.seasar.cubby.action.Path;
import org.seasar.framework.beans.util.BeanMap;
import org.seasar.framework.beans.util.Beans;
import org.tsukuba_bunko.lilac.entity.BibAuthor;
import org.tsukuba_bunko.lilac.entity.Bibliography;
import org.tsukuba_bunko.lilac.entity.Book;
import org.tsukuba_bunko.lilac.service.BookService;


/**
 * Book API
 * @author ppoi
 * @version 2012.04
 */
@ActionClass
public class BookAction {

	@Resource
	public BookService bookService;

	@RequestParameter
	public Integer id;

	@Path("/book/{id,\\d+}")
	@Accept(RequestMethod.GET)
	public ActionResult get() {
		if(id == null) {
			return new SendError(400);
		}

		Book book = bookService.get(id);
		if(book == null) {
			return new SendError(404);
		}
		else {
			Map<String, Object> dto = new java.util.HashMap<String, Object>();
			Beans.copy(book, dto)
				.excludes("bibliographyId", "bibliography", "locationId", "location")
				.dateConverter("yyyy/MM/dd", "acquisitionDate").execute();

			Bibliography bib = book.bibliography;
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

			dto.put("bibliography", bibDto);
			return new Json(dto);
		}
	}
}
