package com.vipul;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Configuration
public class JDKProxies {

    @Bean
    public ApplicationRunner applicationRunner() {
        return (args) -> {
            var defaultCustomer  = new DefaultCustomerService();

            InvocationHandler handler = new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args)
                        throws Throwable {
                    System.out.println("Invocation Handler");
                    if (method.getAnnotation(MyTransactional.class) != null) {
                       return  method.invoke(defaultCustomer, args);
                    }
                    return null;
                }
            };

            var proxyInstance = (CustomerService) Proxy.newProxyInstance(
                    defaultCustomer.getClass().getClassLoader(),
                    defaultCustomer.getClass().getInterfaces(),
                    handler
            );
//            proxyInstance.create();
            proxyInstance.createWithParams("Vipul", "Jain");
        };
    }
}
