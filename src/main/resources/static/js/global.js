 $(document).ready(function() {

          setTimeout(function() {
            $('.alert').fadeOut('slow');
          }, 2000);


           $("#toggleBtn").click(function() {
            $('.sidebar-container').toggleClass("d-md-block");
            if ($('.sidebar-container').hasClass("d-md-block")) {
              $("#toggleBtn i").removeClass("fas fa-arrow-right").addClass("fas fa-arrow-left");
              $('.sidebar-container').css("animation-name", "fadeInLeft");
            } else {
              $("#toggleBtn i").removeClass("fas fa-arrow-left").addClass("fas fa-arrow-right");
              $('.sidebar-container').css("animation-name", "fadeOutRight");
            }
          });

        });