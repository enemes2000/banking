package com.github.enemes2000.model;

import com.google.gson.annotations.SerializedName;

public class Customer {
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @SerializedName("customer_id")
    private String id;

    private String name;
}
