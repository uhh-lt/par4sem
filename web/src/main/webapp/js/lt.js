// GLOBAL VARIAVLES
var prev = ""; // the text to copare before loging. If similar, who cares
// otherwise, log the change
var enableEdit = false; // Enable editing only when the user clicks the button
// (Edit)
var numberOfChanges = 0;// how many chnages do the worker makes. Help to enable
// the submit button.
var spellchecker;
var COUNT = 8;
var hitId;
var assignmentId;
var workerId;

$.ajaxSetup({
    beforeSend:function(){
        // show gif here, eg:
        $("#loading").show();
        $("#experiment").attr("disabled", "disabled");
    },
    complete:function(){
        // hide gif here, eg:
        $("#loading").hide();
        $("#experiment").removeAttr("disabled");
    }
});

$(document).ready(function() {
	//$("#submitButton").hide();
	$("#submitButton").attr("disabled", "disabled");
	$("#experiment").hide();
	$('#assignmentId').val(GetAssignmentId());
	$("#turkSubmit").attr("action", GetHost());
	$('#workerId').val(GetWorkerId());
	$('#hitId').val(GetHitId());

	hitId = GetHitId();
	assignmentId = GetAssignmentId();
	workerId = GetWorkerId();

	$('a#Instlink').click(function() {
		$('#instructions').show();
	});

	spellchecker = new $.SpellChecker('#checktext', {
		lang : 'en',
		parser : 'html',
		webservice : {
			path : appConfig.URL+'/SpellChecker?',
			driver : 'PSpell'
		},
		suggestBox : {
			position : 'below'
		}
	});


	// Check the spelling
	$("#check-textarea").click(function(e) {
		spellchecker.check();
	});
	
	 //=========================KEY SET CONTROLLING ================
	$("#checktext").keydown(function (e) {
		if (e.keyCode == 32) { 
			logit($('#checktext').text(), $('#checktext').html());
		}
		//else if( e.which === 8 ){ return false; }
		/**if(e.metaKey || e.ctrlKey ){
			 e.preventDefault();
			 return false;
		} */
    });
	$("#show-instruction").click(function(e) {

		if ($('#instructions').is(":visible")) {
			$('#instructions').hide();
			this.value = 'Show instructions';
			$('#animation').show();
		} else {
			$('#instructions').show();
			this.value = 'Show animation';
			$('#animation').hide();
		}
	});
	// show OR hid original text
	$("#show-orig-text").click(function(e) {

		if ($('#Origchecktext').is(":visible")) {
			$('#Origchecktext').hide();
			this.value = 'Show original texts';
		} else {
			$('#Origchecktext').show();
			this.value = 'Hide original text';
		}
	});
	
	 //=========================Control Keys ================
	$('#checktext').mouseup(function() {
		var text = getSelected();
//		offset = text.anchorNode.parentElement.offsetLeft - text.anchorNode.parentElement.parentElement.offsetLeft;
		//offset = text.anchorNode.parentElement.offsetWidth - text.anchorNode.parentElement.parentElement.offsetWidth;
	//	offset = $(window).width() -  text.anchorNode.parentElement.offsetLeft;
	//	alert (offset);
		//replaceStartEnd = getStartEndOfClickedElement($('#checktext'))
		//alert (replaceStartEnd.start +"..end.." + replaceStartEnd.end);
		// send to server
		if(stopWords.indexOf($.trim(text.toString().toLowerCase())) != -1){
			alert ("\"" +text +"\" is a simple word")
		}
		else if (text.toString().trim().split(' ').length > 3){
			alert ("Choose a maximum of three words only!");
		}
		else if(text.toString().length <3){
			// do nothing
		}
		else if (text != '') {
			// send server
			sendSelecton(text.toString(), assignmentId);
			//reloadText();
		}
	});
	
	$.ajax({ 
	    url: '//freegeoip.net/json/', 
	    type: 'POST', 
	    dataType: 'jsonp',
	    success: function(location) {
	    	sendLocation(location.ip, location.country_name, location.country_code);
	      }
	});
	
	$("#comment").keyup(function () {
	   if( ($("#comment").val() !== '' && $("#comment").val().trim().split(' ').length > 5) ||(numberOfChanges>COUNT)){
		   enableButton()
		}
		else {
			disableButton()
		}
	});

});

function fetchHtml(){
	return $('#checktext').html();
}

/**
$(document).ready(function(){
	  $('#checktext').bind("cut copy paste",function(e) {
	      e.preventDefault();
	  });
	});
*/
$(document).ready(function(){
	  $("*").dblclick(function(e){
	    e.preventDefault();
	  });
	});


 function sendSelecton(textSelected, assignmentId) {
	// text = $('#checktext').text();
	// $('#checktext').html(text);
	$.ajax({
		url : appConfig.URL+"/updater",
		type : "POST",
		data : ({
			"textSelected" : textSelected,
			"assignmentId" : assignmentId,
			"workerId": workerId,
			"hitId" : hitId,
			"html" : $('#checktext').html()
		}),
		dataType : "text",
		success : function(response) {	
		//	var result = response.replace(/\n/g, "<br />");
		//	$('#checktext').html(result);
		//	spellchecker.check();
			updateText(response.replace(/\n/g, "<br />"));
		}
	});

}
 
 function updateText(result) {
	 $('#checktext').html(result);
	 refresh();
 }
 
 function refresh(callBack) {
	 spellchecker.check();
     setTimeout(function() { 
         callBack();
     }, 500);               
 }
 
 //=========================FONT SELECTION ================
 
$(function() {
	$('#font').fontselect().change(function() {

		// replace + signs with spaces for css
		var font = $(this).val().replace(/\+/g, ' ');

		// split font into family and weight
		font = font.split(':');

		// set family on paragraphs
		$('#checktext').css('font-family', font[0]);
	});
});

$(function() {
	$("#size").change(function() {
		$('#checktext').css("font-size", $(this).val() + "px");
	});
});


//=========================UPDATE LOCATION ================

function sendLocation(pi, cnt,cd) {
	$.ajax({
		url : appConfig.URL+"/logger",
		type : "POST",
		data : ({
			"pi" : pi,
			"cnt" : cnt,
			"cd" : cd,
			"workerId": workerId,
			"hitId" : hitId,
			"assignmentId" : assignmentId
		}),
		dataType : "text"
	});

}

$(document).ready(function() {
	// if (!$("#checktext").val()) {
	reloadText();
	// }
});

//=========================START THIS EXPERIMENT ================
function StartExperiment() {
	if (IsOnTurk() && IsTurkPreview()) {
		alert("Please accept the HIT before continuing.");
		return false;
	}
	/*
	 * if ($.browser.mozilla){ alert("Mozila Firefox is not supported. Please
	 * use Chrome or Safari for this survey") return false; }
	 */
	$('#start').hide();
	$('#experiment').show();
	$('#instructions').hide();
	$('#animation').hide();
	$('#btn_check').hide();
	$('#Origchecktext').hide();
	
	startOver ();
	startOver ();
	spellchecker.check();
	window.scrollTo(0, 0);
}

function startOver() {
	var hitId = GetHitId()
	$.ajax({
		url : appConfig.URL+"/loadDocument",
		type : "POST",
		data : ({
			"start" : true,
		}),
		dataType : "text",
		success : function(response) {
		//	spellchecker.check();
		}
	});
}
//=========================GET TEXT FROM SERVER ================
function reloadText(content) {
	var hitId = GetHitId()
	$.ajax({
		url : appConfig.URL+"/loadDocument",
		type : "POST",
		data : ({
			"content" : content,
			"hitId" : hitId,
			"workerId" : workerId,
			"assignmentId" : assignmentId
		}),
		dataType : "text",
		success : function(response) {
			numberOfChanges = 0
			disableButton()
			var result = response.replace(/\n/g, "<br />");
			$('#checktext').html(result);
			$("#Origchecktext").html(result);
			spellchecker.check();
		}
	});
}
// ============CLEAR TEXT
function clearText(content) {
	$('#checktext').html(content);
	spellchecker.check();
}

//=========================UNDO TEXT CHANGES ================
function getUndo(reun) {
	var hitId = GetHitId()
	$.ajax({
		url : appConfig.URL+"/loadDocument",
		type : "POST",
		data : ({
			"undo" : reun,
			"hitId" : hitId,
			"workerId" : workerId,
			"assignmentId" : assignmentId
		}),
		dataType : "text",
		success : function(response) {
			var result = response.replace(/\n/g, "<br />");
			if(result == "Undo over!" || result == "Redo over!"){
				alert(result);
			}
			else {
				
				if(reun == "undo"){ // as it will be incremented in logit
					numberOfChanges = numberOfChanges - 2;
				}
				
			$('#checktext').html(result);
			
			$.when( spellchecker.check()).done(function() {
				logit($('#checktext').text(), $('#checktext').html(), "undo");
				});
			}
		}
	});
}

function loadText() {
	$.get('resources/spell.txt', function(data) {
		$('#checktext').text(data);
	});
}

/**
function refresh() {
	$('#checktext').html($('#checktext').html());
}
*/
// TEST
function submit() {
	window.location.href = '/web/submit.html';
}


function displayNext() {

	var nHitId = parseInt(hitId) + 1;
	var url = window.location.href;
	url = url.replace("hitId=" + hitId, "hitId=" + nHitId)

	window.location.href = url;
	location.href.replace(location.hash, "")
}
// LOG the text to the database
function logit(text, html, undo) {
	$.ajax({
		url : appConfig.URL+"/logger",
		type : "POST",
		dataType : "text",
		data : {
			"text" : text,
			"html" : html,
			"hitId" : hitId,
			"undo" : undo,
			"workerId" : workerId,
			"assignmentId" : assignmentId
		},
		success : function(response) {
			/**
			//$("#checktext").text(response);
			numberOfChanges++;  
			// 15 or more and compare text for the final
			if (response == "yes" || numberOfChanges > COUNT || ($("#comment").val() !== '' && $("#comment").val().trim().split(' ').length > 5)) {
				enableButton()
			}
			else {
				disableButton()
			} */
		}
	});
}
function disableButton(){
	$("#submitButton").attr("disabled", "disabled");
	$("#submitButton").removeClass("btn-gradient blue").addClass("btn-gradient gray");
}

function enableButton(){
	//$("#submitButton").show()
	$("#submitButton").removeAttr("disabled");
	$("#submitButton").removeClass("btn-gradient gray").addClass("btn-gradient blue");
	
	//$("#simpleText").val($("#checktext").val());
	$("#origTextSubmit").val($("#Origchecktext").text())
	$("#simpleTextSubmit").val($("#checktext").text())
}

function setText(id) {
	$.ajax({
		url : appConfig.URL+"/checkDocument",
		type : "POST",
		data : ({
			"hitId" : id,
			"workerId" : workerId,
			"assignmentId" : assignmentId
		}),
		dataType : "text",
		success : function(response) {
			$("#checktext").text(response);
		}
	});
}

function getSelected() {
	if (window.getSelection) {
		return window.getSelection();
	} else if (document.getSelection) {
		return document.getSelection();
	} else {
		var selection = document.selection && document.selection.createRange();
		if (selection.text) {
			return selection.text;
		}
		return false;
	}
	return false;
}


/**
 * $('#checktext').keydown(function(event) { var forbiddenKeys = new Array('c',
 * 'x', 'v'); var keyCode = (event.keyCode) ? event.keyCode : event.which; var
 * isCtrl; isCtrl = event.ctrlKey if (isCtrl) { for (i = 0; i <
 * forbiddenKeys.length; i++) { if (forbiddenKeys[i] ==
 * String.fromCharCode(keyCode).toLowerCase()) { return false; } } } return
 * true; });
 */


function getStartEndOfClickedElement(element) {
	var start = 0;
	var end = 0;
	//var doc = selectionWin.baseNode.ownerDocument;
	//var win = doc.defaultView || doc.parentWindow;
	var doc = element[0];
	var win = doc;//.defaultView || doc.parentWindow;
	var sel=doc.selection;
	var content;
	if (typeof win.getSelection != "undefined") {
		sel = win.getSelection();
		content = sel.toString();
		if (sel.rangeCount > 0) {
			var range = win.getSelection().getRangeAt(0);
			var preCaretRange = range.cloneRange();
			preCaretRange.selectNodeContents(doc);
			preCaretRange.setEnd(range.startContainer, range.startOffset);
			start = preCaretRange.toString().length;// -diff;
			preCaretRange.setEnd(range.endContainer, range.endOffset);
			end = preCaretRange.toString().length;// - diff;
		}
	} 
	else if ((sel = doc.selection) && sel.type != "Control") {
		var textRange = sel.createRange();
		var preCaretTextRange = doc.body.createTextRange();
		preCaretTextRange.moveToElementText(element);
		preCaretTextRange.setEndPoint("EndToStart", textRange);
		start = preCaretTextRange.text.length;
		preCaretTextRange.setEndPoint("EndToEnd", textRange);
		end = preCaretTextRange.text.length;

		if ($.browser.mozilla) {
			start = start - 74
			end = end - 74
		} else {
			start = start - 330
			end = end - 330
		}
	}
	return {
		start : start,
		end : end,
		content : content
	};
}

function getStartEnd(element) {
	var start = 0;
	var end = 0;
	var doc = element;// .ownerDocument || element.document;
	var win = doc.defaultView || doc.parentWindow;
	var sel;
	var content;
	if (typeof win.getSelection != "undefined") {
		sel = win.getSelection();
		content = sel.toString();
		if (sel.rangeCount > 0) {
			var range = win.getSelection().getRangeAt(0);
			var preCaretRange = range.cloneRange();
			preCaretRange.selectNodeContents(element);
			preCaretRange.setEnd(range.startContainer, range.startOffset);
			start = preCaretRange.toString().length;// -diff;
			preCaretRange.setEnd(range.endContainer, range.endOffset);
			end = preCaretRange.toString().length;// - diff;
			if ($.browser.mozilla) {
				start = start - 74
				end = end - 74
			} else {
				start = start - 330
				end = end - 330
			}
		}
	} else if ((sel = doc.selection) && sel.type != "Control") {
		var textRange = sel.createRange();
		var preCaretTextRange = doc.body.createTextRange();
		preCaretTextRange.moveToElementText(element);
		preCaretTextRange.setEndPoint("EndToStart", textRange);
		start = preCaretTextRange.text.length;
		preCaretTextRange.setEndPoint("EndToEnd", textRange);
		end = preCaretTextRange.text.length;

		if ($.browser.mozilla) {
			start = start - 74
			end = end - 74
		} else {
			start = start - 330
			end = end - 330
		}
	}
	return {
		start : start,
		end : end,
		content : content
	};
}


$.fn.showDiff = function(newValue){
    return this.each(function(){
        $(this).html(
            diffString(
                $(this).text(),
                newValue
            )
        );
    });
}

var stopWords = new Array('a','about','above','across','after','again','against','all','almost','alone','along','already','also',
		'although','always','among','an','and','another','any','anybody','anyone','anything','anywhere','are','area','areas','around',
		'as','ask','asked','asking','asks','at','away','b','back','backed','backing','backs','be','became','because','become','becomes',
		'been','before','began','behind','being','beings','best','better','between','big','both','but','by','c','came','can','cannot',
		'case','cases','certain','certainly','clear','clearly','come','could','d','did','differ','different','differently','do','does',
		'done','down','down','downed','downing','downs','during','e','each','early','either','end','ended','ending','ends','enough','even',
		'evenly','ever','every','everybody','everyone','everything','everywhere','f','face','faces','fact','facts','far','felt','few',
		'find','finds','first','for','four','from','full','fully','further','furthered','furthering','furthers','g','gave','general',
		'generally','get','gets','give','given','gives','go','going','good','goods','got','great','greater','greatest','group','grouped',
		'grouping','groups','h','had','has','have','having','he','her','here','herself','high','high','high','higher','highest','him',
		'himself','his','how','however','i','if','important','in','interest','interested','interesting','interests','into','is','it',
		'its','itself','j','just','k','keep','keeps','kind','knew','know','known','knows','l','large','largely','last','later','latest',
		'least','less','let','lets','like','likely','long','longer','longest','m','made','make','making','man','many','may','me','member',
		'members','men','might','more','most','mostly','mr','mrs','much','must','my','myself','n','necessary','need','needed','needing',
		'needs','never','new','new','newer','newest','next','no','nobody','non','noone','not','nothing','now','nowhere','number','numbers',
		'o','of','off','often','old','older','oldest','on','once','one','only','open','opened','opening','opens','or','order','ordered',
		'ordering','orders','other','others','our','out','over','p','part','parted','parting','parts','per','perhaps','place','places',
		'point','pointed','pointing','points','possible','present','presented','presenting','presents','problem','problems','put','puts',
		'q','quite','r','rather','really','right','right','room','rooms','s','said','same','saw','say','says','second','seconds','see',
		'seem','seemed','seeming','seems','sees','several','shall','she','should','show','showed','showing','shows','side','sides','since',
		'small','smaller','smallest','so','some','somebody','someone','something','somewhere','state','states','still','still','such',
		'sure','t','take','taken','than','that','the','their','them','then','there','therefore','these','they','thing','things','think',
		'thinks','this','those','though','thought','thoughts','three','through','thus','to','today','together','too','took','toward',
		'turn','turned','turning','turns','two','u','under','until','up','upon','us','use','used','uses','v','very','w','want','wanted',
		'wanting','wants','was','way','ways','we','well','wells','went','were','what','when','where','whether','which','while','who',
		'whole','whose','why','will','with','within','without','work','worked','working','works','would','x','y','year','years','yet',
		'you','young','younger','youngest','your','yours','z')	