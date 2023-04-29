$(document).ready(function() {
    $('#requestEditPriceModal').on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget);

        var itemSku = button.data('itemsku');
        var itemName = button.data('itemname');
        var itemPrice = button.data('itemprice');

        var modal = $(this);

        modal.find('.modal-body #modalHeader').text(itemName);
        modal.find('.modal-body #modalSku').val(itemSku);
        modal.find('.modal-body #modalMarketplacePrice').val(itemPrice);
    })

    $("#requestEditModalPriceSubmit").click(function () {

        var itemSku = $("#modalSku").val();
        var itemPrice = $('#modalMarketplacePrice').val();

        var alertMessage = parent.$('#alertMessageUser');
        console.log("run");

        $.ajax({
            url: "/messages?sku=" + itemSku,
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify({
                messageType: "updatePrice",
                marketplacePrice: itemPrice
            }),
            dataType: 'json',
            success: function (response, status, xhr) {

                $('#requestEditPriceModal').modal('hide');

                if (xhr.status == 200)
                    setAlert(alertMessage, "Request sent", true);
                else
                    setAlert(alertMessage, "Something went wrong", false);
            },
            error: function (xhr, status, error) {

                $('#requestEditPriceModal').modal('hide');
                setAlert(alertMessage, "Something went wrong", false);
            }
        });
        timeout(alertMessage);
    });
});