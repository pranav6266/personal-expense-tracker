package com.pranav.service;

import com.pranav.model.AppUser;
import com.pranav.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;

    public AdminServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public List<AppUser> getAllUsers() {
        return userRepository.findAll();
    }
}
