<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/rev/monitoring/readScr" var="readScrUrl" />
<c:url value="/rev/monitoring/readSyncPack" var="readSyncPackUrl" />
<c:url value="/rev/monitoring/readApiLog" var="readApiLogUrl" />
<c:url value="/rev/monitoring/readChanAd" var="readChanAdUrl" />
<c:url value="/rev/monitoring/readChanAdRpt" var="readChanAdRptUrl" />
<c:url value="/rev/monitoring/readEvent" var="readEventUrl" />

<c:url value="/rev/monitoring/destroyEvent" var="destroyEventUrl" />
<c:url value="/rev/monitoring/processCmd" var="processCmdUrl" />
<c:url value="/rev/monitoring/processSyncCmd" var="processSyncCmdUrl" />

<c:url value="/rev/monitoring/readResolutions" var="readResolutionUrl" />
<c:url value="/rev/monitoring/readStatuses" var="readStatusUrl" />
<c:url value="/rev/monitoring/readEventCats" var="readEventCatUrl" />
<c:url value="/rev/monitoring/readReportTypes" var="readReportTypeUrl" />
<c:url value="/rev/monitoring/readEquipTypes" var="readEquipTypeUrl" />
<c:url value="/rev/monitoring/readTriggerTypes" var="readTriggerTypeUrl" />

<c:url value="/rev/monitoring/readMonitStat" var="readMonitStatUrl" />
<c:url value="/rev/monitoring/readSyncPackStat" var="readSyncPackStatUrl" />

<c:url value="/rev/monitoring/impress" var="impressUrl" />
<c:url value="/rev/monitoring/destroyPlTime" var="destoryPlTimeUrl" />


<!-- Opening tags -->

<common:mediumPageOpeningNaverMap />


<!-- Page title -->

<h4 class="pt-1 pb-3 mb-3">
	<span class="mr-1 ${sessionScope['loginUser'].icon}"></span>
	${pageTitle}
</h4>




<!-- Page body -->


<!-- Page scripts  -->

<link rel="stylesheet" href="/resources/vendor/lib/bootstrap-slider/bootstrap-slider.css">

<script>$.fn.slider = null</script>
<script type="text/javascript" src="/resources/vendor/lib/bootstrap-slider/bootstrap-slider.js"></script>


<!-- Java(optional)  -->

<%
	String nameTemplate =
			"<div class='d-flex align-items-center'>" +
				"# if (reqStatus == '6') { #" +
					"<span title='10분내 요청'><span class='fa-solid fa-flag-swallowtail text-blue'></span></span>" +
				"# } else if (reqStatus == '5') { #" +
					"<span title='1시간내 요청'><span class='fa-solid fa-flag-swallowtail text-green'></span></span>" +
				"# } else if (reqStatus == '4') { #" +
					"<span title='6시간내 요청'><span class='fa-solid fa-flag-swallowtail text-yellow'></span></span>" +
				"# } else if (reqStatus == '3') { #" +
					"<span title='24시간내 요청'><span class='fa-solid fa-flag-swallowtail text-orange'></span></span>" +
				"# } else if (reqStatus == '1') { #" +
					"<span title='24시간내 없음'><span class='fa-solid fa-flag-pennant text-danger'></span></span>" +
				"# } else if (reqStatus == '0') { #" +
					"<span title='기록 없음'><span class='fa-solid fa-flag-pennant text-secondary'></span></span>" +
				"# } #" +
				"<span class='pl-1'>#= name #</span>" +
				"<a href='javascript:showScreen(#= id #,\"#= name #\")' class='btn btn-default btn-xs icon-btn ml-1'><span class='fas fa-search text-info'></span></a>" +
				"# if (gpsTime) { #" +
					"# if (isToday(gpsTime)) { #" +
						"<a href='javascript:showScrLoc(#= id #,\"#= name #\")' class='btn btn-default btn-xs icon-btn ml-1'><span class='fa-solid fa-location-arrow text-blue'></span></a>" +
					"# } else { #" +
						"<a href='javascript:showScrLoc(#= id #,\"#= name #\")' class='btn btn-default btn-xs icon-btn ml-1'><span class='fa-light fa-location-arrow'></span></a>" +
					"# } #" +
				"# } #" +
				"<a href='javascript:goSelLogTab(\"#= shortName #\")' class='btn btn-default btn-xs icon-btn ml-1'><span class='fa-regular fa-arrow-right'></span></a>" +
			"</div>";
			
	String apiTemplate = 
			"<a href='" + request.getAttribute("apiTestServer") + "/v1/files/#= shortName #?apikey=" + request.getAttribute("mediumApiKey") + "&test=y' class='badge badge-pill badge-warning mr-1' target='_blank'><span class='pr-1'>file</span><span class='fa-light fa-arrow-up-right-from-square'></span></a>" +
			"<a href='" + request.getAttribute("apiTestServer") + "/v1/ad/#= shortName #?apikey=" + request.getAttribute("mediumApiKey") + "&test=y' class='badge badge-pill badge-warning mr-1' target='_blank'><span class='pr-1'>ad</span><span class='fa-light fa-arrow-up-right-from-square'></span></a>" +
			"<a href='" + request.getAttribute("apiTestServer") + "/v1/chan/#= shortName #?apikey=" + request.getAttribute("mediumApiKey") + "&test=y' class='badge badge-pill badge-warning mr-1' target='_blank'><span class='pr-1'>chan</span><span class='fa-light fa-arrow-up-right-from-square'></span></a>";
	
	String fileDateTemplate = net.doohad.utils.Util.getSmartDate("lastFileDate");
	String adRequestDateTemplate = net.doohad.utils.Util.getSmartDate("lastAdRequestDate");
	String adReportDateTemplate = net.doohad.utils.Util.getSmartDate("lastAdReportDate");
	String infoDateTemplate = net.doohad.utils.Util.getSmartDate("lastInfoDate");
	String commandDateTemplate = net.doohad.utils.Util.getSmartDate("lastCommandDate");
	String commandReportDateTemplate = net.doohad.utils.Util.getSmartDate("lastCommandReportDate");
	String eventDateTemplate = net.doohad.utils.Util.getSmartDate("lastEventDate");
	
	String playerVerTemplate = "<small>#= playerVer #</small>";
	String keeperVerTemplate = "<small>#= keeperVer #</small>";
	String nextCmdTemplate =
			"# if (nextCmd) { #" +
				"<span class='badge badge-outline-#= cmdFailed ? 'danger' : 'secondary' #'><span>#= getFriendlyCmdName(nextCmd) #</span></span>" +
			"# } else { #" +
				"<span></span>" +
			"# } #";
	
	String noRecordsTemplate =
			"<div class='container text-center my-4'>" +
				"<div class='d-flex justify-content-center align-self-center'>" +
					"<span class='fa-thin fa-hexagon-exclamation fa-3x'></span>" +
					"<span class='pl-3 mt-2' style='font-weight: 300; font-size: 1.3rem;'>해당 자료 없음</span>" +
				"</div>" +
			"</div>";
			

	String selectAdDateTemplate = net.doohad.utils.Util.getSmartDate("selectDate");
	String playBeginDateTemplate = net.doohad.utils.Util.getSmartDate("beginDate");
	String playEndDateTemplate = net.doohad.utils.Util.getSmartDate("endDate");
	String reportDateTemplate = net.doohad.utils.Util.getSmartDate("reportDate");
	
	String resultTemplate =
			"# if (result && delayReported) { #" +
				"<span class='fa-light fa-flag-checkered text-danger'></span>" +
			"# } else if (result || adName == null) { #" +
				"<span class='fa-light fa-flag-checkered'></span>" +
			"# } else { #" +
				"<span></span>" +
			"# } #";
	
	String adNameTemplate = 
			"# if (adName) { #" + 
				"<div>" + 
					"# if (purchType == 'G') { #" +
						"<span title='목표 보장' class='pr-1'><span class='fa-regular fa-hexagon-check text-blue fa-fw'></span></span>" +
					"# } else if (purchType == 'N') { #" +
						"<span title='목표 비보장' class='pr-1'><span class='fa-regular fa-hexagon-exclamation fa-fw'></span></span>" +
					"# } else if (purchType == 'H') { #" +
						"<span title='하우스 광고' class='pr-1'><span class='fa-regular fa-house fa-fw'></span></span>" +
					"# } #" +
					"<span>#= adName #</span>" + 
				"</div>" +
			"# } else { #" +
				"<span class='badge badge-outline-secondary' style='font-weight: 300;'>대체</span><span class='pl-1'></span>" +
				"<span>#= creativeName #</span>" + 
			"# } #";
	String uuidTemplate =
			"# if (directReported) { #" +
				"<span class='text-muted'>#= uuid #</span>" +
			"# } else { #" +
				"<span>#= uuid #</span>" +
			"# } #";
			
			
	String catTemplate =
			"# if (category == 'R') { #" + 
				"<span class='fa-solid fa-circle text-red'></span>" + 
			"# } else if (category == 'O') { #" +
				"<span class='fa-solid fa-square text-orange'></span>" + 
			"# } else if (category == 'Y') { #" +
				"<span class='fa-solid fa-star text-yellow'></span>" + 
			"# } else if (category == 'G') { #" +
				"<span class='fa-solid fa-diamond text-green'></span>" + 
			"# } else if (category == 'B') { #" +
				"<span class='fa-solid fa-heart text-blue'></span>" + 
			"# } else if (category == 'P') { #" +
				"<span class='fa-solid fa-apple-whole text-purple'></span>" + 
			"# } else { #" +
				"<span></span>" +
			"# } #";
	String reportTypeTemplate =
			"# if (reportType == 'I') { #" + 
				"<span title='정보'><span class='fa-regular fa-circle-exclamation fa-fw text-info'></span></span>" + 
			"# } else if (reportType == 'W') { #" +
				"<span title='경고'><span class='fa-regular fa-bell fa-fw text-orange'></span></span>" + 
			"# } else if (reportType == 'E') { #" +
				"<span title='오류'><span class='fa-regular fa-light-emergency-on fa-fw text-red'></span></span>" + 
			"# } else { #" +
				"<span></span>" +
			"# } #";
	String equipTypeTemplate =
			"# if (equipType == 'P') { #" + 
				"<span title='STB(Player)'><span class='fa-regular fa-flag fa-fw'></span></span>" + 
			"# } else { #" +
				"<span></span>" +
			"# } #";
	String triggerTypeTemplate =
			"# if (triggerType == 'A') { #" + 
				"<span title='외부 앱'><span class='fa-regular fa-rocket fa-fw'></span></span>" + 
			"# } else if (triggerType == 'C') { #" +
				"<span title='명령'><span class='fa-regular fa-command fa-fw'></span></span>" + 
			"# } else if (triggerType == 'P') { #" +
				"<span title='내부 프로세스'><span class='fa-regular fa-gear fa-fw'></span></span>" + 
			"# } else if (triggerType == 'E') { #" +
				"<span title='기타'><span class='fa-regular fa-circle-dashed fa-fw'></span></span>" + 
			"# } else { #" +
				"<span></span>" +
			"# } #";
	String equipNameTemplate =
			"# if (equipType == 'P') { #" +
				"<span>#= equipName #</span>" +
				"<a href='javascript:showScreen(#= equipId #,\"#= equipName #\")' class='btn btn-default btn-xs icon-btn ml-1'><span class='fas fa-search text-info'></span></a>" +
			"# } else { #" +
				"<span>#= equipName #</span>" +
			"# } #";
			
	String eventRegDateTemplate = net.doohad.utils.Util.getSmartDate("whoCreationDate", true);

	String syncPackNameTemplate =
			"<div class='d-flex align-items-center'>" +
				"# if (reqStatus == '6') { #" +
					"<span title='10분내 요청'><span class='fa-solid fa-flag-swallowtail text-blue'></span></span>" +
				"# } else if (reqStatus == '5') { #" +
					"<span title='1시간내 요청'><span class='fa-solid fa-flag-swallowtail text-green'></span></span>" +
				"# } else if (reqStatus == '4') { #" +
					"<span title='6시간내 요청'><span class='fa-solid fa-flag-swallowtail text-yellow'></span></span>" +
				"# } else if (reqStatus == '3') { #" +
					"<span title='24시간내 요청'><span class='fa-solid fa-flag-swallowtail text-orange'></span></span>" +
				"# } else if (reqStatus == '1') { #" +
					"<span title='24시간내 없음'><span class='fa-solid fa-flag-pennant text-danger'></span></span>" +
				"# } else if (reqStatus == '0') { #" +
					"<span title='기록 없음'><span class='fa-solid fa-flag-pennant text-secondary'></span></span>" +
				"# } #" +
				"<span class='pl-1'>#= name #</span>" +
			"</div>";
	String syncPackShortNameTemplate =
			"<div class='d-flex align-items-center'>" +
				"<span class='pr-1'>#= shortName #</span>" +
			"</div>";

	String lastAdTemplate = 
			"# if (lastAd) { #" +
				"<span>#= lastAd #</span>" +
			"# } else { #" +
				"<span></span>" +
			"# } #";
	
	String activeScrCntTemplate =
			"<div class='d-flex align-items-center'>" +
				"# if (screenCount != 0) { #" +
					"# if (screenCount == activeScreenCount) { #" +
						"<span class='pr-1'><span class='fa-solid fa-circle-small text-blue'></span></span>" +
					"# } else { #" +
						"<span class='pr-1'><span class='fa-solid fa-circle-small text-orange'></span></span>" +
					"# } #" +
				"# } #" +
				"<span>#= activeScreenCount # / #= screenCount #</span>" +
			"</div>";

	String lastAdBeginDateTemplate = net.doohad.utils.Util.getSmartDate("lastAdBegin");
	String lastAdReqDateTemplate = net.doohad.utils.Util.getSmartDate("lastAdReq");

	
	String playBeginDateTemplate2 = net.doohad.utils.Util.getSmartDate("playBeginDate");
	String playEndDateTemplate2 = net.doohad.utils.Util.getSmartDate("playEndDate");
	String creationDateTemplate = net.doohad.utils.Util.getSmartDate("whoCreationDate", true);
	String realBeginDateTemplate = net.doohad.utils.Util.getSmartDate("realBeginDate");

	String diffTemplate =
			"# if (diff > 0) { #" +
				"<span>+</span>" +
			"# } #" +
			"<span>#= diff # s</span>";
%>



<!--  Forms -->

<ul class="nav nav-tabs tabs-alt container-p-x container-m--x mb-4">

<c:if test="${syncStat.syncPackMode}">

	<li class="nav-item">
		<a class="nav-link" data-toggle="tab" href="#active-sync-pack" id="active-sync-pack-tab-a">
			<i class="mr-1 fa-light fa-box-taped"></i>
			활성 동기화 묶음
		</a>
	</li>

</c:if>	
	
	<li class="nav-item">
		<a class="nav-link" data-toggle="tab" href="#active-screen" id="active-screen-tab-a">
			<i class="mr-1 fa-light fa-flag-swallowtail"></i>
			활성 화면
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" data-toggle="tab" href="#ad-sel-log">
			<i class="mr-1 fa-light fa-table-list"></i>
			광고 선택/보고 로그
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" data-toggle="tab" href="#rec-chan-ad">
			<i class="mr-1 fa-light fa-tower-cell"></i>
			채널 광고
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" data-toggle="tab" href="#event-report">
			<i class="mr-1 fa-light fa-bell"></i>
			이벤트 보고
		</a>
	</li>
</ul>

<div class="tab-content">
	<div class="tab-pane" id="active-sync-pack">
	
	
                        

	
<!-- Kendo grid  -->

<div class="mb-4">
<kendo:grid name="grid-sync" pageable="true" filterable="true" sortable="true" scrollable="true" reorderable="true" resizable="true" selectable="single" >
	<kendo:grid-excel fileName="${pageTitle}.xlsx" allPages="true" proxyURL="/proxySave"/>
	<kendo:grid-pageable refresh="true" buttonCount="5" pageSize="10" pageSizes="${pageSizesNormal}" />
	<kendo:grid-toolbarTemplate>
    	<div class="clearfix">
    		<div class="float-left">
    		
    			<div class="d-none d-md-inline">
					<div class="btn-group btn-group-toggle" data-toggle="buttons">
						<label class="btn btn-default px-2" name="status-radio-label">
							<input type="radio" name="status-radio" value="6">
					   			<span class="fa-stack fa-xs" title='10분내 요청'>
									<span class="fa-solid fa-circle fa-stack-2x text-blue"></span>
									<span class="fa-solid fa-flag-swallowtail fa-stack-1x fa-inverse" data-fa-transform="right-1"></span>
					   			</span>
					  			<span class="text-blue"><span id="stat-sync-status-6">${syncStat.statusCnt6}</span></span>
							</input>
						</label>
						<label class="btn btn-default px-2" name="status-radio-label">
							<input type="radio" name="status-radio" value="5">
					   			<span class="fa-stack fa-xs" title='1시간내 요청'>
									<span class="fa-solid fa-circle fa-stack-2x text-green"></span>
									<span class="fa-solid fa-flag-swallowtail fa-stack-1x fa-inverse" data-fa-transform="right-1"></span>
					   			</span>
					  			<span class="text-green"><span id="stat-sync-status-5">${syncStat.statusCnt5}</span></span>
							</input>
						</label>
						<label class="btn btn-default px-2" name="status-radio-label">
							<input type="radio" name="status-radio" value="4">
					   			<span class="fa-stack fa-xs" title='6시간내 요청'>
									<span class="fa-solid fa-circle fa-stack-2x text-yellow"></span>
									<span class="fa-solid fa-flag-swallowtail fa-stack-1x fa-inverse" data-fa-transform="right-1"></span>
					   			</span>
					  			<span class="text-yellow"><span id="stat-sync-status-4">${syncStat.statusCnt4}</span></span>
							</input>
						</label>
						<label class="btn btn-default px-2" name="status-radio-label">
							<input type="radio" name="status-radio" value="3">
					   			<span class="fa-stack fa-xs" title='24시간내 요청'>
									<span class="fa-solid fa-circle fa-stack-2x text-orange"></span>
									<span class="fa-solid fa-flag-swallowtail fa-stack-1x fa-inverse" data-fa-transform="right-1"></span>
					   			</span>
					  			<span class="text-orange"><span id="stat-sync-status-3">${syncStat.statusCnt3}</span></span>
							</input>
						</label>
						<label class="btn btn-default px-2" name="status-radio-label">
							<input type="radio" name="status-radio" value="1">
					   			<span class="fa-stack fa-xs" title='24시간내 없음'>
									<span class="fa-solid fa-circle fa-stack-2x text-danger"></span>
									<span class="fa-solid fa-flag-pennant fa-stack-1x fa-inverse" data-fa-transform="right-2"></span>
					   			</span>
					  			<span class="text-danger"><span id="stat-sync-status-1">${syncStat.statusCnt1}</span></span>
							</input>
						</label>
						<label class="btn btn-default px-2" name="status-radio-label">
							<input type="radio" name="status-radio" value="0">
					   			<span class="fa-stack fa-xs" title='기록 없음'>
									<span class="fa-solid fa-circle fa-stack-2x text-secondary"></span>
									<span class="fa-solid fa-flag-pennant fa-stack-1x fa-inverse" data-fa-transform="right-2"></span>
					   			</span>
					  			<span class="text-secondary"><span id="stat-sync-status-0">${syncStat.statusCnt0}</span></span>
							</input>
						</label>
					</div>
					<span class="pl-2"></span>
	    			<button id="filter-sync-cancel-btn" type="button" class="btn btn-default px-2">
	    				<span title="상태 필터 해제">
							<span class="fa-light fa-filter-circle-xmark fa-lg fa-fw" data-fa-transform="down-2"></span>
	    				</span>
					</button>
    			</div>
    			
    		</div>
    		<div class="float-right">

    			<div class="d-none d-sm-inline">
					<button type='button' id="excel-sync-btn" class='btn icon-btn btn-sm btn-outline-secondary'> 
	    				<span title="엑셀 형식으로 다운로드">
		    				<span class="fa-light fa-file-excel fa-fw fa-lg"></span>
	    				</span>
					</button>
	   				<span class="px-2">
	    				<span class="fa-solid fa-pipe text-muted"></span>
	   				</span>
    			</div>
	    		<div class="btn-group">
					<button type="button" class="btn btn-secondary dropdown-toggle" data-toggle="dropdown">
						<span class="fa-light fa-command"></span>
						<span class="pl-1">원격 명령</span>
					</button>
					<div class="dropdown-menu">
						<a class="dropdown-item" href="javascript:void(0)" id="cmd-sync-reset-btn">
							<i class="fa-light fa-bolt fa-fw text-blue"></i><span class="pl-2">동기화 리셋	</span>
						</a>
					</div>
				</div>
    		
    		</div>
    	</div>
	</kendo:grid-toolbarTemplate>
	<kendo:grid-filterable extra="false" />
	<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />
	<kendo:grid-columns>
		<kendo:grid-column title="묶음 이름" field="name" width="200" template="<%= syncPackNameTemplate %>" />
		<kendo:grid-column title="묶음ID" field="shortName" width="180" template="<%= syncPackShortNameTemplate %>" />
		<kendo:grid-column title="10분내 동작 중" width="120" template="<%= activeScrCntTemplate %>" />
		<kendo:grid-column title="오차(ms)" width="100" field="diff" sortable="false" filterable="false" />
		<kendo:grid-column title="최근 광고 시작" field="lastAdBegin" width="130" filterable="false" sortable="false" template="<%= lastAdBeginDateTemplate %>"/>
		<kendo:grid-column title="최근 광고" width="200" field="lastAd" template="<%= lastAdTemplate %>" sortable="false" filterable="false" />
		<kendo:grid-column title="최근 광고 요청" field="lastAdReq" width="130" filterable="false" sortable="false" template="<%= lastAdReqDateTemplate %>"/>
		<kendo:grid-column title="등급 큐" width="120" field="gradeQueue" sortable="false" filterable="false" />
		<kendo:grid-column title="기기 큐" width="120" field="countQueue" sortable="false" filterable="false" />
		<kendo:grid-column title="채널" width="120" field="channel" sortable="false" filterable="false" />
		<kendo:grid-column title="채널광고번호" width="120" field="seq" sortable="false" filterable="false" />
		<kendo:grid-column title="채널비교(s)" width="120" field="seqDiff" sortable="false" filterable="false" />
		<kendo:grid-column title="재생목록" width="200" field="playlist" sortable="false" filterable="false" />
	</kendo:grid-columns>
	<kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="shortName" dir="asc"/>
		</kendo:dataSource-sort>
       	<kendo:dataSource-filter>
      		<kendo:dataSource-filterItem field="medium.id" operator="eq" logic="and" value="${sessionScope['currMediumId']}" />
      		<kendo:dataSource-filterItem field="activeStatus" operator="eq" logic="and" value="true" />
  	    </kendo:dataSource-filter>
		<kendo:dataSource-transport>
			<kendo:dataSource-transport-read url="${readSyncPackUrl}" dataType="json" type="POST" contentType="application/json">
				<kendo:dataSource-transport-read-data>
					<script>
						function additionalData(e) {
							var checkValue = $("#grid-sync input[name='status-radio']:checked").val();
							if (!checkValue) {
								checkValue = "9";
							}
							
							return { reqStrValue1: checkValue, reqStrValue2: $("#grid-sync input[name='sync-view-type-radio']:checked").val() };
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
					<kendo:dataSource-schema-model-field name="lastAdBegin" type="date" />
					<kendo:dataSource-schema-model-field name="lastAdReq" type="date" />
					<kendo:dataSource-schema-model-field name="diff" type="number" />
				</kendo:dataSource-schema-model-fields>
			</kendo:dataSource-schema-model>
		</kendo:dataSource-schema>
	</kendo:dataSource>
</kendo:grid>
</div>

<!-- / Kendo grid  -->


<!-- Grid button actions  -->

<script>
$(document).ready(function() {

	$('[data-toggle="tooltip"]').tooltip();

	// Filter
	$("#grid-sync input[name='status-radio']").change(function(){
		
		if ($("#grid-sync").data("kendoGrid").dataSource.page() != 1) {
			$("#grid-sync").data("kendoGrid").dataSource.page(1);
		} else {
			$("#grid-sync").data("kendoGrid").dataSource.read();
		}
		
		refreshSyncPackStat();
	});
	// / Filter
	
	// Excel
	$("#excel-sync-btn").click(function(e) {
		e.preventDefault();
		
		$("#grid-sync").data("kendoGrid").saveAsExcel();
	});
	// / Excel
	
	// Filter cancel(필터 취소)
	$("#filter-sync-cancel-btn").click(function(e) {
		e.preventDefault();
		
		setTimeout(function(){
			$("#grid-sync input[name='status-radio']").prop('checked', false);
			$("#grid-sync label[name='status-radio-label']").removeClass("active");
			
			if ($("#grid-sync").data("kendoGrid").dataSource.page() != 1) {
				$("#grid-sync").data("kendoGrid").dataSource.page(1);
			} else {
				$("#grid-sync").data("kendoGrid").dataSource.read();
			}
			
			refreshSyncPackStat();
		}, 100);
	});
	// / Filter cancel(필터 취소)
	
	
	// Command list
	$("#cmd-sync-reset-btn").click(function(e) {
		e.preventDefault();
		
		requestSyncCommandAction("Refresh");
	});
	// / Command list
	
});	
</script>


<script>

function requestSyncCommandAction(cmd) {

	var grid = $("#grid-sync").data("kendoGrid");
	var rows = grid.select();

	var selRows = [];
	rows.each(function(index, row) {
		var selectedItem = grid.dataItem(row);
		selRows.push(selectedItem.id);
	});

	if (selRows.length > 0) {
		
		var data = {
			command: cmd,
			ids: selRows,
		};
		
		$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: "${processSyncCmdUrl}",
			data: JSON.stringify(data),
			success: function (form) {
				showOperationSuccessMsg();
				$("#grid-sync").data("kendoGrid").dataSource.read();
				
				refreshSyncPackStat();
			},
			error: ajaxOperationError
		});
	}
}


function refreshSyncPackStat() {

	$.ajax({
		type: "POST",
		contentType: "application/json",
		dataType: "json",
		url: "${readSyncPackStatUrl}",
		data: { },
		success: function (form) {
			
			$("#stat-sync-status-6").html(form.statusCnt6);
			$("#stat-sync-status-5").html(form.statusCnt5);
			$("#stat-sync-status-4").html(form.statusCnt4);
			$("#stat-sync-status-3").html(form.statusCnt3);
			$("#stat-sync-status-1").html(form.statusCnt1);
			$("#stat-sync-status-0").html(form.statusCnt0);
		},
		error: ajaxOperationError
	});
}

</script>
	
                        


	
	</div>
	<div class="tab-pane" id="active-screen">
	
	
                        

	
<!-- Kendo grid  -->

<div class="mb-4">
<kendo:grid name="grid-screen" pageable="true" filterable="true" sortable="true" scrollable="true" reorderable="true" resizable="true" selectable="multiple" >
	<kendo:grid-excel fileName="${pageTitle}.xlsx" allPages="true" proxyURL="/proxySave"/>
	<kendo:grid-pageable refresh="true" buttonCount="5" pageSize="10" pageSizes="${pageSizesNormal}" />
    <kendo:grid-toolbarTemplate>
    	<div class="clearfix">
    		<div class="float-left">
    			<div class="d-none d-md-inline">
					<div class="btn-group btn-group-toggle" data-toggle="buttons">
						<label class="btn btn-default px-2" name="status-radio-label">
							<input type="radio" name="status-radio" value="6">
					   			<span class="fa-stack fa-xs" title='10분내 요청'>
									<span class="fa-solid fa-circle fa-stack-2x text-blue"></span>
									<span class="fa-solid fa-flag-swallowtail fa-stack-1x fa-inverse" data-fa-transform="right-1"></span>
					   			</span>
					  			<span class="text-blue"><span id="stat-status-6">${stat.statusCnt6}</span></span>
							</input>
						</label>
						<label class="btn btn-default px-2" name="status-radio-label">
							<input type="radio" name="status-radio" value="5">
					   			<span class="fa-stack fa-xs" title='1시간내 요청'>
									<span class="fa-solid fa-circle fa-stack-2x text-green"></span>
									<span class="fa-solid fa-flag-swallowtail fa-stack-1x fa-inverse" data-fa-transform="right-1"></span>
					   			</span>
					  			<span class="text-green"><span id="stat-status-5">${stat.statusCnt5}</span></span>
							</input>
						</label>
						<label class="btn btn-default px-2" name="status-radio-label">
							<input type="radio" name="status-radio" value="4">
					   			<span class="fa-stack fa-xs" title='6시간내 요청'>
									<span class="fa-solid fa-circle fa-stack-2x text-yellow"></span>
									<span class="fa-solid fa-flag-swallowtail fa-stack-1x fa-inverse" data-fa-transform="right-1"></span>
					   			</span>
					  			<span class="text-yellow"><span id="stat-status-4">${stat.statusCnt4}</span></span>
							</input>
						</label>
						<label class="btn btn-default px-2" name="status-radio-label">
							<input type="radio" name="status-radio" value="3">
					   			<span class="fa-stack fa-xs" title='24시간내 요청'>
									<span class="fa-solid fa-circle fa-stack-2x text-orange"></span>
									<span class="fa-solid fa-flag-swallowtail fa-stack-1x fa-inverse" data-fa-transform="right-1"></span>
					   			</span>
					  			<span class="text-orange"><span id="stat-status-3">${stat.statusCnt3}</span></span>
							</input>
						</label>
						<label class="btn btn-default px-2" name="status-radio-label">
							<input type="radio" name="status-radio" value="1">
					   			<span class="fa-stack fa-xs" title='24시간내 없음'>
									<span class="fa-solid fa-circle fa-stack-2x text-danger"></span>
									<span class="fa-solid fa-flag-pennant fa-stack-1x fa-inverse" data-fa-transform="right-2"></span>
					   			</span>
					  			<span class="text-danger"><span id="stat-status-1">${stat.statusCnt1}</span></span>
							</input>
						</label>
						<label class="btn btn-default px-2" name="status-radio-label">
							<input type="radio" name="status-radio" value="0">
					   			<span class="fa-stack fa-xs" title='기록 없음'>
									<span class="fa-solid fa-circle fa-stack-2x text-secondary"></span>
									<span class="fa-solid fa-flag-pennant fa-stack-1x fa-inverse" data-fa-transform="right-2"></span>
					   			</span>
					  			<span class="text-secondary"><span id="stat-status-0">${stat.statusCnt0}</span></span>
							</input>
						</label>
					</div>
					<div class="d-none d-xl-inline">
						<span class="pl-1"></span>
						<div class="btn-group btn-group-toggle" data-toggle="buttons">
							<label class="btn btn-default px-2" name="cmd-radio-label">
								<input type="radio" name="cmd-radio" value="C">
									<span class="fa-light fa-command fa-fw" title="명령이 설정됨"></span>
						  			<span><span id="stat-cmd">${stat.cmdCnt}</span></span>
								</input>
							</label>
							<label class="btn btn-default px-2" name="cmd-radio-label">
								<input type="radio" name="cmd-radio" value="F">
									<span class="fa-light fa-command fa-fw text-danger" title="명령 실행 실패"></span>
						  			<span class="text-danger"><span id="stat-fail-cmd">${stat.cmdFailCnt}</span></span>
								</input>
							</label>
						</div>
					</div>
					<span class="pl-2"></span>
	    			<button id="filter-cancel-btn" type="button" class="btn btn-default px-2">
	    				<span title="상태 필터 해제">
							<span class="fa-light fa-filter-circle-xmark fa-lg fa-fw" data-fa-transform="down-2"></span>
	    				</span>
					</button>
    			</div>
    		</div>
    		<div class="float-right">

    			<div class="d-none d-sm-inline">
					<button type='button' id="excel-screen-btn" class='btn icon-btn btn-sm btn-outline-secondary'> 
	    				<span title="엑셀 형식으로 다운로드">
		    				<span class="fa-light fa-file-excel fa-fw fa-lg"></span>
	    				</span>
					</button>
	   				<span class="px-2">
	    				<span class="fa-solid fa-pipe text-muted"></span>
	   				</span>
    			</div>
    			<div class="d-none d-lg-inline">
					<div class="btn-group btn-group-toggle" data-toggle="buttons">
						<label class="btn btn-outline-secondary btn-sm active" title="필수 항목">
							<input type="radio" name="view-type-radio" value="M" checked>
								<span class='fa-light fa-arrows-to-dot fa-lg'></span>
							</input>
						</label>
						<label class="btn btn-outline-secondary btn-sm" title="시간 정보">
							<input type="radio" name="view-type-radio" value="T">
								<span class='fa-light fa-clock fa-lg'></span>
							</input>
						</label>
						<label class="btn btn-outline-secondary btn-sm" title="기타 항목">
							<input type="radio" name="view-type-radio" value="E">
								<span class='fa-light fa-left-right fa-lg'></span>
							</input>
						</label>
					</div>
	   				<span class="px-2">
	    				<span class="fa-solid fa-pipe text-muted"></span>
	   				</span>
    			</div>
	    		<div class="btn-group">
					<button type="button" class="btn btn-secondary dropdown-toggle" data-toggle="dropdown">
						<span class="fa-light fa-command"></span>
						<span class="pl-1">원격 명령</span>
					</button>
					<div class="dropdown-menu">
						<a class="dropdown-item" href="javascript:void(0)" id="cmd-reboot-btn">
							<i class="fa-light fa-plug-circle-bolt fa-fw"></i><span class="pl-2">기기 재시작(리부팅)</span>
						</a>
						<a class="dropdown-item" href="javascript:void(0)" id="cmd-log-btn">
							<i class="fa-light fa-file-lines fa-fw"></i><span class="pl-2">로그 업로드</span>
						</a>
						<a class="dropdown-item" href="javascript:void(0)" id="cmd-update-btn">
							<i class="fa-light fa-sparkles fa-fw"></i><span class="pl-2">플레이어 업데이트</span>
						</a>
						<a class="dropdown-item" href="javascript:void(0)" id="cmd-restart-btn">
							<i class="fa-light fa-arrow-rotate-right fa-fw"></i><span class="pl-2">플레이어 재시작</span>
						</a>
						<div class="dropdown-divider"></div>
						<a class="dropdown-item" href="javascript:void(0)" id="cmd-cancel-btn">
							<i class="fa-regular fa-delete-left text-danger fa-fw"></i><span class="pl-2">명령 취소/초기화</span>
						</a>
					</div>
				</div>
    		
    		</div>
    	</div>
   	</kendo:grid-toolbarTemplate>
   	<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />
	<kendo:grid-columns>
		<kendo:grid-column title="화면명" field="name" width="300" template="<%= nameTemplate %>" />
		<kendo:grid-column title="화면ID" field="shortName" width="150" />
		<kendo:grid-column title="해상도" field="resolution" width="120" 
				template="#= resolution.replace('x', ' x ') #" >
			<kendo:grid-column-filterable multi="true" itemTemplate="kfcTextOnly">
				<kendo:dataSource>
					<kendo:dataSource-transport>
						<kendo:dataSource-transport-read url="${readResolutionUrl}" dataType="json" type="POST" contentType="application/json" />
					</kendo:dataSource-transport>
				</kendo:dataSource>
			</kendo:grid-column-filterable>
		</kendo:grid-column>
		<kendo:grid-column title="API 테스트" width="200" template="<%= apiTemplate %>" />
		<kendo:grid-column title="플레이어ver" field="playerVer" width="120" template="<%= playerVerTemplate %>" />
		<kendo:grid-column title="최근 기록" field="lastAdReportDate" width="350" filterable="false" template="#= dispActValues(lastTouches, lastFileDate, 
				lastAdRequestDate, lastAdReportDate, lastInfoDate, lastCommandDate, lastCommandReportDate, lastEventDate) #" />
		<kendo:grid-column title="원격 명령" field="nextCmd" width="150" template="<%= nextCmdTemplate %>" />
		<kendo:grid-column title="명령확인" field="lastCommandDate" width="150" template="<%= commandDateTemplate %>" />
		<kendo:grid-column title="명령결과보고" field="lastCommandReportDate" width="150" template="<%= commandReportDateTemplate %>" />
		<kendo:grid-column title="파일정보요청" field="lastFileDate" width="150" template="<%= fileDateTemplate %>" />
		<kendo:grid-column title="현재광고요청" field="lastAdRequestDate" width="150" template="<%= adRequestDateTemplate %>" />
		<kendo:grid-column title="방송완료보고" field="lastAdReportDate" width="150" template="<%= adReportDateTemplate %>" />
		<kendo:grid-column title="플레이어시작" field="lastInfoDate" width="150" template="<%= infoDateTemplate %>" />
		<kendo:grid-column title="이벤트보고" field="lastEventDate" width="150" template="<%= eventDateTemplate %>" />
		<kendo:grid-column title="키퍼ver" field="keeperVer" width="120" template="<%= keeperVerTemplate %>" />
		<kendo:grid-column title="시/군/구" field="regionName" width="150" />
		<kendo:grid-column title="화면 묶음" field="scrPackName" width="150" filterable="false" sortable="false" />
	</kendo:grid-columns>
	<kendo:grid-filterable>
		<kendo:grid-filterable-messages selectedItemsFormat="{0} 항목 선택됨"/>
	</kendo:grid-filterable>
	<kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="name" dir="asc"/>
		</kendo:dataSource-sort>
       	<kendo:dataSource-filter>
   			<kendo:dataSource-filterItem field="mediumId" operator="eq" logic="and" value="${sessionScope['currMediumId']}" >
   			</kendo:dataSource-filterItem>
    	</kendo:dataSource-filter>
		<kendo:dataSource-transport>
			<kendo:dataSource-transport-read url="${readScrUrl}" dataType="json" type="POST" contentType="application/json">
				<kendo:dataSource-transport-read-data>
					<script>
						function additionalData(e) {
							var checkValue1 = $("#grid-screen input[name='status-radio']:checked").val();
							var checkValue2 = $("#grid-screen input[name='cmd-radio']:checked").val();
							if (!checkValue1) {
								checkValue1 = "9";
							}
							if (!checkValue2) {
								checkValue2 = "9";
							}
							
							return { reqStrValue1: checkValue1, reqStrValue2: checkValue2 };
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
					<kendo:dataSource-schema-model-field name="lastFileDate" type="date" />
					<kendo:dataSource-schema-model-field name="lastAdRequestDate" type="date" />
					<kendo:dataSource-schema-model-field name="lastAdReportDate" type="date" />
					<kendo:dataSource-schema-model-field name="lastInfoDate" type="date" />
					<kendo:dataSource-schema-model-field name="lastCommandDate" type="date" />
					<kendo:dataSource-schema-model-field name="lastCommandReportDate" type="date" />
					<kendo:dataSource-schema-model-field name="lastEventDate" type="date" />
					<kendo:dataSource-schema-model-field name="gpsTime" type="date" />
				</kendo:dataSource-schema-model-fields>
			</kendo:dataSource-schema-model>
		</kendo:dataSource-schema>
	</kendo:dataSource>
</kendo:grid>
</div>

<!-- / Kendo grid  -->


<!-- Grid button actions  -->

<script>
$(document).ready(function() {

	$('[data-toggle="tooltip"]').tooltip();

	// Filter
	$("#grid-screen input[name='status-radio']").change(function(){
		
		if ($("#grid-screen").data("kendoGrid").dataSource.page() != 1) {
			$("#grid-screen").data("kendoGrid").dataSource.page(1);
		} else {
			$("#grid-screen").data("kendoGrid").dataSource.read();
		}
		
		refreshScrStat();
	});

	$("#grid-screen input[name='cmd-radio']").change(function(){
		
		if ($("#grid-screen").data("kendoGrid").dataSource.page() != 1) {
			$("#grid-screen").data("kendoGrid").dataSource.page(1);
		} else {
			$("#grid-screen").data("kendoGrid").dataSource.read();
		}
		
		refreshScrStat();
	});
	// / Filter
	
	// Excel
	$("#excel-screen-btn").click(function(e) {
		e.preventDefault();
		
		$("#grid-screen").data("kendoGrid").saveAsExcel();
	});
	// / Excel
	
	// Filter cancel(필터 취소)
	$("#filter-cancel-btn").click(function(e) {
		e.preventDefault();
		
		setTimeout(function(){
			$("#grid-screen input[name='status-radio']").prop('checked', false);
			$("#grid-screen label[name='status-radio-label']").removeClass("active");
			
			$("#grid-screen input[name='cmd-radio']").prop('checked', false);
			$("#grid-screen label[name='cmd-radio-label']").removeClass("active");
			
			if ($("#grid-screen").data("kendoGrid").dataSource.page() != 1) {
				$("#grid-screen").data("kendoGrid").dataSource.page(1);
			} else {
				$("#grid-screen").data("kendoGrid").dataSource.read();
			}
			
			refreshScrStat();
		}, 100);
	});
	// / Filter cancel(필터 취소)
	
	
	// Command list
	$("#cmd-reboot-btn").click(function(e) {
		e.preventDefault();
		
		requestCommandAction("Reboot");
	});

	$("#cmd-update-btn").click(function(e) {
		e.preventDefault();
		
		requestCommandAction("Update");
	});

	$("#cmd-restart-btn").click(function(e) {
		e.preventDefault();
		
		requestCommandAction("Restart");
	});

	$("#cmd-log-btn").click(function(e) {
		e.preventDefault();
		
		requestCommandAction("Log");
	});

	$("#cmd-cancel-btn").click(function(e) {
		e.preventDefault();
		
		requestCommandAction("Cancel");
	});
	// / Command list
	
	
	// 컬럼 조회 유형
	$("input[name='view-type-radio']").change(function(){
		
		var grid = $("#grid-screen").data("kendoGrid");
		var cols = [];
		
		if ($(this).val() == "M") {
			cols = [3, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16];
		} else if ($(this).val() == "T") {
			cols = [3, 14, 15, 16];
		} else if ($(this).val() == "E") {
			cols = [6, 7, 8, 9, 10, 11, 12, 13];
		}
		
		showColumns([1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16], grid);
		hideColumns(cols, grid);

	});

	hideColumns([3, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16], $("#grid-screen").data("kendoGrid"));
	// / 컬럼 조회 유형
	
});	
</script>


<script>

function requestCommandAction(cmd) {

	var grid = $("#grid-screen").data("kendoGrid");
	var rows = grid.select();

	var selRows = [];
	rows.each(function(index, row) {
		var selectedItem = grid.dataItem(row);
		selRows.push(selectedItem.id);
	});

	if (selRows.length > 0) {
		
		var data = {
			command: cmd,
			ids: selRows,
		};
		
		$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: "${processCmdUrl}",
			data: JSON.stringify(data),
			success: function (form) {
				showSaveSuccessMsg();
				$("#grid-screen").data("kendoGrid").dataSource.read();
				
				refreshScrStat();
			},
			error: ajaxSaveError
		});
	}
}


function getFriendlyCmdName(cmd) {
	
	if (cmd) {
		if (cmd == "Reboot") {
			return "기기 재시작(리부팅)";
		} else if (cmd == "Update") {
			return "플레이어 업데이트";
		} else if (cmd == "Restart") {
			return "플레이어 재시작";
		} else if (cmd == "Log") {
			return "로그 업로드";
		}
	}
	
	return cmd;
}


function refreshScrStat() {

	$.ajax({
		type: "POST",
		contentType: "application/json",
		dataType: "json",
		url: "${readMonitStatUrl}",
		data: { },
		success: function (form) {
			
			$("#stat-status-6").html(form.statusCnt6);
			$("#stat-status-5").html(form.statusCnt5);
			$("#stat-status-4").html(form.statusCnt4);
			$("#stat-status-3").html(form.statusCnt3);
			$("#stat-status-1").html(form.statusCnt1);
			$("#stat-status-0").html(form.statusCnt0);
			
			$("#stat-cmd").html(form.cmdCnt);
			$("#stat-fail-cmd").html(form.cmdFailCnt);
		},
		error: ajaxOperationError
	});
}


function dispActValues(values, fileDate, adRequestDate, adReportDate, infoDate, commandDate, commandReportDate, eventDate) {
	
	var ret = "";
	var value = values.split("|");
	var date = null;
	var dateTag = "";
	  
	for(var i = 0; i < value.length; i ++) {
		if (value[i]) {
			date = null;
			dateTag = "";
			
			if (value[i] == "file") {
				date = fileDate;
			} else if (value[i] == "ad") {
				date = adRequestDate;
			} else if (value[i] == "rpt") {
				date = adReportDate;
			} else if (value[i] == "info") {
				date = infoDate;
			} else if (value[i] == "cmd1") {
				date = commandDate;
			} else if (value[i] == "cmd2") {
				date = commandReportDate;
			} else if (value[i] == "evt") {
				date = eventDate;
			}
			
			if (date == null) {
				dateTag = "<span>-</span>";
			} else if (isToday(date)) {
				dateTag = "<span>" + kendo.format('{0:HH:mm:ss}', date) + "</span>";
			} else if (isThisYear(date)) {
				dateTag = "<span>" + kendo.format('{0:M/d}', date) + " <small>" + kendo.format('{0:HH:mm:ss}', date) + "</small></span>";
			} else {
				dateTag = "<span>" + kendo.format('{0:yyyy M/d}', date) + " <small>" + kendo.format('{0:HH:mm:ss}', date) + "</small></span>";
			}
			
			if (value[i] == "file") {
				ret = ret + "<span class='badge badge-outline-secondary default-pointer' title='파일정보요청'>file<span class='pl-1'>" + dateTag + "</span></span>";
			} else if (value[i] == "ad") {
				ret = ret + "<span class='badge badge-outline-secondary default-pointer' title='현재광고요청'>ad<span class='pl-1'>" + dateTag + "</span></span>";
			} else if (value[i] == "rpt") {
				ret = ret + "<span class='badge badge-pill bg-blue text-white default-pointer' title='완료보고'><span class='fa-regular fa-flag-checkered'></span><span class='pl-1'>" + dateTag + "</span></span>";
			} else if (value[i] == "info") {
				ret = ret + "<span class='badge badge-pill bg-green text-white default-pointer' title='플레이어시작'><span class='fa-regular fa-power-off'></span><span class='pl-1'>" + dateTag + "</span></span>";
			} else if (value[i] == "cmd1" || value[i] == "cmd2") {
				ret = ret + "<span class='badge badge-pill bg-orange text-white default-pointer' title='명령확인/보고'><span class='fa-regular fa-command'></span><span class='pl-1'>" + dateTag + "</span></span>";
			} else if (value[i] == "evt") {
				ret = ret + "<span class='badge badge-pill bg-green text-white default-pointer' title='이벤트보고'><span class='fa-regular fa-bell'></span><span class='pl-1'>" + dateTag + "</span></span>";
			} else if (value[i] == "pl1" || value[i] == "pl2" || value[i] == "pl3" || value[i] == "pl4") {
				ret = ret + "<span class='badge badge-pill bg-emerald text-white default-pointer' title='재생목록확인'><span class='fa-regular fa-list-ol'></span><span class='pl-1'>" + dateTag + "</span></span>";
			} else {
				ret = ret + "<span></span>";
			}
			
			ret = ret + "<span class='pl-1'></span>";
		}
	}
	  
	return ret;
}


function deletePlTime(id, lane) {

	$.ajax({
		type: "POST",
		contentType: "application/json",
		dataType: "json",
		url: "${destoryPlTimeUrl}",
		data: JSON.stringify({ id: id, lane: lane }),
		success: function (form) {
			showDeleteSuccessMsg();
			$("#grid-screen").data("kendoGrid").dataSource.read();
		},
		error: ajaxDeleteError
	});
}

</script>

<!-- / Grid button actions  -->


	
	</div>
	<div class="tab-pane" id="ad-sel-log">
	
	
	
<!-- Kendo grid  -->

<div class="mb-4">
<kendo:grid name="grid-api" pageable="true" filterable="true" sortable="true" scrollable="true" reorderable="true" resizable="true" selectable="single" >
	<kendo:grid-excel fileName="${pageTitle}.xlsx" allPages="true" proxyURL="/proxySave"/>
	<kendo:grid-pageable refresh="true" buttonCount="5" pageSize="10" pageSizes="${pageSizesNormal}" />
    <kendo:grid-toolbarTemplate>
    	<div class="clearfix">
    		<div class="float-left">
    			<button type="button" class="btn btn-default d-none d-sm-inline k-grid-excel">엑셀</button>
    		</div>
    		<div class="float-right  d-flex align-items-center">
    			<div id="screen-div" style="display:none;">
    				<span id="screen-name">화면명</span>
					<a href='javascript:showThisScreen()' class='btn btn-default btn-xs icon-btn ml-1'><span class='fas fa-search text-info'></span></a>
    				<span class="px-2">
	    				<span class="fa-solid fa-pipe text-muted"></span>
    				</span>
    			</div>
    			<input id="api-screen-ID" type="text" maxlength="100" class="form-control mr-1" placeholder="화면ID" style="width: 150px;">
    			<button id="query-btn" type="button" class="btn btn-default">조회</button>
    			<div class="d-none d-sm-inline">
	   				<span class="pl-2 pr-1">
	    				<span class="fa-solid fa-pipe text-muted"></span>
	   				</span>
	    			<button id="imp-btn" type="button" class="btn btn-default">
	    				<span class="fa-regular fa-eye text-muted"></span>
	    				노출
	    			</button>
    			</div>
    		</div>
    	</div>
   	</kendo:grid-toolbarTemplate>
   	<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />
	<kendo:grid-columns>
		<kendo:grid-column title="광고 선택" field="selectDate" width="130" template="<%= selectAdDateTemplate %>"/>
		<kendo:grid-column title="광고" field="adName" width="250" template="<%= adNameTemplate %>" />
		<kendo:grid-column title="결과" field="result" width="80" filterable="false" template="<%= resultTemplate %>" />
		<kendo:grid-column title="시작" field="beginDate" width="120" template="<%= playBeginDateTemplate %>"/>
		<kendo:grid-column title="종료" field="endDate" width="120" template="<%= playEndDateTemplate %>"/>
		<kendo:grid-column title="재생시간" field="duration" width="120" template="#= durDisp #" />
		<kendo:grid-column title="보고" field="reportDate" width="120" template="<%= reportDateTemplate %>"/>
		<kendo:grid-column title="광고 소재" field="creativeName" width="200" />
		<kendo:grid-column title="지연보고" field="delayReported" width="110"
				template="#=delayReported ? \"<span class='fa-light fa-check'>\" : \"\"#" />
		<kendo:grid-column title="UUID" field="uuid" width="300" template="<%= uuidTemplate %>" />
	</kendo:grid-columns>
	<kendo:grid-filterable>
		<kendo:grid-filterable-messages selectedItemsFormat="{0} 항목 선택됨"/>
	</kendo:grid-filterable>
	<kendo:grid-dataBound>
		<script>
			function grid_dataBound(e) {
				var grid = e.sender;
				var rows = grid.dataSource.view();

				if (rows.length > 0) {
					apiScrId = rows[0].screenId;
					apiScrName = rows[0].screenName;
					
					showScreenInfo();
				}
			}
		</script>
	</kendo:grid-dataBound>
	<kendo:dataSource serverPaging="false" serverSorting="false" serverFiltering="false" serverGrouping="false" error="kendoReadError">
		<kendo:dataSource-requestStart>
			<script>
				function dataSource_requestStart(e) {
					hideScreenInfo();
				}
			</script>
		</kendo:dataSource-requestStart>
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="selectDate" dir="desc"/>
		</kendo:dataSource-sort>
		<kendo:dataSource-transport>
			<kendo:dataSource-transport-read url="${readApiLogUrl}" dataType="json" type="POST" contentType="application/json">
				<kendo:dataSource-transport-read-data>
					<script>
						function additionalData(e) {
							return { reqStrValue1: $("#api-screen-ID").val() };
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
			<kendo:dataSource-schema-model id="id">
				<kendo:dataSource-schema-model-fields>
					<kendo:dataSource-schema-model-field name="selectDate" type="date" />
					<kendo:dataSource-schema-model-field name="beginDate" type="date" />
					<kendo:dataSource-schema-model-field name="endDate" type="date" />
					<kendo:dataSource-schema-model-field name="reportDate" type="date" />
					<kendo:dataSource-schema-model-field name="duration" type="number" />
					<kendo:dataSource-schema-model-field name="result" type="boolean" />
					<kendo:dataSource-schema-model-field name="delayReported" type="boolean" />
				</kendo:dataSource-schema-model-fields>
			</kendo:dataSource-schema-model>
		</kendo:dataSource-schema>
	</kendo:dataSource>
</kendo:grid>
</div>

<!-- / Kendo grid  -->


<!-- Grid button actions  -->

<script>
$(document).ready(function() {

	// Query
	$("#query-btn").click(function(e) {
		e.preventDefault();

		var grid = $("#grid-api").data("kendoGrid");
		grid.dataSource.read();
	});
	// / Query
	
	// Impress
	$("#imp-btn").click(function(e) {
		e.preventDefault();
			
		var grid = $("#grid-api").data("kendoGrid");
		var rows = grid.select();
	
		var opRows = [];
		
		rows.each(function(index, row) {
			var selectedItem = grid.dataItem(row);
			opRows.push(selectedItem.id);
		});

		
		if (opRows.length > 0) {
			var dataItem = $("#grid-api").data("kendoGrid").dataSource.get(opRows[0]);
		
	    	var data = {
	    		id: dataItem.id,
	    	};
	
			$.ajax({
				type: "POST",
				contentType: "application/json",
				dataType: "json",
				url: "${impressUrl}",
				data: JSON.stringify(data),
				success: function (data, status, xhr) {
					showAlertModal("success", JSON.parse(xhr.responseText));
					$("#form-modal-2").modal("hide");
				},
				error: ajaxOperationError
			});
		}
	});
	// / Impress
	
});


var apiScrId = -1;
var apiScrName = "";


function showScreenInfo() {

	$("#screen-name").text(apiScrName);
	$("#screen-div").show();
}


function hideScreenInfo() {

	apiScrId = -1;
	apiScrName = "";
	
	$("#screen-name").text(apiScrName);
	$("#screen-div").hide();
}


function showThisScreen() {
	
	if (apiScrId > 0 && apiScrName) {
		showScreen(apiScrId, apiScrName);
	}
}


function goSelLogTab(screenID) {
		
		var path = "/rev/monitoring?screen=" + screenID;
		location.href = path;
}

</script>

<!-- / Grid button actions  -->


	
	</div>
	<div class="tab-pane" id="rec-chan-ad">
	
	
	
<!-- Kendo grid  -->

<div class="mb-4">
<div class="card" style="border-bottom: transparent;">
	<h6 class="card-header with-elements pr-2">
		<span class="lead">
			<span class="fa-light fa-tower-cell fa-lg"></span>
			<span class="ml-1">채널 광고</span>
		</span>
		<div class="card-header-elements ml-auto py-0">
			<button type='button' id="chan-ad-excel-btn" class='btn icon-btn btn-sm btn-outline-secondary'> 
				<span class='fa-light fa-file-excel fa-lg'></span>
			</button>
			<span class="px-2">
 				<span class="fa-solid fa-pipe text-muted"></span>
			</span>
			<div class="float-right  d-flex align-items-center">

				<select name="chan-select" class="selectpicker bg-white" data-style="btn-default" data-none-selected-text="" data-width="250px" >

<c:forEach var="item" items="${Channels}">

		<option value="${item.value}" data-content="<span class='fa-light ${item.icon}'></span><span class='pl-2'>${item.text}</span><span class='small pl-3 opacity-75'>${item.subIcon}</span>"></option>

</c:forEach>

				</select>

    		</div>
		</div>
	</h6>
</div>
<kendo:grid name="grid-chan-ad" pageable="true" filterable="true" sortable="true" scrollable="true" reorderable="true" resizable="true" selectable="single" >
	<kendo:grid-excel fileName="채널광고.xlsx" allPages="true" proxyURL="/proxySave"/>
	<kendo:grid-pageable refresh="true" buttonCount="5" pageSize="10" pageSizes="${pageSizesNormal}" />
   	<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />
	<kendo:grid-columns>
		<kendo:grid-column title="시작" field="playBeginDate" width="120" template="<%= playBeginDateTemplate2 %>"/>
		<kendo:grid-column title="종료" field="playEndDate" width="120" template="<%= playEndDateTemplate2 %>"/>
		<kendo:grid-column title="재생시간" field="duration" width="120" template="#= durDisp #" />
		<kendo:grid-column title="광고" field="adName" width="200" />
		<kendo:grid-column title="번호" field="seq" width="120" />
		<kendo:grid-column title="광고 소재" field="creatName" width="200" />
		<kendo:grid-column title="묶음광고" field="adPackIds" width="200" />
		<kendo:grid-column title="생성" field="whoCreationDate" width="120" template="<%= creationDateTemplate %>"/>
		<kendo:grid-column title="힌트" field="hint" width="150" />
		<kendo:grid-column title="광고번호" field="adId" width="120" />
		<kendo:grid-column title="소재번호" field="creatId" width="120" />
		<kendo:grid-column title="광고/소재" field="adCreatId" width="120" />
	</kendo:grid-columns>
	<kendo:grid-filterable>
		<kendo:grid-filterable-messages selectedItemsFormat="{0} 항목 선택됨"/>
	</kendo:grid-filterable>
	<kendo:grid-dataBound>
		<script>
			function grid_dataBound(e) {
		        var grid = this;
		        var hasAdPackData = false;
		        grid.table.find("tr").each(function () {
		            var dataItem = grid.dataItem(this);
		            
	            	if (dataItem.adPackIds) {
	            		hasAdPackData = true;
	            		return false;
	            	}
		        });
				
		        if (!hasAdPackData) {
		        	grid.hideColumn(6);
		        	
		        	return;
		        }

		        grid.showColumn(6);
			}
		</script>
	</kendo:grid-dataBound>
	<kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="playBeginDate" dir="desc"/>
		</kendo:dataSource-sort>
		<kendo:dataSource-transport>
			<kendo:dataSource-transport-read url="${readChanAdUrl}" dataType="json" type="POST" contentType="application/json">
				<kendo:dataSource-transport-read-data>
					<script>
						function additionalData(e) {
							return { reqStrValue1: $("select[name='chan-select']").val() };
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
					<kendo:dataSource-schema-model-field name="playBeginDate" type="date" />
					<kendo:dataSource-schema-model-field name="playEndDate" type="date" />
					<kendo:dataSource-schema-model-field name="realBeginDate" type="date" />
					<kendo:dataSource-schema-model-field name="whoCreationDate" type="date" />
					<kendo:dataSource-schema-model-field name="seq" type="number" />
					<kendo:dataSource-schema-model-field name="adId" type="number" />
					<kendo:dataSource-schema-model-field name="creatId" type="number" />
					<kendo:dataSource-schema-model-field name="adCreatId" type="number" />
				</kendo:dataSource-schema-model-fields>
			</kendo:dataSource-schema-model>
		</kendo:dataSource-schema>
	</kendo:dataSource>
</kendo:grid>
</div>

<!-- / Kendo grid  -->


<!-- Grid button actions  -->

<script>
$(document).ready(function() {
	
	// Excel(채널 광고 grid)
	$("#chan-ad-excel-btn").click(function(e) {
		e.preventDefault();
		
		$("#grid-chan-ad").data("kendoGrid").saveAsExcel();
	});
	// / Excel(채널 광고 grid)

	
	$("select[name='chan-select']").selectpicker('render');

	$("select[name='chan-select']").on("change.bs.select", function(e){

		var grid = $("#grid-chan-ad").data("kendoGrid");
		if (grid.dataSource.page() != 1) {
			grid.dataSource.page(1);
		} else {
			grid.dataSource.read();
		}
	});
	
});

</script>

<!-- / Grid button actions  -->



<c:if test="${syncStat.syncPackMode}">


<!-- Kendo grid  -->

<div class="mb-4">
<div class="card" style="border-bottom: transparent;">
	<h6 class="card-header with-elements pr-2">
		<span class="lead">
			<span class="fa-light fa-tower-broadcast fa-lg"></span>
			<span class="ml-1">묶음 광고 노출</span>
		</span>
		<div class="card-header-elements ml-auto py-0">
			<button type='button' id="chan-ad-rpt-excel-btn" class='btn icon-btn btn-sm btn-outline-secondary'> 
				<span class='fa-light fa-file-excel fa-lg'></span>
			</button>
			<span class="px-2">
 				<span class="fa-solid fa-pipe text-muted"></span>
			</span>
			<div class="float-right  d-flex align-items-center">

				<select name="sp-select" class="selectpicker bg-white" data-style="btn-default" data-none-selected-text="" data-width="250px" >

<c:forEach var="item" items="${SyncPacks}">

		<option value="${item.value}" data-content="<span class='fa-light ${item.icon}'></span><span class='pl-2'>${item.text}</span><span class='small pl-3 opacity-75'>${item.subIcon}</span>"></option>

</c:forEach>

				</select>

    		</div>
		</div>
	</h6>
</div>
<kendo:grid name="grid-chan-ad-rpt" pageable="true" filterable="true" sortable="true" scrollable="true" reorderable="true" resizable="true" selectable="single" >
	<kendo:grid-excel fileName="묶음광고노출.xlsx" allPages="true" proxyURL="/proxySave"/>
	<kendo:grid-pageable refresh="true" buttonCount="5" pageSize="10" pageSizes="${pageSizesNormal}" />
   	<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />
	<kendo:grid-columns>
		<kendo:grid-column title="기기시작" field="realBeginDate" width="120" template="<%= realBeginDateTemplate %>"/>
		<kendo:grid-column title="채널일련번호" field="seq" width="120" />
		<kendo:grid-column title="광고" field="adName" width="200" />
		<kendo:grid-column title="광고 소재" field="creatName" width="200" sortable="false" filterable="false" />
		<kendo:grid-column title="묶음 유형" field="adPackType" width="100" sortable="false" filterable="false" />
		<kendo:grid-column title="화면수" field="scrCount" width="100" />
		<kendo:grid-column title="편성시작" field="playBeginDate" width="120" template="<%= playBeginDateTemplate2 %>"/>
		<kendo:grid-column title="편성비교" field="diff" width="120" template="<%= diffTemplate %>" />
		<kendo:grid-column title="채널" field="chanName" width="200" sortable="false" filterable="false" />
	</kendo:grid-columns>
	<kendo:grid-filterable>
		<kendo:grid-filterable-messages selectedItemsFormat="{0} 항목 선택됨"/>
	</kendo:grid-filterable>
	<kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="realBeginDate" dir="desc"/>
		</kendo:dataSource-sort>
		<kendo:dataSource-transport>
			<kendo:dataSource-transport-read url="${readChanAdRptUrl}" dataType="json" type="POST" contentType="application/json">
				<kendo:dataSource-transport-read-data>
					<script>
						function additionalData(e) {
							return { reqStrValue1: $("select[name='sp-select']").val() };
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
					<kendo:dataSource-schema-model-field name="playBeginDate" type="date" />
					<kendo:dataSource-schema-model-field name="realBeginDate" type="date" />
					<kendo:dataSource-schema-model-field name="seq" type="number" />
					<kendo:dataSource-schema-model-field name="scrCount" type="number" />
				</kendo:dataSource-schema-model-fields>
			</kendo:dataSource-schema-model>
		</kendo:dataSource-schema>
	</kendo:dataSource>
</kendo:grid>
</div>

<!-- / Kendo grid  -->


<!-- Grid button actions  -->

<script>
$(document).ready(function() {
	
	// Excel(묶음 광고 노출 grid)
	$("#chan-ad-rpt-excel-btn").click(function(e) {
		e.preventDefault();
		
		$("#grid-chan-ad-rpt").data("kendoGrid").saveAsExcel();
	});
	// / Excel(묶음 광고 노출 grid)

	
	$("select[name='sp-select']").selectpicker('render');

	$("select[name='sp-select']").on("change.bs.select", function(e){

		var grid = $("#grid-chan-ad-rpt").data("kendoGrid");
		if (grid.dataSource.page() != 1) {
			grid.dataSource.page(1);
		} else {
			grid.dataSource.read();
		}
	});
	
});

function selectCtrlSyncPack(source, shortName) {
	console.log(source);
	console.log(shortName);
}

</script>

<!-- / Grid button actions  -->


</c:if>

	
	</div>
	<div class="tab-pane" id="event-report">
	
	
	
<!-- Kendo grid  -->

<div class="mb-4">
<kendo:grid name="grid-event" pageable="true" filterable="true" sortable="true" scrollable="true" reorderable="true" resizable="true" selectable="${value_gridSelectable}" >
	<kendo:grid-excel fileName="모니터링(이벤트보고).xlsx" allPages="true" proxyURL="/proxySave"/>
	<kendo:grid-pageable refresh="true" buttonCount="5" pageSize="10" pageSizes="${pageSizesNormal}" />
	<kendo:grid-toolbarTemplate>
    	<div class="clearfix">
    		<div class="float-left">
    			<button type="button" class="btn btn-default d-none d-sm-inline k-grid-excel">엑셀</button>
    		</div>
    		<div class="float-right">
    			<button id="delete-event-btn" type="button" class="btn btn-danger">삭제</button>
    		</div>
    	</div>
	</kendo:grid-toolbarTemplate>
   	<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />
	<kendo:grid-columns>
		<kendo:grid-column title="범주" field="category" template="<%= catTemplate %>" width="90" >
			<kendo:grid-column-filterable multi="true" itemTemplate="kfcIconText">
				<kendo:dataSource>
					<kendo:dataSource-transport>
						<kendo:dataSource-transport-read url="${readEventCatUrl}" dataType="json" type="POST" contentType="application/json" />
					</kendo:dataSource-transport>
				</kendo:dataSource>
			</kendo:grid-column-filterable>
		</kendo:grid-column>
		<kendo:grid-column title="유형" field="reportType" template="<%= reportTypeTemplate %>" width="90" >
			<kendo:grid-column-filterable multi="true" itemTemplate="kfcIconText">
				<kendo:dataSource>
					<kendo:dataSource-transport>
						<kendo:dataSource-transport-read url="${readReportTypeUrl}" dataType="json" type="POST" contentType="application/json" />
					</kendo:dataSource-transport>
				</kendo:dataSource>
			</kendo:grid-column-filterable>
		</kendo:grid-column>
		<kendo:grid-column title="등록" field="whoCreationDate" width="150" template="<%= eventRegDateTemplate %>" />
		<kendo:grid-column title="이벤트" field="event" width="120" />
		<kendo:grid-column title="기기" field="equipName" width="250" template="<%= equipNameTemplate %>" />
		<kendo:grid-column title="기기유형" field="equipType" template="<%= equipTypeTemplate %>" width="110" >
			<kendo:grid-column-filterable multi="true" itemTemplate="kfcIconText">
				<kendo:dataSource>
					<kendo:dataSource-transport>
						<kendo:dataSource-transport-read url="${readEquipTypeUrl}" dataType="json" type="POST" contentType="application/json" />
					</kendo:dataSource-transport>
				</kendo:dataSource>
			</kendo:grid-column-filterable>
		</kendo:grid-column>
		<kendo:grid-column title="시작점" field="triggerType" template="<%= triggerTypeTemplate %>" width="120" >
			<kendo:grid-column-filterable multi="true" itemTemplate="kfcIconText">
				<kendo:dataSource>
					<kendo:dataSource-transport>
						<kendo:dataSource-transport-read url="${readTriggerTypeUrl}" dataType="json" type="POST" contentType="application/json" />
					</kendo:dataSource-transport>
				</kendo:dataSource>
			</kendo:grid-column-filterable>
		</kendo:grid-column>
		<kendo:grid-column title="상세내용" field="details" width="250" />
		<kendo:grid-column title="번호" field="id" width="100" />
	</kendo:grid-columns>
	<kendo:grid-filterable>
		<kendo:grid-filterable-messages selectedItemsFormat="{0} 항목 선택됨"/>
	</kendo:grid-filterable>
	<kendo:dataSource serverPaging="false" serverSorting="false" serverFiltering="false" serverGrouping="false" error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="whoCreationDate" dir="desc"/>
		</kendo:dataSource-sort>
       	<kendo:dataSource-filter>
      		<kendo:dataSource-filterItem field="medium.id" operator="eq" logic="and" value="${sessionScope['currMediumId']}" >
      		</kendo:dataSource-filterItem>
  	    </kendo:dataSource-filter>
		<kendo:dataSource-transport>
			<kendo:dataSource-transport-read url="${readEventUrl}" dataType="json" type="POST" contentType="application/json"/>
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
					<kendo:dataSource-schema-model-field name="id" type="number" />
				</kendo:dataSource-schema-model-fields>
			</kendo:dataSource-schema-model>
		</kendo:dataSource-schema>
	</kendo:dataSource>
</kendo:grid>
</div>

<!-- / Kendo grid  -->


<!-- Grid button actions  -->

<script>
$(document).ready(function() {

	// Delete
	$("#delete-event-btn").click(function(e) {
		e.preventDefault();
			
		var grid = $("#grid-event").data("kendoGrid");
		var rows = grid.select();
	
		var delRows = [];
		
		rows.each(function(index, row) {
			var selectedItem = grid.dataItem(row);
			delRows.push(selectedItem.id);
		});
		
		if (delRows.length > 0) {
			showDelConfirmModal(function(result) {
				if (result) {
					$.ajax({
						type: "POST",
						contentType: "application/json",
						dataType: "json",
						url: "${destroyEventUrl}",
						data: JSON.stringify({ items: delRows }),
						success: function (form) {
        					showDeleteSuccessMsg();
							grid.dataSource.read();
						},
						error: ajaxDeleteError
					});
				}
			}, true, delRows.length);
		}
	});
	// / Delete
	
});
</script>

<!-- / Grid button actions  -->

	
	</div>
</div>


<script>
$(document).ready(function() {

<c:choose>
<c:when test="${not empty screenID}">

	$('a[href="#ad-sel-log"]').tab('show');
	$("#api-screen-ID").val("${screenID}");

	var grid = $("#grid-api").data("kendoGrid");
	grid.dataSource.read();

</c:when>
<c:when test="${syncStat.syncPackMode}">

	$('#active-sync-pack-tab-a').addClass('active'); 
	$('#active-sync-pack').addClass('active'); 

</c:when>
<c:otherwise>

	$('#active-screen-tab-a').addClass('active'); 
	$('#active-screen').addClass('active'); 

</c:otherwise>
</c:choose>

});
</script>


<!--  Forms -->


<style>

/* 선택 체크박스를 포함하는 필터 패널을 보기 좋게 */
.k-filter-selected-items {
	font-weight: 500;
	margin: 0.5em 0;
}
.k-filter-menu .k-button {
	width: 47%;
	margin: 0.5em 1% 0.25em;
}


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


/* 그리드 행의 높이 지정 */
.k-grid tbody tr, .k-grid tbody tr td
{
    height: 40px;
}

</style>


<!--  / Forms -->


<!-- / Page body -->





<!-- Functional tags -->

<func:screenInfoModal />
<func:scrLocModal />
<func:cmmValidate />


<!-- Closing tags -->

<common:base />
<common:pageClosing />
