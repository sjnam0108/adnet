<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>

<%@ taglib prefix="inv" tagdir="/WEB-INF/tags/inv"%>


<!-- URL -->

<c:url value="/inv/screen/chans/read" var="readUrl" />


<!-- Opening tags -->

<common:mediumPageOpening />


<!-- Page title -->

<h4 class="pt-1 pb-3 mb-3">
	<span class="text-muted font-weight-light">${pageTitle}<span class="px-2">/</span></span>
	<span class="mr-1 fa-light fa-tower-cell"></span><span class="pl-1">광고 채널</span>
</h4>

<hr class="border-light container-m--x mt-0 mb-4">





<!-- Page body -->


<!--  Overview header -->

<inv:screen />

<!--  / Overview header -->


<!--  Tab -->
<ul class="nav nav-tabs tabs-alt mb-4 mt-3">
	<li class="nav-item">
		<a class="nav-link active" href="/inv/screen/chans/${Screen.id}">
			<i class="mr-1 fa-light fa-tower-cell"></i>
			광고 채널
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" href="/inv/screen/logs/${Screen.id}">
			<i class="mr-1 fa-light fa-file-waveform"></i>
			로그
		</a>
	</li>
</ul>
<!--  / Tab -->


<!--  Root form container -->
<div id="formRoot"></div>


<!--  Page details -->


<!-- Java(optional)  -->

<%
	String noRecordsTemplate =
			"<div class='container text-center my-4'>" +
				"<div class='d-flex justify-content-center align-self-center'>" +
					"<span class='fa-thin fa-hexagon-exclamation fa-3x'></span>" +
					"<span class='pl-3 mt-2' style='font-weight: 300; font-size: 1.3rem;'>해당 자료 없음</span>" +
				"</div>" +
			"</div>";

	String shortNameTemplate =
			"<div>" +
				"<a href='javascript:navToChanAd(#= channel.id #)'><span class='text-link'>#= channel.shortName #</span></a>" +
			"</div>";

	String appendModeTemplate =
			"# if (channel.appendMode == 'A') { #" +
				"<span class='fa-regular fa-robot fa-fw'></span><span class='pl-2'>자율선택</span>" +
			"# } else if (channel.appendMode == 'P') { #" +
				"<span class='fa-regular fa-list-ol fa-fw'></span><span class='pl-2'>재생목록</span>" +
			"# } #";

	String nameTemplate =
			"# if (channel.priorityHigh) { #" +
				"<span title='우선순위 채널' class='pr-1'><span class='fa-light fa-gear text-blue'></span></span>" +
			"# } #" +
			"<span>#= channel.name #</span>";

	String lastAdAppDateTemplate = kr.adnetwork.utils.Util.getSmartDate("lastAdAppDate");
	String lastAdReqDateTemplate = kr.adnetwork.utils.Util.getSmartDate("lastAdReqDate");
%>

<!-- Kendo grid  -->

<div class="mb-4">
<kendo:grid name="grid" pageable="true" filterable="false" scrollable="true" reorderable="true" resizable="true" sortable="false">
	<kendo:grid-sortable mode="mixed" showIndexes="true"/>
    <kendo:grid-selectable mode="multiple, raw"/>
	<kendo:grid-excel fileName="${pageTitle}.xlsx" allPages="true" proxyURL="/proxySave"/>
	<kendo:grid-pageable refresh="true" previousNext="false" numeric="false" pageSize="10000" info="true" />
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
		<kendo:grid-column title="우선순위" field="channel.priority" width="130" template="#= kendo.format('{0:n0}', channel.priority) #" />
		<kendo:grid-column title="채널ID" field="channel.shortName" width="200" template="<%= shortNameTemplate %>" sticky="true" />
		<kendo:grid-column title="채널 이름" field="channel.name" width="200" template="<%= nameTemplate %>" filterable="false" sortable="false" />
		<kendo:grid-column title="해상도" field="channel.resolution" width="120" filterable="false" sortable="false"
				template="#= channel.resolution.replace('x', ' x ') #"  />
		<kendo:grid-column title="게시유형" field="channel.viewTypeCode" width="120" filterable="false" sortable="false" />
		<kendo:grid-column title="광고 추가 모드" field="channel.appendMode" width="150" sortable="false" filterable="false" template="<%= appendModeTemplate %>" />
		<kendo:grid-column title="광고 편성중" field="channel.adAppended" width="120" filterable="false" sortable="false"
				template="#= channel.adAppended ? \"<span class='fa-light fa-check'>\" : \"\"#" />
		<kendo:grid-column title="활성화 상태" field="channel.activeStatus" width="120" filterable="false" sortable="false"
				template="#= channel.activeStatus ? \"<span class='fa-light fa-check'>\" : \"\"#" />
		<kendo:grid-column title="최근 편성" field="lastAdAppDate" width="150" template="<%= lastAdAppDateTemplate %>" filterable="false" sortable="false" />
		<kendo:grid-column title="최근광고요청" field="lastAdReqDate" width="150" template="<%= lastAdReqDateTemplate %>" filterable="false" sortable="false" />
		<kendo:grid-column title="구독수" field="channel.subCount" width="150" filterable="false" sortable="false" />
	</kendo:grid-columns>
	<kendo:grid-filterable>
		<kendo:grid-filterable-messages selectedItemsFormat="{0} 항목 선택됨"/>
	</kendo:grid-filterable>
	<kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="channel.priority" dir="asc"/>
			<kendo:dataSource-sortItem field="channel.shortName" dir="asc"/>
		</kendo:dataSource-sort>
		<kendo:dataSource-transport>
			<kendo:dataSource-transport-read url="${readUrl}" dataType="json" type="POST" contentType="application/json">
				<kendo:dataSource-transport-read-data>
					<script>
						function additionalData(e) {
							return { reqIntValue1:  ${Screen.id} };
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
					<kendo:dataSource-schema-model-field name="lastAdAppDate" type="date" />
					<kendo:dataSource-schema-model-field name="lastAdReqDate" type="date" />
				</kendo:dataSource-schema-model-fields>
			</kendo:dataSource-schema-model>
		</kendo:dataSource-schema>
	</kendo:dataSource>
</kendo:grid>
</div>

<style>






</style>

<!-- / Kendo grid  -->


<!-- Grid button actions  -->

<script>
$(document).ready(function() {


});
</script>

<!-- / Grid button actions  -->


<!--  Scripts -->

<script>

function navToChanAd(chanId) {
	var path = "/org/channel/ad/" + chanId;
	location.href = path;
}

</script>

<!--  / Scripts -->


<!-- / Page body -->





<!-- Functional tags -->


<!-- Closing tags -->

<common:base />
<common:pageClosing />
