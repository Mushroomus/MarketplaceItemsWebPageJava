$(document).ready(function() {

    $('#updateModalInformations').on('show.bs.modal', function (event) {
                    var button = $(event.relatedTarget);

                    var itemName = button.data('itemName');
                    var itemCraftable = button.data('itemCraftable');
                    var itemClass = button.data('itemClass');
                    var itemQuality = button.data('itemQuality');
                    var itemType = button.data('itemType');

                    var oldItemSku = button.data('oldItemSku');
                    var oldItemName = button.data('oldItemName');
                    var oldItemCraftable = button.data('oldItemCraftable');
                    var oldItemClass = button.data('oldItemClass');
                    var oldItemQuality = button.data('oldItemQuality');
                    var oldItemType = button.data('oldItemType');


                    var modal = $(this);

                        modal.find('.modal-body #updateModalSku').text(oldItemSku);
                        modal.find('.modal-body #updateModalName').text(oldItemName);
                        modal.find('.modal-body #updateModalClass').text(oldItemClass);
                        modal.find('.modal-body #updateModalQuality').text(oldItemQuality);
                        modal.find('.modal-body #updateModalType').text(oldItemType);
                        modal.find('.modal-body #updateModalCraftable').text(oldItemCraftable ? 'Yes' : 'No');

                         // Compare old and new item attributes and display differences
                         var diffList = modal.find('.modal-body #diffList');
                         diffList.empty();

                         if(itemName !== oldItemName) {
                             diffList.append('<li>' + 'Name: ' + oldItemName + ' <i class="fas fa-arrow-right fa-sm"></i> ' + itemName + '</li>');
                         }
                         if(itemCraftable !== oldItemCraftable) {
                             var oldCraftable = oldItemCraftable ? 'Yes' : 'No';
                             var newCraftable = itemCraftable ? 'Yes' : 'No';
                             diffList.append('<li>' + 'Craftable: ' + oldCraftable + ' <i class="fas fa-arrow-right fa-sm"></i> ' + newCraftable + '</li>');
                         }
                         if(itemClass !== oldItemClass) {
                             diffList.append('<li>' + 'Class: ' + oldItemClass + ' <i class="fas fa-arrow-right fa-sm"></i> ' + itemClass + '</li>');
                         }
                         if(itemQuality !== oldItemQuality) {
                             diffList.append('<li>' + 'Quality: ' + oldItemQuality + ' <i class="fas fa-arrow-right fa-sm"></i> ' + itemQuality + '</li>');
                         }
                         if(itemType !== oldItemType) {
                             diffList.append('<li>' + 'Type: ' + oldItemType + ' <i class="fas fa-arrow-right fa-sm"></i> ' + itemType + '</li>');
                         }

                         if(diffList.children().length === 0) {
                             diffList.append('<li>' + 'No changes' + '</li>');
                         }

    });

});