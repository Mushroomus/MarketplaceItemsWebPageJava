package com.marketplace.MarketplaceItems.service;

import org.springframework.stereotype.Service;

@Service
public interface ItemImageService {
    String findByDefindexReturnUrl(Integer defindex);
}
