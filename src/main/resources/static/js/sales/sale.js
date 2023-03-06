
refreshTable(0);
var currentPage = 0;

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


    function refreshTable(page) {

               $.ajax({
                 url: "list-refresh?page=" + page,
                 type: "GET",
                 dataType: 'json',
                 success: function(data) {
                 console.log(data);


                if(data.page.totalPages == 0) {
                    $("table tbody").empty();
                    $("table").hide();
                    $(".pagination").empty();
                    $("#noSales").show();
                }
                else {

                     if(currentPage == data.page.totalPages)
                        refreshTable(--currentPage);
                    $("table").show();
                    $("#noSales").hide();

                    if(data._embedded && data._embedded.saleList)
                        updateTable(data._embedded.saleList);

                    updatePagination(data.page)
                }

               }
               });
          }

             function updateTable(data) {
               // Clear the current table
               $("table tbody").empty();

                   data.forEach(function(sale) {
                     let sale_date = moment(sale.date).format('DD.MM.YYYY HH:MM');
                     let newRow = "<tr>" +
                       "<td>" + sale.orderId + "</td>" +
                       "<td>" + sale_date + "</td>" +
                       "<td>" + sale.price + "</td>" +
                       "<td>" + sale.net + "</td>" +
                       "<td>" + sale.fee + "</td>";

                       if (sale.item != null) {
                         newRow += "<td><button class='btn btn-primary' " +
                                  "data-sku='" + sale.item.sku + "' " +
                                  "data-name='" + sale.item.name + "' " +
                                  "data-marketplacePrice='" + sale.item.marketplacePrice + "' " +
                                  "data-craftable='" + sale.item.craftable + "' " +
                                  "data-classItem='" + sale.item.classItem + "' " +
                                  "data-quality='" + sale.item.quality + "' " +
                                  "data-type='" + sale.item.type + "'>View Item</button></td>";
                       } else {
                         newRow += "<td><button class='btn btn-danger' data-bs-toggle='tooltip' data-bs-placement='top' title='Item is not in database'><i class='fas fa-exclamation-triangle'></i></button></td>";
                         $(function () {
                              $('[data-bs-toggle="tooltip"]').tooltip()
                          })
                       }

                       newRow += "</tr>";
                     $("table tbody").append(newRow);

                   });
             }

 $(document).ready(function() {
});
