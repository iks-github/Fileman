#set( $classDescriptor = $model.getClassDescriptor("Tenant") )
#set( $className = $TemplateStringUtility.firstToLowerCase($classDescriptor.simpleName)) 

@TargetFileName fileman-${className}-table-layout-component.html
@TargetDir ..\\..\\Fileman-Frontend\\src\app\components\layout\\${className}\table-layout
@NameOfValidModel SpringBootBackendAngularFrontModel
@ReplaceStart "<!-- The row-td-sections below is generated: Do not modify manually! -->"
@ReplaceEnd "<!-- The row-td-sections above is generated: Do not modify manually! -->"

#parse("commonSubtemplates/FrontendHtmlTableRows.tpl")
