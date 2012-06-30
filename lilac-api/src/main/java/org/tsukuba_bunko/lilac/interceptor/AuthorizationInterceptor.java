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
package org.tsukuba_bunko.lilac.interceptor;

import java.lang.reflect.Method;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletResponse;

import org.aopalliance.intercept.MethodInvocation;
import org.seasar.cubby.action.SendError;
import org.seasar.framework.aop.interceptors.AbstractInterceptor;
import org.seasar.framework.container.annotation.tiger.Component;
import org.seasar.framework.container.annotation.tiger.InstanceType;
import org.seasar.framework.util.StringUtil;
import org.tsukuba_bunko.lilac.annotation.Auth;
import org.tsukuba_bunko.lilac.helper.auth.UserSessionHelper;
import org.tsukuba_bunko.lilac.service.UserSessionService;


/**
 * アクションに対する認可機能を実施するインタセプタです。
 * @author $Author: $
 * @version $Revision: $ $Date: $
 */
@Component(instance=InstanceType.REQUEST)
public class AuthorizationInterceptor extends AbstractInterceptor {

	/**
	 * serial version UID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * User Sessionサービス
	 */
	@Resource
	public UserSessionService userSessionService;

	/**
	 * ユーザセッションヘルパ
	 */
	@Resource
	public UserSessionHelper userSessionHelper;

	/**
	 * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
	 */
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Method method = invocation.getMethod();
		Auth auth = method.getAnnotation(Auth.class);
		if(auth != null) {
			String sessionId = userSessionHelper.getSessionId();
			if(StringUtil.isBlank(sessionId)) {
				return new SendError(HttpServletResponse.SC_UNAUTHORIZED);
			}
			try {
				userSessionService.getValidSession(sessionId);
			}
			catch(NoResultException nre) {
				return new SendError(HttpServletResponse.SC_FORBIDDEN);
			}
		}
		return invocation.proceed();
	}
}
