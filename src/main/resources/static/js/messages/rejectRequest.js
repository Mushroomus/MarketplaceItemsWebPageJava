 $(document).ready(function() {

     $('#rejectRequest').on('show.bs.modal', function (event) {
         var button = $(event.relatedTarget);

         var messageId = button.data('messageId');
         $('#messageId').val(messageId);
     });

      $("#rejectButton").click(function() {

              var messageId = $('#messageId').val();
              var alertMessage = parent.$('#alertMessageAdmin');

              $.ajax({
                  url: "reject?messageId=" + messageId,
                  type: "GET",
                  contentType: "application/json",
                  dataType: 'json',
                  success: function (response, status, xhr) {

                      $('#rejectRequest').modal('hide');

                      console.log(xhr.status);
                      if (xhr.status == 200) {
                          refreshTable(currentPage);
                          alertMessage.text('Request was rejected').addClass('alert alert-success').show();
                      } else
                          alertMessage.text('Something went wrong').addClass("alert alert-danger").show();

                       setTimeout(function() {
                            alertMessage.fadeOut('slow');
                         }, 2000);
                  },
                  error: function (xhr, status, error) {
                      $('#rejectRequest').modal('hide');
                      alertMessage.text('Something went wrong').addClass("alert alert-danger").show();

                      setTimeout(function() {
                           alertMessage.fadeOut('slow');
                        }, 2000);
                  }
              });
      });




 });


