package com.syedee.graphqlbackend.controllers.graphql;

import com.syedee.graphqlbackend.config.CurrentUser;
import com.syedee.graphqlbackend.database.Question;
import com.syedee.graphqlbackend.database.QuizEntity;
import com.syedee.graphqlbackend.database.UserEntity;
import com.syedee.graphqlbackend.helper.Crypto;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Helper {
    public static String getIdGreaterThanString(String after) {
        if (after == null) {
            return null;
        }
        return Crypto.decodeBase64(after);
    }

    public static Integer getIdGreaterThanInteger(String after) {
        if (after == null) {
            return null;
        }
        return Integer.valueOf(Crypto.decodeBase64(after));
    }

    public static PageInfo getPageInfo(List<Edge<QuizGqlDto>> edges, int limit) {
        if (edges.size() == 0) {
            return PageInfo.of(false);
        }
        var firstEdge = edges.get(0);
        var lastEdge = edges.get(edges.size() - 1);
        return PageInfo.of(edges.size() == limit, firstEdge.cursor, lastEdge.cursor);
    }

    public static UserGqlDto gqlDtoOf(UserEntity userEntity) {
        return new UserGqlDto(userEntity.getId(), userEntity.getName(), userEntity.getRole());
    }

    public static QuizGqlDto gqlDtoOf(QuizEntity quizEntity) {
        return new QuizGqlDto(quizEntity.getId(), quizEntity.getUrlPath(), quizEntity.getTitle(), quizEntity.getDraft(), quizEntity.getAuthorUserId(), gqlDtoOf(quizEntity.getQuestions()));
    }

    public static List<QuestionsGqlResponseDto> gqlDtoOf(List<Question> question) {
        if (question == null) {
            return null;
        }
        return question.stream().map(Helper::gqlDtoOf).collect(Collectors.toList());
    }

    public static QuestionsGqlResponseDto gqlDtoOf(Question question) {
        return new QuestionsGqlResponseDto(question.getStatement(), question.getSingleSelect(), question.getOptions(), question.getCorrectAnswers());
    }

    public static List<Question> entityOf(List<QuestionsGqlInputDto> gqlInputDtos) {
        return gqlInputDtos.stream()
                .map(Helper::entityOf)
                .collect(Collectors.toList());
    }

    public static Question entityOf(QuestionsGqlInputDto inputDto) {
        return new Question(inputDto.getStatement(), inputDto.getSingleSelect(), inputDto.getOptions(), inputDto.getCorrectAnswers());
    }

    public static CurrentUser currentUser(Principal principal) {
        return (CurrentUser) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
    }

    public static QuizConnection connectionOf(List<QuizGqlDto> quizGqlDtos, int limit) {
        var edges = quizGqlDtos.stream().map(
                quizGqlDto -> new Edge<QuizGqlDto>(quizGqlDto.getId().toString(), quizGqlDto)
        ).collect(Collectors.toList());

        return new QuizConnection(edges, getPageInfo(edges, limit));
    }

    public static List<Boolean> result(List<QuestionAnswerGqlInputDto> answers, List<Question> questions) {
        if (answers.size() != questions.size()) {
            return Arrays.asList();
        }
        List<Boolean> result = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {
            var question = questions.get(i);
            var answer = answers.get(i);
            result.add(
                    answer.getAnswers().containsAll(question.getCorrectAnswers()) &&
                            question.getCorrectAnswers().containsAll(answer.getAnswers())
            );
        }
        return result;
    }
}
