
$(document).ready(function() {


    var addForm = $("#addForm");
    var submitButton = $("#addSubmitButton");

    addForm.on('submit', function(event) {

        event.preventDefault();

        if (addForm[0].checkValidity())
            addForm[0].submit();
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