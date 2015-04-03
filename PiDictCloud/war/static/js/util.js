/**
* Returns an XMLHttp instance to use for asynchronous
* downloading. This method will never throw an exception, but will
* return NULL if the browser does not support XmlHttp for any reason.
* @return {XMLHttpRequest|Null}
*/
function createXmlHttpRequest() {
 try {
   if (typeof ActiveXObject != 'undefined') {
     return new ActiveXObject('Microsoft.XMLHTTP');
   } else if (window["XMLHttpRequest"]) {
     return new XMLHttpRequest();
   }
 } catch (e) {
   changeStatus(e);
 }
 return null;
};

/**
* This functions wraps XMLHttpRequest open/send function.
* It lets you specify a URL and will call the callback if
* it gets a status code of 200.
* @param {String} url The URL to retrieve
* @param {Function} callback The function to call once retrieved.
*/
function downloadUrl(url, type, data, callback) {
 var status = -1;
 var request = createXmlHttpRequest();
 if (!request) {
   return false;
 }

 request.onreadystatechange = function() {
   if (request.readyState == 4) { 
	   removeLoader();
       status = request.status;    
       if (status == 200) {
    	   callback(request.responseText);    	   
       }else
       {
    	   alert('Status response from server '+status);
       }
   }
 }
 request.open(type, url, true);
 if (type == "POST") {
  request.setRequestHeader("Content-type", "application/x-www-form-urlencoded;charset=UTF-8");
  request.setRequestHeader("Content-length", data.length);
  request.setRequestHeader("Connection", "close"); 
  request.timeout = 30000;//30s
  request.ontimeout = function () { removeLoader();alert('Timeout waiting for response from server!') ; }
 }

 try {
   createLoader();
   request.send(data);
 } catch (e) {
   changeStatus(e);
 }
};

function downloadScript(url) {
  var script = document.createElement('script');
  script.src = url;
  document.body.appendChild(script);
}

var isloading = false;
function createLoader()
{
    if(!isloading)
    {
        isloading = true;
        $("body").addClass("loading");
    }
}

function removeLoader()
{
    if(isloading)
    {
        isloading = false;
        $("body").removeClass("loading");
    }
}

function openOffersDialog(prospectElementID) {
	$('#' + prospectElementID).css('display','block');
        $('#' + prospectElementID).animate({'left':'30%'},100);
}


function closeOffersDialog(prospectElementID) {
	$(function($) {
		$(document).ready(function() {
			$('#' + prospectElementID).css('position','absolute');
			$('#' + prospectElementID).animate({'left':'-100%'}, 100, function() {
				$('#' + prospectElementID).css('position','fixed');
				$('#' + prospectElementID).css('left','100%');
				
			});
		});
	});
}
