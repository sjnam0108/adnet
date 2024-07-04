<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ taglib prefix="adc" tagdir="/WEB-INF/tags/adc"%>


<!-- URL -->

<c:url value="/adc/ad/update" var="updateUrl" />
<c:url value="/adc/ad/approve" var="approveUrl" />
<c:url value="/adc/ad/reject" var="rejectUrl" />
<c:url value="/adc/ad/archive" var="archiveUrl" />
<c:url value="/adc/ad/unarchive" var="unarchiveUrl" />
<c:url value="/adc/ad/pause" var="pauseUrl" />
<c:url value="/adc/ad/resume" var="resumeUrl" />
<c:url value="/adc/ad/destroy" var="destroyUrl" />

<c:url value="/adc/campaign/detail/readAuditTrail" var="readAuditTrailUrl" />
<c:url value="/adc/campaign/detail/readAuditTrailValue" var="readAuditTrailValueUrl" />
<c:url value="/adc/campaign/detail/readCreatFiles" var="readCreatFileUrl" />



<!-- Opening tags -->

<common:mediumPageOpening />


<!-- Page title -->

<h4 class="pt-1 pb-3 mb-3">
	<span class="text-muted font-weight-light">캠페인<span class="px-2">/</span>${pageTitle}<span class="px-2">/</span></span>
	<span class="mr-1 fa-light fa-microscope"></span><span class="pl-1">광고 상세</span>
</h4>

<hr class="border-light container-m--x mt-0 mb-4">





<!-- Page body -->


<!-- Page scripts  -->

<link rel="stylesheet" href="/resources/vendor/lib/bootstrap-slider/bootstrap-slider.css">

<script>$.fn.slider = null</script>
<script type="text/javascript" src="/resources/vendor/lib/bootstrap-slider/bootstrap-slider.js"></script>


<!-- Java(optional)  -->

<%
	String dateTemplate = net.doohad.utils.Util.getSmartDate("whoCreationDate", true, true);
	
	String noRecordsTemplate =
			"<div class='container text-center my-4'>" +
				"<div class='d-flex justify-content-center align-self-center'>" +
					"<span class='fa-thin fa-hexagon-exclamation fa-3x'></span>" +
					"<span class='pl-3 mt-2' style='font-weight: 300; font-size: 1.3rem;'>해당 자료 없음</span>" +
				"</div>" +
			"</div>";
			
	String actTypeTemplate =
			"# if (actType == 'N') { #" +
				"<span class='fa-light fa-asterisk fa-fw text-blue'></span><span class='pl-1'>생성</span>" +
			"# } else if (actType == 'X') { #" +
				"<span class='fa-light fa-trash-can fa-fw text-danger'></span><span class='pl-1'>삭제</span>" +
			"# } else if (actType == 'E') { #" +
				"<span class='fa-light fa-pencil-alt fa-fw'></span><span class='pl-1'>변경</span>" +
			"# } else if (actType == 'S') { #" +
				"<span class='fa-light fa-circle-check fa-fw'></span><span class='pl-1'>설정</span>" +
			"# } else if (actType == 'U') { #" +
				"<span class='fa-light fa-circle-dashed fa-fw'></span><span class='pl-1'>설정해제</span>" +
			"# } else { #" +
				"<span>-</span>" +
			"# } #";
	String methodTemplate =
			"# if (method == 'F') { #" +
				"<span class='fa-light fa-pen-to-square fa-fw'></span><span class='pl-1'>웹 폼</span>" +
			"# } else if (method == 'B') { #" +
				"<span class='fa-light fa-gear fa-fw'></span><span class='pl-1'>일괄 작업</span>" +
			"# } else { #" +
				"<span>-</span>" +
			"# } #";
			
	String valueTemplate = 
			"<span>" +
				"# if (actType == 'E' && tgtType == 'Mobil' && !tgtValue) { #" +
					"<div class='detailSingleMobile'></div>" +
				"# } else if (actType == 'E' && tgtType == 'Inven' && !tgtValue) { #" +
					"<div class='detailSingleInven'></div>" +
				"# } else { #" +
					"<span class='detailOldText'></span>" +
					"<span class='px-2'>" +
						"<span class='fa-light fa-arrow-right fa-fw'></span>" +
					"</span>" +
					"<span class='detailNewText'></span>" + 
				"# } #" +
			"</span>";
	String targetTemplate =
			"#= target" +
				".replace('{IconAC}', '<span class=\"badge badge-pill bg-secondary text-white default-pointer\">광고 소재</span><span class=\"pr-1\"></span>')" +
				".replace('{IconMobCR}', '<span class=\"fa-light fa-circle-dot fa-fw\" title=\"원 반경 지역\"></span><span class=\"pr-1\"></span>')" +
				".replace('{IconMobRG}', '<span class=\"fa-light fa-mountain-city fa-fw\" title=\"모바일 지역\"></span><span class=\"pr-1\"></span>')" +
				".replace('{IconInvSC}', '<span class=\"fa-light fa-screen-users fa-fw\" title=\"매체 화면\"></span><span class=\"pr-1\"></span>')" +
				".replace('{IconInvST}', '<span class=\"fa-light fa-map-pin fa-fw\" title=\"사이트\"></span><span class=\"pr-1\"></span>')" +
				".replace('{IconInvCT}', '<span class=\"fa-light fa-city fa-fw\" title=\"광역시/도\"></span><span class=\"pr-1\"></span>')" +
				".replace('{IconInvRG}', '<span class=\"fa-light fa-mountain-city fa-fw\" title=\"시/군/구\"></span><span class=\"pr-1\"></span>')" +
				".replace('{IconInvCD}', '<span class=\"fa-light fa-location-crosshairs fa-fw\" title=\"입지 유형\"></span><span class=\"pr-1\"></span>')" +
				".replace('{IconInvSP}', '<span class=\"fa-light fa-box-taped fa-fw\" title=\"화면 묶음\"></span><span class=\"pr-1\"></span>')" +
				".replace('{TagSmallO}', '<small>')" +
				".replace('{TagSmallC}', '</small>') #";
%>


<!--  Overview header -->

<adc:campaign />

<!--  / Overview header -->


<!--  Tab -->

<ul class="nav nav-tabs tabs-alt mb-4 mt-3">
	<li class="nav-item">
		<a class="nav-link" href="/adc/campaign/ads/${Campaign.id}">
			<i class="mr-1 fa-light fa-audio-description"></i>
			광고 목록
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link active" href="/adc/campaign/detail/${Campaign.id}">
			<i class="mr-1 fa-light fa-microscope"></i>
			광고 상세
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" href="/adc/campaign/creatives/${Campaign.id}">
			<i class="mr-1 fa-light fa-clapperboard-play"></i>
			광고 소재
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" href="/adc/campaign/mobtargets/${Campaign.id}">
			<i class="mr-1 fa-light fa-bus"></i>
			모바일 타겟팅
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" href="/adc/campaign/invtargets/${Campaign.id}">
			<i class="mr-1 fa-light fa-bullseye-arrow"></i>
			<span id="inven-target-tab-title">인벤 타겟팅</span>
		</a>
	</li>
	<li class="nav-item mr-auto">
		<a class="nav-link" href="/adc/campaign/timetarget/${Campaign.id}">
			<i class="mr-1 fa-light fa-alarm-clock"></i>
			<span id="time-target-tab-title">시간 타겟팅</span>
		</a>
	</li>

<c:if test="${fn:length(currAds) > 0}" >

	<select name="nav-item-ad-select" class="selectpicker bg-white mb-1" data-style="btn-default" data-none-selected-text="" data-width="300px" data-size="10" >

<c:forEach var="item" items="${currAds}">

		<option value="${item.value}" data-content="<span class='fa-light ${item.icon}'></span><span class='pl-2'>${item.text}</span><span class='small pl-3 opacity-75'><span class='fa-regular ${item.subIcon}'></span></span>"></option>

</c:forEach>

	</select>

<script>
$(document).ready(function() {

	$("select[name='nav-item-ad-select']").selectpicker('render');

	$("select[name='nav-item-ad-select']").on("change.bs.select", function(e){
		location.href = "/adc/campaign/detail/${Campaign.id}/" + $("select[name='nav-item-ad-select']").val();
	});
	
	bootstrapSelectVal($("select[name='nav-item-ad-select']"), "${currAdId}");
	
});	
</script>

</c:if>
	
</ul>

<!--  / Tab -->


<!--  Root form container -->
<div id="formRoot"></div>


<!--  Page details -->

<c:choose>
<c:when test="${fn:length(currAds) == 0}" >

	<div class="card">
		<div class="card-body">
			<div class="form-row">
				<div class='container text-center my-4'>
					<div class='d-flex justify-content-center align-self-center'>
						<span class='fa-thin fa-diamond-exclamation fa-3x'></span>
						<span class='pl-3 mt-2' style='font-weight: 300; font-size: 1.3rem;'>현재 선택된 광고 없음</span>
					</div>
				</div>
			</div>
		</div>
	</div>

</c:when>
<c:otherwise>


<div class="mb-4">
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
				<span class="fa-regular fa-toggle-on text-orange"></span><span class="pl-1">요약</span>
			</a>
			<a href='javascript:summaryToggleOn()' class='btn btn-default btn-xs ml-1' style="display: none;" id="summary-toggle-off-btn">
				<span class="fa-regular fa-toggle-off text-green"></span><span class="pl-1">상세</span>
			</a>
			<div class="btn-group pl-3">
				<button type="button" class="btn btn-sm btn-secondary dropdown-toggle" data-toggle="dropdown">
					<span class="fa-light fa-lg fa-signs-post"></span>
					<span class="pl-1">진행</span>
				</button>
				<div class="dropdown-menu">
					<a class="dropdown-item" href="javascript:void(0)" id="edit-btn">
						<i class="fa-regular fa-pencil-alt text-success fa-fw"></i><span class="pl-2">수정</span>
					</a>
					<div class="dropdown-divider"></div>
					<a class="dropdown-item" href="javascript:void(0)" id="approve-btn">
						<i class="fa-light fa-thumbs-up fa-fw"></i><span class="pl-2">승인</span>
					</a>
					<a class="dropdown-item" href="javascript:void(0)" id="reject-btn">
						<i class="fa-light fa-thumbs-down fa-fw"></i><span class="pl-2">거절</span>
					</a>
					<div class="dropdown-divider"></div>
					<a class="dropdown-item" href="javascript:void(0)" id="archive-btn">
						<i class="fa-light fa-box-archive fa-fw"></i><span class="pl-2">보관</span>
					</a>
					<a class="dropdown-item" href="javascript:void(0)" id="unarchive-btn">
						<i class="fa-light fa-box-open fa-fw"></i><span class="pl-2">보관 해제</span>
					</a>
					<div class="dropdown-divider"></div>
					<a class="dropdown-item" href="javascript:void(0)" id="pause-btn">
						<i class="fa-regular fa-circle-pause text-danger fa-fw"></i><span class="pl-2">잠시 멈춤</span>
					</a>
					<a class="dropdown-item" href="javascript:void(0)" id="resume-btn">
						<i class="fa-light fa-play fa-fw"></i><span class="pl-2">재개</span>
					</a>
					<div class="dropdown-divider"></div>
					<a class="dropdown-item" href="javascript:void(0)" id="delete-btn">
						<i class="fa-regular fa-trash-can text-danger fa-fw"></i><span class="pl-2">삭제</span>
					</a>
				</div>    			
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
				<span class="fa-thin fa-sidebar fa-3x text-gray fa-fw"></span>
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

<c:if test="${not empty Ad.tgtTodayDisp}">
		
		<div class="col-sm-12 col-md-12">
			<div class="d-flex align-items-center justify-content-center py-2 lead footer-title">${Ad.tgtTodayDisp}</div>
		</div>
		
</c:if>

	</div>
</div>
</div>

<style>

.footer-title {
	font-size: 30px;
	font-weight: 300;
}
.footer-title small {
	font-size: 20px;
	font-weight: 300;
}

</style>


<!-- Kendo grid  -->

<div class="mb-4">
<div class="card" style="border-bottom: transparent;">
	<h6 class="card-header with-elements pl-3">
		<div class="card-header-title py-1">
			<span class="fa-light fa-clock-rotate-left fa-lg"></span>
			<span class="ml-2 font-weight-light" style="font-size: 1.2rem;">변경 이력</span>
		</div>
	</h6>
</div>
<kendo:grid name="grid-audit" pageable="true" filterable="true" sortable="true" scrollable="true" 
		reorderable="true" resizable="true" selectable="single" detailTemplate="template">
	<kendo:grid-excel fileName="${pageTitle}.xlsx" allPages="true" proxyURL="/proxySave"/>
	<kendo:grid-pageable refresh="true" buttonCount="5" pageSize="10" pageSizes="${pageSizesNormal}" />
   	<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />
	<kendo:grid-columns>
		<kendo:grid-column title="시간" field="whoCreationDate" width="120" template="<%= dateTemplate %>" />
		<kendo:grid-column title="유형" field="actType" width="120" filterable="false" sortable="false" template="<%= actTypeTemplate %>" />
		<kendo:grid-column title="대상" field="target" width="300" filterable="false" sortable="false" template="<%= targetTemplate %>" />
		<kendo:grid-column title="누가?" field="actedBy" width="120" filterable="false" sortable="false" template="#= actedByShortName #" />
		<!-- kendo:grid-column title="방법" field="method" width="120" filterable="false" sortable="false" template="<%= methodTemplate %>" /-->
	</kendo:grid-columns>
	<kendo:grid-dataBound>
		<script>
			function grid_dataBound(e) {

				var items = e.sender.items();
				items.each(function () {
					var row = $(this);
					var dataItem = e.sender.dataItem(row);
					if (dataItem.actType == "N" || dataItem.actType == "U") {
						row.find(".k-hierarchy-cell").html("");
					} else if (dataItem.actType == "S" && dataItem.tgtType == "Mobil") {
						row.find(".k-hierarchy-cell").html("");
					} else if (dataItem.actType == "S" && dataItem.tgtType == "Inven") {
						row.find(".k-hierarchy-cell").html("");
					}
				});
        	}
		</script>
	</kendo:grid-dataBound>
	<kendo:grid-filterable>
		<kendo:grid-filterable-messages selectedItemsFormat="{0} 항목 선택됨"/>
	</kendo:grid-filterable>
	<kendo:grid-detailInit>
		<script>
			function grid_detailInit(e) {

				var mItem = e.sender.dataItem(e.masterRow);
				
				if (mItem.actType == 'S' && mItem.tgtType == 'CPack') {
					
					if (mItem.tgtValue) {
						
						$.ajax({
							type: "POST",
							contentType: "application/json",
							dataType: "json",
							url: "${readCreatFileUrl}",
							data: JSON.stringify({ ids: mItem.tgtValue }),
							success: function (data, status) {
								
								$('#creat-files-' + mItem.id).html("<div class='gallery-sizer col-6 col-sm-4 col-md-3 col-xl-2 position-absolute'></div>");
								
								var template = kendo.template($("#itemViewTemplate").html());
								
								for(var i in data) {
									$('#creat-files-' + mItem.id).append(template(data[i]));
								}
							},
							error: ajaxReadError
						});
						
					} else {
						
						var html = 	"<div class='container text-center my-4'>";
						html += 		"<div class='d-flex justify-content-center align-self-center'>";
						html += 			"<span class='fa-thin fa-hexagon-exclamation fa-3x'></span>";
						html += 			"<span class='pl-3 mt-2' style='font-weight: 300; font-size: 1.3rem;'>해당 자료 없음</span>";
						html +=			"</div>";
						html +=		"</div>";
						$('#creat-files-' + mItem.id).html(html);
					}
					
				} else if (mItem.actType == 'S' && mItem.tgtType == 'Time') {
					
					if (mItem.tgtValue && mItem.tgtValue.length == 168) {
						$("#time-table-no-data-row-" + mItem.id).hide();
						$("#time-table-data-row-" + mItem.id).show();
						
						setExpHourStrCustom(mItem.id, mItem.tgtValue);
					} else {
						$("#time-table-no-data-row-" + mItem.id).show();
						$("#time-table-data-row-" + mItem.id).hide();
					}
					
				}
        	}
		</script>
	</kendo:grid-detailInit>
	<kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="id" dir="desc"/>
		</kendo:dataSource-sort>
		<kendo:dataSource-transport>
			<kendo:dataSource-transport-read url="${readAuditTrailUrl}" dataType="json" type="POST" contentType="application/json">
				<kendo:dataSource-transport-read-data>
					<script>
						function additionalData(e) {
							return { reqIntValue1:  ${Ad.id} };
						}
					</script>
				</kendo:dataSource-transport-read-data>
			</kendo:dataSource-transport-read>
			<kendo:dataSource-transport-parameterMap>
				<script>
					function parameterMap(options,type) {
						return JSON.stringify(options);	
					}
				</script>
			</kendo:dataSource-transport-parameterMap>
		</kendo:dataSource-transport>
		<kendo:dataSource-schema data="data" total="total" groups="data">
			<kendo:dataSource-schema-model id="id">
				<kendo:dataSource-schema-model-fields>
					<kendo:dataSource-schema-model-field name="whoCreationDate" type="date" />
				</kendo:dataSource-schema-model-fields>
			</kendo:dataSource-schema-model>
		</kendo:dataSource-schema>
	</kendo:dataSource>
</kendo:grid>
<kendo:grid-detailTemplate id="template">

	# if ( actType == 'E' || ( actType == 'S' && tgtType == 'Creat' )) { #
	
	<kendo:grid name="grid_#=id#" pageable="false" filterable="false" sortable="false" scrollable="false">
		<kendo:grid-pageable refresh="false" previousNext="false" numeric="false" pageSize="10000" info="false" alwaysVisible="false" />
	   	<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />
		<kendo:grid-columns>
			<kendo:grid-column title=" " field="itemText" width="200" />
			<kendo:grid-column template="<%= valueTemplate %>" />
		</kendo:grid-columns>
		<kendo:grid-dataBound>
			<script>
				function grid_dataBound(e) {
					var rows = this.dataSource.view();

					for (var i = 0; i < rows.length; i ++) {
						$(this.tbody.find("tr[data-uid='" + rows[i].uid + "'] .detailOldText"))
								.text(rows[i].oldDispText);
						$(this.tbody.find("tr[data-uid='" + rows[i].uid + "'] .detailNewText"))
								.text(rows[i].newDispText);
						$(this.tbody.find("tr[data-uid='" + rows[i].uid + "'] .detailSingleMobile"))
								.html(mobileOrderTemplate(rows[i]));
						$(this.tbody.find("tr[data-uid='" + rows[i].uid + "'] .detailSingleInven"))
								.html(invenOrderTemplate(rows[i]));
					}
					
					e.sender.thead.hide();
	        	}
			</script>
		</kendo:grid-dataBound>
		<kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" error="kendoReadError">
			<kendo:dataSource-transport>
				<kendo:dataSource-transport-read url="${readAuditTrailValueUrl}" dataType="json" type="POST" contentType="application/json">
					<kendo:dataSource-transport-read-data>
						<script>
							function additionalData(e) {
								return { reqIntValue1:  #= id # };
							}
						</script>
					</kendo:dataSource-transport-read-data>
				</kendo:dataSource-transport-read>
				<kendo:dataSource-transport-parameterMap>
				<script>
					function parameterMap(options) { 
						return JSON.stringify(options);
					}
				</script>
				</kendo:dataSource-transport-parameterMap>
			</kendo:dataSource-transport>
		<kendo:dataSource-schema data="data" total="total" groups="data">
			<kendo:dataSource-schema-model id="id">
				<kendo:dataSource-schema-model-fields>
				</kendo:dataSource-schema-model-fields>
			</kendo:dataSource-schema-model>
		</kendo:dataSource-schema>
		</kendo:dataSource>
	</kendo:grid>
	
	# } else if ( actType == 'S' && tgtType == 'CPack' ) { #
	
	<div class="row form-row" id="creat-files-#= id #" style="min-height: 50px;"></div>
	
	
	# } else if ( actType == 'S' && tgtType == 'Time' ) { #
	
	<div class="form-row" id="time-table-no-data-row-#= id #">
		<div class='container text-center my-4'>
			<div class='d-flex justify-content-center align-self-center'>
				<span class='fa-thin fa-hexagon-exclamation fa-3x'></span>
				<span class='pl-3 mt-2' style='font-weight: 300; font-size: 1.3rem;'>해당 자료 없음</span>
			</div>
		</div>
	</div>
	<div class="form-row" id="time-target-data-row-#= id #" >
		<div style="border: solid 1px rgba(0, 0, 0, 0.08); background-color: white;">
			<div class="p-2">

				<table id="base-time-table">
					<tr>
						<th></th>
						<th><pre class="my-0">AM</pre></th>
						<th><pre class="my-0">1</pre></th>
						<th><pre class="my-0">2</pre></th>
						<th><pre class="my-0">3</pre></th>
						<th><pre class="my-0">4</pre></th>
						<th><pre class="my-0">5</pre></th>
						<th><pre class="my-0">6</pre></th>
						<th><pre class="my-0">7</pre></th>
						<th><pre class="my-0">8</pre></th>
						<th><pre class="my-0">9</pre></th>
						<th><pre class="my-0">10</pre></th>
						<th><pre class="my-0">11</pre></th>
						<th><pre class="my-0">PM</pre></th>
						<th><pre class="my-0">1</pre></th>
						<th><pre class="my-0">2</pre></th>
						<th><pre class="my-0">3</pre></th>
						<th><pre class="my-0">4</pre></th>
						<th><pre class="my-0">5</pre></th>
						<th><pre class="my-0">6</pre></th>
						<th><pre class="my-0">7</pre></th>
						<th><pre class="my-0">8</pre></th>
						<th><pre class="my-0">9</pre></th>
						<th><pre class="my-0">10</pre></th>
						<th><pre class="my-0">11</pre></th>
					</tr>
					<tr>
						<th class="pr-2">월<small>요일</small></th>
						<td id="btt-#= id #-0"></td>
						<td id="btt-#= id #-1"></td>
						<td id="btt-#= id #-2"></td>
						<td id="btt-#= id #-3"></td>
						<td id="btt-#= id #-4"></td>
						<td id="btt-#= id #-5"></td>
						<td id="btt-#= id #-6"></td>
						<td id="btt-#= id #-7"></td>
						<td id="btt-#= id #-8"></td>
						<td id="btt-#= id #-9"></td>
						<td id="btt-#= id #-10"></td>
						<td id="btt-#= id #-11"></td>
						<td id="btt-#= id #-12"></td>
						<td id="btt-#= id #-13"></td>
						<td id="btt-#= id #-14"></td>
						<td id="btt-#= id #-15"></td>
						<td id="btt-#= id #-16"></td>
						<td id="btt-#= id #-17"></td>
						<td id="btt-#= id #-18"></td>
						<td id="btt-#= id #-19"></td>
						<td id="btt-#= id #-20"></td>
						<td id="btt-#= id #-21"></td>
						<td id="btt-#= id #-22"></td>
						<td id="btt-#= id #-23"></td>
					</tr>
					<tr>
						<th>화<small>요일</small></th>
						<td id="btt-#= id #-24"></td>
						<td id="btt-#= id #-25"></td>
						<td id="btt-#= id #-26"></td>
						<td id="btt-#= id #-27"></td>
						<td id="btt-#= id #-28"></td>
						<td id="btt-#= id #-29"></td>
						<td id="btt-#= id #-30"></td>
						<td id="btt-#= id #-31"></td>
						<td id="btt-#= id #-32"></td>
						<td id="btt-#= id #-33"></td>
						<td id="btt-#= id #-34"></td>
						<td id="btt-#= id #-35"></td>
						<td id="btt-#= id #-36"></td>
						<td id="btt-#= id #-37"></td>
						<td id="btt-#= id #-38"></td>
						<td id="btt-#= id #-39"></td>
						<td id="btt-#= id #-40"></td>
						<td id="btt-#= id #-41"></td>
						<td id="btt-#= id #-42"></td>
						<td id="btt-#= id #-43"></td>
						<td id="btt-#= id #-44"></td>
						<td id="btt-#= id #-45"></td>
						<td id="btt-#= id #-46"></td>
						<td id="btt-#= id #-47"></td>
					</tr>
					<tr>
						<th>수<small>요일</small></th>
						<td id="btt-#= id #-48"></td>
						<td id="btt-#= id #-49"></td>
						<td id="btt-#= id #-50"></td>
						<td id="btt-#= id #-51"></td>
						<td id="btt-#= id #-52"></td>
						<td id="btt-#= id #-53"></td>
						<td id="btt-#= id #-54"></td>
						<td id="btt-#= id #-55"></td>
						<td id="btt-#= id #-56"></td>
						<td id="btt-#= id #-57"></td>
						<td id="btt-#= id #-58"></td>
						<td id="btt-#= id #-59"></td>
						<td id="btt-#= id #-60"></td>
						<td id="btt-#= id #-61"></td>
						<td id="btt-#= id #-62"></td>
						<td id="btt-#= id #-63"></td>
						<td id="btt-#= id #-64"></td>
						<td id="btt-#= id #-65"></td>
						<td id="btt-#= id #-66"></td>
						<td id="btt-#= id #-67"></td>
						<td id="btt-#= id #-68"></td>
						<td id="btt-#= id #-69"></td>
						<td id="btt-#= id #-70"></td>
						<td id="btt-#= id #-71"></td>
					</tr>
					<tr>
						<th>목<small>요일</small></th>
						<td id="btt-#= id #-72"></td>
						<td id="btt-#= id #-73"></td>
						<td id="btt-#= id #-74"></td>
						<td id="btt-#= id #-75"></td>
						<td id="btt-#= id #-76"></td>
						<td id="btt-#= id #-77"></td>
						<td id="btt-#= id #-78"></td>
						<td id="btt-#= id #-79"></td>
						<td id="btt-#= id #-80"></td>
						<td id="btt-#= id #-81"></td>
						<td id="btt-#= id #-82"></td>
						<td id="btt-#= id #-83"></td>
						<td id="btt-#= id #-84"></td>
						<td id="btt-#= id #-85"></td>
						<td id="btt-#= id #-86"></td>
						<td id="btt-#= id #-87"></td>
						<td id="btt-#= id #-88"></td>
						<td id="btt-#= id #-89"></td>
						<td id="btt-#= id #-90"></td>
						<td id="btt-#= id #-91"></td>
						<td id="btt-#= id #-92"></td>
						<td id="btt-#= id #-93"></td>
						<td id="btt-#= id #-94"></td>
						<td id="btt-#= id #-95"></td>
					</tr>
					<tr>
						<th>금<small>요일</small></th>
						<td id="btt-#= id #-96"></td>
						<td id="btt-#= id #-97"></td>
						<td id="btt-#= id #-98"></td>
						<td id="btt-#= id #-99"></td>
						<td id="btt-#= id #-100"></td>
						<td id="btt-#= id #-101"></td>
						<td id="btt-#= id #-102"></td>
						<td id="btt-#= id #-103"></td>
						<td id="btt-#= id #-104"></td>
						<td id="btt-#= id #-105"></td>
						<td id="btt-#= id #-106"></td>
						<td id="btt-#= id #-107"></td>
						<td id="btt-#= id #-108"></td>
						<td id="btt-#= id #-109"></td>
						<td id="btt-#= id #-110"></td>
						<td id="btt-#= id #-111"></td>
						<td id="btt-#= id #-112"></td>
						<td id="btt-#= id #-113"></td>
						<td id="btt-#= id #-114"></td>
						<td id="btt-#= id #-115"></td>
						<td id="btt-#= id #-116"></td>
						<td id="btt-#= id #-117"></td>
						<td id="btt-#= id #-118"></td>
						<td id="btt-#= id #-119"></td>
					</tr>
					<tr>
						<th>토<small>요일</small></th>
						<td id="btt-#= id #-120"></td>
						<td id="btt-#= id #-121"></td>
						<td id="btt-#= id #-122"></td>
						<td id="btt-#= id #-123"></td>
						<td id="btt-#= id #-124"></td>
						<td id="btt-#= id #-125"></td>
						<td id="btt-#= id #-126"></td>
						<td id="btt-#= id #-127"></td>
						<td id="btt-#= id #-128"></td>
						<td id="btt-#= id #-129"></td>
						<td id="btt-#= id #-130"></td>
						<td id="btt-#= id #-131"></td>
						<td id="btt-#= id #-132"></td>
						<td id="btt-#= id #-133"></td>
						<td id="btt-#= id #-134"></td>
						<td id="btt-#= id #-135"></td>
						<td id="btt-#= id #-136"></td>
						<td id="btt-#= id #-137"></td>
						<td id="btt-#= id #-138"></td>
						<td id="btt-#= id #-139"></td>
						<td id="btt-#= id #-140"></td>
						<td id="btt-#= id #-141"></td>
						<td id="btt-#= id #-142"></td>
						<td id="btt-#= id #-143"></td>
					</tr>
					<tr>
						<th>일<small>요일</small></th>
						<td id="btt-#= id #-144"></td>
						<td id="btt-#= id #-145"></td>
						<td id="btt-#= id #-146"></td>
						<td id="btt-#= id #-147"></td>
						<td id="btt-#= id #-148"></td>
						<td id="btt-#= id #-149"></td>
						<td id="btt-#= id #-150"></td>
						<td id="btt-#= id #-151"></td>
						<td id="btt-#= id #-152"></td>
						<td id="btt-#= id #-153"></td>
						<td id="btt-#= id #-154"></td>
						<td id="btt-#= id #-155"></td>
						<td id="btt-#= id #-156"></td>
						<td id="btt-#= id #-157"></td>
						<td id="btt-#= id #-158"></td>
						<td id="btt-#= id #-159"></td>
						<td id="btt-#= id #-160"></td>
						<td id="btt-#= id #-161"></td>
						<td id="btt-#= id #-162"></td>
						<td id="btt-#= id #-163"></td>
						<td id="btt-#= id #-164"></td>
						<td id="btt-#= id #-165"></td>
						<td id="btt-#= id #-166"></td>
						<td id="btt-#= id #-167"></td>
					</tr>
				</table>

			</div>
		</div>
	</div>
	
	# } else { #
	# } #
	
</kendo:grid-detailTemplate>
</div>

<style>

/* Vertical Scrollbar 삭제 */
.k-grid .k-grid-header
{
   padding: 0 !important;
}
.k-grid .k-grid-content
{
   overflow-y: visible;
   min-height: 90px;
}


/* 시간 설정 테이블 */
table#base-time-table {
	border-spacing: 0px;
	display: inline-block;
}
table#base-time-table th {
	font-weight: 400;
	padding: 0;
	border-style: none;
}
table#base-time-table td {
	border: 1px solid #fff;
	width: 28px;
	height: 30px;
	margin: 10px;
	background-color: rgba(24,28,33,0.06) !important;
	cursor: default;
	padding: 0px;
}
table#base-time-table td.selected {
	background-color: #02a96b !important;
}
table#base-time-table td.rselected {
	background-color: #02BC77 !important;
}
table#base-time-table td.nzselected {
	background-color: #f00 !important;
}

</style>


<script id="itemViewTemplate" type="text/x-kendo-template">

<div class="creat-item card card-condenced mr-2 mb-2" ukid="#: creatId #">
	<div class="card-body align-items-center p-0" style="width: 144px;">
		<div class="p-2">
			<Img name="thumb-img" src='/thumbs/#= thumbUri #' class='thumb-128'>
		</div>
		<div class="pt-2">
			<div class='d-flex justify-content-center text-dark mb-2'>#: creatName #</div>
			<div class="text-muted small px-2">
				# if (mediaType == "V") { #
					<span class="fa-solid fa-film text-primary"></span><span class="pl-1 pr-2">동영상</span>
					<span class="fa-regular fa-alarm-clock"></span><span class="pl-1">#: duration #</span>
				# } else if (mediaType == "I") { #
					<span class="fa-solid fa-image text-primary"></span><span class="pl-1">이미지</span>
				# } #
				<span class="pr-2"></span>
				<span class="fa-regular fa-ruler-vertical"></span>
				#: fileLength #
			</div>
		</div>
	</div>
</div>

</script>


<script id="targetOrderViewMobilTemplate" type="text/x-kendo-template">

<span>

# if ( newValue == 'CR' ) { #
	<span class="fa-light fa-circle-dot fa-fw" title="원 반경 지역"></span><span class="pr-1"></span><span>#= newText #</span>
# } else if ( newValue == 'RG' ) { #
	<span class="fa-light fa-mountain-city fa-fw" title="모바일 지역"></span><span class="pr-1"></span><span>#= newText #</span>
# } #
	
</span>

</script>


<script id="targetOrderViewInvenTemplate" type="text/x-kendo-template">

<span>

# if ( newValue == 'SC' ) { #
	<span class="fa-light fa-screen-users fa-fw" title="매체 화면"></span><span class="pr-1"></span><span>#= newText #</span>
# } else if ( newValue == 'ST' ) { #
	<span class="fa-light fa-map-pin fa-fw" title="사이트"></span><span class="pr-1"></span><span>#= newText #</span>
# } else if ( newValue == 'CT' ) { #
	<span class="fa-light fa-city fa-fw" title="광역시/도"></span><span class="pr-1"></span><span>#= newText #</span>
# } else if ( newValue == 'RG' ) { #
	<span class="fa-light fa-mountain-city fa-fw" title="시/군/구"></span><span class="pr-1"></span><span>#= newText #</span>
# } else if ( newValue == 'CD' ) { #
	<span class="fa-light fa-location-crosshairs fa-fw" title="입지 유형"></span><span class="pr-1"></span><span>#= newText #</span>
# } else if ( newValue == 'SP' ) { #
	<span class="fa-light fa-box-taped fa-fw" title="화면 묶음"></span><span class="pr-1"></span><span>#= newText #</span>
# } #
	
</span>

</script>

<!-- / Kendo grid  -->


<!-- Form button actions  -->

<script>
$(document).ready(function() {

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
	

	// Edit
	$("#edit-btn").click(function(e) {
		e.preventDefault();
		
		edit();
	});
	// / Edit
	
	// Approve
	$("#approve-btn").click(function(e) {
		e.preventDefault();

		$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: "${approveUrl}",
			data: JSON.stringify({ items: [ ${Ad.id} ] }),
			success: function (form) {
				showOperationSuccessMsg();
				reload();
			},
			error: ajaxOperationError
		});
	});
	// / Approve
	
	// Reject
	$("#reject-btn").click(function(e) {
		e.preventDefault();
			
		$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: "${rejectUrl}",
			data: JSON.stringify({ items: [ ${Ad.id} ] }),
			success: function (form) {
				showOperationSuccessMsg();
				reload();
			},
			error: ajaxOperationError
		});
	});
	// / Reject
	
	// Archive
	$("#archive-btn").click(function(e) {
		e.preventDefault();
		
		$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: "${archiveUrl}",
			data: JSON.stringify({ items: [ ${Ad.id} ] }),
			success: function (form) {
				showOperationSuccessMsg();
				reload();
			},
			error: ajaxOperationError
		});
	});
	// / Archive
	
	// Unarchive
	$("#unarchive-btn").click(function(e) {
		e.preventDefault();
		
		$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: "${unarchiveUrl}",
			data: JSON.stringify({ items: [ ${Ad.id} ] }),
			success: function (form) {
				showOperationSuccessMsg();
				reload();
			},
			error: ajaxOperationError
		});
	});
	// / Unarchive
	
	// Pause
	$("#pause-btn").click(function(e) {
		e.preventDefault();
		
		$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: "${pauseUrl}",
			data: JSON.stringify({ items: [ ${Ad.id} ] }),
			success: function (form) {
				showOperationSuccessMsg();
				reload();
			},
			error: ajaxOperationError
		});
	});
	// / Pause
	
	// Resume
	$("#resume-btn").click(function(e) {
		e.preventDefault();
		
		$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: "${resumeUrl}",
			data: JSON.stringify({ items: [ ${Ad.id} ] }),
			success: function (form) {
				showOperationSuccessMsg();
				reload();
			},
			error: ajaxOperationError
		});
	});
	// / Resume
	
	// Delete
	$("#delete-btn").click(function(e) {
		e.preventDefault();
		
		$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: "${destroyUrl}",
			data: JSON.stringify({ items: [ ${Ad.id} ] }),
			success: function (form) {
				showDeleteSuccessMsg();
				reload();
			},
			error: ajaxDeleteError
		});
	});
	// / Delete
	
});	
</script>

<!-- / Form button actions  -->


<!--  Forms -->

<script id="template-1" type="text/x-kendo-template">

<div class="modal fade" data-backdrop="static" id="form-modal-1">
	<div class="modal-dialog modal-lg">
		<form class="modal-content" id="form-1" rowid="-1" url="${createUrl}">
      
			<!-- Modal Header -->
			<div class="modal-header move-cursor">
				<h5 class="modal-title">
					광고
					<span class="font-weight-light pl-1"><span name="subtitle"></span></span>
				</h5>
				<button type="button" class="close" data-dismiss="modal">×</button>
			</div>
        
			<!-- Modal body -->
			<div class="modal-body modal-bg-color">
				<div class="form-row">
					<div class="form-group col">
						<label class="form-label">
							광고명
							<span class="text-danger">*</span>
						</label>
						<input name="name" type="text" maxlength="100" class="form-control required">
					</div>
					<div class="form-group col">
						<label class="form-label">
							캠페인
							<span class="text-danger">*</span>
						</label>
						<input type="text" class="form-control" value="${Campaign.name}" readonly="readonly">
					</div>
				</div>
				<div class="form-row">
					<div class="form-group col">
						<label class="form-label">
							구매 유형
						</label>
						<select name="purchType" class="selectpicker bg-white" data-style="btn-default" data-tick-icon="fa-blank" data-none-selected-text="">
							<option value="G" data-icon="fa-regular fa-hexagon-check text-blue fa-fw mr-1">목표 보장</option>
							<option value="N" data-icon="fa-regular fa-hexagon-exclamation fa-fw mr-1">목표 비보장</option>
							<option value="H" data-icon="fa-regular fa-house fa-fw mr-1">하우스 광고</option>
						</select>
					</div>
					<div class="form-group col">
						<label class="form-label">
							시작일
							<span class="text-danger">*</span>
						</label>
						<input name="startDate" type="text" class="form-control required">
					</div>
					<div class="form-group col">
						<label class="form-label">
							종료일
							<span class="text-danger">*</span>
						</label>
						<input name="endDate" type="text" class="form-control required">
					</div>
				</div>
				<div class="form-row" name="purch-type-depending-div">
					<div class="form-group col">
						<label class="form-label">
							우선 순위
							<span data-toggle="tooltip" data-placement="right" title="최고 1에서 최저 10 사이의 값을 선택해 주세요. 기본 선택값은 5입니다.">
								<span class="fa-regular fa-circle-info text-info"></span>
							</span>
						</label>
						<div class="slider-primary">
							<input name="priority" type="text" data-slider-min="1" data-slider-max="10" data-slider-step="1" data-slider-value="5">
						</div>
					</div>
                </div>
				<div class="form-row" name="purch-type-depending-div">
					<div class="form-group col">
						<label class="form-label">
							광고 예산
						</label>
						<div class="input-group">
							<input name="budget" type="text" value="0" class="form-control input-change required" >
							<div class="input-group-append">
								<span class="input-group-text">원</span>
							</div>
						</div>
					</div>
					<div class="form-group col">
						<label class="form-label">
							보장 노출량
						</label>
						<div class="input-group">
							<input name="goalValue" type="text" value="0" class="form-control input-change required" >
							<div class="input-group-append">
								<span class="input-group-text">회</span>
							</div>
						</div>
					</div>
					<div class="form-group col">
						<label class="form-label">
							목표 노출량
						</label>
						<div class="input-group">
							<input name="sysValue" type="text" value="0" class="form-control input-change required" >
							<div class="input-group-append">
								<span class="input-group-text">회</span>
							</div>
						</div>
					</div>
				</div>
				<div class="form-row" name="purch-type-depending-div">
					<div class="form-group col">
						<label class="form-label">
							집행 방법
							<span data-toggle="tooltip" data-placement="right" title="광고 예산과 보장/목표 노출량 값에 따라 집행 방법이 결정됩니다.">
								<span class="fa-regular fa-circle-info text-info"></span>
							</span>
						</label>
						<select name="goalType" class="selectpicker bg-white" data-style="btn-default" data-tick-icon="fa-blank" data-none-selected-text="" disabled>
							<option value="A" data-icon="fa-regular fa-sack-dollar fa-fw mr-1">광고 예산</option>
							<option value="I" data-icon="fa-regular fa-eye fa-fw mr-1">노출량</option>
							<option value="U" data-icon="fa-regular fa-infinity fa-fw mr-1">무제한 노출</option>
						</select>
					</div>
					<div class="form-group col">
						<label class="form-label">
							일별 광고 분산
						</label>
						<select name="impDailyType" class="selectpicker bg-white" data-style="btn-default" data-tick-icon="fa-blank" data-none-selected-text="">
							<option value="E" data-icon="fa-regular fa-equals fa-fw mr-1">모든 날짜 균등</option>
							<option value="W" data-icon="fa-regular fa-bars-staggered fa-fw mr-1">통계 기반 요일별 차등</option>
						</select>
					</div>
					<div class="form-group col">
						<label class="form-label">
							1일 광고 분산
						</label>
						<select name="impHourlyType" class="selectpicker bg-white" data-style="btn-default" data-tick-icon="fa-blank" data-none-selected-text="">
							<option value="E" data-icon="fa-regular fa-equals fa-fw mr-1">모든 시간 균등</option>
							<option value="D" data-icon="fa-regular fa-sun fa-fw mr-1">일과 시간 집중</option>
						</select>
					</div>
				</div>
				<div class="form-row">
					<div class="form-group col">
						<label class="form-label">
							현재 노출량 추가 제어
						</label>
						<select name="impAddRatio" class="selectpicker bg-white" data-style="btn-default" data-tick-icon="fa-blank" data-none-selected-text="">
							<option value="1000">+1000 %</option>
							<option value="900">+900 %</option>
							<option value="800">+800 %</option>
							<option value="700">+700 %</option>
							<option value="600">+600 %</option>
							<option value="500">+500 %</option>
							<option value="400">+400 %</option>
							<option value="300">+300 %</option>
							<option value="200">+200 %</option>
							<option value="150">+150 %</option>
							<option value="100">+100 %</option>
							<option value="75">+75 %</option>
							<option value="50">+50 %</option>
							<option value="20">+20 %</option>
							<option value="0">+0 %</option>
							<option value="-15">-15 %</option>
							<option value="-30">-30 %</option>
							<option value="-50">-50 %</option>
							<option value="-70">-70 %</option>
							<option value="-90">-90 %</option>
						</select>
					</div>
					<div class="form-group col">
						<label class="form-label">
							게시 유형
						</label>
						<select name="viewType" class="selectpicker bg-white" data-style="btn-default" data-tick-icon="fa-blank" data-none-selected-text="">
<c:forEach var="item" items="${ViewTypes}">
							<option value="${item.value}">${item.text}</option>
</c:forEach>
						</select>
					</div>
					<div class="form-group col" name="purch-type-depending-div">
						<label class="form-label">
							CPM
						</label>
						<select name="cpmType" class="selectpicker bg-white" data-style="btn-default" data-tick-icon="fa-blank" data-none-selected-text="">
							<option value="0" data-icon="fa-regular fa-screen-users fa-fw mr-1">화면 설정값 기준</option>
							<option value="5" data-icon="fa-regular fa-input-numeric fa-fw mr-1">값 지정</option>
						</select>
					</div>
					<div name="purch-type-depending-div">
						<div name="cpm-value-div" class="form-group col" style="display: none;">
							<label class="form-label">
								CPM 지정
							</label>
							<div class="input-group">
								<input name="cpm" type="text" value="1000" class="form-control required" >
								<div class="input-group-append">
									<span class="input-group-text">원</span>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="form-row">
					<div class="form-group col">
						<label class="form-label">
							하루 노출한도
							<span data-toggle="tooltip" data-placement="right" title="하루 동안 매체 전체 화면에서의 총합입니다.">
								<span class="fa-regular fa-circle-info text-info"></span>
							</span>
						</label>
						<select name="dailyCapType" class="selectpicker bg-white" data-style="btn-default" data-tick-icon="fa-blank" data-none-selected-text="">
							<option value="0" data-icon="fa-regular fa-circle-xmark fa-fw mr-1">노출한도 설정 안함</option>
							<option value="1" data-icon="fa-regular fa-input-numeric fa-fw mr-1">값 지정</option>
						</select>
					</div>
					<div name="daily-cap-value-div" class="form-group col" style="display: none;">
						<label class="form-label">
							노출한도 값 지정
						</label>
						<div class="input-group">
							<input name="dailyCap" type="text" class="form-control required" value="1000">
							<div class="input-group-append">
								<span class="input-group-text">회</span>
							</div>
						</div>
					</div>
					<div class="form-group col">
						<label class="form-label">
							동일 광고 송출 금지 시간
							<span data-toggle="tooltip" data-placement="right" title="동일한 광고가 송출되지 않도록 보장되는 시간입니다.">
								<span class="fa-regular fa-circle-info text-info"></span>
							</span>
						</label>
						<select name="freqCapType" class="selectpicker bg-white" data-style="btn-default" data-tick-icon="fa-blank" data-none-selected-text="">
							<option value="0" data-icon="fa-regular fa-earth-asia fa-fw mr-1">매체 설정값(${mediumFreqCapAd}) 적용</option>
							<option value="1" data-icon="fa-regular fa-circle-xmark fa-fw mr-1">금지 시간 설정 안함</option>
							<option value="2" data-icon="fa-regular fa-input-numeric fa-fw mr-1">값 지정</option>
						</select>
					</div>
					<div name="freq-cap-value-div" class="form-group col" style="display: none;">
						<label class="form-label">
							송출 금지 값 지정
						</label>
						<div class="input-group">
							<input name="freqCap" type="text" class="form-control required" value="0">
							<div class="input-group-append">
								<span class="input-group-text">초</span>
							</div>
						</div>
					</div>
				</div>
				<div class="form-row">
					<div class="form-group col">
						<label class="form-label">
							화면당 하루 노출한도
						</label>
						<select name="dailyScrCapType" class="selectpicker bg-white" data-style="btn-default" data-tick-icon="fa-blank" data-none-selected-text="">
							<option value="0" data-icon="fa-regular fa-earth-asia fa-fw mr-1">매체 설정값(${mediumDailyScrCap}) 적용</option>
							<option value="1" data-icon="fa-regular fa-input-numeric fa-fw mr-1">값 지정</option>
						</select>
					</div>
					<div name="daily-scr-cap-value-div" class="form-group col" style="display: none;">
						<label class="form-label">
							화면당 하루 노출한도 값 지정
						</label>
						<div class="input-group">
							<input name="dailyScrCap" type="text" value="100" class="form-control required" >
							<div class="input-group-append">
								<span class="input-group-text">회</span>
							</div>
						</div>
					</div>
					<div class="form-group col">
						<label class="form-label">
							재생 시간
						</label>
						<select name="durationType" class="selectpicker bg-white" data-style="btn-default" data-tick-icon="fa-blank" data-none-selected-text="">
							<option value="0" data-icon="fa-regular fa-screen-users fa-fw mr-1">화면 설정값 기준</option>
							<option value="5" data-icon="fa-regular fa-input-numeric fa-fw mr-1">값 지정</option>
						</select>
					</div>
					<div class="form-group col" name="duration-value-div" style="display: none;">
						<label class="form-label">
							재생 시간 지정
						</label>
						<div class="input-group">
							<input name="durSecs" type="text" value="15" class="form-control required">
							<div class="input-group-append">
								<span class="input-group-text">초</span>
							</div>
						</div>
					</div>
				</div>
				<div class="form-row">
					<div class="form-group col mb-0">
						<label class="form-label">
							운영자 메모
						</label>
						<textarea name="memo" rows="2" maxlength="150" class="form-control"></textarea>
					</div>
				</div>
			</div>
        
			<!-- Modal footer -->
			<div class="modal-footer d-flex">
				<button type="button" class="btn btn-round btn-outline-secondary mr-auto" onClick="openCalc()">
                	<span class="fa-light fa-calculator"></span>
                	<span class="ml-1">노출량 계산기</span>
				</button>
				<button type="button" class="btn btn-default" data-dismiss="modal">취소</button>
				<button type="button" class="btn btn-primary" onclick='saveForm1()'>저장</button>
			</div>
			
		</form>
	</div>
</div>

</script>

<style>


</style>


<div class="modal fade modal-level-plus-1" id="modal-calculator" tabindex="-1" role="dialog">
	<div class="modal-dialog modal-dialog-centered modal-sm" role="document">
		<div class="modal-content">
      
			<!-- Modal Header -->
			<div class="modal-header move-cursor">
				<h5 class="modal-title">
					노출량 계산기
				</h5>
				<button type="button" class="close" data-dismiss="modal">×</button>
			</div>
        
			<!-- Modal body -->
			<div class="modal-body modal-bg-color">
				<div class="form-group col">
					<label class="form-label">
						총 광고일수
					</label>
					<div class="input-group">
						<input id="calc-item-total-days" type="text" value="1" class="form-control"  disabled>
						<div class="input-group-append">
							<span class="input-group-text" id="tgt-today-postfix">일</span>
						</div>
					</div>
				</div>
				<div class="form-group col">
					<label class="form-label">
						화면당 1일 노출량
					</label>
					<div class="input-group">
						<input id="calc-item-cnt-per-one-day-screen" type="text" value="100" class="form-control"
						 		onchange="calcImpCnt()"
						 		oninput="this.value = this.value.replace(/[^0-9]/g, '');">
						<div class="input-group-append">
							<span class="input-group-text"]>회</span>
						</div>
					</div>
				</div>
				<div class="form-group col">
					<label class="form-label">
						매체 화면 수
					</label>
					<div class="input-group">
						<input id="calc-item-active-screen-cnt" type="text" value="1" class="form-control"
								onchange="calcImpCnt()"
						 		oninput="this.value = this.value.replace(/[^0-9]/g, '');">
						<div class="input-group-append">
							<span class="input-group-text">기</span>
						</div>
					</div>
				</div>
				<div class="form-group col">
					<label class="form-label">
						총 노출량
					</label>
					<div class="input-group">
						<input id="calc-item-impression-cnt" type="text" value="1" class="form-control"  disabled>
						<div class="input-group-append">
							<span class="input-group-text">회</span>
						</div>
					</div>
				</div>
			</div>
        
			<!-- Modal footer -->
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">취소</button>
				<button type="button" class="btn btn-primary" onclick='pasteCalcResult()'>복사</button>
			</div>
			
		</div>
	</div>
</div>

<!--  / Forms -->


<!--  Scripts -->

<script>

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


function validatePurchType() {

	if ($("#form-1 select[name='purchType']").val() == "H") {
		$("#form-1 div[name='purch-type-depending-div']").hide();
	} else {
		$("#form-1 div[name='purch-type-depending-div']").show();
	}
}


function validateDurationType() {
	
	if ($("#form-1 select[name='durationType']").val() == "0") {
		$("#form-1 div[name='duration-value-div']").hide();
		$("#form-1 input[name='durSecs']").attr('readonly', 'readonly');
	} else {
		$("#form-1 input[name='durSecs']").removeAttr('readonly');
		$("#form-1 div[name='duration-value-div']").show();
		$("#form-1 input[name='durSecs']").select();
		$("#form-1 input[name='durSecs']").focus();
	}
}


function validateCpmType() {
	
	if ($("#form-1 select[name='cpmType']").val() == "0") {
		$("#form-1 div[name='cpm-value-div']").hide();
		$("#form-1 input[name='cpm']").attr('readonly', 'readonly');
	} else {
		$("#form-1 input[name='cpm']").removeAttr('readonly');
		$("#form-1 div[name='cpm-value-div']").show();
		$("#form-1 input[name='cpm']").select();
		$("#form-1 input[name='cpm']").focus();
	}
}


function validateDailyCapType() {
	
	if ($("#form-1 select[name='dailyCapType']").val() == "0") {
		$("#form-1 div[name='daily-cap-value-div']").hide();
		$("#form-1 input[name='dailyCap']").attr('readonly', 'readonly');
	} else {
		$("#form-1 input[name='dailyCap']").removeAttr('readonly');
		$("#form-1 div[name='daily-cap-value-div']").show();
		$("#form-1 input[name='dailyCap']").select();
		$("#form-1 input[name='dailyCap']").focus();
	}
}


function validateFreqCapType() {
	
	if ($("#form-1 select[name='freqCapType']").val() == "0" || $("#form-1 select[name='freqCapType']").val() == "1") {
		$("#form-1 div[name='freq-cap-value-div']").hide();
		$("#form-1 input[name='freqCap']").attr('readonly', 'readonly');
	} else {
		$("#form-1 input[name='freqCap']").removeAttr('readonly');
		$("#form-1 div[name='freq-cap-value-div']").show();
		$("#form-1 input[name='freqCap']").select();
		$("#form-1 input[name='freqCap']").focus();
	}
}


function validateDailyScrCapType() {
	
	if ($("#form-1 select[name='dailyScrCapType']").val() == "0") {
		$("#form-1 div[name='daily-scr-cap-value-div']").hide();
		$("#form-1 input[name='dailyScrCap']").attr('readonly', 'readonly');
	} else {
		$("#form-1 input[name='dailyScrCap']").removeAttr('readonly');
		$("#form-1 div[name='daily-scr-cap-value-div']").show();
		$("#form-1 input[name='dailyScrCap']").select();
		$("#form-1 input[name='dailyScrCap']").focus();
	}
}


function checkGoalType() {
	
	// 예산, 보장 노출량, 목표 노출량
	var budget = Number($.trim($("#form-1 input[name='budget']").val()));
	var goalValue = Number($.trim($("#form-1 input[name='goalValue']").val()));
	var sysValue = Number($.trim($("#form-1 input[name='sysValue']").val()));
	
    if (goalValue > 0 || sysValue > 0) {
    	bootstrapSelectVal($("#form-1 select[name='goalType']"), "I");
    	$("#tgt-today-postfix").text("회");
    } else if (budget > 0) {
    	bootstrapSelectVal($("#form-1 select[name='goalType']"), "A");
    	$("#tgt-today-postfix").text("원");
    } else {
    	bootstrapSelectVal($("#form-1 select[name='goalType']"), "U");
    	$("#tgt-today-postfix").text("회");
    }
}


function initForm1(subtitle) {
	
	$("#formRoot").html(kendo.template($("#template-1").html()));
	
	$("#form-1 select[name='purchType']").selectpicker('render');
	$("#form-1 select[name='durationType']").selectpicker('render');
	$("#form-1 select[name='goalType']").selectpicker('render');
	$("#form-1 select[name='cpmType']").selectpicker('render');
	$("#form-1 select[name='impDailyType']").selectpicker('render');
	$("#form-1 select[name='impHourlyType']").selectpicker('render');
	$("#form-1 select[name='impAddRatio']").selectpicker('render');
	$("#form-1 select[name='dailyCapType']").selectpicker('render');
	$("#form-1 select[name='freqCapType']").selectpicker('render');
	$("#form-1 select[name='dailyScrCapType']").selectpicker('render');
	$("#form-1 select[name='viewType']").selectpicker('render');
	
	var today = new Date();
	today.setHours(0, 0, 0, 0);
	
	var start = new Date();
	var end = new Date();
	start.setDate(today.getDate() + 2);
	end.setDate(today.getDate() + 31);
	
	$("#form-1 input[name='startDate']").kendoDatePicker({
		format: "yyyy-MM-dd",
		parseFormats: [
			"yyyy-MM-dd",
		],
		value: start,
		//min: today,
	});
	
	$("#form-1 input[name='endDate']").kendoDatePicker({
		format: "yyyy-MM-dd",
		parseFormats: [
			"yyyy-MM-dd",
		],
		value: end,
		//min: today,
	});
	
	$("#form-1 select[name='purchType']").on("change.bs.select", function(e){
		validatePurchType();
	});
	
	$("#form-1 select[name='durationType']").on("change.bs.select", function(e){
		validateDurationType();
	});
	
	$("#form-1 select[name='cpmType']").on("change.bs.select", function(e){
		validateCpmType();
	});
	
	$("#form-1 select[name='dailyCapType']").on("change.bs.select", function(e){
		validateDailyCapType();
	});
	
	$("#form-1 select[name='freqCapType']").on("change.bs.select", function(e){
		validateFreqCapType();
	});
	
	$("#form-1 select[name='dailyScrCapType']").on("change.bs.select", function(e){
		validateDailyScrCapType();
	});
	
	$(".input-change").change(function(){
		checkGoalType();
	});

	
	bootstrapSelectVal($("#form-1 select[name='goalType']"), "U");
	bootstrapSelectVal($("#form-1 select[name='impDailyType']"), "E");
	bootstrapSelectVal($("#form-1 select[name='impHourlyType']"), "E");
	bootstrapSelectVal($("#form-1 select[name='impAddRatio']"), "0");
	
	$("#form-1 textarea[name='memo']").keypress(function (e) {
		if (e.keyCode != 13) {
			return;
		}
		
		$(this).text().replace(/\n/g, "");
		
		return false;
	});
	
	$('[data-toggle="tooltip"]').tooltip();

	
	if (subtitle == null) {		
		// ADD 모드
		$("#form-1 input[name='startDate']").data("kendoDatePicker").min(today);
		$("#form-1 input[name='endDate']").data("kendoDatePicker").min(today);
	}

	$("#form-1 input[name='priority']").slider();
	

	$("#form-1 span[name='subtitle']").text(subtitle ? subtitle : "추가");
	

	$("#form-1").validate({
		rules: {
			name: {
				minlength: 2,
			},
			startDate: { date: true },
			endDate: { date: true },
			budget: {
				digits: true,
			},
			sysValue: {
				digits: true,
			},
			cpm: {
				digits: true,
				min: 1,
			},
			durSecs: {
				digits: true,
				min: 5,
			},
			freqCap: {
				digits: true, min: 10,
			},
			goalValue: {
				digits: true,
			},
			dailyCap: {
				digits: true, min: 1,
			},
			dailyScrCap: {
				digits: true, min: 1,
			},
		}
	});
}


function saveForm1() {

	// kendo datepicker validation
	validateKendoDateValue($("#form-1 input[name='startDate']"));
	validateKendoDateValue($("#form-1 input[name='endDate']"));

	if ($("#form-1").valid()) {
		
		var goAhead = true;
		
		var durationType = $("#form-1 select[name='durationType']").val();
		var durSecs = Number($.trim($("#form-1 input[name='durSecs']").val()));
		if (durationType == "0") {
			durSecs = 0;
		} else {
			goAhead = durSecs >= 5;
		}
		
		var cpmType = $("#form-1 select[name='cpmType']").val();
		var cpm = Number($.trim($("#form-1 input[name='cpm']").val()));
		if (cpmType == "0") {
			cpm = 0;
		} else {
			goAhead = goAhead && cpm >= 1;
		}
		
		var dailyCapType = $("#form-1 select[name='dailyCapType']").val();
		var dailyCap = Number($.trim($("#form-1 input[name='dailyCap']").val()));
		if (dailyCapType == "0") {
			dailyCap = 0;
		}
		
		var freqCapType = $("#form-1 select[name='freqCapType']").val();
		var freqCap = Number($.trim($("#form-1 input[name='freqCap']").val()));
		if (freqCapType == "0") {
			freqCap = 0;
		} else if (freqCapType == "1") {
			freqCap = 1;
		}
		
		var dailyScrCapType = $("#form-1 select[name='dailyScrCapType']").val();
		var dailyScrCap = Number($.trim($("#form-1 input[name='dailyScrCap']").val()));
		if (dailyScrCapType == "0") {
			dailyScrCap = 0;
		}

		
    	var data = {
    		id: Number($("#form-1").attr("rowid")),
    		name: $.trim($("#form-1 input[name='name']").val()),
    		campaign: ${Campaign.id},
    		purchType: $("#form-1 select[name='purchType']").val(),
    		priority: Number($.trim($("#form-1 input[name='priority']").val())),
    		startDate: $("#form-1 input[name='startDate']").data("kendoDatePicker").value(),
    		endDate: $("#form-1 input[name='endDate']").data("kendoDatePicker").value(),
    		impAddRatio: Number($("#form-1 select[name='impAddRatio']").val()),
    		freqCap: freqCap,
    		durSecs: durSecs,
    		goalType: $("#form-1 select[name='goalType']").val(),
    		goalValue: Number($.trim($("#form-1 input[name='goalValue']").val())),
    		sysValue: Number($("#form-1 input[name='sysValue']").val()),
    		budget: Number($("#form-1 input[name='budget']").val()),
    		dailyCap: dailyCap,
    		dailyScrCap: dailyScrCap,
    		cpm: cpm,
    		impDailyType: $("#form-1 select[name='impDailyType']").val(),
    		impHourlyType: $("#form-1 select[name='impHourlyType']").val(),
    		viewType: $("#form-1 select[name='viewType']").val(),
    		memo: $.trim($("#form-1 textarea[name='memo']").val()),
    	};
    	
    	if (goAhead) {

    		$.ajax({
    			type: "POST",
    			contentType: "application/json",
    			dataType: "json",
    			url: $("#form-1").attr("url"),
    			data: JSON.stringify(data),
    			success: function (data, status, xhr) {
    				
    				showSaveSuccessMsg();
    				$("#form-modal-1").modal("hide");

    				reload();
    			},
    			error: ajaxSaveError
    		});
    	}
	}
}


function edit() {
	
	initForm1("변경");

	var startDate = new Date(${Ad.startDateLong});
	var endDate = new Date(${Ad.endDateLong});
	
	
	$("#form-1").attr("rowid", ${Ad.id});
	$("#form-1").attr("url", "${updateUrl}");
	
	$("#form-1 input[name='name']").val("${Ad.name}");
	$("#form-1 input[name='freqCap']").val("${Ad.freqCap}");
	$("#form-1 input[name='goalValue']").val("${Ad.goalValue}");
	$("#form-1 input[name='dailyCap']").val("${Ad.dailyCap}");

	$("#form-1 input[name='budget']").val(${Ad.budget});
	$("#form-1 input[name='sysValue']").val(${Ad.sysValue});
	$("#form-1 input[name='dailyScrCap']").val(${Ad.dailyScrCap});

	bootstrapSelectVal($("#form-1 select[name='purchType']"), "${Ad.purchType}");
	bootstrapSelectVal($("#form-1 select[name='goalType']"), "${Ad.goalType}");
	bootstrapSelectVal($("#form-1 select[name='viewType']"), "${Ad.viewTypeCode}");

	bootstrapSelectVal($("#form-1 select[name='impAddRatio']"), "${Ad.impAddRatio}");
	bootstrapSelectVal($("#form-1 select[name='impDailyType']"), "${Ad.impDailyType}");
	bootstrapSelectVal($("#form-1 select[name='impHourlyType']"), "${Ad.impHourlyType}");

	$("#form-1 input[name='startDate']").data("kendoDatePicker").value(startDate);
	$("#form-1 input[name='endDate']").data("kendoDatePicker").value(endDate);

	$("#form-1 textarea[name='memo']").text("${Ad.memo}");
	
	bootstrapSelectDisabled($("#form-1 select[name='campaign']"), true);
	

	// 시작일과 종료일에 대해 오늘 날짜 비교하여 더 작은 값을 min으로, 더 큰 값을 max로 처리
	var today = new Date();
	today.setHours(0, 0, 0, 0);
	
	if (startDate < today) {
		$("#form-1 input[name='startDate']").data("kendoDatePicker").min(startDate);
		$("#form-1 input[name='endDate']").data("kendoDatePicker").min(startDate);
	}

	$("#form-1 input[name='startDate']").data("kendoDatePicker").enable(${Ad.startDateEditable});
	
	$("#form-1 input[name='priority']").slider("setValue", [${Ad.priority}]);
	
	if (${Ad.duration} == 0) {
		bootstrapSelectVal($("#form-1 select[name='durationType']"), "0");
		$("#form-1 input[name='durSecs']").val("15");		// 초기값으로 15
	} else {
		bootstrapSelectVal($("#form-1 select[name='durationType']"), "5");
		$("#form-1 input[name='durSecs']").val("${Ad.duration}");
	}
	validateDurationType();
	
	if (${Ad.cpm} == 0) {
		bootstrapSelectVal($("#form-1 select[name='cpmType']"), "0");
		$("#form-1 input[name='cpm']").val("1000");		// 초기값으로 1000
	} else {
		bootstrapSelectVal($("#form-1 select[name='cpmType']"), "5");
		$("#form-1 input[name='cpm']").val("${Ad.cpm}");
	}
	validateCpmType();
	
	if (${Ad.dailyCap} == 0) {
		bootstrapSelectVal($("#form-1 select[name='dailyCapType']"), "0");
		$("#form-1 input[name='dailyCap']").val("1000");		// 초기값으로 1000
	} else {
		bootstrapSelectVal($("#form-1 select[name='dailyCapType']"), "1");
		$("#form-1 input[name='dailyCap']").val("${Ad.dailyCap}");
	}
	validateDailyCapType();
	
	if (${Ad.freqCap} == 0) {
		bootstrapSelectVal($("#form-1 select[name='freqCapType']"), "0");
		$("#form-1 input[name='freqCap']").val("60");		// 초기값으로 60
	} else if (${Ad.freqCap} == 1) {
		bootstrapSelectVal($("#form-1 select[name='freqCapType']"), "1");
		$("#form-1 input[name='freqCap']").val("60");		// 초기값으로 60
	} else {
		bootstrapSelectVal($("#form-1 select[name='freqCapType']"), "1");
		$("#form-1 input[name='freqCap']").val("${Ad.freqCap}");
	}
	validateFreqCapType();
	
	if (${Ad.dailyScrCap} == 0) {
		bootstrapSelectVal($("#form-1 select[name='dailyScrCapType']"), "0");
		$("#form-1 input[name='dailyScrCap']").val("100");		// 초기값으로 100
	} else {
		bootstrapSelectVal($("#form-1 select[name='dailyScrCapType']"), "1");
		$("#form-1 input[name='dailyScrCap']").val("${Ad.dailyScrCap}");
	}
	validateDailyScrCapType();
	
	validatePurchType();
	
	if ("${Ad.goalType}" == "A") {
		$("#tgt-today-postfix").text("원");
	} else {
		$("#tgt-today-postfix").text("회");
	}

	
	$('#form-modal-1 .modal-dialog').draggable({ handle: '.modal-header' });
	$("#form-modal-1").modal();
}


function reload() {
	
	setTimeout(function(){
		location.reload();
	}, 1000);
}

</script>


<script>

var mobileOrderTemplate = kendo.template($("#targetOrderViewMobilTemplate").html());
var invenOrderTemplate = kendo.template($("#targetOrderViewInvenTemplate").html());

var calcPasteTgtItem = null;

function openCalc() {
	
	// 총 광고일수: 광고 시작일과 종료일의 차이로
	// 화면당 1일 노출량: 기본값 100으로
	// 매체 화면수: 컨트롤러에 의한 전달

	var totalDays = 1;
	
	var startDate = $("#form-1 input[name='startDate']").data("kendoDatePicker").value();
	var endDate = $("#form-1 input[name='endDate']").data("kendoDatePicker").value();
	
	if (startDate != null || endDate != null) {
		totalDays = Math.round((endDate.getTime() - startDate.getTime() + 1) / (1000 * 3600 * 24) + 1);
	}
	if (totalDays < 1) {
		totalDays = 1;
	}
	
	var activeScrCnt = ${mediumActiveScrCnt};
	if (activeScrCnt < 1) {
		activeScrCnt = 1;
	}
	
	var oneDayScrImpCnt = 100;
	
	$("#calc-item-total-days").val(totalDays);
	$("#calc-item-cnt-per-one-day-screen").val(oneDayScrImpCnt);
	$("#calc-item-active-screen-cnt").val(activeScrCnt);
	$("#calc-item-impression-cnt").val(totalDays * oneDayScrImpCnt * activeScrCnt);
	
	
	calcPasteTgtItem = $("#form-1 input[name='goalValue']");

	
	$('#modal-calculator .modal-dialog').draggable({ handle: '.modal-header' });
	$("#modal-calculator").modal();
}


function calcImpCnt() {

	var totalDays = Number($("#calc-item-total-days").val());
	var oneDayScrImpCnt = Number($("#calc-item-cnt-per-one-day-screen").val());
	var activeScrCnt = Number($("#calc-item-active-screen-cnt").val());
	
	$("#calc-item-impression-cnt").val(totalDays * oneDayScrImpCnt * activeScrCnt);
}


function pasteCalcResult() {
	
	calcPasteTgtItem.val($("#calc-item-impression-cnt").val());
	checkGoalType();
	
	$("#modal-calculator").modal("hide");
}


$(document).ready(function() {
	$("#modal-calculator").on('show.bs.modal', function (e) {

		setTimeout(function(){
			$('.modal-backdrop:last-child').addClass('modal-level-plus-1');
		});
	});
});


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


function setExpHourStrCustom(gid, timeStr) {

	if (timeStr.length == 168) {
		var cnt = 0;
		for(var i = 0; i < 168; i++) {
			$("#btt-" + gid + "-" + i).removeClass("selected rselected nselected");
			if (Number(timeStr.substr(i, 1)) == 1) {
				$("#btt-" + gid + "-" + i).addClass("rselected");
				cnt ++;
			}
		}
	}
}


</script>

<!--  / Scripts -->


</c:otherwise>
</c:choose>

<!--  / Page details -->


<!-- / Page body -->





<!-- Functional tags -->

<func:cmmValidate />


<!-- Closing tags -->

<common:base />
<common:pageClosing />
