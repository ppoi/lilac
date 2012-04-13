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
 *
 * $Id:　$
 */
package org.tsukuba_bunko.lilac.service.impl;

import javax.annotation.Resource;

import org.junit.runner.RunWith;
import org.seasar.framework.unit.Seasar2;
import org.tsukuba_bunko.lilac.entity.UserSession;

import static org.junit.Assert.*;

/**
 * @author $Author: $
 * @version $Revision: $ $Date: $
 */
@RunWith(Seasar2.class)
public class UserAuthServiceImplTest {

	@Resource
	public UserSessionServiceImpl service;

	public void login_getValidSession() throws Exception {
		String user = "ppoi";
		String password = "1234567890";
		UserSession session = service.open(user, password);
		assertNotNull(session);
		assertNotNull(session.id);

		UserSession refetch = service.getValidSession(session.id);
		assertNotNull(refetch);
		assertEquals(session.id, refetch.id);
	}

	public void digestPassword_1() throws Exception {
		String password = "1234567890";
		String result1 = service.digestPassword(password);
		System.out.println("<" + result1 + ">");
		String result2 = service.digestPassword(password);
		System.out.println(result2);
	}
}
