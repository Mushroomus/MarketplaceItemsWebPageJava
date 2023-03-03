
function addMessageToList(message) {

    console.log(message);
    var list = $('#message-list');

    var item = $('<li>').addClass('list-group-item');
    var username = $('<small>').addClass('text-muted').text('Username: ' + message.username);
    var title;

    if(message.messageType == 'delete')
        title = $('<h5>').addClass('mb-1').text('Delete Item');
    else if(message.messageType == 'add')
        title = $('<h5>').addClass('mb-1').text('Add Item');
    else if(message.messageType == 'update')
        title = $('<h5>').addClass('mb-1').text('Update Item');
    else
        title = $('<h5>').addClass('mb-1').text('Update Price');

    var body;


    if(message.messageType == 'add')
        body = $('<p>').addClass('mb-1').text('Item: ' + message.name);
    else
        body = $('<p>').addClass('mb-1').text('Item: ' + message.item.name);

    var button = $('<button>').addClass('btn btn-primary float-end')
                                   .attr('data-bs-toggle', 'modal')
                                   .append($('<i>').addClass('fas fa-info-circle'));

     switch (message.messageType) {
            case 'delete':
                button.attr('data-bs-target', '#deleteModalInformations');
                button.data('itemSku', message.item.sku);
                button.data('itemName', message.item.name);
                button.data('itemCraftable', message.item.craftable);
                button.data('itemClass', message.item.classItem);
                button.data('itemQuality', message.item.quality);
                button.data('itemType', message.item.type);
                break;
            case 'add':
                button.attr('data-bs-target', '#addModalInformations');
                button.data('itemSku', message.sku);
                button.data('itemName', message.name);
                button.data('itemCraftable', message.craftable);
                button.data('itemClass', message.itemClass);
                button.data('itemQuality', message.quality);
                button.data('itemImage', message.image);
                button.data('itemPrice', message.marketplacePrice);
                button.data('itemType', message.type);
                break;
            case 'update':
                button.attr('data-bs-target', '#updateModalInformations');

                button.data('oldItemSku', message.item.sku);
                button.data('oldItemName', message.item.name);
                button.data('oldItemCraftable', message.item.craftable);
                button.data('oldItemClass', message.item.classItem);
                button.data('oldItemQuality', message.item.quality);
                button.data('oldItemType', message.item.type);

                button.data('itemName', message.name);
                button.data('itemCraftable', message.craftable);
                button.data('itemClass', message.itemClass);
                button.data('itemQuality', message.quality);
                button.data('itemType', message.type);

                break;
            case 'updatePrice':
                button.attr('data-bs-target', '#updatePriceModalInformations');

                 button.data('oldItemSku', message.item.sku);
                 button.data('oldItemName', message.item.name);
                 button.data('oldItemCraftable', message.item.craftable);
                 button.data('oldItemClass', message.item.classItem);
                 button.data('oldItemQuality', message.item.quality);
                 button.data('oldItemType', message.item.type);
                  button.data('oldItemPrice', message.item.marketplacePrice);

                 button.data('itemPrice', message.marketplacePrice);
                 break;
        }


    item.append(title).append(body).append(username).append(button);
    list.append(item);
}

$(document).ready(function() {

    window.datetimepickerStartDate = $('#datetimepickerStartDate').tempusDominus({
       //put your config here
     });

      window.datetimepickerEndDate = $('#datetimepickerEndDate').tempusDominus({
       //put your config here
     });

    $.ajax({
        type: "GET",
        url: "/messages/fetch",
        contentType: "application/json",
        success: function(response, status, xhr) {
            console.log(response);
            response.messages.forEach(function(message) {
                addMessageToList(message);
            });
        },
        error: function(xhr, status, error) {
            console.log(error);
        }
    });

});


