<%@ tag pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<!-- URL -->

<c:url value="/adc/campaign/recalcDailyAchves" var="recalcDailyAchvUrl" />
<c:url value="/adc/campaign/recalcTodayTarget" var="recalcTodayTargetUrl" />
<c:url value="/adc/ad/recalcDailyAchves" var="recalcAdDailyAchvUrl" />
<c:url value="/adc/ad/recalcTodayTarget" var="recalcAdTodayTargetUrl" />


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
				<button type="button" class="btn btn-outline-secondary btn-round icon-btn btn-custom" data-toggle="dropdown">
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

<div class="card">
	<h6 class="card-header with-elements pl-2">
		<span class="lead">
			<span class="fa-stack fa-xs" title="광고" style="vertical-align: top; color: #a0a0a0;">
				<span class="fa-solid fa-circle fa-stack-2x"></span>
				<span class="fa-solid fa-audio-description fa-stack-1x fa-inverse"></span>
			</span>
			<span id="ad-name"></span>
		</span>
		<div class="card-header-elements ml-auto">
			<a href='javascript:summaryToggleOff()' class='btn btn-default btn-xs ml-1' id="summary-toggle-on-btn">
				<span class="fa-light fa-toggle-on text-orange"></span><span class="pl-1">요약</span>
			</a>
			<a href='javascript:summaryToggleOn()' class='btn btn-default btn-xs ml-1' style="display: none;" id="summary-toggle-off-btn">
				<span class="fa-light fa-toggle-off text-green"></span><span class="pl-1">상세</span>
			</a>
		</div>
		<div class="btn-group ml-3">
			<button type="button" class="btn btn-outline-secondary btn-round icon-btn btn-custom" data-toggle="dropdown">
				<i class="fa-light fa-ellipsis-stroke fa-lg"></i>
			</button>
			<div class="dropdown-menu">
				<h6 class="dropdown-header lead">선택한 광고에 대해</h6>
				<a class="dropdown-item" href="javascript:void(0)" id="qlb-ad-daily-achv-btn">
					<i class="fa-light fa-calendar-days fa-fw"></i><span class="pl-2">일자별 목표 달성률 재계산</span>
				</a>
				<a class="dropdown-item" href="javascript:void(0)" id="qlb-ad-today-target-btn">
					<i class="fa-light fa-golf-flag-hole fa-fw"></i><span class="pl-2">오늘 목표값 재계산</span>
				</a>
			</div>    			
		</div>
	</h6>
	<div class="row no-gutters row-bordered row-border-light" id="summary-toggle-root-div">
			
		<div class="col-sm-6 col-md-4" custom="1">
			<div class="d-flex align-items-center container-p-x py-4">
			
<c:choose>
<c:when test="${Ad.status == 'D'}">

				<span class="fa-thin fa-asterisk fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">광고 상태</div>
					<div class="text-large">준비</div>
				</div>
				
</c:when>
<c:when test="${Ad.status == 'P'}">

				<span class="fa-thin fa-quare-question fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">광고 상태</div>
					<div class="text-large">승인대기</div>
				</div>
				
</c:when>
<c:when test="${Ad.status == 'J'}">

				<span class="fa-thin fa-do-not-enter fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">광고 상태</div>
					<div class="text-large">거절</div>
				</div>
				
</c:when>
<c:when test="${Ad.status == 'A'}">

				<span class="fa-thin fa-alarm-clock fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">광고 상태</div>
					<div class="text-large">예약</div>
				</div>
				
</c:when>
<c:when test="${Ad.status == 'R'}">

				<span class="fa-thin fa-bolt-lightning fa-3x text-orange fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">광고 상태</div>
					<div class="text-large">진행</div>
				</div>
				
</c:when>
<c:when test="${Ad.status == 'C'}">

				<span class="fa-thin fa-flag-checkered fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">광고 상태</div>
					<div class="text-large">완료</div>
				</div>
				
</c:when>
<c:when test="${Ad.status == 'V'}">

				<span class="fa-thin fa-box-archive  fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">광고 상태</div>
					<div class="text-large">보관</div>
				</div>
				
</c:when>
</c:choose>
			
			</div>
		</div>
		<div class="col-sm-6 col-md-4" custom="2" id="summary-toggle-period">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-calendar-range fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">광고 기간</div>
					<div class="text-large">
		
	<c:choose>
	<c:when test="${Ad.dispPeriod eq Campaign.dispPeriod}">
			
						캠페인과 동일
						
	</c:when>
	<c:otherwise>
	
						${Ad.dispPeriod}
						
	</c:otherwise>
	</c:choose>
		
					</div>
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4" custom="1">
			<div class="d-flex align-items-center container-p-x py-4">
			
	<c:choose>
	<c:when test="${Ad.purchType == 'G'}">

				<span class="fa-thin fa-hexagon-check fa-3x text-blue fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">구매 유형</div>
					<div class="text-large">목표 보장.${Ad.priority}</div>
				</div>
				
	</c:when>
	<c:when test="${Ad.purchType == 'N'}">

				<span class="fa-thin fa-hexagon-exclamation fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">구매 유형</div>
					<div class="text-large">목표 비보장.${Ad.priority}</div>
				</div>
				
	</c:when>
	<c:when test="${Ad.purchType == 'H'}">

				<span class="fa-thin fa-house fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">구매 유형</div>
					<div class="text-large">하우스 광고</div>
				</div>
				
	</c:when>
	</c:choose>
			
			</div>
		</div>


<c:if test="${Ad.purchType ne 'H'}">
		
		<div class="col-sm-6 col-md-4" custom="2" id="summary-toggle-budget">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-sack-dollar fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">광고 예산</div>
					
		<c:choose>
		<c:when test="${Ad.budget == 0}">
					
					<div class="text-large">설정 없음</div>

		</c:when>
		<c:otherwise>
					
					<div class="text-large"><span id="ad-budget"></span> 원</div>

		</c:otherwise>
		</c:choose>
					
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4" custom="2" id="summary-toggle-goal-value">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-hexagon-check fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">보장 노출량</div>
					
		<c:choose>
		<c:when test="${Ad.goalValue == 0}">
					
					<div class="text-large">설정 없음</div>

		</c:when>
		<c:otherwise>
					
					<div class="text-large"><span id="ad-goal-value"></span> 회</div>

		</c:otherwise>
		</c:choose>
					
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4" custom="2" id="summary-toggle-sys-value">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-golf-flag-hole fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">목표 노출량</div>
					
		<c:choose>
		<c:when test="${Ad.sysValue == 0}">
					
					<div class="text-large">설정 없음</div>

		</c:when>
		<c:otherwise>
					
					<div class="text-large"><span id="ad-sys-value"></span> 회</div>

		</c:otherwise>
		</c:choose>
					
				</div>
			</div>
		</div>
		

		<div class="col-sm-6 col-md-4" custom="1">
			<div class="d-flex align-items-center container-p-x py-4">
			
	<c:choose>
	<c:when test="${Ad.goalType == 'A'}">

				<span class="fa-thin fa-sack-dollar fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">집행 방법</div>
					<div class="text-large">광고 예산</div>
				</div>

	</c:when>
	<c:when test="${Ad.goalType == 'I'}">

				<span class="fa-thin fa-eye fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">집행 방법</div>
					<div class="text-large">노출량</div>
				</div>

	</c:when>
	<c:when test="${Ad.goalType == 'U'}">

				<span class="fa-thin fa-infinity fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">집행 방법</div>
					<div class="text-large">무제한 노출</div>
				</div>

	</c:when>
	</c:choose>
					
			</div>
		</div>
		
</c:if>

		
		<div class="col-sm-6 col-md-4" custom="2" id="summary-toggle-imp-daily-type">
			<div class="d-flex align-items-center container-p-x py-4">

<c:choose>
<c:when test="${Ad.impDailyType == 'E'}">

				<span class="fa-thin fa-equals fa-3x text-gray"></span>
				
</c:when>
<c:when test="${Ad.impDailyType == 'W'}">

				<span class="fa-thin fa-bars-staggered fa-3x text-gray"></span>
				
</c:when>
</c:choose>
				<div class="ml-3">
					<div class="text-muted small">일별 광고 분산</div>
<c:choose>
<c:when test="${Ad.impDailyType == 'E'}">

					<div class="text-large">모든 날짜 균등</div>

</c:when>
<c:when test="${Ad.impDailyType == 'W'}">

					<div class="text-large">통계 기반 요일별 차등</div>

</c:when>
</c:choose>

				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4" custom="2" id="summary-toggle-imp-hourly-type">
			<div class="d-flex align-items-center container-p-x py-4">

<c:choose>
<c:when test="${Ad.impHourlyType == 'E'}">

				<span class="fa-thin fa-equals fa-3x text-gray"></span>
				
</c:when>
<c:when test="${Ad.impHourlyType == 'D'}">

				<span class="fa-thin fa-sun fa-3x text-gray"></span>
				
</c:when>
</c:choose>
				<div class="ml-3">
					<div class="text-muted small">하루 광고 분산</div>
<c:choose>
<c:when test="${Ad.impHourlyType == 'E'}">

					<div class="text-large">모든 시간 균등</div>

</c:when>
<c:when test="${Ad.impHourlyType == 'D'}">

					<div class="text-large">일과 시간 집중</div>

</c:when>
</c:choose>

				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4" custom="2" id="summary-toggle-view-type">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-sidebar fa-3x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">게시 유형</div>
<c:choose>
<c:when test="${not empty Ad.viewTypeCode}">

					<div class="text-large">
						<span class="pr-2">${Ad.viewTypeCode}</span><small><span class="text-muted">${Ad.fixedResolution}</span></small>
					</div>

</c:when>
<c:otherwise>

					<div class="text-large">매체 기본 유형</div>

</c:otherwise>
</c:choose>
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4" custom="2" id="summary-toggle-daily-cap">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-arrow-right-to-line fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">하루 노출한도</div>
					
		<c:choose>
		<c:when test="${Ad.dailyCap == 0}">
					
					<div class="text-large">설정 없음</div>

		</c:when>
		<c:otherwise>
					
					<div class="text-large"><span id="ad-daily-cap"></span> 회</div>

		</c:otherwise>
		</c:choose>
					
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4" custom="2" id="summary-toggle-daily-scr-cap">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-arrow-right-to-line fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">화면당 하루 노출한도</div>
					
		<c:choose>
		<c:when test="${Ad.dailyScrCap == 0}">
					
					<div class="text-large">설정 안함</div>

		</c:when>
		<c:otherwise>
					
					<div class="text-large"><span id="ad-daily-scr-cap"></span> 회</div>

		</c:otherwise>
		</c:choose>
					
				</div>
			</div>
		</div>
		
		<div class="col-sm-6 col-md-4" custom="2" id="summary-toggle-freq-cap">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-hand fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">동일 광고 송출 금지</div>
					
		<c:choose>
		<c:when test="${Ad.freqCap == 0}">
					
					<div class="text-large">매체 설정값 적용</div>

		</c:when>
		<c:when test="${Ad.freqCap == 1}">
					
					<div class="text-large">설정 안함</div>

		</c:when>
		<c:otherwise>
					
					<div class="text-large"><span id="ad-freq-cap"></span> 초</div>

		</c:otherwise>
		</c:choose>
					
				</div>
			</div>
		</div>

		
<c:if test="${Ad.purchType ne 'H'}">
		
		<div class="col-sm-6 col-md-4" custom="2" id="summary-toggle-cpm">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-coin fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">CPM</div>
					
		<c:choose>
		<c:when test="${Ad.cpm == 0}">
					
					<div class="text-large">화면 설정값</div>

		</c:when>
		<c:otherwise>
					
					<div class="text-large"><span id="ad-cpm"></span> 원</div>

		</c:otherwise>
		</c:choose>
					
				</div>
			</div>
		</div>
		
</c:if>


		<div class="col-sm-6 col-md-4" custom="2" id="summary-toggle-duration">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-timer fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">재생 시간</div>
					
		<c:choose>
		<c:when test="${Ad.duration == 0}">
					
					<div class="text-large">화면 설정값</div>

		</c:when>
		<c:otherwise>
					
					<div class="text-large"><span id="ad-duration"></span> 초</div>

		</c:otherwise>
		</c:choose>
					
				</div>
			</div>
		</div>

		
<c:if test="${(Ad.status == 'A' or Ad.status == 'R')}">
		
		<div class="col-sm-6 col-md-4" custom="2" id="summary-toggle-imp-add-ratio">
			<div class="d-flex align-items-center container-p-x py-4">
					
		<c:choose>
		<c:when test="${Ad.impAddRatio gt 0}">
					
				<span class="fa-thin fa-up-from-line fa-3x text-success fa-fw"></span>

		</c:when>
		<c:when test="${Ad.impAddRatio eq 0}">
					
				<span class="fa-thin fa-snooze fa-3x text-gray fa-fw"></span>

		</c:when>
		<c:otherwise>
					
				<span class="fa-thin fa-down-from-line fa-3x text-danger fa-fw"></span>

		</c:otherwise>
		</c:choose>
				
				<div class="ml-3">
					<div class="text-muted small">노출량 추가 제어</div>
					<div class="text-large">${Ad.impAddRatioDisp}</div>
				</div>
			</div>
		</div>
		
</c:if>

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
	

	var title = "";
	if (${Ad.paused} == true) {
		title = "<span title='잠시 멈춤'><span class='fa-light fa-circle-pause text-danger'></span></span><span class='pr-1'></span>";
	}
	title = title + "<span>${Ad.name}</span>";
	if (${Ad.mobTargeted} == true) {
		title = title + "<span class='pl-1'></span><span title='모바일 타겟팅'><span class='fa-light fa-bullseye-arrow text-orange'></span></span>";
	} else if (${Ad.invenTargeted} == true) {
		title = title + "<span class='pl-1'></span><span title='인벤토리 타겟팅'><span class='fa-light fa-bullseye-arrow text-blue'></span></span>";
	}
	if (${Ad.timeTargeted} == true) {
		title = title + "<span class='pl-1'></span><span title='시간 타겟팅'><span class='fa-light fa-alarm-clock text-green'></span></span>";
	}
	if ('${Ad.statusCard}' == 'Y') {
		title = title + "<span class='pl-1'></span><span title='오늘 진행되는 광고 소재 없음'><span class='fa-solid fa-rectangle-vertical text-yellow'></span></span>";
	} else if ('${Ad.statusCard}' == 'R') {
		title = title + "<span class='pl-1'></span><span title='활성중인 광고 소재 없음'><span class='fa-solid fa-rectangle-vertical text-danger'></span></span>";
	}
	
	var resolutions = "<span class='pl-3'>" + dispResoBadgeValues("${Ad.resolutions}") + "</span>";
	$("#ad-name").html(title + resolutions);
	
	
	var regexp = /\B(?=(\d{3})+(?!\d))/g;
	$("#ad-budget").html("${Ad.budget}".replace(regexp, ','));
	$("#ad-goal-value").html("${Ad.goalValue}".replace(regexp, ','));
	$("#ad-sys-value").html("${Ad.sysValue}".replace(regexp, ','));
	$("#ad-daily-cap").html("${Ad.dailyCap}".replace(regexp, ','));
	$("#ad-daily-scr-cap").html("${Ad.dailyScrCap}".replace(regexp, ','));
	$("#ad-freq-cap").html("${Ad.freqCap}".replace(regexp, ','));
	$("#ad-cpm").html("${Ad.cpm}".replace(regexp, ','));
	$("#ad-duration").html("${Ad.duration}".replace(regexp, ','));
	
	
	if ('${Ad.dispPeriod}' == '${Campaign.dispPeriod}') {
		$("#summary-toggle-period").attr("custom", "3");
	}
	if ('${Ad.purchType}' != 'H') {
		if (${Ad.budget} == 0) {
			$("#summary-toggle-budget").attr("custom", "3");
		}
		if (${Ad.goalValue} == 0) {
			$("#summary-toggle-goal-value").attr("custom", "3");
		}
		if (${Ad.sysValue} == 0) {
			$("#summary-toggle-sys-value").attr("custom", "3");
		}
	}
	if ('${Ad.impDailyType}' == '${Ad.mediumImpDailyType}') {
		$("#summary-toggle-imp-daily-type").attr("custom", "3");
	}
	if ('${Ad.impHourlyType}' == '${Ad.mediumImpHourlyType}') {
		$("#summary-toggle-imp-hourly-type").attr("custom", "3");
	}
	if ('${Ad.viewTypeCode}' == '') {
		$("#summary-toggle-view-type").attr("custom", "3");
	}
	if (${Ad.dailyCap} == 0) {
		$("#summary-toggle-daily-cap").attr("custom", "3");
	}
	if (${Ad.dailyScrCap} == 0) {
		$("#summary-toggle-daily-scr-cap").attr("custom", "3");
	}
	if (${Ad.freqCap} == 0) {
		$("#summary-toggle-freq-cap").attr("custom", "3");
	}
	if (${Ad.cpm} == 0) {
		$("#summary-toggle-cpm").attr("custom", "3");
	}
	if (${Ad.duration} == 0) {
		$("#summary-toggle-duration").attr("custom", "3");
	}
	if (('${Ad.status}' == 'R' || '${Ad.status}' == 'A') && ${Ad.impAddRatio} != 0) {
	} else {
		$("#summary-toggle-imp-add-ratio").attr("custom", "3");
	}
	
	controlItemVisibility(2);
	
});


function dispResoBadgeValues(values) {
	
	var ret = "";
	var value = values.split("|");
	  
	for(var i = 0; i < value.length; i ++) {
		if (value[i]) {
			var item = value[i].split(":");
			if (item.length == 2) {
				if (Number(item[0]) == 1) {
					ret = ret + "<small><span class='badge badge-outline-success'>";
				} else if (Number(item[0]) == 0) {
					ret = ret + "<small><span class='badge badge-outline-secondary'>";
				} else {
					ret = ret + "<small><span class='badge badge-outline-danger'>";
				}
				
				ret = ret + "<span class='text-small'>" + item[1] + "</span></span></small><span class='pl-1'></span>";
			}
		}
	}
	  
	return ret;
}


function navToAdv(advId) {
	var path = "/adc/creative/creatives/" + advId;
	location.href = path;
}


function summaryToggleOff() {
	$("#summary-toggle-off-btn").show();
	$("#summary-toggle-on-btn").hide();
	
	controlItemVisibility(3);
}


function summaryToggleOn() {
	$("#summary-toggle-off-btn").hide();
	$("#summary-toggle-on-btn").show();
	
	controlItemVisibility(2);
}


function controlItemVisibility(level) {
	
	// level 1: 필수
	// level 2: 필수 + 중요도 높음
	// level 3: 모든 항목
	
	$("#summary-toggle-root-div").children().each(function() {
		
		var itemLevel = $(this).attr("custom");
		if (itemLevel != undefined) {
			
			var currItemLevel = Number(itemLevel);
			if (currItemLevel <= level) {
				$(this).show();
			} else {
				$(this).hide();
			}
		}
	});
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

	$("#qlb-ad-daily-achv-btn").click(function(e) {
		e.preventDefault();

		qlbRequest("${recalcAdDailyAchvUrl}", ${Ad.id});
	});
	
	$("#qlb-ad-today-target-btn").click(function(e) {
		e.preventDefault();

		qlbRequest("${recalcAdTodayTargetUrl}", ${Ad.id});
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

