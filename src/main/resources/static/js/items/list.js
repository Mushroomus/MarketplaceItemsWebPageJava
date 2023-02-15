$(document).ready(function() {

          $(".list-group").sortable({
            connectWith: ".list-group",
            placeholder: "list-group-item bg-primary",
            dropOnEmpty: true,
            update: function(event, ui) {
                        var item = ui.item;
                        var button = item.find(".move-item");
                        if (item.parent().attr("id") == "list1") {
                          button.removeClass("btn-success").addClass("btn-danger");
                          button.html("<i class='fas fa-arrow-right'></i>");
                        } else {
                          button.removeClass("btn-danger").addClass("btn-success");
                          button.html("<i class='fas fa-arrow-left'></i>");
                        }
                    }
          }).disableSelection();

          $(".move-item").on("click", function() {
            var item = $(this).closest(".list-group-item");
            if (item.parent().attr("id") == "list1") {
              item.appendTo("#list2");
              $(this).removeClass("btn-success").addClass("btn-danger");
              $(this).html("<i class='fas fa-arrow-left'></i>");
            } else {
              item.appendTo("#list1");
              $(this).removeClass("btn-danger").addClass("btn-success");
              $(this).html("<i class='fas fa-arrow-right'></i>");
            }
          });

      // Attach click event listener to button with "details-item" class
        $(".details-item").on("click", function() {
          // Get the item text from the closest list-group-item element
          var itemText = $(this).closest('.list-group-item').find('.item-text').text();

          // Generate the HTML for the popover content
          var contentHTML = '<div class="popover-details">' +
                            '<p>Item: ' + itemText + '</p>' +
                            '<p>Details: Some sample details here</p>' +
                            '</div>';

          // Show the popover
          $(this).popover({
            trigger: 'focus',
            content: contentHTML,
            html: true,
            placement: 'right'
          }).popover('show');
        });

});