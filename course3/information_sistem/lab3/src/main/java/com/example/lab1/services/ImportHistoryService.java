package com.example.lab1.services;

import com.example.lab1.entities.ImportHistory;
import com.example.lab1.repositories.ImportHistoryRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class ImportHistoryService {

    @Inject
    private ImportHistoryRepository repository;

    public ImportHistory create(ImportHistory history) {
        return repository.create(history);
    }

    public List<ImportHistory> findAll() {
        return repository.findAll();
    }

    public ImportHistory findById(Long id) {
        return repository.findById(id);
    }
}

