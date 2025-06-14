package com.vipul.proxies;

import com.vipul.annotation.MyTransactional;
import com.vipul.service.CustomerService;
import com.vipul.service.DefaultCustomerService;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

//@Configuration
public class SpringProxies {

     static boolean transactional(Object obj) {
         var hasTransactional = new AtomicBoolean(false);
         List<Class<?>> allClasses = new ArrayList<>();
         allClasses.add(obj.getClass());
         Collections.addAll(allClasses, obj.getClass().getInterfaces());
//         allClasses.addAll(List.of(obj.getClass().getInterfaces()));

         allClasses.forEach(clzz -> {
             ReflectionUtils.doWithMethods(clzz, method -> {
                 if (method.getAnnotation(MyTransactional.class) != null) {
                     hasTransactional.set(true);
                 }
             });
         });
         return hasTransactional.get();
    }

    // callback Interface
    static class MyTransactionalBeanPostProcessor implements BeanPostProcessor {

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            if (bean instanceof CustomerService && transactional(bean)) {
                ProxyFactory pf = new ProxyFactory();
                Arrays.stream(bean.getClass().getInterfaces()).map(Class::getName).forEach(System.out::println);
                pf.setInterfaces(bean.getClass().getInterfaces());
                pf.setTarget(bean);
                pf.addAdvice(new MethodInterceptor() {

                    @Override
                    public Object invoke(MethodInvocation invocation) throws Throwable {
                        Method method = invocation.getMethod();
                        Object[] args = invocation.getArguments();
                        System.out.println("Invocation Handler");
                        if (method.getAnnotation(MyTransactional.class) != null) {
                            return method.invoke(bean, args);
                        }
                        return null;
                    }
                });
                System.out.println("=====================================================");
                var proxy = pf.getProxy(bean.getClass().getClassLoader());
                Arrays.stream(proxy.getClass().getInterfaces()).map(Class::getName).forEach(System.out::println);

                return proxy;
            }
            return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
        }
    }

    @Bean
    MyTransactionalBeanPostProcessor transactionalBeanPostProcessor() {
         return new MyTransactionalBeanPostProcessor();
    }

    @Bean
    DefaultCustomerService defaultCustomerService() {
         return new DefaultCustomerService();
    }

    @Bean
    public ApplicationRunner applicationRunner(CustomerService customerService) {
        return (args) -> {
            customerService.create();
            customerService.add();
        };
    }

}
