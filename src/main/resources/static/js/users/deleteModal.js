$(document).ready(function() {

$('#deleteModal').on('show.bs.modal', function (event) {
                var button = $(event.relatedTarget);

                var userId = button.data('userid');
                var username = button.data('username');

                username = "'" + username + "' will be deleted permanently. Are you sure?";

                var modal = $(this);
                modal.find('.modal-body #modalDeleteUserId').val(userId);
                modal.find('.modal-body #modalDeleteUserName').text(username);
          });


 $("#deleteModalSubmit").click(function() {

                var userId = $("#modalDeleteUserId").val();
                var alertMessage = parent.$('#alertMessage');

                $.ajax({
                        url: "delete?id=" + userId,
                        type: "GET",
                        contentType: "application/json",
                        dataType: 'json',
                        success: function(response, status, xhr) {
                            if (xhr.status == 200) {

                                 $('#deleteModal').modal('hide');
                                 refreshTable(currentPage);
                                 alertMessage.text('User was deleted').addClass('alert alert-success').show();
                            }
                            else
                                alertMessage.text('Something went wrong').addClass('alert alert-danger').show();
                            },
                             error: function(xhr, status, error) {
                                 alertMessage.text('Something went wrong').addClass('alert alert-danger').show();
                             }
                        });
                        timeout(alertMessage);
                 });
});