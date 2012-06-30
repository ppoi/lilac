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
package org.tsukuba_bunko.lilac.helper.auth;


/**
 * ユーザセッションHelper
 * @author $Author: $
 * @version $Revision: $ $Date: $
 */
public interface UserSessionHelper {

	/**
	 * 現在のリクエストに関連づけられたセッションIDを取得します。
	 * @return 現在のリクエストに関連づけられたセッションID
	 */
	public String getSessionId();

	/**
	 * セッションIDを変更します。変更は次回のリクエストから有効になります。
	 * @param sessionId セッションID
	 */
	public void setSessionId(String sessionId);
}
