<%@taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0" %>
<%@include file="/libs/foundation/global.jsp"%>
<cq:defineObjects/>
<cq:includeClientLib categories="cq.jquery"/>
<style>
<!--
form {
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
  
input[type="text"] {
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
-->
</style>
<script>
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
		
	function displaySurveys(currentInd){
		window.currentIndex = currentInd;
	fetchSurveys();
	$("#surveyContainer").show();
	return false;
	}

	function insertIntoField(){
		var valid = true;
		var selectedSurvey = $('input[name=radiosurvey]:checked').val();
	            if(selectedSurvey != undefined && selectedSurvey != "" ){
	            	$("#surveyId"+window.currentIndex).val(selectedSurvey);
	            	$("#surveyId"+window.currentIndex).prop("readonly","readonly");
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
	
$(document).ready(function(){

	var index = 1;
    updateCount();
     var failure = function(err) {
             alert("Unable to retrive data "+err);
     };

    var url = "/bin/fp/repgen/initialize";
	var minbtnsrc = '<%=currentDesign.getPath() +"/images/buttonminus.png"%>';
    var plzbtnsrc = '<%=currentDesign.getPath() +"/images/buttonplus.png"%>';
    $("[id*='addb']").click(addHandler); 

    function addHandler() { 
    	$('#wbContainer').append('<div id="wsContainer'+index+'"  style="white-space: nowrap;"><label>Survey ID : </label><input type="text" id="surveyId'+index+'" name="surveyId'+index+'">&nbsp;&nbsp;<button class="button" onclick="displaySurveys('+index+')" type="button">Browse Surveys ...</button>&nbsp;&nbsp;<label>Worksheet name : </label><input type="text" id="wsname'+index+'" name="wsname'+index+'" value="WorkSheet-'+index+'"><input id="rawData'+index+'" name="rawData'+index+'" type="checkbox">Raw Data Only<br/></div>');
        index++;
        updateCount();
        console.debug('Add updated Index : '+index);
    }

    $("[id*='deleteb']").click(function() {
        if(index <= 1) return false;
		$('#wsContainer'+(index-1)).remove();
        index--;
        updateCount();
        console.debug('Remove updated Index : '+index);
   }); 
    function updateCount(){
        $("#sheetcount").val(index);
    }

    $("#reportform").submit(function(event){
        var count = $("#sheetcount").val();
        for(var i = 0; i<count;i++){
        	if($("#surveyId"+i).val() == "" || $("#wsname"+i).val() == ""){
        		alert("Please provide valid Worksheet Name and Survey ID");
        		event.preventDefault();
        	}
        }
    });
});
</script>
<div id="formDiv">
<br>
    <form id="reportform" class="dark-matter" target="_blank" action="/bin/fp/repgen/submit" method="post">
<fieldset>
    <legend> Report Workbook </legend><br/>
    <a href="#"><img alt="" src='<%= currentDesign.getPath() +"/images/buttonplus.png"%>' id="addb0" /></a>&nbsp;&nbsp;&nbsp;&nbsp;<a href="#"><img alt="" src='<%=currentDesign.getPath() +"/images/buttonminus.png"%>' id="deleteb0"/></a>
    <br/>
    <div id="wbContainer">
        <div id="wsContainer0" style="white-space: nowrap;">
    <label>Survey ID : </label><input type="text" id="surveyId0" name="surveyId0">&nbsp;&nbsp;
    <button class="button" onclick="displaySurveys(0)" type="button">Browse Surveys ...</button>
    <label>Worksheet Name : </label><input type="text" id="wsname0" name="wsname0" value="WorkSheet-0">
    <input id="rawData0" name="rawData0" type="checkbox">Raw Data Only
    <br/>
        </div>
    </div>
    <br/>
    <input type="hidden" id="sheetcount" name="sheetcount">
    <button class="button" id="download">Download</button>
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

</div>