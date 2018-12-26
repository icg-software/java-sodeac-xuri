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
 * https://tools.ietf.org/html/rfc3986#section-3.4
 */

/**
 * Query component of URI. Query components contains multiple subcomponents of type {@link QuerySegment}. 
 * 
 * @author Sebastian Palarus
 * @since 1.0
 * @version 1.0
 *
 */
public class QueryComponent extends AbstractComponent<QuerySegment>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 393235970805892989L;

	public QueryComponent()
	{
		super(ComponentType.QUERY);
	}

}
