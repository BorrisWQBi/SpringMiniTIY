package com.borris.annotation;

public @interface RequestMapping {
    String url() default "";
}
