package com.lipengyu.service;

import com.lipengyu.spring.MyApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Test {
    public static void main(String[] args) throws InstantiationException, IllegalAccessException {
        MyApplicationContext myApplicationContext = new MyApplicationContext(AppConfig.class);

       /* System.out.println(myApplicationContext.getBean("userService"));
        System.out.println(myApplicationContext.getBean("userService"));
        System.out.println(myApplicationContext.getBean("userService"));
        System.out.println(myApplicationContext.getBean("userService"));*/

/*        UserService userService = (UserService) myApplicationContext.getBean("userService");
        userService.test();*/

        UserInterface userInterface = ( UserInterface) myApplicationContext.getBean("userService");
        userInterface.test();
    }
}
