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
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

//@Configuration
public class SpringProxies {

    @Bean
    public ApplicationRunner applicationRunner(CustomerService customerService) {
        return (args) -> {
            customerService.create();
            customerService.createWithParams("Vipul", "Jain");
        };
    }

    @Bean
    DefaultCustomerService defaultCustomerService() {
        return new DefaultCustomerService();
    }

    @Bean
    MyTransactionalBeanPostProcessor transactionalBeanPostProcessor() {
        return new MyTransactionalBeanPostProcessor();
    }

    class MyTransactionalBeanPostProcessor implements BeanPostProcessor {

        @Override
        @NonNull
        public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
            if (bean instanceof CustomerService && isClassTransactional(bean)) {
                ProxyFactory pf = new ProxyFactory();
                pf.setInterfaces(bean.getClass().getInterfaces());
                pf.setTarget(bean);
                pf.addAdvice(getMethodInterceptor(bean));

                return pf.getProxy(bean.getClass().getClassLoader());
            }
            return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
        }

        boolean isClassTransactional(Object obj) {
            var hasTransactional = new AtomicBoolean(false);
            List<Class<?>> allClasses = new ArrayList<>();
            allClasses.add(obj.getClass());
            Collections.addAll(allClasses, obj.getClass().getInterfaces());

            allClasses.forEach(clzz -> {
                ReflectionUtils.doWithMethods(clzz, method -> {
                    if (method.getAnnotation(MyTransactional.class) != null) {
                        hasTransactional.set(true);
                    }
                });
            });
            return hasTransactional.get();
        }

        public MethodInterceptor getMethodInterceptor(Object bean) {
            return (MethodInvocation invocation) -> {
                Method method = invocation.getMethod();
                Object[] args = invocation.getArguments();
                System.out.println("Invocation Handler is invoked on " + method.getName());
                if (Objects.nonNull(method.getAnnotation(MyTransactional.class))) {
                    return method.invoke(bean, args);
                }
                return null;
            };
        }
    }

}
