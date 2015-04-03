/*
copyright by phan nguyen
*/
		//listener for mouse event
		window.addEventListener("mouseup", getselectedtext, false);
		window.addEventListener("mousedown", gestdown, false);

		//global variable
		var curMouseX;
		var curMouseY;
		var screenW = window.innerWidth;
		var screenH = window.innerHeight;
		var isMouseDown = false;
		var isPopShow = false;
		var myDivId = 'pidict_div__chrome_extension_';
		var myGoogleTextAreaId = myDivId+'txtgoogletranslate';
		var myGoogleMoveAreaId = myDivId+'move';
		var myTextAreaId = myDivId+'txtmytranslate';
		var myChkSaveId = myDivId + 'chksave';
        var myChkSaveRefId = myDivId + 'chksaveref';
		var curTxt= '';
		//var isTabCreated = false;
		function getselectedtext(event)
		{
			if (event.which)
				  button= (event.which < 2) ? "LEFT" :
					 ((event.which == 2) ? "MIDDLE" : "RIGHT");
			if (button=="LEFT"){
				document.oncontextmenu = null;
				if(isMouseDown && event.ctrlKey==1){
					var txt = '';
					if (window.getSelection)
					{
						txt = window.getSelection();
					}
					else if (document.getSelection)
					{
						txt = document.getSelection();
					}
					else if (document.selection)
					{
						txt = document.selection.createRange().text;
					}
					if(txt!='')
					{
						//alert('send '+txt);
						var action_value = 'translate';
						//var additionalInfo = {
						//  "action": action_value,
						//  "searchfor": txt.toString()
						//};
						//chrome.extension.connect().postMessage(additionalInfo);
						if(event.offsetX || event.offsetY) {
							curMouseX=event.clientX + document.body.scrollLeft;
							curMouseY=event.clientY + document.body.scrollTop;							
						}
						else {
							curMouseX=event.pageX;
							curMouseY=event.pageY;							
						}
						//alert('X '+ curMouseX + ' Y '+ curMouseY);
						
						//alert('X '+ curMouseX + ' Y '+ curMouseY);
						curTxt = txt.toString();
						//alert(txt.toString());
						//chrome.extension.sendRequest({action : action_value, searchfor : txt.toString()}, createNShowPopup);
                        chrome.runtime.sendMessage({action : action_value, searchfor : txt.toString()},createNShowPopup);

					}
				}
		   }
		   isMouseDown = false;
  	    }

		function createNShowPopup(data) {
			//alert(data.status);
            //alert('OKKKKKKKKK');
			var mainDiv = document.createElement('div');
			mainDiv.className = "pidict_div__chrome_extension_popup";
			mainDiv.id = myDivId;
			mainDiv.innerHTML = "<div id=\""+myGoogleTextAreaId+ "\"></div>" +
								"<div class=\"pidict_div__chrome_extension_move\" id=\""+myGoogleMoveAreaId+ "\">"+
								"<br/><input type=\"checkbox\" id=\""+myChkSaveId+ "\"><b> Save it ?</b>" +
                                "<br/><input type=\"checkbox\" id=\""+myChkSaveRefId+ "\"><b style=\"font-size:12px\"> And ref ?</b>" +
								"<br/><div class=\"pidict_div__chrome_extension_ex\" >PiDict</div></div>"

			//alert(mainDiv.innerHTML);
			document.body.appendChild(mainDiv);					
			detectPositionForDivTag(curMouseX,curMouseY);
			document.getElementById(myDivId).style.top = curMouseY +'px'; // 
			document.getElementById(myDivId).style.left = curMouseX +'px'; //
			document.getElementById(myGoogleMoveAreaId).onmousedown = function() {dragStart(event,myDivId)}
			document.getElementById(myGoogleTextAreaId).innerHTML = "<textarea class=pidict_div__chrome_extension_textarea name=newname id=\""+myTextAreaId+ "\" rows=\"5\" cols=\"25\" >" + data.text.toString() + "</textarea> ";
			isPopShow = true;			
			//isTabCreated = data.tab;
			//alert(data.text.toString());
		}	
		
		function gestdown(event) {
			if (event.which)
				  button= (event.which < 2) ? "LEFT" :
					 ((event.which == 2) ? "MIDDLE" : "RIGHT");
		    if (button=="LEFT"){
				isMouseDown = true;				
				if(isPopShow)
				{
					var x;
					var y;
					if(event.offsetX || event.offsetY) {
						x = event.clientX + document.body.scrollLeft;
						y = event.clientY + document.body.scrollTop;							
					}
					else {
						x = event.pageX;
						y = event.pageY;							
					}
					if(IsBelongDivTagErea(x,y)){
						isMouseDown = false;
						//alert("belong");
					}
					else{
					//alert("not belong");
						if(document.getElementById(myChkSaveId).checked)
						{
                            var txtSend = document.getElementById(myTextAreaId).value.toString()!=''?document.getElementById(myTextAreaId).value.toString():document.getElementById(myGoogleTextAreaId).value.toString();
                            var curURL = "";
                            if(document.getElementById(myChkSaveRefId).checked)
                                curURL = document.URL;
							chrome.runtime.sendMessage({action : 'save', phrase: curTxt.toString(),category:'',vimeaning:txtSend.toString(),enmeaning:'',cloudstatus:'No',ref:curURL});

                            var existDiv = document.getElementById(myDivId);
                            if(existDiv)
                                document.body.removeChild(existDiv);
                            isPopShow = false;
						}
						else
						{
						//alert("not checkbox");
							var existDiv = document.getElementById(myDivId);
							if(existDiv)
								document.body.removeChild(existDiv);						
							isPopShow = false;						
						}
					}
				}
			}
			
		}

		function detectPositionForDivTag(x,y) {
			var myDiv = document.getElementById(myDivId);			
			//alert("detect");
			if(x + myDiv.offsetWidth > screenW + document.body.scrollLeft ){
				curMouseX =  screenW - myDiv.offsetWidth - 20 + document.body.scrollLeft ;				
			}
			//alert("detect middle");
			if(y + myDiv.offsetHeight > screenH + document.body.scrollTop){
				curMouseY =  screenH - myDiv.offsetHeight - 20 + document.body.scrollTop;				
			}
			//alert("detect end");
		}
		function IsBelongDivTagErea(x,y)
		{
			if(isPopShow)//just detect position when popup shown
			{
				//alert('X '+ curMouseX + ' Y '+ curMouseY);
				var myDiv = document.getElementById(myDivId);
				//alert("check");
				if(curMouseX < x && x < curMouseX + myDiv.offsetWidth && curMouseY < y && y < curMouseY + myDiv.offsetHeight )
					return true;				
				return false;
			}
		}
		/////////////////////////

