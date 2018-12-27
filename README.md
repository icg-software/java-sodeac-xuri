
[![Build Status](https://travis-ci.org/spalarus/java-sodeac-xuri.svg?branch=master)](https://travis-ci.org/spalarus/java-sodeac-xuri)
# Extensible URI parser
URI parser with extensible capabilities.

## Purpose

xURI enables to use customized deserializer while parsing URI string.

## Maven

```xml
<dependency>
  <groupId>org.sodeac</groupId>
  <artifactId>org.sodeac.xuri</artifactId>
  <version>1.0.0</version>
</dependency>
```

## Getting Started

```java

// define common uri
URI uri = new URI("http://nobody:password@example.org:8080/cgi-bin/script.php?action=submit&pageid=86392001#section_2");
		
SchemeComponent scheme = uri.getScheme();       // contains no subcomponent
AuthorityComponent authority = uri.getAuthority(); // contains multiple subcomponents of type AuthoritySubComponent
PathComponent path = uri.getPath(); // contains multiple subcomponents of type PathSegment
QueryComponent query = uri.getQuery(); // contains multiple subcomponents of type PathSegment
FragmentComponent fragment = uri.getFragment(); // contains no subcomponent
	
// scheme string
String proto = scheme.getValue(); // http
		
// password string
String password = authority.getSubComponentList().stream()
	.filter((s) -> s.getPrefixDelimiter() == ':')
	.filter((s) -> s.getPostfixDelimiter() == '@')
	.findFirst()
	.get()
	.getValue(); // password
		
// pageName
String pageName = path.getSubComponentList().stream()
	.reduce((first, second) -> second)
	.get()
	.getValue(); // script.php
		
// page id
String  pageId = query.getSubComponentList().stream()
	.filter((q) -> q.getName().equals("pageid"))
	.findFirst()
	.get()
	.getValue(); // 86392001
		
String section = fragment.getValue(); // section_2
```
### String formated values in URI query

Encapsulate query values in single quotes for unlimited character set.

```java

// define uri with string formated querysegment  => structure ${name}=string:'${value}'
URI uri = new URI("sdc:?timeout=1ms&filter=string:'John * & \\' Doe'");

// first query segment

QuerySegment querySeg1 = uri.getQuery().getSubComponentList().get(0);
String name1 = querySeg1.getName(); // timeout
String value1 = querySeg1.getValue(); // 1ms

// second query segment

QuerySegment querySeg2 = uri.getQuery().getSubComponentList().get(1);
String name2 = querySeg2.getName(); // filter
String value2 = querySeg2.getValue(); // John * & ' Doe
    
```

### JSON formated values in URI query

Set JSON string as query value.

```java

// define uri with json formated querysegment 
URI uri = new URI("sdc:?org.sodec.utils.SecurityToken:token=json:{\"id\":13, \"token\":\"070bd30d-eb6b-46ec-8d58-301adfe38c19\", signer:{\"id\":1, \"token\":\"dc2a5993-f2b4-46f2-a7a2-e2a4376f9df7\"} }");
		
QuerySegment querySeg = uri.getQuery().getSubComponentList().get(0);
String type = querySeg.getType(); // org.sodec.utils.SecurityToken
String name = querySeg.getName();> // token
String format = querySeg.getFormat(); // json
String value = querySeg.getValue(); // {"id":13, "token":"070bd30d-eb6b-46ec-8d58-301adfe38c19", signer:{"id":1, "token":"dc2a5993-f2b4-46f2-a7a2-e2a4376f9df7"} }
    
```

### LDAP filter extension

Example to use ldap filter expression in authority section. 

```java

// define uri with ldap filter extension
URI uri = new URI("sdc://localnode:eventdispatcher(|(id=default)(id=user))/org.sodeac.user.service");

// ldap filter extension from second authority segment
IExtension<IFilterItem> ldapFilterExtension = (IExtension<IFilterItem>) uri.getAuthority().getSubComponentList().get(1).getExtension(LDAPFilterExtension.TYPE);

// decode filterstring
AttributeLinker attributeLinker = (AttributeLinker)ldapFilterExtension.decodeFromString(ldapFilterExtension.getExpression());
Attribute attribute1 = (Attribute)attributeLinker.getLinkedItemList().get(0);
Attribute attribute2 = (Attribute)attributeLinker.getLinkedItemList().get(1);

// user ldap filter object
LogicalOperator op = attributeLinker.getOperator(); // OR
String attr1 = attribute1.getName() + attribute1.getOperator().getAbbreviation() +  attribute1.getValue(); // id=default
String attr2 = attribute2.getName() + attribute2.getOperator().getAbbreviation() +  attribute2.getValue(); // id=user
```

## License
[Eclipse Public License 2.0](https://github.com/spalarus/java-sodeac-xuri/blob/master/LICENSE)
