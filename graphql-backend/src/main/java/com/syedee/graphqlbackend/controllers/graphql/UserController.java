package com.syedee.graphqlbackend.controllers.graphql;

import com.syedee.graphqlbackend.database.*;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Arrays;
import java.util.Objects;

import static com.syedee.graphqlbackend.controllers.graphql.Helper.*;


@Controller
public class UserController {
    private final PostgresQueryService postgresQueryService;

    public UserController(final PostgresQueryService postgresQueryService, final AuthenticationManager authenticationManager) {
        this.postgresQueryService = postgresQueryService;
    }

    @MutationMapping
    @Secured("VISITOR")
    public CreateNewUserMutationResponse createNewUser(
            @Argument String name,
            @Argument String password,
            Principal principal
    ) {
        if (Objects.isNull(name) || name.isBlank() || Objects.isNull(password) || password.isBlank()) {
            return CreateNewUserMutationResponse.of(Arrays.asList("[Error] Failed to create new user. Invalid user name or password."));
        }

        return postgresQueryService.createNewUser(name, password)
                .map(Helper::gqlDtoOf)
                .map(CreateNewUserMutationResponse::of)
                .orElse(CreateNewUserMutationResponse.of(Arrays.asList("[Error] Failed to create new user. Database error.")));
    }
}
