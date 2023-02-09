 $(document).ready(function() {
          $('#deleteModal').on('show.bs.modal', function (event) {
                var button = $(event.relatedTarget);

                var userId = button.data('userid');
                var username = button.data('username');

                username = "'" + username + "' will be deleted permanently. Are you sure?";

                var modal = $(this);
                modal.find('.modal-body #modalDeleteUserId').val(userId);
                modal.find('.modal-body #modalDeleteUserName').text(username);
          })
        })