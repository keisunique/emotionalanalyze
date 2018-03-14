
//单位转换工具

function changeUnit(number){
	if(number==null){
		return "0";
	}
	if(number == ""){
		return "0";
	}
	if(number == 0 || number == "0"){
		return "0";
	}
	if(number < 1024){
		return parseFloat(number).toFixed(2)+"B";
	}
	else if(number>=1024 && number < 1048576){
		return parseFloat(number/1024).toFixed(2)+"K";
	}
	else if(number < 1073741824 && number >= 1048576){
		return parseFloat(number/1048576).toFixed(2)+"M";
	}
	else if(number < 1099511627776 && number >= 1073741824){
		return parseFloat(number/1073741824).toFixed(2)+"G";
	}
	else{
		return parseFloat(number/1099511627776).toFixed(2)+"T";
	}	
}