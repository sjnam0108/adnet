<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>

<%@ taglib prefix="rev" tagdir="/WEB-INF/tags/rev"%>


<!-- URL -->

<c:url value="/rev/mediumrpt/screen/readScrTotOvw" var="readScrTotOvwUrl" />
<c:url value="/rev/mediumrpt/screen/readScrTot" var="readScrTotUrl" />
<c:url value="/rev/mediumrpt/screen/readScrFailTot" var="readScrFailTotUrl" />
<c:url value="/rev/mediumrpt/screen/readScrNoAdTot" var="readScrNoAdTotUrl" />
<c:url value="/rev/mediumrpt/screen/readScrFbTot" var="readScrFbTotUrl" />


<!-- Opening tags -->

<common:mediumPageOpening />


<!-- Page title -->

<h4 class="pt-1 pb-3 mb-3">
	<span class="text-muted font-weight-light">${pageTitle}<span class="px-2">/</span></span>
	<span class="mr-1 fa-light fa-screen-users"></span><span class="pl-1">화면</span>
</h4>

<hr class="border-light container-m--x mt-0 mb-3">






<!-- Page body -->


<!--  Overview header -->

<rev:medium />


<!--  Scripts -->

<script>

function navigateToDate(date) {
	
	showWaitModal();
	location.href = "/rev/mediumrpt/screen?date=" + date;
}

</script>

<!--  / Overview header -->


<!--  Tab -->
<ul class="nav nav-tabs tabs-alt mb-4 mt-4">
	<li class="nav-item">
		<a class="nav-link" href="javascript:navigateToSummary();">
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
		<a class="nav-link active" href="javascript:navigateToScreen();">
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


<!-- Java(optional)  -->

<%
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
	
	String screenNameTemplate2 =
			"# if (deleted == true) { #" +
				"<span title='삭제 처리'><span class='fa-regular fa-trash-can text-danger fa-fw'></span></span>" +
				"<span class='pl-1'>#= name #</span>" +
			"# } else { #" +
				"<div class='d-flex align-items-center'>" +
					"# if (reqStatus == '6') { #" +
						"<span title='10분내 요청'><span class='fa-solid fa-flag-swallowtail fa-fw text-blue'></span></span>" +
					"# } else if (reqStatus == '5') { #" +
						"<span title='1시간내 요청'><span class='fa-solid fa-flag-swallowtail fa-fw text-green'></span></span>" +
					"# } else if (reqStatus == '4') { #" +
						"<span title='6시간내 요청'><span class='fa-solid fa-flag-swallowtail fa-fw text-yellow'></span></span>" +
					"# } else if (reqStatus == '3') { #" +
						"<span title='24시간내 요청'><span class='fa-solid fa-flag-swallowtail fa-fw text-orange'></span></span>" +
					"# } else if (reqStatus == '1') { #" +
						"<span title='24시간내 없음'><span class='fa-solid fa-flag-pennant fa-fw text-danger'></span></span>" +
					"# } else if (reqStatus == '0') { #" +
						"<span title='기록 없음'><span class='fa-solid fa-flag-pennant fa-fw text-secondary'></span></span>" +
					"# } #" +
					"<span class='pl-1'>#= name #</span>" +
					"<a href='javascript:showScreen(#= id #,\"#= name #\")' class='btn btn-default btn-xs icon-btn ml-1'><span class='fas fa-search text-info'></span></a>" +
				"</div>" +
			"# } #";
	
	String noRecordsTemplate =
			"<div class='container text-center my-4'>" +
				"<div class='d-flex justify-content-center align-self-center'>" +
					"<span class='fa-thin fa-hexagon-exclamation fa-3x'></span>" +
					"<span class='pl-3 mt-2' style='font-weight: 300; font-size: 1.3rem;'>해당 자료 없음</span>" +
				"</div>" +
			"</div>";
%>

<!-- Kendo grid  -->

<div class="mb-4">
<div class="card" style="border-bottom: transparent;">
	<h6 class="card-header with-elements py-2">
		<span class="lead">
			<span class="fa-light fa-ballot fa-lg"></span>
			<span class="ml-1">요약</span>
		</span>
		<div class="card-header-elements ml-auto">
			<button type='button' id="excel-ovw-btn" class='btn icon-btn btn-sm btn-outline-secondary'> 
				<span class='fa-light fa-file-excel fa-lg'></span>
			</button>
		</div>
	</h6>
</div>
<kendo:grid name="ovw-grid" pageable="true" filterable="true" sortable="true" scrollable="true" reorderable="true" resizable="true">
    <kendo:grid-selectable mode="multiple, raw"/>
	<kendo:grid-excel fileName="매체리포트(화면요약).xlsx" allPages="true" proxyURL="/proxySave"/>
	<kendo:grid-pageable refresh="true" buttonCount="5" pageSize="10" pageSizes="${pageSizesNormal}" />
   	<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />
	<kendo:grid-columns>
		<kendo:grid-column title="화면" field="name" width="250" template="<%= screenNameTemplate2 %>" />
		<kendo:grid-column title="성공" field="succTotal" format="{0:n0}" />
		<kendo:grid-column title="실패" field="failTotal" format="{0:n0}" />
		<kendo:grid-column title="대체광고" field="fbTotal" format="{0:n0}" />
		<kendo:grid-column title="광고없음" field="noAdTotal" format="{0:n0}" />
		<kendo:grid-column title="합계" field="total" format="{0:n0}" />
	</kendo:grid-columns>
	<kendo:grid-filterable>
		<kendo:grid-filterable-messages selectedItemsFormat="{0} 항목 선택됨"/>
	</kendo:grid-filterable>
	<kendo:dataSource serverPaging="false" serverSorting="false" serverFiltering="false" serverGrouping="false" error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="name" dir="asc"/>
		</kendo:dataSource-sort>
		<kendo:dataSource-transport>
			<kendo:dataSource-transport-read url="${readScrTotOvwUrl}" dataType="json" type="POST" contentType="application/json">
				<kendo:dataSource-transport-read-data>
					<script>
						function additionalData(e) {
							return { reqStrValue1:  "${currDate}" };
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
					<kendo:dataSource-schema-model-field name="succTotal" type="number" />
					<kendo:dataSource-schema-model-field name="failTotal" type="number" />
					<kendo:dataSource-schema-model-field name="fbTotal" type="number" />
					<kendo:dataSource-schema-model-field name="noAdTotal" type="number" />
					<kendo:dataSource-schema-model-field name="total" type="number" />
				</kendo:dataSource-schema-model-fields>
			</kendo:dataSource-schema-model>
		</kendo:dataSource-schema>
	</kendo:dataSource>
</kendo:grid>
</div>


<div class="mb-4">
<div class="card" style="border-bottom: transparent;">
	<h6 class="card-header with-elements py-2">
		<span class="lead">
			<span class="fa-light fa-flag-checkered fa-lg"></span>
			<span class="ml-1">성공</span>
		</span>
		<div class="card-header-elements ml-auto">
			<button type='button' id="excel-total-btn" class='btn icon-btn btn-sm btn-outline-secondary'> 
				<span class='fa-light fa-file-excel fa-lg'></span>
			</button>
		</div>
	</h6>
</div>
<kendo:grid name="total-grid" pageable="true" filterable="true" sortable="true" scrollable="true" reorderable="true" resizable="true">
    <kendo:grid-selectable mode="multiple, raw"/>
	<kendo:grid-excel fileName="매체리포트(화면성공).xlsx" allPages="true" proxyURL="/proxySave"/>
	<kendo:grid-pageable refresh="true" buttonCount="5" pageSize="10" pageSizes="${pageSizesNormal}" />
   	<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />
	<kendo:grid-columns>
		<kendo:grid-column title="화면" field="screen.name" width="250" template="<%= screenNameTemplate %>" />
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
		<kendo:grid-column title="광고수" field="adCount" width="120" filterable="false" />
	</kendo:grid-columns>
	<kendo:grid-filterable>
		<kendo:grid-filterable-messages selectedItemsFormat="{0} 항목 선택됨"/>
	</kendo:grid-filterable>
	<kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="succTotal" dir="desc"/>
		</kendo:dataSource-sort>
       	<kendo:dataSource-filter>
   			<kendo:dataSource-filterItem field="medium.id" operator="eq" logic="and" value="${sessionScope['currMediumId']}" >
   			</kendo:dataSource-filterItem>
    	</kendo:dataSource-filter>
		<kendo:dataSource-transport>
			<kendo:dataSource-transport-read url="${readScrTotUrl}" dataType="json" type="POST" contentType="application/json">
				<kendo:dataSource-transport-read-data>
					<script>
						function additionalData(e) {
							return { reqStrValue1:  "${currDate}" };
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
					<kendo:dataSource-schema-model-field name="succTotal" type="number" />
				</kendo:dataSource-schema-model-fields>
			</kendo:dataSource-schema-model>
		</kendo:dataSource-schema>
	</kendo:dataSource>
</kendo:grid>
</div>


<div class="mb-4">
<div class="card" style="border-bottom: transparent;">
	<h6 class="card-header with-elements py-2">
		<span class="lead">
			<span class="fa-light fa-cloud-question fa-lg"></span>
			<span class="ml-1">실패</span>
		</span>
		<div class="card-header-elements ml-auto">
			<button type='button' id="excel-fail-btn" class='btn icon-btn btn-sm btn-outline-secondary'> 
				<span class='fa-light fa-file-excel fa-lg'></span>
			</button>
		</div>
	</h6>
</div>
<kendo:grid name="fail-grid" pageable="true" filterable="true" sortable="true" scrollable="true" reorderable="true" resizable="true">
    <kendo:grid-selectable mode="multiple, raw"/>
	<kendo:grid-excel fileName="매체리포트(화면실패).xlsx" allPages="true" proxyURL="/proxySave"/>
	<kendo:grid-pageable refresh="true" buttonCount="5" pageSize="10" pageSizes="${pageSizesNormal}" />
   	<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />
	<kendo:grid-columns>
		<kendo:grid-column title="화면" field="screen.name" width="250" template="<%= screenNameTemplate %>" />
		<kendo:grid-column title="합계" field="dateTotal" width="100" />
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
	</kendo:grid-columns>
	<kendo:grid-filterable>
		<kendo:grid-filterable-messages selectedItemsFormat="{0} 항목 선택됨"/>
	</kendo:grid-filterable>
	<kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="dateTotal" dir="desc"/>
		</kendo:dataSource-sort>
       	<kendo:dataSource-filter>
   			<kendo:dataSource-filterItem field="medium.id" operator="eq" logic="and" value="${sessionScope['currMediumId']}" >
   			</kendo:dataSource-filterItem>
    	</kendo:dataSource-filter>
		<kendo:dataSource-transport>
			<kendo:dataSource-transport-read url="${readScrFailTotUrl}" dataType="json" type="POST" contentType="application/json">
				<kendo:dataSource-transport-read-data>
					<script>
						function additionalData(e) {
							return { reqStrValue1:  "${currDate}" };
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
					<kendo:dataSource-schema-model-field name="dateTotal" type="number" />
				</kendo:dataSource-schema-model-fields>
			</kendo:dataSource-schema-model>
		</kendo:dataSource-schema>
	</kendo:dataSource>
</kendo:grid>
</div>


<div class="mb-4">
<div class="card" style="border-bottom: transparent;">
	<h6 class="card-header with-elements py-2">
		<span class="lead">
			<small><span class='badge badge-outline-secondary' style='font-weight: 300;'>대체</span></small>
			<span class="ml-1">대체광고</span>
		</span>
		<div class="card-header-elements ml-auto">
			<button type='button' id="excel-fallback-btn" class='btn icon-btn btn-sm btn-outline-secondary'> 
				<span class='fa-light fa-file-excel fa-lg'></span>
			</button>
		</div>
	</h6>
</div>
<kendo:grid name="fallback-grid" pageable="true" filterable="true" sortable="true" scrollable="true" reorderable="true" resizable="true">
    <kendo:grid-selectable mode="multiple, raw"/>
	<kendo:grid-excel fileName="매체리포트(화면대체광고).xlsx" allPages="true" proxyURL="/proxySave"/>
	<kendo:grid-pageable refresh="true" buttonCount="5" pageSize="10" pageSizes="${pageSizesNormal}" />
   	<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />
	<kendo:grid-columns>
		<kendo:grid-column title="화면" field="screen.name" width="250" template="<%= screenNameTemplate %>" />
		<kendo:grid-column title="합계" field="dateTotal" width="100" />
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
	</kendo:grid-columns>
	<kendo:grid-filterable>
		<kendo:grid-filterable-messages selectedItemsFormat="{0} 항목 선택됨"/>
	</kendo:grid-filterable>
	<kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="dateTotal" dir="desc"/>
		</kendo:dataSource-sort>
       	<kendo:dataSource-filter>
   			<kendo:dataSource-filterItem field="medium.id" operator="eq" logic="and" value="${sessionScope['currMediumId']}" >
   			</kendo:dataSource-filterItem>
    	</kendo:dataSource-filter>
		<kendo:dataSource-transport>
			<kendo:dataSource-transport-read url="${readScrFbTotUrl}" dataType="json" type="POST" contentType="application/json">
				<kendo:dataSource-transport-read-data>
					<script>
						function additionalData(e) {
							return { reqStrValue1:  "${currDate}" };
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
					<kendo:dataSource-schema-model-field name="dateTotal" type="number" />
				</kendo:dataSource-schema-model-fields>
			</kendo:dataSource-schema-model>
		</kendo:dataSource-schema>
	</kendo:dataSource>
</kendo:grid>
</div>


<div class="mb-4">
<div class="card" style="border-bottom: transparent;">
	<h6 class="card-header with-elements py-2">
		<span class="lead">
			<span class="fa-light fa-empty-set fa-lg"></span>
			<span class="ml-1">광고없음</span>
		</span>
		<div class="card-header-elements ml-auto">
			<button type='button' id="excel-no-ad-btn" class='btn icon-btn btn-sm btn-outline-secondary'> 
				<span class='fa-light fa-file-excel fa-lg'></span>
			</button>
		</div>
	</h6>
</div>
<kendo:grid name="no-ad-grid" pageable="true" filterable="true" sortable="true" scrollable="true" reorderable="true" resizable="true">
    <kendo:grid-selectable mode="multiple, raw"/>
	<kendo:grid-excel fileName="매체리포트(화면광고없음).xlsx" allPages="true" proxyURL="/proxySave"/>
	<kendo:grid-pageable refresh="true" buttonCount="5" pageSize="10" pageSizes="${pageSizesNormal}" />
   	<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />
	<kendo:grid-columns>
		<kendo:grid-column title="화면" field="screen.name" width="250" template="<%= screenNameTemplate %>" />
		<kendo:grid-column title="합계" field="dateTotal" width="100" />
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
	</kendo:grid-columns>
	<kendo:grid-filterable>
		<kendo:grid-filterable-messages selectedItemsFormat="{0} 항목 선택됨"/>
	</kendo:grid-filterable>
	<kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="dateTotal" dir="desc"/>
		</kendo:dataSource-sort>
       	<kendo:dataSource-filter>
   			<kendo:dataSource-filterItem field="medium.id" operator="eq" logic="and" value="${sessionScope['currMediumId']}" >
   			</kendo:dataSource-filterItem>
    	</kendo:dataSource-filter>
		<kendo:dataSource-transport>
			<kendo:dataSource-transport-read url="${readScrNoAdTotUrl}" dataType="json" type="POST" contentType="application/json">
				<kendo:dataSource-transport-read-data>
					<script>
						function additionalData(e) {
							return { reqStrValue1:  "${currDate}" };
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
					<kendo:dataSource-schema-model-field name="dateTotal" type="number" />
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

	// Excel
	$("#excel-ovw-btn").click(function(e) {
		e.preventDefault();
		
		$("#ovw-grid").data("kendoGrid").saveAsExcel();
	});
	
	$("#excel-total-btn").click(function(e) {
		e.preventDefault();
		
		$("#total-grid").data("kendoGrid").saveAsExcel();
	});
	
	$("#excel-fail-btn").click(function(e) {
		e.preventDefault();
		
		$("#fail-grid").data("kendoGrid").saveAsExcel();
	});
	
	$("#excel-fallback-btn").click(function(e) {
		e.preventDefault();
		
		$("#fallback-grid").data("kendoGrid").saveAsExcel();
	});
	
	$("#excel-no-ad-btn").click(function(e) {
		e.preventDefault();
		
		$("#no-ad-grid").data("kendoGrid").saveAsExcel();
	});
	
	// / Excel

});
</script>

<!-- / Grid button actions  -->


<!-- Common styles  -->

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


/* 그리드 행의 높이 지정 */
.k-grid tbody tr, .k-grid tbody tr td
{
    height: 40px;
}

</style>

<!-- / Common styles  -->


<!-- / Page body -->





<!-- Functional tags -->

<func:screenInfoModal />


<!-- Closing tags -->

<common:base />
<common:pageClosing />
