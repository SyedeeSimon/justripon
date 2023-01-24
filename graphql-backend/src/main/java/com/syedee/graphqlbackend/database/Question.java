package com.syedee.graphqlbackend.database;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Question {
    private final String statement;
    private final Boolean singleSelect;
    private final List<String> options;
    private final List<Integer> correctAnswers;

    @JsonCreator
    public Question(
            @JsonProperty("statement") String statement,
            @JsonProperty("singleSelect") Boolean singleSelect,
            @JsonProperty("options") List<String> options,
            @JsonProperty("correctAnswers") List<Integer> correctAnswers
    ) {
        this.statement = statement;
        this.singleSelect = singleSelect;
        this.options = options;
        this.correctAnswers = correctAnswers;
    }

    public String getStatement() {
        return statement;
    }

    public List<String> getOptions() {
        return options;
    }

    public Boolean getSingleSelect() {
        return singleSelect;
    }

    public List<Integer> getCorrectAnswers() {
        return correctAnswers;
    }
}
