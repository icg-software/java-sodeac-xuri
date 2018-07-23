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


import java.io.Serializable;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.sodeac.xuri.ldapfilter.LDAPFilterEncodingHandler;


/*
 * 
https://tools.ietf.org/html/rfc3986

*/

public class URIParser implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5276111582405090279L;
	
	public static final char COLON		 	= ':';
	public static final char SLASH			= '/';
	public static final char BACKSLASH		= '\\';
	public static final char QUESTION_MARK	= '?';
	public static final char FRAGMENT 		= '#';
	public static final char SINGLE_QUOTE 	= '\'';
	public static final char DOUBLE_QUOTE 	= '"';
	public static final char PERCENT_SIGN 	= '%';
	public static final char AT_SIGN	 	= '@';
	public static final char AND			= '&';
	public static final char EQUAL			= '=';
	
	public static List<IEncodingExtensionHandler> getEncodingExtensionHandler(ComponentType componentType, URI uri)
	{
		List<IEncodingExtensionHandler> list = new ArrayList<IEncodingExtensionHandler>();
		list.add(new LDAPFilterEncodingHandler());
		
		return list;
	}
	
	public static volatile URIParser instance = null;
	
	protected static URIParser getInstance()
	{
		if(instance == null)
		{
			instance = new URIParser();
		}
		return instance;
	}

	protected URI parse(URI uri)
	{
		ParserHelperContainerObject workerObject = new ParserHelperContainerObject(); // TODO cache
		workerObject.uri = uri;
		workerObject.fullPath = uri.getFullPath();
		workerObject.maxPosition = workerObject.fullPath.length() -1;
		
		parseScheme(workerObject);
		if(workerObject.containsAuthority)
		{
			parseAuthority(workerObject);
		}
		else
		{
			workerObject.uri.authority = new AuthorityComponent();
		}
		
		if(workerObject.containsPath)
		{
			parsePath(workerObject);
		}
		else
		{
			workerObject.uri.path = new PathComponent(false);
			workerObject.uri.path.setExpression("");
		}
		if(workerObject.containsQuery)
		{
			parseQuery(workerObject);
		}
		else
		{
			workerObject.uri.query = new QueryComponent();
			workerObject.uri.query.setExpression("");
		}
		if(workerObject.containsFragment)
		{
			parseFragment(workerObject);
		}
		// TODO recycle workerObject
		return uri;
	}
	
	private void parseScheme(ParserHelperContainerObject workerObject )
	{
		for(; workerObject.currentPosition <= workerObject.maxPosition; workerObject.currentPosition++)
		{
			switch(workerObject.readNextCharactor())
			{
				case COLON:
					String scheme = decodeUrl(workerObject.mainStringBuilder.toString());
					if((workerObject.fullPath.length() > (workerObject.currentPosition + 1)) && (workerObject.fullPath.charAt(workerObject.currentPosition + 1) == '/') )
					{
						if((workerObject.fullPath.length() > (workerObject.currentPosition + 2)) && (workerObject.fullPath.charAt(workerObject.currentPosition + 2) == '/') )
						{
							workerObject.currentPosition = workerObject.currentPosition + 3;
							workerObject.containsAuthority = true;
							workerObject.pathIsRelative = false;
						}
						else
						{
							workerObject.currentPosition = workerObject.currentPosition + 2;
							workerObject.containsAuthority = false;
							workerObject.pathIsRelative = true;
						}
						workerObject.uri.scheme = new SchemeComponent(scheme);
						return;
					}
					
					workerObject.containsAuthority = false;
					workerObject.currentPosition = workerObject.currentPosition + 1;
					workerObject.pathIsRelative = true;
					workerObject.uri.scheme = new SchemeComponent(scheme);
					return ; 
				default :
					workerObject.appendCurrentCharacter();
			}
		}
		throw new FormatException("scheme not found: " + workerObject.fullPath);
	}
	
	private static void parseAuthority(ParserHelperContainerObject workerObject )
	{
		workerObject.uri.authority = new AuthorityComponent();
		workerObject.uri.authority.setExpression("");
		
		if(workerObject.currentPosition > workerObject.maxPosition )
		{
			return ;
		}
		
		List<IEncodingExtensionHandler> registeredEncodingExtensionHandler = getEncodingExtensionHandler(ComponentType.AUTHORITY, workerObject.uri);
				
		workerObject.clearStringBuilder();
		workerObject.inIPv6Mode = false;
		workerObject.pathIsEmpty = true;
		workerObject.pathIsRelative = true;
		workerObject.containsQuery = false;
		workerObject.containsFragment = false;
		workerObject.authoritySubComponent = null;
		workerObject.containsExtension = false;
		workerObject.prefixdelimiter = '/';
		workerObject.backup1CurrentPosition = workerObject.currentPosition;
		workerObject.backup2CurrentPosition = workerObject.backup1CurrentPosition;
		
		if(workerObject.extensionHandleObject == null)
		{
			workerObject.extensionHandleObject = new ExtensionHandleObject();
		}
		workerObject.extensionHandleObject.fullPath = workerObject.fullPath;
		workerObject.extensionHandleObject.rawResult = workerObject.mainStringBuilder;
		workerObject.extensionHandleObject.uri = workerObject.uri;
		workerObject.extensionHandleObject.extension = null;
		workerObject.extensionHandleObject.component = ComponentType.AUTHORITY;
		
		fullpathloop :
		for(; workerObject.currentPosition <= workerObject.maxPosition; workerObject.currentPosition++)
		{
			workerObject.readNextCharactor();
			
			for(IEncodingExtensionHandler encodingExtensionHandler : registeredEncodingExtensionHandler)
			{
				workerObject.extensionHandleObject.position = workerObject.currentPosition;
				workerObject.extensionBegin = encodingExtensionHandler.openerCharactersMatched(workerObject.extensionHandleObject);
				if(workerObject.extensionBegin > -1)
				{
					if(workerObject.authoritySubComponent == null)
					{
						workerObject.authoritySubComponent = new AuthoritySubComponent(null,decodeUrl(workerObject.mainStringBuilder.toString()));
						
						workerObject.clearStringBuilder();
						workerObject.authoritySubComponent.setPrefixDelimiter(workerObject.prefixdelimiter);
					}
					else
					{
						workerObject.clearStringBuilder();
					}
					
					workerObject.extensionHandleObject.extension = null;
					workerObject.extensionHandleObject.position = workerObject.extensionBegin;
					workerObject.extensionEnd = encodingExtensionHandler.parseRawExtensionString(workerObject.extensionHandleObject);
					workerObject.currentPosition = workerObject.extensionEnd;
					
					workerObject.containsExtension = true;
					
					if(workerObject.extensionHandleObject.extension != null)
					{
						workerObject.authoritySubComponent.addExtension(workerObject.extensionHandleObject.extension);
					}
					
					if(workerObject.currentPosition > workerObject.maxPosition )
					{
						break fullpathloop;
					}
					
					workerObject.currentPosition--;
					continue fullpathloop;
				}
				
			}
			
			switch(workerObject.currentCharacter)
			{
				case AT_SIGN: // https://tools.ietf.org/html/rfc3986#section-3.2.1		
					if( workerObject.containsExtension)
					{
						workerObject.authoritySubComponent.setExpression(workerObject.fullPath.substring(workerObject.backup2CurrentPosition, workerObject.currentPosition));
					}
					else
					{
						workerObject.expression = workerObject.mainStringBuilder.toString();
						workerObject.value = decodeUrl(workerObject.expression);
						workerObject.authoritySubComponent = new AuthoritySubComponent(workerObject.expression,workerObject.value);
					}
					workerObject.uri.authority.addSubComponent(workerObject.authoritySubComponent);
					workerObject.clearStringBuilder();
					workerObject.authoritySubComponent.setPrefixDelimiter(workerObject.prefixdelimiter);
					workerObject.authoritySubComponent.setPostfixDelimiter(workerObject.currentCharacter);
					workerObject.authoritySubComponent = null;
					workerObject.prefixdelimiter = '@';
					workerObject.containsExtension = false;
					workerObject.backup2CurrentPosition = workerObject.currentPosition + 1;
						
					break;
				case COLON:
					if(workerObject.inIPv6Mode)
					{
						workerObject.appendCurrentCharacter();
					}
					else
					{
						if( workerObject.containsExtension)
						{
							workerObject.authoritySubComponent.setExpression(workerObject.fullPath.substring(workerObject.backup2CurrentPosition, workerObject.currentPosition));
						}
						else
						{
							workerObject.expression = workerObject.mainStringBuilder.toString();
							workerObject.value = decodeUrl(workerObject.expression);
							workerObject.authoritySubComponent = new AuthoritySubComponent(workerObject.expression,workerObject.value);
						}
						workerObject.uri.authority.addSubComponent(workerObject.authoritySubComponent);
						workerObject.authoritySubComponent.setPrefixDelimiter(workerObject.prefixdelimiter);
						workerObject.authoritySubComponent.setPostfixDelimiter(workerObject.currentCharacter);
						workerObject.clearStringBuilder();
						workerObject.authoritySubComponent = null;
						workerObject.prefixdelimiter = ':';
						workerObject.containsExtension = false;
						workerObject.backup2CurrentPosition = workerObject.currentPosition + 1;
					}
					break;
				case SLASH:
					if( workerObject.containsExtension)
					{
						workerObject.authoritySubComponent.setExpression(workerObject.fullPath.substring(workerObject.backup2CurrentPosition, workerObject.currentPosition));
					}
					else
					{
						workerObject.expression = workerObject.mainStringBuilder.toString();
						workerObject.value = decodeUrl(workerObject.expression);
						workerObject.authoritySubComponent = new AuthoritySubComponent(workerObject.expression,workerObject.value);
					}
					workerObject.uri.authority.addSubComponent(workerObject.authoritySubComponent);
					workerObject.uri.authority.setExpression(workerObject.fullPath.substring(workerObject.backup1CurrentPosition, workerObject.currentPosition));
					workerObject.authoritySubComponent.setPrefixDelimiter(workerObject.prefixdelimiter);
					workerObject.authoritySubComponent.setPostfixDelimiter(workerObject.currentCharacter);
					workerObject.pathIsEmpty = false;
					workerObject.pathIsRelative = false;
					workerObject.currentPosition++;
					workerObject.authoritySubComponent = null;
					
					return;
				case QUESTION_MARK:
					if( workerObject.containsExtension)
					{
						workerObject.authoritySubComponent.setExpression(workerObject.fullPath.substring(workerObject.backup2CurrentPosition, workerObject.currentPosition));
					}
					else
					{
						workerObject.expression = workerObject.mainStringBuilder.toString();
						workerObject.value = decodeUrl(workerObject.expression);
						workerObject.authoritySubComponent = new AuthoritySubComponent(workerObject.expression,workerObject.value);
					}
					workerObject.uri.authority.addSubComponent(workerObject.authoritySubComponent);
					workerObject.uri.authority.setExpression(workerObject.fullPath.substring(workerObject.backup1CurrentPosition, workerObject.currentPosition));
					workerObject.authoritySubComponent.setPrefixDelimiter(workerObject.prefixdelimiter);
					workerObject.authoritySubComponent.setPostfixDelimiter(workerObject.currentCharacter);
					workerObject.containsQuery = true;
					workerObject.currentPosition++;
					workerObject.authoritySubComponent = null;
					return;
				case '[': // IPv6 - first char in subcomponent - https://tools.ietf.org/html/rfc3986#section-3.2.2
					if( workerObject.containsExtension)
					{
						break;
					}
					
					if(workerObject.mainStringBuilder.length() == 0)
					{
						workerObject.inIPv6Mode = true;
						workerObject.appendCurrentCharacter();
					}
					else
					{
						throw new FormatException("vorbidden character found in authority: " + workerObject.currentCharacter + " | " + workerObject.fullPath);
					}
					break;
				case ']': // IPv6 - last char in subcomponent -https://tools.ietf.org/html/rfc3986#section-3.2.2
					if( workerObject.containsExtension)
					{
						break;
					}
					
					if
					(
						(workerObject.currentPosition == workerObject.maxPosition ) ||
						(
							(workerObject.fullPath.charAt(workerObject.currentPosition + 1) == '/') ||
							(workerObject.fullPath.charAt(workerObject.currentPosition + 1) == '?') ||
							(workerObject.fullPath.charAt(workerObject.currentPosition + 1) == ':') ||
							(workerObject.fullPath.charAt(workerObject.currentPosition + 1) == '#') 
						)
					)
					{
						workerObject.appendCurrentCharacter();
						workerObject.inIPv6Mode = false;
					}
					else
					{
						throw new FormatException("vorbidden character found in authority: " + workerObject.currentCharacter + " | " + workerObject.fullPath);
					}
					break;
				case FRAGMENT:
					if( workerObject.containsExtension)
					{
						workerObject.authoritySubComponent.setExpression(workerObject.fullPath.substring(workerObject.backup2CurrentPosition, workerObject.currentPosition));
					}
					else
					{
						workerObject.expression = workerObject.mainStringBuilder.toString();
						workerObject.value = decodeUrl(workerObject.expression);
						workerObject.authoritySubComponent = new AuthoritySubComponent(workerObject.expression,workerObject.value);
					}
					workerObject.uri.authority.addSubComponent(workerObject.authoritySubComponent);
					workerObject.uri.authority.setExpression(workerObject.fullPath.substring(workerObject.backup1CurrentPosition, workerObject.currentPosition));
					workerObject.authoritySubComponent.setPrefixDelimiter(workerObject.prefixdelimiter);
					workerObject.authoritySubComponent.setPostfixDelimiter(workerObject.currentCharacter);
					workerObject.containsFragment = true;
					workerObject.currentPosition++;
					workerObject.authoritySubComponent = null;
					return;
				default :
					if(! workerObject.containsExtension)
					{
						workerObject.appendCurrentCharacter();
					}
			}
		}
		if( workerObject.containsExtension)
		{
			workerObject.authoritySubComponent.setExpression(workerObject.fullPath.substring(workerObject.backup2CurrentPosition, workerObject.currentPosition));
		}
		else
		{
			workerObject.expression = workerObject.mainStringBuilder.toString();
			workerObject.value = decodeUrl(workerObject.expression);
			workerObject.authoritySubComponent = new AuthoritySubComponent(workerObject.expression,workerObject.value);
		}
		workerObject.uri.authority.addSubComponent(workerObject.authoritySubComponent);
		workerObject.uri.authority.setExpression(workerObject.fullPath.substring(workerObject.backup1CurrentPosition, workerObject.currentPosition));
		workerObject.authoritySubComponent.setPrefixDelimiter(workerObject.prefixdelimiter);
		workerObject.authoritySubComponent.setPostfixDelimiter('/');
		workerObject.currentPosition++;
		workerObject.authoritySubComponent = null;
	}
	
	private static void parsePath(ParserHelperContainerObject workerObject )
	{
		workerObject.uri.path = new PathComponent(! workerObject.pathIsRelative);
		workerObject.uri.path.setExpression("");
		
		if(workerObject.currentPosition > workerObject.maxPosition )
		{
			if(!workerObject.pathIsRelative)
			{
				workerObject.uri.path.setExpression("/");
			}
			return ;
		}
		
		List<IEncodingExtensionHandler> registeredEncodingExtensionHandler = getEncodingExtensionHandler(ComponentType.PATH, workerObject.uri);
				
		workerObject.clearStringBuilder();
		workerObject.containsQuery = false;
		workerObject.containsFragment = false;
		workerObject.pathSegment = null;
		workerObject.containsExtension = false;
		workerObject.backup1CurrentPosition = workerObject.currentPosition;
		workerObject.backup2CurrentPosition = workerObject.backup1CurrentPosition;
		
		if(workerObject.extensionHandleObject == null)
		{
			workerObject.extensionHandleObject = new ExtensionHandleObject();
		}
		workerObject.extensionHandleObject.fullPath = workerObject.fullPath;
		workerObject.extensionHandleObject.rawResult = workerObject.mainStringBuilder;
		workerObject.extensionHandleObject.uri = workerObject.uri;
		workerObject.extensionHandleObject.extension = null;
		workerObject.extensionHandleObject.component = ComponentType.PATH;
		
		fullpathloop :
		for(; workerObject.currentPosition <= workerObject.maxPosition; workerObject.currentPosition++)
		{
			workerObject.readNextCharactor();
			
			for(IEncodingExtensionHandler encodingExtensionHandler : registeredEncodingExtensionHandler)
			{
				workerObject.extensionHandleObject.position = workerObject.currentPosition;
				workerObject.extensionBegin = encodingExtensionHandler.openerCharactersMatched(workerObject.extensionHandleObject);
				if(workerObject.extensionBegin > -1)
				{
					if(workerObject.pathSegment == null)
					{
						workerObject.pathSegment = new PathSegment(null,decodeUrl(workerObject.mainStringBuilder.toString()));
						
						workerObject.clearStringBuilder();
					}
					else
					{
						workerObject.clearStringBuilder();
					}
					
					workerObject.extensionHandleObject.extension = null;
					workerObject.extensionHandleObject.position = workerObject.extensionBegin;
					workerObject.extensionEnd = encodingExtensionHandler.parseRawExtensionString(workerObject.extensionHandleObject);
					workerObject.currentPosition = workerObject.extensionEnd;
					
					workerObject.containsExtension = true;
					
					if(workerObject.extensionHandleObject.extension != null)
					{
						workerObject.pathSegment.addExtension(workerObject.extensionHandleObject.extension);
					}
					
					if(workerObject.currentPosition > workerObject.maxPosition )
					{
						break fullpathloop;
					}
					
					workerObject.currentPosition--;
					continue fullpathloop;
				}
			}
			
			switch(workerObject.currentCharacter)
			{
				case SLASH:
					if( workerObject.containsExtension)
					{
						workerObject.pathSegment.setExpression(workerObject.fullPath.substring(workerObject.backup2CurrentPosition, workerObject.currentPosition));
					}
					else
					{
						workerObject.expression = workerObject.mainStringBuilder.toString();
						workerObject.value = decodeUrl(workerObject.expression);
						workerObject.pathSegment = new PathSegment(workerObject.expression,workerObject.value);
					}
					workerObject.uri.path.addSubComponent(workerObject.pathSegment);
					workerObject.clearStringBuilder();
					workerObject.pathSegment = null;
					workerObject.containsExtension = false;
					workerObject.backup2CurrentPosition = workerObject.currentPosition + 1;
					
					break;
				case QUESTION_MARK:
					if( workerObject.containsExtension)
					{
						workerObject.pathSegment.setExpression(workerObject.fullPath.substring(workerObject.backup2CurrentPosition, workerObject.currentPosition));
					}
					else
					{
						workerObject.expression = workerObject.mainStringBuilder.toString();
						workerObject.value = decodeUrl(workerObject.expression);
						workerObject.pathSegment = new PathSegment(workerObject.expression,workerObject.value);
					}
					workerObject.uri.path.addSubComponent(workerObject.pathSegment);
					workerObject.uri.path.setExpression((workerObject.pathIsRelative ? "" : "/") + workerObject.fullPath.substring(workerObject.backup1CurrentPosition, workerObject.currentPosition));
					workerObject.containsQuery = true;
					workerObject.currentPosition++;
					workerObject.pathSegment = null;
					return;
			
				case FRAGMENT:
					if( workerObject.containsExtension)
					{
						workerObject.pathSegment.setExpression(workerObject.fullPath.substring(workerObject.backup2CurrentPosition, workerObject.currentPosition));
					}
					else
					{
						workerObject.expression = workerObject.mainStringBuilder.toString();
						workerObject.value = decodeUrl(workerObject.expression);
						workerObject.pathSegment = new PathSegment(workerObject.expression,workerObject.value);
					}
					workerObject.uri.path.addSubComponent(workerObject.pathSegment);
					workerObject.uri.path.setExpression((workerObject.pathIsRelative ? "" : "/") +workerObject.fullPath.substring(workerObject.backup1CurrentPosition, workerObject.currentPosition));
					workerObject.containsFragment = true;
					workerObject.currentPosition++;
					workerObject.pathSegment = null;
					return;
				default :
					if(! workerObject.containsExtension)
					{
						workerObject.appendCurrentCharacter();
					}
				
			}
		}
		if( workerObject.containsExtension)
		{
			workerObject.pathSegment.setExpression(workerObject.fullPath.substring(workerObject.backup2CurrentPosition, workerObject.currentPosition));
		}
		else
		{
			workerObject.expression = workerObject.mainStringBuilder.toString();
			workerObject.value = decodeUrl(workerObject.expression);
			workerObject.pathSegment = new PathSegment(workerObject.expression,workerObject.value);
		}
		workerObject.uri.path.addSubComponent(workerObject.pathSegment);
		workerObject.uri.path.setExpression((workerObject.pathIsRelative ? "" : "/") + workerObject.fullPath.substring(workerObject.backup1CurrentPosition, workerObject.currentPosition));
		workerObject.currentPosition++;
		workerObject.pathSegment = null;
	}
	
	private static void parseQuery(ParserHelperContainerObject workerObject )
	{
		workerObject.uri.query = new QueryComponent();
		workerObject.uri.query.setExpression("");
		
		List<IEncodingExtensionHandler> registeredEncodingExtensionHandler = getEncodingExtensionHandler(ComponentType.QUERY, workerObject.uri);
				
		workerObject.clearStringBuilder();
		workerObject.containsFragment = false;
		workerObject.querySegment = null;
		workerObject.containsExtension = false;
		workerObject.backup1CurrentPosition = workerObject.currentPosition;
		workerObject.backup2CurrentPosition = workerObject.backup1CurrentPosition;
		
		if(workerObject.extensionHandleObject == null)
		{
			workerObject.extensionHandleObject = new ExtensionHandleObject();
		}
		workerObject.extensionHandleObject.fullPath = workerObject.fullPath;
		workerObject.extensionHandleObject.rawResult = workerObject.mainStringBuilder;
		workerObject.extensionHandleObject.uri = workerObject.uri;
		workerObject.extensionHandleObject.extension = null;
		workerObject.extensionHandleObject.component = ComponentType.QUERY;
		
		fullpathloop :
		for(; workerObject.currentPosition <= workerObject.maxPosition; workerObject.currentPosition++)
		{
			workerObject.readNextCharactor();
			
			for(IEncodingExtensionHandler encodingExtensionHandler : registeredEncodingExtensionHandler)
			{
				workerObject.extensionHandleObject.position = workerObject.currentPosition;
				workerObject.extensionBegin = encodingExtensionHandler.openerCharactersMatched(workerObject.extensionHandleObject);
				if(workerObject.extensionBegin > -1)
				{
					if(workerObject.querySegment  == null)
					{
						workerObject.querySegment = new QuerySegment(workerObject.expression,workerObject.qtype,workerObject.qname,workerObject.qformat,workerObject.qvalue);
						
						workerObject.clearStringBuilder();
					}
					else
					{
						workerObject.clearStringBuilder();
					}
					
					workerObject.extensionHandleObject.extension = null;
					workerObject.extensionHandleObject.position = workerObject.extensionBegin;
					workerObject.extensionEnd = encodingExtensionHandler.parseRawExtensionString(workerObject.extensionHandleObject);
					workerObject.currentPosition = workerObject.extensionEnd;
					
					workerObject.containsExtension = true;
					
					if(workerObject.extensionHandleObject.extension != null)
					{
						workerObject.querySegment.addExtension(workerObject.extensionHandleObject.extension);
					}
					
					if(workerObject.currentPosition > workerObject.maxPosition )
					{
						break fullpathloop;
					}
					
					workerObject.currentPosition--;
					continue fullpathloop;
				}
			}
			
			switch(workerObject.currentCharacter)
			{
				case COLON:
					if( workerObject.containsExtension)
					{
						break;
					}
					
					if(! workerObject.qtypeParsed)
					{
						workerObject.qtype = decodeUrl(workerObject.mainStringBuilder.toString());
						workerObject.qtypeParsed = true;
						workerObject.clearStringBuilder();
						break;
					}
					if(workerObject.qnameParsed && (! workerObject.qformatParsed))
					{
						workerObject.qformat = decodeUrl(workerObject.mainStringBuilder.toString());
						workerObject.qformatParsed = true;
						workerObject.clearStringBuilder();
						
						if(workerObject.qformat.equals("string"))
						{
							workerObject.currentPosition++;
							handleString(workerObject);
						}
						if(workerObject.qformat.equals("json"))
						{
							workerObject.currentPosition++;
							handleJSON(workerObject);
						}
						break;
					}
					workerObject.appendCurrentCharacter();
					break;
				case EQUAL:
					if( workerObject.containsExtension)
					{
						break;
					}
					if(! workerObject.qnameParsed)
					{
						workerObject.qname = decodeUrl(workerObject.mainStringBuilder.toString());
						workerObject.qtypeParsed = true;
						workerObject.qnameParsed = true;
						workerObject.clearStringBuilder();
						break;
					}
					workerObject.appendCurrentCharacter();
					break;
				case AND:
					
					if( workerObject.containsExtension)
					{
						workerObject.querySegment.setExpression(workerObject.fullPath.substring(workerObject.backup2CurrentPosition, workerObject.currentPosition));
					}
					else
					{
						workerObject.expression = workerObject.fullPath.substring(workerObject.backup2CurrentPosition, workerObject.currentPosition);
						if(workerObject.qtypeParsed && (! workerObject.qnameParsed))
						{
							workerObject.qformat = workerObject.qtype;
							workerObject.qtype = null;
						}
						workerObject.qvalue = decodeUrl(workerObject.mainStringBuilder.toString());
						
						workerObject.querySegment = new QuerySegment(workerObject.expression,workerObject.qtype,workerObject.qname,workerObject.qformat,workerObject.qvalue);
					}
					workerObject.uri.query.addSubComponent(workerObject.querySegment);
					workerObject.clearStringBuilder();
					workerObject.querySegment = null;
					workerObject.containsExtension = false;
					workerObject.resetQueryValue();
					workerObject.backup2CurrentPosition = workerObject.currentPosition + 1;
					
					break;
				case FRAGMENT:
					if( workerObject.containsExtension)
					{
						workerObject.querySegment.setExpression(workerObject.fullPath.substring(workerObject.backup2CurrentPosition, workerObject.currentPosition));
					}
					else
					{
						workerObject.expression = workerObject.fullPath.substring(workerObject.backup2CurrentPosition, workerObject.currentPosition);
						if(workerObject.qtypeParsed && (! workerObject.qnameParsed))
						{
							workerObject.qformat = workerObject.qtype;
							workerObject.qtype = null;
						}
						workerObject.qvalue = decodeUrl(workerObject.mainStringBuilder.toString());
						workerObject.querySegment = new QuerySegment(workerObject.expression,workerObject.qtype,workerObject.qname,workerObject.qformat,workerObject.qvalue);
					}
					workerObject.uri.query.setExpression(workerObject.fullPath.substring(workerObject.backup1CurrentPosition, workerObject.currentPosition));
					workerObject.uri.query.addSubComponent(workerObject.querySegment);
					workerObject.clearStringBuilder();
					workerObject.querySegment = null;
					workerObject.containsExtension = false;
					workerObject.resetQueryValue();
					workerObject.containsFragment = true;
					workerObject.currentPosition++;
					return;
				default :
					if(! workerObject.containsExtension)
					{
						workerObject.appendCurrentCharacter();
					}
				
			}
		}
		if( workerObject.containsExtension)
		{
			workerObject.querySegment.setExpression(workerObject.fullPath.substring(workerObject.backup2CurrentPosition, workerObject.currentPosition));
		}
		else
		{
			workerObject.expression = workerObject.fullPath.substring(workerObject.backup2CurrentPosition, workerObject.currentPosition);
			if(workerObject.qtypeParsed && (! workerObject.qnameParsed))
			{
				workerObject.qformat = workerObject.qtype;
				workerObject.qtype = null;
			}
			workerObject.qvalue = decodeUrl(workerObject.mainStringBuilder.toString());
			workerObject.querySegment = new QuerySegment(workerObject.expression,workerObject.qtype,workerObject.qname,workerObject.qformat,workerObject.qvalue);
		}
		workerObject.uri.query.setExpression(workerObject.fullPath.substring(workerObject.backup1CurrentPosition, workerObject.currentPosition));
		workerObject.uri.query.addSubComponent(workerObject.querySegment);
		workerObject.clearStringBuilder();
		workerObject.querySegment = null;
		workerObject.containsExtension = false;
		workerObject.resetQueryValue();
		workerObject.currentPosition++;
	}
	
	private static void parseFragment(ParserHelperContainerObject workerObject )
	{		
		List<IEncodingExtensionHandler> registeredEncodingExtensionHandler = getEncodingExtensionHandler(ComponentType.FRAGMENT, workerObject.uri);
				
		workerObject.clearStringBuilder();
		workerObject.containsExtension = false;
		workerObject.backup1CurrentPosition = workerObject.currentPosition;
		workerObject.backup2CurrentPosition = workerObject.backup1CurrentPosition;
		
		if(workerObject.extensionHandleObject == null)
		{
			workerObject.extensionHandleObject = new ExtensionHandleObject();
		}
		workerObject.extensionHandleObject.fullPath = workerObject.fullPath;
		workerObject.extensionHandleObject.rawResult = workerObject.mainStringBuilder;
		workerObject.extensionHandleObject.uri = workerObject.uri;
		workerObject.extensionHandleObject.extension = null;
		workerObject.extensionHandleObject.component = ComponentType.FRAGMENT;
		
		fullpathloop :
		for(; workerObject.currentPosition <= workerObject.maxPosition; workerObject.currentPosition++)
		{
			workerObject.readNextCharactor();
			
			for(IEncodingExtensionHandler encodingExtensionHandler : registeredEncodingExtensionHandler)
			{
				workerObject.extensionHandleObject.position = workerObject.currentPosition;
				workerObject.extensionBegin = encodingExtensionHandler.openerCharactersMatched(workerObject.extensionHandleObject);
				if(workerObject.extensionBegin > -1)
				{
					if(workerObject.uri.fragment == null)
					{
						workerObject.uri.fragment = new FragmentComponent(decodeUrl(workerObject.mainStringBuilder.toString()));
						workerObject.uri.fragment.setExpression("");
					}
					workerObject.clearStringBuilder();
					
					workerObject.clearStringBuilder();
					workerObject.extensionHandleObject.extension = null;
					workerObject.extensionHandleObject.position = workerObject.extensionBegin;
					workerObject.extensionEnd = encodingExtensionHandler.parseRawExtensionString(workerObject.extensionHandleObject);
					workerObject.currentPosition = workerObject.extensionEnd;
					
					workerObject.containsExtension = true;
					
					if(workerObject.extensionHandleObject.extension != null)
					{
						workerObject.querySegment.addExtension(workerObject.extensionHandleObject.extension);
					}
					
					if(workerObject.currentPosition > workerObject.maxPosition )
					{
						break fullpathloop;
					}
					
					workerObject.currentPosition--;
					continue fullpathloop;
				}
			}
			
			if(! workerObject.containsExtension)
			{
				workerObject.appendCurrentCharacter();
			}
		}
		
		if(workerObject.uri.fragment == null)
		{
			workerObject.uri.fragment = new FragmentComponent(decodeUrl(workerObject.mainStringBuilder.toString()));
		}
		workerObject.uri.fragment.setExpression(workerObject.fullPath.substring(workerObject.backup1CurrentPosition, workerObject.currentPosition));
		workerObject.clearStringBuilder();
		workerObject.containsExtension = false;
		workerObject.resetQueryValue();
		workerObject.currentPosition++;
	}

    private static String decodeUrl(String raw) 
    {
    	if (raw.indexOf(PERCENT_SIGN) < 0)
    	{
    		return raw;
    	}
    	return URLDecoder.decode(raw);
    }
    
    private static void handleString(ParserHelperContainerObject workerObject)
    {
    	workerObject.quoteChar = SINGLE_QUOTE;
    	workerObject.escapeChar = BACKSLASH;
    	workerObject.inEscape = false;
    	
    	if(workerObject.readNextCharactor() == workerObject.quoteChar)
    	{
    		workerObject.currentPosition++;
    	}
    	else
    	{
    		throw new RuntimeException("encoded string parameter must start with \' | " + workerObject.currentCharacter + " : " + workerObject.fullPath);
    	}
    	
		for(; workerObject.currentPosition <= workerObject.maxPosition; workerObject.currentPosition++)
		{
			workerObject.readNextCharactor();
			
			if(workerObject.inEscape)
			{
				workerObject.inEscape = false;
				if(workerObject.currentCharacter != workerObject.quoteChar)
				{
					workerObject.mainStringBuilder.append(workerObject.escapeChar);
				}
				workerObject.appendCurrentCharacter();
				continue;
			}
			
			if(workerObject.currentCharacter == workerObject.escapeChar)
			{
				workerObject.inEscape  = true;
				continue;
			}
			
			if(workerObject.currentCharacter == workerObject.quoteChar)
			{
				workerObject.clearParser();
				return;
			}
			
			workerObject.appendCurrentCharacter();
		}
		
		workerObject.clearParser();
		throw new FormatException("no closing sequence \"" + workerObject.escapeChar + "\" found in string parameter " + " : " + workerObject.fullPath);
    }
    
    private static void handleJSON(ParserHelperContainerObject workerObject)
    {
    	workerObject.quoteChar = DOUBLE_QUOTE;
    	workerObject.escapeChar = BACKSLASH;
    	workerObject.inEscape = false;
    	workerObject.nestedLevel = 0;
    	
    	if(workerObject.readNextCharactor() == '{')
    	{
    		workerObject.currentPosition++;
    		workerObject.nestedLevel++;
    		workerObject.appendCurrentCharacter();
    	}
    	else
    	{
    		throw new RuntimeException("encoded json parameter must start with { | " + workerObject.currentCharacter + " : " + workerObject.fullPath);
    	}
    	
		for(; workerObject.currentPosition <= workerObject.maxPosition; workerObject.currentPosition++)
		{
			workerObject.readNextCharactor();
			workerObject.appendCurrentCharacter();
			
			if(workerObject.currentCharacter == workerObject.quoteChar)
			{
				if(workerObject.inQuote)
				{
					if(workerObject.inEscape)
					{
						workerObject.inEscape = false;
					}
					else
					{
						workerObject.inQuote = false;
					}
				}
				else
				{
					workerObject.inQuote = true;
				}
				continue;
			}
			
			if(workerObject.currentCharacter == workerObject.escapeChar)
			{
				if(workerObject.inQuote)
				{
					workerObject.inEscape = ! workerObject.inEscape;
				}
				continue;
			}
			
			if(workerObject.inQuote)
			{
				continue;
			}
			
			if(workerObject.readNextCharactor() == '{')
	    	{
				workerObject.nestedLevel++;
	    	}
			
			if(workerObject.readNextCharactor() == '}')
	    	{
				workerObject.nestedLevel--;
				
				if(workerObject.nestedLevel == 0)
				{
					workerObject.clearParser();
					return;
				}
	    	}
		}
		workerObject.clearParser();
		throw new FormatException("no closing sequence } found in json parameter " + " : " + workerObject.fullPath);
    }
    
	/*private static void parseFilterArea(StringBuilder sb,URIPathItem uriPathItem)
	{
		UnparsedFilterPart rootFilterPart = new UnparsedFilterPart();
		rootFilterPart.raw = sb.toString();
		rootFilterPart.parentPathItem = uriPathItem;
		List<UnparsedFilterPart> toParse = new ArrayList<UnparsedFilterPart>();
		toParse.add(rootFilterPart);
		
		clearStringBuilder(sb);
		
		while(toParse.size() > 0)
		{
			UnparsedFilterPart filterPart = toParse.remove(0);
			
			boolean simpleMode = true;
			
			if(filterPart.raw.indexOf('(') > -1){simpleMode = false;}
			
			boolean invert = false;
			FilterItemLinker.Operation linkOperation = FilterItemLinker.Operation.AND;
			
			if(! simpleMode)
			{
				List<String> filterItemList = new ArrayList<String>();
				boolean initSection = true;
				int over = 0;
				int posFirst = -1;
				char c;
				for(int i = 0; i < filterPart.raw.length(); i++)
				{
					c = filterPart.raw.charAt(i);
					if(c == ' ')
					{
						continue;
					}
					if(initSection)
					{
						if(c == '!')
						{
							invert = true;
							continue;
						}
						if(c == '&')
						{
							linkOperation = FilterItemLinker.Operation.AND;
							continue;
						}
						if(c == '|')
						{
							linkOperation = FilterItemLinker.Operation.OR;
							continue;
						}
						if(c == '^')
						{
							linkOperation = FilterItemLinker.Operation.XOR;
							continue;
						}
						initSection = false;
					}
					
					if(c == '(')
					{
						if(over == 0)
						{
							posFirst = i;
						}
						over++;
					}
					else if(c == ')')
					{
						over--;
						if(over == 0)
						{
							String filterItemString = filterPart.raw.substring(posFirst+1, i);
							filterItemList.add(filterItemString);
						}
						if(over < 0)
						{
							throw new FormatException("FilterItem malformed: " + filterPart.raw);
						}
					}
				}
				
				if(filterItemList.size() == 0)
				{
					throw new FormatException("FilterItem malformed: " + filterPart.raw);
				}
				
				if(over > 0)
				{
					throw new FormatException("FilterItem malformed: " + filterPart.raw);
				}
				
				if(filterItemList.size() == 0)
				{
					continue;
				}
				
				if((filterItemList.size() == 1) && (filterItemList.get(0).indexOf("(") < 0))
				{
					filterPart.raw = filterItemList.get(0);
				}
				else
				{
					FilterItemLinker linker = new FilterItemLinker();
					linker.setInvert(invert);
					linker.setOperation(linkOperation);
					
					if(filterPart.parentPathItem != null)
					{
						((URIPathItem )filterPart.parentPathItem).setFilterItem(linker);
					}
					else
					{
						filterPart.parentLinker.getLinkItemList().add(linker);
					}
					
					for(String  part : filterItemList)
					{
						UnparsedFilterPart newUnparsed = new UnparsedFilterPart();
						newUnparsed.parentLinker = linker;
						newUnparsed.raw = part;
						toParse.add(newUnparsed);
					}
					continue;
				}
			}
			
			String simpleFilterToParse = filterPart.raw;
			
			URIPathItemFilter filter = new URIPathItemFilter();
			filter.setInvert(invert);
			
			// key: keyType:key (buchstaben/zahl

			int operatorBegin = -1;
			int valueBegin = -1;
			
			char c;
			for(int i = 0; i < simpleFilterToParse.length(); i++)
			{
				c = simpleFilterToParse.charAt(i);
				if(c == ' ')
				{
					continue;
				}
				
				if(operatorBegin > -1)
				{
					if
					(
						((c >= 'a') && (c <= 'z')) ||
						((c >= 'A') && (c <= 'Z')) ||
						((c >= '0') && (c <= '9')) || 
						((c == '\'') || (c == '"'))
					)
					{
						valueBegin = i;
						filter.setOperator(simpleFilterToParse.substring(operatorBegin, i).trim());
						//System.out.println(".." + filter.getOperator() + "..");
						break;
					}
				}
				
				if(operatorBegin < 0)
				{
					if
					(
						((c >= 'a') && (c <= 'z')) ||
						((c >= 'A') && (c <= 'Z')) ||
						((c >= '0') && (c <= '9')) ||
						((c == '.') || (c == ':') | (c == '-') || (c == '_') )
					)
					{
						continue;
					}
					
					filter.setKey(simpleFilterToParse.substring(0, i).trim());
					operatorBegin = i;
					continue;
					
				}
			}
			
			if(valueBegin > -1)
			{
				filter.setValue(decodeString(simpleFilterToParse.substring(valueBegin,simpleFilterToParse.length()).trim()));
				//System.out.println("..." + filter.getValue() + "...");
			}
			else if(operatorBegin > -1)
			{
				filter.setOperator(simpleFilterToParse.substring(operatorBegin, simpleFilterToParse.length()).trim());
				//System.out.println(".." + filter.getOperator() + "..");
			}
			
			
			if(filterPart.parentPathItem != null)
			{
				((URIPathItem )filterPart.parentPathItem).setFilterItem(filter);
			}
			else
			{
				filterPart.parentLinker.getLinkItemList().add(filter);
			}
			
		}
		
	}*/
	
	private class ParserHelperContainerObject
	{
		private char currentCharacter = '.';
		private String fullPath;
		
		private boolean containsExtension = true;
		private boolean pathIsEmpty = false;
		private boolean pathIsRelative = false;
		private boolean containsAuthority = true;
		private boolean containsPath = true;
		private boolean containsQuery = false;
		private boolean containsFragment = false;
		private char prefixdelimiter = ':';
		private int currentPosition = 0;
		private int backup1CurrentPosition = 0;
		private int backup2CurrentPosition = 0;
		private int maxPosition = -1;
		private int extensionBegin = -1;
		private int extensionEnd = -1;
		private String value;
		private String expression;
		private StringBuilder mainStringBuilder = new StringBuilder();
		private URI uri = null;
		private ExtensionHandleObject extensionHandleObject;
		
		private char readNextCharactor()
		{
			this.currentCharacter = fullPath.charAt(currentPosition);
			return this.currentCharacter;
		}
		
		// authority
		private boolean inIPv6Mode = false;
		private AuthoritySubComponent authoritySubComponent = null;
		
		// path
		private PathSegment pathSegment = null;
		
		// query 
		private QuerySegment querySegment = null;
		private String qtype = null;
		private String qname = null;
		private String qformat = null;
		private String qvalue = null;
		
		private boolean qtypeParsed = false;
		private boolean qnameParsed = false;
		private boolean qformatParsed = false;
		private boolean qvalueEncoded = false;
		
		private void resetQueryValue()
		{
			qtype = null;
			qname = null;
			qformat = null;
			qvalue = null;
			qtypeParsed = false;
			qnameParsed = false;
			qformatParsed = false;
			qvalueEncoded = false;
		}
		
		// parser
		private char quoteChar = SINGLE_QUOTE;
		private char escapeChar = BACKSLASH;
		private boolean inEscape = false;
		
		private boolean inQuote = false;
		private String quoteBegin = "";
		private String quoteEnd = "";
		private int nestedLevel = 0;
		
		private void clearParser()
		{
			quoteChar = SINGLE_QUOTE;
			escapeChar = BACKSLASH;
			inEscape = false;
			
			inQuote = false;
			quoteBegin = "";
			quoteEnd = "";
			nestedLevel = 0;
		}
		
		
		private void appendCurrentCharacter()
		{
			this.mainStringBuilder.append(currentCharacter);
		}
		
		private void clearStringBuilder()
		{
			if(mainStringBuilder.length() > 0)
			{
				mainStringBuilder.delete(0, mainStringBuilder.length() );
			}
		}
	}
}
