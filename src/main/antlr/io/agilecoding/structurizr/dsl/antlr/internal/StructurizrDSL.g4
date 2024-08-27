grammar StructurizrDSL;

workspace: 'workspace' (name=identifier (description=QUOTED_STRING)?)? '{' identifierMode? model? views? properties? '}';

identifierMode: '!identifiers' ('hierarchical'|'flat');
model: 'model' '{' (person | softwareSystem | relationship)* '}';
views: 'views' '{' (systemContextView | dynamicView | systemLandscapeView)*  '}';

person: (id=ID '=')? 'person' name=identifier (description=QUOTED_STRING)? ('{' (relationship)* '}')?;
softwareSystem: (id=ID '=')? 'softwareSystem' name=identifier (description=QUOTED_STRING)? ('{' (container)* '}')?;
container: id=ID '=' 'container' name=identifier (description=QUOTED_STRING)? ('{' (component)* '}')?;
component: id=ID '=' 'component' name=identifier (description=QUOTED_STRING)?;
relationship: ((id=ID '=')? source=ID)? '->' destination=ID (description=QUOTED_STRING)?;

systemContextView: 'systemContext' softwareSystemId=ID '{' viewConfiguration* '}';
systemLandscapeView: 'systemLandscape' '{' viewConfiguration* '}';
dynamicView: 'dynamic' star '{' (relationship)*  (autolayout)? '}';

viewConfiguration: include | exclude | autolayout;
include: 'include' elementIdentifier;
exclude: 'exclude' elementIdentifier;

properties: 'properties' '{' (property)* '}';
property: key=QUOTED_STRING value=QUOTED_STRING;

autolayout: 'autolayout' | 'autoLayout';

elementIdentifier: starRelationship | star | idList;

starRelationship: STAR_RELATIONSHIP;
star: STAR;

idList: ID (ID)*;

identifier: ID | QUOTED_STRING;

ID: [a-zA-Z_][a-zA-Z_0-9]*;
QUOTED_STRING
   :   '"' (ESC | ~["\\])* '"'
   {setText(getText().substring(1, getText().length()-1));}
   ;

ESC : '\\' 'n' {setText("\n");} ;
STAR: '*';
STAR_RELATIONSHIP: '*->*';
WS: [ \t\r\n]+ -> skip;

// Lexer rules for comments
COMMENT
   : '/*' .*? '*/' -> skip
   ;
LINE_COMMENT
   : ('//' | '#') ~[\r\n]* -> skip
   ;
