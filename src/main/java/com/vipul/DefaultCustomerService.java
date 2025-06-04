package com.vipul;

public class DefaultCustomerService implements CustomerService {
    @Override
    public void create() {
        System.out.println("create default customer service executed");
    }

    @Override
    public void add() {
        System.out.println("add default customer service executed");
    }

    @Override
    public void createWithParams(String param1, String param2) {
        System.out.println("create with params executed");
    }
}
