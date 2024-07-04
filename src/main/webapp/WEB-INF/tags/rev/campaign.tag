<%@ tag pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


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
			<button type="button" class="btn btn-outline-blue btn-round icon-btn btn-custom mr-3" id="qlb-campaign-setting" title="캠페인 설정">
				<i class="fa-light fa-sliders fa-lg"></i>
			</button>
			<button type="button" class="btn btn-outline-dark btn-round icon-btn btn-custom mr-1" id="qlb-campaign" title="리포트 목록">
				<i class="fa-light fa-file-invoice fa-lg"></i>
			</button>
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
					</div>
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4">
			<div class="d-flex align-items-center container-p-x py-4">
			
	<c:choose>
	<c:when test="${Campaign.goalType == 'A'}">

				<span class="fa-thin fa-sack-dollar fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">예산</div>
					<div class="text-large"><span id="campaign-budget"></span> 원</div>
				</div>

	</c:when>
	<c:when test="${Campaign.goalType == 'I'}">

				<span class="fa-thin fa-eye fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">보장량</div>
					<div class="text-large"><span id="campaign-goal-value"></span> 회</div>
				</div>

	</c:when>
	<c:when test="${Campaign.goalType == 'U'}">

				<span class="fa-thin fa-empty-set fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">예산/보장량</div>
					<div class="text-large">설정없음</div>
				</div>

	</c:when>
	<c:when test="${Campaign.goalType == 'M'}">

				<span class="fa-thin fa-eye fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">보장량</div>
					<div class="text-large"><span id="campaign-goal-value"></span> 회</div>
				</div>

	</c:when>
	</c:choose>
				
			</div>
		</div>
		<div class="col-sm-6 col-md-4">
			<div class="d-flex align-items-center container-p-x py-4">
			
	<c:choose>
	<c:when test="${Campaign.goalType == 'A'}">

				<span class="fa-thin fa-sack-dollar fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">집행 방법</div>
					<div class="text-large">광고 예산</div>
				</div>

	</c:when>
	<c:when test="${Campaign.goalType == 'I'}">

				<span class="fa-thin fa-eye fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">집행 방법</div>
					<div class="text-large">노출량</div>
				</div>

	</c:when>
	<c:when test="${Campaign.goalType == 'U'}">

				<span class="fa-thin fa-infinity fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">집행 방법</div>
					<div class="text-large">무제한 노출</div>
				</div>

	</c:when>
	<c:when test="${Campaign.goalType == 'M'}">

				<span class="fa-thin fa-question fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">집행 방법</div>
					<div class="text-large">여러 방법</div>
				</div>

	</c:when>
	</c:choose>
					
			</div>
		</div>
		<div class="col-sm-6 col-md-4">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-percent fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">달성률</div>
					<div class="text-large"><span id="campaign-achv-ratio"></span></div>
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-eye fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">노출량</div>
					<div class="text-large"><span id="campaign-actual-value"></span> 회</div>
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-coins fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">집행금액</div>
					<div class="text-large"><span id="campaign-actual-amount"></span> 원</div>
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-coin fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">집행 CPM</div>
					<div class="text-large"><span id="campaign-actual-cpm"></span> 원</div>
				</div>
			</div>
		</div>
		
<c:if test="${not empty Campaign.tgtTodayDisp}">
		
		<div class="col-sm-12 col-md-12">
			<div class="d-flex align-items-center justify-content-center py-2 lead footer-title">${Campaign.tgtTodayDisp}</div>
		</div>
		
</c:if>

	</div>
</div>


<!-- Form button actions  -->

<script>
$(document).ready(function() {

	// Quick Link Buttons
	$("#qlb-campaign-setting").click(function(e) {
		e.preventDefault();
		
		location.href = "/adc/campaign/${Campaign.id}";
	});
	
	$("#qlb-campaign").click(function(e) {
		e.preventDefault();
		
		location.href = "/rev/camprpt";
	});
	
	$("#qlb-ad").click(function(e) {
		e.preventDefault();
		
		location.href = "/rev/adrpt";
	});
	// / Quick Link Buttons

	var campTitle = "<span>${Campaign.name}</span>";
	if ('${Campaign.statusCard}' == 'Y') {
		campTitle = campTitle + "<span class='pl-1'></span><span title='오늘 진행되는 광고 소재 없음'><span class='fa-solid fa-rectangle-vertical text-yellow'></span></span>";
	} else if ('${Campaign.statusCard}' == 'R') {
		campTitle = campTitle + "<span class='pl-1'></span><span title='활성중인 광고 소재 없음'><span class='fa-solid fa-rectangle-vertical text-danger'></span></span>";
	}
	
	$("#campaign-name").html(campTitle);
	
	$("#campaign-budget").html(kendo.format("{0:n0}", ${Campaign.budget}));
	$("#campaign-goal-value").html(kendo.format("{0:n0}", ${Campaign.goalValue}));
	
	$("#campaign-achv-ratio").html(kendo.format("{0:n2}", ${Campaign.achvRatio}));
	$("#campaign-actual-value").html(kendo.format("{0:n0}", ${Campaign.actualValue}));
	$("#campaign-actual-amount").html(kendo.format("{0:n0}", ${Campaign.actualAmount}));
	$("#campaign-actual-cpm").html(kendo.format("{0:n0}", ${Campaign.actualCpm}));

});
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


/* 오늘/하루 목표 출력 */
.footer-title {
	font-size: 30px;
	font-weight: 300;
}
.footer-title small {
	font-size: 20px;
	font-weight: 300;
}

</style>

<!-- / Form button actions  -->

<!--  / HTML tags -->

