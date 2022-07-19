package com.example.where_to_go.models;

public class Friend {
    private final String id;
    private final String name;

    public Friend(String _id, String _name) {
        id = _id;
        name = _name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
