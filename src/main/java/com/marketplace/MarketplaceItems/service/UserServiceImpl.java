package com.marketplace.MarketplaceItems.service;

import com.marketplace.MarketplaceItems.dao.UserDAO;
import com.marketplace.MarketplaceItems.entity.Item;
import com.marketplace.MarketplaceItems.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService{

    private UserDAO userDAO;

    @Autowired
    public UserServiceImpl(UserDAO theUserDAO) {
        userDAO = theUserDAO;
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return userDAO.findAll(pageable);
    };

    @Override
    public void saveUser(User user) { userDAO.save(user); }

    @Override
    public void deleteUserById(Integer id) { userDAO.deleteById(id); }

    @Override
    public void updateUser(User user) { userDAO.save(user); }

    @Override
    public User findByUsername(String username) { return userDAO.findByUsername(username); }

    @Override
    public Page<User> findAll(Pageable pageable, String search, String role, LocalDateTime startDate, LocalDateTime endDate) {
        Specification<User> spec = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(search != null && !search.isEmpty()) {
                String searchPattern = "%" + search + "%";
                predicates.add(builder.like(root.get("username"), searchPattern  ));
            }
            if (role != null && !role.isEmpty()) {
                predicates.add(builder.equal(root.get("role"), role));
            }
            if (startDate != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("date"), startDate));
            }
            if (endDate != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("date"), endDate));
            }
            return predicates.isEmpty() ? builder.conjunction() : builder.and(predicates.toArray(new javax.persistence.criteria.Predicate[predicates.size()]));
        };

        return userDAO.findAll(spec, pageable);
    }

}
