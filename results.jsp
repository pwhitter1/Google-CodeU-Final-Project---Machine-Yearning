<!DOCTYPE html>
<html>

  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
        <link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
        <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
</head>


<style>
       label {
           float: left;
       }
       input {
           width: 40%;
       }
       span {
           display: block;
           overflow: hidden;
           padding-right: 5px;
       }

       h3 {
         margin:10px;
         padding:0;
       }

       #footer {
         position:absolute;
         bottom:0;
         width:100%;
         height:60px;   /* Height of the footer */
       }

   </style>

<body>

  <nav class="navbar navbar-default navbar-fixed-top">
          <div class="container-fluid">
              <!-- header -->
            <div class="navbar-header">
              <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#navbar1">
                  <span class="sr-ony"></span>
                  <span class="icon-bar"></span>
                  <span class="icon-bar"></span>
                  <span class="icon-bar"></span>
              </button>
              <a class="navbar-brand" href="response.jsp"><b>HOME</b></a>
            </div>
             <!-- menu -->
             <div class="collapse navbar-collapse" id="navbar1">
                 <p class="nav navbar-text"> Showing search results for: <b><%

                  String term = request.getParameter("searchterm");
                  if(term == null) { term = request.getParameter("search"); }
                  out.print(term); %></b>

                </p>
                 <form class ="navbar-form navbar-right" role ="search">
                     <div class="input-group">
                         <form theme="simple" cssClass="searchbox" action="results.jsp"  method="GET">
                         <input type="text" name="search" id="searchTerm" class="form-control" placeholder="Search" style="width:400px;">
                         <span class="input-group-btn">
                             <button type="submit" id="searchBar" class="btn btn-default">
                                 <span class="glyphicon glyphicon-search"></span>
                             </button>
                         </form>
                         </span>
                     </div>
                 </form>
              </div>
          </div>

         </nav>


  <%@ page import="java.io.*,com.flatironschool.javacs.WikiSearch, com.flatironschool.javacs.SimpleSpellCheck, java.util.List, java.util.ArrayList"%>

<div>
  <%
    out.println("<BR>");
    out.println("<BR>");
    out.println("<BR>");

    SimpleSpellCheck check = new SimpleSpellCheck();

    int count = 1;
    WikiSearch search = new WikiSearch();

    String query = request.getParameter("searchterm");
    query = (query != null) ? query : request.getParameter("search");
    if(query == null) { return; }

    /* SEARCH */
    List<String> searchResults = new ArrayList<String>();
    searchResults.addAll(search.searchQuery(query));
    for(String result : searchResults) {
      out.print("<h4>");
      out.println("&nbsp; &nbsp;" + count + ". " + "<a href='");
      out.println("https://en.wikipedia.org/wiki/" + result);
      out.println("'>" + result + "</a></h4><BR>");
      count++;
    }

    /* DID YOU MEAN */
    if(searchResults.size() == 0) {
      String correctedQuery = check.checkSpelling(query);
      //apparently the dictionary query is shifted over by one
      if(!query.equals(correctedQuery.substring(1, correctedQuery.length()))) {
        out.println("&nbsp; Did you mean: <b>" + correctedQuery + "</b>?");
      }

      out.println("<BR>");
      out.println("&nbsp; No search results.");
  }

  %>
</div>


    <div align="left" id="footer">
      <font size="2">&nbsp; Top 15 Pages [07/17/16-07/23/16]</font>
      <div style="height:0px;"> </div>
      <font size="2">&nbsp; Donald_Trump, Ivana_Trump,
        Ivanka_Trump, Kabali, Main_Page, Marla_Maples, Melania_Trump,
        Mike_Pence, Okto, Pokémon_Go, Proyecto_40, Stranger_Things_(TV_series),
        Tanghulu, Tiffany_Trump, Tim_Kaine</font>
    </div>
  </div>
</div>

</body>

</html>
