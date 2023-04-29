package com.marketplace.MarketplaceItems.model;

import com.marketplace.MarketplaceItems.entity.Message;

import java.util.List;

public class MessagesResponse {
        private int totalPages;
        private long totalElements;

        private int currentPage;
        private List<Message> messages;


        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public long getTotalElements() {
            return totalElements;
        }

        public void setTotalElements(long totalElements) {
            this.totalElements = totalElements;
        }

        public List<Message> getMessages() {
            return messages;
        }

        public void setMessages(List<Message> messages) {
            this.messages = messages;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }
    }