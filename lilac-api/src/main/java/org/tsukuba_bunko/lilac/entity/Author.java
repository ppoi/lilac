package org.tsukuba_bunko.lilac.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Author {

	/**
	 * 作者ID
	 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Integer id;
	
	/**
	 * 作者名
	 */
	public String name;
	
	/**
	 * 作者WebサイトURL
	 */
	public String website;
	
	/**
	 * 作者Twitterアカウント
	 */
	public String twitter;

	/**
	 * シノニムキー
	 */
	public Integer synonymKey;
	
	/**
	 * 備考
	 */
	public String note;

	/**
	 * エンティティバージョン
	 */
	public Integer version;
}
