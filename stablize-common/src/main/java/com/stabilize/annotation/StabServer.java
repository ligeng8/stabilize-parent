package com.stabilize.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StabServer {
	String value()  default "";
	String module() default "";
	String group() default "";
	String export() default "";
	String intefaceClass() default "";
}
