package org.tsukuba_bunko.lilac.web;

import org.seasar.cubby.action.ActionClass;
import org.seasar.cubby.action.ActionResult;
import org.seasar.cubby.action.Forward;
import org.seasar.cubby.action.Json;
import org.seasar.cubby.action.Path;

@ActionClass
@Path("/")
public class ServiceInfoAction {

	public ActionResult index() {
		return new Json("hello");
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
