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
package org.tsukuba_bunko.lilac.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.seasar.extension.jdbc.JdbcManager;
import org.tsukuba_bunko.lilac.entity.Account;
import org.tsukuba_bunko.lilac.entity.UserAuth;
import org.tsukuba_bunko.lilac.service.AccountService;


/**
 * {@link AccountService}実装
 * @author ppoi
 * @version 2012.04
 */
public class AccountServiceImpl implements AccountService {

	@Resource
	public JdbcManager jdbcManager;

	/**
	 * @see org.tsukuba_bunko.lilac.service.AccountService#get(java.lang.String)
	 */
	@Override
	public Account get(String username) {
		return jdbcManager.from(Account.class)
				.where("username=?", username)
				.getSingleResult();
	}

	/**
	 * @see org.tsukuba_bunko.lilac.service.AccountService#list()
	 */
	@Override
	public List<Account> list() {
		return jdbcManager.from(Account.class)
				.orderBy("username ASC")
				.getResultList();
	}

	/**
	 * @see org.tsukuba_bunko.lilac.service.AccountService#create(org.tsukuba_bunko.lilac.entity.Account)
	 */
	@Override
	public void create(Account entity) {
		jdbcManager.insert(entity).execute();
		UserAuth userAuth = new UserAuth();
		userAuth.username = entity.username;
		jdbcManager.insert(userAuth).execute();
	}

	/**
	 * @see org.tsukuba_bunko.lilac.service.AccountService#update(org.tsukuba_bunko.lilac.entity.Account)
	 */
	@Override
	public void update(Account entity) {
		jdbcManager.update(entity).execute();
	}

	/**
	 * @see org.tsukuba_bunko.lilac.service.AccountService#delete(java.lang.String)
	 */
	@Override
	public void delete(String username) {
		Account account = get(username);
		if(account != null) {
			jdbcManager.delete(account).execute();
		}
	}

	/**
	 * @see org.tsukuba_bunko.lilac.service.AccountService#setCredential(org.tsukuba_bunko.lilac.entity.UserAuth)
	 */
	@Override
	public void setCredential(UserAuth credential) {
		jdbcManager.update(credential).execute();
	}
}
