package com.example.lab1.dto;

import java.util.List;

public class ImportRequest {
    private String userName;
    private List<BookCreatureDto> creatures;
    private String fileContent;  // Base64 encoded file content
    private String fileName;      // Original file name

    public ImportRequest() {
    }

    public ImportRequest(String userName, List<BookCreatureDto> creatures) {
        this.userName = userName;
        this.creatures = creatures;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<BookCreatureDto> getCreatures() {
        return creatures;
    }

    public void setCreatures(List<BookCreatureDto> creatures) {
        this.creatures = creatures;
    }

    public String getFileContent() {
        return fileContent;
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}

