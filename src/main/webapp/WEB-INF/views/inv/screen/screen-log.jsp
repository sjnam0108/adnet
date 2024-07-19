<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>

<%@ taglib prefix="inv" tagdir="/WEB-INF/tags/inv"%>


<!-- URL -->

<c:url value="/inv/screen/logs/read" var="readUrl" />


<!-- Opening tags -->

<common:mediumPageOpening />


<!-- Page title -->

<h4 class="pt-1 pb-3 mb-3">
	<span class="text-muted font-weight-light">${pageTitle}<span class="px-2">/</span></span>
	<span class="mr-1 fa-light fa-file-waveform"></span><span class="pl-1">로그</span>
</h4>

<hr class="border-light container-m--x mt-0 mb-4">





<!-- Page body -->


<!--  Overview header -->

<inv:screen />

<!--  / Overview header -->


<!--  Tab -->
<ul class="nav nav-tabs tabs-alt mb-4 mt-3">
	<li class="nav-item">
		<a class="nav-link" href="/inv/screen/chans/${Screen.id}">
			<i class="mr-1 fa-light fa-tower-cell"></i>
			광고 채널
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link active" href="/inv/screen/logs/${Screen.id}">
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
	String operTemplate = 
			"<button type='button' onclick='download(#= id #)' class='btn icon-btn btn-sm btn-outline-secondary borderless'>" + 
			"<span class='fa-solid fa-download'></span></button>";

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
			
	String lengthTemplate = "<div class='len-container'><span data-toggle='tooltip' data-placement='top' title='#= dispFileLength #'>#= smartLength #</span></div>";
			
	String uploadDateTemplate = kr.adnetwork.utils.Util.getSmartDate("uploadDate");
%>

<!-- Kendo grid  -->

<div class="mb-4">
<kendo:grid name="grid" pageable="true" filterable="true" scrollable="true" reorderable="true" resizable="true" sortable="true">
	<kendo:grid-sortable mode="mixed" showIndexes="true"/>
    <kendo:grid-selectable mode="multiple, raw"/>
	<kendo:grid-excel fileName="${pageTitle}.xlsx" allPages="true" proxyURL="/proxySave"/>
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
	<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />
	<kendo:grid-columns>
		<kendo:grid-column title=" " width="50" filterable="false" sortable="false" template="<%= operTemplate %>" />
        <kendo:grid-column title="날짜" field="date" filterable="false" />
        <kendo:grid-column title="파일명" field="filename" filterable="false" />
		<kendo:grid-column title="파일크기" field="length" filterable="false" template="<%= lengthTemplate %>" />
        <kendo:grid-column title="업로드일시" field="uploadDate" filterable="false" template="<%= uploadDateTemplate %>" />
	</kendo:grid-columns>
	<kendo:grid-filterable>
		<kendo:grid-filterable-messages selectedItemsFormat="{0} 항목 선택됨"/>
	</kendo:grid-filterable>
	<kendo:dataSource error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="lastModified" dir="desc"/>
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
		<kendo:dataSource-schema>
			<kendo:dataSource-schema-model>
				<kendo:dataSource-schema-model-fields>
					<kendo:dataSource-schema-model-field name="length" type="number"/>
					<kendo:dataSource-schema-model-field name="uploadDate" type="date"/>
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


function download(id) {
	
	var dataItem = $("#grid").data("kendoGrid").dataSource.get(id);

	var path = "/adn/common/download?type=Log&file=" + dataItem.filename;
	
	location.href = path;
}

</script>

<!--  / Scripts -->


<!-- / Page body -->





<!-- Functional tags -->


<!-- Closing tags -->

<common:base />
<common:pageClosing />
