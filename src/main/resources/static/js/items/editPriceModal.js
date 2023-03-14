$(document).ready(function() {
    $('#editPriceModal').on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget);

        var itemSku = button.data('itemsku');
        var itemName = button.data('itemname');
        var itemPrice = button.data('mpprice');

        var modal = $(this);

        modal.find('.modal-body #modalHeader').text(itemName);
        modal.find('.modal-body #modalSku').val(itemSku);
        modal.find('.modal-body #modalMarketplacePrice').val(itemPrice);
    })

    $("#editModalPriceSubmit").click(function () {

        var itemSku = $("#modalSku").val();
        var itemPrice = $('#modalMarketplacePrice').val();

        let alertMessage = parent.$('#alertMessage');

        $.ajax({
            url: "updatePrice",
            type: "PUT",
            contentType: "application/json",
            data: JSON.stringify({
                sku: itemSku,
                mpPrice: itemPrice
            }),
            success: function (response, status, xhr) {

                if (xhr.status == 200) {
                    $('#editPriceModal').modal('hide');
                    refreshTable(currentPage);
                    setAlert(alertMessage, "Price updated", true);
                }
                else
                    setAlert(alertMessage, "Something went wrong", false);
            },
            error: function (xhr, status, error) {
                setAlert(alertMessage, "Something went wrong", false);
            }
        });
        timeout(alertMessage);
    });
});