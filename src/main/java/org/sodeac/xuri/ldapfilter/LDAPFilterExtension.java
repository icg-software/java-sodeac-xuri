/*******************************************************************************
 * Copyright (c) 2016, 2018 Sebastian Palarus
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Sebastian Palarus - initial API and implementation
 *******************************************************************************/
package org.sodeac.xuri.ldapfilter;

import org.sodeac.xuri.IExtension;

public class LDAPFilterExtension implements IExtension
{
	public static final String TYPE = "org.sodeac.xuri.ldapfilter";

	@Override
	public String getType()
	{
		return TYPE;
	}

}
