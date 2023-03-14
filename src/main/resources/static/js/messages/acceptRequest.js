 $(document).ready(function() {

     $('#acceptRequest').on('show.bs.modal', function (event) {
         var button = $(event.relatedTarget);

         var messageId = button.data('messageId');
         $('#messageId').val(messageId);
     });

      $("#acceptButton").click(function() {

              var messageId = $('#messageId').val();
              var alertMessage = parent.$('#alertMessageAdmin');

              $.ajax({
                  url: "accept?messageId=" + messageId,
                  type: "GET",
                  contentType: "application/json",
                  dataType: 'json',
                  success: function (response, status, xhr) {

                      $('#acceptRequest').modal('hide');

                      console.log(xhr.status);
                      if (xhr.status == 200) {
                          refreshTable(currentPage);
                          setAlert(alertMessage, "Request accepted", true);
                      } else
                            setAlert(alertMessage, "Something went wrong", false);
                  },
                  error: function (xhr, status, error) {
                      $('#acceptRequest').modal('hide');
                      setAlert(alertMessage, "Something went wrong", false);
                  }
              });
              timeout(alertMessage);
      });




 });


