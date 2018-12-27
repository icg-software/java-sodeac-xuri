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
import java.util.concurrent.locks.ReentrantLock;


/**
 * Key-Value segment of URI query. Format of query segment: {@code type:name=format:value}. Name is required.
 * 
 * <p>Values with format {@code string} must begin and end with single quote. single quotes in payload require a backslash as escapesequence .
 * <p>Values with format {@code json} must begin with { and end with } .
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
	private static final long serialVersionUID = 4999925096175079827L;

	/**
	 * constructor for query segment
	 * 
	 * @param expression representative string value query segment
	 * @param type the type of segment (not required)
	 * @param name the name of segment
	 * @param format the format of segment (not required / null, json or string)
	 * @param value  the value of segment
	 */
	public QuerySegment(String expression,String type, String name, String format, String value)
	{
		super();
		this.expression = expression;
		this.type = type;
		this.name = name;
		this.format = format;
		this.value = value;
		
		this.extensionsLock = new ReentrantLock();
	}
	
	private List<IExtension<?>> extensions = null;
	private volatile List<IExtension<?>> extensionsImmutable = null;
	
	private Lock extensionsLock = null;
	private String expression = null;
	private String type = null;
	private String name = null;
	private String format = null;
	private String value = null;
	
	/**
	 * setter for expression string
	 * 
	 * @param expression
	 */
	protected void setExpression(String expression)
	{
		this.expression = expression;
	}

	/**
	 * Append an extension for this query segment
	 * 
	 * @param extension
	 */
	protected void addExtension(IExtension<?> extension)
	{
		this.extensionsLock.lock();
		try
		{
			if(this.extensions == null)
			{
				this.extensions = new ArrayList<IExtension<?>>();
			}
			this.extensions.add(extension);
			this.extensionsImmutable = null;
		}
		finally 
		{
			this.extensionsLock.unlock();
		}
	}

	@Override
	public IExtension<?> getExtension(String type)
	{
		List<IExtension<?>> extensionList = getExtensionList();
		
		if((type == null) && (! extensionList.isEmpty()))
		{
			return extensionList.get(0);
		}
		for(IExtension<?> extension : extensionList)
		{
			if(type.equals(extension.getType()))
			{
				return extension;
			}
		}
		return null;
	}

	@Override
	public List<IExtension<?>> getExtensionList()
	{
		List<IExtension<?>> extensionList = extensionsImmutable;
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
				this.extensionsImmutable = Collections.unmodifiableList(this.extensions == null ? new ArrayList<IExtension<?>>() : new ArrayList<IExtension<?>>(this.extensions));
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
	public List<IExtension<?>> getExtensionList(String type)
	{
		List<IExtension<?>> extensionList = new ArrayList<IExtension<?>>();
		for(IExtension<?> extension : getExtensionList())
		{
			if(type.equals(extension.getType()))
			{
				extensionList.add(extension);
			}
		}
		return extensionList;
	}

	/**
	 * getter for representative string for query segment (type,name,format,value and extensions)
	 * 
	 * @return representative string for query segment
	 */
	public String getExpression()
	{
		return expression;
	}

	/**
	 * getter for type
	 * 
	 * @return
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * getter for name
	 * 
	 * @return
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * getter for format
	 * 
	 * @return
	 */
	public String getFormat()
	{
		return format;
	}

	/**
	 * getter for value
	 * 
	 * @return
	 */
	public String getValue()
	{
		return value;
	}

}
