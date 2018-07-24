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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;


/**
 * 
 * @author Sebastian Palarus
 * @since 1.0
 * @version 1.0
 *
 */
public class QuerySegment implements Serializable, IExtensible
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -9003894798935854417L;
	
	public QuerySegment(String expression,String type, String name, String format, String value)
	{
		super();
		this.expression = expression;
		this.type = type;
		this.name = name;
		this.format = format;
		this.value = value;
	}
	
	private List<IExtension> extensions = null;
	private volatile List<IExtension> extensionsImmutable = null;
	
	private Lock extensionsLock = null;
	private String expression = null;
	private String type = null;
	private String name = null;
	private String format = null;
	private String value = null;
	
	protected void setExpression(String expression)
	{
		this.expression = expression;
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

	public String getType()
	{
		return type;
	}

	public String getName()
	{
		return name;
	}

	public String getFormat()
	{
		return format;
	}

	public String getValue()
	{
		return value;
	}

}
