function getCurrentUrl(){
    	var baseUrl = $("meta[name=baseUrl]").attr("content")
    	return baseUrl + "/api/report";

}







function init(){
$("#report-link").addClass('active');

}

$(document).ready(init);