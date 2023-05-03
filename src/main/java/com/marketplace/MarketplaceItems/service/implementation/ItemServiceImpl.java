package com.marketplace.MarketplaceItems.service.implementation;

import com.marketplace.MarketplaceItems.dao.ItemDAO;
import com.marketplace.MarketplaceItems.entity.Item;
import com.marketplace.MarketplaceItems.exception.InternalServerErrorException;
import com.marketplace.MarketplaceItems.exception.RecordNotFoundException;
import com.marketplace.MarketplaceItems.model.ResponseMessage;
import com.marketplace.MarketplaceItems.service.ItemImageService;
import com.marketplace.MarketplaceItems.service.ItemService;
import com.marketplace.MarketplaceItems.service.operation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ItemServiceImpl implements ItemService, MessageItemOperations, ItemListAndItemOperations, SaleItemOperations {

    private ItemDAO itemDAO;
    private ItemSaleOperations itemSaleOperations;
    private ItemImageService itemImageService;

    @Autowired
    public void setItemDAO(ItemDAO theItemDAO) {
        itemDAO = theItemDAO;
    }

    @Autowired
    public void setItemSale(@Qualifier("saleServiceImpl") ItemSaleOperations theItemSaleOperations) {
        itemSaleOperations = theItemSaleOperations;
    }

    @Autowired
    public void setItemImageService(@Qualifier("itemImageServiceImpl") ItemImageService theItemImageService) {
        itemImageService = theItemImageService;
    }

    @Override
    public ResponseEntity<PagedModel<Item>> getItems(int page, int size, String search, String craftable, List<String> classes, List<String> qualities, List<String> types) {
        Page<Item> items;
        Pageable pageable = PageRequest.of(page, size);

        List<String> nullableClasses = Optional.ofNullable(classes).filter(list -> !list.isEmpty()).orElse(null);
        List<String> nullableQualities = Optional.ofNullable(qualities).filter(list -> !list.isEmpty()).orElse(null);
        List<String> nullableTypes = Optional.ofNullable(types).filter(list -> !list.isEmpty()).orElse(null);

        items = itemDAO.findAllFilters(search, craftable, nullableClasses, nullableQualities, nullableTypes, pageable);
        PagedModel<Item> pagedModel = PagedModel.of(items.getContent(), new PagedModel.PageMetadata(items.getSize(), items.getNumber(), items.getTotalElements()));

        return new ResponseEntity<>(pagedModel, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResponseMessage> addItem(Item item) {
        try {
            String skuPrefix = item.getSku().split(";")[0];
            String image_url = "";

            if (skuPrefix.matches("\\d+")) {
                Integer shortenSku = Integer.parseInt(item.getSku().split(";")[0]);
                image_url = itemImageService.findByDefindexReturnUrl(shortenSku);
            }

            item.setImage(image_url);
            itemDAO.save(item);
            itemSaleOperations.updateSkuNewAddedItem(item, item.getSku());
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Item was added"));
        } catch (Exception e) {
            throw new InternalServerErrorException("Error occured while adding an Item");
        }
    }
    @Override
    public ResponseEntity<ResponseMessage> deleteItem(String itemSku) {
        try {
            /*
            itemListService.deleteAllByItemSku(itemSku);
            messageService.deleteAllByItemSku(itemSku);
             */
            Item item = itemDAO.findItemBySku(itemSku);
            itemSaleOperations.updateItemDeletedNull(item);
            itemDAO.deleteById(itemSku);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Item was deleted"));
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to delete Item");
        }
    }

    @Override
    public ResponseEntity<ResponseMessage> updateItem(Item item) {
        try {
            Optional<Item> searchItem = itemDAO.findById(item.getSku());

            if(searchItem.isPresent()) {
                Item updateItem = searchItem.get();
                updateItem.setName(item.getName());
                updateItem.setCraftable(item.isCraftable());
                updateItem.setClassItem(item.getClassItem());
                updateItem.setQuality(item.getQuality());
                updateItem.setType(item.getType());

                if(!(item.getImage() == null || item.getImage().isEmpty()))
                    updateItem.setImage(item.getImage());

                itemDAO.save(updateItem);
            } else {
                throw new RecordNotFoundException("Item not found");
            }
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Item was updated successfully"));
        } catch (Exception e) {
            throw new InternalServerErrorException("Error occurred while updating the item");
        }
    }

    @Override
    public ResponseEntity<ResponseMessage> updateItemPrice( Map<String, Object> requestBody) {
        try {
            String sku = (String) requestBody.get("sku");
            Double mpPrice = Double.parseDouble( (String) requestBody.get("mpPrice"));

            Item item = itemDAO.findItemBySku(sku);
            item.setMarketplacePrice(mpPrice);
            itemDAO.save(item);

            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Price was updated"));
        } catch (Exception e) {
            throw new InternalServerErrorException("Error occured while updating item price");
        }
    }

    @Override
    public void saveItem(Item item) {
        itemDAO.save(item);
    }

    @Override
    public void deleteItemBySku(String sku) {
        itemDAO.deleteById(sku);
    }

    @Override
    public Item findItemBySku(String sku) {
        return itemDAO.findItemBySku(sku);
    }

    @Override
    public void updateItemMarketplacePriceBySku(String sku, Double marketplacePrice) {
        itemDAO.updateMarketplacePriceBySku(sku, marketplacePrice);
    }
}
