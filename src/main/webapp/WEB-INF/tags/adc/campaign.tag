<%@ tag pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<!-- URL -->

<c:url value="/adc/campaign/recalcDailyAchves" var="recalcDailyAchvUrl" />
<c:url value="/adc/campaign/recalcTodayTarget" var="recalcTodayTargetUrl" />


<!--  HTML tags -->

<div class="card">
	<h6 class="card-header with-elements pl-2">
		<span class="lead">
			<span class="fa-stack fa-xs" title="캠페인" style="vertical-align: top; color: #a0a0a0;">
				<span class="fa-solid fa-circle fa-stack-2x"></span>
				<span class="fa-solid fa-briefcase fa-stack-1x fa-inverse fa-lg"></span>
			</span>
			<span id="campaign-name"></span>
		</span>
		<div class="card-header-elements ml-auto p-0 m-0">
			<button type="button" class="btn btn-outline-blue btn-round icon-btn btn-custom mr-3" id="qlb-campaign-report" title="리포트 조회">
				<i class="fa-light fa-file-invoice fa-lg"></i>
			</button>
			<button type="button" class="btn btn-outline-dark btn-round icon-btn btn-custom mr-1" id="qlb-advertiser" title="광고주 목록">
				<i class="fa-light fa-user-tie-hair fa-lg"></i>
			</button>
			<button type="button" class="btn btn-outline-dark btn-round icon-btn btn-custom mr-1" id="qlb-campaign" title="캠페인 목록">
				<i class="fa-light fa-briefcase fa-lg"></i>
			</button>
			<button type="button" class="btn btn-outline-dark btn-round icon-btn btn-custom mr-1" id="qlb-ad" title="광고 목록">
				<i class="fa-light fa-audio-description fa-lg"></i>
			</button>
			<button type="button" class="btn btn-outline-dark btn-round icon-btn btn-custom mr-3" id="qlb-creative" title="광고 소재 목록">
				<i class="fa-light fa-clapperboard-play fa-lg"></i>
			</button>
			<div class="btn-group">
				<button type="button" class="btn btn-outline-secondary btn-round icon-btn btn-custom" data-toggle="dropdown" title="이 캠페인에 대해 ...">
					<i class="fa-light fa-ellipsis-stroke fa-lg"></i>
				</button>
				<div class="dropdown-menu">
					<h6 class="dropdown-header lead">선택한 캠페인에 대해</h6>
					<a class="dropdown-item" href="javascript:void(0)" id="qlb-camp-daily-achv-btn">
						<i class="fa-light fa-calendar-days fa-fw"></i><span class="pl-2">일자별 목표 달성률 재계산</span>
					</a>
					<a class="dropdown-item" href="javascript:void(0)" id="qlb-camp-today-target-btn">
						<i class="fa-light fa-golf-flag-hole fa-fw"></i><span class="pl-2">오늘 목표값 재계산</span>
					</a>
				</div>    			
			</div>
		</div>
	</h6>
	<div class="row no-gutters row-bordered row-border-light">
		<div class="col-sm-6 col-md-4">
			<div class="d-flex align-items-center container-p-x py-4">
			
<c:choose>
<c:when test="${Campaign.status == 'U'}">

				<span class="fa-thin fa-alarm-clock fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">캠페인 상태</div>
					<div class="text-large">예약</div>
				</div>
				
</c:when>
<c:when test="${Campaign.status == 'R'}">

				<span class="fa-thin fa-bolt-lightning fa-3x text-orange fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">캠페인 상태</div>
					<div class="text-large">진행</div>
				</div>
				
</c:when>
<c:when test="${Campaign.status == 'C'}">

				<span class="fa-thin fa-flag-checkered fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">캠페인 상태</div>
					<div class="text-large">완료</div>
				</div>
				
</c:when>
<c:when test="${Campaign.status == 'V'}">

				<span class="fa-thin fa-box-archive  fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">캠페인 상태</div>
					<div class="text-large">보관</div>
				</div>
				
</c:when>
</c:choose>
			
			</div>
		</div>
		<div class="col-sm-6 col-md-4">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-calendar-range fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">캠페인 기간</div>
					<div class="text-large">
						${Campaign.dispPeriod}
					</div>
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-user-tie-hair fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">광고주</div>
					<div class="text-large">
						${Campaign.advertiser.name}
						<a href='javascript:navToAdv(${Campaign.advertiser.id})' class='btn btn-default btn-xs icon-btn ml-1'>
						<span class='fa-regular fa-arrow-right'></span>
						</a>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>


<!-- Form button actions  -->

<script>
$(document).ready(function() {

	// Quick Link Buttons
	$("#qlb-campaign-report").click(function(e) {
		e.preventDefault();
		
		location.href = "/rev/camprpt/ad/${Campaign.id}";
	});
	
	$("#qlb-advertiser").click(function(e) {
		e.preventDefault();
		
		location.href = "/org/advertiser";
	});
	
	$("#qlb-campaign").click(function(e) {
		e.preventDefault();
		
		location.href = "/adc/campaign";
	});
	
	$("#qlb-ad").click(function(e) {
		e.preventDefault();
		
		location.href = "/adc/ad";
	});
	
	$("#qlb-creative").click(function(e) {
		e.preventDefault();
		
		location.href = "/adc/creative";
	});
	// / Quick Link Buttons

	var campTitle = "<span>${Campaign.name}</span>";
	if ('${Campaign.statusCard}' == 'Y') {
		campTitle = campTitle + "<span class='pl-1'></span><span title='오늘 진행되는 광고 소재 없음'><span class='fa-solid fa-rectangle-vertical text-yellow'></span></span>";
	} else if ('${Campaign.statusCard}' == 'R') {
		campTitle = campTitle + "<span class='pl-1'></span><span title='활성중인 광고 소재 없음'><span class='fa-solid fa-rectangle-vertical text-danger'></span></span>";
	}
	
	$("#campaign-name").html(campTitle);
});


function navToAdv(advId) {
	var path = "/adc/creative/creatives/" + advId;
	location.href = path;
}

</script>


<script>

$(document).ready(function() {

	// Quick Link Buttons - ellipsis
	$("#qlb-camp-daily-achv-btn").click(function(e) {
		e.preventDefault();

		qlbRequest("${recalcDailyAchvUrl}", ${Campaign.id});
	});
	
	$("#qlb-camp-today-target-btn").click(function(e) {
		e.preventDefault();

		qlbRequest("${recalcTodayTargetUrl}", ${Campaign.id});
	});
	// / Quick Link Buttons - ellipsis

});


function qlbRequest(url, id) {
	
	showWaitModal();
	
	$.ajax({
		type: "POST",
		contentType: "application/json",
		dataType: "json",
		url: url,
		data: JSON.stringify({ id: id }),
		success: function (data, status, xhr) {
			hideWaitModal();
			showOperationSuccessMsg();
		},
		error: function(e) {
			hideWaitModal();
			
			var msg = JSON.parse(e.responseText).error;
			if (msg == "OperationError") {
				showOperationErrorMsg();
			} else {
				showAlertModal("danger", msg);
			}
		}
	});
}

</script>


<style>

/* 아웃라인 버튼 파란색 */
.btn-outline-blue {
    border-color: rgba(72,125,242,0.9);
    background: transparent;
    color: rgba(72,125,242,0.9);
}
.btn-outline-blue:hover {
    border-color: transparent;
    background: rgba(72,125,242,0.9);
    color: #fff;
}


/* 그리드 행의 높이 지정 */
.card-table tbody tr, .k-grid tbody tr td
{
    height: 40px;
}

</style>

<!-- / Form button actions  -->

<!--  / HTML tags -->

