function createLi(data) {

    console.log(data);
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

let fetchFirst = true;

function getTotalPage(year, month, day, type) {
  $('#spinner').prop('hidden', false);
  let url;
  let loadMoreFunction;
  if (type === 'bar') {
    url = `sales-items-month-total-pages?year=${year}&month=${month}`;
    loadMoreFunction = () => loadMoreItemsMonth(year, month, page);
  } else {
    url = `sales-items-day-total-pages?year=${year}&month=${month}&day=${day}`;
    loadMoreFunction = () => loadMoreItemsDay(year, month, day, page);
  }

  return new Promise((resolve, reject) => {
    $.ajax({
      url,
      type: 'GET',
      success: function (data) {
        resolve(data);
      },
      error: function () {
        $('#spinner').prop('hidden', true);
        reject(new Error('Failed to fetch items'));
      },
    })
      .then(function (total) {
        totalPages = total;

        console.log(fetchFirst + ' ' + totalPages);
        if(fetchFirst == true) {
            if(totalPages == 0)
                $('#noItems').prop('hidden',false);
            else
                fetchFirst = false;
        }

        loadMoreFunction();
      })
      .catch(function (error) {
        console.log(error);
      });
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


function loadMoreItemsDay(year, month, day) {
    $('#spinner').prop('hidden', false);

       $.ajax({
           url: "sales-items-day?year=" + year + "&month=" + month + "&day=" + day + "&page=" + page,
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

var page;
var totalPages = null;

$(document).ready(function() {

    $('#showItemsYear').on('show.bs.modal', function (event) {
       page = 0;
       $('#items-list').empty();
       $('#noItems').prop('hidden', true);

       var year = $('#showItemsYear').data('year');
       var month = $('#showItemsYear').data('month');
       var day = $('#showItemsYear').data('day');

       if($('#showItemsYear').data('type') == 'bar') {
           $('#showItemsYearHeader').text($('#showItemsYear').data('monthText') + ' ' + year);
           getTotalPage(year,month,null, 'bar');
        } else {
           day = $('#showItemsYear').data('day');
           $('#showItemsYearHeader').text($('#showItemsYear').data('day') + ' ' + $('#showItemsYear').data('monthText') + ' ' + year);
           getTotalPage(year,month,day,'line');
        }

       var modalBody = document.querySelector('#showItemsYear .modal-body');

       modalBody.addEventListener('scroll', function() {
       if (modalBody.scrollTop + modalBody.clientHeight == modalBody.scrollHeight) {

         if (totalPages != null && page < totalPages) {
               if($('#showItemsYear').data('type') == 'bar')
                    loadMoreItemsMonth(year, month, page + 1);
               else
                    loadMoreItemsDay(year, month, day, page + 1);
         }
       }
     });
  });

    $('#showItemsYear').on('hidden.bs.modal', function (e) {
        fetchFirst = true;
    })
});
