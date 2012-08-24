package org.tsukuba_bunko.lilac.web;

import java.util.Map;

import javax.annotation.Resource;

import org.seasar.cubby.action.ActionClass;
import org.seasar.cubby.action.ActionResult;
import org.seasar.cubby.action.Forward;
import org.seasar.cubby.action.Json;
import org.seasar.cubby.action.Path;

@ActionClass
@Path("/")
public class ServiceInfoAction {

	@Resource
	public String lilacApiVersion;

	public ActionResult index() {
		Map<String, Object> dto = new java.util.HashMap<String, Object>();
		dto.put("version", lilacApiVersion);
		dto.put("api", new String[]{"label", "author", "book", "session", "export", "import"});
		return new Json(dto);
	}
	
	public ActionResult author() {
		return new Forward(AuthorAction.class);
	}
	
	public ActionResult book() {
		return new Forward(BibliographyAction.class);
	}

	public ActionResult label() {
		return new Forward(LabelAction.class);
	}

	public ActionResult session() {
		return new Forward(SessionAction.class);
	}
}
