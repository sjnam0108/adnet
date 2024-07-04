<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>

<%@ taglib prefix="rev" tagdir="/WEB-INF/tags/rev"%>


<!-- URL -->

<c:url value="/rev/mediumrpt/site/readSitTot" var="readSitTotUrl" />
<c:url value="/rev/mediumrpt/summary/readTimeChart" var="readTimeChartUrl" />


<!-- Opening tags -->

<common:mediumPageOpening />


<!-- Page title -->

<h4 class="pt-1 pb-3 mb-3">
	<span class="text-muted font-weight-light">${pageTitle}<span class="px-2">/</span></span>
	<span class="mr-1 fa-light fa-ballot"></span><span class="pl-1">요약</span>
</h4>

<hr class="border-light container-m--x mt-0 mb-3">






<!-- Page body -->


<!--  Overview header -->

<rev:medium-summary />


<!--  Scripts -->

<script>

function navigateToDate(date) {
	
	showWaitModal();
	location.href = "/rev/mediumrpt/summary?date=" + date;
}

</script>

<!--  / Overview header -->


<!--  Tab -->
<ul class="nav nav-tabs tabs-alt mb-4 mt-4">
	<li class="nav-item">
		<a class="nav-link active" href="javascript:navigateToSummary();">
			<i class="mr-1 fa-light fa-ballot"></i>
			요약
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" href="javascript:navigateToCamp();">
			<i class="mr-1 fa-light fa-briefcase"></i>
			캠페인
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" href="javascript:navigateToAd();">
			<i class="mr-1 fa-light fa-audio-description"></i>
			광고
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" href="javascript:navigateToCreat();">
			<i class="mr-1 fa-light fa-clapperboard-play"></i>
			광고 소재
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" href="javascript:navigateToScreen();">
			<i class="mr-1 fa-light fa-screen-users"></i>
			화면
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" href="javascript:navigateToSite();">
			<i class="mr-1 fa-light fa-map-pin"></i>
			사이트
		</a>
	</li>
</ul>
<!--  / Tab -->


<!--  Root form container -->
<div id="formRoot"></div>


<!--  Page details -->


<!-- Chart area -->

<div class="mb-4">
<div class="card" style="border-bottom: transparent;">
	<h6 class="card-header with-elements py-2">
		<span class="lead">
			<span class="fa-light fa-gear fa-lg"></span>
			<span class="ml-1">서비스 응답</span>
		</span>
		<div class="card-header-elements ml-auto">

<c:if test="${not stat.dataNotFound}">

			<div class="btn-group btn-group-toggle" data-toggle="buttons">
				<label class="btn btn-outline-secondary btn-sm active">
					<input type="radio" name="chart-type-radio" value="C" checked>
						<span class='fa-light fa-chart-column fa-lg mx-2'></span>
					</input>
				</label>
				<label class="btn btn-outline-secondary btn-sm">
					<input type="radio" name="chart-type-radio" value="100">
						100%
					</input>
				</label>
			</div>

</c:if>
		
		</div>
	</h6>
	
<c:choose>
<c:when test="${not stat.dataNotFound}">
	
	<div class="row no-gutters row-bordered">
		<div class="m-3 d-flex">
			<div id="summary-chart-control" class="align-self-center" style="height: 220px; width: 220px;">
			</div>
		</div>
		<div class="col">
			<div class="m-3">
				<div id="summary-time-chart-control" class="align-self-center" style="height: 250px;">
				</div>
			</div>
		</div>
	</div>

</c:when>
<c:when test="${stat.dataNotFound}">
	
	<div class='container text-center my-4'>
		<div class='d-flex justify-content-center align-self-center my-4'>
			<span class='fa-thin fa-hexagon-exclamation fa-3x'></span>
			<span class='pl-3 mt-2' style='font-weight: 300; font-size: 1.3rem;'>해당 자료 없음</span>
		</div>
	</div>

</c:when>
</c:choose>

</div>
</div>

<script id="template" type="text/x-kendo-template">

<div class="card">
	<h6 class="popover-header"><span class="font-weight-600">#: category #</span><span class="font-weight-400">시 정각부터 한시간</span></h6>
	<div class="popover-body">
		<div>
			<span class="fa-solid fa-rectangle-wide fa-xs text-blue"></span><span class="pl-1">성공:</span>
			<span class="font-weight-500 pl-1">#: kendo.format("{0:n0}", points[0].value) #</span>
		</div>
		<div>
			<span class="fa-solid fa-rectangle-wide fa-xs text-danger"></span><span class="pl-1">실패:</span>
			<span class="font-weight-500 pl-1">#: kendo.format("{0:n0}", points[1].value) #</span>
		</div>
		<div>
			<span class="fa-solid fa-rectangle-wide fa-xs text-green"></span><span class="pl-1">대체광고:</span>
			<span class="font-weight-500 pl-1">#: kendo.format("{0:n0}", points[2].value) #</span>
		</div>
		<div>
			<span class="fa-solid fa-rectangle-wide fa-xs text-muted"></span><span class="pl-1">광고없음:</span>
			<span class="font-weight-500 pl-1">#: kendo.format("{0:n0}", points[3].value) #</span>
		</div>
		<hr class="container-m-nx border-light my-2">
		<div>
			<span class="fa-light fa-sigma fa-xs"></span><span class="pl-1">요청합계:</span>
			<span class="font-weight-500 pl-1">#: kendo.format("{0:n0}", points[0].value + points[1].value + points[2].value + points[3].value) #</span>
		</div>
	</div>
</div>
</script>

<script>

var chartLeft = null, chartRight = null;
var chartRightData = [];

$(document).ready(function() {
	
	window.dispatchEvent(new Event('resize'));
	
	// 차트 유형
	$("input[name='chart-type-radio']").change(function(){
		
		refreshChart();

	});

});

window.addEventListener('resize', function(event){
	
	if (chartLeft == null) {
	    
		createLeftChart();
		
		chartLeft = $("#summary-chart-control").data("kendoChart");
	}
	if (chartRight == null) {
	    
		createRightChart();
		
		chartRight = $("#summary-time-chart-control").data("kendoChart");
	}
	
	if (chartRight != null) {
		chartRight.dataSource.data(chartRightData);
		chartRight.refresh();
	}
	
});


function refreshChart() {
	
	if (chartRight != null) {
		
		var typeVal = $("input[name='chart-type-radio']:checked").val();

		if (typeVal == 'C') {
			chartRight.options.series[0].stack.type = "normal";
		} else {
			chartRight.options.series[0].stack.type = "100%";
		}
		
		chartRight.refresh();
	}
}


function readChartData() {
	
	$.ajax({
		type: "POST",
		contentType: "application/json",
		dataType: "json",
		url: "${readTimeChartUrl}",
		data: JSON.stringify({ reqStrValue1: "${currDate}" }),
		success: function (data, status) {

			chartRightData = data;
			chartRight.dataSource.data(chartRightData);

			refreshChart();
		},
		error: ajaxOperationError
	});
}


function toShortenQualifierPlusPct(value) {

	if (value && !isNaN(value)) {
		if (value >= 1000000000) {
			if (value / 1000000000 == kendo.parseInt(value / 1000000000)) {
				return kendo.format('{0:n0}', kendo.parseInt(value / 1000000000)) + "b";
			} else {
				return kendo.format('{0:n1}', kendo.parseInt(value / 100000000) / 10) + "b";
			}
		} else if (value >= 1000000) {
			if (value / 1000000 == kendo.parseInt(value / 1000000)) {
				return kendo.format('{0:n0}', kendo.parseInt(value / 1000000)) + "m";
			} else {
				return kendo.format('{0:n1}', kendo.parseInt(value / 100000) / 10) + "m";
			}
		} else if (value >= 1000) {
			if (value / 1000 == kendo.parseInt(value / 1000)) {
				return kendo.format('{0:n0}', kendo.parseInt(value / 1000)) + "k";
			} else {
				return kendo.format('{0:n1}', kendo.parseInt(value / 100) / 10) + "k";
			}
		}
		
		var typeVal = $("input[name='chart-type-radio']:checked").val();
		if (typeVal == "100") {
			return kendo.format('{0:p0}', value);
		}
		
	}
	
	return value;
}


function createLeftChart() {
	
	var pctSucc = 0;
	var pctFail = 0;
	var pctFb = 0;
	var pctNoAd = 0;
	
<c:if test="${not empty stat.pctSucc}">
	pctSucc = ${stat.pctSucc};
</c:if>
<c:if test="${not empty stat.pctFail}">
	pctFail = ${stat.pctFail};
</c:if>
<c:if test="${not empty stat.pctFb}">
	pctFb = ${stat.pctFb};
</c:if>
<c:if test="${not empty stat.pctNoAd}">
	pctNoAd = ${stat.pctNoAd};
</c:if>
	
	
    $("#summary-chart-control").kendoChart({
    	
        chartArea: {
            margin: 0,
        },
		legend: {
			visible: false,
		},
		dataSource: {
			data: [{
				"service": "성공",
				"pct": pctSucc
			}, {
				"service": "실패",
                "pct": pctFail
			}, {
				"service": "대체광고",
                "pct": pctFb
			}, {
				"service": "광고없음",
                "pct": pctNoAd
			}]
		},
        series: [{
            type: "donut",
            field: "pct",
            categoryField: "service",
            padding: 10,
        }],
        seriesColors: ["#487df2", "#d9534f", "#70cc33", "#a3a4a6"],
        tooltip: {
            visible: true,
			font: "13px 'Roboto', 'Noto Sans', 'Noto Sans CJK KR', -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Oxygen', 'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue'",
            template: "#= dataItem.service # - #= dataItem.pct # %",
            padding: 0,
        },
        /*
		legendItemClick: function(e) {
			e.preventDefault();
		},
		legendItemHover: function(e) {
			e.preventDefault();
		},
		legendItemLeave: function(e) {
			e.preventDefault();
		},
		*/
		transitions: false,
    	
    });

}


function createRightChart() {
	
    $("#summary-time-chart-control").kendoChart({
    	
        chartArea: {
            margin: 0,
        },
        dataSource: {
            data: chartRightData
        },
		legend: {
			visible: false,
		},
    	
		seriesDefaults: {
			type: "column",
			//stack: { type: "100%" }
			stack: { type: "normal" }
		},
		series: [{
			field: "succ",
			categoryField: "hour",
			name: "성공"
		},{
			field: "fail",
			categoryField: "hour",
			name: "실패"
		},{
			field: "fb",
			categoryField: "hour",
			name: "대체광고"
		},{
			field: "noAd",
			categoryField: "hour",
			name: "광고없음"
		}],
		seriesColors: ["#487df2", "#d9534f", "#70cc33", "#a3a4a6"],
        valueAxis: [{
			labels: {
				template: "#= toShortenQualifierPlusPct(value) #"
			}
		}],
		tooltip: {
			visible: true,
			shared: true,
			sharedTemplate: kendo.template($("#template").html()),
        	font: "13px 'Roboto', 'Noto Sans', 'Noto Sans CJK KR', -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Oxygen', 'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue'",
			background: "transparent",
			border: {
				width: 0
			}
		}
    });

}

</script>

<!-- / Chart area -->



<!-- Grid button actions  -->

<script>
$(document).ready(function() {

<c:if test="${not stat.dataNotFound}">

	readChartData();
	
</c:if>
			
});
</script>

<!-- / Grid button actions  -->


<!-- Common styles  -->


<!-- / Common styles  -->


<!-- / Page body -->





<!-- Functional tags -->



<!-- Closing tags -->

<common:base />
<common:pageClosing />
