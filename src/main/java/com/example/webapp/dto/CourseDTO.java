package com.example.webapp.dto;

public class CourseDTO {

    private Long id;
    private String code;
    private String name;
    private String description;
    private Integer credits;

    public CourseDTO() {}

    public CourseDTO(String code, String name, String description, Integer credits) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.credits = credits;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCredits() {
        return credits;
    }

    public void setCredits(Integer credits) {
        this.credits = credits;
    }
}
