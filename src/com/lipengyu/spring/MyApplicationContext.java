package com.lipengyu.spring;

import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class MyApplicationContext {

    private Class configClass;

    private ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    // 单例池
    private ConcurrentHashMap<String, Object> singletonObjects = new ConcurrentHashMap<>();

    private ArrayList<BeanPostProcessor> beanPostProcessors = new ArrayList<>();
    /**
     * 构造方法
     *
     * @param configClass
     */
    public MyApplicationContext(Class configClass) throws InstantiationException, IllegalAccessException {
        this.configClass = configClass;

        // 扫描，路径在configClass注解上
        if (configClass.isAnnotationPresent(ComponentScan.class)) { // 是否有这个注解
            ComponentScan componentScan = (ComponentScan) configClass.getAnnotation(ComponentScan.class); // 拿到注解
            String path = componentScan.value(); // com.lipengyu.service

            path = path.replace(".", "/"); // com/lipengyu/service
            ClassLoader classLoader = MyApplicationContext.class.getClassLoader();
            URL resource = classLoader.getResource(path);  // D:/xxx/spring/com/lipengyu/service

            File file = new File(resource.getFile());

            // System.out.println(file); // E:\studyjavacode\spring\out\production\spring\com\lipengyu\service
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File f : files) {
                    String fileName = f.getAbsolutePath();
                    // System.out.println(fileName);

                    String className = fileName.substring(fileName.indexOf("com"), fileName.indexOf(".class"));
                    className = className.replace("\\", ".");
                    // System.out.println(className); // // com.lipengyu.service.UserService
                    if (fileName.endsWith(".class")) { // class结尾
                        Class<?> aClass = null; // 获得类对象
                        try {
                            aClass = classLoader.loadClass(className);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }

                        if (aClass.isAnnotationPresent(Component.class)) {

                            if (BeanPostProcessor.class.isAssignableFrom(aClass)) { // 类是否是某个类的派生
                                BeanPostProcessor object = (BeanPostProcessor) aClass.newInstance();
                                beanPostProcessors.add(object); // 创建对象 然后加入到 队列中
                            }
                            // bean
                            Component component = aClass.getAnnotation(Component.class);
                            String beanName = component.value(); // 获取类名

                            if (beanName.equals("")) {
                                beanName = Introspector.decapitalize(aClass.getSimpleName()); // 没传入名字，构建一个名字
                            }

                            BeanDefinition beanDefinition = new BeanDefinition();
                            beanDefinition.setType(aClass); // 设置类型
                            if (aClass.isAnnotationPresent(Scope.class)) {
                                Scope annotation = aClass.getAnnotation(Scope.class); // 获取作用域
                                beanDefinition.setScope(annotation.value());
                            } else {
                                beanDefinition.setScope("singleton"); // 没有注解，默认单例
                            }

                            beanDefinitionMap.put(beanName, beanDefinition); // 放入

                        }
                    }
                }
            }
        }

        // 创建单例bean
        for (String beanName : beanDefinitionMap.keySet()) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);

            if (beanDefinition.getScope().equals("singleton")) {
                Object bean = createBean(beanName, beanDefinition);
                singletonObjects.put(beanName, bean); // 放入单例池
            }
        }
    }

    /**
     * 返回bean对象
     *
     * @param beanName
     * @return
     */
    public Object getBean(String beanName) {

        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName); // 去取这个对象

        if (beanDefinition == null) {
            throw new NullPointerException();
        } else {
            String scope = beanDefinition.getScope();
            if (scope.equals("singleton")) { // 单例
                Object bean = singletonObjects.get(beanName); // 单例池中获取单例对象
                if (bean == null) {
                    bean = createBean(beanName, beanDefinition);// 若单例池中没有，创建
                    // 放入单例池
                    singletonObjects.put(beanName, bean);
                }
                return bean;
            } else { // 多例
                // 直接创建
                return createBean(beanName, beanDefinition);
            }
        }
    }

    /**
     * 创建对象
     *
     * @param beanName
     * @param beanDefinition
     * @return
     */
    private Object createBean(String beanName, BeanDefinition beanDefinition) { // 模拟bean的声明周期

        Class clazz = beanDefinition.getType();

        try {
            Object instance = clazz.getConstructor().newInstance();

            // 依赖注入的实现
            for (Field f : clazz.getDeclaredFields()) { // declared可以获取所有私
                if (f.isAnnotationPresent(Autowire.class)) { // 属性有autowire注解
                    f.setAccessible(true); // 同意反射访问 私有
                    f.set(instance, getBean(f.getName()));  // 属性名字当bean名字取找，为其注入
                }
            }

            // 如果实现了接口，把名字传入，aware回调
            if (instance instanceof BeanNameAware) {
                ((BeanNameAware) instance).setBeanName(beanName);
            }

            // 初始化前调用
            for (BeanPostProcessor beanPostProcessor: beanPostProcessors) {
                instance = beanPostProcessor.postProcessBeforeInitialization(beanName, instance);
            }

            // 回调，是spring告诉你的属性值，初始化是调用程序员自己写的
            // 初始化，调用程序员自己写的初始化方法内容，不管它干什么，调用就完事了
            if (instance instanceof InitializingBean) {
                ((InitializingBean) instance).afterPropertiesSet();
            }

            // 初始化后调用
            for (BeanPostProcessor beanPostProcessor: beanPostProcessors) {
                instance = beanPostProcessor.postProcessAfterInitialization(beanName, instance);
            }

            return instance;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return null;
    }
}