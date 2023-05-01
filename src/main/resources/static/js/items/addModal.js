function setStartValues() {
    $("#itemSku").val('');
    $("#itemName").val('');
    $("#itemCraftable").val('true');
    $("#itemClass").val('Multi-class');
    $("#itemQuality").val('Genuine');
    $("#itemType").val('Cosmetics');
    $("#itemMarketplacePrice").val('0.00');
}


$(document).ready(function() {


    var addForm = $("#addForm");
    var submitButton = $("#addSubmitButton");

    $("#addModal").on('hidden.bs.modal', function (event) {
            setStartValues();
      });

    addForm.on('submit', function(event) {

        event.preventDefault();

        if (addForm[0].checkValidity()) {

            var sku = $("#itemSku").val();
            var name = $("#itemName").val();
            var craftable = $("#itemCraftable").val();
            var itemClass = $("#itemClass").val();
            var quality = $("#itemQuality").val();
            var type = $("#itemType").val();
            var marketplacePrice = $("#itemMarketplacePrice").val();

            var modalAddItemMessage = $("#modalAddItemMessage");

            $.ajax({
                url: "/items",
                type: "POST",
                contentType: "application/json",
                data: JSON.stringify({
                    sku: sku,
                    name: name,
                    marketplacePrice: marketplacePrice,
                    craftable: craftable,
                    classItem: itemClass,
                    quality: quality,
                    type: type,
                    image: ""
                }),
                dataType: 'json',
                success: function(response, status, xhr) {
                    if (xhr.status == 200) {
                        setAlert(modalAddItemMessage, "Item added", true);
                        refreshTable(currentPage);
                        setStartValues();
                    }
                    else
                        setAlert(modalAddItemMessage, "Something went wrong", false);
                },
                error: function(xhr, status, error) {
                    setAlert(modalAddItemMessage, "Something went wrong", false);
                }
            });

            timeout(modalAddItemMessage);
        }
    });

   addForm.on('input', function(event) {

     if (addForm[0].checkValidity())
        submitButton.prop('disabled', false);
     else
        submitButton.prop('disabled', true);
   });
});