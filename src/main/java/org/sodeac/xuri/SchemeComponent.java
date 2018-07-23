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

/*
 * https://tools.ietf.org/html/rfc3986#section-3.1
 */
public class SchemeComponent extends AbstractComponent<NoSubComponent>
{
	private String value = null;
	
	public SchemeComponent(String value)
	{
		super(ComponentType.SCHEME);
		this.value = value;
	}

	public String getValue()
	{
		return this.value;
	}
	
}
