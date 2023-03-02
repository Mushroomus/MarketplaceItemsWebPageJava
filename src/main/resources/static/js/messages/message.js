
$(document).ready(function() {

    $.ajax({
        type: "GET",
        url: "/messages/fetch",
        contentType: "application/json",
        success: function(response, status, xhr) {
            console.log(response);
        },
        error: function(xhr, status, error) {
            console.log(error);
        }
    });

});


