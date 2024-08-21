grammar StructurizrDSL;

workspace: 'workspace' (name=(ID | QUOTED_STRING) (description=QUOTED_STRING)?)? '{' identifierMode? model? views? properties? '}';

identifierMode: '!identifiers' ('hierarchical'|'flat');
model: 'model' '{' (person | softwareSystem | relationship)* '}';
views: 'views' '{' (systemContextView | dynamicView | systemLandscapeView)*  '}';

person: (id=ID '=')? 'person' name=(ID | QUOTED_STRING) (description=QUOTED_STRING)?;
softwareSystem: (id=ID '=')? 'softwareSystem' name=(ID | QUOTED_STRING) (description=QUOTED_STRING)? ('{' (container)* '}')?;
container: id=ID '=' 'container' name=(ID | QUOTED_STRING) (description=QUOTED_STRING)? ('{' (component)* '}')?;
component: id=ID '=' 'component' name=(ID | QUOTED_STRING) (description=QUOTED_STRING)?;
relationship: (id=ID '=')? source=ID '->' destination=ID (description=QUOTED_STRING)?;

systemContextView: 'systemContext' softwareSystemId=ID '{' include* exclude* autolayout? '}';
systemLandscapeView: 'systemLandscape' '{' include* exclude* '}';
dynamicView: 'dynamic' STAR '{' (relationship)*  (autolayout)? '}';

include: 'include' (STAR | idList);
exclude: 'exclude' (STAR | idList);

properties: 'properties' '{' (property)* '}';
property: key=QUOTED_STRING value=QUOTED_STRING;

autolayout: 'autolayout' | 'autoLayout';

idList: ID (ID)*;

ID: [a-zA-Z_][a-zA-Z_0-9]*;
QUOTED_STRING
   :   '"' (ESC | ~["\\])* '"'
   {setText(getText().substring(1, getText().length()-1));}
   ;

ESC : '\\' 'n' {setText("\n");} ;
STAR: '*';
WS: [ \t\r\n]+ -> skip;

// Lexer rules for comments
COMMENT
   : '/*' .*? '*/' -> skip
   ;
LINE_COMMENT
   : ('//' | '#') ~[\r\n]* -> skip
   ;
