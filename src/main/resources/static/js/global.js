function timeout(alert) {
    setTimeout(function() {
     $(alert).fadeOut('slow');
    }, 2000);
}

function setAlert(alert, text, success) {
    if(success)
        alert.text(text).removeClass("alert alert-danger").addClass("alert alert-success").show();
    else
        alert.text(text).removeClass("alert alert-danger").addClass("alert alert-danger").show();
}

 $(document).ready(function() {
             $.ajax({
                 type: "GET",
                 url: "/sales/graphs/check-sales",
                 success: function(response) {
                     if(response.salesEmpty == true) {
                         console.log("true");
                         $('#graphsLink').fadeIn();
                     }
                     else {
                         console.log("false");
                         $('#graphsLink').fadeOut();
                     }
                 },
                 error: function(xhr) {
                     console.log(xhr);
                 }
             });

           $("#toggleButton").click(function() {
            $('.sidebar-container').toggleClass("d-md-block");
            if ($('.sidebar-container').hasClass("d-md-block")) {
              $("#toggleButton i").removeClass("fa-arrow-right").addClass("fa-arrow-left");
              $('.sidebar-container').css("animation-name", "fadeInLeft");
            } else {
              $("#toggleButton i").removeClass("fa-arrow-left").addClass("fa-arrow-right");
              $('.sidebar-container').css("animation-name", "fadeOutRight");
            }
          });
 });

 function updatePagination(data) {
     var pagination = $(".pagination");
     pagination.empty();

     if (data.totalPages != 0) {
         var firstButton = "<li class='page-item " + (data.number === 0 ? "disabled" : "") + "'>" +
             "<a class='page-link' href='#' onclick='refreshTable(0)'>" +
             "<span aria-hidden='true'>&laquo;</span>" +
             "<span class='sr-only'>First</span>" +
             "</a>" +
             "</li>";
         pagination.append(firstButton);

         var previousButton = "<li class='page-item " + (data.number === 0 ? "disabled" : "") + "'>" +
             "<a class='page-link' href='#' " + (data.number === 0 ? "" : "onclick='refreshTable(" + (data.number - 1) + ")'") + ">" +
             "<span aria-hidden='true'>&lsaquo;</span>" +
             "<span class='sr-only'>Previous</span>" +
             "</a>" +
             "</li>";
         pagination.append(previousButton);

         var visiblePages = 5;
         var startPage = Math.max(0, data.number - Math.floor(visiblePages / 2));
         var endPage = Math.min(data.totalPages - 1, startPage + visiblePages - 1);
         if (endPage - startPage < visiblePages - 1) {
             startPage = Math.max(0, endPage - visiblePages + 1);
         }

         if (startPage > 0) {
             pagination.append("<li class='page-item disabled'><a class='page-link'>...</a></li>");
         }

         for (var i = startPage; i <= endPage; i++) {
             var className = i === data.number ? "page-item active" : "page-item";
             var pageLink = "<li class='" + className + "'><a class='page-link' href='#' onclick='refreshTable(" + i + ")'>" + (i + 1) + "</a></li>";
             pagination.append(pageLink);
         }

         if (endPage < data.totalPages - 1) {
             pagination.append("<li class='page-item disabled'><a class='page-link'>...</a></li>");
         }

         var nextButton = "<li class='page-item " + (data.number === data.totalPages - 1 ? "disabled" : "") + "'>" +
             "<a class='page-link' href='#' " + (data.number === data.totalPages - 1 ? "" : "onclick='refreshTable(" + (data.number + 1) + ")'") + ">" +
             "<span aria-hidden='true'>&rsaquo;</span>" +
             "<span class='sr-only'>Next</span>" +
             "</a>" +
             "</li>";
         pagination.append(nextButton);

         var lastButton = "<li class='page-item " + (data.number === data.totalPages - 1 ? "disabled" : "") + "'>" +
                              "<a class='page-link' href='#' onclick='refreshTable(" + (data.totalPages - 1) + ")'>" +
                              "<span aria-hidden='true'>&raquo;</span>" +
                              "<span class='sr-only'>Last</span>" +
                              "</a>" +
                              "</li>";
                            pagination.append(lastButton);
     }
   };