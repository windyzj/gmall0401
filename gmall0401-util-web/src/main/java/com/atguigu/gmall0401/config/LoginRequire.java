package com.atguigu.gmall0401.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)    // source  : override    class    runtime
public @interface LoginRequire {

    boolean autoRedirect() default true;
}

