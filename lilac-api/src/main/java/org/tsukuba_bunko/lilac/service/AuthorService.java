package org.tsukuba_bunko.lilac.service;

import java.util.List;

import org.tsukuba_bunko.lilac.entity.Author;

public interface AuthorService {

	public Author get(int id);

	public List<Author> synonym(int id);

	public List<Author> list();
	
	public List<Author> list(String nameCondition);
}
