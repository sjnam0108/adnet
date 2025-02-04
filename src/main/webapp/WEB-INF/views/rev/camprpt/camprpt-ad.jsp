<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>

<%@ taglib prefix="rev" tagdir="/WEB-INF/tags/rev"%>


<!-- URL -->

<c:url value="/rev/camprpt/ad/read" var="readUrl" />
<c:url value="/rev/camprpt/ad/readDaily" var="readDailyUrl" />
<c:url value="/rev/camprpt/ad/readWeekDaily" var="readWeekDailyUrl" />
<c:url value="/rev/camprpt/ad/readScreenDaily" var="readScreenDailyUrl" />
<c:url value="/rev/camprpt/ad/readChart" var="readChartUrl" />


<!-- Opening tags -->

<common:mediumPageOpening />


<!-- Page title -->

<h4 class="pt-1 pb-3 mb-3">
	<span class="text-muted font-weight-light">${pageTitle}<span class="px-2">/</span></span>
	<span class="mr-1 fa-light fa-audio-description"></span><span class="pl-1">광고</span>
</h4>

<hr class="border-light container-m--x mt-0 mb-4">





<!-- Page body -->


<!--  Overview header -->

<rev:campaign />

<!--  / Overview header -->


<!--  Tab -->
<ul class="nav nav-tabs tabs-alt mb-4 mt-3">
	<li class="nav-item">
		<a class="nav-link active" href="/rev/camprpt/ad/${Campaign.id}">
			<i class="mr-1 fa-light fa-audio-description"></i>
			광고
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" href="/rev/camprpt/creat/${Campaign.id}">
			<i class="mr-1 fa-light fa-clapperboard-play"></i>
			광고 소재
		</a>
	</li>
</ul>
<!--  / Tab -->


<!--  Root form container -->
<div id="formRoot"></div>


<!--  Page details -->


<!-- Java(optional)  -->

<%

%>


<!--  / Page details -->


<!-- Java(optional)  -->

<%
	String noRecordsTemplate =
			"<div class='container text-center my-4'>" +
				"<div class='d-flex justify-content-center align-self-center'>" +
					"<span class='fa-thin fa-hexagon-exclamation fa-3x'></span>" +
					"<span class='pl-3 mt-2' style='font-weight: 300; font-size: 1.3rem;'>해당 자료 없음</span>" +
				"</div>" +
			"</div>";

	String rowClass =
			"# if (totalRow) { #" +
				"accent" +
			"# } #";
	String nameTemplate =
			"<div>" + 
				"# if (paused == true) { #" +
					"<span title='잠시 멈춤'><span class='fa-regular fa-circle-pause text-danger'></span></span><span class='pr-1'></span>" +
				"# } #" +
				"# if (totalRow) { #" +
					"<span class='accent'>#= name #</span>" + 
				"# } else { #" +
					"# if (status == 'A' || status == 'R') { #" +
						"# if (impAddRatio > 0) { #" +
							"<span title='현재 노출량 추가 제어'><span class='badge badge-pill badge-success'><small>#= impAddRatioDisp #</small></span></span>" +
						"# } else if (impAddRatio < 0) { #" +
							"<span title='현재 노출량 추가 제어'><span class='badge badge-pill badge-danger'><small>#= impAddRatioDisp #</small></span></span>" +
						"# } # " +
					"# } # " +
					"<a href='javascript:navToAd(-1, #= id #)'><span class='text-link'>#= name #</span></a>" +
				"# } #" +
				"# if (mobTargeted == true) { #" +
					"<span class='pl-1'></span><span title='모바일 타겟팅'><span class='fa-regular fa-bullseye-arrow text-orange'></span></span>" +
				"# } else if (invenTargeted == true) { #" +
					"<span class='pl-1'></span><span title='인벤토리 타겟팅'><span class='fa-regular fa-bullseye-arrow text-blue'></span></span>" +
				"# } #" +
				"# if (timeTargeted == true) { #" +
					"<span class='pl-1'></span><span title='시간 타겟팅'><span class='fa-regular fa-alarm-clock text-green'></span></span>" +
				"# } #" +
				"# if (tgtTodayDisp) { #" +
					"<span data-toggle='tooltip' data-placement='right' title=\"#= tgtTodayDisp #\">" +
						"<span class='pl-1'><span class='fa-regular fa-golf-flag-hole text-info'></span></span>" +
					"</span>" +
				"# } #" +
				"# if (singleAdFiltered) { #" +
					"<a href='javascript:filterAd(#= id #)' class='btn btn-default btn-xs icon-btn ml-1' title='이 광고로만 제한'><span class='fa-regular fa-filter text-secondary'></span></a>" +
				"# } #" +
			"</div>";
	String statusTemplate =
			"# if (status == 'D') { #" +
				"<span class='fa-regular fa-asterisk fa-fw'></span><span class='pl-1 " + rowClass + "'>준비</span>" +
			"# } else if (status == 'P') { #" +
				"<span class='fa-regular fa-square-question fa-fw'></span><span class='pl-1 " + rowClass + "'>승인대기</span>" +
			"# } else if (status == 'J') { #" +
				"<span class='fa-regular fa-do-not-enter fa-fw'></span><span class='pl-1 " + rowClass + "'>거절</span>" +
			"# } else if (status == 'A') { #" +
				"<span class='fa-regular fa-alarm-clock fa-fw'></span><span class='pl-1 " + rowClass + "'>예약</span>" +
			"# } else if (status == 'R') { #" +
				"<span class='fa-regular fa-bolt-lightning text-orange fa-fw'></span><span class='pl-1 " + rowClass + "'>진행</span>" +
			"# } else if (status == 'C') { #" +
				"<span class='fa-regular fa-flag-checkered fa-fw'></span><span class='pl-1 " + rowClass + "'>완료</span>" +
			"# } else if (status == 'V') { #" +
				"<span class='fa-regular fa-box-archive fa-fw'></span><span class='pl-1 " + rowClass + "'>보관</span>" +
			"# } else { #" +
				"<span>-</span>" +
			"# } #";

	String purchTypeTemplate =
			"# if (purchType == 'G') { #" +
				"<span class='fa-regular fa-hexagon-check text-blue fa-fw'></span><span class='pl-1 " + rowClass + "'>목표 보장.#= priority #</span>" +
			"# } else if (purchType == 'N') { #" +
				"<span class='fa-regular fa-hexagon-exclamation fa-fw'></span><span class='pl-1 " + rowClass + "'>목표 비보장.#= priority #</span>" +
			"# } else if (purchType == 'H') { #" +
				"<span class='fa-regular fa-house fa-fw'></span><span class='pl-1 " + rowClass + "'>하우스 광고</span>" +
			"# } else { #" +
				"<span>-</span>" +
			"# } #";
	String budgetTemplate =
			"# if (purchType == 'H') { #" +
				"<span></span>" +
			"# } else { #" +
				"<span class='" + rowClass + "'>#= kendo.format('{0:n0}', budget) #</span>" +
			"# } #";
	String goalValueTemplate =
			"# if (purchType == 'H') { #" +
				"<span></span>" +
			"# } else { #" +
				"<span class='" + rowClass + "'>#= kendo.format('{0:n0}', goalValue) #</span>" +
			"# } #";
	String sysValueTemplate =
			"# if (purchType == 'H') { #" +
				"<span></span>" +
			"# } else if (totalRow) { #" +
				"<span class='accent'>#= kendo.format('{0:n0}', sysValue) #</span>" +
			"# } else if (sysValueProposed) { #" +
				"<span class=\"text-muted\">#= kendo.format('{0:n0}', sysValue) #</span>" +
				"<span class='pl-1' title='자동 설정'><span class='fa-regular fa-asterisk text-info fa-2xs'></span></span>" +
			"# } else { #" +
				"<span>#= kendo.format('{0:n0}', sysValue) #</span>" +
			"# } #";
	String goalTypeTemplate =
			"# if (purchType == 'H') { #" +
				"<span></span>" +
			"# } else if (goalType == 'A') { #" +
				"<span title='광고 예산' class='" + rowClass + "'><span class='fa-regular fa-sack-dollar fa-fw'></span><span class='pl-1'></span>" +
			"# } else if (goalType == 'I') { #" +
				"<span title='노출량' class='" + rowClass + "'><span class='fa-regular fa-eye fa-fw'></span><span class='pl-1'></span>" +
			"# } else if (goalType == 'U') { #" +
				"<span title='무제한 노출' class='" + rowClass + "'><span class='fa-regular fa-infinity fa-fw'></span><span class='pl-1'></span>" +
			"# } else { #" +
				"<span>-</span>" +
			"# } #";
	String impDailyTypeTemplate =
			"# if (impDailyType == 'E') { #" +
				"<span title='모든 날짜 균등'><span class='fa-regular fa-equals fa-fw'></span>" +
			"# } else if (impDailyType == 'W') { #" +
				"<span title='통계 기반 요일별 차등'><span class='fa-regular fa-bars-staggered fa-fw'></span>" +
			"# } else { #" +
				"<span>-</span>" +
			"# } #";
	String impHourlyTypeTemplate =
			"# if (impHourlyType == 'E') { #" +
				"<span title='모든 시간 균등'><span class='fa-regular fa-equals fa-fw'></span>" +
			"# } else if (impHourlyType == 'D') { #" +
				"<span title='일과 시간 집중'><span class='fa-regular fa-sun fa-fw'></span>" +
			"# } else { #" +
				"<span>-</span>" +
			"# } #";
	String dailyScrCapTemplate =
			"# if (proposedDailyScrCap > 0) { #" +
				"<span class=\"text-muted\">#= kendo.format('{0:n0}', proposedDailyScrCap) #</span>" +
				"<span class='pl-1' title='자동 설정'><span class='fa-regular fa-asterisk text-info fa-2xs'></span></span>" +
			"# } else if (dailyScrCap > 0) { #" +
				"<span class='" + rowClass + "'>#= kendo.format('{0:n0}', dailyScrCap) #</span>" +
			"# } else { #" +
				"<span></span>" +
			"# } #";
			
	String startDateTemplate = "<span class='" + rowClass + "'>" + kr.adnetwork.utils.Util.getSmartDate("startDate", false, false) + "</span>";
	String endDateTemplate = "<span class='" + rowClass + "'>" + kr.adnetwork.utils.Util.getSmartDate("endDate", false, false) + "</span>";
			
	String playDateTemplate = kr.adnetwork.utils.Util.getSmartDate2("playDate");
	
	String cntScreenTemplate = "<span class='" + rowClass + "'>#= kendo.format('{0:n0}', cntScreen) #</span>";
	String actualAmountTemplate = "<span class='" + rowClass + "'>#= kendo.format('{0:n0}', actualAmount) #</span>";
	String totalTemplate = "<span class='" + rowClass + "'>#= kendo.format('{0:n0}', total) #</span>";

	String actualCpmTemplate = "<span class='" + rowClass + "'>#= kendo.format('{0:n0}', actualCpm) #</span>";
	String achvRatioTemplate =
			"# if (totalRow && achvRatio == 0) { #" +
				"<span>-</span>" +
			"# } else if (purchType == 'H') { #" +
				"<span></span>" +
			"# } else { #" +
				"<span class='" + rowClass + "'>#= kendo.format('{0:n2}', achvRatio) #</span>" +
			"# } #";

	String tgtTodayTemplate =
			"# if (goalType) { #" +
				"# if (goalType == 'I') { #" +
					"<span title='노출량'><span class='fa-regular fa-eye fa-fw'></span><span class='pr-1'></span>" +
					"<span>#= kendo.format('{0:n0}', tgtToday) # 회</span>" + 
				"# } else if (goalType == 'A') { #" +
					"<span title='광고 예산'><span class='fa-regular fa-sack-dollar fa-fw'></span><span class='pr-1'></span>" +
					"<span>#= kendo.format('{0:n0}', tgtToday) # 원</span>" + 
				"# } #" +
			"# } else { #" +
				"<span></span>" + 
			"# } #";
	String dailyAchvRatioTemplate =
			"# if (goalType) { #" +
				"# if (achvRatio < 50) { #" +
					"<span title='위험'><span class='fa-light fa-siren-on text-red fa-lg fa-fw'></span><span class='pr-1'></span>" +
				"# } else if (achvRatio < 80) { #" +
					"<span title='경고'><span class='fa-light fa-siren text-orange fa-lg fa-fw'></span><span class='pr-1'></span>" +
				"# } else if (achvRatio < 90) { #" +
					"<span title='주의'><span class='fa-light fa-diamond-exclamation text-purple fa-lg fa-fw'></span><span class='pr-1'></span>" +
				"# } else if (achvRatio < 100) { #" +
					"<span title='보통'><span class='fa-light fa-circle-check text-success fa-lg fa-fw'></span><span class='pr-1'></span>" +
				"# } else if (achvRatio >= 100) { #" +
					"<span title='우수'><span class='fa-light fa-star text-blue fa-lg fa-fw'></span><span class='pr-1'></span>" +
				"# } #" +
				"<span>#= kendo.format('{0:n2}', achvRatio) # %</span>" + 
			"# } else { #" +
				"<span></span>" + 
			"# } #";

	String achvTemplate =
			"# if (goalType) { #" +
				"<span id='cht_#= dataId #' class='achv-bullet'></span>" +
			"# } else { #" +
				"<span></span>" + 
			"# } #";
	
	String cnt00Template = "<span class='" + rowClass + "'>#= kendo.format('{0:n0}', cnt00) #</span>";
	String cnt01Template = "<span class='" + rowClass + "'>#= kendo.format('{0:n0}', cnt01) #</span>";
	String cnt02Template = "<span class='" + rowClass + "'>#= kendo.format('{0:n0}', cnt02) #</span>";
	String cnt03Template = "<span class='" + rowClass + "'>#= kendo.format('{0:n0}', cnt03) #</span>";
	String cnt04Template = "<span class='" + rowClass + "'>#= kendo.format('{0:n0}', cnt04) #</span>";
	String cnt05Template = "<span class='" + rowClass + "'>#= kendo.format('{0:n0}', cnt05) #</span>";
	String cnt06Template = "<span class='" + rowClass + "'>#= kendo.format('{0:n0}', cnt06) #</span>";
	String cnt07Template = "<span class='" + rowClass + "'>#= kendo.format('{0:n0}', cnt07) #</span>";
	String cnt08Template = "<span class='" + rowClass + "'>#= kendo.format('{0:n0}', cnt08) #</span>";
	String cnt09Template = "<span class='" + rowClass + "'>#= kendo.format('{0:n0}', cnt09) #</span>";
	String cnt10Template = "<span class='" + rowClass + "'>#= kendo.format('{0:n0}', cnt10) #</span>";
	String cnt11Template = "<span class='" + rowClass + "'>#= kendo.format('{0:n0}', cnt11) #</span>";
	String cnt12Template = "<span class='" + rowClass + "'>#= kendo.format('{0:n0}', cnt12) #</span>";
	String cnt13Template = "<span class='" + rowClass + "'>#= kendo.format('{0:n0}', cnt13) #</span>";
	String cnt14Template = "<span class='" + rowClass + "'>#= kendo.format('{0:n0}', cnt14) #</span>";
	String cnt15Template = "<span class='" + rowClass + "'>#= kendo.format('{0:n0}', cnt15) #</span>";
	String cnt16Template = "<span class='" + rowClass + "'>#= kendo.format('{0:n0}', cnt16) #</span>";
	String cnt17Template = "<span class='" + rowClass + "'>#= kendo.format('{0:n0}', cnt17) #</span>";
	String cnt18Template = "<span class='" + rowClass + "'>#= kendo.format('{0:n0}', cnt18) #</span>";
	String cnt19Template = "<span class='" + rowClass + "'>#= kendo.format('{0:n0}', cnt19) #</span>";
	String cnt20Template = "<span class='" + rowClass + "'>#= kendo.format('{0:n0}', cnt20) #</span>";
	String cnt21Template = "<span class='" + rowClass + "'>#= kendo.format('{0:n0}', cnt21) #</span>";
	String cnt22Template = "<span class='" + rowClass + "'>#= kendo.format('{0:n0}', cnt22) #</span>";
	String cnt23Template = "<span class='" + rowClass + "'>#= kendo.format('{0:n0}', cnt23) #</span>";

	String screenNameTemplate =
			"# if (screen.deleted == true) { #" +
				"<span title='삭제 처리'><span class='fa-regular fa-trash-can text-danger fa-fw'></span></span>" +
				"<span class='pl-1'>#= screen.name #</span>" +
			"# } else { #" +
				"<div class='d-flex align-items-center'>" +
					"# if (screen.reqStatus == '6') { #" +
						"<span title='10분내 요청'><span class='fa-solid fa-flag-swallowtail fa-fw text-blue'></span></span>" +
					"# } else if (screen.reqStatus == '5') { #" +
						"<span title='1시간내 요청'><span class='fa-solid fa-flag-swallowtail fa-fw text-green'></span></span>" +
					"# } else if (screen.reqStatus == '4') { #" +
						"<span title='6시간내 요청'><span class='fa-solid fa-flag-swallowtail fa-fw text-yellow'></span></span>" +
					"# } else if (screen.reqStatus == '3') { #" +
						"<span title='24시간내 요청'><span class='fa-solid fa-flag-swallowtail fa-fw text-orange'></span></span>" +
					"# } else if (screen.reqStatus == '1') { #" +
						"<span title='24시간내 없음'><span class='fa-solid fa-flag-pennant fa-fw text-danger'></span></span>" +
					"# } else if (screen.reqStatus == '0') { #" +
						"<span title='기록 없음'><span class='fa-solid fa-flag-pennant fa-fw text-secondary'></span></span>" +
					"# } #" +
					"<span class='pl-1'>#= screen.name #</span>" +
					"<a href='javascript:showScreen(#= screen.id #,\"#= screen.name #\")' class='btn btn-default btn-xs icon-btn ml-1'><span class='fas fa-search text-info'></span></a>" +
				"</div>" +
			"# } #";
	
%>

<!-- Kendo grid  -->

<div class="mb-4">
<div class="card" style="border-bottom: transparent;">
	<h6 class="card-header with-elements py-2">
		<span class="lead">
			<span class="fa-light fa-audio-description fa-lg"></span>
			<span class="ml-1">광고별</span>
		</span>
		<div class="card-header-elements ml-auto">
			<button type='button' id="filter-btn" class='btn icon-btn btn-sm btn-outline-secondary' title='필터링'> 
				<span class='fa-light fa-filter fa-lg'></span>
			</button>
			<button type='button' id="cancel-filter-btn" class='btn icon-btn btn-sm btn-outline-secondary' style="display:none;" title="필터 해제"> 
				<span class='fa-light fa-filter-circle-xmark fa-lg'></span>
			</button>
			<span class="pr-2"></span>
			<button type='button' id="ad-excel-btn" class='btn icon-btn btn-sm btn-outline-secondary'> 
				<span class='fa-light fa-file-excel fa-lg'></span>
			</button>
			<button type='button' id="exp-col-btn" class='btn icon-btn btn-sm btn-outline-secondary'> 
				<span id="exp-col-btn-exp"><span class='fa-light fa-right-from-line fa-lg'></span></span>
				<span id="exp-col-btn-col" style="display: none;"><span class='fa-light fa-left-to-line fa-lg'></span></span>
			</button>
		</div>
	</h6>
</div>
<kendo:grid name="grid-ad" pageable="true" scrollable="true" filterable="false" sortable="false" resizable="true">
    <kendo:grid-selectable mode="multiple, row"/>
	<kendo:grid-pageable refresh="false" previousNext="false" numeric="false" pageSize="10000" info="false" />
	<kendo:grid-excel fileName="리포트(광고별).xlsx" allPages="true" proxyURL="/proxySave"/>
   	<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />
	<kendo:grid-columns>
		<kendo:grid-column title="광고명" field="name" width="300" template="<%= nameTemplate %>" filterable="false" sticky="true" />
		<kendo:grid-column title="상태" field="status" width="100" template="<%= statusTemplate %>" filterable="false" />
		<kendo:grid-column title="구매 유형" field="purchType" width="140" template="<%= purchTypeTemplate %>" filterable="false" />
		<kendo:grid-column title="예산" field="budget" width="120" template="<%= budgetTemplate %>" filterable="false" />
		<kendo:grid-column title="보장" field="goalValue" width="120" template="<%= goalValueTemplate %>" filterable="false" />
		<kendo:grid-column title="목표" field="sysValue" width="120" template="<%= sysValueTemplate %>" filterable="false" />
		<kendo:grid-column title="노출량" field="total" width="100" template="<%= totalTemplate %>" filterable="false" />
		<kendo:grid-column title="집행금액" field="actualAmount" width="120" template="<%= actualAmountTemplate %>" filterable="false" />
		<kendo:grid-column title="집행CPM" field="actualCpm" width="120" template="<%= actualCpmTemplate %>" filterable="false" />
		<kendo:grid-column title="달성률(%)" field="achvRatio" width="120" template="<%= achvRatioTemplate %>" filterable="false" />
		<kendo:grid-column title="집행방법" field="goalType" width="100" template="<%= goalTypeTemplate %>" filterable="false" />
		<kendo:grid-column title="일별 분산" field="impDailyType" width="100" template="<%= impDailyTypeTemplate %>" filterable="false" />
		<kendo:grid-column title="하루 분산" field="impHourlyType" width="100" template="<%= impHourlyTypeTemplate %>" filterable="false" />
		<kendo:grid-column title="화면하루한도" field="dailyScrCap" width="150" template="<%= dailyScrCapTemplate %>" filterable="false" />
		<kendo:grid-column title="시작일" field="startDate" width="100" template="<%= startDateTemplate %>" filterable="false" />
		<kendo:grid-column title="종료일" field="endDate" width="100" template="<%= endDateTemplate %>" filterable="false" />
		<kendo:grid-column title="화면수" field="cntScreen" width="100" template="<%= cntScreenTemplate %>" filterable="false" />
		<kendo:grid-column title="00" field="cnt00" width="80" template="<%= cnt00Template %>" filterable="false" hidden="true" />
		<kendo:grid-column title="01" field="cnt01" width="80" template="<%= cnt01Template %>" filterable="false" hidden="true" />
		<kendo:grid-column title="02" field="cnt02" width="80" template="<%= cnt02Template %>" filterable="false" hidden="true" />
		<kendo:grid-column title="03" field="cnt03" width="80" template="<%= cnt03Template %>" filterable="false" hidden="true" />
		<kendo:grid-column title="04" field="cnt04" width="80" template="<%= cnt04Template %>" filterable="false" hidden="true" />
		<kendo:grid-column title="05" field="cnt05" width="80" template="<%= cnt05Template %>" filterable="false" hidden="true" />
		<kendo:grid-column title="06" field="cnt06" width="80" template="<%= cnt06Template %>" filterable="false" hidden="true" />
		<kendo:grid-column title="07" field="cnt07" width="80" template="<%= cnt07Template %>" filterable="false" hidden="true" />
		<kendo:grid-column title="08" field="cnt08" width="80" template="<%= cnt08Template %>" filterable="false" hidden="true" />
		<kendo:grid-column title="09" field="cnt09" width="80" template="<%= cnt09Template %>" filterable="false" hidden="true" />
		<kendo:grid-column title="10" field="cnt10" width="80" template="<%= cnt10Template %>" filterable="false" hidden="true" />
		<kendo:grid-column title="11" field="cnt11" width="80" template="<%= cnt11Template %>" filterable="false" hidden="true" />
		<kendo:grid-column title="12" field="cnt12" width="80" template="<%= cnt12Template %>" filterable="false" hidden="true" />
		<kendo:grid-column title="13" field="cnt13" width="80" template="<%= cnt13Template %>" filterable="false" hidden="true" />
		<kendo:grid-column title="14" field="cnt14" width="80" template="<%= cnt14Template %>" filterable="false" hidden="true" />
		<kendo:grid-column title="15" field="cnt15" width="80" template="<%= cnt15Template %>" filterable="false" hidden="true" />
		<kendo:grid-column title="16" field="cnt16" width="80" template="<%= cnt16Template %>" filterable="false" hidden="true" />
		<kendo:grid-column title="17" field="cnt17" width="80" template="<%= cnt17Template %>" filterable="false" hidden="true" />
		<kendo:grid-column title="18" field="cnt18" width="80" template="<%= cnt18Template %>" filterable="false" hidden="true" />
		<kendo:grid-column title="19" field="cnt19" width="80" template="<%= cnt19Template %>" filterable="false" hidden="true" />
		<kendo:grid-column title="20" field="cnt20" width="80" template="<%= cnt20Template %>" filterable="false" hidden="true" />
		<kendo:grid-column title="21" field="cnt21" width="80" template="<%= cnt21Template %>" filterable="false" hidden="true" />
		<kendo:grid-column title="22" field="cnt22" width="80" template="<%= cnt22Template %>" filterable="false" hidden="true" />
		<kendo:grid-column title="23" field="cnt23" width="80" template="<%= cnt23Template %>" filterable="false" hidden="true" />
	</kendo:grid-columns>
	<kendo:grid-dataBound>
		<script>
			function grid_dataBound(e) {
				$('[data-toggle="tooltip"]').tooltip();
				
				oneAdId = -1;
		        var grid = this;
		        grid.table.find("tr").each(function () {
		            var dataItem = grid.dataItem(this);

		            if (dataItem.id && oneAdId != dataItem.id) {
		            	if (oneAdId == -1) {
		            		oneAdId = dataItem.id;
		            	} else {
		            		oneAdId = -1;
		            		return false;
		            	}
		            }
		        });

		        if (oneAdId > -1) {
		        	$("#grid-screen-daily-div").show();
		    		$("#grid-screen-daily").data("kendoGrid").dataSource.read();
		        } else {
		        	$("#grid-screen-daily-div").hide();
		        }
			}
		</script>
	</kendo:grid-dataBound>
	<kendo:grid-filterable>
		<kendo:grid-filterable-messages selectedItemsFormat="{0} 항목 선택됨"/>
	</kendo:grid-filterable>
	<kendo:dataSource serverPaging="false" serverSorting="false" serverFiltering="false" serverGrouping="false" error="kendoReadError">
		<kendo:dataSource-transport>
			<kendo:dataSource-transport-read url="${readUrl}" dataType="json" type="POST" contentType="application/json">
				<kendo:dataSource-transport-read-data>
					<script>
						function additionalData(e) {
							return { reqStrValue1: JSON.stringify(searchData), reqIntValue1:  ${Campaign.id} };
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
		<kendo:dataSource-schema>
			<kendo:dataSource-schema-model>
				<kendo:dataSource-schema-model-fields>
					<kendo:dataSource-schema-model-field name="startDate" type="date" />
					<kendo:dataSource-schema-model-field name="endDate" type="date" />
				</kendo:dataSource-schema-model-fields>
			</kendo:dataSource-schema-model>
		</kendo:dataSource-schema>
	</kendo:dataSource>
</kendo:grid>
</div>

<!-- / Kendo grid  -->


<!-- Kendo grid  -->

<div class="mb-4">
<div class="card" style="border-bottom: transparent;">
	<h6 class="card-header with-elements py-2">
			<span class="lead">
				<span class="fa-light fa-calendar-range fa-lg"></span>
				<span class="ml-1">일자별</span>
			</span>
			<div class="card-header-elements ml-auto">
				<button type='button' id="daily-excel-btn" class='btn icon-btn btn-sm btn-outline-secondary'> 
					<span class='fa-light fa-file-excel fa-lg'></span>
				</button>
			</div>
	</h6>
</div>
<kendo:grid name="grid-daily" pageable="true" scrollable="true" resizable="true">
	<kendo:grid-sortable mode="mixed" showIndexes="true"/>
    <kendo:grid-selectable mode="multiple, row"/>
	<kendo:grid-pageable refresh="false" buttonCount="5" pageSize="10" pageSizes="${pageSizesNormal}" />
	<kendo:grid-excel fileName="리포트(일자별).xlsx" allPages="true" proxyURL="/proxySave"/>
   	<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />
	<kendo:grid-columns>
		<kendo:grid-column title="날짜" field="playDate" width="120" template="<%= playDateTemplate %>" sticky="true" />
		<kendo:grid-column title="노출량" field="total" width="100" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="화면수" field="cntScreen" width="100" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="화면당" field="actualValuePerScreen" width="100" format="{0:n2}" filterable="false" />
		<kendo:grid-column title="하루 목표" field="tgtToday" width="120" template="<%= tgtTodayTemplate %>" filterable="false" />
		<kendo:grid-column title="목표 대비" field="achvRatio" width="120" template="<%= dailyAchvRatioTemplate %>" filterable="false" />
		<kendo:grid-column title="달성" width="150" template="<%= achvTemplate %>" filterable="false" />
		<kendo:grid-column title="집행금액" field="actualAmount" width="120" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="00" field="cnt00" width="80" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="01" field="cnt01" width="80" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="02" field="cnt02" width="80" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="03" field="cnt03" width="80" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="04" field="cnt04" width="80" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="05" field="cnt05" width="80" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="06" field="cnt06" width="80" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="07" field="cnt07" width="80" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="08" field="cnt08" width="80" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="09" field="cnt09" width="80" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="10" field="cnt10" width="80" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="11" field="cnt11" width="80" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="12" field="cnt12" width="80" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="13" field="cnt13" width="80" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="14" field="cnt14" width="80" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="15" field="cnt15" width="80" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="16" field="cnt16" width="80" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="17" field="cnt17" width="80" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="18" field="cnt18" width="80" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="19" field="cnt19" width="80" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="20" field="cnt20" width="80" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="21" field="cnt21" width="80" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="22" field="cnt22" width="80" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="23" field="cnt23" width="80" format="{0:n0}" filterable="false" />
	</kendo:grid-columns>
	<kendo:grid-dataBound>
		<script>
			function grid_dataBound(e) {
		        var grid = this;
		        var hasDailyAchvData = false;
		        grid.table.find("tr").each(function () {
		            var dataItem = grid.dataItem(this);
		            
	            	if (dataItem.goalType) {
	            		hasDailyAchvData = true;
	            		return false;
	            	}
		        });
				
		        if (!hasDailyAchvData) {
		        	grid.hideColumn(4);
		        	grid.hideColumn(5);
		        	grid.hideColumn(6);
		        	
		        	return;
		        }
		        
		        grid.showColumn(4);
		        grid.showColumn(5);
		        grid.showColumn(6);
		        
		        grid.table.find("tr").each(function () {
		            var dataItem = grid.dataItem(this);

		            $(this).find(".achv-bullet").kendoChart({
		        		chartArea: {
		        			background: "transparent",
		        			height: 30,
		        		},
		                series: [{
		                    type: "bullet",
		                    color: dataItem.colorCode,
		                    data: [[dataItem.achvRatio, 100]],
		                    target: {
		                        color: '#4e5155',
		                      }
		                }],
		                categoryAxis: {
		                    majorGridLines: {
		                        visible: false
		                    },
		                    majorTicks: {
		                        visible: false
		                    },
		                    visible: false,
		                },
		                valueAxis: [{
		                    plotBands: [
		                    	{ from: 0, to: 50, color: "black", opacity: .3 },
		                    	{ from: 50, to: 80, color: "black", opacity: .2 },
		                    	{ from: 80, to: 90, color: "black", opacity: .1 },
		                    	{ from: 90, to: 110, color: "black", opacity: .0 },
		                    	],
		                    majorGridLines: {
		                        visible: false
		                    },
		                    min: 0,
		                    max: 110,
		                    minorTicks: {
		                        visible: false
		                    },
		                    visible: false,
		                }],
		                tooltip: {
		                    visible: false,
		                }
		            });
		        });
			}
		</script>
	</kendo:grid-dataBound>
	<kendo:grid-filterable>
		<kendo:grid-filterable-messages selectedItemsFormat="{0} 항목 선택됨"/>
	</kendo:grid-filterable>
	<kendo:dataSource serverPaging="false" serverSorting="false" serverFiltering="false" serverGrouping="false" error="kendoReadError">
		<kendo:dataSource-sort>

	   		<c:choose>
			<c:when test="${Campaign.status == 'R'}">
				<kendo:dataSource-sortItem field="playDate" dir="desc"/>
	   		</c:when>
   			<c:otherwise>
				<kendo:dataSource-sortItem field="playDate" dir="asc"/>
	   		</c:otherwise>
   			</c:choose>
   			
		</kendo:dataSource-sort>
		<kendo:dataSource-transport>
			<kendo:dataSource-transport-read url="${readDailyUrl}" dataType="json" type="POST" contentType="application/json">
				<kendo:dataSource-transport-read-data>
					<script>
						function additionalData(e) {
							return { reqStrValue1: JSON.stringify(searchData), reqIntValue1:  ${Campaign.id} };
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
		<kendo:dataSource-schema>
			<kendo:dataSource-schema-model id="playDate">
				<kendo:dataSource-schema-model-fields>
					<kendo:dataSource-schema-model-field name="playDate" type="date" />
				</kendo:dataSource-schema-model-fields>
			</kendo:dataSource-schema-model>
		</kendo:dataSource-schema>
	</kendo:dataSource>
</kendo:grid>
</div>

<!-- / Kendo grid  -->


<!-- Chart area -->

<div class="mb-4">
<div class="card" style="border-bottom: transparent;">
	<h6 class="card-header with-elements py-2">
		<span class="lead">
			<span class="fa-light fa-chart-line fa-lg"></span>
			<span class="ml-1">노출량 차트</span>
		</span>
		<div class="card-header-elements ml-auto">
		
			<div class="btn-group btn-group-toggle" data-toggle="buttons">
				<label class="btn btn-outline-secondary btn-sm active">
					<input type="radio" name="chart-type-radio" value="L" checked>
						<span class='fa-light fa-chart-line fa-lg'></span>
					</input>
				</label>
				<label class="btn btn-outline-secondary btn-sm">
					<input type="radio" name="chart-type-radio" value="C">
						<span class='fa-light fa-chart-column fa-lg'></span>
					</input>
				</label>
			</div>
		
			<div class="px-1"></div>
			
			<div class="btn-group btn-group-toggle" data-toggle="buttons">
				<label class="btn btn-outline-secondary btn-sm active">
					<input type="radio" name="chart-interval-radio" value="D" checked>
						날짜
					</input>
				</label>
				<label class="btn btn-outline-secondary btn-sm">
					<input type="radio" name="chart-interval-radio" value="H">
						시간
					</input>
				</label>
			</div>
		</div>
	</h6>
	<div class="m-3">
		<div id="impression-chart-control" style="height: 250px;">
		</div>
	</div>
</div>
</div>

<script>

var chart = null;
var chartData = [];

$(document).ready(function() {
	
	window.dispatchEvent(new Event('resize'));

	
	// 자료 간격 단위
	$("input[name='chart-interval-radio']").change(function(){
		
		readChartData();

	});
	
	// 차트 유형
	$("input[name='chart-type-radio']").change(function(){
		
		refreshChart();

	});
	
});

window.addEventListener('resize', function(event){
	
	if (chart == null) {
	    
		createChart();
		
	    chart = $("#impression-chart-control").data("kendoChart");
	}
	
	if (chart != null) {
		chart.dataSource.data(chartData);
	}
	
});


function refreshChart() {
	
	if (chart != null) {
		
		var typeVal = $("input[name='chart-type-radio']:checked").val();
		var intervalVal = $("input[name='chart-interval-radio']:checked").val();
		
		var stepVal = 7, skipVal = 3;
		
		if (typeVal == 'C') {
			chart.options.series[0].type = "column";
		} else {
			chart.options.series[0].type = "line";
		}
		
		if (intervalVal == 'H') {
			stepVal = 72;
			skipVal = 12;
		}
		
		chart.options.categoryAxis.majorTicks.step = stepVal;
		chart.options.categoryAxis.majorTicks.skip = skipVal;
		chart.options.categoryAxis.labels.step = stepVal;
		chart.options.categoryAxis.labels.skip = skipVal;
		
		chart.refresh();
	}
}


function readChartData() {
	
	var interval = $("input[name='chart-interval-radio']:checked").val();
	var searchDataFinal = null;
	if (searchData) {
		searchDataFinal = JSON.stringify(searchData);
	}

	$.ajax({
		type: "POST",
		contentType: "application/json",
		dataType: "json",
		url: "${readChartUrl}/" + interval,
		data: JSON.stringify({ reqStrValue1: searchDataFinal, reqIntValue1:  ${Campaign.id} }),
		success: function (data, status) {
			chartData = data;
			
			chart.dataSource.data(chartData);
			
			refreshChart();
		},
		error: ajaxOperationError
	});
}


function toShortenQualifier(value) {

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
	}
	
	return value;
}

function createChart() {
	
    $("#impression-chart-control").kendoChart({
    	
        legend: {
            visible: true
        },
        dataSource: {
            data: chartData
        },
        series: [{
            type: "line",
            style: "smooth",
            markers: {
                visible: true
            },
            field: "value",
            categoryField: "playTimeDisp",
            color: "#487df2",
            markers: {
				size: 5
			},
			tooltip: {
				background: "#eceff1",
            	font: "13px 'Roboto', 'Noto Sans', 'Noto Sans CJK KR', -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Oxygen', 'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue'",
			}
        }],
        categoryAxis: {
            majorGridLines: {
                visible: false
            },
            majorTicks: {
                visible: true,
            	step: 7,
            	skip: 3
            },
            labels: {
            	step: 7,
            	skip: 3
            }
        },
        valueAxis: [{
			labels: {
				template: "#= toShortenQualifier(value) #"
			}
		}],
        tooltip: {
            visible: true,
            template: "#= dataItem.playTimeDisp # - #= kendo.format('{0:n0}', value) # 회",
            border: {
            	width: 1,
            	color: "#90a4ae"
            }
        }
        
    });
}

</script>

<!-- / Chart area -->


<!-- Kendo grid  -->

<div class="mb-4">
<div class="card" style="border-bottom: transparent;">
	<h6 class="card-header with-elements py-2">
			<span class="lead">
				<span class="fa-light fa-calendar-week fa-lg"></span>
				<span class="ml-1">요일별</span>
			</span>
			<div class="card-header-elements ml-auto">
				<button type='button' id="week-daily-excel-btn" class='btn icon-btn btn-sm btn-outline-secondary'> 
					<span class='fa-light fa-file-excel fa-lg'></span>
				</button>
			</div>
	</h6>
</div>
<kendo:grid name="grid-week-daily" pageable="true" scrollable="true" resizable="true">
	<kendo:grid-sortable mode="mixed" showIndexes="true"/>
    <kendo:grid-selectable mode="multiple, row"/>
	<kendo:grid-pageable refresh="false" previousNext="false" numeric="false" pageSize="10000" info="false" />
	<kendo:grid-excel fileName="리포트(요일별).xlsx" allPages="true" proxyURL="/proxySave"/>
   	<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />
	<kendo:grid-columns>
		<kendo:grid-column title="요일" field="weekDay" width="120" template="#= weekDayName #" sticky="true" />
		<kendo:grid-column title="노출량" field="total" width="100" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="화면수" field="cntScreen" width="100" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="집행금액" field="actualAmount" width="120" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="00" field="cnt00" width="80" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="01" field="cnt01" width="80" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="02" field="cnt02" width="80" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="03" field="cnt03" width="80" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="04" field="cnt04" width="80" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="05" field="cnt05" width="80" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="06" field="cnt06" width="80" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="07" field="cnt07" width="80" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="08" field="cnt08" width="80" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="09" field="cnt09" width="80" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="10" field="cnt10" width="80" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="11" field="cnt11" width="80" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="12" field="cnt12" width="80" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="13" field="cnt13" width="80" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="14" field="cnt14" width="80" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="15" field="cnt15" width="80" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="16" field="cnt16" width="80" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="17" field="cnt17" width="80" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="18" field="cnt18" width="80" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="19" field="cnt19" width="80" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="20" field="cnt20" width="80" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="21" field="cnt21" width="80" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="22" field="cnt22" width="80" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="23" field="cnt23" width="80" format="{0:n0}" filterable="false" />
	</kendo:grid-columns>
	<kendo:grid-filterable>
		<kendo:grid-filterable-messages selectedItemsFormat="{0} 항목 선택됨"/>
	</kendo:grid-filterable>
	<kendo:dataSource serverPaging="false" serverSorting="false" serverFiltering="false" serverGrouping="false" error="kendoReadError">
		<kendo:dataSource-transport>
			<kendo:dataSource-transport-read url="${readWeekDailyUrl}" dataType="json" type="POST" contentType="application/json">
				<kendo:dataSource-transport-read-data>
					<script>
						function additionalData(e) {
							return { reqStrValue1: JSON.stringify(searchData), reqIntValue1:  ${Campaign.id} };
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
		<kendo:dataSource-schema>
			<kendo:dataSource-schema-model id="weekDay">
				<kendo:dataSource-schema-model-fields>
				</kendo:dataSource-schema-model-fields>
			</kendo:dataSource-schema-model>
		</kendo:dataSource-schema>
	</kendo:dataSource>
</kendo:grid>
</div>

<!-- / Kendo grid  -->



<!-- Kendo grid  -->

<div class="mb-4" id="grid-screen-daily-div" style="display:none;">
<div class="card" style="border-bottom: transparent;">
	<h6 class="card-header with-elements py-2">
		<span class="lead">
			<span class="fa-light fa-clapperboard-play fa-lg"></span>
			<span class="ml-1">개별 화면 / 일자별 / 광고 소재별</span>
		</span>
		<div class="card-header-elements ml-auto">
			<button type='button' id="screen-daily-excel-btn" class='btn icon-btn btn-sm btn-outline-secondary'> 
				<span class='fa-light fa-file-excel fa-lg'></span>
			</button>
		</div>
	</h6>
</div>
<kendo:grid name="grid-screen-daily" pageable="true" scrollable="true" filterable="true" resizable="true">
	<kendo:grid-sortable mode="mixed" showIndexes="true"/>
    <kendo:grid-selectable mode="multiple, row"/>
	<kendo:grid-excel fileName="리포트(개별화면일자별).xlsx" allPages="true" proxyURL="/proxySave"/>
	<kendo:grid-pageable refresh="true" buttonCount="5" pageSize="10" pageSizes="${pageSizesNormal}" />
	<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />

	<kendo:grid-columns>
		<kendo:grid-column title="화면" field="screen.name" width="250" template="<%= screenNameTemplate %>" sticky="true" />
		<kendo:grid-column title="날짜" field="playDate" width="120" template="<%= playDateTemplate %>" />
		<kendo:grid-column title="합계" field="dateTotal" width="100" />
		<kendo:grid-column title="성공" field="succTotal" width="100" />
		<kendo:grid-column title="00" field="cnt00" width="80" filterable="false" />
		<kendo:grid-column title="01" field="cnt01" width="80" filterable="false" />
		<kendo:grid-column title="02" field="cnt02" width="80" filterable="false" />
		<kendo:grid-column title="03" field="cnt03" width="80" filterable="false" />
		<kendo:grid-column title="04" field="cnt04" width="80" filterable="false" />
		<kendo:grid-column title="05" field="cnt05" width="80" filterable="false" />
		<kendo:grid-column title="06" field="cnt06" width="80" filterable="false" />
		<kendo:grid-column title="07" field="cnt07" width="80" filterable="false" />
		<kendo:grid-column title="08" field="cnt08" width="80" filterable="false" />
		<kendo:grid-column title="09" field="cnt09" width="80" filterable="false" />
		<kendo:grid-column title="10" field="cnt10" width="80" filterable="false" />
		<kendo:grid-column title="11" field="cnt11" width="80" filterable="false" />
		<kendo:grid-column title="12" field="cnt12" width="80" filterable="false" />
		<kendo:grid-column title="13" field="cnt13" width="80" filterable="false" />
		<kendo:grid-column title="14" field="cnt14" width="80" filterable="false" />
		<kendo:grid-column title="15" field="cnt15" width="80" filterable="false" />
		<kendo:grid-column title="16" field="cnt16" width="80" filterable="false" />
		<kendo:grid-column title="17" field="cnt17" width="80" filterable="false" />
		<kendo:grid-column title="18" field="cnt18" width="80" filterable="false" />
		<kendo:grid-column title="19" field="cnt19" width="80" filterable="false" />
		<kendo:grid-column title="20" field="cnt20" width="80" filterable="false" />
		<kendo:grid-column title="21" field="cnt21" width="80" filterable="false" />
		<kendo:grid-column title="22" field="cnt22" width="80" filterable="false" />
		<kendo:grid-column title="23" field="cnt23" width="80" filterable="false" />
		<kendo:grid-column title="실패" field="failTotal" width="100" />
		<kendo:grid-column title="시간목표" field="currHourGoal" width="100" filterable="false" />
		<kendo:grid-column title="광고 소재" field="adCreative.creative.name" width="250" filterable="false" sortable="false" />
	</kendo:grid-columns>
	<kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="playDate" dir="desc"/>
		</kendo:dataSource-sort>
		<kendo:dataSource-transport>
			<kendo:dataSource-transport-read url="${readScreenDailyUrl}" dataType="json" type="POST" contentType="application/json">
				<kendo:dataSource-transport-read-data>
					<script>
						function additionalData(e) {
							return { reqStrValue1: JSON.stringify(searchData), reqIntValue1: ${Campaign.id}, reqIntValue2: oneAdId };
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
					<kendo:dataSource-schema-model-field name="playDate" type="date" />
					<kendo:dataSource-schema-model-field name="dateTotal" type="number" />
					<kendo:dataSource-schema-model-field name="succTotal" type="number" />
					<kendo:dataSource-schema-model-field name="failTotal" type="number" />
				</kendo:dataSource-schema-model-fields>
			</kendo:dataSource-schema-model>
		</kendo:dataSource-schema>
	</kendo:dataSource>
</kendo:grid>
</div>


<!-- Grid button actions  -->

<script>
$(document).ready(function() {

	// 광고 grid 확장 여부
	var gridExpanded = false;
	
	
	// Excel(광고별 grid)
	$("#ad-excel-btn").click(function(e) {
		e.preventDefault();
		
		$("#grid-ad").data("kendoGrid").saveAsExcel();
	});
	// / Excel(일자별 grid)

	
	// Excel(일자별 grid)
	$("#daily-excel-btn").click(function(e) {
		e.preventDefault();
		
		$("#grid-daily").data("kendoGrid").saveAsExcel();
	});
	// / Excel(일자별 grid)

	
	// Excel(요일별 grid)
	$("#week-daily-excel-btn").click(function(e) {
		e.preventDefault();
		
		$("#grid-week-daily").data("kendoGrid").saveAsExcel();
	});
	// / Excel(일자별 grid)

	
	// Excel(개별 화면 / 일자별 / 광고 소재별 grid)
	$("#screen-daily-excel-btn").click(function(e) {
		e.preventDefault();
		
		$("#grid-screen-daily").data("kendoGrid").saveAsExcel();
	});
	// / Excel(개별 화면 / 일자별 / 광고 소재별 grid)

	
	// Filter
	$("#filter-btn").click(function(e) {
		e.preventDefault();
		
		
		initForm1();

		
		$('#form-modal-1 .modal-dialog').draggable({ handle: '.modal-header' });
		$("#form-modal-1").modal();
	});
	// / Filter

	
	// Cancel Filtering
	$("#cancel-filter-btn").click(function(e) {
		e.preventDefault();
		
		cancelFiltering();
	});
	// / Cancel Filtering

	
	// 그리드 확장/축소
	$("#exp-col-btn").click(function(e) {
		e.preventDefault();
		
		var grid = $("#grid-ad").data("kendoGrid");
		var startIdx = 17
		
		if (gridExpanded) {
			$("#exp-col-btn-exp").show();
			$("#exp-col-btn-col").hide();

			for(var i = startIdx; i < startIdx + 24; i ++) {
				grid.hideColumn(i);
			}
		} else {
			$("#exp-col-btn-exp").hide();
			$("#exp-col-btn-col").show();

			for(var i = startIdx; i < startIdx + 24; i ++) {
				grid.showColumn(i);
			}
		}
		
		gridExpanded = !gridExpanded;
	});
	// / 그리드 확장/축소
	
});	
</script>

<!-- / Grid button actions  -->


<!--  Forms -->

<script id="template-1" type="text/x-kendo-template">

<div class="modal fade" data-backdrop="static" id="form-modal-1">
	<div class="modal-dialog modal-sm">
		<form class="modal-content" id="form-1" rowid="-1" url="${createUrl}">
      
			<!-- Modal Header -->
			<div class="modal-header move-cursor">
				<h5 class="modal-title">
					필터링
					<span class="font-weight-light pl-1"><span name="subtitle">광고</span>
				</h5>
				<button type="button" class="close" data-dismiss="modal">×</button>
			</div>
        
			<!-- Modal body -->
			<div class="modal-body modal-bg-color">
				<div class="form-row">
					<div class="form-group col">
						<label class="form-label">
							광고
						</label>
						<select name="ad" class="selectpicker bg-white required" data-style="btn-default" 
								data-none-selected-text=""
								data-count-selected-text="{0} 개의 광고 항목이 선택됨"
								data-selected-text-format="count" title="대상 광고를 선택하십시오"
								data-actions-box="true"
								data-select-all-text="전체 선택"
								data-deselect-all-text="전체 선택 해제"
								multiple>
<c:forEach var="item" items="${Ads}">
	<c:choose>
	<c:when test="${item.icon eq 'selected'}">
							<option value="${item.value}" selected>${item.text}</option>
	</c:when>
	<c:otherwise>
							<option value="${item.value}">${item.text}</option>
	</c:otherwise>
	</c:choose>
</c:forEach>
						</select>
					</div>
				</div>
				<div class="form-row">
					<div class="form-group col-6">
						<label class="form-label">
							시작일
						</label>
						<input name="startDate" type="text" class="form-control required">
					</div>
					<div class="form-group col-6">
						<label class="form-label">
							종료일
						</label>
						<input name="endDate" type="text" class="form-control required">
					</div>
				</div>
			</div>
        
			<!-- Modal footer -->
			<div class="modal-footer d-flex">
				<button type="button" class="btn btn-default" data-dismiss="modal">취소</button>
				<button type="button" class="btn btn-primary" onclick='searchForm()'>검색</button>
			</div>
			
		</form>
	</div>
</div>

</script>


<style>







/* 그리드 행의 높이 지정 */
.k-grid tbody tr, .k-grid tbody tr td
{
    height: 40px;
}


/* 그리드 자료 새로고침 버튼을 우측 정렬  */
div.k-pager-wrap.k-grid-pager.k-widget.k-floatwrap {
	display: flex!important;
	justify-content: flex-end!important;
}


/* 합계 행 스타일 */
.accent {
	font-weight: 500;
}


/* 주요 그리드의 최대 높이 제한 */
#grid-ad .k-grid-content {
	max-height: 220px;
}

</style>


<!--  / Forms -->


<!--  Scripts -->

<script>

var searchData = null;
var searchIds = null;
var searchSDate = null, searchEDate = null;


function to_date(dateStr) {
	
    var yyyyMMdd = String(dateStr);
    
    var sYear = yyyyMMdd.substring(0, 4);
    var sMonth = yyyyMMdd.substring(5, 7);
    var sDate = yyyyMMdd.substring(8, 10);

    return new Date(Number(sYear), Number(sMonth)-1, Number(sDate));
}


function date_format(date) {

	var yyyy = date.getFullYear().toString();
	var mm = (date.getMonth()+1).toString();
	var dd  = date.getDate().toString();
	
	var mmChars = mm.split('');
	var ddChars = dd.split('');
	
	return yyyy + '-' + (mmChars[1] ? mm : "0"+mmChars[0]) + '-' + (ddChars[1] ? dd : "0"+ddChars[0]);
}
	
	
function initForm1(subtitle) {
	
	$("#formRoot").html(kendo.template($("#template-1").html()));

	$("#form-1 select[name='ad']").selectpicker('render');
	

	$("#form-1 input[name='startDate']").kendoDatePicker({
		format: "yyyy-MM-dd",
		parseFormats: [
			"yyyy-MM-dd",
		],
		min: to_date("${filterMinDate}"),
		max: to_date("${filterMaxDate}"),
	});

	$("#form-1 input[name='endDate']").kendoDatePicker({
		format: "yyyy-MM-dd",
		parseFormats: [
			"yyyy-MM-dd",
		],
		min: to_date("${filterMinDate}"),
		max: to_date("${filterMaxDate}"),
	});
	
	
	if (searchData == null) {
		$("#form-1 input[name='startDate']").data("kendoDatePicker").value(to_date("${filterMinDate}"));
		$("#form-1 input[name='endDate']").data("kendoDatePicker").value(to_date("${filterMaxDate}"));
	} else {
		
		$("#form-1 select[name='ad'] option").each(function() {
			$(this).prop("selected", ("|" + searchIds + "|").indexOf("|" + $(this).val() + "|") != -1);
		});
		$("#form-1 select[name='ad']").selectpicker("refresh");
		
		$("#form-1 input[name='startDate']").data("kendoDatePicker").value(searchSDate);
		$("#form-1 input[name='endDate']").data("kendoDatePicker").value(searchEDate);
	}

	
	$("#form-1").validate({
		rules: {
			startDate: { date: true },
			endDate: { date: true },
		}
	});
}


function searchForm() {

	// kendo datepicker validation
	validateKendoDateValue($("#form-1 input[name='startDate']"));
	validateKendoDateValue($("#form-1 input[name='endDate']"));
	
	if ($("#form-1").valid()) {
		
		searchSDate = $("#form-1 input[name='startDate']").data("kendoDatePicker").value();
		searchEDate = $("#form-1 input[name='endDate']").data("kendoDatePicker").value();
		
		var items = eval($("#form-1 select[name='ad']").val());
		
		searchIds = "";
		items.forEach(function (item, index) {
			if (searchIds) {
				searchIds += "|";
			}
			searchIds += item;
		});
		
		var data = {
			ids: searchIds,
			startDate: searchSDate,
			endDate: searchEDate,
		};
		
		
		if (date_format(searchSDate) == date_format(to_date("${filterMinDate}")) &&
				date_format(searchEDate) == date_format(to_date("${filterMaxDate}")) &&
				items.length == ${AdsSize}) {
			
			searchData = null;
			
			searchIds = null;
			searchSDate = null;
			searchEDate = null;
		} else {
			searchData = data;
		}

		if (searchData == null) {
			$("#filter-btn").addClass("btn-outline-secondary").removeClass("btn-primary");
			$("#cancel-filter-btn").hide();
		} else {
			$("#filter-btn").addClass("btn-primary").removeClass("btn-outline-secondary");
			$("#cancel-filter-btn").show();
		}
		
		
		$("#form-modal-1").modal("hide");
		
		$("#grid-ad").data("kendoGrid").dataSource.read();
		$("#grid-daily").data("kendoGrid").dataSource.read();
		$("#grid-week-daily").data("kendoGrid").dataSource.read();
		
		readChartData();
	}
}


function navToAd(campId, adId) {
	var path = "/adc/campaign/detail/${Campaign.id}/" + adId;
	location.href = path;
}


function filterAd(adId) {
	var path = "/rev/camprpt/${Campaign.id}?filter=" + adId;
	location.href = path;
}


function cancelFiltering() {
	
	searchData = null;
	
	searchIds = null;
	searchSDate = null;
	searchEDate = null;
	
	$("#filter-btn").addClass("btn-outline-secondary").removeClass("btn-primary");
	$("#cancel-filter-btn").hide();
	
	$("#grid-ad").data("kendoGrid").dataSource.read();
	$("#grid-daily").data("kendoGrid").dataSource.read();
	$("#grid-week-daily").data("kendoGrid").dataSource.read();
	
	$("#grid-screen-daily-div").hide();
	
	readChartData();
}

</script>


<script>

var oneAdId = -1;

$(document).ready(function() {

<c:choose>
<c:when test="${empty filterId}">

	readChartData();
	
</c:when>
<c:otherwise>

	searchSDate = to_date("${filterSDate}");
	searchEDate = to_date("${filterEDate}");
	searchIds = "${filterId}";
	
	var data = {
		ids: searchIds,
		startDate: searchSDate,
		endDate: searchEDate,
	};
	
	
	if (date_format(searchSDate) == date_format(to_date("${filterMinDate}")) &&
			date_format(searchEDate) == date_format(to_date("${filterMaxDate}")) &&
			1 == ${AdsSize}) {
		
		searchData = null;
		
		searchIds = null;
		searchSDate = null;
		searchEDate = null;
		
		$("#cancel-filter-btn").hide();
		
		readChartData();
	} else {
		searchData = data;
	
		$("#filter-btn").addClass("btn-primary").removeClass("btn-outline-secondary");
		$("#cancel-filter-btn").show();
		
		$("#grid-ad").data("kendoGrid").dataSource.read();
		$("#grid-daily").data("kendoGrid").dataSource.read();
		$("#grid-week-daily").data("kendoGrid").dataSource.read();
		
		readChartData();
	}

</c:otherwise>
</c:choose>
	
});	
</script>

<!--  / Scripts -->


<!-- / Page body -->





<!-- Functional tags -->

<func:screenInfoModal />
<func:cmmValidate />


<!-- Closing tags -->

<common:base />
<common:pageClosing />
