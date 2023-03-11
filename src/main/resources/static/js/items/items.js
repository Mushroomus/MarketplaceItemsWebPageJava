    refreshTable(0);
    var filter = false;
    var searchFilter = false;
    var currentPage = 0;


    function clearAllCheckboxes() {
        // Select all the checkboxes with the name "classes", "qualities", and "types"
        var checkboxes = document.querySelectorAll('input[type="checkbox"][name="classes"], input[type="checkbox"][name="qualities"], input[type="checkbox"][name="types"]');
        // Loop through all the checkboxes and set their "checked" property to false
        for (var i = 0; i < checkboxes.length; i++) {
          checkboxes[i].checked = false;
        }
      }

    function clearFilters() {
        filter = false;
        searchFilter = false;
        currentPage = 0;
        refreshTable(0);

        $('#searchInput').val('');
        $('#craftable').val('');
        clearAllCheckboxes();
    }

    function filterButton() {
        filter = true;
        refreshTable(0);
        $('html, body').animate({scrollTop: 0}, 800);
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

        let url = "list-refresh";
        url += "?page=" + page;

        if(searchFilter == true) {
            var search = $("#searchInput").val();

            if(searchFilter != null)
                url += "&search=" + search;
        }

        if(filter == true) {
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
        console.log(url);
        return url;
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
                    $("#noItems").show();
                }
                else {

                    if(currentPage == data.page.totalPages)
                        refreshTable(--currentPage);

                    $("table").show();
                    $("#noItems").hide();
                    if(data._embedded && data._embedded.itemList) {

                        if (window.location.href.indexOf('/items/list-admin') !== -1)
                            updateTableAdmin(data._embedded.itemList);
                        else
                             updateTableUser(data._embedded.itemList);
                    }

                    updatePagination(data.page)
                }
            }
        });
    }


    function updateTableAdmin(data) {
        // Clear the current table
        $("table tbody").empty();

        data.forEach(function(item) {
            let newRow = "<tr>" +
                "<td><img src='" + item.image + "'></td>" +
                "<td>" + item.sku + "</td>" +
                "<td>" + item.name + "</td>" +
                "<td>"  + item.marketplacePrice + "</td>" +
                "<td>" + item.craftable + "</td>" +
                "<td>" + item.classItem + "</td>" +
                "<td>" + item.quality + "</td>" +
                "<td>" + item.type + "</td>" +

                "<td>" +

                "<button type='button' class='btn btn-secondary' data-bs-toggle='modal' data-bs-target='#editPriceModal'" +
                "data-itemName='" + item.name + "' data-itemSku='" + item.sku + "' data-mpPrice='" + item.marketplacePrice + "'>" +
                "<i class='fas fa-coins'></i>" +
                "</button>" +

                "<button type='button' class='btn btn-primary  mx-2' data-bs-toggle='modal' data-bs-target='#editModal'" +
                "data-itemSku='" + item.sku + "' data-itemName='" + item.name + "' data-itemCraftable='" + item.craftable + "' data-itemClass='" + item.classItem
                + "' data-itemQuality='" + item.quality + "' data-itemType='" + item.type + "' data-itemImage='" + item.image
                + "'>" +
                "<i class='fas fa-pencil-alt'></i>" +
                "</button>" +

                "<button type='submit' class='btn btn-danger' data-bs-toggle='modal' data-bs-target='#deleteModal'" +
                "data-itemSku='" + item.sku + "' data-itemName='" + item.name + "'>" +
                "<i class='fas fa-trash'></i>" +
                "</button>" +

                "</td>" +

                "</tr>";
            $("table tbody").append(newRow);
        });
    }

    function updateTableUser(data) {
            // Clear the current table
            $("table tbody").empty();

            data.forEach(function(item) {
                let newRow = "<tr>" +
                    "<td><img src='" + item.image + "'></td>" +
                    "<td>" + item.sku + "</td>" +
                    "<td>" + item.name + "</td>" +
                    "<td>"  + item.marketplacePrice + "</td>" +
                    "<td>" + item.craftable + "</td>" +
                    "<td>" + item.classItem + "</td>" +
                    "<td>" + item.quality + "</td>" +
                    "<td>" + item.type + "</td>" +

                    "<td>" +

                    "<button type='submit' class='btn btn-secondary mx-2' data-bs-toggle='modal' data-bs-target='#requestEditPriceModal'" +
                    "data-itemSku='" + item.sku + "' data-itemPrice='" + item.marketplacePrice +  "' data-itemName='" + item.name + "'>" +
                    "<i class='fas fa-dollar-sign'></i>" +
                    "</button>" +

                     "<button type='button' class='btn btn-primary' data-bs-toggle='modal' data-bs-target='#requestEditModal'" +
                    "data-itemSku='" + item.sku + "' data-itemName='" + item.name + "' data-itemCraftable='" + item.craftable + "' data-itemClass='" + item.classItem
                    + "' data-itemQuality='" + item.quality + "' data-itemType='" + item.type + "' data-itemImage='" + item.image
                    + "'>" +
                    "<i class='fas fa-pencil-alt'></i>" +
                    "</button>" +

                    "<button type='submit' class='btn btn-danger mx-2' data-bs-toggle='modal' data-bs-target='#requestDeleteModal'" +
                    "data-itemSku='" + item.sku + "' data-itemName='" + item.name + "'>" +
                    "<i class='fas fa-trash'></i>" +
                    "</button>" +

                    "</td>" +

                    "</tr>";
                $("table tbody").append(newRow);
            });
        }


    function updatePagination(data) {
        var pagination = $(".pagination");
        pagination.empty();

        if(data.totalPages != 0) {

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

            var lastButton = "<li class='page-item " + (data.number === data.totalPages - 1 ? "disabled" : "") + "'>" +
                "<a class='page-link' href='#' onclick='refreshTable(" + (data.totalPages - 1) + ")'>" +
                "<span aria-hidden='true'>&raquo;</span>" +
                "<span class='sr-only'>Last</span>" +
                "</a>" +
                "</li>";
            pagination.append(lastButton);
        }
    }
