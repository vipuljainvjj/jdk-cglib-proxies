package com.vipul.proxies;

import com.vipul.annotation.MyTransactional;
import com.vipul.service.CustomerService;
import com.vipul.service.DefaultCustomerService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;

/*
 * It is designed to demonstrate the use of Java's dynamic proxy mechanism.
 * It uses the Proxy class to create a proxy instance of the CustomerService interface.
 * The proxy is configured with an InvocationHandler that intercepts method calls on the proxy instance.

 * Functionality of the Class
 * Dynamic Proxy Creation: The class creates a proxy instance for the CustomerService interface using Java's reflection API.
 * This allows for method calls on the proxy to be intercepted and handled by the InvocationHandler.
 * Invocation Handling: The InvocationHandler checks if the method being invoked has the @MyTransactional annotation.
 * If it does, it proceeds to invoke the actual method on the CustomerService implementation.

 * Explanation of method.invoke(getCustomerService(), args);
 * In the InvocationHandler, the line method.invoke(getCustomerService(), args); is crucial. Here's what it does:
 * method.invoke(...): This uses reflection to call the specified method on the target object.
 * In this context, it calls the method on the actual CustomerService implementation.
 * args: These are the arguments passed to the method being invoked.
 * They are forwarded to the actual method call on the CustomerService implementation.

 * The proxy instance is used in the applicationRunner method, where it calls create() and createWithParams(...) on the proxy.
 * The InvocationHandler intercepts these calls, checks for the @MyTransactional annotation,
 * and then invokes the actual method on the DefaultCustomerService if the annotation is present.
 */

@Configuration
public class JDKProxies {

    @Bean
    public ApplicationRunner applicationRunner() {
        return (args) -> {
            var proxyInstance = (CustomerService) Proxy.newProxyInstance(
                    getCustomerService().getClass().getClassLoader(),
                    getCustomerService().getClass().getInterfaces(),
                    getInvocationHandler()
            );
            proxyInstance.create();
            proxyInstance.createWithParams("Vipul", "Jain");
        };
    }

    @Bean
    public CustomerService getCustomerService() {
        return new DefaultCustomerService();
    }

    @Bean
    public InvocationHandler getInvocationHandler() {
        return (Object proxy, Method method, Object[] args) -> {
            System.out.println("Handler Invoked for method: " + method.getName());
            if (Objects.nonNull(method.getAnnotation(MyTransactional.class))) {
                return  method.invoke(getCustomerService(), args);
            }
            return null;
        };
    }

}