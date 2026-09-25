package com.pranav.service;

import com.pranav.model.AppUser;
import org.springframework.stereotype.Service;

import java.util.List;

public interface AdminService {
    List<AppUser> getAllUsers();
}
