grammar StructurizrDSL;

workspace: 'workspace' (name=STRING (description=STRING)?)? '{' model? views? properties? '}';

model: 'model' '{' (person | softwareSystem | relationship)* '}';
views: 'views' '{' (systemContext | dynamic)*  '}';

person: id=ID '=' 'person' name=STRING (description=STRING)?;
softwareSystem: id=ID '=' 'softwareSystem' name=STRING (description=STRING)?;
relationship: source=ID '->' destination=ID (description=STRING)?;

systemContext: 'systemContext' softwareSystemId=ID '{' include* autolayout? '}';
include: 'include' (STAR | idList);

dynamic: 'dynamic' STAR '{' (relationship)*  (autolayout)? '}';

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