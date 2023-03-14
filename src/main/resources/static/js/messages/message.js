var firstFetch = true;
var filter = false;
var searchFilter = false;
var currentPage = 0;
refreshTable(0);

function addMessageToList(message) {

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

            var rejectButton = $('<button>').addClass('btn btn-danger float-end')
                                               .attr('data-bs-toggle', 'modal')
                                               .append($('<i>').addClass('fas fa-times'))
                                               .attr('data-bs-target', '#rejectRequest')
                                               .css('margin-right', '10px')
                                               .data('messageId', message.id);

            var acceptButton = $('<button>').addClass('btn btn-success float-end')
                                                           .attr('data-bs-toggle', 'modal')
                                                           .append($('<i>').addClass('fas fa-check'))
                                                           .attr('data-bs-target', '#acceptRequest')
                                                           .css('margin-right', '10px')
                                                           .data('messageId', message.id);

    item.append(title).append(body).append(username).append(button).append(rejectButton).append(acceptButton);
    list.append(item);
}

 function stringToCorrectDate(stringDate) {
        let dateString = stringDate;
        let dateComponents = dateString.split(".");
        let day = dateComponents[0];
        let month = dateComponents[1];
        let yearAndTime = dateComponents[2].split(", ");
        let year = yearAndTime[0];
        let time = yearAndTime[1];

        return new Date(year, month - 1, day, time.split(":")[0], time.split(":")[1]);
    }

    function clearFilters() {
        filter = false;
        searchFilter = false;
        currentPage = 0;
        refreshTable(0);
    }

    function filterButton() {
     filter = true;
     refreshTable(0);
    }

    function searchButton() {
     searchFilter = true;
     refreshTable(0);
    }

function createUrl(page) {

        if(page == null || page == "") {
            currentPage = 0;
            page = 0;
        }
        currentPage = page;

        let url = "fetch";
        url += "?page=" + page;

        if(firstFetch == true) {
            firstFetch = false;
            return url += "&types=add,delete,update,updatePrice"
        }

        if(searchFilter == true) {
            var search = $("#searchInput").val();

            if(searchFilter != null)
                url += "&search=" + search;
        }

        if(filter == true) {

            var selectedTypes = "";

            $("input[type='checkbox']").each(function() {
              if($(this).is(":checked")) {
                selectedTypes += $(this).val() + ",";
              }
            });

            if(selectedTypes.slice(-1) == ",") {
              selectedTypes = selectedTypes.slice(0, -1);
            }

            var timestampStartDate = null;
            var timestampEndDate = null;

            if($("#datetimepickerInputStartDate").val() != "" && $("#datetimepickerInputStartDate").val() != null) {
                var startDate = stringToCorrectDate( $("#datetimepickerInputStartDate").val() );
                timestampStartDate = startDate.getTime();
            }

            if($("#datetimepickerInputEndDate").val() != "" && $("#datetimepickerInputStartDate").val() != null) {
                var endDate = stringToCorrectDate( $("#datetimepickerInputEndDate").val() );
                timestampEndDate = endDate.getTime();
            }

            if (selectedTypes != null && selectedTypes != "")
                url += "&types=" + selectedTypes;
            if (startDate != null && startDate != "")
                url += "&startDate=" + timestampStartDate;
            if (endDate != null && endDate != "")
                url += "&endDate=" + timestampEndDate;

            console.log(url);
            firstFetch = false;
        }

        return url;
    }

function refreshTable(page) {

         $.ajax({
           url: createUrl(page),
           type: "GET",
           dataType: 'json',
            success: function(response, status, xhr) {
                      console.log(response);

                      $('#message-list').empty();

                       if(response.totalPages == 0) {
                            $(".pagination").empty();
                            $("#noMessages").show();
                       } else {
                            if(currentPage == response.totalPages)
                                refreshTable(--currentPage);
                            $("#noMessages").hide();

                            response.messages.forEach(function(message) {
                                addMessageToList(message);
                            });

                            const pages = {
                                totalPages: response.totalPages,
                                totalElements: response.totalElements,
                                number: response.currentPage
                            }
                            updatePagination(pages);
                        }
                  },
                  error: function(xhr, status, error) {
                      console.log(error);
                  }
         });
}

$(document).ready(function() {

    $("#toggleButton").click(function() {
        $('.sidebar-container').toggleClass("d-md-block");
        if ($('.sidebar-container').hasClass("d-md-block")) {
          $("#toggleButton svg").removeClass("fa-arrow-right").addClass("fa-arrow-left");
          //$("#toggleButton i").toggleClass("fa-arrow-right fa arrow-left");
          $('.sidebar-container').css("animation-name", "fadeInLeft");
        } else {
          $("#toggleButton svg").removeClass("fa-arrow-left").addClass("fa-arrow-right");
          //$("#toggleButton i").toggleClass("fa-arrow-left fa arrow-right");
          $('.sidebar-container').css("animation-name", "fadeOutRight");
        }
     });

    window.datetimepickerStartDate = $('#datetimepickerStartDate').tempusDominus({
     });

      window.datetimepickerEndDate = $('#datetimepickerEndDate').tempusDominus({
     });
});


