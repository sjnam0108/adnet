<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/rev/camprpt/read" var="readUrl" />
<c:url value="/rev/camprpt/readCamp" var="readCampUrl" />
<c:url value="/rev/camprpt/readAd" var="readAdUrl" />

<c:url value="/rev/camprpt/readCampStatuses" var="readCampStatusUrl" />
<c:url value="/rev/camprpt/readAdStatuses" var="readAdStatusUrl" />
<c:url value="/rev/camprpt/readAdPurchTypes" var="readAdPurchTypeUrl" />
<c:url value="/rev/camprpt/readAdGoalTypes" var="readAdGoalTypeUrl" />
<c:url value="/rev/camprpt/readImpDailyTypes" var="readImpDailyTypeUrl" />
<c:url value="/rev/camprpt/readImpHourlyTypes" var="readImpHourlyTypeUrl" />


<!-- Opening tags -->

<common:mediumPageOpening />


<!-- Page title -->

<h4 class="pt-1 pb-3 mb-3">
	<span class="mr-1 ${sessionScope['loginUser'].icon}"></span>
	<!-- span class="mr-1 ${sessionScope['loginUser'].icon} fa-beat text-blue" style="--fa-animation-iteration-count: 2;"></span-->
	${pageTitle}
</h4>





<!-- Page body -->


<!-- Java(optional)  -->

<%
	String advertiserTemplate = "<span>#= advertiser #</span>";
	String statusTemplate =
			"# if (status == 'U') { #" +
				"<span class='fa-regular fa-alarm-clock'></span><span class='pl-2'>시작전</span>" +
			"# } else if (status == 'R') { #" +
				"<span class='fa-regular fa-bolt-lightning text-orange'></span><span class='pl-2'>진행</span>" +
			"# } else if (status == 'C') { #" +
				"<span class='fa-regular fa-flag-checkered'></span><span class='pl-2'>완료</span>" +
			"# } else { #" +
				"<span>-</span>" +
			"# } #";
			
	String startDateTemplate = kr.adnetwork.utils.Util.getSmartDate("startDate", false, false);
	String endDateTemplate = kr.adnetwork.utils.Util.getSmartDate("endDate", false, false);

	String noRecordsTemplate =
			"<div class='container text-center my-4'>" +
				"<div class='d-flex justify-content-center align-self-center'>" +
					"<span class='fa-thin fa-hexagon-exclamation fa-3x'></span>" +
					"<span class='pl-3 mt-2' style='font-weight: 300; font-size: 1.3rem;'>해당 자료 없음</span>" +
				"</div>" +
			"</div>";
			
	String nameTemplate =
			"<div>" +
				"<a href='javascript:navToRpt(#= id #)'><span class='text-link'>#= name #</span></a>" + 
				"# if (tgtTodayDisp) { #" +
					"<span data-toggle='tooltip' data-placement='right' title=\"#= tgtTodayDisp #\">" +
						"<span class='pl-1'><span class='fa-regular fa-golf-flag-hole text-info'></span></span>" +
					"</span>" +
				"# } #" +
			"</div>";
	String goalTypeTemplate =
			"# if (goalType == 'A') { #" +
				"<span title='광고 예산'><span class='fa-regular fa-sack-dollar fa-fw'></span>" +
			"# } else if (goalType == 'I') { #" +
				"<span title='노출량'><span class='fa-regular fa-eye fa-fw'></span>" +
			"# } else if (goalType == 'U') { #" +
				"<span title='무제한 노출'><span class='fa-regular fa-infinity fa-fw'></span>" +
			"# } else if (goalType == 'M') { #" +
				"<span title='여러 방법'><span class='fa-regular fa-question fa-fw'></span>" +
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
			"# } else if (impHourlyType == 'A') { #" +
				"<span title='포함된 광고 설정에 따름'><span class='fa-regular fa-audio-description fa-fw'></span>" +
			"# } else { #" +
				"<span>-</span>" +
			"# } #";

			
	String adNameTemplate =
			"<div>" + 
				"# if (paused == true) { #" +
					"<span title='잠시 멈춤'><span class='fa-regular fa-circle-pause text-danger'></span></span><span class='pr-1'></span>" +
				"# } #" +
				"# if (status == 'A' || status == 'R') { #" +
					"# if (impAddRatio > 0) { #" +
						"<span title='현재 노출량 추가 제어'><span class='badge badge-pill badge-success'><small>#= impAddRatioDisp #</small></span></span>" +
					"# } else if (impAddRatio < 0) { #" +
						"<span title='현재 노출량 추가 제어'><span class='badge badge-pill badge-danger'><small>#= impAddRatioDisp #</small></span></span>" +
					"# } # " +
				"# } # " +
				"<a href='javascript:navToRptAd(#= campaignId #, #= id #)'><span class='text-link'>#= name #</span></a>" + 
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
			"</div>";
	String adStatusTemplate =
			"# if (status == 'D') { #" +
				"<span class='fa-regular fa-asterisk fa-fw'></span><span class='pl-1'>준비</span>" +
			"# } else if (status == 'P') { #" +
				"<span class='fa-regular fa-square-question fa-fw'></span><span class='pl-1'>승인대기</span>" +
			"# } else if (status == 'J') { #" +
				"<span class='fa-regular fa-do-not-enter fa-fw'></span><span class='pl-1'>거절</span>" +
			"# } else if (status == 'A') { #" +
				"<span class='fa-regular fa-alarm-clock fa-fw'></span><span class='pl-1'>예약</span>" +
			"# } else if (status == 'R') { #" +
				"<span class='fa-regular fa-bolt-lightning text-orange fa-fw'></span><span class='pl-1'>진행</span>" +
			"# } else if (status == 'C') { #" +
				"<span class='fa-regular fa-flag-checkered fa-fw'></span><span class='pl-1'>완료</span>" +
			"# } else if (status == 'V') { #" +
				"<span class='fa-regular fa-box-archive fa-fw'></span><span class='pl-1'>보관</span>" +
			"# } else { #" +
				"<span>-</span>" +
			"# } #";
	String purchTypeTemplate =
			"# if (purchType == 'G') { #" +
				"<span class='fa-regular fa-hexagon-check text-blue fa-fw'></span><span class='pl-1'>목표 보장.#= priority #</span>" +
			"# } else if (purchType == 'N') { #" +
				"<span class='fa-regular fa-hexagon-exclamation fa-fw'></span><span class='pl-1'>목표 비보장.#= priority #</span>" +
			"# } else if (purchType == 'H') { #" +
				"<span class='fa-regular fa-house fa-fw'></span><span class='pl-1'>하우스 광고</span>" +
			"# } else { #" +
				"<span>-</span>" +
			"# } #";
	String adGoalTypeTemplate =
			"# if (purchType == 'H') { #" +
				"<span></span>" +
			"# } else if (goalType == 'A') { #" +
				"<span title='광고 예산'><span class='fa-regular fa-sack-dollar fa-fw'></span><span class='pl-1'></span>" +
			"# } else if (goalType == 'I') { #" +
				"<span title='노출량'><span class='fa-regular fa-eye fa-fw'></span><span class='pl-1'></span>" +
			"# } else if (goalType == 'U') { #" +
				"<span title='무제한 노출'><span class='fa-regular fa-infinity fa-fw'></span><span class='pl-1'></span>" +
			"# } else { #" +
				"<span>-</span>" +
			"# } #";
	String budgetTemplate =
			"# if (purchType == 'H') { #" +
				"<span></span>" +
			"# } else { #" +
				"<span>#= kendo.format('{0:n0}', budget) #</span>" +
			"# } #";
	String goalValueTemplate =
			"# if (purchType == 'H') { #" +
				"<span></span>" +
			"# } else { #" +
				"<span>#= kendo.format('{0:n0}', goalValue) #</span>" +
			"# } #";
	String sysValueTemplate =
			"# if (purchType == 'H') { #" +
				"<span></span>" +
			"# } else if (proposedSysValue > 0) { #" +
				"<span class=\"text-muted\">#= kendo.format('{0:n0}', proposedSysValue) #</span>" +
				"<span class='pl-1' title='자동 설정'><span class='fa-regular fa-asterisk text-info fa-2xs'></span></span>" +
			"# } else { #" +
				"<span>#= kendo.format('{0:n0}', sysValue) #</span>" +
			"# } #";
	String actualAmountTemplate = "<span>#= kendo.format('{0:n0}', actualAmount) #</span>";
	String totalTemplate = "<span>#= kendo.format('{0:n0}', actualValue) #</span>";
	String actualCpmTemplate = "<span>#= kendo.format('{0:n0}', actualCpm) #</span>";
	String achvRatioTemplate =
			"# if (achvRatio == 0) { #" +
				"<span>-</span>" +
			"# } else if (purchType == 'H') { #" +
				"<span></span>" +
			"# } else { #" +
				"<span>#= kendo.format('{0:n2}', achvRatio) #</span>" +
			"# } #";

	String campSysValueTemplate =
			"# if (selfManaged && proposedSysValue > 0) { #" +
				"<span class=\"text-muted\">#= kendo.format('{0:n0}', proposedSysValue) #</span>" +
				"<span class='pl-1' title='자동 설정'><span class='fa-regular fa-asterisk text-info fa-2xs'></span></span>" +
			"# } else { #" +
				"<span>#= kendo.format('{0:n0}', sysValue) #</span>" +
			"# } #";
			
%>


<!--  Forms -->

<ul class="nav nav-tabs tabs-alt container-p-x container-m--x mb-4">
	<li class="nav-item">
		<a class="nav-link active" data-toggle="tab" href="#progressing">
			<i class="mr-1 fa-light fa-bolt-lightning"></i>
			진행 중
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" data-toggle="tab" href="#camp">
			<i class="mr-1 fa-light fa-briefcase"></i>
			캠페인
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" data-toggle="tab" href="#ad">
			<i class="mr-1 fa-light fa-audio-description"></i>
			광고
		</a>
	</li>
</ul>

<div class="tab-content">
	<div class="tab-pane active" id="progressing">
	
	
	
	

<!-- Kendo grid  -->

<div class="mb-4">
<kendo:grid name="grid" pageable="true" filterable="true" scrollable="true" resizable="true">
	<kendo:grid-sortable mode="mixed" showIndexes="true"/>
    <kendo:grid-selectable mode="multiple, row"/>
	<kendo:grid-excel fileName="리포트(진행중).xlsx" allPages="true" proxyURL="/proxySave"/>
	<kendo:grid-pageable refresh="true" buttonCount="5" pageSize="10" pageSizes="${pageSizesNormal}" />
	<kendo:grid-toolbarTemplate>
    	<div class="clearfix">
    		<div class="float-left">
    			<button type="button" class="btn btn-default d-none d-sm-inline k-grid-excel">엑셀</button>
    		</div>
    		<div class="float-right">
    		</div>
    	</div>
	</kendo:grid-toolbarTemplate>
	<kendo:grid-filterable extra="false" />

	<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />
	<kendo:grid-columns>
		<kendo:grid-column title="캠페인명" field="name" width="250" template="<%= nameTemplate %>" sticky="true" />
		<kendo:grid-column title="예산" field="budget" width="120" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="보장" field="goalValue" width="120" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="목표" field="sysValue" width="120" template="<%= campSysValueTemplate %>" filterable="false" />
		<kendo:grid-column title="노출량" field="actualValue" width="100" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="집행금액" field="actualAmount" width="120" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="집행CPM" field="actualCpm" width="120" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="달성률(%)" field="achvRatio" width="120" format="{0:n2}" filterable="false" />
		<kendo:grid-column title="집행방법" field="goalType" width="100" template="<%= goalTypeTemplate %>" filterable="false" />
		<kendo:grid-column title="일별 분산" field="impDailyType" width="100" template="<%= impDailyTypeTemplate %>" filterable="false" />
		<kendo:grid-column title="자체 목표" field="selfManaged" width="120"
				template="#=selfManaged ? \"<span class='fa-light fa-check'>\" : \"\"#" filterable="false" />

		<kendo:grid-column title="시작일" field="startDate" width="120" template="<%= startDateTemplate %>" />
		<kendo:grid-column title="종료일" field="endDate" width="120" template="<%= endDateTemplate %>" />
		<kendo:grid-column title="광고 수" width="80" filterable="false" sortable="false"
				template="<span>#= adCount #</span>" />
		<kendo:grid-column title="광고주" field="advertiser" width="200" template="<%= advertiserTemplate %>" />
	</kendo:grid-columns>
	<kendo:grid-dataBound>
		<script>
			function grid_dataBound(e) {
				$('[data-toggle="tooltip"]').tooltip();
			}
		</script>
	</kendo:grid-dataBound>
	<kendo:grid-filterable>
		<kendo:grid-filterable-messages selectedItemsFormat="{0} 항목 선택됨"/>
	</kendo:grid-filterable>
	<kendo:dataSource serverPaging="false" serverSorting="false" serverFiltering="false" serverGrouping="false" error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="name" dir="asc"/>
		</kendo:dataSource-sort>
		<kendo:dataSource-transport>
			<kendo:dataSource-transport-read url="${readUrl}" dataType="json" type="POST" contentType="application/json"/>
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
					<kendo:dataSource-schema-model-field name="startDate" type="date" />
					<kendo:dataSource-schema-model-field name="endDate" type="date" />
				</kendo:dataSource-schema-model-fields>
			</kendo:dataSource-schema-model>
		</kendo:dataSource-schema>
	</kendo:dataSource>
</kendo:grid>
</div>

<!-- / Kendo grid  -->
	



	
	</div>
	<div class="tab-pane" id="camp">
	
	
	
	

<!-- Kendo grid  -->

<div class="mb-4">
<kendo:grid name="grid-camp" pageable="true" filterable="true" scrollable="true" resizable="true">
	<kendo:grid-sortable mode="mixed" showIndexes="true"/>
    <kendo:grid-selectable mode="multiple, row"/>
	<kendo:grid-excel fileName="리포트(캠페인).xlsx" allPages="true" proxyURL="/proxySave"/>
	<kendo:grid-pageable refresh="true" buttonCount="5" pageSize="10" pageSizes="${pageSizesNormal}" />
	<kendo:grid-toolbarTemplate>
    	<div class="clearfix">
    		<div class="float-left">
    			<button type="button" class="btn btn-default d-none d-sm-inline k-grid-excel">엑셀</button>
    		</div>
    		<div class="float-right">
    		</div>
    	</div>
	</kendo:grid-toolbarTemplate>
	<kendo:grid-filterable extra="false" />

	<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />
	<kendo:grid-columns>
		<kendo:grid-column title="캠페인명" field="name" width="250" template="<%= nameTemplate %>" sticky="true" />
		<kendo:grid-column title="상태" field="status" width="120" template="<%= statusTemplate %>" >
			<kendo:grid-column-filterable multi="true" itemTemplate="kfcIconText">
				<kendo:dataSource>
					<kendo:dataSource-transport>
						<kendo:dataSource-transport-read url="${readCampStatusUrl}" dataType="json" type="POST" contentType="application/json" />
					</kendo:dataSource-transport>
				</kendo:dataSource>
			</kendo:grid-column-filterable>
		</kendo:grid-column>
		<kendo:grid-column title="예산" field="budget" width="120" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="보장" field="goalValue" width="120" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="목표" field="sysValue" width="120" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="노출량" field="actualValue" width="100" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="집행금액" field="actualAmount" width="120" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="집행CPM" field="actualCpm" width="120" format="{0:n0}" filterable="false" />
		<kendo:grid-column title="달성률(%)" field="achvRatio" width="120" format="{0:n2}" filterable="false" />
		<kendo:grid-column title="집행방법" field="goalType" width="100" template="<%= goalTypeTemplate %>" filterable="false" />
		<kendo:grid-column title="일별 분산" field="impDailyType" width="100" template="<%= impDailyTypeTemplate %>" filterable="false" />
		<kendo:grid-column title="자체 목표" field="selfManaged" width="120"
				template="#=selfManaged ? \"<span class='fa-light fa-check'>\" : \"\"#" filterable="false" />

		<kendo:grid-column title="시작일" field="startDate" width="120" template="<%= startDateTemplate %>" />
		<kendo:grid-column title="종료일" field="endDate" width="120" template="<%= endDateTemplate %>" />
		<kendo:grid-column title="광고 수" width="80" filterable="false" sortable="false"
				template="<span>#= adCount #</span>" />
		<kendo:grid-column title="광고주" field="advertiser" width="200" template="<%= advertiserTemplate %>" />
	</kendo:grid-columns>
	<kendo:grid-filterable>
		<kendo:grid-filterable-messages selectedItemsFormat="{0} 항목 선택됨"/>
	</kendo:grid-filterable>
	<kendo:dataSource serverPaging="false" serverSorting="false" serverFiltering="false" serverGrouping="false" error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="startDate" dir="desc"/>
		</kendo:dataSource-sort>
		<kendo:dataSource-transport>
			<kendo:dataSource-transport-read url="${readCampUrl}" dataType="json" type="POST" contentType="application/json"/>
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
					<kendo:dataSource-schema-model-field name="startDate" type="date" />
					<kendo:dataSource-schema-model-field name="endDate" type="date" />
				</kendo:dataSource-schema-model-fields>
			</kendo:dataSource-schema-model>
		</kendo:dataSource-schema>
	</kendo:dataSource>
</kendo:grid>
</div>

<!-- / Kendo grid  -->
	



	
	</div>
	<div class="tab-pane" id="ad">
	
	
	
	

<!-- Kendo grid  -->

<div class="mb-4">
<kendo:grid name="grid-ad" pageable="true" filterable="true" scrollable="true" resizable="true">
	<kendo:grid-sortable mode="mixed" showIndexes="true"/>
    <kendo:grid-selectable mode="multiple, row"/>
	<kendo:grid-excel fileName="리포트(광고).xlsx" allPages="true" proxyURL="/proxySave"/>
	<kendo:grid-pageable refresh="true" buttonCount="5" pageSize="10" pageSizes="${pageSizesNormal}" />
	<kendo:grid-toolbarTemplate>
    	<div class="clearfix">
    		<div class="float-left">
    			<button type="button" class="btn btn-default d-none d-sm-inline k-grid-excel">엑셀</button>
    		</div>
    		<div class="float-right">
    		</div>
    	</div>
	</kendo:grid-toolbarTemplate>
	<kendo:grid-filterable extra="false" />

	<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />
	<kendo:grid-columns>
		<kendo:grid-column title="광고명" field="name" width="300" template="<%= adNameTemplate %>" sticky="true" />
		<kendo:grid-column title="상태" field="status" width="120" template="<%= adStatusTemplate %>" >
			<kendo:grid-column-filterable multi="true" itemTemplate="kfcIconText">
				<kendo:dataSource>
					<kendo:dataSource-transport>
						<kendo:dataSource-transport-read url="${readAdStatusUrl}" dataType="json" type="POST" contentType="application/json" />
					</kendo:dataSource-transport>
				</kendo:dataSource>
			</kendo:grid-column-filterable>
		</kendo:grid-column>
		<kendo:grid-column title="구매 유형" field="purchType" width="130" template="<%= purchTypeTemplate %>" >
			<kendo:grid-column-filterable multi="true" itemTemplate="kfcIconText">
				<kendo:dataSource>
					<kendo:dataSource-transport>
						<kendo:dataSource-transport-read url="${readAdPurchTypeUrl}" dataType="json" type="POST" contentType="application/json" />
					</kendo:dataSource-transport>
				</kendo:dataSource>
			</kendo:grid-column-filterable>
		</kendo:grid-column>
		<kendo:grid-column title="예산" field="budget" width="120" template="<%= budgetTemplate %>" />
		<kendo:grid-column title="보장" field="goalValue" width="120" template="<%= goalValueTemplate %>" />
		<kendo:grid-column title="목표" field="sysValue" width="120" template="<%= sysValueTemplate %>" />
		<kendo:grid-column title="노출량" field="total" width="100" template="<%= totalTemplate %>" filterable="false" />
		<kendo:grid-column title="집행금액" field="actualAmount" width="120" template="<%= actualAmountTemplate %>" filterable="false" />
		<kendo:grid-column title="집행CPM" field="actualCpm" width="120" template="<%= actualCpmTemplate %>" filterable="false" />
		<kendo:grid-column title="달성률(%)" field="achvRatio" width="120" template="<%= achvRatioTemplate %>" filterable="false" />
		<kendo:grid-column title="집행방법" field="goalType" width="120" template="<%= adGoalTypeTemplate %>" >
			<kendo:grid-column-filterable multi="true" itemTemplate="kfcIconText">
				<kendo:dataSource>
					<kendo:dataSource-transport>
						<kendo:dataSource-transport-read url="${readAdGoalTypeUrl}" dataType="json" type="POST" contentType="application/json" />
					</kendo:dataSource-transport>
				</kendo:dataSource>
			</kendo:grid-column-filterable>
		</kendo:grid-column>
		<kendo:grid-column title="일별 분산" field="impDailyType" width="120" template="<%= impDailyTypeTemplate %>" >
			<kendo:grid-column-filterable multi="true" itemTemplate="kfcIconText">
				<kendo:dataSource>
					<kendo:dataSource-transport>
						<kendo:dataSource-transport-read url="${readImpDailyTypeUrl}" dataType="json" type="POST" contentType="application/json" />
					</kendo:dataSource-transport>
				</kendo:dataSource>
			</kendo:grid-column-filterable>
		</kendo:grid-column>
		<kendo:grid-column title="하루 분산" field="impHourlyType" width="120" template="<%= impHourlyTypeTemplate %>" >
			<kendo:grid-column-filterable multi="true" itemTemplate="kfcIconText">
				<kendo:dataSource>
					<kendo:dataSource-transport>
						<kendo:dataSource-transport-read url="${readImpHourlyTypeUrl}" dataType="json" type="POST" contentType="application/json" />
					</kendo:dataSource-transport>
				</kendo:dataSource>
			</kendo:grid-column-filterable>
		</kendo:grid-column>
		<kendo:grid-column title="시작일" field="startDate" width="120" template="<%= startDateTemplate %>" />
		<kendo:grid-column title="종료일" field="endDate" width="120" template="<%= endDateTemplate %>" />
	</kendo:grid-columns>
	<kendo:grid-dataBound>
		<script>
			function grid_dataBound(e) {
				$('[data-toggle="tooltip"]').tooltip();
			}
		</script>
	</kendo:grid-dataBound>
	<kendo:grid-filterable>
		<kendo:grid-filterable-messages selectedItemsFormat="{0} 항목 선택됨"/>
	</kendo:grid-filterable>
	<kendo:dataSource serverPaging="false" serverSorting="false" serverFiltering="false" serverGrouping="false" error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="startDate" dir="desc"/>
		</kendo:dataSource-sort>
		<kendo:dataSource-transport>
			<kendo:dataSource-transport-read url="${readAdUrl}" dataType="json" type="POST" contentType="application/json"/>
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
					<kendo:dataSource-schema-model-field name="cpm" type="number" />
					<kendo:dataSource-schema-model-field name="budget" type="number" />
					<kendo:dataSource-schema-model-field name="goalValue" type="number" />
					<kendo:dataSource-schema-model-field name="sysValue" type="number" />
					<kendo:dataSource-schema-model-field name="dailyCap" type="number" />
					<kendo:dataSource-schema-model-field name="dailyScrCap" type="number" />
					<kendo:dataSource-schema-model-field name="priority" type="number" />
					<kendo:dataSource-schema-model-field name="duration" type="number" />
					<kendo:dataSource-schema-model-field name="paused" type="boolean" />
					<kendo:dataSource-schema-model-field name="startDate" type="date" />
					<kendo:dataSource-schema-model-field name="endDate" type="date" />
					<kendo:dataSource-schema-model-field name="whoCreationDate" type="date" />
					<kendo:dataSource-schema-model-field name="whoLastUpdateDate" type="date" />
				</kendo:dataSource-schema-model-fields>
			</kendo:dataSource-schema-model>
		</kendo:dataSource-schema>
	</kendo:dataSource>
</kendo:grid>
</div>

<!-- / Kendo grid  -->
	



	
	



	
	</div>
</div>


<!--  Forms -->


<style>







/* 그리드 행의 높이 지정 */
.k-grid tbody tr, .k-grid tbody tr td
{
    height: 40px;
}

</style>


<!--  / Forms -->


<!--  Scripts -->

<script>

function navToRpt(campId) {
	var path = "/rev/camprpt/ad/" + campId;
	location.href = path;
}


function navToRptAd(campId, adId) {
	var path = "/rev/camprpt/" + campId + "?filter=" + adId;
	location.href = path;
}

</script>

<!--  / Scripts -->


<!-- / Page body -->





<!-- Functional tags -->

<func:cmmValidate />


<!-- Closing tags -->

<common:base />
<common:pageClosing />
