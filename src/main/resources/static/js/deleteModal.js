 $(document).ready(function() {
          $('#deleteModal').on('show.bs.modal', function (event) {
                var button = $(event.relatedTarget);

                var itemSku = button.data('itemsku');
                var itemName = button.data('itemname');

                itemName = "'" + itemName + "' will be deleted permanently. Are you sure?";

                var modal = $(this);
                modal.find('.modal-body #modalDeleteItemSku').val(itemSku);
                modal.find('.modal-body #modalDeleteItemName').text(itemName);
          })
        })