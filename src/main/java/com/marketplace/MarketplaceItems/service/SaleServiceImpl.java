package com.marketplace.MarketplaceItems.service;

import com.marketplace.MarketplaceItems.dao.SaleDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SaleServiceImpl implements  SaleService {

    private SaleDAO saleDAO;

    @Autowired
    public SaleServiceImpl(SaleDAO theSaleDAO) {
        saleDAO = theSaleDAO;
    }
}
