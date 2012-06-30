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
package org.tsukuba_bunko.lilac.service;

import javax.persistence.NoResultException;

import org.tsukuba_bunko.lilac.entity.UserSession;


/**
 * 認証サービス
 * @author $Author: $
 * @version $Revision: $ $Date: $
 */
public interface UserSessionService {

	/**
	 * 認証を行い，ユーザセッションを開始します。
	 * @param user ユーザID
	 * @param password パスワード
	 * @return ユーザセッション情報
	 * @throws NoResultException　認証に失敗した場合
	 */
	public UserSession open(String user, String password);

	/**
	 * 有効なユーザセッション情報を取得します。
	 * @param sessionId セッションID
	 * @return 有効なユーザセッション情報
	 * @throws NoResultException 有効なセッション情報がない場合
	 */
	public UserSession getValidSession(String sessionId);

	/**
	 * ユーザセッションを無効化します。
	 * @param sessionId セッションID
	 */
	public void invalidate(String sessionId);
}
