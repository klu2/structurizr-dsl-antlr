grammar StructurizrDSL;

workspace: 'workspace' (name=STRING (description=STRING)?)? '{' model? views? properties? '}';

model: 'model' '{' (person | softwareSystem | relationship)* '}';
views: 'views' '{' (systemContextView | dynamicView | systemLandscapeView)*  '}';

person: id=ID '=' 'person' name=STRING (description=STRING)?;
softwareSystem: (id=ID '=')? 'softwareSystem' name=STRING (description=STRING)? ('{' (container)* '}')?;
container: id=ID '=' 'container' name=STRING (description=STRING)? ('{' (component)* '}')?;
component: id=ID '=' 'component' name=STRING (description=STRING)?;
relationship: (id=ID '=')? source=ID '->' destination=ID (description=STRING)?;

systemContextView: 'systemContext' softwareSystemId=ID '{' include* exclude* autolayout? '}';
systemLandscapeView: 'systemLandscape' '{' include* exclude* '}';
dynamicView: 'dynamic' STAR '{' (relationship)*  (autolayout)? '}';

include: 'include' (STAR | idList);
exclude: 'exclude' (STAR | idList);

properties: 'properties' '{' (property)* '}';
property: key=STRING value=STRING;

autolayout: 'autolayout' | 'autoLayout';

idList: ID (ID)*;

ID: [a-zA-Z_][a-zA-Z_0-9]*;
STRING
   :   '"' (ESC | ~["\\])* '"'
   {setText(getText().substring(1, getText().length()-1));}
   ;
ESC : '\\' 'n' {setText("\n");} ;
STAR: '*';
WS: [ \t\r\n]+ -> skip;

// Lexer rules for comments
COMMENT
   :   '/*' .*? '*/' -> skip
   ;
LINE_COMMENT
   :   ('//' | '#') ~[\r\n]* -> skip
   ;