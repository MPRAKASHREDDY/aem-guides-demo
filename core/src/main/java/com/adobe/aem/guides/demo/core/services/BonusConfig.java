package com.adobe.aem.guides.demo.core.services;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Employee Bonus Configuration")
public @interface BonusConfig
{
   @AttributeDefinition(name = "Bonus Percentage")
    double bonusPercentage() default 10;
}
