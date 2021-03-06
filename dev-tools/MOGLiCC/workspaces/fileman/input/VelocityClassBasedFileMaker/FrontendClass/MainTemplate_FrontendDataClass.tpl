#set( $notNeeded = $classDescriptor.getMetaInfoValueFor("FrontendService").contains("NOT FOUND") || 
                   ! $classDescriptor.getMetaInfoValueFor("FrontendService").equalsIgnoreCase("true") )

@TargetFileName ${classDescriptor.simpleName}.ts # Name of output file with extension but without path
@TargetDir $model.getMetaInfoValueFor("frontendSrcDir")/common/domainobjects/gen
@CreateNew true # creates target dir if not existing and overwrites target file if existing
@NameOfValidModel SpringBootBackendAngularFrontModel
@SkipGeneration $notNeeded 

#set( $attributeDescriptorList = $classDescriptor.getAttributeDescriptorList() )

#foreach ($attributeDescriptor in $attributeDescriptorList)

	#parse("FrontendType.tpl")

	#if( $isDomainType )
		'import { $JavaType } from 'src/app/common/domainobjects/gen/$JavaType';
	#elseif ( $isDomainTypeArray )
	    #set( $innerType = $TemplateStringUtility.replaceAllIn($frontendType, "[]", "") )
		'import { $innerType } from 'src/app/common/domainobjects/gen/$innerType';
	#end
#end
'
export class ${classDescriptor.simpleName}
{
#foreach ($attributeDescriptor in $attributeDescriptorList)
	
	#parse("FrontendType.tpl")
	
	#set( $attributeName = $TemplateStringUtility.firstToLowerCase($attributeDescriptor.name) ) 
	#set( $attributeName = $TemplateStringUtility.replaceAllIn($attributeName, " ", "") ) 

	'    $attributeName: $frontendType;
#end

'
'    constructor(untyped${classDescriptor.simpleName}: any) {
'        if (untyped${classDescriptor.simpleName} != null) {

#foreach ($attributeDescriptor in $attributeDescriptorList)
	
	#set( $attributeName = $TemplateStringUtility.replaceAllIn($attributeDescriptor.name, " ", "") ) 
	'            this.$attributeName = untyped${classDescriptor.simpleName}.$attributeName;
#end

'        }
'    }
'

#parse("getAttributeNamesMethod.tpl")

#parse("GetterMethods.tpl")

#parse("SetterMethods.tpl")

#parse("EqualsMethod.tpl")

#parse("getStringRepresentation.tpl")
}
'