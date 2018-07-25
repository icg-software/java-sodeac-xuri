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

import java.io.Serializable;

/**
 * 
 * @author Sebastian Palarus
 * @since 1.0
 * @version 1.0
 *
 */
public class URI implements Serializable 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1421043812832070455L;

	protected String fullPath = null;
	
	protected SchemeComponent scheme = null;
	protected AuthorityComponent authority = null;
	protected PathComponent path = null;
	protected QueryComponent query = null;
	protected FragmentComponent fragment = null;
	
	public URI(String fullPath)
	{
		super();
		this.fullPath = fullPath;
		URIParser.getInstance().parse(this);
	}

	public String getFullPath() 
	{
		return fullPath;
	}
	
	@Override
	public String toString() 
	{
		return this.fullPath;
	}
	
	public SchemeComponent getScheme()
	{
		return this.scheme;
	}

	public AuthorityComponent getAuthority()
	{
		return authority;
	}

	public QueryComponent getQuery()
	{
		return query;
	}

	public PathComponent getPath()
	{
		return path;
	}
	
	public FragmentComponent getFragment()
	{
		return this.fragment;
	}
	
}
