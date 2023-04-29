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
                  url: "/messages/" + messageId + "/reject",
                  type: "GET",
                  contentType: "application/json",
                  dataType: 'json',
                  success: function (response, status, xhr) {

                      $('#rejectRequest').modal('hide');

                      console.log(xhr.status);
                      if (xhr.status == 200) {
                          refreshTable(currentPage);
                          setAlert(alertMessage, "Request rejected", true);
                      } else
                          setAlert(alertMessage, "Something went wrong", false);
                  },
                  error: function (xhr, status, error) {
                      $('#rejectRequest').modal('hide');
                      setAlert(alertMessage, "Something went wrong", false);
                  }
              });
              timeout(alertMessage);
      });




 });


