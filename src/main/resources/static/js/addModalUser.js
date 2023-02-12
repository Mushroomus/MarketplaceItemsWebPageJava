    function filterButton() {
        var role = $("#filterRoleDropdown").val();
        var startDate = $("filterStartDatePicker").val();
        var endDate = $("filterEndDatePicker").val();

        let urlParams = new URLSearchParams(window.location.search);
        let search = urlParams.get("search");
        let page = urlParams.get("page");

        if(page == null || page == "")
            page = 0;

            let url = "list-refresh";
            url += "?page=" + page;

            if(search != null)
                url += "&search=" + search;

            url += "&role=" + role;
            url += "&startDate=" + startDate;
            url += "&endDate=" + endDate;

             $.ajax({
                  url: url,
                  type: "GET",
                  dataType: 'json',
                  success: function(data) {
                  updateTable(data._embedded.userList);
                  updatePagination(data.page)
                }
                });
    }


    function searchButton() {
         var search = $("#searchInput").val();

         let urlParams = new URLSearchParams(window.location.search);
         var page = urlParams.get("page");
         let role = urlParams.get("role");
         let startDate = urlParams.get("startDate");
         let endDate = urlParams.get("endDate");

         if(page == null || page == "")
            page = 0;

             let url = "list-refresh";
             url += "?page=" + page;
                if (search != null && search != "") {
                    url += "&search=" + search;
                }
                if (role != null && role != "") {
                    url += "&role=" + role;
                }
                if (startDate != null && startDate != "") {
                    url += "&startDate=" + startDate;
                }
                if (endDate != null && endDate != "") {
                    url += "&endDate=" + endDate;
                }

         $.ajax({
              url: url,
              type: "GET",
              dataType: 'json',
              success: function(data) {
              //console.log(data);
              updateTable(data._embedded.userList);
              updatePagination(data.page)
            }
            });

    }


    function refreshTable(page) {

                let urlParams = new URLSearchParams(window.location.search);

                  var page = urlParams.get("page");
                  let role = urlParams.get("role");
                  let startDate = urlParams.get("startDate");
                  let endDate = urlParams.get("endDate");

               $.ajax({
                 url: "list-refresh?page=" + page + "&search=" + search,
                 type: "GET",
                 dataType: 'json',
                 success: function(data) {
                 console.log(data);
                 updateTable(data._embedded.userList);
                 updatePagination(data.page)
               }
               });
             }


             function updateTable(data) {
               // Clear the current table
               $("table tbody").empty();

               // Loop through the list of users and add them to the table
               data.forEach(function(user) {
                 let first_date = moment(user.date).format('DD.MM.YYYY HH:MM');
                 let newRow = "<tr>" +
                   "<td>" + user.username + "</td>" +
                   "<td>" + user.role + "</td>" +
                   "<td>" + first_date + "</td>" +
                   "<td>" +
                   "<button type='submit' class='btn btn-danger' data-bs-toggle='modal' data-bs-target='#deleteModal'" +
                   "data-userId='" + user.id + "' data-username='" + user.username + "'>" +
                   "<i class='fas fa-trash'></i>" +
                   "</button>" +
                   "<button type='button' class='btn btn-primary' data-bs-toggle='modal' data-bs-target='#editModal'" +
                   "data-userId='" + user.id + "' data-username='" + user.username + "' data-role='" + user.role + "' data-password='" + user.password + "'>" +
                   "<i class='fas fa-pencil-alt'></i>" +
                   "</button>" +
                   "</td>" +
                   "</tr>";
                 $("table tbody").append(newRow);
               });
             }

             function updatePagination(data) {
                 var pagination = $(".pagination");
                 pagination.empty();

                 var previousButton = "<li class='page-item " + (data.number === 0 ? "disabled" : "") + "'>" +
                     "<a class='page-link' href='#' " + (data.number === 0 ? "" : "onclick='refreshTable(" + (data.number - 1) + ")'") + ">" +
                     "<span aria-hidden='true'>&lsaquo;</span>" +
                     "<span class='sr-only'>Previous</span>" +
                     "</a>" +
                     "</li>";
                 pagination.append(previousButton);

                 for (var i = 0; i < data.totalPages; i++) {
                     var className = i === data.number ? "page-item active" : "page-item";
                     var pageLink = "<li class='" + className + "'><a class='page-link' href='#' onclick='refreshTable(" + i + ")'>" + (i + 1) + "</a></li>";
                     pagination.append(pageLink);
                 }

                 var nextButton = "<li class='page-item " + (data.number === data.totalPages - 1 ? "disabled" : "") + "'>" +
                     "<a class='page-link' href='#' " + (data.number === data.totalPages - 1 ? "" : "onclick='refreshTable(" + (data.number + 1) + ")'") + ">" +
                     "<span aria-hidden='true'>&rsaquo;</span>" +
                     "<span class='sr-only'>Next</span>" +
                     "</a>" +
                     "</li>";
                 pagination.append(nextButton);
             }


$(document).ready(function() {

 $("#addModalSubmit").click(function() {

                event.preventDefault();

                var username = $("#userUsername").val();
                var password = $("#userPassword").val();
                var repeatPassword = $("#userPasswordRepeat").val();
                var role = $("#userRole").val();

/*
                if (username == "" || password == "" || password != repeatPassword ){

                    if(username == "")
                        $("#modalEditMessage").text("Username is empty").addClass("alert alert-danger").show();
                    else if(password == "")
                        $("#modalEditMessage").text("Password is empty").addClass("alert alert-danger").show();
                    else
                        $("#modalEditMessage").text("Passwords do not match").addClass("alert alert-danger").show();

                    setTimeout(function() {
                            $("#modalEditMessage").fadeOut('slow');
                         }, 2000);
                    return;
                }

                if(password.length < 6 || ( !password.match(/\d/) || !password.match(/[!@#$%^&*]/) )) {
                    if(password.length < 6)
                        $("#modalEditMessage").text("Password must be at least 6 characters long").addClass("alert alert-danger").show();
                    else if(!password.match(/\d/) || !password.match(/[!@#$%^&*]/))
                        $("#modalEditMessage").text("Password must contain at least one number and one special character").addClass("alert alert-danger").show();

                    setTimeout(function() {
                        $("#modalEditMessage").fadeOut('slow');
                    }, 2000);
                    return;
                }
*/
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
                             var currentPage = parseInt(window.location.search.split("=")[1]);
                             if(currentPage == null || currentPage == "")
                                currentPage = 0;

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