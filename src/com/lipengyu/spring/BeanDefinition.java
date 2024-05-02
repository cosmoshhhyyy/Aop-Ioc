package com.lipengyu.spring;

public class BeanDefinition {

    // 类型
    private Class type;
    // 作用域
    private String scope;

    public Class getType() {
        return type;
    }

    public void setType(Class type) {
        this.type = type;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
