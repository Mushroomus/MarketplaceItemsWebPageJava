
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

    $.ajax({
        url: 'sales-best', // Replace with your server endpoint URL
        success: function (items) {

            console.log(items);

        }
    });
}

var chart;

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
}


function updateSalesChart(year) {
    // Get the sales data for the selected year from the server using AJAX
    $.ajax({
        url: 'sales-year?year=' + year, // Replace with your server endpoint URL
        success: function(salesData) {

            var monthNames = [  "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"];

            var labels = salesData.map(function(d) { return monthNames[d.month]; });
            var values = salesData.map(function(d) { return d.count; });

            //var chart = $('#myChart').get(0).getContext('2d');
            chart.data.labels = labels;
            chart.data.datasets[0].data = values;
            chart.update();
        }
    });
}

$(document).ready(function() {
    initChart();
    fetchYears();
});