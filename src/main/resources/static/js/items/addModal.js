
$(document).ready(function() {


    var addForm = $("#addForm");
    var submitButton = $("#addSubmitButton");

    addForm.on('submit', function(event) {

        event.preventDefault();

        if (addForm[0].checkValidity()) {

            var sku = $("#itemSku").val();
            var name = $("#itemName").val();
            var craftable = $("#itemCraftable").val();
            var itemClass = $("#itemClass").val();
            var quality = $("#itemQuality").val();
            var type = $("#itemType").val();
            var image = $("#itemImage").val();

            $.ajax({
                url: "add",
                type: "POST",
                contentType: "application/json",
                data: JSON.stringify({
                    sku: sku,
                    name: name,
                    craftable: craftable,
                    classItem: itemClass,
                    quality: quality,
                    type: type,
                    image: image
                }),
                dataType: 'json',
                success: function(response, status, xhr) {
                    if (xhr.status == 200) {

                        $("#modalAddItemMessage").text("Item successfully added").removeClass("alert alert-danger").addClass("alert alert-success").show();
                        refreshTable(currentPage);

                    }
                    else if (xhr.status == 400)
                        $("#modalAddItemMessage").text("Something went wrong").removeClass("alert alert-danger").addClass("alert alert-danger").show();
                    else
                        $("#modalAddItemMessage").text("Something went wrong").removeClass("alert alert-danger").addClass("alert alert-danger").show();
                },
                error: function(xhr, status, error) {
                    $("#modalAddItemMessage").text("Something went wrong").removeClass("alert alert-danger").addClass("alert alert-danger").show();
                }
            });

            setTimeout(function() {
                $("#modalAddItemMessage").fadeOut('slow');
            }, 5000);

        }
            //addForm[0].submit();
    });

   addForm.on('input', function(event) {

     if (addForm[0].checkValidity())
        submitButton.prop('disabled', false);
     else
        submitButton.prop('disabled', true);
   });

/*
var myForm = $("#addForm");

myForm.addEventListener('submit', function(event) {

  // Prevent the default form submission behavior
  event.preventDefault();

  // Check if all inputs in the form are valid
  if (myForm.checkValidity()) {
    // If all inputs are valid, submit the form
    myForm.submit();
  } else {
    // If any input is invalid, display an error message
    alert('Please fill out all required fields');
  }
});


 $("#addModalItemSubmit").click(function() {
        var sku = $("#itemSku").val();
        var name = $('#itemName').val();

        if ($('.was-validated')[0].checkValidity()) {
            $('.was-validated').submit();
          }

    });


/*
  var myForm = document.querySelector('.was-validated');
  var submitButton = myForm.querySelector('button[type="submit"]');

  myForm.addEventListener('input', function(event) {
    // Check if all inputs in the form are valid
    if (myForm.checkValidity()) {
      // If all inputs are valid, enable the submit button
      submitButton.disabled = false;
    } else {
      // If any input is invalid, disable the submit button
      submitButton.disabled = true;
    }
  });
  */

});