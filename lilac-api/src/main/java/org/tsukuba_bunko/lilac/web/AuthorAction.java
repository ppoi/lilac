package org.tsukuba_bunko.lilac.web;

import java.util.List;

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
import org.tsukuba_bunko.lilac.entity.Author;
import org.tsukuba_bunko.lilac.service.AuthorService;

@ActionClass
@Path(value="author",priority=0)
@Accept({RequestMethod.GET})
public class AuthorAction {

	public AuthorService creatorService;


	/**
	 * 著者名検索条件
	 */
	@RequestParameter
	public String name;

	/**
	 * 著者ID
	 */
	@RequestParameter
	public int id;

	/**
	 * 
	 * @return
	 */
	public ActionResult index() throws Exception {
		if(name != null) {
			name = new String(name.getBytes("ISO-8859-1"), "UTF-8");
		}
		return new Json(creatorService.list(name));
	}
	
	/**
	 * 
	 * @return
	 */
	@Path("{id}")
	public ActionResult get() {

		Author author = creatorService.get(id);
		if(author == null) {
			return new SendError(404);
		}

		BeanMap authorDTO = new BeanMap();
		Beans.copy(author, authorDTO).execute();

		if(author.synonymKey != null) {
			List<Author> synonym = creatorService.synonym(author.id);
			authorDTO.put("synonym", synonym);
		}

		return new Json(authorDTO);
	}

	@Path("{id}")
	@Accept({RequestMethod.PUT})
	public ActionResult update() {
		System.out.println("ぎゃーーーーーーーーーー");
		Author author = creatorService.get(id);
		if(author == null) {
			return new SendError(404);
		}
		return new Json(creatorService.get(id));
	}
}
