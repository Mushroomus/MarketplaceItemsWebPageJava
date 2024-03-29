      refreshTable(0);
      var filter = false;
      var searchFilter = false;
      var currentPage = 0;

   function clearFilters() {
        filter = false;
        searchFilter = false;
        currentPage = 0;
        refreshTable(0);
    }

    function stringToCorrectDate(stringDate) {
        let dateString = stringDate;
        let dateComponents = dateString.split(".");
        let day = dateComponents[0];
        let month = dateComponents[1];
        let yearAndTime = dateComponents[2].split(", ");
        let year = yearAndTime[0];
        let time = yearAndTime[1];

        return new Date(year, month - 1, day, time.split(":")[0], time.split(":")[1]);
    }

    function filterButton() {
        filter = true;
        refreshTable(0);
    }


    function searchButton() {
        searchFilter = true;
        refreshTable(0);
    }

    function createUrl(page) {

        if(page == null || page == "") {
            currentPage = 0;
            page = 0;
        }
        currentPage = page;

        let url = "/users";
        url += "?page=" + page;

        if(searchFilter == true) {
            var search = $("#searchInput").val();

            if(searchFilter != null)
                url += "&search=" + search;
        }

        if(filter == true) {

            var role = $("#filterRoleDropdown").val();

            var timestampStartDate = null;
            var timestampEndDate = null;

            if($("#datetimepickerInputStartDate").val() != "" && $("#datetimepickerInputStartDate").val() != null) {
                var startDate = stringToCorrectDate( $("#datetimepickerInputStartDate").val() );
                timestampStartDate = startDate.getTime();
            }

            if($("#datetimepickerInputEndDate").val() != "" && $("#datetimepickerInputStartDate").val() != null) {
                var endDate = stringToCorrectDate( $("#datetimepickerInputEndDate").val() );
                timestampEndDate = endDate.getTime();
            }

            if (role != null && role != "")
                url += "&role=" + role;
            if (startDate != null && startDate != "")
                url += "&startDate=" + timestampStartDate;
            if (endDate != null && endDate != "")
                url += "&endDate=" + timestampEndDate;
        }

        return url;
    }

    function checkAllInputsValid() {
      var isValid = true;
      $('#modalBody input').each(function() {
        if ($(this).hasClass('is-invalid')) {
          isValid = false;
          return false;
        }
      });

      return isValid;
    }



    function refreshTable(page) {

               $.ajax({
                 url: createUrl(page),
                 type: "GET",
                 dataType: 'json',
                 success: function(data) {
                 //console.log(data);

                if(data.page.totalPages == 0) {
                    $("table tbody").empty();
                    $("table").hide();
                    $(".pagination").empty();
                    $("#noUsers").show();
                }
                else {

                     if(currentPage == data.page.totalPages)
                        refreshTable(--currentPage);

                    $("table").show();
                    $("#noUsers").hide();
                    if(data._embedded && data._embedded.userList)
                        updateTable(data._embedded.userList);

                    updatePagination(data.page)
                }

               }
               });
             }

             function updateTable(data) {
               // Clear the current table
               $("table tbody").empty();

                   data.forEach(function(user) {
                     let first_date = moment(user.date).format('DD.MM.YYYY HH:MM');
                     let newRow = "<tr>" +
                       "<td>" + user.username + "</td>" +
                       "<td>" + user.role + "</td>" +
                       "<td>" + first_date + "</td>" +
                       "<td>" +
                       "<button type='button' class='btn btn-primary' data-bs-toggle='modal' data-bs-target='#editModal'" +
                       "data-userId='" + user.id + "' data-username='" + user.username + "' data-role='" + user.role + "' data-password='" + user.password + "'>" +
                         "<i class='fas fa-pencil-alt'></i>" +
                       "</button>" +
                         "<button type='submit' class='btn btn-danger mx-2' data-bs-toggle='modal' data-bs-target='#deleteModal'" +
                         "data-userId='" + user.id + "' data-username='" + user.username + "'>" +
                         "<i class='fas fa-trash'></i>" +
                       "</button>" +
                       "</td>" +
                       "</tr>";
                     $("table tbody").append(newRow);
                   });
             }

 $(document).ready(function() {

         $("#toggleButton").click(function() {
             $('.sidebar-container').toggleClass("d-md-block");
             if ($('.sidebar-container').hasClass("d-md-block")) {
               $("#toggleButton svg").removeClass("fa-arrow-right").addClass("fa-arrow-left");
               $('.sidebar-container').css("animation-name", "fadeInLeft");
             } else {
               $("#toggleButton svg").removeClass("fa-arrow-left").addClass("fa-arrow-right");
               $('.sidebar-container').css("animation-name", "fadeOutRight");
             }
          });

     $('#searchInput').val('');

     window.datetimepickerStartDate = $('#datetimepickerStartDate').tempusDominus({
         });

          window.datetimepickerEndDate = $('#datetimepickerEndDate').tempusDominus({
         });
});
