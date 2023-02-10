$(document).ready(function() {

 $("#addModalItemSubmit").click(function() {
        var sku = $("#itemSku").val();
        var name = $('#itemName').val();


        if (sku == "" || name == "" ){
            event.preventDefault();

            if(sku == "")
                $("#modalAddItemMessage").text("Sku is empty").addClass("alert alert-danger").show();
            else
                $("#modalAddItemMessage").text("Name is empty").addClass("alert alert-danger").show();

            setTimeout(function() {
                    $("#modalAddItemMessage").fadeOut('slow');
                 }, 2000);
        }
    });

});