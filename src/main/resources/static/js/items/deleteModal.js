 $(document).ready(function() {

     $('#deleteModal').on('show.bs.modal', function (event) {
         var button = $(event.relatedTarget);

         var itemSku = button.data('itemsku');
         var itemName = button.data('itemname');

         itemName = "'" + itemName + "' will be deleted permanently. Are you sure?";

         var modal = $(this);
         modal.find('.modal-body #modalDeleteItemSku').val(itemSku);
         modal.find('.modal-body #modalDeleteItemName').text(itemName);
     })

     $("#deleteModalSubmit").click(function () {

         var itemSku = $("#modalDeleteItemSku").val();

         $.ajax({
             url: "delete?sku=" + itemSku,
             type: "GET",
             contentType: "application/json",
             dataType: 'json',
             success: function (response, status, xhr) {

                var alertMessage = parent.$('#alertMessage');

                 if (xhr.status == 200) {

                     $('#deleteModal').modal('hide');
                     refreshTable(currentPage);
                     alertMessage.text('Item was deleted').addClass('alert alert-success').show();
                 } else
                     alertMessage.text('Something went wrong').addClass("alert alert-danger").show();

                  setTimeout(function() {
                       alertMessage.fadeOut('slow');
                    }, 2000);
             },
             error: function (xhr, status, error) {
                 alertMessage.text('Something went wrong').addClass("alert alert-danger").show();

                 setTimeout(function() {
                      alertMessage.fadeOut('slow');
                   }, 2000);
             }
         });

         setTimeout(function () {
             $("#modalDeleteMessage").fadeOut('slow');
         }, 2000);

     });
 });


