
refreshTable(0);
var currentPage = 0;
var filter = false;

function filterButton() {
    filter = true;
    console.log(createUrl(currentPage));
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

function createUrl(page) {

    if(page == null || page == "") {
        currentPage = 0;
        page = 0;
    }
    currentPage = page;

    let url = "list-refresh";
    url += "?page=" + page;

    if(filter == true) {

        if($('#switchUseSale').prop('checked')) {
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

            if (startDate != null && startDate != "")
                url += "&startDate=" + timestampStartDate;
            if (endDate != null && endDate != "")
                url += "&endDate=" + timestampEndDate;

            var minPrice = $('#min-price').val();
            var maxPrice = $('#max-price').val();

            if(minPrice != null && minPrice !== 0 )
                url += "&minPrice=" + minPrice;

            if(maxPrice != null && maxPrice !== 0 )
                url += "&maxPrice=" + maxPrice;
        }

        if($('#switchUseItem').prop('checked')) {
            var craftable = $('#filterCraftableDropdown').val();

            var selectedClasses = "";
            $('input[name="classes"]:checked').each(function() {
                selectedClasses += $(this).val() + ",";
            });

            selectedClasses = selectedClasses.slice(0, -1);


            var selectedQualities = "";
            $('input[name="qualities"]:checked').each(function() {
                selectedQualities += $(this).val() + ",";
            });

            selectedQualities = selectedQualities.slice(0, -1);

            var selectedTypes = "";
            $('input[name="types"]:checked').each(function() {
                selectedTypes += $(this).val() + ",";
            });

            selectedTypes = selectedTypes.slice(0, -1);

            if(craftable != "Any")
                url += "&craftable=" + craftable;

            if(selectedClasses != "")
                url += "&classes=" + selectedClasses;

            if(selectedQualities != "")
                url += "&qualities=" + selectedQualities;

            if(selectedTypes != "")
                url += "&types=" + selectedTypes;
        }
    }
    return url;
}

    function refreshTable(page) {

               $.ajax({
                 url: createUrl(page),
                 type: "GET",
                 dataType: 'json',
                 success: function(data) {

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
                         newRow += "<td><button class='btn btn-primary' data-bs-toggle='modal' data-bs-target='#itemInformationsModal'" +
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
     $('#excelSummary').on('click', function() {
         $.ajax({
             url: 'excelSummary',
             type: 'GET',
             xhrFields: {
                 responseType: 'blob'
             },
             success: function(data) {
                 var link = document.createElement('a');
                link.href = window.URL.createObjectURL(data);
                link.download = 'data.xlsx';

                // Trigger the download
                document.body.appendChild(link);
                link.click();

                // Clean up the link element
                document.body.removeChild(link);
             }
         });
     });


     window.datetimepickerStartDate = $('#datetimepickerStartDate').tempusDominus({
     });

     window.datetimepickerEndDate = $('#datetimepickerEndDate').tempusDominus({
     });

});
