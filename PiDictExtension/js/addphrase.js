//alert('ce')
String.prototype.trim = function() {
    return this.replace(/^\s+|\s+$/g, "");
};

chrome.extension.getBackgroundPage().funcAddWord =  addNewDataRow;
//get db object from background page
var PiDictDB = chrome.extension.getBackgroundPage().PiDictDB;
displayAllRecordsInDB();
var tempText='';
var codeDel = '24680';

function displayAllRecordsInDB() {

    if(PiDictDB)
    {
        PiDictDB.transaction(function(tx) {
            tx.executeSql("SELECT * FROM tblPhraseEntities", [], function(tx, result) {
                for (var i=0; i < result.rows.length; i++) {
                    var rowdata = result.rows.item(i);
                    addNewDataRow(rowdata.id,rowdata.cloudstatus,rowdata.phrase,rowdata.category,rowdata.vimeaning,rowdata.enmeaning,rowdata.ref,rowdata.modifiedOn);
                }
            });
        });
    }
}


function updateDataIntoDB(id,colname,value)
{
    if(PiDictDB)
    {
        chrome.extension.getBackgroundPage().updateRecord(id,colname,value);
    }
}

function addPhraseIntoDB(phrase,category,vimeaning,enmeaning,cloudstatus)
{
    if(PiDictDB)
    {
        chrome.extension.getBackgroundPage().addNewPhrase(phrase,category,vimeaning,enmeaning,cloudstatus);
    }
}

function deleteDataIntoDB(ids,isDelAll)
{
    if(PiDictDB)
    {
        chrome.extension.getBackgroundPage().deleteRecords(ids,isDelAll,deleteRowSuccessCallback);
    }
}

function addNewDataRow(idkey, cloudstatus, phrase, category, vimeaning, enmeaning, ref, lastmodified) {
    //alert(phrase + vimeaning);
    var newrow = document.createElement("tr");
    var colNo = document.createElement("td");
    var colSelect = document.createElement("td");
    var colCloudStatus = document.createElement("td");
    var colPhrase = document.createElement("td");
    var colCategory = document.createElement("td");
    var colViMeaning = document.createElement("td");
    var colEnMeaning = document.createElement("td");
    var colLastMod = document.createElement("td");
    //create checkbox for select column
    var labelEle = document.createElement("label");
    var inputEle = document.createElement("input");
    inputEle.type = "checkbox";
    inputEle.name = "checks";
    labelEle.appendChild(inputEle);
    colSelect.appendChild(labelEle);
    applyCssToCheckbox(labelEle);

    colPhrase.colname = 'phrase';
    colPhrase.cols = 20;
    //colPhrase.key = idkey;
    colPhrase.ondblclick=changeContent;

    colViMeaning.colname = 'vimeaning';
    colViMeaning.cols = 35;
    //colViMeaning.key = idkey;
    colViMeaning.ondblclick=changeContent;

    colCategory.colname = 'category';
    colCategory.cols = 20;
    //colCategory.key = idkey;
    colCategory.ondblclick=changeContent;

    colEnMeaning.colname = 'enmeaning';
    colEnMeaning.cols = 35;
    //colEnMeaning.key = idkey;
    colEnMeaning.ondblclick=changeContent;

    newrow.key = idkey;
     if(ref =="")
        colNo.innerText = $("#tbodySavedWordList").children().length+1;
    else{
         $("<a>")
             .attr('href',ref)
             .attr('target','_blank')
             .text($("#tbodySavedWordList").children().length+1).appendTo(colNo);//no with link

     }
    colCloudStatus.innerText = cloudstatus;
    colPhrase.innerText = phrase.replace(/(\r\n|\n|\r)/gm,' ');
    colCategory.innerText = category.replace(/(\r\n|\n|\r)/gm,' ');
    colViMeaning.innerText = vimeaning.replace(/(\r\n|\n|\r)/gm,' ');
    colEnMeaning.innerText = enmeaning.replace(/(\r\n|\n|\r)/gm,' ');
    colLastMod.innerText = lastmodified;
    colLastMod.colname = 'lastmodified';

    newrow.appendChild(colNo);
    newrow.appendChild(colSelect);
    newrow.appendChild(colCloudStatus);
    newrow.appendChild(colPhrase);
    newrow.appendChild(colCategory);
    newrow.appendChild(colViMeaning);
    newrow.appendChild(colEnMeaning);
    newrow.appendChild(colLastMod);
    $("#tbodySavedWordList").append(newrow)
}
// Shorthand for document.querySelector.
function select(selector) {
    return document.querySelector(selector);
}


function changeContent(e)
{
    e = e || window.event;
    var tablecell = e.target || e.srcElement;
    //alert(tablecell);
    //tablecell.innerHTML = "<INPUT type=text name=newname onBlur=\"javascript:submitNewName(this);\" value=\""+tablecell.innerHTML+"\">";
   // tablecell.innerHTML = "<textarea name=newname rows=\"5\" cols=\"50\" onBlur=\"submitNewName()\">" + tablecell.innerText + "</textarea>";
    var textareaEle = document.createElement("textarea");
    textareaEle.rows = 5;
    textareaEle.cols  = tablecell.cols;
    textareaEle.onblur = submitNewText;
    textareaEle.innerText = tablecell.innerText.trim();
    tempText = textareaEle.innerText;
    //alert(tempText);
    tablecell.innerText="";
    tablecell.appendChild(textareaEle);
    tablecell.firstChild.focus();
}

function submitNewText(e) {
    e = e || window.event;
    var textfield = e.target || e.srcElement;
    //alert(textfield.value);
    if(tempText!=textfield.value.trim())
    {
        //alert(textfield.value);
        //updateDataIntoDB(textfield.parentNode.key,textfield.parentNode.colname,textfield.value.trim());
        updateDataIntoDB($(textfield).closest('tr').attr('key'),textfield.parentNode.colname,textfield.value.trim());
        var row = textfield.parentNode.parentNode;
        if(row && row.lastChild && row.lastChild.colname == 'lastmodified')
        {
            row.lastChild.innerText = (new Date()).toLocaleString();
        }
    }
    textfield.parentNode.innerText= textfield.value.replace(/(\r\n|\n|\r)/gm,' ').trim();
    tempText='';
}

/************************************************/
$(document).ready(function() {
    $("#tab2").hide(); // Initially hide all content
    $("#tab3").hide(); // Initially hide all content
    $("#tab4").hide(); // Initially hide all content
    $("#tabs li:first").attr("id","current"); // Activate first tab
    $("#tab1").show(); // Show first tab content

    $('#tabs a').click(function(e) {
        e.preventDefault();
        $("#tab1").hide(); // Initially hide all content
        $("#tab2").hide(); // Initially hide all content
        $("#tab3").hide(); // Initially hide all content
        $("#tab4").hide(); // Initially hide all content
        $("#tabs li").attr("id",""); //Reset id's
        $(this).parent().attr("id","current"); // Activate this
        $('#' + $(this).attr('title')).show(); // Show content for current tab
    });

    $("#btndel").click(function () {
        if($('#code').val()==codeDel)
        {
            var arrayIds = [];
            $('#tbodySavedWordList').find('input:checkbox, input:radio').each(function () {
                if( $(this).attr('checked')){
                    arrayIds.push($(this).closest('tr').attr('key'));
                }
            });

            if(arrayIds.length>0)
            {
                if(chrome.extension.getBackgroundPage().newwordsCount==arrayIds.length)
                {
                    deleteDataIntoDB([],true);
                }else
                {
                    deleteDataIntoDB(arrayIds,false);
                }
            }
        }
        else{
            alert('Input a valid code!');
        }
        $('#code').val('');
    });

    $("#btnprint").click(function () {
        var divContent = $("<div>");
        var table = $("<table>").attr('border','1').attr('width','100%').appendTo(divContent);
        var thead = $("<thead>").appendTo(table);
        var hrow = $("<tr>").appendTo(thead);
        $("<th>").attr('width','4%').text('No').appendTo(hrow);//no
        $("<th>").attr('width','20%').text('Phrase').appendTo(hrow);//phrase
        $("<th>").attr('width','15%').text('Category').appendTo(hrow);//category
        $("<th>").attr('width','29%').text('Vietnamese').appendTo(hrow);//vimeaning
        $("<th>").attr('width','32%').text('English').appendTo(hrow);//enmeaning
        var no = 1;
        var tbody = $("<tbody>").appendTo(table);
        $('#tbodySavedWordList').find('tr').each(function () {
            if($(this).find(":checkbox:first").attr('checked'))
            {
                var datarow = $("<tr>").appendTo(tbody);
                $("<td>").text(no++).appendTo(datarow);
                $("<td>").text($(this).find("td:nth-child(4)").text().trim()).appendTo(datarow);
                $("<td>").text($(this).find("td:nth-child(5)").text().trim()).appendTo(datarow);
                $("<td>").text($(this).find("td:nth-child(6)").text().trim()).appendTo(datarow);
                $("<td>").text($(this).find("td:nth-child(7)").text().trim()).appendTo(datarow);
            }
        });
        if($('#tbodyHistoryList').find("tr input[type=checkbox]:checked").length>0 ||
           $('#tbodySearchList').find("tr input[type=checkbox]:checked").length>0)
        {
                 if(confirm('Do you want to print the selected phrases from the other tabs?'))
                 {
                     $('#tbodyHistoryList').find('tr').each(function () {
                         if($(this).find(":checkbox:first").attr('checked'))
                         {
                             var datarow = $("<tr>").appendTo(tbody);
                             $("<td>").text(no++).appendTo(datarow);
                             $("<td>").text($(this).find("td:nth-child(3)").text().trim()).appendTo(datarow);
                             $("<td>").text($(this).find("td:nth-child(4)").text().trim()).appendTo(datarow);
                             $("<td>").text($(this).find("td:nth-child(5)").text().trim()).appendTo(datarow);
                             $("<td>").text($(this).find("td:nth-child(6)").text().trim()).appendTo(datarow);
                         }
                     });

                     $('#tbodySearchList').find('tr').each(function () {
                         if($(this).find(":checkbox:first").attr('checked'))
                         {
                             var datarow = $("<tr>").appendTo(tbody);
                             $("<td>").text(no++).appendTo(datarow);
                             $("<td>").text($(this).find("td:nth-child(3)").text().trim()).appendTo(datarow);
                             $("<td>").text($(this).find("td:nth-child(4)").text().trim()).appendTo(datarow);
                             $("<td>").text($(this).find("td:nth-child(5)").text().trim()).appendTo(datarow);
                             $("<td>").text($(this).find("td:nth-child(6)").text().trim()).appendTo(datarow);
                         }
                     });
                 }
        }

       // alert(divContent.html());
        var docprint=window.open("");
        docprint.document.open();
        docprint.document.write('<html><head><title>List of new phrases</title>');
        docprint.document.write('<style type=\"text/css\"> table{table-layout:fixed;} td{text-align:center;word-wrap: break-word;} </style>');
        docprint.document.write('</head><body onLoad=\"self.print()\">');
        docprint.document.write(divContent.html());
        docprint.document.write('<div style=\"margin:0px 15px 0px 0px;\" align=\"right\">PiDict</div>');
        docprint.document.write('</body></html>');
        docprint.document.close();
        docprint.focus();

    });

    $("#btnprint1").click(function () {
        if($('#datepicker1').val() && $('#datepicker2').val())
        {
            var divContent = $("<div>");
            var table = $("<table>").attr('border','1').attr('width','100%').appendTo(divContent);
            var thead = $("<thead>").appendTo(table);
            var hrow = $("<tr>").appendTo(thead);
            $("<th>").attr('width','4%').text('No').appendTo(hrow);//no
            $("<th>").attr('width','20%').text('Phrase').appendTo(hrow);//phrase
            $("<th>").attr('width','15%').text('Category').appendTo(hrow);//category
            $("<th>").attr('width','29%').text('Vietnamese').appendTo(hrow);//vimeaning
            $("<th>").attr('width','32%').text('English').appendTo(hrow);//enmeaning
            var no = 1;
            var tbody = $("<tbody>").appendTo(table);
            $('#tbodyHistoryList').find('tr').each(function () {
                if($(this).find(":checkbox:first").attr('checked'))
                {
                    var datarow = $("<tr>").appendTo(tbody);
                    $("<td>").text(no++).appendTo(datarow);
                    $("<td>").text($(this).find("td:nth-child(3)").text().trim()).appendTo(datarow);
                    $("<td>").text($(this).find("td:nth-child(4)").text().trim()).appendTo(datarow);
                    $("<td>").text($(this).find("td:nth-child(5)").text().trim()).appendTo(datarow);
                    $("<td>").text($(this).find("td:nth-child(6)").text().trim()).appendTo(datarow);
                }
            });

            if($('#tbodySavedWordList').find("tr input[type=checkbox]:checked").length>0 ||
                $('#tbodySearchList').find("tr input[type=checkbox]:checked").length>0)
            {
                if(confirm('Do you want to print the selected phrases from the other tabs?'))
                {
                    $('#tbodySavedWordList').find('tr').each(function () {
                        if($(this).find(":checkbox:first").attr('checked'))
                        {
                            var datarow = $("<tr>").appendTo(tbody);
                            $("<td>").text(no++).appendTo(datarow);
                            $("<td>").text($(this).find("td:nth-child(4)").text().trim()).appendTo(datarow);
                            $("<td>").text($(this).find("td:nth-child(5)").text().trim()).appendTo(datarow);
                            $("<td>").text($(this).find("td:nth-child(6)").text().trim()).appendTo(datarow);
                            $("<td>").text($(this).find("td:nth-child(7)").text().trim()).appendTo(datarow);
                        }
                    });

                    $('#tbodySearchList').find('tr').each(function () {
                        if($(this).find(":checkbox:first").attr('checked'))
                        {
                            var datarow = $("<tr>").appendTo(tbody);
                            $("<td>").text(no++).appendTo(datarow);
                            $("<td>").text($(this).find("td:nth-child(3)").text().trim()).appendTo(datarow);
                            $("<td>").text($(this).find("td:nth-child(4)").text().trim()).appendTo(datarow);
                            $("<td>").text($(this).find("td:nth-child(5)").text().trim()).appendTo(datarow);
                            $("<td>").text($(this).find("td:nth-child(6)").text().trim()).appendTo(datarow);
                        }
                    });
                }
            }

            // alert(divContent.html());
            var docprint=window.open("");
            docprint.document.open();
            docprint.document.write('<html><head><title>List of history phrases</title>');
            docprint.document.write('<style type=\"text/css\"> table{table-layout:fixed;} td{text-align:center;word-wrap: break-word;} </style>');
            docprint.document.write('</head><body onLoad=\"self.print()\">');
            docprint.document.write('<div style=\"margin:0px 15px 0px 0px;\" align=\"right\">'+ $('#datepicker1').val()+ ' - ' + $('#datepicker2').val()+ '</div>');
            docprint.document.write(divContent.html());
            docprint.document.write('<div style=\"margin:0px 15px 0px 0px;\" align=\"right\">PiDict</div>');
            docprint.document.write('</body></html>');
            docprint.document.close();
            docprint.focus();
        }else
        {
            alert('Please correct start date and end date!');
        }

    });

    $("#btnprint2").click(function () {
        var divContent = $("<div>");
        var table = $("<table>").attr('border','1').attr('width','100%').appendTo(divContent);
        var thead = $("<thead>").appendTo(table);
        var hrow = $("<tr>").appendTo(thead);
        $("<th>").attr('width','4%').text('No').appendTo(hrow);//no
        $("<th>").attr('width','20%').text('Phrase').appendTo(hrow);//phrase
        $("<th>").attr('width','15%').text('Category').appendTo(hrow);//category
        $("<th>").attr('width','29%').text('Vietnamese').appendTo(hrow);//vimeaning
        $("<th>").attr('width','32%').text('English').appendTo(hrow);//enmeaning
        var no = 1;
        var tbody = $("<tbody>").appendTo(table);
        $('#tbodySearchList').find('tr').each(function () {
            if($(this).find(":checkbox:first").attr('checked'))
            {
                var datarow = $("<tr>").appendTo(tbody);
                $("<td>").text(no++).appendTo(datarow);
                $("<td>").text($(this).find("td:nth-child(3)").text().trim()).appendTo(datarow);
                $("<td>").text($(this).find("td:nth-child(4)").text().trim()).appendTo(datarow);
                $("<td>").text($(this).find("td:nth-child(5)").text().trim()).appendTo(datarow);
                $("<td>").text($(this).find("td:nth-child(6)").text().trim()).appendTo(datarow);
            }
        });

        if($('#tbodySavedWordList').find("tr input[type=checkbox]:checked").length>0 ||
            $('#tbodyHistoryList').find("tr input[type=checkbox]:checked").length>0)
        {
            if(confirm('Do you want to print the selected phrases from the other tabs?'))
            {
                $('#tbodySavedWordList').find('tr').each(function () {
                    if($(this).find(":checkbox:first").attr('checked'))
                    {
                        var datarow = $("<tr>").appendTo(tbody);
                        $("<td>").text(no++).appendTo(datarow);
                        $("<td>").text($(this).find("td:nth-child(4)").text().trim()).appendTo(datarow);
                        $("<td>").text($(this).find("td:nth-child(5)").text().trim()).appendTo(datarow);
                        $("<td>").text($(this).find("td:nth-child(6)").text().trim()).appendTo(datarow);
                        $("<td>").text($(this).find("td:nth-child(7)").text().trim()).appendTo(datarow);
                    }
                });

                $('#tbodyHistoryList').find('tr').each(function () {
                    if($(this).find(":checkbox:first").attr('checked'))
                    {
                        var datarow = $("<tr>").appendTo(tbody);
                        $("<td>").text(no++).appendTo(datarow);
                        $("<td>").text($(this).find("td:nth-child(3)").text().trim()).appendTo(datarow);
                        $("<td>").text($(this).find("td:nth-child(4)").text().trim()).appendTo(datarow);
                        $("<td>").text($(this).find("td:nth-child(5)").text().trim()).appendTo(datarow);
                        $("<td>").text($(this).find("td:nth-child(6)").text().trim()).appendTo(datarow);
                    }
                });
            }
        }
        // alert(divContent.html());
        var docprint=window.open("");
        docprint.document.open();
        docprint.document.write('<html><head><title>List of phrases</title>');
        docprint.document.write('<style type=\"text/css\"> table{table-layout:fixed;} td{text-align:center;word-wrap: break-word;} </style>');
        docprint.document.write('</head><body onLoad=\"self.print()\">');
        docprint.document.write(divContent.html());
        docprint.document.write('<div style=\"margin:0px 15px 0px 0px;\" align=\"right\">PiDict</div>');
        docprint.document.write('</body></html>');
        docprint.document.close();
        docprint.focus();
    });


    $("#btnaddrow").click(function () {
       addPhraseIntoDB('','','','','No');
    });



    $("#bottom").click(function () {
        window.scrollTo(0, document.body.scrollHeight);
    });

    $("#bottom1").click(function () {
        window.scrollTo(0, document.body.scrollHeight);
    });

    $("#bottom2").click(function () {
        window.scrollTo(0, document.body.scrollHeight);
    });

    $("#btncloudup").click(function () {
        var jsonObj = [];
        $('#tbodySavedWordList').find('tr').each(function () {
            //only row is checked and status cloud is 'No' will be send to cloud
            if($(this).find(":checkbox:first").attr('checked') && $(this).find("td:nth-child(3)").text().trim()=='No')
            {
                jsonObj.push({clientid:$(this).attr('key'),
                phrase:$(this).find("td:nth-child(4)").text().trim(),
                category:$(this).find("td:nth-child(5)").text().trim(),
                vimeaning:$(this).find("td:nth-child(6)").text().trim(),
                enmeaning:$(this).find("td:nth-child(7)").text().trim()});
            }
        });
        if(jsonObj.length>0)
        {
            var jsonText=JSON.stringify(jsonObj);
           // alert(jsonText);
            var param = "code="+$('#code').val()+"&data="+jsonText;
            requestToCloud("/extension",param,processResponseFromCloudForSubmitPhrases);
        }
        $('#code').val('');
    });

    //get history of phrases from cloud
    $("#btnshowhistory").click(function () {
        if($('#datepicker1').val() && $('#datepicker2').val())
        {
            //alert($('#datepicker1').val().replace(/\//ig,"-"));
            var param = "code="+$('#code1').val()+"&startdate="+$('#datepicker1').val().replace(/\//ig,"-")+"&enddate="+$('#datepicker2').val().replace(/\//ig,"-");
            requestToCloud("/historyextension",param,processResponseFromCloudForHistoryPhrase);
            $('#code1').val('');
        }else
        {
            alert('Please input start date and end date!');
        }
    });
    $('#code1').bind('keypress',function (event){
        if (event.keyCode === 13){//press enter
            if($('#datepicker1').val() && $('#datepicker2').val())
            {
                //alert($('#datepicker1').val().replace(/\//ig,"-"));
                var param = "code="+$('#code1').val()+"&startdate="+$('#datepicker1').val().replace(/\//ig,"-")+"&enddate="+$('#datepicker2').val().replace(/\//ig,"-");
                requestToCloud("/historyextension",param,processResponseFromCloudForHistoryPhrase);
                $('#code1').val('');
            }else
            {
                alert('Please input start date and end date!');
            }
        }
    });

    //search phrases from cloud
    $("#btnsearch").click(function () {
        var values_chk = [];
        $("#site-bottom-bar-content2").find('input:checkbox').each(function(){
            var checked = $(this).attr('checked');
            if(checked){
                values_chk.push($(this).val());
            }
        });
        if(values_chk.length < 1){
            values_chk = ['1','2','3','4','5'];
        }
        var param = "code="+$('#code2').val()+"&keyword="+$('#input_search').val().trim()+"&category="+values_chk.join("_");
        requestToCloud("/searchextension",param,processResponseFromCloudForSearchPhrase);
        $('#code2').val('');
    });
    $('#code2').bind('keypress',function (event){
        if (event.keyCode === 13){//press enter
            var values_chk = [];
            $("#site-bottom-bar-content2").find('input:checkbox').each(function(){
                var checked = $(this).attr('checked');
                if(checked){
                    values_chk.push($(this).val());
                }
            });
            if(values_chk.length < 1){
                values_chk = ['1','2','3','4','5'];
            }
            var param = "code="+$('#code2').val()+"&keyword="+$('#input_search').val().trim()+"&category="+values_chk.join("_");
            requestToCloud("/searchextension",param,processResponseFromCloudForSearchPhrase);
            $('#code2').val('');
        }
    });

});

function deleteRowSuccessCallback(isDelAll)
{
    $('#tbodySavedWordList').empty();
    var checkBox = $('#theadSavedWordList').find(':checkbox:first');
    if(checkBox.attr('checked')){
        var span = checkBox.next('span');
        span.toggleClass('selected');  //add class if not, remove class if it is
        checkBox.attr('checked',false);
    }

    if(!isDelAll)
    {
       displayAllRecordsInDB();
    }
}

function requestToCloud(url,param,callback)
{
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function () {
        if (xhr.readyState == 4) {
            removeLoader();
            callback(xhr.responseText);
        }
    };
    var cloudUrl = "http://pidictcloud.appspot.com/"+url;
    xhr.open("POST",cloudUrl, true);
   // xhr.setRequestHeader("Content-type", "application/json; charset=utf-8");
    xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded;charset=UTF-8");
    xhr.setRequestHeader("Content-length", param.length);
    xhr.setRequestHeader("Connection", "close");
    xhr.timeout = 30000;//30s
    xhr.ontimeout = function () { removeLoader();alert('Timeout waiting for response from cloud!') ; }
    createLoader();
    xhr.send(param);

}

function processResponseFromCloudForSubmitPhrases(responsetext)
{
    //alert(responsetext);
    var obj = jQuery.parseJSON(responsetext);
    if(obj!=null)
    {
        if(obj.resultlist!=null && obj.resultlist.length>0)
        {
            for(var i in obj.resultlist)
            {
                //alert(obj.resultlist[i]);
                updateDataIntoDB(obj.resultlist[i],'cloudstatus','Yes');

            }
            //change status cloud
            $('#tbodySavedWordList').find('tr').each(function () {
                for(var i in obj.resultlist)
                {
                    if($(this).attr('key')==obj.resultlist[i])
                    {
                        $(this).find("td:nth-child(3)").text('Yes');
                        break;
                    }
                }
            });
        }
        alert(obj.message) ;
    }else
    {
        alert('Response = \''+responsetext+'\' from cloud cannot be parsed!') ;
    }
}
function processResponseFromCloudForHistoryPhrase(responsetext)
{
    //alert(responsetext);
    var obj = jQuery.parseJSON(responsetext);
    if(obj!=null)
    {
        if(obj.resultlist!=null && obj.resultlist.length>0)
        {
            var no =0;
            $('#tbodyHistoryList').empty();
            var checkBox = $('#theadHistoryList').find(':checkbox:first');
            if(checkBox.attr('checked')){
                var span = checkBox.next('span');
                span.toggleClass('selected');  //add class if not, remove class if it is
                checkBox.attr('checked',false);
            }
            for(var i in obj.resultlist)
            {
                //alert(obj.resultlist.length);
                var datarow = $("<tr>").appendTo($('#tbodyHistoryList'));

                $("<td>").text(++no).appendTo(datarow);//no col
                //checkbox col
                var inputLabelEle = $("<label>'").appendTo($("<td>").appendTo(datarow))
                $("<input>").attr('type','checkbox').attr('name','checks').appendTo(inputLabelEle);
                applyCssToCheckbox(inputLabelEle);
                //phrase col
                $("<td>").text(obj.resultlist[i].phrase==null?"":obj.resultlist[i].phrase).appendTo(datarow);
                //category col
                $("<td>").text(obj.resultlist[i].category==null?"":obj.resultlist[i].category).appendTo(datarow);
                //vietnamese col
                $("<td>").text(obj.resultlist[i].vimeaning==null?"":obj.resultlist[i].vimeaning).appendTo(datarow);
                //english col
                $("<td>").text(obj.resultlist[i].enmeaning==null?"":obj.resultlist[i].enmeaning).appendTo(datarow);
                //created on col
                $("<td>").text(obj.resultlist[i].createdon==null?"":obj.resultlist[i].createdon).appendTo(datarow);
            }
        }
        alert(obj.message) ;
    }else
    {
        alert('Response = \''+responsetext+'\' from cloud cannot be parsed!') ;
    }
}
function processResponseFromCloudForSearchPhrase(responsetext)
{
    //alert(responsetext);
    var obj = jQuery.parseJSON(responsetext);
    if(obj!=null)
    {
        if(obj.resultlist!=null && obj.resultlist.length>0)
        {
            var no =0;
            $('#tbodySearchList').empty();
            var checkBox = $('#theadSearchList').find(':checkbox:first');
            if(checkBox.attr('checked')){
                var span = checkBox.next('span');
                span.toggleClass('selected');  //add class if not, remove class if it is
                checkBox.attr('checked',false);
            }
            for(var i in obj.resultlist)
            {
                //alert(obj.resultlist.length);
                var datarow = $("<tr>").appendTo($('#tbodySearchList'));

                $("<td>").text(++no).appendTo(datarow);//no col
                //checkbox col
                var inputLabelEle = $("<label>'").appendTo($("<td>").appendTo(datarow))
                $("<input>").attr('type','checkbox').attr('name','checks').appendTo(inputLabelEle);
                applyCssToCheckbox(inputLabelEle);
                //phrase col
                $("<td>").text(obj.resultlist[i].phrase==null?"":obj.resultlist[i].phrase).appendTo(datarow);
                //category col
                $("<td>").text(obj.resultlist[i].category==null?"":obj.resultlist[i].category).appendTo(datarow);
                //vietnamese col
                $("<td>").text(obj.resultlist[i].vimeaning==null?"":obj.resultlist[i].vimeaning).appendTo(datarow);
                //english col
                $("<td>").text(obj.resultlist[i].enmeaning==null?"":obj.resultlist[i].enmeaning).appendTo(datarow);
                //created on col
                $("<td>").text(obj.resultlist[i].createdon==null?"":obj.resultlist[i].createdon).appendTo(datarow);
            }
        }
        alert(obj.message) ;
    }else
    {
        alert('Response = \''+responsetext+'\' from cloud cannot be parsed!') ;
    }
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




