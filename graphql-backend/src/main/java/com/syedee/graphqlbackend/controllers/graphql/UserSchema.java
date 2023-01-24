package com.syedee.graphqlbackend.controllers.graphql;

import graphql.com.google.common.collect.Lists;

import java.util.List;

class UserGqlDto {
    private final Integer id;
    private final String name;
    private final String role;

    public UserGqlDto(final Integer id, final String name, final String role) {
        this.id = id;
        this.name = name;
        this.role = role;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }
}

class CreateNewUserMutationResponse extends MutationResponse {
    private final UserGqlDto user;

    private CreateNewUserMutationResponse(Boolean successful, List<String> errorMessages, UserGqlDto user) {
        super(successful, errorMessages);
        this.user = user;
    }

    public static CreateNewUserMutationResponse of(List<String> errorMessages) {
        return new CreateNewUserMutationResponse(false, errorMessages, null);
    }

    public static CreateNewUserMutationResponse of(UserGqlDto userGqlDto) {
        return new CreateNewUserMutationResponse(true, Lists.newArrayList(), userGqlDto);
    }

    public UserGqlDto getUser() {
        return user;
    }
}