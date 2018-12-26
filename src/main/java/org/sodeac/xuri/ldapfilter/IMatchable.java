package org.sodeac.xuri.ldapfilter;

/**
 * Interface of matchable property
 * 
 * @author Sebastian Palarus
 * @since 1.0
 * @version 1.0
 *
 */
public interface IMatchable
{
	/**
	 * returns matchable match to ldap attribute
	 * 
	 * @param operator ldap operator
	 * @param name name of ldap attribute
	 * @param valueExpression value of ldap attribute
	 * 
	 * @return matchable match to atomic ldap expression
	 */
	public boolean matches(ComparativeOperator operator, String name, String valueExpression);
}