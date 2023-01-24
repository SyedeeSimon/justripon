package com.syedee.graphqlbackend.database;

import java.util.List;

public class QuizEntity {
    private final Integer id;
    private final String urlPath;
    private final String title;
    private final Boolean draft;
    private final Integer authorUserId;
    private final List<Question> questions;

    public QuizEntity(Integer id, String urlPath, String title, Boolean draft, Integer authorUserId, List<Question> questions) {
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

    public List<Question> getQuestions() {
        return questions;
    }
}
