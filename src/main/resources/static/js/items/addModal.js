function setStartValues() {
    $("#itemSku").val('');
    $("#itemName").val('');
    $("#itemCraftable").val('true');
    $("#itemClass").val('Multi-class');
    $("#itemQuality").val('Genuine');
    $("#itemType").val('Cosmetics');
    $("#itemImage").val('');
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
            var image = $("#itemImage").val();
            var marketplacePrice = $("#itemMarketplacePrice").val();

            $.ajax({
                url: "add",
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
                    image: image
                }),
                dataType: 'json',
                success: function(response, status, xhr) {
                    if (xhr.status == 200) {
                        $("#modalAddItemMessage").text("Item was added").removeClass("alert alert-danger").addClass("alert alert-success").show();
                        refreshTable(currentPage);
                        setStartValues();
                    }
                    else if (xhr.status == 400)
                        $("#modalAddItemMessage").text("Something went wrong").removeClass("alert alert-danger").addClass("alert alert-danger").show();
                    else
                        $("#modalAddItemMessage").text("Something went wrong").removeClass("alert alert-danger").addClass("alert alert-danger").show();
                },
                error: function(xhr, status, error) {
                    $("#modalAddItemMessage").text("Something went wrong").removeClass("alert alert-danger").addClass("alert alert-danger").show();
                }
            });

            setTimeout(function() {
                $("#modalAddItemMessage").fadeOut('slow');
            }, 5000);

        }
    });

   addForm.on('input', function(event) {

     if (addForm[0].checkValidity())
        submitButton.prop('disabled', false);
     else
        submitButton.prop('disabled', true);
   });
});