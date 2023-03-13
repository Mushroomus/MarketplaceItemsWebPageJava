$(document).ready(function() {

    $('#itemInformationsModal').on('show.bs.modal', function (event) {
                    var button = $(event.relatedTarget);

                    var itemSku = button.data('sku');
                    var itemName = button.data('name');
                    var itemCraftable = button.data('craftable');
                    var itemClass = button.data('classitem');
                    var itemQuality = button.data('quality');
                    var itemPrice = button.data('marketplaceprice');
                    var itemType = button.data('type');

                    var modal = $(this);
                    modal.find('.modal-body #itemInformationsSku').text(itemSku);
                    modal.find('.modal-body #itemInformationsName').text(itemName);
                    modal.find('.modal-body #itemInformationsPrice').text(itemPrice);

                    modal.find('.modal-body #itemInformationsClass').text(itemClass);
                    modal.find('.modal-body #itemInformationsQuality').text(itemQuality);
                    modal.find('.modal-body #itemInformationsType').text(itemType);
                    modal.find('.modal-body #itemInformationsCraftable').text(itemCraftable ? 'Yes' : 'No');

    });

});