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
package org.tsukuba_bunko.lilac.web.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.seasar.cubby.controller.RequestParser;
import org.seasar.framework.exception.IORuntimeException;
import org.seasar.framework.log.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;


/**
 * リクエストボディのJSONデータをリクエストパラメータとして処理する {@link RequestParser}実装
 * @author $Author: $
 * @version $Revision: $ $Date: $
 */
public class JsonRequestParser implements RequestParser {

	/**
	 * ログ
	 */
	private static final Logger logger = Logger.getLogger(JsonRequestParser.class);

	/**
	 * @see org.seasar.cubby.controller.RequestParser#getParameterMap(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public Map<String, Object[]> getParameterMap(HttpServletRequest request) {
		Object requestData = getJavaObject(parseContent(request));
		if(logger.isDebugEnabled()) {
			logger.debug("requestData=" + requestData);
		}
		Map<String, Object[]> parameterMap = new java.util.HashMap<String, Object[]>();
		parameterMap.put("requestData", new Object[]{requestData});
		parameterMap.putAll(request.getParameterMap());
		return parameterMap;
	}

	/**
	 * @see org.seasar.cubby.controller.RequestParser#isParsable(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public boolean isParsable(HttpServletRequest request) {
		String contentType = request.getHeader("Content-Type");
		if(logger.isDebugEnabled()) {
			logger.debug("Expected: application/json, Actual: " + contentType);
		}
		return "application/json".equals(contentType);
	}

	/**
	 * リクエストボディをJSONとして解析します。
	 * @param request HTTPリクエストオブジェクト
	 * @return JSON要素
	 */
	public JsonElement parseContent(HttpServletRequest request) {
		BufferedReader source = null;
		try {
			source = request.getReader();
			JsonParser parser = new JsonParser();
			return parser.parse(source);
		}
		catch(IOException ioe) {
			throw new IORuntimeException(ioe);
		}
		finally {
			IOUtils.closeQuietly(source);
		}
	}

	/**
	 * JSON要素をJavaオブジェクトに変換します。
	 * @param element　JSON要素
	 * @return Javaオブジェクト
	 */
	public Object getJavaObject(JsonElement element) {
		if(element.isJsonObject()) {
			return getJavaObject(element.getAsJsonObject());
		}
		else if(element.isJsonArray()) {
			return getJavaObject(element.getAsJsonArray());
		}
		else if(element.isJsonPrimitive()) {
			return getJavaObject(element.getAsJsonPrimitive());
		}
		else {
			return null;
		}
	}

	/**
	 * JavaScriptオブジェクト型要素をJavaオブジェクト(Map<String, Object>)に変換します。
	 * @param jsonObject　JavaScriptオブジェクト型要素
	 * @return Mapオブジェクト
	 */
	public Map<String, Object> getJavaObject(JsonObject jsonObject) {
		Map<String, Object> object = new java.util.HashMap<String, Object>();
		for(Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			object.put(entry.getKey(), getJavaObject(entry.getValue()));
		}
		return object;
	}

	/**
	 * JavaScript配列型要素をJavaオブジェクト(List<Object>)に変換します。
	 * @param jsonArray JavaScript配列型要素 
	 * @return Listオブジェクト
	 */
	public List<Object> getJavaObject(JsonArray jsonArray) {
		List<Object> array = new java.util.ArrayList<Object>();
		for(JsonElement element : jsonArray) {
			array.add(getJavaObject(element));
		}
		return array;
	}

	/**
	 * JavaScriptプリミティブ型要素(真偽値,数値,文字列)をJavaオブジェクトに変換します。
	 * @param jsonPrimitive　JavaScriptプリミティブ型要素
	 * @return　Javaオブジェクト
	 */
	public Object getJavaObject(JsonPrimitive jsonPrimitive) {
		if(jsonPrimitive.isBoolean()) {
			return jsonPrimitive.getAsBoolean();
		}
		else if(jsonPrimitive.isNumber()) {
			return jsonPrimitive.getAsNumber();
		}
		else {
			return jsonPrimitive.getAsString();
		}
	}
}
