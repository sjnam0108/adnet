<%@ tag pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<!--  HTML tags -->

<div class="clearfix">
	<div class="float-left ml-3 lead header_title">${currDateTitle}</div>
	<div class="float-right mt-2">
		<div class="btn-group mr-2">
			<button id="prev-btn" type="button" class="btn btn-secondary">
				<span class="fa-regular fa-angle-left fa-lg"></span>
			</button>
			<button id="calendar-btn" type="button" class="btn btn-secondary">
				<span class="fa-light fa-calendar-days fa-lg"></span>
			</button>
			<button id="next-btn" type="button" class="btn btn-secondary">
				<span class="fa-regular fa-angle-right fa-lg"></span>
			</button>
		</div>
		<div class="btn-group mr-2">
			<button id="today-btn" type="button" class="btn btn-secondary">
				오늘
			</button>
		</div>
	</div>
</div>

<div class="card">
	<h6 class="card-header with-elements pl-2">
		<span class="lead">
			<span class="fa-stack fa-xs" title="매체" style="vertical-align: top; color: #a0a0a0;">
				<span class="fa-solid fa-circle fa-stack-2x"></span>
				<span class="fa-solid fa-earth-asia fa-stack-1x fa-inverse fa-lg"></span>
			</span>
			<span>${mediumName}</span>
		</span>
		<div class="card-header-elements ml-auto p-0 m-0">
			<span class="lead">${mediumShortName}</span>
		</div>
	</h6>
	<div class="row no-gutters row-bordered row-border-light">
		<div class="col-sm-6 col-md-4 col-xl-2">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-plug fa-3x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">광고 요청 합계</div>
					<div class="text-large">${stat.sumRequest}</div>
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4 col-xl-2">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-flag-checkered fa-3x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">성공 합계</div>
					<div class="text-large">${stat.sumSucc}</div>
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4 col-xl-2">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-cloud-question fa-3x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">실패 합계</div>
					<div class="text-large">${stat.sumFail}</div>
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4 col-xl-2">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="my-2">
					<span class='badge badge-outline-secondary' style='font-weight: 300;'>대체</span>
				</span>
				<div class="ml-3">
					<div class="text-muted small">대체광고 합계</div>
					<div class="text-large">${stat.sumFb}</div>
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4 col-xl-2">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-empty-set fa-3x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">광고없음 합계</div>
					<div class="text-large">${stat.sumNoAd}</div>
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4 col-xl-2">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-screen-users fa-3x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">화면 수</div>
					<div class="text-large">${stat.cntScr}</div>
				</div>
			</div>
		</div>
	</div>
	<hr class="m-0">
	<div class="row no-gutters row-bordered row-border-light">
		<div class="col-sm-6 col-md-4 col-xl-2">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-plug fa-3x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">화면당 광고 요청</div>
					<div class="text-large">${stat.avgRequest}</div>
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4 col-xl-2">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-flag-checkered fa-3x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">화면당 성공</div>
					<div class="text-large">${stat.avgSucc}</div>
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4 col-xl-2">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-cloud-question fa-3x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">화면당 실패</div>
					<div class="text-large">${stat.avgFail}</div>
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4 col-xl-2">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="my-2">
					<span class='badge badge-outline-secondary' style='font-weight: 300;'>대체</span>
				</span>
				<div class="ml-3">
					<div class="text-muted small">화면당 대체광고</div>
					<div class="text-large">${stat.avgFb}</div>
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4 col-xl-2">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-empty-set fa-3x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">화면당 광고없음</div>
					<div class="text-large">${stat.avgNoAd}</div>
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4 col-xl-2">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-map-pin fa-3x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">사이트 수</div>
					<div class="text-large">${stat.cntSit}</div>
				</div>
			</div>
		</div>
	</div>
	<hr class="m-0">
	<div class="row no-gutters row-bordered row-border-light">
		<div class="col-sm-6 col-md-4 col-xl-2">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-ruler-combined fa-3x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">표준 편차</div>
					<div class="text-large">${stat.stdRequest}</div>
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4 col-xl-2">
			<div class="d-flex align-items-center container-p-x py-3">
				<div>
					<div>
						<span class="fa-thin fa-percent fa-3x text-gray"></span>
					</div>
					<span class="pr-2"></span><span class="fa-solid fa-rectangle-wide text-blue"></span>
				</div>
				<div class="ml-3">
					<div class="text-muted small">성공 비율</div>
					<div class="text-large">${stat.pctSucc}</div>
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4 col-xl-2">
			<div class="d-flex align-items-center container-p-x py-3">
				<div>
					<div>
						<span class="fa-thin fa-percent fa-3x text-gray"></span>
					</div>
					<span class="pr-2"></span><span class="fa-solid fa-rectangle-wide text-danger"></span>
				</div>
				<div class="ml-3">
					<div class="text-muted small">실패 비율</div>
					<div class="text-large">${stat.pctFail}</div>
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4 col-xl-2">
			<div class="d-flex align-items-center container-p-x py-3">
				<div>
					<div>
						<span class="fa-thin fa-percent fa-3x text-gray"></span>
					</div>
					<span class="pr-2"></span><span class="fa-solid fa-rectangle-wide text-green"></span>
				</div>
				<div class="ml-3">
					<div class="text-muted small">대체광고 비율</div>
					<div class="text-large">${stat.pctFb}</div>
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4 col-xl-2">
			<div class="d-flex align-items-center container-p-x py-3">
				<div>
					<div>
						<span class="fa-thin fa-percent fa-3x text-gray"></span>
					</div>
					<span class="pr-2"></span><span class="fa-solid fa-rectangle-wide text-muted"></span>
				</div>
				<div class="ml-3">
					<div class="text-muted small">광고없음 비율</div>
					<div class="text-large">${stat.pctNoAd}</div>
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4 col-xl-2">
			<div class="d-flex align-items-center container-p-x py-4">
			</div>
		</div>
	</div>
</div>

<style>

.header_title {
	font-size: 35px;
	font-weight: 300;
}
.header_title small {
	font-size: 20px;
	font-weight: 300;
}

</style>

<!--  / HTML tags -->


<!--  Scripts -->

<script>

var searchIds = "";


function navigateToCamp() {
	
	showWaitModal();
	location.href = "/rev/mediumrpt/camp?date=${currDate}";
}


function navigateToAd() {
	
	showWaitModal();
	location.href = "/rev/mediumrpt/ad?date=${currDate}";
}


function navigateToCreat() {
	
	showWaitModal();
	location.href = "/rev/mediumrpt/creat?date=${currDate}";
}


function navigateToScreen() {
	
	showWaitModal();
	location.href = "/rev/mediumrpt/screen?date=${currDate}";
}


function navigateToSite() {
	
	showWaitModal();
	location.href = "/rev/mediumrpt/site?date=${currDate}";
}


function navigateToSummary() {
	
	showWaitModal();
	location.href = "/rev/mediumrpt/summary?date=${currDate}";
}


function to_date(dateStr) {
	
    var yyyyMMdd = String(dateStr);
    
    var sYear = yyyyMMdd.substring(0, 4);
    var sMonth = yyyyMMdd.substring(5, 7);
    var sDate = yyyyMMdd.substring(8, 10);

    return new Date(Number(sYear), Number(sMonth)-1, Number(sDate));
}


function initCalendarForm() {
	
	$("#formRoot").html(kendo.template($("#template-calendar").html()));
	
	var today = new Date();
	today.setHours(0, 0, 0, 0);

	$("#form-cal div[name='calendar']").kendoCalendar({
		value: to_date("${currDate}"),
        max: today,
    });
	
	$("#form-cal div[name='calendar']").data("kendoCalendar").bind("change", function() {
		$("#form-modal-cal").modal("hide");
		navigateToDate(kendo.toString(this.value(), "yyyy-MM-dd"));
    });	
}


$(document).ready(function() {

	$('[data-toggle="tooltip"]').tooltip();

	// Prev
	$("#prev-btn").click(function(e) {
		e.preventDefault();
		
		navigateToDate("${prevDate}");
	});
	// / Prev

	
	// Next
	$("#next-btn").click(function(e) {
		e.preventDefault();
		
		navigateToDate("${nextDate}");
	});
	// / Next

	
	// Today
	$("#today-btn").click(function(e) {
		e.preventDefault();
		
		navigateToDate("${today}");
	});
	// / Today

	
	// Calendar
	$("#calendar-btn").click(function(e) {
		e.preventDefault();
		
		initCalendarForm();

		
		$('#form-modal-cal .modal-dialog').draggable({ handle: '.modal-header' });
		$("#form-modal-cal").modal();
	});
	// / Calendar
	
});	
</script>

<!--  / Scripts -->


<!--  Forms -->

<script id="template-calendar" type="text/x-kendo-template">

<div class="modal fade" id="form-modal-cal">
	<div class="modal-dialog modal-sm">
		<form class="modal-content" id="form-cal" rowid="-1" url="${createUrl}">
      
			<!-- Modal Header -->
			<div class="modal-header move-cursor">
				<h5 class="modal-title">
					대상일
					<span class="font-weight-light pl-1">선택</span>
				</h5>
				<button type="button" class="close" data-dismiss="modal">×</button>
			</div>
        
			<!-- Modal body -->
			<div class="modal-body modal-bg-color">
				<div style="text-align: center;">
					<div name="calendar"></div>
				</div>
			</div>
			
		</form>
	</div>
</div>

</script>

<!--  / Forms -->
