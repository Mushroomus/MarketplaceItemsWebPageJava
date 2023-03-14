 $(document).ready(function() {

     $('#addSales').on('show.bs.modal', function (event) {
     })

     $('#load-csv-btn').click(function () {
        // Verify if a file was selected
        var file = $('#csv-file').prop('files')[0];

        if (!file) {
          alert('Please select a file.');
          return false;
        } else if (file.name.split('.').pop() !== 'csv') {
          alert('Please select a CSV file.');
          return false;
        }
        else {

            var formData = new FormData();
            formData.append('file', file);

            $('#addSales').modal('hide');

            $('table').hide();
            $('#spinnerPaginationHide').hide();
            $('#spinner').prop('hidden', false);

            var alertMessageSale = $("#alertMessageSale");

            $.ajax({
                      type: "POST",
                      url: "fetchCSV",
                      data: formData,
                      contentType: false,
                      processData: false,
                      success: function(data) {
                            $('#spinner').prop('hidden', true);
                            $('table').show();
                            $('#spinnerPaginationHide').show();
                            setAlert(alertMessageSale, "Records added", true);
                      },
                      error: function(xhr, status, error) {
                            $('#spinner').prop('hidden', true);
                            $('table').show();
                            $('#spinnerPaginationHide').show();
                            setAlert(alertMessageSale, "Something went wrong", false);
                      }
                    });
                    timeout(alertMessage);
             }
      });

 });


