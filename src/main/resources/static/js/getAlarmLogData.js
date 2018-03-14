function  recursionJSON(data) {
	        	
    if(typeof(data) == "object") {   	
        $.each(data, function(i, n){ 
        	var temp = "<tr><td>"+n.alarm_name+"</td><td>"+n.agentip+"</td><td>"+n.trigger_time+"</td><td>"+n.alarm_level+"</td><td>"+n.alarm_classfiy+"</td>";
        	
        	$.each(n.alarm_condition, function(j,k) {
        		
        		temp += "<td>"+k+"</td>";
        	});
        	temp += "</tr>";
        
            $("#tb").append(temp);
        })              
    }
};
        	
function getDate(){
	$.ajax({
		
		url: "http://localhost:8080/flowvisualization/getalarmlog",
		type: "GET",
		headers: {
			Accept: "application/json"
		},
		success: function(data,textStatus){
			console.log(data);
			recursionJSON(data);				
		},
		error: function(data,textStatus,errorThrown){
			console.log("error");
		},			
	})
}

$(document).ready(function(){  
    getDate();
});  