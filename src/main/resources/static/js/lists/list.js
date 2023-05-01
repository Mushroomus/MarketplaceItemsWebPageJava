var currentPage = 0;
var totalPages = 0;
var filter = false;

function createUrl(page) {
    var url = "/lists?page=" + page;

    if(filter) {
          var searchInput = $('#searchInput').val().trim();
          var craftableDropdown = $('#filterCraftableDropdown').val();

          if (searchInput !== "") {
            url += "&search=" + searchInput;
          }

          if (craftableDropdown !== "" && craftableDropdown != "Any") {
            url += "&craftable=" + craftableDropdown;
          }

          var selectedClasses = [];
          $('input[name="classes"]:checked').each(function() {
            selectedClasses.push($(this).val());
          });

          if (selectedClasses.length > 0) {
            url += "&classes=" + selectedClasses.join(",");
          }

          var selectedQualities = [];
          $('input[name="qualities"]:checked').each(function() {
            selectedQualities.push($(this).val());
          });

          if (selectedQualities.length > 0) {
            url += "&qualities=" + selectedQualities.join(",");
          }

          var selectedTypes = [];
          $('input[name="types"]:checked').each(function() {
            selectedTypes.push($(this).val());
          });

          if (selectedTypes.length > 0) {
            url += "&types=" + selectedTypes.join(",");
          }
    }

    return url;
}

function fetchList() {
    $.ajax({
      type: "GET",
      url: createUrl(currentPage),
      success: function(response) {
        // Clear current items in the list
        $('#list1').empty();

        if(response._embedded && response._embedded.itemList) {
            $('#empty-message').hide();

            $.each(response._embedded.itemList, function(i, item) {

                if ($('#list2 .details-item[data-sku="' + item.sku + '"]').length > 0)
                    return;

              var listItem = $('<li class="list-group-item">' +
                              '<div class="row">' +
                                  '<div class="col-9">' +

                                        '<span class="item-text">' + item.name + '</span>' +

                                  '</div>' +
                                  '<div class="col-3">' +
                                      '<button class="btn btn-success move-item" data-page="' + response.page.number + '" style="margin-right: 3px"><i class="fas fa-arrow-right"></i></button>' +
                                      '<button class="btn btn-primary details-item" data-sku="' + item.sku + '" data-class-item="' + item.classItem + '" data-craftable="' + item.craftable + '" data-quality="' + item.quality + '" data-type="' + item.type + '" data-bs-toggle="popover" data-bs-placement="left"><i class="fas fa-info-circle"></i></button>' +
                                  '</div>' +
                              '</div>' +
                            '</li>');

              $('#list1').append(listItem);
            });
        } else {
            $('#empty-message').show();
        }

          currentPage = response.page.number;
          totalPages = response.page.totalPages;

          if (currentPage == 0)
              $('#prev-page').attr('disabled', true);
          else
              $('#prev-page').attr('disabled', false);


          if (currentPage == totalPages - 1)
             $('#next-page').attr('disabled', true);
          else
             $('#next-page').attr('disabled', false);
      },
      error: function(jqXHR, textStatus, errorThrown) {
        console.log("Error fetching list: " + textStatus + ", " + errorThrown);
      }
    });
}

function updatePagination(direction) {
      if (direction === "prev") {
        currentPage--;
      } else {
        currentPage++;
      }
      fetchList();
}


function fetchRightList() {
    return new Promise(function(resolve, reject) {
                $.ajax({
                  type: "GET",
                  url: "fetch-right-list?listName=" + $("#listName").val(),
                  success: function(response) {
                    $('#list2').empty();

                    if(response != null) {
                        $('#empty-message').hide();

                        $.each(response, function(i, item) {
                          var listItem = $('<li class="list-group-item">' +
                                          '<div class="row">' +
                                              '<div class="col-9">' +

                                                    '<span class="item-text">' + item.name + '</span>' +

                                              '</div>' +
                                              '<div class="col-3">' +
                                                  '<button class="btn btn-danger move-item" style="margin-right: 3px"><i class="fas fa-arrow-left"></i></button>' +
                                                  '<button class="btn btn-primary details-item" data-sku="' + item.sku + '" data-class-item="' + item.classItem + '" data-craftable="' + item.craftable + '" data-quality="' + item.quality + '" data-type="' + item.type + '" data-bs-toggle="popover" data-bs-placement="left"><i class="fas fa-info-circle"></i></button>' +
                                              '</div>' +
                                          '</div>' +
                                        '</li>');

                          $('#list2').append(listItem);
                        });
                    } else {
                        $('#empty-message').show();
                    }
                    resolve();
                  },
                  error: function(jqXHR, textStatus, errorThrown) {
                    console.log("Error fetching list: " + textStatus + ", " + errorThrown);
                    reject();
                  }
              });
    });
}


$(document).ready(function() {

    if($('#alertDelete') != null)
        timeout($('#alertDelete'));

    $('#alertMessageList').hide();

    var alertList = localStorage.getItem('alertList');
    var alertMessageList = $("#alertMessageList");

    if(alertList && window.location.href.indexOf('/lists/show') !== -1) {
        if(alertList == "create")
            setAlert(alertMessageList, "List created", true);
        else
            setAlert(alertMessageList, "List edited", true);

        timeout(alertMessageList);
        localStorage.removeItem('alertList');
    }

    $(".price-items-button").click(function() {
        $('#spinnerHide').prop('hidden', true);
        $('#spinner').prop('hidden', false);
    });


    if (window.location.href.indexOf('/lists/create-list') !== -1) {
            fetchList();
        } else if (window.location.href.indexOf('/lists/edit') !== -1) {
            fetchRightList().then(function() {
                fetchList();
            })
            .catch(function() {

            })
        }


        $("#sideFilter").click(function() {
            filter = true;
            currentPage = 0;
            fetchList();
        });

         $("#searchButton").click(function() {
            filter = true;
            currentPage = 0;
            fetchList();
        })

        $("#clearFilter").click(function() {
            if(filter == true) {
                filter = false;
                currentPage = 0;
                fetchList();
            }
        });

        $("#editList").click(function() {

            var listName = $("#listName").val();
            var itemSku = [];

            $('#list2 li').each(function() {
                var sku = $(this).find('.details-item').attr('data-sku');
                if (sku) {
                    itemSku.push(sku);
                }
             });

             $.ajax({
                    type: "POST",
                    url: "/lists/saveEdit",
                    contentType: "application/json",
                    data: JSON.stringify({
                        itemSku: itemSku,
                        listName: listName
                    }),
                    success: function(response, status, xhr) {
                        if(xhr.status == 200) {
                            localStorage.setItem('alertList',"edit");
                            window.location.href = "show";
                        }
                        else {
                            setAlert(alertMessageList, "Something went wrong", false);
                        }
                    },
                    error: function(xhr, status, error) {
                        setAlert(alertMessageList, "Something went wrong", false);
                    }
                });
                timeout(alertMessageList);
        })

        $("#saveList").click(function() {

                var username = $("#usernameNavbar").text();
                var listName = $("#inputName").val();

                var itemSku = [];

                $('#list2 li').each(function() {
                    var sku = $(this).find('.details-item').attr('data-sku');
                    if (sku) {
                        itemSku.push(sku);
                    }
                });

                $.ajax({
                    type: "POST",
                    url: "/lists",
                    contentType: "application/json",
                    data: JSON.stringify({
                        username: username,
                        itemSku: itemSku,
                        listName: listName
                    }),
                    success: function(response, status, xhr) {
                        if(xhr.status == 201) {
                            localStorage.setItem('alertList',"create");
                            window.location.href = "show";
                        }
                        else {
                            setAlert(alertMessageList, "Something went wrong", false);
                        }
                    },
                    error: function(xhr, status, error) {
                        setAlert(alertMessageList, "Something went wrong", false);
                    }
                });
                timeout(alertMessageList);
        });

          $(".list-group").sortable({
            connectWith: ".list-group",
            placeholder: "list-group-item bg-primary",
            dropOnEmpty: true,
            update: function(event, ui) {
                        var item = ui.item;
                        var button = item.find(".move-item");
                        if (item.parent().attr("id") == "list1") {

                           var dataPage = item.find(".move-item").data('page');

                              item.appendTo("#list1");
                              button.removeClass("btn-danger").addClass("btn-success");
                              button.html("<i class='fas fa-arrow-right'></i>");

                        } else {
                          button.removeClass("btn-success").addClass("btn-danger");
                          button.html("<i class='fas fa-arrow-left'></i>");
                        }
                    }
          }).disableSelection();


          $(document).on("click", ".move-item", function() {
            var item = $(this).closest(".list-group-item");
            if (item.parent().attr("id") == "list1") {

              item.appendTo("#list2");
              $(this).removeClass("btn-success").addClass("btn-danger");
              $(this).html("<i class='fas fa-arrow-left'></i>");

            } else {

              var dataPage = $(this).data('page');

              item.appendTo("#list1");
              $(this).removeClass("btn-danger").addClass("btn-success");
              $(this).html("<i class='fas fa-arrow-right'></i>");
            }
          });



  $(document).on("click", ".details-item", function() {
         // Get the item text from the closest list-group-item element

                  var sku = $(this).data('sku');
                  var classItem = $(this).data('class-item');
                  var craftable = $(this).data('craftable');
                  var quality = $(this).data('quality');
                  var type = $(this).data('type');
                  var itemText = $(this).closest('.list-group-item').find('.item-text').text();

                  // Generate the HTML for the popover content
                  var contentHTML = '<div class="popover-details">' +
                                    '<p>Class: ' + classItem + '</p><hr>' +
                                    '<p>Craftable: ' + craftable + '</p><hr>' +
                                    '<p>Quality: ' + quality + '</p><hr>' +
                                    '<p>Type: ' + type + '</p><hr>' +
                                    '</div>';

                  // Show the popover
                  $(this).popover({
                    trigger: 'focus',
                    title: sku,
                    content: contentHTML,
                    html: true,
                    placement: 'right'
                  }).popover('show');
         });

        $('[id^=priceItems]').submit(function(e) {
            var marketplaceKeyPrice = $('#marketplaceKeyPrice').val();

            var formData = $(this).serialize();
            formData += '&marketplaceKeyPrice=' + marketplaceKeyPrice;

            $.ajax({
                type: 'POST',
                url: $(this).attr('action'),
                data: formData,
                success: function(response) {
                    $('.container').html(response);
                    $('#spinnerHide').prop('hidden', false);
                    $('#spinner').prop('hidden', true);
                },
                error: function(xhr, status, error) {
                    console.log('Error: ' + error);
                }
            });
            e.preventDefault();
        });

        $('#marketplaceKeyPrice').on('input', function() {
            var price = $('#marketplaceKeyPrice').val();
            var priceButtons = $('.price-items-button');

            if (price.trim() === '' || price.trim() === '0') {
                priceButtons.prop('disabled', true);
            } else {
                priceButtons.prop('disabled', false);
            }
        });
});