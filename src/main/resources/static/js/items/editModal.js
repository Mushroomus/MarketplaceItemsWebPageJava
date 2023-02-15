$(document).ready(function() {
          $('#editModal').on('show.bs.modal', function (event) {
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

                if (editForm[0].checkValidity())
                    editForm[0].submit();
            });

           editForm.on('input', function(event) {

             if (editForm[0].checkValidity())
                submitButton.prop('disabled', false);
             else
                submitButton.prop('disabled', true);
           });

        /*
          $("#userEditModalSubmit").click(function() {

                var name = $('#modalItemName').val();

                if (name == ""){
                    event.preventDefault();
                     $("#modalEditItemMessage").text("Name is empty").addClass("alert alert-danger").show();
                }

                setTimeout(function() {
                        $("#modalEditItemMessage").fadeOut('slow');
                     }, 2000);

          });
          */
        });