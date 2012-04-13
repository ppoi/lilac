/*
 * All Rights Reserved.
 * Copyright (C) 2011 Tsukuba Bunko.
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

import java.util.List;

import org.tsukuba_bunko.lilac.entity.Label;


/**
 * @author $Author: $
 * @version $Revision: $ $Date: $
 */
public interface LabelService {

	public Label get(String name);

	public void create(Label label);

	public void update(Label label);

	public void delete(String name);

	public List<Label> list();
}
