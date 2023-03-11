function fetchYears() {

    $.ajax({
        url: 'get-years', // Replace with your server endpoint URL
        success: function (years) {
            // Populate the year select options
            var yearSelect = $('#yearSelect');
            $.each(years, function (index, year) {
                yearSelect.append($('<option>', {
                    value: year,
                    text: year
                }));
            });
            updateSalesChart(yearSelect.val());
        }
    });

    /*
    $.ajax({
        url: 'sales-best', // Replace with your server endpoint URL
        success: function (items) {

            console.log(items);

        }
    });
    */
}

var chart;

var monthNames = [ "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"];


function initChart() {
    var ctx = $('#myChart');
    chart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: [],
            datasets: [{
                label: 'Sales',
                data: [],
                backgroundColor: 'rgba(255, 99, 132, 0.2)',
                borderColor: 'rgba(255, 99, 132, 1)',
                borderWidth: 1
            }]
        },
        options: {
            scales: {
                y: {
                    beginAtZero: true
                }
            }
        }
    });

    ctx.on('click', function(evt) {
        var activePoints = chart.getElementsAtEventForMode(evt, 'nearest', {intersect: true}, true);
        if (activePoints.length > 0) {
            var clickedElementIndex = activePoints[0].index;

            var year = $('#yearSelect').val();

            if(chart.config.type == 'bar') {
                let firstSpaceIndex = chart.data.labels[clickedElementIndex].indexOf(' ');
                var month = chart.data.labels[clickedElementIndex].substring(0, firstSpaceIndex);

                $('#showItemsYear').data('monthText', month);
                $('#showItemsYear').data('month', ($.inArray(month, monthNames) + 1));
            } else {
                var parts = chart.data.labels[clickedElementIndex].split(".");
                var day = parts[0];
                var month = parts[1];
                var monthWithoutLeadingZero = parseInt(month, 10).toString();
                var monthText = monthNames[monthWithoutLeadingZero - 1];

                $('#showItemsYear').data('month', month);
                $('#showItemsYear').data('monthText', monthText);
                $('#showItemsYear').data('day', day);
            }

            $('#showItemsYear').data('type', chart.config.type);
            $('#showItemsYear').data('year', year);
            $('#showItemsYear').modal('show');
        }
    });
}

var monthsFetched = false;

function updateSalesChart(year) {

    currentYear = year;
    // Get the sales data for the selected year from the server using AJAX
    $.ajax({
        url: 'sales-year?year=' + year, // Replace with your server endpoint URL
        success: function(salesData) {

             var monthSelect = $('#monthSelect');

            if(monthsFetched == false) {
                 monthSelect.empty();
                 monthSelect.append($('<option>', { value: "all", text: "All" }));

                 $.each(salesData, function (index, sale) {
                                var month = sale.month;
                                monthSelect.append($('<option>', {
                                    value: monthNames[month-1],
                                    text: monthNames[month-1]
                                }));
                            });
                 monthsFetched = true;
            }

            var labels = salesData.map(function(d) { return monthNames[d.month-1] + ' ' + year; });
            var values = salesData.map(function(d) { return d.count; });

            chart.config.type = 'bar';
            chart.data.labels = labels;
            chart.data.datasets[0].data = values;
            chart.update();
        }
    });
}

$(document).ready(function() {
    initChart();
    fetchYears();

    $('#monthSelect').change( function() {

      var year = $('#yearSelect').val();
      var month = $('#monthSelect').val();

      if(month == "all") {
        updateSalesChart(year);
      } else {
          $.ajax({
            url: 'sales-month?year=' + year + '&month=' +  ($.inArray(month, monthNames) + 1),
            type: 'GET',
            dataType: 'json',
            success: function(data) {

              var monthYear =  '.' + (String($.inArray(month, monthNames) + 1).padStart(2, '0')) + '.' + year;

              chart.config.type = 'line';
              chart.data.labels = data.map(function(row) { return String(row.day).padStart(2, '0') + monthYear; });
              chart.data.datasets[0].data = data.map(function(row) { return row.count; });
              chart.update();
            },
            error: function(jqXHR, textStatus, errorThrown) {
              console.log('Error: ' + errorThrown);
            }
          });
        }
    });


    $('#yearSelect').change( function() {
      monthsFetched = false;
      $('#monthSelect').val('all');
      updateSalesChart($('#yearSelect').val());
    });



});