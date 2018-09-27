package com.yeoh.seeker.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The method annotated with {@link Hide} will be packed out to jar/aar after method's modifier is changed .
 *
 * @author JingYeoh
 * @since 2018-08-09
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface Hide {

    /**
     * Allow or not processed .
     *
     * @return default value is {@link Modifier#PRIVATE} .
     *
     * @see Modifier#PRIVATE
     * @see Modifier#DEFAULT
     * @see Modifier#PUBLIC
     * @see Modifier#PROTECTED
     */
    Modifier value() default Modifier.PRIVATE;
}
