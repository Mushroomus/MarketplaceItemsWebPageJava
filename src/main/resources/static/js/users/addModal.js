$(document).ready(function() {
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
               });

               $('#userUsername').trigger('input');
               $('#userPassword').trigger('input');

             $("#addSubmit").click(function(event) {

                var password = $("#userPassword").val();
                var repeatPassword = $("#userPasswordRepeat").val();

                var isValid = checkAllInputsValid();
                console.log(isValid);

                if(!isValid)
                    return;

                var username = $("#userUsername").val();
                var password = $("#userPassword").val();
                var repeatPassword = $("#userPasswordRepeat").val();
                var role = $("#userRole").val();

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

                             $("#modalEditMessage").text("added").addClass("alert alert-success").show();
                             refreshTable(currentPage);

                        }
                        else if (xhr.status == 400)
                            $("#modalEditMessage").text("added").addClass("alert alert-danger").show();
                        else
                            $("#modalEditMessage").text("added").addClass("alert alert-danger").show();

                        },
                         error: function(xhr, status, error) {
                            console.error(error);
                            console.error(xhr.responseText);
                         }
                    });

                     setTimeout(function() {
                        $("#modalEditMessage").fadeOut('slow');
                     }, 2000);

             });
});