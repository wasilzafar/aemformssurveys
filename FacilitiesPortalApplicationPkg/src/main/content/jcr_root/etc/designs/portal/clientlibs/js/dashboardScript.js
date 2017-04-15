window.allSurveysData = [];
window.allCharts = [];
$('#submitButton').click(function(event){ 
	event.preventDefault();
    $("#containerChart").html("");
	var isValid = validateSelectedSurveys();
	if(isValid){
	fetchAllSurveysData();
	populateDashboard(window.allSurveysData);
	}
	$('#containerChart').show();
	return false;
});

function validateSelectedSurveys(){
	var formNameRef;
	var valid = true;
	var table = $("table tbody");
	table.find('tr').not(':first').each(function (i) {
        var $tds = $(this).find('td'),
        surveyId = $tds.eq(0).text(),
            formName = $tds.eq(2).text(),
            checked = $($tds).find('input:checkbox:checked').length > 0;
            if(checked == true && (formNameRef == undefined || formNameRef == ""))
            	formNameRef = formName;
            else if(checked == true && formNameRef != formName ){
            	alert("Please only select surveys conducted on same form !");
            	valid = false;
            	return valid;
            }
            	
    });
	if(valid)
 	  $("#surveyContainer").hide();
    return true;
}

function fetchAllSurveysData(){
	window.allSurveysData = [];
	var selectedSurveys = getSelectedSurveys();
	for (var count = 0; count < selectedSurveys.length; count++) {
	    $.ajax("/bin/fp/dbdfetcher/submit?surveyId="+selectedSurveys[count], {
            type: "GET",
            async: false,
            cache: false,
	        success: function(data, status, xhr) {
	        	},
	        error: function(xhr, status, err) {
	                failure(err);
	            }
	        }).done(function(data){
	        	window.allSurveysData[count] = data.allformparameters;	        	
	        });
	}
}

function getSelectedSurveys(){
	var selectedSurveys = new Array();
	var table = $("table tbody");
	table.find('tr').not(':first').each(function (i) {
        var $tds = $(this).find('td'),
        surveyId = $tds.eq(0).text(),
            checked = $($tds).find('input:checkbox:checked').length > 0;
            if(checked == true)
            	selectedSurveys.push(surveyId);
            	
    });
	return selectedSurveys;
}

function populateDashboard(data) {
	for (chart in window.allCharts) {
		chart.destroy;
	}
	var ctxArray = [];
    $("#containerChart").html("");
    try {
        var i = 0;
    	for (var key in data[0]) {
    		console.log(key + " -> " + data[0][key]);
    		var retData = populateDatasetsForKey(data,key);
    		//alert("Data : "+retData);
    		if(i%2 === 0)
    		$("#containerChart").append('<div style="border: 2px solid white;float: left;height: 50%;min-width: 45%"><canvas id='+key.replace(/[^A-Z0-9]+/ig, "_")+'></canvas></div>');
    		else
    			$("#containerChart").append('<div style="border: 2px solid white;float: left;height: 50%;min-width: 45%"><canvas id='+key.replace(/[^A-Z0-9]+/ig, "_")+'></canvas></div><br/><br/><br/><br/>');	
    		ctxArray[i] = document.getElementById(key.replace(/[^A-Z0-9]+/ig, "_"));
    		var dataBar = {
    			    labels: retData[0].labels,
    			    datasets: retData
    			};
    		var myChartValue = new Chart(ctxArray[i], {
    			type: 'bar',
    			data : dataBar,
    			options : {
    				legend: {
    			        display: false
    			    },
    				title : {
					display : true,
					text : key,
                    fontFamily:"'Trebuchet MS','Helvetica Neue', 'Helvetica', 'Arial'",
                    fontSize:20
				},
    				 scales: {
    					 xAxes: [{
			                ticks: {
			                	fontSize:20,
		                    min: 0,
		                    stepSize: 1
		                },
		                gridLines: {
		                	display:true,
		                	color : "white"
		                },
		                scaleLabel: {
		                    display: false,
		                    labelString: 'Selected values in form ',
			                    fontColor:'white',
			                    fontFamily:"'Trebuchet MS','Helvetica Neue', 'Helvetica', 'Arial'",
			                    fontSize:20
		                  }
		            }],
    			            yAxes: [{
    			                ticks: {
     			                	fontSize:20,
    			                    min: 0,
    			                    stepSize: 1
    			                },
    			                gridLines: {
    			                	display:true,
    			                	color : "white"
    			                },
    			                scaleLabel: {
    			                    display: false,
    			                    labelString: 'Selected values count',
    			                    fontColor:"white",
    			                    fontFamily:"'Trebuchet MS','Helvetica Neue', 'Helvetica', 'Arial'",
    			                    fontSize:20
    			                  }
    			            }]
    			        }
    			}
    		});
    		myChartValue.update();
    		window.allCharts.push(myChartValue);
    		i++;
    	}
    } catch(err) {
        failure(err);
    }


}

function makeRandomColor(){
	  return '#'+Math.random().toString(16).substr(2,6);
	}