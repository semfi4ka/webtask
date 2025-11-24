package com.filippovich.webtask.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Rating implements Serializable {
    private long id;
    private RatingSubjectType subjectType;
    private long subjectId;
    private User user;
    private int score;
    private String comment;
    private LocalDateTime createdAt;

    public Rating() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public RatingSubjectType getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(RatingSubjectType subjectType) {
        this.subjectType = subjectType;
    }

    public long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(long subjectId) {
        this.subjectId = subjectId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
