$(document).ready(function() {

    $('#deleteModalInformations').on('show.bs.modal', function (event) {
                    var button = $(event.relatedTarget);

                    var itemSku = button.data('itemSku');
                    var itemName = button.data('itemName');
                    var itemCraftable = button.data('itemCraftable');
                    var itemClass = button.data('itemClass');
                    var itemQuality = button.data('itemQuality');
                    var itemType = button.data('itemType');

                    var modal = $(this);
                    modal.find('.modal-body #deleteModalSku').text(itemSku);
                    modal.find('.modal-body #deleteModalName').text(itemName);

                    modal.find('.modal-body #deleteModalClass').text(itemClass);
                    modal.find('.modal-body #deleteModalQuality').text(itemQuality);
                    modal.find('.modal-body #deleteModalType').text(itemType);
                    modal.find('.modal-body #deleteModalCraftable').text(itemCraftable ? 'Yes' : 'No');
    });

});