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

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seasar.framework.unit.Seasar2;
import org.tsukuba_bunko.lilac.entity.UserSession;

import static org.junit.Assert.*;

/**
 * {@link UserSessionServiceImpl} のテストケース
 */
@RunWith(Seasar2.class)
public class UserAuthServiceImplTest {

	@Resource
	public UserSessionServiceImpl service;

	@Test
	public void login() {
		String user = "testuser1";
		String password = "password_user1";
		UserSession session = service.open(user, password);
		assertNotNull(session);
		assertEquals(user, session.username);
		assertNotNull(session.id);
		assertNotNull(session.createdTimestamp);
	}

	@Test
	public void get() {
		String sessionId = "miku39";
		UserSession session = service.getValidSession(sessionId);
		assertNotNull(session);
		assertEquals(sessionId, session.id);
		assertEquals("testuser1", session.username);
		assertNotNull(session.createdTimestamp);
	}
}
