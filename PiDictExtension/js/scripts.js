//all init here
$(function(){
    /* For zebra striping */
    $("table tr:nth-child(odd)").addClass("odd-row");
    /* For cell text alignment */
    $("table td:first-child, table th:first-child").addClass("first");
    /* For removing the last border */
    $("table td:last-child, table th:last-child").addClass("last");
    /******************checkbox**********************/
    applyCssToCheckbox($("#theadSavedWordList"));
    applyCssToCheckbox($("#theadHistoryList"));
    applyCssToCheckbox($("#theadSearchList"));


    $('#theadSavedWordList').find(':checkbox').click(function() {
       // var checkboxes = $('label').find('input:checkbox, input:radio');
        // $this will contain a reference to the checkbox
        if ($(this).is(':checked')) {
            // the checkbox was checked
            $('#tbodySavedWordList').find('input:checkbox, input:radio').each(function () {
                if(! $(this).attr('checked')){
                    var span = $(this).next('span');
                    span.toggleClass('selected');  //add class if not, remove class if it is
                    $(this).attr('checked',true);
                }
            });

        } else {
            // the checkbox was unchecked
            $('#tbodySavedWordList').find('input:checkbox, input:radio').each(function () {
                if( $(this).attr('checked')){
                    var span = $(this).next('span');
                    span.toggleClass('selected');  //add class if not, remove class if it is
                    $(this).attr('checked',false);
                }
            });
        }
    });

    $('#theadHistoryList').find(':checkbox').click(function() {
        // $this will contain a reference to the checkbox
        if ($(this).is(':checked')) {
            // the checkbox was checked
            $('#tbodyHistoryList').find('input:checkbox, input:radio').each(function () {
                if(! $(this).attr('checked')){
                    var span = $(this).next('span');
                    span.toggleClass('selected');  //add class if not, remove class if it is
                    $(this).attr('checked',true);
                }
            });

        } else {
            // the checkbox was unchecked
            $('#tbodyHistoryList').find('input:checkbox, input:radio').each(function () {
                if( $(this).attr('checked')){
                    var span = $(this).next('span');
                    span.toggleClass('selected');  //add class if not, remove class if it is
                    $(this).attr('checked',false);
                }
            });
        }
    });

    $('#theadSearchList').find(':checkbox').click(function() {
        // $this will contain a reference to the checkbox
        if ($(this).is(':checked')) {
            // the checkbox was checked
            $('#tbodySearchList').find('input:checkbox, input:radio').each(function () {
                if(! $(this).attr('checked')){
                    var span = $(this).next('span');
                    span.toggleClass('selected');  //add class if not, remove class if it is
                    $(this).attr('checked',true);
                }
            });

        } else {
            // the checkbox was unchecked
            $('#tbodySearchList').find('input:checkbox, input:radio').each(function () {
                if( $(this).attr('checked')){
                    var span = $(this).next('span');
                    span.toggleClass('selected');  //add class if not, remove class if it is
                    $(this).attr('checked',false);
                }
            });
        }
    });
     /*************************search filter***********************************/
     $('#site-bottom-bar-content2').find('.search').bind('focus click',function(){
         event.stopPropagation();   //prevent propagating event to parents in DOM
         $('#site-bottom-bar-content2').find('.sb_dropdown')
             .show();
     });

    $('#site-bottom-bar-content2').find('.sb_dropdown').find('label[for="all"]').prev().bind('click',function(){
        $(this).parent().siblings().find(':checkbox').attr('checked',this.checked).attr('disabled',this.checked);
    });
    $('#content_tab').click(function() {
        $('#site-bottom-bar-content2')
            .find('.sb_dropdown')
            .hide();
    });

    $('#site-bottom-bar-content2').find('.sb_dropdown').click(function(event){
        event.stopPropagation();  //prevent propagating event to parents in DOM
    });
    /*************************date picker***********************************/
    new datepickr('datepicker1', {
        'dateFormat': 'Y/m/d'
    });
    new datepickr('datepicker2', {
        'dateFormat': 'Y/m/d'
    });
    /*
	$('label').find('input:checkbox, input:radio').each(function () {
		
            var input = $(this),
                name = input.attr('name');
				
            input
				.css({'position': 'absolute', 'margin-left': '-9999px'})
				.after('<span>&nbsp;</span>').parents('p').attr('id', name);
				
            var span = input.next('span');
			
            if (input.is(':checked')) {
                span.addClass('selected');
            }
			
            if (input.val() !== '') {
                input.attr('value', input.parent().text());
            }

            input.click(function () {
				input.focus();
                if (input.is(':checkbox')) {
                    span.toggleClass('selected');
                }
                if (input.is(':radio')) {
                    $('input[name="' + name + '"]').next('span').removeClass('selected');
                    span.addClass('selected');
                }
				
                // IE checkmark fix
                if ($.browser.msie) {
                    if (input.is(':checkbox')) {
                        if (input.is(':checked')) {
                            span.append('<em>|</em>');
                        } else {
                            span.children().remove();
                        }
                    }
                }
                /////////////////////////////////
				
            });
        });*/
    /*$(document).ready(function() {
        //$("#tab1").hide(); // Initially hide all content
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
    })();     */
});


function applyCssToCheckbox(labelEle)
{
             $(labelEle).find('input:checkbox, input:radio').each(function () {
                 var input = $(this),
                     name = input.attr('name');

                 input
                     .css({'position': 'absolute', 'margin-left': '-9999px'})
                     .after('<span>&nbsp;</span>').parents('p').attr('id', name);

                 input.attr('checked',false);
                 var span = input.next('span');

                 if (input.is(':checked')) {
                     span.addClass('selected');
                 }

                 if (input.val() !== '') {
                     input.attr('value', input.parent().text());
                 }

                 input.click(function () {
                     input.focus();
                     if (input.is(':checkbox')) {
                         span.toggleClass('selected');
                         if (input.is(':checked')) {
                             input.attr('checked',true);
                         }else
                         {
                             input.attr('checked',false);
                         }

                     }
                     if (input.is(':radio')) {
                         $('input[name="' + name + '"]').next('span').removeClass('selected');
                         span.addClass('selected');
                     }

                     // IE checkmark fix
                     if ($.browser.msie) {
                         if (input.is(':checkbox')) {
                             if (input.is(':checked')) {
                                 span.append('<em>|</em>');
                             } else {
                                 span.children().remove();
                             }
                         }
                     }
                     /////////////////////////////////

                 });
             });
}


