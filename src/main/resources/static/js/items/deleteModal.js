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
                 if (xhr.status == 200) {

                     $('#deleteModal').modal('hide');
                     refreshTable(currentPage);

                 } else if (xhr.status == 400)
                     $("#modalDeleteMessage").text(response.message).addClass("alert alert-danger").show();
                 else
                     $("#modalDeleteMessage").text(response.message).addClass("alert alert-danger").show();

             },
             error: function (xhr, status, error) {
                 console.error(error);
                 console.error(xhr.responseText);
             }
         });

         setTimeout(function () {
             $("#modalDeleteMessage").fadeOut('slow');
         }, 2000);

     });
 });


