
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

    var button = $('<button>').addClass('btn btn-primary btn-sm float-end')
                                   .text('View Details')
                                   .attr('data-bs-toggle', 'modal');

     switch (message.messageType) {
            case 'delete':
                button.attr('data-bs-target', '#deleteModalInformations');
                button.data('itemName', message.item.name);
                break;
            case 'add':
                button.attr('data-bs-target', '#addModal');
                button.data('itemName', message.name);
                break;
            case 'update':
                button.attr('data-bs-target', '#updateModal');
                button.data('itemId', message.item.name);
                break;
            case 'updatePrice':
                button.attr('data-bs-target', '#updatePriceModal');
                button.data('itemId', message.item.name);
                button.data('marketplacePrice', message.marketplacePrice);
                break;
            default:
                // handle other message types if needed
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


