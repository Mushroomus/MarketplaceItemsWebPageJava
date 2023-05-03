package com.marketplace.MarketplaceItems.model;

import com.marketplace.MarketplaceItems.entity.Message;
import lombok.Builder;

import java.util.List;

@Builder
public class GetMessagesResponse {
    private int totalPages;
    private long totalElements;
    private int currentPage;
    private List<Message> messages;
}