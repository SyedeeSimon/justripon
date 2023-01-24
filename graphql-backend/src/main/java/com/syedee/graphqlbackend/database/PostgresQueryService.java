package com.syedee.graphqlbackend.database;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.com.google.common.collect.Maps;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.*;

@Service
public class PostgresQueryService {
    private static final ZoneId UTC = ZoneId.of("UTC");
    private static final int URL_PATH_LENGTH = 6;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public PostgresQueryService(final NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public Optional<UserEntity> findUserByName(String userName) {
        var sql = "SELECT * FROM users WHERE name = :user_name";
        Map params = Maps.newHashMap();
        params.put("user_name", userName);
        try {
            return namedParameterJdbcTemplate.<Optional<UserEntity>>queryForObject(sql, params, (rs, rowNum) ->
                    Optional.of(UserEntity.of(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("password"),
                            rs.getString("role")
                    ))
            );
        } catch (DataAccessException exception) {
            return Optional.empty();
        }
    }

    public Optional<UserEntity> createNewUser(String userName, String password) {
        try {
            var sql = "INSERT INTO postgres.users (name, password) \n" +
                    "VALUES (:user_name, :password) \n" +
                    "RETURNING *";

            Map params = Maps.newHashMap();
            params.put("user_name", userName);
            params.put("password", password);
            return namedParameterJdbcTemplate.<Optional<UserEntity>>queryForObject(sql, params,
                    (rs, rowNum) -> Optional.of(UserEntity.of(rs.getInt("id"), rs.getString("name"), rs.getString("role")))
            );
        } catch (DuplicateKeyException exception) {
            return Optional.empty();
        }
    }

    public Optional<QuizEntity> findQuiz(Integer id) {
        var sql = "SELECT * FROM quizzes WHERE id=:id";
        Map params = Maps.newHashMap();
        params.put("id", id);
        try {
            return namedParameterJdbcTemplate.<Optional<QuizEntity>>queryForObject(sql, params,
                    (rs, rowNum) -> {
                        try {
                            return Optional.of(new QuizEntity(rs.getInt("id"), rs.getString("url_path"), rs.getString("title"), rs.getBoolean("draft"), rs.getInt("author_user_id"), deserializeQuestions(rs.getString("questions"))));
                        } catch (JsonProcessingException e) {
                            return Optional.empty();
                        }
                    }
            );
        } catch (DataAccessException exception) {
            return Optional.empty();
        }
    }

    public List<QuizEntity> fetchAllPublishedQuizzes(Integer idGreaterThan, Integer limit) {
        var sql = "SELECT * FROM quizzes WHERE \n" +
                "draft = FALSE \n" +
                (idGreaterThan != null ? "AND id < :idGreaterThan" : "") + "\n" +
                "ORDER BY id DESC \n" +
                "LIMIT :limit";

        Map params = Maps.newHashMap();
        params.put("idGreaterThan", idGreaterThan);
        params.put("limit", limit);

        return namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) -> {
            try {
                return quizEntityOf(rs);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public Optional<QuizEntity> createNewQuiz(String title, Integer authorUserId) {
        var urlPath = RandomStringUtils.random(URL_PATH_LENGTH, true, true);
        return upsertQuiz(new QuizEntity(null, urlPath, title, true, authorUserId, null));
    }

    /**
     * Prepare and execute update of quiz with title and question supplied
     */
    public Optional<QuizEntity> updateQuiz(
            Integer id,
            String title,
            Boolean draft,
            List<Question> questions, Integer authorUserId
    ) {
        return findQuiz(id).flatMap(
                quizEntity -> {
                    if (!quizEntity.getAuthorUserId().equals(authorUserId) || !quizEntity.getDraft()) {
                        return Optional.empty();
                    }
                    var newQuizEntity = new QuizEntity(id, quizEntity.getUrlPath(), title, draft, quizEntity.getAuthorUserId(), questions);
                    return upsertQuiz(newQuizEntity);
                }
        );
    }

    private Optional<QuizEntity> upsertQuiz(final QuizEntity quizEntity) {
        try {
            var sql = "INSERT INTO quizzes(url_path, title, draft, author_user_id, questions)\n" +
                    "values(:url_path, :title, :draft, :author_user_id, :questions)\n" +
                    "ON CONFLICT (url_path) DO UPDATE\n" +
                    "SET title = EXCLUDED.title, draft=EXCLUDED.draft, questions=EXCLUDED.questions \n" +
                    "RETURNING *";

            Map params = Maps.newHashMap();
            params.put("url_path", quizEntity.getUrlPath());
            params.put("title", quizEntity.getTitle());
            params.put("draft", quizEntity.getDraft());
            params.put("author_user_id", quizEntity.getAuthorUserId());
            params.put("questions", serializeQuestions(quizEntity.getQuestions()));

            return namedParameterJdbcTemplate.<Optional<QuizEntity>>queryForObject(sql, params,
                    (rs, rowNum) -> {
                        try {
                            return Optional.of(quizEntityOf(rs));
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    }
            );
        } catch (DataAccessException exception) {
            return Optional.empty();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public Boolean deleteQuiz(final Integer id, final Integer authorUserId) {
        try {
            var sql = "DELETE FROM quizzes WHERE id=:id AND author_user_id=:author_user_id";

            Map params = Maps.newHashMap();
            params.put("id", id);
            params.put("author_user_id", authorUserId);

            if (namedParameterJdbcTemplate.update(sql, params) == 1) {
                return true;
            }
            return false;

        } catch (DataAccessException exception) {
            return false;
        }
    }

    private List<Question> deserializeQuestions(String json) throws JsonProcessingException {
        if (json == null) {
            return null;
        }
        return objectMapper.readValue(json, new TypeReference<List<Question>>() {
        });
    }

    private String serializeQuestions(List<Question> questions) throws JsonProcessingException {
        if (questions == null) {
            return null;
        }
        return objectMapper.writeValueAsString(questions);
    }

    private QuizEntity quizEntityOf(ResultSet rs) throws SQLException, JsonProcessingException {
        return new QuizEntity(rs.getInt("id"), rs.getString("url_path"), rs.getString("title"), rs.getBoolean("draft"), rs.getInt("author_user_id"), deserializeQuestions(rs.getString("questions")));
    }
}
