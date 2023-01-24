package com.syedee.graphqlbackend.controllers.graphql;

import com.syedee.graphqlbackend.database.PostgresQueryService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
public class QuizController {

    private final PostgresQueryService postgresQueryService;

    public QuizController(final PostgresQueryService postgresQueryService, final AuthenticationManager authenticationManager) {
        this.postgresQueryService = postgresQueryService;
    }

    @MutationMapping
    @Secured("USER")
    public QuizMutationResponse createNewQuiz(
            @Argument String title,
            Principal principal
    ) {
        if (Objects.isNull(title) || title.isBlank()) {
            return QuizMutationResponse.of(Arrays.asList("[Error] Failed to create new quiz. Invalid quiz title."));
        }
        var user = Helper.currentUser(principal);

        return postgresQueryService.createNewQuiz(title, user.getId())
                .map(Helper::gqlDtoOf)
                .map(QuizMutationResponse::of)
                .orElse(QuizMutationResponse.of(Arrays.asList("[Error] Failed to create new quiz.")));
    }

    @MutationMapping
    @Secured("USER")
    public QuizMutationResponse updateQuiz(
            @Argument Integer id,
            @Argument UpdateQuizMutationInput input,
            Principal principal
    ) {
        if (Objects.isNull(input.getTitle()) || input.getTitle().isBlank()) {
            return QuizMutationResponse.of(Arrays.asList("[Error] Failed to update quiz. Invalid quiz title."));
        }
        var user = Helper.currentUser(principal);
        return postgresQueryService.updateQuiz(id, input.getTitle(), input.getDraft(), Helper.entityOf(input.getQuestions()), user.getId())
                .map(Helper::gqlDtoOf)
                .map(QuizMutationResponse::of)
                .orElse(QuizMutationResponse.of(Arrays.asList("[Error] Failed to update quiz.")));
    }

    @MutationMapping
    @Secured("USER")
    public Boolean deleteQuiz(
            @Argument Integer id,
            Principal principal
    ) {
        var user = Helper.currentUser(principal);
        return postgresQueryService.deleteQuiz(id, user.getId());
    }

    @QueryMapping
    @Secured({"USER", "VISITOR"})
    public QuizGqlDto fetchQuizExamMode(
            @Argument Integer id,
            Principal principal
    ) {
        var user = Helper.currentUser(principal);
        return postgresQueryService.findQuiz(id)
                .map(quizEntity -> {
                    if (quizEntity.getDraft()) {
                        return null;
                    }
                    return Helper.gqlDtoOf(quizEntity);
                }).orElse(null);
    }

    @QueryMapping
    @Secured("USER")
    public QuizGqlDto fetchQuizManageMode(
            @Argument Integer id,
            Principal principal
    ) {
        var user = Helper.currentUser(principal);
        return postgresQueryService.findQuiz(id)
                .map(quizEntity -> {
                    if (!quizEntity.getAuthorUserId().equals(user.getId())) {
                        return null;
                    }
                    return Helper.gqlDtoOf(quizEntity);
                }).orElse(null);
    }

    @QueryMapping
    @Secured({"USER", "VISITOR"})
    public Connection<QuizGqlDto> fetchAllPublishedQuiz(
            @Argument Integer first,
            @Argument String after
    ) {
        var afterInt = after == null ? null : Integer.valueOf(after);
        var quizGqlDtos = postgresQueryService.fetchAllPublishedQuizzes(afterInt, first)
                .stream()
                .map(Helper::gqlDtoOf)
                .collect(Collectors.toList());

        return Helper.connectionOf(quizGqlDtos, first);
    }

    @QueryMapping
    @Secured({"USER", "VISITOR"})
    public List<Boolean> generateQuizResult(
            @Argument Integer quizId,
            @Argument List<QuestionAnswerGqlInputDto> answers
    ) {
        return postgresQueryService.findQuiz(quizId)
                .map(quizEntity -> Helper.result(answers, quizEntity.getQuestions()))
                .orElse(Arrays.asList());
    }
}
