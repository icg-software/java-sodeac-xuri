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
 * https://tools.ietf.org/html/rfc3986#section-3.2
 */

public class AuthorityComponent extends AbstractComponent<AuthoritySubComponent>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3937914592142761246L;


	public AuthorityComponent()
	{
		super(ComponentType.AUTHORITY);
	}
	
	
	public String getMajorComponent()
	{
		return super.getExpression();
	}

}
