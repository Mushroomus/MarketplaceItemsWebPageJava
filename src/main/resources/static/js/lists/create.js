
$(document).ready(function() {

    var listNamesArray = $("#list-names").text().split(",");
    console.log(listNamesArray);

    $('#inputName').trigger('input');

    $('#inputName').on('input', function() {

        var listName = $(this).val();

        if(listName == "") {
            $("#inputName").removeClass("is-valid").addClass("is-invalid");
            $("#saveList").prop("disabled", true);
        } else if(listNamesArray.includes(listName)) {
            $("#inputName").removeClass("is-valid").addClass("is-invalid");
            $("#saveList").prop("disabled", true);
        } else {
            $("#inputName").removeClass("is-invalid").addClass("is-valid");
            $("#saveList").prop("disabled", false);
        }
    });

});


