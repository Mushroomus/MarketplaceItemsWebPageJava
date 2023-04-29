 function checkEditInputsValid() {

    var username = $('#modalUsername');
    var password = $('#modalPassword');
    var repeatPassword = $('#modalPasswordRepeat');

    var checkbox = $("#changePasswordCheckbox").prop('checked');

    if(username.hasClass('is-invalid'))
        return false;

    if(checkbox) {
       if(password.hasClass('is-invalid'))
            return false;

       if(repeatPassword.hasClass('is-invalid'))
            return false;
    }

     return true;
 }


 $(document).ready(function() {

     $('#modalUsername').on('input', function() {

       var username = $(this).val();

       if (username == "") {
         // Show custom error message
         $("#usernameValidation").text("Username can't be empty").show();
         $("#modalUsername").removeClass("is-valid").addClass("is-invalid");
       } else {
         $("#usernameValidation").hide();
         $("#modalUsername").removeClass("is-invalid").addClass("is-valid");
       }

        $('#modalSubmit').prop('disabled', !checkEditInputsValid());
     });

     $('#modalPassword').on('input', function() {

           var password = $(this).val();
           var pattern = new RegExp("^(?=.*[0-9])(?=.*[!@#$%^&+=])(?=\\S+$).{6,}$");

         if($("#changePasswordCheckbox").prop('checked')) {
                if (!pattern.test(password)) {
                     // Show custom error message
                     $("#passwordValidation").text("At least six length, one special char and one number").show();
                     $("#modalPassword").removeClass("is-valid").addClass("is-invalid");
                   } else {
                     $("#passwordValidation").hide();
                     $("#modalPassword").removeClass("is-invalid").addClass("is-valid");
                   }

                   $('#modalPasswordRepeat').trigger('input');

                 }

                  $('#modalSubmit').prop('disabled', !checkEditInputsValid());
            });


    $('#modalPasswordRepeat').on('input', function() {
      var password = $("#modalPassword").val();
      var repeatPassword = $(this).val();

    if($("#changePasswordCheckbox").prop('checked')) {
          if(password == "") {
             $("#passwordMatchError").text("Password is empty").show();
             $("#modalPasswordRepeat").removeClass("is-valid").addClass("is-invalid");
          }
          else if (password !== repeatPassword) {
            // Show custom error message
            $("#passwordMatchError").text("Passwords do not match").show();
            $("#modalPasswordRepeat").removeClass("is-valid").addClass("is-invalid");
          } else {
            $("#passwordMatchError").hide();
            $("#modalPasswordRepeat").removeClass("is-invalid").addClass("is-valid");
          }
      }

      $('#modalSubmit').prop('disabled', !checkEditInputsValid());
    });

             $('#modalUsername').trigger('input');
             $("#passwordField").hide();
             $("#repeatPasswordField").hide();
             $("#alertMessage").hide();

               $('#editModal').on('show.bs.modal', function (event) {
                             var button = $(event.relatedTarget);

                             var userId = button.data('userid');
                             var username = button.data('username');
                             var role = button.data('role');
                             var password = button.data('password');

                             var modal = $(this);

                             modal.find('.modal-body #modalUserId').val(userId);
                             modal.find('.modal-body #modalUsername').val(username);
                             modal.find('.modal-body #modalUserRole').val(role);
                             modal.find('.modal-body #modalUserPassword').val(password);

                             modal.find('.modal-body #changePasswordCheckbox').prop('checked', false);
                             $('#passwordField').slideUp();
                             $('#repeatPasswordField').slideUp();

                             modal.find('.modal-body #modalPassword').val('');
                             modal.find('.modal-body #modalPasswordRepeat').val('');

                       });

                       $("#changePasswordCheckbox").change(function() {
                             var passwordField = $('#passwordField');
                             var passwordRepeatField = $('#repeatPasswordField');

                              if (this.checked) {
                                 passwordField.slideDown();
                                 passwordRepeatField.slideDown();
                                 $('#modalPassword').trigger('input');
                              } else {
                                 passwordField.slideUp();
                                 passwordRepeatField.slideUp();

                                 passwordField.val('');
                                 passwordRepeatField.val('');
                                 $("#modalPassword").removeClass("is-valid").removeClass("is-invalid");
                                 $("#modalPasswordRepeat").removeClass("is-valid").removeClass("is-invalid");
                              }
                              $('#modalSubmit').prop('disabled', !checkEditInputsValid());
                         });

                        $("#modalSubmit").click(function() {

                             var id = $('#modalUserId').val();
                             var role = $('#modalUserRole').val();
                             var password = $('#modalUserPassword').val();
                             var username = $('#modalUsername').val();

                             var changePassword = $("#modalPassword").val();
                             var repeatPassword = $("#modalPasswordRepeat").val();
                             var passwordChecked = $("#changePasswordCheckbox").is(":checked");

                             var modalMessage = $("#modalMessage");

                             if (username == "" || ( passwordChecked && ( changePassword == "" || changePassword != repeatPassword)) ){

                                 if(username == "")
                                    setAlert(modalMessage, "Username is empty", false);
                                 else if(changePassword == "")
                                    setAlert(modalMessage, "Password is empty", false);
                                 else
                                    setAlert(modalMessage, "Passwords do not match", false);

                                 timeout(modalMessage);
                                 return;
                             }

                             if(passwordChecked && (changePassword.length < 6 || ( !changePassword.match(/\d/) || !changePassword.match(/[!@#$%^&*]/) ))) {

                                if(changePassword.length < 6)
                                    setAlert(modalMessage, "Password must be at least 6 characters long", false);
                                else if(!changePassword.match(/\d/) || !changePassword.match(/[!@#$%^&*]/))
                                    setAlert(modalMessage, "Password must contain at least one number and one special character", false);

                                 timeout(modalMessage);
                                 return;
                             }

                             var alertMessage = parent.$('#alertMessage');

                             $.ajax({
                               url: "/users",
                               type: "PUT",
                               data: JSON.stringify({
                                 user: {
                                      id: id,
                                      username: username,
                                      password: password,
                                      role: role
                                 },
                                 changePassword: changePassword
                               }),
                               contentType: "application/json",
                               success: function(response) {
                                    $('#editModal').modal('hide');
                                    refreshTable(currentPage);
                                    setAlert(alertMessage, "User edited", true)
                               },
                               error: function(error) {
                                    setAlert(alertMessage, "Something went wrong", false);
                               }
                             });
                             timeout(alertMessage);

                          });
});