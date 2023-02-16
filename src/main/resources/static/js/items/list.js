var currentPage = 0;
var totalPages = 0;

function fetchList() {
    $.ajax({
      type: "GET",
      url: "fetch-list?page=" + currentPage,
      success: function(response) {
        // Clear current items in the list
        $('#list1').empty();

        // Add new items to the list
        $.each(response._embedded.itemList, function(i, item) {

            if ($('#list2 .details-item[data-sku="' + item.sku + '"]').length > 0)
                return;

          var listItem = $('<li class="list-group-item">');
          var itemText = $('<span class="item-text">').text(item.name);
          var moveButton = $('<button class="btn btn-success move-item" data-page="' + response.page.number + '" style="margin-left: 10px; margin-right: 3px"><i class="fas fa-arrow-right"></i></button>');
          var detailsButton = $('<button class="btn btn-primary details-item" data-sku="' + item.sku + '" data-class-item="' + item.classItem + '" data-craftable="' + item.craftable + '" data-quality="' + item.quality + '" data-type="' + item.type + '" data-bs-toggle="popover" data-bs-placement="left"><i class="fas fa-info-circle"></i> Details</button>');


          listItem.append(itemText);
          listItem.append(moveButton);
          listItem.append(detailsButton);

          $('#list1').append(listItem);
        });

              currentPage = response.page.number;
              totalPages = response.page.totalPages;

              if (currentPage == 0)
                  $('#prev-page').attr('disabled', true);
              else
                  $('#prev-page').attr('disabled', false);


              if (currentPage == totalPages - 1)
                 $('#next-page').attr('disabled', true);
              else
                 $('#next-page').attr('disabled', false);
      },
      error: function(jqXHR, textStatus, errorThrown) {
        console.log("Error fetching list: " + textStatus + ", " + errorThrown);
      }
    });
}

function updatePagination(direction) {
      if (direction === "prev") {
        currentPage--;
      } else {
        currentPage++;
      }
      fetchList();
}

fetchList();

$(document).ready(function() {

          $(".list-group").sortable({
            connectWith: ".list-group",
            placeholder: "list-group-item bg-primary",
            dropOnEmpty: true,
            update: function(event, ui) {
                        var item = ui.item;
                        var button = item.find(".move-item");
                        if (item.parent().attr("id") == "list1") {

                           var dataPage = item.find(".move-item").data('page');

                            if(dataPage == currentPage) {
                              item.appendTo("#list1");
                              button.removeClass("btn-danger").addClass("btn-success");
                              button.html("<i class='fas fa-arrow-right'></i>");
                            } else {
                              item.remove();
                            }


                        } else {
                          button.removeClass("btn-success").addClass("btn-danger");
                          button.html("<i class='fas fa-arrow-left'></i>");
                        }
                    }
          }).disableSelection();

          $(document).on("click", ".move-item", function() {
            var item = $(this).closest(".list-group-item");
            if (item.parent().attr("id") == "list1") {

              item.appendTo("#list2");
              $(this).removeClass("btn-success").addClass("btn-danger");
              $(this).html("<i class='fas fa-arrow-left'></i>");

            } else {

              var dataPage = $(this).data('page');

              if(dataPage == currentPage) {
                    item.appendTo("#list1");
                    $(this).removeClass("btn-danger").addClass("btn-success");
                    $(this).html("<i class='fas fa-arrow-right'></i>");
              } else {
                 item.remove();
              }
            }
          });



  $(document).on("click", ".details-item", function() {
         // Get the item text from the closest list-group-item element

                  var sku = $(this).data('sku');
                  var classItem = $(this).data('class-item');
                  var craftable = $(this).data('craftable');
                  var quality = $(this).data('quality');
                  var type = $(this).data('type');
                  var itemText = $(this).closest('.list-group-item').find('.item-text').text();

                  // Generate the HTML for the popover content
                  var contentHTML = '<div class="popover-details">' +
                                    '<p>Class: ' + classItem + '</p><hr>' +
                                    '<p>Craftable: ' + craftable + '</p><hr>' +
                                    '<p>Quality: ' + quality + '</p><hr>' +
                                    '<p>Type: ' + type + '</p><hr>' +
                                    '</div>';

                  // Show the popover
                  $(this).popover({
                    trigger: 'focus',
                    title: sku,
                    content: contentHTML,
                    html: true,
                    placement: 'right'
                  }).popover('show');
         });
});