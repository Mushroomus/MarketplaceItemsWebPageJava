$(document).ready(function() {
          $('#editModal').on('show.bs.modal', function (event) {
                var button = $(event.relatedTarget);

                var itemSku = button.data('itemsku');
                var itemName = button.data('itemname');
                var itemCraftable = button.data('itemcraftable');
                var itemClass = button.data('itemclass');
                var itemQuality = button.data('itemquality');
                var itemType = button.data('itemtype');

                var modal = $(this);

                modal.find('.modal-body #modalItemSku').val(itemSku);
                modal.find('.modal-body #modalItemName').val(itemName);
                modal.find('.modal-body #modalItemCraftable').val(itemCraftable.toString());
                modal.find('.modal-body #modalItemClass').val(itemClass);
                modal.find('.modal-body #modalItemQuality').val(itemQuality);
                modal.find('.modal-body #modalItemType').val(itemType);
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

                 var alertMessage = parent.$('#alertMessage');

                if (editForm[0].checkValidity()) {
                     $.ajax({
                           url: "update",
                           type: "PUT",
                           data: JSON.stringify({
                              sku: sku,
                              name: name,
                              craftable: craftable,
                              classItem: itemClass,
                              quality: quality,
                              type: type
                           }),
                           contentType: "application/json",
                           success: function(response) {
                                $('#editModal').modal('hide');
                                refreshTable(currentPage);
                                setAlert(alertMessage, "Item edited", true);
                           },
                           error: function(error) {
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