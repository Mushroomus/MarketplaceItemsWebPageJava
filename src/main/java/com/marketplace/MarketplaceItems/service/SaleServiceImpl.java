package com.marketplace.MarketplaceItems.service;

import com.marketplace.MarketplaceItems.dao.SaleDAO;
import com.marketplace.MarketplaceItems.entity.Sale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SaleServiceImpl implements  SaleService {

    private SaleDAO saleDAO;

    @Autowired
    public SaleServiceImpl(SaleDAO theSaleDAO) {
        saleDAO = theSaleDAO;
    }

    @Override
    public Page<Sale> findAll(Pageable pageable) {
        return saleDAO.findAll(pageable);
    }

    @Override
    public void saveAll(List<Sale> sales) {
        saleDAO.saveAll(sales);
    }
}
