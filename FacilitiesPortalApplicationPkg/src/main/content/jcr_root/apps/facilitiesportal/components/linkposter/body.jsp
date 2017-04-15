<%@taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0" %>
<%@include file="/libs/foundation/global.jsp"%>
<cq:defineObjects/>
<cq:includeClientLib categories="cq.jquery, ckeditor"/>
<!-- <script src="//cdn.ckeditor.com/4.5.11/basic/ckeditor.js"></script> -->
  <script type="text/javascript"> 
  function validateSubmit(){
	  if($("#participants").val() == "" || $("#surveyId").val() == "" || $("#message").val() == "" || $("#subject").val() == "" || $("#surveyForm").val() == null || $("#description").val() == "" ){
		  var confirmation = confirm("Please validate data :\n"+"Participants Email ID	: "+$("#participants").val()+"\n"+
				  "Email Subject	: "+$("#subject").val()+"\n"+
				  "Email Message	: "+$("#message").val()+"\n"+
					"Description	: "+$("#description").val()+"\n"+
					"Selected Form	: "+$("#surveyForm").val()+"\n");
		  if(confirmation) $("#surveyId").val(parseInt(window.SurveyID));
		  return confirmation;
	  }
		  else {
			  $("#surveyId").val(parseInt(window.SurveyID));
			  return true
  }
  }
  function fetchSurveys(){
		 $.ajax("/bin/fp/survey/list", {
		type: "GET",
   success: function(data, status, xhr) {
 	  $('#surveys tr').not(':first').remove();
       var surData = data.surveys;
       try {
     	  jQuery.each(surData, function(i,data) {
               $("#surveys").append("<tr><td>" + data.surveyId + "</td><td>" + data.description + "</td><td>" + data.status + "</td><td><a href='#' onclick='toggleStatus(this.id)' id='" + data.surveyId + "'>Active/Deactivate</a></td><td><a href='#' onclick='removeSurvey(this.id)' id='" + data.surveyId + "'>Delete</a></td></tr>");
           });
           
           
           } catch(err) {
           //failure(err);
       }
   },
   error: function(xhr, status, err) {
       //failure(err);
   } 
});
}
  function toggleStatus(surveyID) {
	  var confirmed = confirm("Please confirm you want to change status ?");
	   if(confirmed)
		 $.ajax("/bin/fp/survey/update?surveyId="+surveyID, {
		     success: function(data, status, xhr) {
		         try {
		        	 fetchSurveys();
		        	 } catch(err) {
		             failure(err);
		         }
		     },
		     error: function(xhr, status, err) {
		         failure(err);
		     } 
		 });
		 return false;
  }
  
  function removeSurvey(surveyID) {
	  var confirmed = confirm("It might be an active survey ! Confirm removal ? ");
	   if(confirmed)
		 $.ajax("/bin/fp/survey/delete?surveyId="+surveyID, {
		     success: function(data, status, xhr) {
		         try {
		        	 fetchSurveys();
		        	 } catch(err) {
		             failure(err);
		         }
		     },
		     error: function(xhr, status, err) {
		         failure(err);
		     } 
		 });
		 return false;
  };
  
  var failure = function(err) {
      alert("Operational error "+err);
};
  
  $(document).ready(function() {  
	  window.GeoFormsJSON =  new Object();
	  window.GeographiesJSON = new Object();
	  fetchSurveys();
  $.ajax("/bin/fp/survey/forms", {
      success: function(data, status, xhr) {
    	  console.log(xhr.status); 
   	   if(true){
          window.GeoFormsJSON= data.geoforms;
   	   }
          try {
              data.geoforms.forEach(function(elem, index) {
           	var opt = new Option(elem, elem);
           	$('#surveyForm').append(opt); 
           	  }); 
              
  			} catch(err) {
              failure(err);
          }
      },
      error: function(xhr, status, err) {
    	  console.log(xhr.status); 
          failure(err);
      } 
  }); // End of fetch form for geo
  
  $.ajax("/bin/fp/survey/getId", {
      success: function(data, status, xhr) {
    	  window.SurveyID = data.id;
          try {
        	  $("#surveyId").val('SRV-'+data.id);
              
  			} catch(err) {
              failure(err);
          }
      },
      error: function(xhr, status, err) {
          failure(err);
      } 
  }); // End of fetch form for geo

  $('#cancelbutton').click(function() {
	  $("#surveyform").hide();
  });
  
  
  $(document).keyup(function(e) {
	     if (e.keyCode == 27) { 
	    	 $("#surveyform").hide();
	    }
	});
  
  $('#createsurvey').click(function() {
	  $("#surveyform").show();
  });
  
  $('#sendbutton').click(function() {
	  var ifValid = validateSubmit();
  		if(ifValid) {
	  $.ajax("/bin/fp/survey/create", {
		  type: "POST",
		  data: $('form#surveyform').serialize(),
		  beforeSend: function(){
			  $("#surveyform").fadeOut(5000);
          },
	       success: function(data, status, xhr) {
	           try { 
	        	   $("#surveyform").fadeIn(3000);
	        	   setTimeout(window.location.reload(),4000);
	               } catch(err) {
	               failure(err);
	           }
	       },
	       error: function(xhr, status, err) {
	           failure(err);
	       } 
	   });
	    return false; 
	  }
	    
	   }
  
  ); 
  
 // End of fetch geos 
}); // End of document ready
   
</script> 
 <style type="text/css">
td a {
	border: 1px solid #aaa;
	text-decoration: none;
	background-color: #fafafa;
	color: #123456;
	margin: 2px;
}

select {
	width: 100px;
	height: 80px;
}

#surveyform {
	position: absolute;
	width: 50%;
	left: 25%;
	top: 25%;
	margin: 0 auto;
	background-color: white;
	border-left: 6px solid red;
    box-shadow: 5px 5px 10px #888888;
}
#messageEditor {
  height: 375px;
}
#surveyform a, input, textarea, select, input[type=button] {
	position: relative;
	width: 50%;
	left: 25%;
}

#surveyform label {
position: relative;
	text-align: center;
	font-weight: bold;
	color: black;
	background-color: grey;
	left : 25%; 
	width: 50%;
}

#surveyform select {
position: relative;
	text-align: center;
	font-weight: bold;
	color: black;
	background-color: grey;
	height: 50%;
}

#surveys {
	font-family: "Trebuchet MS", Arial, Helvetica, sans-serif;
	border-collapse: collapse;
	width: 95%;
}

#surveys td, #surveys th {
	border: 1px solid #ddd;
	padding: 8px;
}

#surveys tr:nth-child(even) {
	background-color: #f2f2f2;
}

#surveys tr:hover {
	background-color: orange;
}

#surveys th {
	padding-top: 12px;
	padding-bottom: 12px;
	text-align: left;
	background-color: #696969;
	color: white;
}

.button {
	background-color: #696969;
	border-width: thin;
	border-radius: 5px;
	color: white;
	padding: 1%;
	text-align: center;
	text-decoration: none;
	display: inline-block;
	font-size: 90%;
	cursor: pointer;
    box-shadow: 5px 5px 10px #888888;
  clear: both;
}

.button:hover {
	background-color: orange;
}
.button:active {
	border-width: medium;
}

</style> 
<a href="#" class="button" id="createsurvey">New Survey</a>
<table id="surveys">
  <tr>
    <th>ID</th>
    <th>Description</th>
    <th>Status</th>
    <th></th>
    <th></th>
  </tr>
  
</table>
<form id="surveyform" class="dark-matter" action="#" method="post" style="display:none">
<br/>
	<label for="surveyId">Survey ID</label><br/>
    <input id="surveyId" name="surveyId" type="text" readonly="readonly"></input><br/><br/>
    <label for="description">Description</label><br/>
    <input id="description" name="description" type="text"></input><br/><br/>
    <label for="duration">Duration(in days)</label><br/>
    <input id="duration" name="duration" type="text"></input><br/><br/>
    <label for="participants">Participants Email ID</label><br/>
    <input id="participants" name="participants" type="text"></input><br/><br/>
    <label for="subject">Email Subject</label><br>
    <input id="subject" name="subject" type="text"></input><br/><br/>
    <label for="message">Email Message</label><br>
    <textarea id="message" name="message"></textarea><br><br/>
  	<select id="surveyForm" name="surveyForm" id="surveyForm"></select> <br/><br>
   <input id="sendbutton" class="button" type="button" value="Start"><br><input id="cancelbutton" class="button" type="button" value="Cancel">

</form>
<br/><br/><br/><br/>


<cq:include path="toolbar" resourceType="foundation/components/parsys"/>