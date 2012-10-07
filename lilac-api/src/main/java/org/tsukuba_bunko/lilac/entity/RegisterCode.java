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
package org.tsukuba_bunko.lilac.entity;


/**
 * 登録区分
 * @author ppoi
 * @version 2012.06
 */
public enum RegisterCode {

	/**
	 * りら(2012/09～)
	 */
	lilac,

	/**
	 * 管理台帳(2009/07～2011/08)
	 */
	excel,

	/**
	 * 移行期間中(2011/08～2012/08)
	 */
	migrating,

	/**
	 * 記録開始前(～2009/06)
	 */
	prehistory
}
