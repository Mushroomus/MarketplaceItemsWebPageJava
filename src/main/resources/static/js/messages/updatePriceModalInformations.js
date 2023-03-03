$(document).ready(function() {

    $('#updatePriceModalInformations').on('show.bs.modal', function (event) {
                    var button = $(event.relatedTarget);

                    var oldItemSku = button.data('oldItemSku');
                    var oldItemName = button.data('oldItemName');
                    var oldItemCraftable = button.data('oldItemCraftable');
                    var oldItemClass = button.data('oldItemClass');
                    var oldItemQuality = button.data('oldItemQuality');
                    var oldItemType = button.data('oldItemType');
                    var oldItemPrice = button.data('oldItemPrice');

                    var itemPrice = button.data('itemPrice');


                    var modal = $(this);

                        modal.find('.modal-body #updatePriceModalSku').text(oldItemSku);
                        modal.find('.modal-body #updatePriceModalName').text(oldItemName);
                        modal.find('.modal-body #updatePriceModalClass').text(oldItemClass);
                        modal.find('.modal-body #updatePriceModalQuality').text(oldItemQuality);
                        modal.find('.modal-body #updatePriceModalType').text(oldItemType);
                        modal.find('.modal-body #updatePriceModalCraftable').text(oldItemCraftable ? 'Yes' : 'No');

                         var diffList = modal.find('.modal-body #diffList');
                         diffList.empty();

                         var priceDiff = parseFloat(itemPrice) - parseFloat(oldItemPrice);
                         var priceDiffPercentage = ((priceDiff / parseFloat(oldItemPrice)) * 100).toFixed(2) + '%';

                         diffList.append('<li>' + 'MP Price: ' + oldItemPrice + ' <i class="fas fa-arrow-right fa-sm"></i> ' +  itemPrice + ' (' + priceDiff.toFixed(2) + ', ' + priceDiffPercentage + ')</li>');

    });

});