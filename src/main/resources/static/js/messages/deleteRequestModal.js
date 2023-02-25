$(document).ready(function() {

     $('#requestDeleteModal').on('show.bs.modal', function (event) {
         var button = $(event.relatedTarget);

         var itemSku = button.data('itemsku');
         var itemName = button.data('itemname');
         console.log(itemSku);

         var text = 'Request to delete ' + "'" + itemName + "'";

         var modal = $(this);
         modal.find('.modal-body #requestDeleteSku').val(itemSku);
         modal.find('.modal-body #requestDeleteText').text(text);
     })

     $("#requestDeleteModalSubmit").click(function () {

         var itemSku = $("#requestDeleteSku").val();
         console.log(itemSku);

         $.ajax({
             url: "/messages/create?sku=" + itemSku,
             type: "POST",
             contentType: "application/json",
             data: JSON.stringify({
                 messageType: "delete",
                 }),
             dataType: 'json',
             success: function (response, status, xhr) {

                var alertMessage = parent.$('#alertMessageUser');
                console.log(alertMessage);

                 if (xhr.status == 200) {
                     $('#requestDeleteModal').modal('hide');
                    // alertMessage.text('Request was sent').addClass('alert alert-success').show();
                 } else {
                     /*
                     alertMessage.text('Something went wrong').addClass("alert alert-danger").show();

                  setTimeout(function() {
                       alertMessage.fadeOut('slow');
                    }, 2000);
                    */
                  }
             },
             error: function (xhr, status, error) {
             /*
                 alertMessage.text('Something went wrong').addClass("alert alert-danger").show();

                 setTimeout(function() {
                      alertMessage.fadeOut('slow');
                   }, 2000);
                   */
             }
         });

         setTimeout(function () {
             $("#modalDeleteMessage").fadeOut('slow');
         }, 2000);

     });
 });