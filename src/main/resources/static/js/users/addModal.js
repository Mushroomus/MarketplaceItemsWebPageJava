$(document).ready(function() {

    $('#addModal').on('show.bs.modal', function (event) {
        var modal = $(this);
        modal.find('.modal-body #userUsername').val('');
        modal.find('.modal-body #userPassword').val('');
        modal.find('.modal-body #userPasswordRepeat').val('');
        modal.find('.modal-body #userRole').val('ADMIN');

        $('#userUsername').trigger('input');
        $('#userPassword').trigger('input');
        $('#userPasswordRepeat').trigger('input');
    });

               $('#userUsername').on('input', function() {

                   var username = $(this).val();

                   if (username == "") {
                     // Show custom error message
                     $("#usernameValidation").text("Username can't be empty").show();
                     $("#userUsername").removeClass("is-valid").addClass("is-invalid");
                   } else {
                     $("#usernameValidation").hide();
                     $("#userUsername").removeClass("is-invalid").addClass("is-valid");
                   }

                   $('#addSubmit').prop('disabled', !checkAllInputsValid());
                 });

                   $('#userPassword').on('input', function() {

                      var password = $(this).val();
                      var pattern = new RegExp("^(?=.*[0-9])(?=.*[!@#$%^&+=])(?=\\S+$).{6,}$");

                      if (!pattern.test(password)) {
                        // Show custom error message
                        $("#passwordValidation").text("At least six length, one special char and one number").show();
                        $("#userPassword").removeClass("is-valid").addClass("is-invalid");
                      } else {
                        $("#passwordValidation").hide();
                        $("#userPassword").removeClass("is-invalid").addClass("is-valid");
                      }

                      $('#userPasswordRepeat').trigger('input');

                      $('#addSubmit').prop('disabled', !checkAllInputsValid());

                    });

               $('#userPasswordRepeat').on('input', function() {
                 var password = $("#userPassword").val();
                 var repeatPassword = $(this).val();

                 if(password == "") {
                    $("#passwordMatchError").text("Password is empty").show();
                    $("#userPasswordRepeat").removeClass("is-valid").addClass("is-invalid");
                 }
                 else if (password !== repeatPassword) {
                   // Show custom error message
                   $("#passwordMatchError").text("Passwords do not match").show();
                   $("#userPasswordRepeat").removeClass("is-valid").addClass("is-invalid");
                 } else {
                   $("#passwordMatchError").hide();
                   $("#userPasswordRepeat").removeClass("is-invalid").addClass("is-valid");
                 }

                  $('#addSubmit').prop('disabled', !checkAllInputsValid());

               });

               $('#userUsername').trigger('input');
               $('#userPassword').trigger('input');

             $("#addSubmit").click(function(event) {

                var password = $("#userPassword").val();
                var repeatPassword = $("#userPasswordRepeat").val();

                var isValid = checkAllInputsValid();

                if(!isValid)
                    return;

                var username = $("#userUsername").val();
                var password = $("#userPassword").val();
                var repeatPassword = $("#userPasswordRepeat").val();
                var role = $("#userRole").val();

                var modalEditMessage = $("#modalEditMessage");

                $.ajax({
                    url: "add",
                    type: "POST",
                    contentType: "application/json",
                    data: JSON.stringify({
                        username: username,
                        password: password,
                        role: role
                    }),
                    dataType: 'json',
                    success: function(response, status, xhr) {
                        if (xhr.status == 200) {
                             setAlert(modalEditMessage, "User added", true);
                             refreshTable(currentPage);

                              var username = $("#userUsername").val('');
                              var password = $("#userPassword").val('');
                              var repeatPassword = $("#userPasswordRepeat").val('');
                              var role = $("#userRole").val('ADMIN');

                              $('#userUsername').trigger('input');
                              $('#userPassword').trigger('input');
                        }
                        else
                            setAlert(modalEditMessage, "Something went wrong", false);
                        },
                         error: function(xhr, status, error) {
                            setAlert(modalEditMessage, "Something went wrong", false);
                         }
                    });

                    timeout(modalEditMessage);
             });
});