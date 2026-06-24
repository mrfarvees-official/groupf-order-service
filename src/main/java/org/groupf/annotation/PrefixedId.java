package org.groupf.annotation;

import org.hibernate.annotations.IdGeneratorType;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@IdGeneratorType(PrefixedIdGenerator.class)
@Target({FIELD, METHOD})
@Retention(RUNTIME)
public @interface PrefixedId {

    String prefix();

    String sequenceName();

    int numberLength() default 4;
}
