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
package org.tsukuba_bunko.lilac.service;

import java.util.List;

import org.tsukuba_bunko.lilac.entity.Account;
import org.tsukuba_bunko.lilac.entity.UserAuth;


/**
 * アカウント情報管理Service
 * @author ppoi
 * @version 2012.04
 */
public interface AccountService {

	public Account get(String username);

	public List<Account> list();

	public void create(Account entity);

	public void update(Account entity);

	public void delete(String username);

	public void setCredential(UserAuth credential);
}
