package com.marketplace.MarketplaceItems.service;

import com.marketplace.MarketplaceItems.entity.User;
import com.marketplace.MarketplaceItems.model.ResponseMessage;
import com.marketplace.MarketplaceItems.model.UpdateUserRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@Service
public interface UserService {

    User getCurrentUser();

    Page<User> findAll(Pageable pageable);

    void saveUser(User user);

    void deleteUserById(Integer id);

    void updateUser(User user);

    User findByUsername(String username);

    ResponseEntity<ResponseMessage> addUser(User user);
    ResponseEntity<PagedModel<User>> getUserList(int page, int size, String search, String role, String startDate, String endDate);
    ResponseEntity<ResponseMessage> deleteUser(int id);
    ResponseEntity<String> updateUser(UpdateUserRequest request);
}
