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
package org.tsukuba_bunko.lilac.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

import org.seasar.framework.util.Base64Util;
import org.seasar.framework.util.MessageDigestUtil;
import org.seasar.framework.util.StringUtil;


/**
 * メッセージダイジェストUtility
 * @author ppoi
 * @version 2012.04
 */
public class DigestUtil {

	/**
	 * SHA-1ダイジェスト文字列を取得します。
	 * @param text 入力文字列
	 * @return ダイジェスト結果
	 */
	public static String digestText(String text) {
		if(StringUtil.isBlank(text)) {
			return text;
		}

		MessageDigest md = MessageDigestUtil.getInstance("SHA-1");
		try {
			md.update(text.getBytes("UTF-8"));
			return Base64Util.encode(md.digest());
		}
		catch(UnsupportedEncodingException uee) {
			throw new IllegalStateException(uee);
		}
	}

}
