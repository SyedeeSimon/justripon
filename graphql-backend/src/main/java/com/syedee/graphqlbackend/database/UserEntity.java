package com.syedee.graphqlbackend.database;

public class UserEntity {
    private final int id;
    private final String name;
    private final String password;
    private final String role;

    private UserEntity(int id, String name, String password, String role) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.role = role;
    }

    public static UserEntity of(final Integer id, final String name, final String role) {
        return new UserEntity(id, name, null, role);
    }

    public static UserEntity of(final Integer id, final String name, final String password, final String role) {
        return new UserEntity(id, name, password, role);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }
}
