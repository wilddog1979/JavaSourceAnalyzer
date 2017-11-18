package org.eaSTars.dblayer.model;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(FIELD)
public @interface Attribute {

	String column();
	
	boolean primarykey() default false;
	
	boolean nullable() default false;
}
