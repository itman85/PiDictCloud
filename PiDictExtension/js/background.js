var newpage_url = chrome.extension.getURL("html/newword.html");
var isTranslate = true;
var newWordTabId = -1;
var funcAddWord = null;
var newwordsCount = 0;
// Listen for the content script to send a message to the background page.
//chrome.extension.onRequest.addListener(onMessage);
chrome.runtime.onMessage.addListener(onMessage);
//create context menus
var id = chrome.contextMenus.create({"title": "See list of words", "contexts":["all"],"onclick": genericOnClick});
var radio1 = chrome.contextMenus.create({"title": "Enable", "type": "radio","contexts":["all"],"checked":true,"onclick":radio1OnClick});
var radio2 = chrome.contextMenus.create({"title": "Disable", "type": "radio","contexts":["all"],"onclick":radio2OnClick});
//load new page
chrome.browserAction.onClicked.addListener(function(tab) {
    focusOrCreateTab(newpage_url);
});

//Database init
var PiDictDB = openDatabase("PiDictDB", "1.0", "PiDict database", 5000000);//5MB of size
createTableIfNotExist();
getAllRecordsInDB();




function onMessage(request,sender, sendResponse) {
    if (isTranslate && request.action == 'translate') {
        //alert('dich '+request.searchfor);
        //console.log(request.searchfor);
        doTranslate(request.searchfor,"en","vi",sendResponse);
        return true;//return true from the event listener to indicate you wish to send a response asynchronously
    }
    else if (request.action == 'save') {
        addNewPhrase(request.phrase,request.category,request.vimeaning,request.enmeaning,request.cloudstatus,request.ref);
    }


};

function doTranslate(text, fromLang, toLang, sendResponse) {
    sendReqTranslate(text, fromLang,toLang, function(result, text) {
        //alert(text);
        if (result && result.sentences && result.sentences.length > 0) {
            //alert(result.sentences.length);
            var textTrans = '';
            for(i in result.sentences) {
                if(textTrans.length != 0) {
                    textTrans += ' ';
                }
                textTrans += result.sentences[i].trans;
            }
            var textAdditional = null;
            if(result.dict && result.dict.length > 0) {
                if(textTrans.length != 0) {
                    textTrans += '\n';
                }
                textAdditional = '';
                for(dict in result.dict) {
                    if(result.dict[dict].pos.length > 0) {
                        textAdditional += '[' + result.dict[dict].pos + ']';
                    }
                    textAdditional += ' ' + result.dict[dict].terms.join(', ') + '.\n';
                }
            }
            /*"{"sentences":[{"trans":"và","orig":"and","translit":"","src_translit":""}],"dict":[{"pos":"liên từ","terms":["và","cùng","với"],"entry":[{"word":"và","reverse_translation":["and"],"score":5.2554233e-06},{"word":"cùng","reverse_translation":["and"],"score":1.3363882e-07},{"word":"với","reverse_translation":["and"],"score":1.3156695e-07}]}],"src":"en","server_time":22}"*/
            var isTabCreated = false;
            if(newWordTabId != -1){
                isTabCreated = true;
            }
            //alert(textTrans+' '+textAdditional);
           sendResponse({text:textTrans+' '+textAdditional,tab: isTabCreated});

        }
        else
        {
            sendResponse({text:text,tab: isTabCreated});
        }
    });
}

function sendReqTranslate(text, fromLang, toLang,callback) {
    //alert(text + fromLang+toLang);
    //var url = 'http://translate.google.com/translate_a/t?client=t&otf=1&pc=0';
    var url = 'http://translate.google.com/translate_a/t?client=f';
    url += '&text=' + text;
    url += '&hl=vi';
    if(fromLang != 'auto') {
        url += '&sl=' + fromLang;
    }
    url += '&tl=' + toLang;
    //alert(url);
    var xhr = new XMLHttpRequest();
    xhr.open("GET", url, true);
    xhr.onreadystatechange = function() {
        if (xhr.readyState == 4) {
            if(xhr.status==200)//OK
            {
                // JSON.parse does not evaluate the attacker's scripts.
                callback(JSON.parse(xhr.responseText), text);
            }else
            {
                callback(null, 'Translation request error '+xhr.statusText);
            }

        }
    }
    xhr.send();
}


function radio1OnClick(info, tab) {
    //console.log("radio item " + info.menuItemId + " was clicked (previous checked state was "  + info.wasChecked + ")");
    isTranslate = true;
}
function radio2OnClick(info, tab) {
    isTranslate = false;
}
function genericOnClick(info, tab) {
    //console.log("item " + info.menuItemId + " was clicked");
    focusOrCreateTab(newpage_url);
}

chrome.tabs.onRemoved.addListener(function(tabid, removeInfo) {
    //alert('tabclose')
    if (tabid==newWordTabId) {
        //alert(newWordTabId);
        newWordTabId = -1;
    }
});


function focusOrCreateTab(url) {
    if (newWordTabId!=-1) {
        chrome.tabs.update(newWordTabId, {"selected":true});
    } else {
        chrome.tabs.create({"url":url, "selected":true},function(newTabid){newWordTabId = newTabid.id;});
    }
}

/********************Database***********************/

//SQLStatementErrorCallback
function onError(tx, error) {
    alert("There has been an error: " + error.message);
}

//SQLTransactionErrorCallback  for transaction
function onTxError( error) {
   // alert("There has been an error: " + error.message);
}

//SQLVoidCallback  for transaction
function onTxSuccess() {

}


function createTableIfNotExist() {
    PiDictDB.transaction(function(tx) {
        tx.executeSql("CREATE TABLE IF NOT EXISTS tblPhraseEntities (id INTEGER PRIMARY KEY  AUTOINCREMENT, phrase TEXT,category TEXT, vimeaning TEXT, enmeaning TEXT,cloudstatus TEXT, ref TEXT, modifiedOn TEXT)",
            [],
            function(tx) { },
            onError);
    });

}
// add record with random values
function addNewPhrase(phrase,category,vimeaning,enmeaning,cloudstatus,ref) {
    var lastmodified = (new Date()).toLocaleString();
    PiDictDB.transaction(function(tx) {
        tx.executeSql("INSERT INTO tblPhraseEntities (phrase,category,vimeaning,enmeaning,cloudstatus,ref,modifiedOn) VALUES (?,?,?,?,?,?,?)",
            [phrase,category,vimeaning,enmeaning,cloudstatus,ref,lastmodified],
            function(tx, result) {
				newwordsCount++;
				chrome.browserAction.setBadgeText({"text": ""+newwordsCount});
                chrome.browserAction.setBadgeBackgroundColor({"color": [255, 0, 0, 255]});
                if(funcAddWord && newWordTabId!=-1)
                    funcAddWord(result.insertId,cloudstatus, phrase, category, vimeaning, enmeaning, ref, lastmodified);
			},
            onError);
    });
}


// select all records and display number of record as badge text
function getAllRecordsInDB() {
    PiDictDB.transaction(function(tx) {
        tx.executeSql("SELECT * FROM tblPhraseEntities", [], function(tx, result) {
            if(result.rows.length>0)
            {
				newwordsCount = result.rows.length;
                chrome.browserAction.setBadgeText({"text": ""+newwordsCount});
                chrome.browserAction.setBadgeBackgroundColor({"color": [255, 0, 0, 255]});
            }
        },onError);
    });
}

function updateRecord(id, col,value) {
    PiDictDB.transaction(function(tx) {
        var sql =  "UPDATE tblPhraseEntities SET "+ col +" = ? , modifiedOn = ? WHERE id = ?";
        tx.executeSql(sql, [value,(new Date()).toLocaleString(), id], null, onError);
    });
}



function deleteRecords(ids,isDelAll,delCallback) {
    PiDictDB.transaction(function(tx) {
        if(isDelAll)
        {
            tx.executeSql("DELETE FROM tblPhraseEntities;", [], null, onError);
        }
        else
        {
            var sql =  "DELETE FROM tblPhraseEntities WHERE id = ?";
            for(var i=0;i<ids.length;i++)
            {
                tx.executeSql(sql, [ids[i]], null, onError);
            }
        }
    },null,function() //transaction successful
    {
      if(isDelAll)
      {
          newwordsCount = 0;
          chrome.browserAction.setBadgeText({"text": ""});
      }else
      {
          newwordsCount -= ids.length;
          chrome.browserAction.setBadgeText({"text": ""+newwordsCount});
          chrome.browserAction.setBadgeBackgroundColor({"color": [255, 0, 0, 255]});
      }
      delCallback(isDelAll);
    });
}
