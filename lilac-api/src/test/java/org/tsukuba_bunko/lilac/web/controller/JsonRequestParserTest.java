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
import java.io.StringReader;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seasar.framework.unit.Seasar2;
import org.seasar.framework.unit.annotation.EasyMock;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import static org.easymock.EasyMock.*;
import static org.seasar.framework.unit.S2Assert.*;
/**
 * @author $Author: $
 * @version $Revision: $ $Date: $
 */
@RunWith(Seasar2.class)
public class JsonRequestParserTest {

	@Resource
	private JsonRequestParser jsonRequestParser;

	@EasyMock
	private HttpServletRequest request;

	public void recordSimpleObject() throws Exception {
		Map<String, Object> requestData = new java.util.HashMap<String, Object>();
		requestData.put("text1", "Hatsune Miku");
		requestData.put("text2", "初音ミク");
		requestData.put("number", 39);
		requestData.put("null", null);

		Map<String, Object> nestedObject = new java.util.HashMap<String, Object>();
		nestedObject.put("name", "初音ミク");
		nestedObject.put("id", 39);
		requestData.put("object", nestedObject);

		List<Object> nestedArray = new java.util.ArrayList<Object>();
		nestedArray.add("text");
		nestedArray.add(2);
		requestData.put("array", nestedArray);

		Gson gson = new GsonBuilder().serializeNulls().create();
		StringReader reader = new StringReader(gson.toJson(requestData));
		expect(request.getReader()).andReturn(new BufferedReader(reader));
		expect(request.getParameterMap()).andReturn(new java.util.HashMap<String, String[]>());
	}

	@Test
	public void simpleObject() {
		Map<String, Object[]> result = jsonRequestParser.getParameterMap(request);
		assertNotNull(result);
		assertEquals(1, result.size());

		Object[] requestDataParameter = result.get("requestData");
		assertNotNull(requestDataParameter);
		assertEquals(1, requestDataParameter.length);

		@SuppressWarnings("unchecked")
		Map<String, Object> requestData = (Map<String, Object>)requestDataParameter[0];
		assertNotNull(requestData);
		assertEquals(6, requestData.size());
		assertEquals("Hatsune Miku", requestData.get("text1"));
		assertEquals("初音ミク", requestData.get("text2"));
		assertNotNull(requestData.get("number"));
		assertEquals(39, ((Number)requestData.get("number")).intValue());
		assertTrue(requestData.containsKey("null"));
		assertNull(requestData.get("null"));

		@SuppressWarnings("unchecked")
		Map<String, Object> nestedObject = (Map<String, Object>)requestData.get("object");
		assertNotNull(nestedObject);
		assertEquals(39, ((Number)nestedObject.get("id")).intValue());
		assertEquals("初音ミク", nestedObject.get("name"));

		@SuppressWarnings("unchecked")
		List<Object> nestedArray = (List<Object>)requestData.get("array");
		assertNotNull(nestedArray);
		assertEquals(2, nestedArray.size());
	}

	public void recordIsParsable() {
		expect(request.getHeader("Content-Type")).andReturn("application/json").once();
	}

	@Test
	public void isParsable() {
		assertTrue(jsonRequestParser.isParsable(request));
	}

	public void recordIsNotParsable() {
		expect(request.getHeader("Content-Type")).andReturn("application/x-www-form-urlencoded").once();
	}

	@Test
	public void isNotParsable() {
		assertFalse(jsonRequestParser.isParsable(request));
	}

	public void recordIsNotParsableWithNoContentTypeHeader() {
		expect(request.getHeader("Content-Type")).andReturn(null).once();
	}

	@Test
	public void isNotParsableWithNoContentTypeHeader() {
		assertFalse(jsonRequestParser.isParsable(request));
	}
}
