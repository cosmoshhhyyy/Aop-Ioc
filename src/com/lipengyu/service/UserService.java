package com.lipengyu.service;

import com.lipengyu.spring.*;

@Component("")
@Scope("prototype")
public class UserService implements BeanNameAware, InitializingBean, UserInterface {

    @Autowire
    private OrderService orderService;

    private String beanName; // 记一下自己bean的名字

    private String xxx;

    public void xxxxx() {

    }
    public void test() {
        System.out.println(orderService);
    }

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Override
    public void afterPropertiesSet() {
        // ......
        System.out.println("abc");
    }
}
