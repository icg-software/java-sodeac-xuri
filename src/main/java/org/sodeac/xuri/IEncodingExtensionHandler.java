/*******************************************************************************
 * Copyright (c) 2016, 2018 Sebastian Palarus
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sebastian Palarus - initial API and implementation
 *******************************************************************************/
package org.sodeac.xuri;

public interface IEncodingExtensionHandler<T>
{
	public String getType();
	public int parseRawExtensionString(ExtensionHandleObject extensionHandleObject);
	public int openerCharactersMatched(ExtensionHandleObject extensionHandleObject);
	public ComponentType[] getApplicableComponents();
	public String encodeToString(T extensionDataObject);
}