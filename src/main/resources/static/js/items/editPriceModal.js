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
        console.log(itemSku + itemPrice);

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
                    alertMessage.text('Price was updated').addClass('alert alert-success').show();
                } else
                    alertMessage.text('Something went wrong').addClass("alert alert-danger").show();

                setTimeout(function() {
                    alertMessage.fadeOut('slow');
                }, 2000);
            },
            error: function (xhr, status, error) {
                alertMessage.text('Something went wrong').addClass("alert alert-danger").show();

                setTimeout(function() {
                    alertMessage.fadeOut('slow');
                }, 2000);
            }
        });

    });
});