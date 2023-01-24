package com.syedee.graphqlbackend.controllers.graphql;

import java.util.Arrays;
import java.util.List;

abstract class QuestionGqlDto {
    private final String statement;
    private final Boolean singleSelect;
    private final List<String> options;
    private final List<Integer> correctAnswers;

    protected QuestionGqlDto(String statement, Boolean singleSelect, List<String> options, List<Integer> correctAnswers) {
        this.statement = statement;
        this.singleSelect = singleSelect;
        this.options = options;
        this.correctAnswers = correctAnswers;
    }

    public String getStatement() {
        return statement;
    }

    public Boolean getSingleSelect() {
        return singleSelect;
    }

    public List<String> getOptions() {
        return options;
    }

    public List<Integer> getCorrectAnswers() {
        return correctAnswers;
    }
}

class QuestionsGqlInputDto extends QuestionGqlDto {
    protected QuestionsGqlInputDto(final String statement, final Boolean singleSelect, final List<String> options, final List<Integer> correctAnswers) {
        super(statement, singleSelect, options, correctAnswers);
    }
}

class QuestionsGqlResponseDto extends QuestionGqlDto {
    protected QuestionsGqlResponseDto(String statement, Boolean isSingleSelect, List<String> options, List<Integer> correctAnswers) {
        super(statement, isSingleSelect, options, correctAnswers);
    }
}

class QuizGqlDto {
    private final Integer id;
    private final String urlPath;
    private final String title;
    private final Boolean draft;
    private final Integer authorUserId;
    private final List<QuestionsGqlResponseDto> questions;

    public QuizGqlDto(Integer id, String urlPath, String title, Boolean draft, Integer authorUserId, List<QuestionsGqlResponseDto> questions) {
        this.id = id;
        this.urlPath = urlPath;
        this.title = title;
        this.draft = draft;
        this.authorUserId = authorUserId;
        this.questions = questions;
    }

    public Integer getId() {
        return id;
    }

    public String getUrlPath() {
        return urlPath;
    }

    public String getTitle() {
        return title;
    }

    public Boolean getDraft() {
        return draft;
    }

    public Integer getAuthorUserId() {
        return authorUserId;
    }

    public List<QuestionsGqlResponseDto> getQuestions() {
        return questions;
    }
}

/**
 * This Model is meant to be used for both create and update mutations' response
 */
class QuizMutationResponse extends MutationResponse {
    private final QuizGqlDto quiz;

    private QuizMutationResponse(Boolean successful, List<String> errorMessages, QuizGqlDto quiz) {
        super(successful, errorMessages);
        this.quiz = quiz;
    }

    public static QuizMutationResponse of(List<String> errorMessages) {
        return new QuizMutationResponse(false, errorMessages, null);
    }

    public static QuizMutationResponse of(QuizGqlDto quizGqlDto) {
        return new QuizMutationResponse(true, Arrays.asList(), quizGqlDto);
    }

    public QuizGqlDto getQuiz() {
        return quiz;
    }
}

class UpdateQuizMutationInput {
    private final String title;

    private final Boolean draft;
    private final List<QuestionsGqlInputDto> questions;

    public UpdateQuizMutationInput(String title, Boolean draft, List<QuestionsGqlInputDto> questions) {
        this.title = title;
        this.draft = draft;
        this.questions = questions;
    }

    public String getTitle() {
        return title;
    }

    public Boolean getDraft() {
        return draft;
    }

    public List<QuestionsGqlInputDto> getQuestions() {
        return questions;
    }
}

class QuizConnection extends Connection<QuizGqlDto> {
    public QuizConnection(List<Edge<QuizGqlDto>> edges, PageInfo pageInfo) {
        super(edges, pageInfo);
    }
}

class QuestionAnswerGqlInputDto {
    private final Integer questionIndex;
    private final List<Integer> answers;

    QuestionAnswerGqlInputDto(Integer questionIndex, List<Integer> answers) {
        this.questionIndex = questionIndex;
        this.answers = answers;
    }

    public Integer getQuestionIndex() {
        return questionIndex;
    }

    public List<Integer> getAnswers() {
        return answers;
    }
}