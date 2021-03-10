package com.github.enemes2000.controller;

import com.github.enemes2000.model.CustomerCountTransaction;
import com.github.enemes2000.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @RequestMapping("/customer")
    public List<CustomerCountTransaction> get(@RequestParam("customerid") String customerid){

        Collection<CustomerCountTransaction> coll = transactionService.getKey(customerid);

      return coll.stream().collect(Collectors.toList());
    }
}
