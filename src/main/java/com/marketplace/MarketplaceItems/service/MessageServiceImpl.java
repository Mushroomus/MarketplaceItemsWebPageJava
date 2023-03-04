package com.marketplace.MarketplaceItems.service;

import com.marketplace.MarketplaceItems.dao.MessageDAO;
import com.marketplace.MarketplaceItems.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MessageServiceImpl implements MessageService{

    private MessageDAO messageDAO;

    @Autowired
    public  MessageServiceImpl(MessageDAO theMessageDAO) {
        messageDAO = theMessageDAO;
    }

    @Override
    public void saveMessage(Message theMessage) {
        messageDAO.save(theMessage);
    }

    @Override
    public void deleteAllByItemSku(String itemSku) {
        messageDAO.deleteAllByItemSku(itemSku);
    }

    @Override
    public void deleteAllByUserId(int id_user) {
        messageDAO.deleteAllByUserId(id_user);
    }

    @Override
    public void deleteById(Long id) {
        messageDAO.deleteById(id);
    }

    @Override
    public Optional<Message> findById(Long id) {
        return messageDAO.findById(id);
    }

    @Override
    public Page<Message> findAll(Pageable pageable, String search, List<String> types, LocalDateTime startDate, LocalDateTime endDate) {
        Specification<Message> spec = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(search != null && !search.isEmpty()) {
                String searchPattern = "%" + search + "%";
                predicates.add(builder.or(
                        builder.like(root.get("item").get("name"), searchPattern),
                        builder.like(root.get("user").get("username"), searchPattern)
                ));
                System.out.println(search);
            }
            if (types != null && !types.isEmpty()) {
                predicates.add(builder.in(root.get("messageType")).value(types));
                System.out.println(types);
            }
            if (startDate != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("date"), startDate));
                System.out.println(startDate);
            }
            if (endDate != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("date"), endDate));
                System.out.println(endDate);
            }
            return predicates.isEmpty() ? builder.conjunction() : builder.and(predicates.toArray(new javax.persistence.criteria.Predicate[predicates.size()]));
        };

        return messageDAO.findAll(spec, pageable);
    }
}
