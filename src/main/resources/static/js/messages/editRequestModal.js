$(document).ready(function() {
          $('#requestEditModal').on('show.bs.modal', function (event) {
                var button = $(event.relatedTarget);

                var itemSku = button.data('itemsku');
                var itemName = button.data('itemname');
                var itemCraftable = button.data('itemcraftable');
                var itemClass = button.data('itemclass');
                var itemQuality = button.data('itemquality');
                var itemType = button.data('itemtype');
                var itemImage = button.data('itemimage');

                var modal = $(this);

                modal.find('.modal-body #modalItemSku').val(itemSku);
                modal.find('.modal-body #modalItemName').val(itemName);
                modal.find('.modal-body #modalItemCraftable').val(itemCraftable.toString());
                modal.find('.modal-body #modalItemClass').val(itemClass);
                modal.find('.modal-body #modalItemQuality').val(itemQuality);
                modal.find('.modal-body #modalItemType').val(itemType);
                modal.find('.modal-body #modalItemImage').val(itemImage);
          });


            var editForm = $("#editForm");
            var submitButton = $("#editSubmitButton");

            editForm.on('submit', function(event) {

                event.preventDefault();

                 var sku = $("#modalItemSku").val();
                 var name = $("#modalItemName").val();
                 var craftable = $("#modalItemCraftable").val();
                 var itemClass = $("#modalItemClass").val();
                 var quality = $("#modalItemQuality").val();
                 var type = $("#modalItemType").val();
                 var image = $("#modalItemImage").val();

                 var alertMessage = parent.$('#alertMessageUser');

                if (editForm[0].checkValidity()) {
                     $.ajax({
                           url: "/messages/create?sku=" + sku,
                           type: "POST",
                           data: JSON.stringify({
                              messageType: "update",
                              name: name,
                              craftable: craftable,
                              itemClass: itemClass,
                              quality: quality,
                              type: type,
                              image: image
                           }),
                           contentType: "application/json",
                           success: function(response,status,xhr) {
                               $('#requestEditModal').modal('hide');

                               if (xhr.status == 200)
                                   setAlert(alertMessage, "Request sent", true);
                               else
                                   setAlert(alertMessage, "Something went wrong", false);
                           },
                           error: function(error) {
                               $('#requestEditModal').modal('hide');
                               setAlert(alertMessage, "Something went wrong", false);
                           }
                         });
                         timeout(alertMessage);
                }

            });

           editForm.on('input', function(event) {

             if (editForm[0].checkValidity())
                submitButton.prop('disabled', false);
             else
                submitButton.prop('disabled', true);
           });
        });