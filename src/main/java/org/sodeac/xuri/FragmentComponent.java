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
package org.sodeac.xuri;

/*
 * https://tools.ietf.org/html/rfc3986#section-3.5
 */

public class FragmentComponent extends AbstractComponent<NoSubComponent>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1184594786879375170L;
	
	private String value = null;
	
	public FragmentComponent(String value)
	{
		super(ComponentType.FRAGMENT);
		this.value = value;
	}
	
	public String getValue()
	{
		return this.value;
	}

}
