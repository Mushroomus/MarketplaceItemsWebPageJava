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

            if(craftable != "" && craftable != "Any")
                url += "&craftable=" + craftable;

            if(selectedClasses != "")
                url += "&classes=" + selectedClasses;

            if(selectedQualities != "")
                url += "&qualities=" + selectedQualities;

            if(selectedTypes != "")
                url += "&types=" + selectedTypes;
        }

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
                    $("#noUsers").show();
                }
                else {

                    if(currentPage == data.page.totalPages)
                        refreshTable(--currentPage);

                    $("table").show();
                    //$("#noUsers").hide();
                    if(data._embedded && data._embedded.itemList)
                        updateTable(data._embedded.itemList);

                   // updatePagination(data.page)
                }
            }
        });
    }


    function updateTable(data) {
        // Clear the current table
        $("table tbody").empty();

        data.forEach(function(item) {
            let newRow = "<tr>" +
                "<td>" + item.image + "</td>" +
                "<td>" + item.sku + "</td>" +
                "<td>" + item.name + "</td>" +
                "<td>" + item.craftable + "</td>" +
                "<td>" + item.classItem + "</td>" +
                "<td>" + item.quality + "</td>" +
                "<td>" + item.type + "</td>" +

                "<td>" +

                "<button type='submit' class='btn btn-danger mx-2' data-bs-toggle='modal' data-bs-target='#deleteModal'" +
                "data-itemSku='" + item.sku + "' data-itemName='" + item.name + "'>" +
                "<i class='fas fa-trash'></i>" +
                "</button>" +

                "<button type='button' class='btn btn-primary' data-bs-toggle='modal' data-bs-target='#editModal'" +
                "data-itemSku='" + item.sku + "' data-itemName='" + item.name + "' data-itemCraftable='" + item.craftable + "' data-itemClass='" + item.itemClass
                + "' data-itemQuality='" + item.quality + "' data-itemType='" + item.type + "' data-itemImage='" + item.image
                + "'>" +
                "<i class='fas fa-pencil-alt'></i>" +
                "</button>" +

                "</td>" +

                "</tr>";
            $("table tbody").append(newRow);
        });
    }

