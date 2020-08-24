
#foreach ($attributeDescriptor in $attributeDescriptorList)

	#if ( $attributeDescriptor.doesHaveAnyMetaInfosWithName("guiType"))

		#set( $attributeName = $TemplateStringUtility.firstToLowerCase($attributeDescriptor.name) ) 
		#set( $attributeName = $TemplateStringUtility.replaceAllIn($attributeName, " ", "") ) 

		'
		'  get ${attributeName}C() {
		'    return this.form.get('inputFieldControl.detailsForm.${attributeName}Control');
		'  }
	
	#end
#end

