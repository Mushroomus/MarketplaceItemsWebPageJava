 $(document).ready(function() {

    $("#passwordField").hide();
             $("#repeatPasswordField").hide();
             $("#alertMessage").hide();

               $('#editModal').on('show.bs.modal', function (event) {
                             var button = $(event.relatedTarget);

                             var userId = button.data('userid');
                             var username = button.data('username');
                             var role = button.data('role');
                             var password = button.data('password');
                             console.log(password);

                             var modal = $(this);

                             modal.find('.modal-body #modalUserId').val(userId);
                             modal.find('.modal-body #modalUserName').val(username);
                             modal.find('.modal-body #modalUserRole').val(role);
                             modal.find('.modal-body #modalUserPassword').val(password);
                       });

                       $("#changePasswordCheckbox").change(function() {
                             var passwordField = $('#passwordField');
                             var passwordRepeatField = $('#repeatPasswordField');

                              if (this.checked) {
                                 passwordField.slideDown();
                                 passwordRepeatField.slideDown();
                              } else {
                                 passwordField.slideUp();
                                 passwordRepeatField.slideUp();
                              }
                         });

                        $("#modalSubmit").click(function() {

                             var id = $('#modalUserId').val();
                             var role = $('#modalUserRole').val();
                             var password = $('#modalUserPassword').val();
                             var username = $('#modalUsername').val();

                             var changePassword = $("#modalPassword").val();
                             var repeatPassword = $("#modalPasswordRepeat").val();
                             var passwordChecked = $("#changePasswordCheckbox").is(":checked");

                             if (username == "" || ( passwordChecked && ( changePassword == "" || changePassword != repeatPassword)) ){

                                 if(username == "")
                                     $("#modalMessage").text("Username is empty").addClass("alert alert-danger").show();
                                 else if(changePassword == "")
                                     $("#modalMessage").text("Password is empty").addClass("alert alert-danger").show();
                                 else
                                     $("#modalMessage").text("Passwords do not match").addClass("alert alert-danger").show();

                                 setTimeout(function() {
                                         $("#modalMessage").fadeOut('slow');
                                      }, 2000);

                                 return;
                             }

                             if(passwordChecked && (changePassword.length < 6 || ( !changePassword.match(/\d/) || !changePassword.match(/[!@#$%^&*]/) ))) {

                                if(changePassword.length < 6)
                                    $("#modalMessage").text("Password must be at least 6 characters long").addClass("alert alert-danger").show();
                                 else if(!changePassword.match(/\d/) || !changePassword.match(/[!@#$%^&*]/))
                                    $("#modalMessage").text("Password must contain at least one number and one special character").addClass("alert alert-danger").show();

                                 setTimeout(function() {
                                      $("#modalMessage").fadeOut('slow');
                                 }, 2000);

                                 return;

                             }

                             // ajax
                             $.ajax({
                               url: "update",
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
                               },
                               error: function(error) {
                                 console.log(error);
                               }
                             });

                          });
});