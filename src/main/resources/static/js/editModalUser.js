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
                var username = $('#modalUsername').val();
                var password = $("#modalPassword").val();
                var repeatPassword = $("#modalPasswordRepeat").val();
                var passwordChecked = $("#changePasswordCheckbox").is(":checked");

                if (username == "" || ( passwordChecked && ( password == "" || password != repeatPassword)) ){
                    event.preventDefault();

                    if(username == "")
                        $("#modalMessage").text("Username is empty").addClass("alert alert-danger").show();
                    else if(password == "")
                        $("#modalMessage").text("Password is empty").addClass("alert alert-danger").show();
                    else
                        $("#modalMessage").text("Passwords do not match").addClass("alert alert-danger").show();

                    setTimeout(function() {
                            $("#modalMessage").fadeOut('slow');
                         }, 2000);
                }

                if(password.length < 6) {
                    event.preventDefault();
                    $("#modalEditMessage").text("Password must be at least 6 characters long").addClass("alert alert-danger").show();
                }
                else if(!password.match(/\d/) || !password.match(/[!@#$%^&*]/)) {
                    event.preventDefault();
                    $("#modalEditMessage").text("Password must contain at least one number and one special character").addClass("alert alert-danger").show();
                }

                setTimeout(function() {
                        $("#modalEditMessage").fadeOut('slow');
                    }, 2000);

             });
        });

