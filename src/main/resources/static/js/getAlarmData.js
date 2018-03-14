function  recursionJSON(data) {
    	
    if(typeof(data) == "object") {
    	
        $.each(data, function(i, n){ 
        	
        	var temp = "<tr><td>"+n.alarm_definition.alarm_name+"</td><td>"+n.agentip+"</td><td>"+n.port+"</td><td>"+n.status+"</td><td>"+n.alarm_definition.alarm_describe+"</td><td>"+n.alarm_definition.alarm_classfiy+"</td><td>"+n.alarm_definition.alarm_level+"</td><td>"+n.alarm_definition.alarm_type+"</td>";
        	
        	$.each(n.alarm_condition, function(j,k) {
        		
        		temp += "<td>"+k+"</td>";
        	});
        	$.each(n.alarm_receiver, function(j,k) {
        		
        		temp += "<td>"+k+"</td>";
        	});
        	
        	if(n.status==1){
        		temp += "<td><button id=\"pause\" name=\" "+n.alarm_definition.alarm_name+ " \" type=\"button\" class=\"btn btn-default\">暂停</button><button id=\"delete\" name=\" "+n.alarm_definition.alarm_name+ " \" type=\"button\" class=\"btn btn-default\">删除</button></td>";
        	}else{
        		temp += "<td><button type=\"button\" class=\"btn btn-default\">启动</button><button type=\"button\" class=\"btn btn-default\">删除</button></td>"
        	}      
        	temp += "</tr>";        	
           	$("#tb").append(temp);
        })              
    }
};
			

function getDate(){
	$.ajax({
		
		url: "http://localhost:8080/flowvisualization/getalarm",
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