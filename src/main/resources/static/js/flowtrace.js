
//获取select
var engine = $('select[name="EngineModel[]"]');
//页面加载，初始赋值select选项   
function seletcInit(){
	$.get('/flowvisualization/getSelectDatas', function (engineModels) {
        engines = eval(engineModels);
       // console.log(engines);
        for (var i = 0; i < engines.length; i++) {
            engine.append('<option>' + engines[i] + '</option>');
        }
    })
}
seletcInit();
//for (var i in engines) {
//          engine.append('<option>' + engines[i] + '</option>');
//      }
  $('#inputModel').on('keyup', function(){ 
	 var str = $(this).val();
	 if(str.substr(str.length-1,1) == ','){
		 seletcInit();
	 } 
 })
//绑定值至select（可用于修改） 
if ('<%=order.EngineModel%>') {
          engine.val('<%=order.EngineModel%>');
      }
//select的change事件，选中的值赋给input
$('select[name="EngineModel[]"]').change(function() {
		var inputVal = $('#inputModel').val();
		if(inputVal){
			inputVal = inputVal + document.getElementById('SelectModel').options[document.getElementById('SelectModel').selectedIndex].value;
		}else{
			inputVal = document.getElementById('SelectModel').options[document.getElementById('SelectModel').selectedIndex].value;
		}
		$('#inputModel').val(inputVal);
      })

//可编辑select具体实现
var focus = false;
      $("#inputModel").focus(function () {
          focus = true;
          $(this).next().css('display', 'block');
      }).blur(function () {
          if (focus) {
              $(this).next().css('display', 'none');
          }
      }).keyup(function () {
          var queryCondition = $("#inputModel").val().toLowerCase();
          console.log(queryCondition);
          if (queryCondition.length != 0) {
              engine.empty();
              for (var i in engines) {
                  if (engines[i].toLowerCase().indexOf(queryCondition) != -1) {
                      engine.append('<option>' + engines[i] + '</option>');
                  }
              }
             
          } else {
              for (var i in engines) {
                  engine.append('<option>' + engines[i] + '</option>');
              }
          }
      }).next().mousedown(function () {
          focus = false;
      }).change(function () { 
          $(this).css('display', 'none').prev();
      }); 
function SearchData() {
		
		var s1 = $("input[name='inputModel']").val();
		var s2 = $("input[name='values']:checked").val();
		var s3 = $("input[name='filter']").val();
		//alert(s2);
		
		var s = [];
		//indexOf 方法返回一个整数值，指出 String 对象内子字符串的开始位置。如果没有找到子字符串，则返回 -1。
		if(s1.indexOf(",")>0){s = s1.split(',');}
		
setInterval(function () {
		$.ajax({
			url:"/flowvisualization/getTopFlow",
			//dataType:"json",
			data:$("form").serialize(),
			success:function(data){
				$("#table1").hide();//隐藏下面的table
				console.log(data);
				console.log(s1);console.log(s2);console.log(s3);
				if(data == "success") {
					$.ajax({
						url:"/flowvisualization/getFlowTraceDatas",
						data:$("form").serialize(),
						dataType:"json",
						success:function(data){
							//console.log(data);
							//将keys值循环到表格的标题中（用户输入的keys值）
							var t = "";
							if(s.length>0){
								for(i in s) {
									t += "<th width='100'>"+ toChinese(s[i]) +"</th>";
								}
							}else {
								t = "<th width='100'>"+ toChinese(s1) +"</th>";
							}
							
							//数据不为空，填充表格
							var str = "";
							if(data != "") {
								for(i in data) {
									var keys = [];
									var keyStr = "";
									if((data[i].key).indexOf(",")){keys = (data[i].key).split(",");}
									if(keys.length > 0){
										for(j in keys){
											keyStr += "<td>"+ keys[j] +"</td>";
										}
										<!--<td><a href='#' onclick='TraceFlow(this)'>是</a></td><td><a href='#' onclick='deleteFlow(this)'>删除流</a></td>-->
										str += "<tr class='text-c' id='"+ data[i].agent+ "-"+ data[i].dataSource +"&"+ s1+"-"+ s3 +"'><td>"+ data[i].agent +"</td><td>"+ data[i].dataSource +"</td>"+ keyStr + "<td>"+ changeUnit(toInt(data[i].value)) +"</td></tr>";
									}else{
										str += "<tr class='text-c' id='"+ data[i].agent+ "-"+ data[i].dataSource +"&"+ s1+"-"+ s3 +"'><td>"+ data[i].agent +"</td><td>"+ data[i].dataSource +"</td><td>"+ data[i].keys + "</td><td>"+ changeUnit(toInt(data[i].value)) +"</td></tr>";
									}
								}
							}
							
							//将数据填充到表格
							if(data != "") {
								<!-- <th colspan='2'>是否跟踪流</th>--> 
								$("#result").html("<p  align='center' class ='f-20 text-success'>"+$("#flowtitle").val()+"</p><table class='table table-border table-bordered table-hover table-bg' style=''><thead><tr class='text-c'><th width='150'>节点IP</th><th width='50'>端口</th>"+ t +"<th width='150'>流值 ("+toChinese(s2)+")</th></tr></thead>"
											+ str
											+"</table>");
							}else if(data == "") {
								if(s.length>0){
									$("#result").html("<p  align='center' class ='f-20 text-success'>"+$("#flowtitle").val()+"</p><table class='table table-border table-bordered table-hover table-bg' style=''><thead><tr class='text-c'>"+ t +"<tr class='text-c'><td colspan='"+ s.length +"'>没有数据</td></tr></thead></table>");
								}else{
									$("#result").html("<p  align='center' class ='f-20 text-success'>"+$("#flowtitle").val()+"</p><table class='table table-border table-bordered table-hover table-bg' style=''><thead><tr class='text-c'>"+ t +"<tr class='text-c'><td>没有数据</td></tr></thead></table>");
								}
								
							}
							
						}
					});
				} else if(data == "failure") {
					//console.log("failure");
					var t = "";
					if(s.length>0){
						for(i in s) {
							t += "<th>"+ s[i] +"</th>";
						}
						$("#result").html("<p  align='center' class ='f-20 text-success'>"+$("#flowtitle").val()+"</p><table class='table table-border table-bordered table-hover table-bg'><thead><tr class='text-c'>"+ t +"<tr><td colspan='"+ s.length +"'>没有数据</td></tr></thead></table>");
					}else {
						t = "<th>"+ s1 +"</th>";
						$("#result").html("<p  align='center' class ='f-20 text-success'>"+$("#flowtitle").val()+"</p><table class='table table-border table-bordered table-hover table-bg'><tr class='text-c'>"+ t +"<tr><td>没有数据</td></tr></table>");
					}
					
				}
					//定时器：跟踪topflow
					/* var timesRun = 0;//定时器次数
					var intervalId = setInterval(function (){
						timesRun += 1;
						$.ajax({
							url:"/flowvisualization/getTopFlowDatas",
							data:$("form").serialize(),
							dataType:"json",
							success:function(data){
								console.log(data);
								if(data != null) {
									//将数据填充到表格
								}
							}
						});
					}, 1000); */
					//主动暂停定时器
			},
			error:function(){
				console.log("error");
			}
		});
	//}
},2000); 
}

   function toChinese(t){
	   switch(t){
	   case 'ipsource':
		   return '源IP地址';break;  
	   case 'or:ipsource:ip6source':
		   return '源IP地址';break;    
	   case 'ipdestination':
		   return '目的IP地址';break;
	   case 'or:ipdestination:ip6destination':
		   return '目的IP地址';break;
	   case 'tcpsourceport':
		   return '源TCP端口';break;
	   case 'tcpdestinationport':
		   return '目的TCP端口';break;
	   case 'udpsourceport':
		   return '源UDP端口';break;
	   case 'udpdestinationport':
		   return '目的UDP端口';break;
	   case 'icmpunreachablenet':
		   return 'ICMP不可达网段相应的IP地址';break;
	   case 'arpipsender':
		   return '发送ARP请求的IP地址';break;
	   case 'httpurl':
		   return 'HTTP請求的url地址';break;
	   case 'macsource':
		   return '源物理地址';break;
	   case 'macdestination':
		   return '目的物理地址';break;
	   case 'bps':
		   return '位/秒';break;
	   case 'Bps':
		   return '字节/秒';break;
	   case 'fps':
		   return '帧/秒';break;
	   case 'requests':
		   return '请求/秒';break;
	   }
   }
   function toInt(t){
	   if(parseFloat(t)> 0.01){
	   return parseFloat(t).toFixed(2);
	   }
	   else{
		   return  parseFloat(t).toExponential(2);
	   }
   }
	function clearData() {
		$("#inputModel").val('');
		$("input[type='radio']").attr("checked", false);
		$("#filter").val('');
		window.location.href="/flowvisualization/TopFlow1";
	}
	$('table').mLoading({
	    text:"加载中...",//加载文字，默认值：加载中...
	    html:false,//设置加载内容是否是html格式，默认值是false
	    content:"",//忽略icon和text的值，直接在加载框中显示此值
	    mask:false//是否显示遮罩效果，默认显示
	});	
	$('table').mLoading("hide");//隐藏loading组件
	function searchBigData(t){
			$('table').mLoading("show");//显示loading组件
			var show = $('#backbutton').css('display');
			$('#backbutton').css('display',show =='block'?'none':'block');
			var idValue = t.parentNode.parentNode.id;
			console.log(idValue);
			if("tcp" == idValue){
				$("#inputModel").val('ipsource,ipdestination,tcpsourceport,tcpdestinationport');
				$("input[value='fps']").attr("checked",true);
				//$("#filter").val("tcpflags~.......1.");
				$("#flowtitle").val("TCP传输层流跟踪");
			}
			if("udp" == idValue){
				$("#inputModel").val('ipsource,ipdestination,udpsourceport,udpdestinationport');
				$("input[value='fps']").attr("checked",true);
				$("#flowtitle").val("UDP传输层流跟踪");
				
			}
			if("arp" == idValue){
				$("#inputModel").val('arpipsender');
				$("input[value='requests']").attr("checked",true);
				$("#filter").val("arpoperation=1");
				$("#flowtitle").val("ARP网络层流跟踪");
			}
			if("http" == idValue){
				$("#inputModel").val('httpurl');
				$("input[value='requests']").attr("checked",true);
			}
			if("dns1" == idValue){
				$("#inputModel").val('dnsqname');
				$("input[value='requests']").attr("checked",true);
			}
			if("dns2" == idValue){
				$("#inputModel").val('or:ipsource:ip6source');
				$("input[value='requests']").attr("checked",true);
				$("#filter").val("dnsqr=false");
				$("#flowtitle").val("DNS应用层流跟踪");
			}
			if("dns3" == idValue) {
				$("#inputModel").val('or:ipsource:ip6source');
				$("input[value='requests']").attr("checked",true);
				$("#filter").val("dnsqr=true");
			}
			if("icmp1" == idValue){
				$("#inputModel").val('or:ipdestination:ip6destination');
				$("input[value='fps']").attr("checked",true);
				$("#filter").val("dnsqr=true");
			}
			if("icmp2" == idValue){
				$("#inputModel").val('or:ipdestination:ip6destination,icmpunreachableprotocol');
				$("input[value='fps']").attr("checked",true);
			}
			if("icmp3" == idValue){
				$("#inputModel").val('or:ipdestination:ip6destination,icmpunreachablehost');
				$("input[value='fps']").attr("checked",true);
			}
			if("icmp4" == idValue){
				$("#inputModel").val('or:ipdestination:ip6destination,icmpunreachablenet');
				$("input[value='fps']").attr("checked",true);
				$("#flowtitle").val("ICMP网络层流跟踪");
			}
			if("ip1" == idValue){
				$("#inputModel").val('ipsource');
				$("input[value='bps']").attr("checked",true);
			}
			if("ip2" == idValue){
				$("#inputModel").val('ipdestination');
				$("input[value='bps']").attr("checked",true);
			}
			if("ip3" == idValue) {
				$("#inputModel").val('ipsource,ipdestination');
				$("input[value='Bps']").attr("checked",true);
				$("#flowtitle").val("IP网络层流跟踪");
			}
			if("ipv61" == idValue){
				$("#inputModel").val('ip6source');
				$("input[value='bps']").attr("checked",true);
			}
			if("ipv62" == idValue){
				$("#inputModel").val('ip6destination');
				$("input[value='bps']").attr("checked",true);
			}
			if("ipv63" == idValue){
				$("#inputModel").val('ip6source,ip6destination');
				$("input[value='bps']").attr("checked",true);
			}
			if("ethernet1" == idValue){
				$("#inputModel").val('macsource,macdestination');
				$("input[value='fps']").attr("checked",true);
				$("#flowtitle").val("ARP网络层流跟踪");
			}
			if("ethernet2" == idValue){
				$("#inputModel").val('macsource,oui:macsource:name');
				$("input[value='fps']").attr("checked",true);
				$("#filter").val("macdestination=FFFFFFFFFFFF");
			}
			if("vxlan" == idValue){
				$("#inputModel").val('vni');
				$("input[value='bps']").attr("checked",true);
			}
			if("nvgre" == idValue){
				$("#inputModel").val('grevsid');
				$("input[value='bps']").attr("checked",true);
			}
			if("genve" == idValue) {
				$("#inputModel").val('genevevni');
				$("input[value='bps']").attr("checked",true);
			}
			
			SearchData();
		}
		function TraceFlow(t){
			var ids = t.parentNode.parentNode.id;
			var id = ids.split("&")[0];
			var keys = ids.split("&")[1];
			window.location.href = "/flowvisualization/FlowTraceMap1?ids="+id+"&keysFliter="+keys;
		}
		
		function deleteFlow(x){
	/*		var ids = x.parentNode.parentNode.id;
			var keys = ids.split("&")[1];
			var inputModel = keys.split("-")[0];
			var filter = keys.split("-")[1]; 
*/
			var inputModel = $("input[name='inputModel']").val();
			var values = $("input[name='values']").val();
			var filter = $("input[name='filter']").val();
			$.ajax({
				url:"/flowvisualization/deleteFlow",
				data:{"inputModel":inputModel, "filter":filter},
				success:function(data){
					console.log(data);
					if(data == "success"){
						//alert("删除成功！");
						var show = $('#backbutton').css('display');
						$('#backbutton').css('display',show =='block'?'none':'block');
						window.location.href="/flowvisualization/FlowTrace1";
					}
					if(data == "failure"){
						layer.msg('删除失败',{icon: 2,time:1500});
					}
				}
			}); 
		}
