function fetchSurveys(){
		 $.ajax("/bin/fp/survey/list", {
		type: "GET",
   success: function(data, status, xhr) {
	 	  $('#surveys tr').not(':first').remove();
       var surData = data.surveys;
       try {
     	  jQuery.each(surData, function(i,data) {
               $("#surveys").append("<tr><td>" + data.surveyId + "</td><td>" + data.description + "</td><td>" + data.form + "</td><td><input type='checkbox' name=" + data.surveyId + " value=" + data.surveyId + " id=" + data.surveyId + "/></td></tr>");
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

function populateDatasetsForKey(data, key){
	 var datasetsCollection = [];
	 for (var surveyCount = 0; surveyCount < data.length; surveyCount++) {

		 var barLabels = [];
		var dataArr = [];
		var bgColor = [];
		var brColor = [];
		
		var processEGAPKeywise = false;
		var processYNKeywise = false;
		for (var props in data[surveyCount][key]){
			if(data[surveyCount][key].hasOwnProperty('Poor')||data[surveyCount][key].hasOwnProperty('Excellent')||data[surveyCount][key].hasOwnProperty('Good')||data[surveyCount][key].hasOwnProperty('Average')){
				processEGAPKeywise =  true;
		}else if(data[surveyCount][key].hasOwnProperty('Yes')||data[surveyCount][key].hasOwnProperty('No')){
				processYNKeywise = true;
		}else{
			barLabels.push(props);
			dataArr.push(data[surveyCount][key][props]);
			bgColor.push(makeRandomColor());
			brColor.push(makeRandomColor());
		}
		}
		if(processEGAPKeywise){
			var keyInObjectCount = 0, keyCount;
		    for (keyCount in data[surveyCount][key]) {
		        if (data[surveyCount][key].hasOwnProperty(keyCount)) keyInObjectCount++;
		    }
		    //alert("Key count found : "+keyInObjectCount);
		    var valuesSequence = ["Excellent","Good","Average","Poor"];
		    var colorSequence = ["white","green","yellow","red"];
		    
		    for (var ratings = 0; ratings < valuesSequence.length; ratings++) {
		    	if (data[surveyCount][key].hasOwnProperty(valuesSequence[ratings])){
		    	barLabels.push(valuesSequence[ratings]);
		    	dataArr.push(data[surveyCount][key][valuesSequence[ratings]]);
   			bgColor.push(colorSequence[ratings]);
   			brColor.push(colorSequence[ratings]);
		    	} else {
   		    	barLabels.push(valuesSequence[ratings]);
   		    	dataArr.push(0);
       			bgColor.push(colorSequence[ratings]);
       			brColor.push(colorSequence[ratings]);
   		    	}
			}
		    
		}
		
		if(processYNKeywise){
			var keyInObjectCount = 0, keyCount;
		    for (keyCount in data[surveyCount][key]) {
		        if (data[surveyCount][key].hasOwnProperty(keyCount)) keyInObjectCount++;
		    }
		    //alert("Key count found : "+keyInObjectCount);
		    var valuesSequence = ["Yes","No"];
		    var colorSequence = ["green","red"];
		    
		    for (var ratings = 0; ratings < valuesSequence.length; ratings++) {
		    	if (data[surveyCount][key].hasOwnProperty(valuesSequence[ratings])){
		    	barLabels.push(valuesSequence[ratings]);
		    	dataArr.push(data[surveyCount][key][valuesSequence[ratings]]);
   			bgColor.push(colorSequence[ratings]);
   			brColor.push(colorSequence[ratings]);
		    	} else {
   		    	barLabels.push(valuesSequence[ratings]);
   		    	dataArr.push(0);
       			bgColor.push(colorSequence[ratings]);
       			brColor.push(colorSequence[ratings]);
   		    	}
			}
		    
		}
		datasetsCollection[surveyCount] = {};
		datasetsCollection[surveyCount].label = key;
		datasetsCollection[surveyCount].labels = barLabels;
		datasetsCollection[surveyCount].backgroundColor = bgColor;
		datasetsCollection[surveyCount].borderColor = brColor;
		datasetsCollection[surveyCount].borderWidth = 5;
		datasetsCollection[surveyCount].data = dataArr;
		
	 }
	 return datasetsCollection;
	}
var failure = function(err) {
    alert("Unable to retrive data "+err);
};
$(document).ready(function(){


     
     $('#cancelbutton').click(function() {
   	  $("#surveyContainer").hide();
     });
     
     
     $(document).keyup(function(e) {
   	     if (e.keyCode == 27) { 
   	    	 $("#surveyContainer").hide();
   	    }
   	});
     
   function makeRandomColor(){
    	  return '#'+Math.random().toString(16).substr(2,6);
    	}
    
  
});