$(document).ready(function() {

 $("#addModalSubmit").click(function() {
                var username = $("#userUsername").val();
                var password = $("#userPassword").val();
                var repeatPassword = $("#userPasswordRepeat").val();


                if (username == "" || password == "" || password != repeatPassword ){
                    event.preventDefault();

                    if(username == "")
                        $("#modalEditMessage").text("Username is empty").addClass("alert alert-danger").show();
                    else if(password == "")
                        $("#modalEditMessage").text("Password is empty").addClass("alert alert-danger").show();
                    else
                        $("#modalEditMessage").text("Passwords do not match").addClass("alert alert-danger").show();

                    setTimeout(function() {
                            $("#modalEditMessage").fadeOut('slow');
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