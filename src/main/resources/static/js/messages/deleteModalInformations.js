$(document).ready(function() {

    $('#deleteModalInformations').on('show.bs.modal', function (event) {
                    var button = $(event.relatedTarget);

                    var itemName = button.data('itemName');
                    console.log(itemName);

                    var modal = $(this);
                    modal.find('.modal-body #modalDeleteItemName').text(itemName);
    });

});