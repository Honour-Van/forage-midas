package com.jpmc.midascore.service;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public float getBalance(long id) {
        return repository.findById(id)
                .map(UserRecord::getBalance)
                .orElseThrow(() -> new RuntimeException("User with id " + id + " not found"));
    }
    public float getBalanceByName(String name) {
        return repository.findByName(name)
                .map(UserRecord::getBalance)
                .orElseThrow(() -> new RuntimeException("User with name " + name + " not found"));
    }

    @Transactional
    public void updateBalance(long id, float newBalance) {
        repository.findById(id).ifPresentOrElse(
                user -> {
                    user.setBalance(newBalance);
                    repository.save(user);
                },
                () -> { throw new RuntimeException("User with id " + id + " not found"); }
        );
    }

    public String getUserName(long id) {
        return repository.findById(id)
                .map(UserRecord::getName)
                .orElseThrow(() -> new RuntimeException("User with id " + id + " not found"));
    }

    public void printAllUsers() {
        repository.findAll().forEach(System.out::println);
    }


}
