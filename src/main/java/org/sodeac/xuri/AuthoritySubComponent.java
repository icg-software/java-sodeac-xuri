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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AuthoritySubComponent implements IExtensible
{
	private List<IExtension> extensions = null;
	private volatile List<IExtension> extensionsImmutable = null;
	private Lock extensionsLock = null;
	private String expression = null;
	private String value = null;
	private char prefixDelimiter = ':';
	private char postfixDelimiter = ':';
	
	protected AuthoritySubComponent(String expression, String value)
	{
		super();
		extensions = new ArrayList<IExtension>();
		this.extensionsLock = new ReentrantLock();
		this.expression = expression;
		this.value = value;
	}
	
	protected void setExpression(String expression)
	{
		this.expression = expression;
	}
	
	protected void setPrefixDelimiter(char delimiter)
	{
		this.prefixDelimiter = delimiter;
	}
	
	public char getPrefixDelimiter()
	{
		return prefixDelimiter;
	}
	
	protected void setPostfixDelimiter(char postfixDelimiter)
	{
		this.postfixDelimiter = postfixDelimiter;
	}

	public char getPostfixDelimiter()
	{
		return postfixDelimiter;
	}

	protected void addExtension(IExtension extension)
	{
		this.extensionsLock.lock();
		try
		{
			this.extensions.add(extension);
			this.extensionsImmutable = null;
		}
		finally 
		{
			this.extensionsLock.unlock();
		}
	}

	@Override
	public IExtension getExtension(String type)
	{
		List<IExtension> extensionList = getExtensionList();
		
		if((type == null) && (! extensionList.isEmpty()))
		{
			return extensionList.get(0);
		}
		for(IExtension extension : extensionList)
		{
			if(type.equals(extension.getType()))
			{
				return extension;
			}
		}
		return null;
	}

	@Override
	public List<IExtension> getExtensionList()
	{
		List<IExtension> extensionList = extensionsImmutable;
		if(extensionList == null)
		{
			this.extensionsLock.lock();
			try
			{
				extensionList = this.extensionsImmutable;
				if(extensionList != null)
				{
					return extensionList;
				}
				this.extensionsImmutable = Collections.unmodifiableList(new ArrayList<IExtension>(this.extensions));
				extensionList = this.extensionsImmutable;
			}
			finally 
			{
				this.extensionsLock.unlock();
			}
		}
		return extensionList;
	}

	@Override
	public List<IExtension> getExtensionList(String type)
	{
		List<IExtension> extensionList = new ArrayList<IExtension>();
		for(IExtension extension : getExtensionList())
		{
			if(type.equals(extension.getType()))
			{
				extensionList.add(extension);
			}
		}
		return extensionList;
	}

	public String getExpression()
	{
		return expression;
	}

	public String getValue()
	{
		return value;
	}

}
