<%@taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0" %>
<%@include file="/libs/foundation/global.jsp"%>
<cq:defineObjects/>
<cq:includeClientLib categories="cq.jquery"/>
<script type="text/javascript">
$(document).ready(function(){
$('#cancelbutton').click(function() {
  	  $("#surveyContainer").hide();
    });
    
    
    $(document).keyup(function(e) {
  	     if (e.keyCode == 27) { 
  	    	 $("#surveyContainer").hide();
  	    }
  	});
});
 
function fetchSurveys(){
	 $.ajax("/bin/fp/survey/list", {
	type: "GET",
success: function(data, status, xhr) {
	  $('#surveys tr').not(':first').remove();
  var surData = data.surveys;
  try {
	  jQuery.each(surData, function(i,data) {
          $("#surveys").append("<tr>"
        		  +"<td>" + data.surveyId + "</td>"
        		  +"<td>" + data.description + "</td>"
        		  +"<td>" + data.form + "</td>"
        		+"<td><input type='radio' name='radiosurvey' value=" + data.surveyId + "></td>"
        		  +"</tr>");
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
	
function displaySurveys(){
fetchSurveys();
$("#surveyContainer").show();
return false;
}

function removeId(id){
	 $.ajax("/bin/fp/del/critsub?criticalId="+id, {
	     success: function(data, status, xhr) {
	         try {
	        	 fetchCriticalSubmissions();
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

function fetchCriticalSubmissions(){	
	 var surId = $("#surveyId").val();
var ifValid = isInt(surId);
	if(ifValid) {
		$.ajax("/bin/fp/surveysub?surveyId="+surId, {
		     success: function(data, status, xhr) {
		         window.submissionsJSON = data;
		         try {
		        	 var html = '<table id="surveys"><tr><th>User ID</th><th>Critical</th><th>View</th></tr><tbody>';
		        	 if(data != null ){
		        		 for (var key in data) {
		        			  if (data.hasOwnProperty(key)) {
		        				if(data[key]['isCritical']=='true')
		        					html += '<tr style="background-color:red">';
		        				else
		        					html += '<tr>';
		        				html += '<td>' + key.substr(0, key.indexOf('_'))+ '</td>';
		        				html += '<td>' + data[key]['isCritical']+ '</td>';
		        				html += '<td><a  target="_blank" href="/bin/fp/view?submissionNode=' + key+ '">View &#8679;</a></td>';
		        				html += "</tr>";
		        			  }
		        			}
		        	 }
		        	 html += '</tbody></table>';
		        	 $('#submissionstable').html("");
		        	 $(html).appendTo('#submissionstable');
		        	 } catch(err) {
		             failure(err);
		         }
		     },
		     error: function(xhr, status, err) {
		         failure(err);
		     } 
		 });
return false; 
}else{
	  alert("Please fill numeric Survey ID");
	  return false;
}
	}
var failure = function(err) {
  alert("Unable to retrive data "+err);
};

function isInt(value) {
	  return !isNaN(value) && 
	         parseInt(Number(value)) == value && 
	         !isNaN(parseInt(value, 10));
	}
function insertIntoField(){
	var valid = true;
	var selectedSurvey = $('input[name=radiosurvey]:checked').val();
            if(selectedSurvey != undefined && selectedSurvey != "" ){
            	$("#surveyId").prop("readonly","").val(selectedSurvey);
            	$("#surveyId").prop("readonly","readonly");
            }
            else{
            	alert("Please select one survey !");
            	valid = false;
            	return valid;
            }
            	
    
	if(valid)
 	  $("#surveyContainer").hide();
    return true;
}	
$(document).ready(function() {  
	
$('#submitButton').click(function() {
	return fetchCriticalSubmissions();
	}
); 
}); // End of document ready
</script>
 <style type="text/css">
td a {
	border: 1px solid #aaa;
	background-color: #fafafa;
	color: #123456;
	margin: 2px;
}

#controlpanelform {
width: 50%;
display: inline-block;
}

fieldset{
	display :inline;
	width : 90%;
    background-color: grey;
    border-style: none;
    border-top: solid;
}
legend {
    background-color: grey;
    color: white;
    white-space: nowrap;
    overflow: visible;
  }
#controlpanelform input {
	border: none;
    font-family: "Trebuchet MS", Arial, Helvetica, sans-serif;
    color: #525252;
    height: 25px;
    line-height:15px;
    margin-bottom: 16px;
    margin-right: 6px;
    margin-top: 2px;
    outline: 0 none;
    padding: 5px 0px 5px 5px;
    width: 70%;
    border-radius: 2px;
    -webkit-border-radius: 2px;
    -moz-border-radius: 2px;
    -moz-box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.075);
    background: "white";
}

#surveys {
	font-family: "Trebuchet MS", Arial, Helvetica, sans-serif;
	border-collapse: collapse;
	width: 100%;
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
<br>
<form id="controlpanelform" class="dark-matter" method="post">
<fieldset>
    <legend>  Submissions for Survey ID  </legend><br/>
    <label>Survey ID : </label><input type="text" id="surveyId" name="surveyId"><button class="button" onclick="displaySurveys()" type="button">Browse Surveys ...</button>
  <br/><br/>
<button class="button" id="submitButton">Submit</button>
 </fieldset>
</form>
<div id="surveyContainer" style="display:none;position: absolute;	width: 50%;left: 25%;top: 25%;margin: 0 auto;background-color: white;">
<table id="surveys">
  <tr>
    <th>ID</th>
    <th>Description</th>
    <th>Form</th>
    <th></th>
  </tr>
</table>
<input id="selectbutton" class="button" type="button" onclick="insertIntoField()" value="Done">&nbsp;&nbsp;&nbsp;&nbsp;<input id="cancelbutton" class="button" type="button" value="Cancel">
</div>
<div id="submissionstable"></div><br/><br/>
<iframe name="opentarget" style="display:none"></iframe>
<cq:include path="toolbar" resourceType="foundation/components/parsys"/>