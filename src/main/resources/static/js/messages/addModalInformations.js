$(document).ready(function() {

    $('#addModalInformations').on('show.bs.modal', function (event) {
                    var button = $(event.relatedTarget);

                    var itemSku = button.data('itemSku');
                    var itemName = button.data('itemName');
                    var itemCraftable = button.data('itemCraftable');
                    var itemClass = button.data('itemClass');
                    var itemQuality = button.data('itemQuality');
                    var itemPrice = button.data('itemPrice');
                    var itemImage = button.data('itemImage');
                    var itemType = button.data('itemType');


                    var modal = $(this);
                    modal.find('.modal-body #addModalSku').text(itemSku);
                    modal.find('.modal-body #addModalName').text(itemName);
                    modal.find('.modal-body #addModalPrice').text(itemPrice);

                    modal.find('.modal-body #addModalClass').text(itemClass);
                    modal.find('.modal-body #addModalQuality').text(itemQuality);
                    modal.find('.modal-body #addModalType').text(itemType);
                    modal.find('.modal-body #addModalCraftable').text(itemCraftable ? 'Yes' : 'No');

                    if(itemImage == null || itemImage == "")
                        modal.find('.modal-body #imageLi').hide();
                    else
                        modal.find('.modal-body #addModalImage').text(itemImage);
    });

});