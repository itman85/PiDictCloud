var columnChart =(function() {
	return function(containerId,arrDataPoints,chartTitle,axisXTitle,axisYTitle,boxpopupId,boxpopupContentId){
		var chart = new CanvasJS.Chart(containerId, {
			title : {
				text : chartTitle,
			},
			axisX : {
				title : axisXTitle,	
				valueFormatString: "MM/DD",
				interval: 14,
				intervalType: "day",
				labelAngle : 75,
				labelFontSize : 11,
				labelFontFamily : "Arial",

			},
			axisY : {
				title : axisYTitle,
				interval : 5,
				gridThickness : 0.3,
				minimum: 0,
			},
			data : [ {
				click: function(e){
					$('#' + boxpopupContentId).html("<b>"+e.dataPoint.label + " : " + e.dataPoint.y + "</b><br/><br/><p align=\"left\">+&nbsp" + e.dataPoint.name+"</p>");
			          //alert(e.dataPoint.label + " : " + e.dataPoint.y + "\r\n" + e.dataPoint.name);
					openOffersDialog(boxpopupId);
			        },
				type : "column",
				lineThickness : 5,
				toolTipContent : "{label} : <strong>{y}</strong>",
				dataPoints : arrDataPoints
			} ]
		});
		chart.render();
	}
})();