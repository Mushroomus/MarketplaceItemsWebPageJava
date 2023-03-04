 $(document).ready(function() {

          setTimeout(function() {
            $('.alert').fadeOut('slow');
          }, 2000);


           $("#toggleButton").click(function() {
            $('.sidebar-container').toggleClass("d-md-block");
            if ($('.sidebar-container').hasClass("d-md-block")) {
              $("#toggleButton i").removeClass("fa-arrow-right").addClass("fa-arrow-left");
              $('.sidebar-container').css("animation-name", "fadeInLeft");
            } else {
              $("#toggleButton i").removeClass("fa-arrow-left").addClass("fa-arrow-right");
              $('.sidebar-container').css("animation-name", "fadeOutRight");
            }
          });

        });