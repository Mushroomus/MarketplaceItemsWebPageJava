var firstGraph = true;


function fetchStartData() {
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

            if(firstGraph)
                updateSalesChart(yearSelect.val());
            else
                updateBestWorstCharts(yearSelect.val());
        }
    });
}

var chart;
var secondChart;
var thirdChart;

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

    var secondCtx = $('#myChart2');
        secondChart = new Chart(secondCtx, {
            type: 'pie',
            data: {
                labels: [],
                datasets: [{
                    label: 'Sales',
                    data: [],
                    backgroundColor: 'rgba(255, 99, 132, 0.2)',
                    borderColor: 'rgba(255, 99, 132, 1)',
                     backgroundColor: [
                        'rgba(255, 99, 132, 0.2)',
                        'rgba(54, 162, 235, 0.2)',
                        'rgba(255, 206, 86, 0.2)',
                        'rgba(75, 192, 192, 0.2)',
                        'rgba(153, 102, 255, 0.2)'
                      ],
                      borderColor: [
                        'rgba(255, 99, 132, 1)',
                        'rgba(54, 162, 235, 1)',
                        'rgba(255, 206, 86, 1)',
                        'rgba(75, 192, 192, 1)',
                        'rgba(153, 102, 255, 1)'
                      ],
                      borderWidth: 1
                }]
            },
            options: {
                plugins: {
                  legend: {
                    position: 'bottom'
                  }
                }
              }
        });

      var thirdCtx = $('#myChart3');
              thirdChart = new Chart(thirdCtx, {
                  type: 'pie',
                  data: {
                      labels: [],
                      datasets: [{
                          label: 'Sales',
                          data: [],
                          backgroundColor: 'rgba(255, 99, 132, 0.2)',
                          borderColor: 'rgba(255, 99, 132, 1)',
                           backgroundColor: [
                              'rgba(255, 99, 132, 0.2)',
                              'rgba(54, 162, 235, 0.2)',
                              'rgba(255, 206, 86, 0.2)',
                              'rgba(75, 192, 192, 0.2)',
                              'rgba(153, 102, 255, 0.2)'
                            ],
                            borderColor: [
                              'rgba(255, 99, 132, 1)',
                              'rgba(54, 162, 235, 1)',
                              'rgba(255, 206, 86, 1)',
                              'rgba(75, 192, 192, 1)',
                              'rgba(153, 102, 255, 1)'
                            ],
                            borderWidth: 1
                      }]
                  },
                  options: {
                      plugins: {
                        legend: {
                          position: 'bottom'
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

function fetchMonths(year) {

     var monthSelect = $('#monthSelect');

     $.ajax({
            url: 'fetch-months?year=' + year, // Replace with your server endpoint URL
            success: function(months) {
                monthSelect.empty();
                monthSelect.append($('<option>', { value: "all", text: "All" }));

                 $.each(months, function (index, month) {
                    monthSelect.append($('<option>', {
                        value: monthNames[month-1],
                        text: monthNames[month-1]
                    }));
                });

                monthsFetched = true;
            }
        });

   monthsFetched = true;
}

function updateSalesChart(year) {

    currentYear = year;
    // Get the sales data for the selected year from the server using AJAX
    $.ajax({
        url: 'sales-year?year=' + year, // Replace with your server endpoint URL
        success: function(salesData) {

             var monthSelect = $('#monthSelect');

            if(monthsFetched == false)
                 fetchMonths(year);

            var labels = salesData.map(function(d) { return monthNames[d.month-1] + ' ' + year; });
            var values = salesData.map(function(d) { return d.count; });

            chart.config.type = 'bar';
            chart.data.labels = labels;
            chart.data.datasets[0].data = values;
            chart.update();
        }
    });
}

function updateBestWorstCharts(year, month) {

    //currentYear = year;

    let url;
    if(month == null)
        url = 'sales-best?year=' + year;
    else
      url = 'sales-best?year=' + year + '&month=' + month;

     if(monthsFetched == false)
        fetchMonths(year);

    $.ajax({
        url: url, // Replace with your server endpoint URL
        success: function(salesBest) {

            var labels = salesBest.map(function(d) { return d.sku + ' ' + d.name });
            var values = salesBest.map(function(d) { return d.count; });

            secondChart.data.labels = labels;
            secondChart.data.datasets[0].data = values;
            secondChart.update();
        }
      });

       $.ajax({
              url: url.replace('best', 'worst'), // Replace with your server endpoint URL
              success: function(salesWorst) {

                  var labels = salesWorst.map(function(d) { return d.sku + ' ' + d.name });
                  var values = salesWorst.map(function(d) { return d.count; });

                  thirdChart.data.labels = labels;
                  thirdChart.data.datasets[0].data = values;
                  thirdChart.update();
              }
            });
};

$(document).ready(function() {
    initChart();
    fetchStartData();

    $('#monthSelect').change( function() {

      var year = $('#yearSelect').val();
      var month = $('#monthSelect').val();

      if(month == "all" && firstGraph) {
            updateSalesChart(year);
      } else if(firstGraph) {
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
        else if(firstGraph == false) {
            if(month == "all")
                updateBestWorstCharts($('#yearSelect').val(), null);
            else
                updateBestWorstCharts($('#yearSelect').val(), ($.inArray(month, monthNames) + 1));
        }
    });


    $('#yearSelect').change( function() {
      monthsFetched = false;
      $('#monthSelect').val('all');

      if(firstGraph)
        updateSalesChart($('#yearSelect').val());
      else
        updateBestWorstCharts($('#yearSelect').val(), null);
    });

     $('#firstGraphOption').click(function() {
            $('#myChart').show();
            $('#myChart2').hide();
            $('#myChart3').hide();
            firstGraph = true;
          });

    $('#secondGraphOption').click(function() {

        $('#myChart').hide();
        $('#myChart2').show();
        $('#myChart3').show();
        firstGraph = false;
        if($('#monthSelect').val() == 'all')
            updateBestWorstCharts($('#yearSelect').val(), null);
        else
            updateBestWorstCharts($('#yearSelect').val(), ($.inArray($('#monthSelect').val(), monthNames) + 1));

      });



});