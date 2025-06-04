package com.vipul;

public interface CustomerService {

    @MyTransactional
    void create();

    void add();

    void createWithParams(String param1, String param2);
}
