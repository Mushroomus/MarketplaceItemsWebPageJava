function createLi(data) {
     var listItems = "";
      $.each(data, function(index, item) {
        listItems += '<li class="list-group-item">';
          listItems += '<h6>' + item.sku + '</h4>';
          listItems += "Name: " + item.name + '<br>';
          listItems += "<small style= 'text-muted'>Sale Number: " + item.count + '</small>';
          listItems += '</li>';
      });

     $("#items-list").append(listItems);
}

var page;
var totalPages = null;

function getTotalPageMonth(year,month) {

    $('#spinner').prop('hidden', false);

    return new Promise((resolve, reject) => {
        $.ajax({
          url: "sales-items-month-total-pages?year=" + year + "&month=" + month,
          type: "GET",
          success: function(data) {
            resolve(data);
          },
          error: function() {
            $('#spinner').prop('hidden', true);
            reject("Failed to fetch items");
          }
        })
      }).then(function(total) {
          totalPages = total;
          loadMoreItemsMonth(year, month);
        }).catch(function(error) {
          console.log(error);
        });
 }


function loadMoreItemsMonth(year, month) {
    $('#spinner').prop('hidden', false);

    $.ajax({
        url: "sales-items-month?year=" + year + "&month=" + month + "&page=" + page,
        type: "GET",
        success: function(data) {

            $('#spinner').prop('hidden', true);

            if(data.length > 0) {
                createLi(data);
                page++;
            }
        },
        error: function() {
          $('#spinner').prop('hidden', true);
          alert("Failed to fetch items");
        }
      });
}
var year;
var month;

$(document).ready(function() {

    $('#showItemsYear').on('show.bs.modal', function (event) {
       page = 0;
       $('#items-list').empty();

       year = $('#showItemsYear').data('year');
       month = $('#showItemsYear').data('month');

       if($('#showItemsYear').data('type') == 'bar') {
           $('#showItemsYearHeader').text($('#showItemsYear').data('monthText') + ' ' + year);
           getTotalPageMonth(year,month);
           /*
          $.ajax({
            url: "sales-items-month?year=" + year + "&month=" + month,
            type: "GET",
            success: function(data) {
                createLi(data);
            },
            error: function() {
              alert("Failed to fetch items");
            }
          });
          */
      } else {
            var day = $('#showItemsYear').data('day');
           $('#showItemsYearHeader').text($('#showItemsYear').data('day') + ' ' + $('#showItemsYear').data('monthText') + ' ' + year);

           $.ajax({
               url: "sales-items-day?year=" + year + "&month=" + month + "&day=" + day,
               type: "GET",
               success: function(data) {
                    createLi(data);
               },
              error: function() {
                alert("Failed to fetch items");
              }
        });
     }

     var modalBody = document.querySelector('#showItemsYear .modal-body');

     modalBody.addEventListener('scroll', function() {
       if (modalBody.scrollTop + modalBody.clientHeight == modalBody.scrollHeight) {
       console.log(totalPages + ' ' + page)
         if (totalPages != null && page < totalPages) {
               loadMoreItemsMonth(year, month, page + 1);
         }
       }
     });

  });
});