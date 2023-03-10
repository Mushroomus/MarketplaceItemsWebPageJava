$(document).ready(function() {

    $('#showItemsYear').on('show.bs.modal', function (event) {
       var year = $('#showItemsYear').data('year');
       var month = $('#showItemsYear').data('month');

       $('#showItemsYearHeader').text($('#showItemsYear').data('monthText') + ' ' + year);

      $.ajax({
        url: "sales-items-month?year=" + year + "&month=" + month,
        type: "GET",
        success: function(data) {
          // Loop through the data and create list items
          var listItems = "";
          $.each(data, function(index, item) {
            listItems += '<li class="list-group-item">';
              listItems += '<h6>' + item.sku + '</h4>';
              listItems += "Name: " + item.name + '<br>';
              listItems += "<small style= 'text-muted'>Sale Number: " + item.count + '</small>';
              listItems += '</li>';
          });

          // Append the list items to the list
          $("#items-list").append(listItems);
        },
        error: function() {
          alert("Failed to fetch items");
        }
      });
  });
});