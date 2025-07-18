package com.vipul.annotation;

import org.springframework.aot.hint.annotation.Reflective;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE,METHOD})
@Retention(RUNTIME)
@Inherited
@Documented
@Reflective
public @interface MyTransactional {
}
