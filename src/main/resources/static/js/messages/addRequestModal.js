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

    $("#requestAddModal").on('hidden.bs.modal', function (event) {
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

            var alertMessage = parent.$('#alertMessageUser');

            $.ajax({
                url: "/messages/create",
                type: "POST",
                contentType: "application/json",
                data: JSON.stringify({
                    messageType: "add",
                    sku: sku,
                    name: name,
                    marketplacePrice: marketplacePrice,
                    craftable: craftable,
                    itemClass: itemClass,
                    quality: quality,
                    type: type,
                    image: image
                }),
                dataType: 'json',
                success: function (response, status, xhr) {

                    $('#requestAddModal').modal('hide');

                    if (xhr.status == 200)
                        alertMessage.text('Request was sent').addClass('alert alert-success').show();
                    else
                        alertMessage.text('Something went wrong').addClass("alert alert-danger").show();


                    setTimeout(function() {
                        alertMessage.fadeOut('slow');
                    }, 2000);
                },
                error: function (xhr, status, error) {

                    $('#requestAddModal').modal('hide');
                    alertMessage.text('Something went wrong').addClass("alert alert-danger").show();

                    setTimeout(function() {
                        alertMessage.fadeOut('slow');
                    }, 2000);
                }
            });
        }
    });

    addForm.on('input', function(event) {

        if (addForm[0].checkValidity())
            submitButton.prop('disabled', false);
        else
            submitButton.prop('disabled', true);
    });
});