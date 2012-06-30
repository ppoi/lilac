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
package org.tsukuba_bunko.lilac.web.converter;

import org.seasar.cubby.converter.ConversionException;
import org.seasar.cubby.converter.ConversionHelper;
import org.seasar.cubby.converter.impl.AbstractConverter;


/**
 * 無変換コンバータ
 * @author $Author: $
 * @version $Revision: $ $Date: $
 */
public class NoopConverter extends AbstractConverter {

	/**
	 * @see org.seasar.cubby.converter.Converter#getObjectType()
	 */
	@Override
	public Class<?> getObjectType() {
		return String.class;
	}

	/**
	 * @see org.seasar.cubby.converter.Converter#convertToObject(java.lang.Object, java.lang.Class, org.seasar.cubby.converter.ConversionHelper)
	 */
	@Override
	public Object convertToObject(Object value, Class<?> objectType, ConversionHelper helper)
			throws ConversionException {
		return value;
	}

	/**
	 * @see org.seasar.cubby.converter.Converter#convertToString(java.lang.Object, org.seasar.cubby.converter.ConversionHelper)
	 */
	@Override
	public String convertToString(Object value, ConversionHelper helper) {
		return String.valueOf(value);
	}
}
