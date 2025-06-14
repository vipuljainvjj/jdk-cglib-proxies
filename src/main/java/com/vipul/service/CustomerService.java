package com.vipul.service;

import com.vipul.annotation.MyTransactional;

public interface CustomerService {

    void create();

    void add();

    @MyTransactional
    void createWithParams(String param1, String param2);

}
