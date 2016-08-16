<!DOCTYPE html>
<html>

  <head>

    <link rel="shortcut icon" href="/favicon.ico" type="image/x-icon">
    <link rel="icon" href="/favicon.ico" type="image/x-icon">

      <br></br><br></br><br></br><br></br>
    <font size="30" face="Bookman Old Style">  <center> Machine Yearning </center> </font>
      <div style="height:25px;"> </div>
    <font size="5"> <center> Wikipedia Search Engine - Top 15</center> </font>
      <div style="height:50px;"> </div>
  </head>

  <style>

    .searchbox{
      text-align:center;
    }

    input[type="submit"]{
      border: none;
      color: white;
      padding: 15px 32px;
      text-align: center;
      text-decoration: none;
      display: inline-block;
      font-size: 16px;
      margin: 4px 2px;
      cursor: pointer;
      border-radius: 4px;
    }

    input[id="search"]{
      background-color: #008CBA; /* Blue */
    }

    input[id="lucky"]{
      background-color: #4CAF50; /* Green */
    }

  </style>


  <body>


    <form action="results.jsp" method="GET">
      <div class="searchbox">
          <input type="text" name="searchterm" id="searchterm" placeholder="Search" style="width: 300px; height: 40px; font-size: 15pt;">
          <input type="submit" id="search" value="Search">
          <input type="submit" id="lucky" value="I'm feeling lucky">
      </div>
    </form>

    <div style="position: relative; right: 0px; bottom: 360px;">
      Keywords: AND, OR, MINUS
    </div>

      <div style="position: relative; width: 250px; height: 100px;">
        <div style="position: absolute; right: -1095px; bottom: -200px;">
          <div align="right">
            <font size="3"> by Caitlin Whitter and Alan Peral </font>
          </div>
        </div>
      </div>

  </body>


<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
<link rel="stylesheet" href="https://ajax.googleapis.com/ajax/libs/jqueryui/1.11.3/themes/smoothness/jquery-ui.css" />
<script src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.11.3/jquery-ui.min.js"></script>


<!--Wikipedia live search suggestions from API-->
<script>
$("#searchterm").autocomplete({
    source: function(request, response) {
        $.ajax({
            url: "http://en.wikipedia.org/w/api.php",
            dataType: "jsonp",
            data: {
                'action': "opensearch",
                'format': "json",
                'search': request.term
            },
            success: function(data) {
                response(data[1]);
            }
        });
    }
});
</script>


<!-- Buttons -
        Needed to write script instead of using a form so the two buttons can perform
      different actions -->

<!--Regular search (uses our java code)-->
<script>
  document.getElementById('search').onclick = function() {
        //show div once button is pressed once
       document.getElementById('searchResultDiv').style.display = "block";
  };
</script>

<!--I'm feeling lucky search-->
<script>
  document.getElementById('lucky').onclick = function() {
      var searchterm = document.getElementById('searchterm').value;
      window.open('https://en.wikipedia.org/wiki/' + searchterm);
    };
</script>


</html>
