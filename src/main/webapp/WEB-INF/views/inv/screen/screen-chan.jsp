<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>

<%@ taglib prefix="inv" tagdir="/WEB-INF/tags/inv"%>


<!-- URL -->

<c:url value="/inv/site/screen/read" var="readUrl" />

<c:url value="/inv/screen/create" var="createUrl" />
<c:url value="/inv/screen/update" var="updateUrl" />
<c:url value="/inv/screen/destroy" var="destroyUrl" />

<c:url value="/inv/screen/readResolutions" var="readResolutionUrl" />

<c:url value="/inv/screen/updateTime" var="updateTimeUrl" />


<!-- Opening tags -->

<common:mediumPageOpeningNaverMap />


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


<!-- Java(optional)  -->

<%
	String editTemplate =
			"<div class='text-nowrap'>" +
				"<button type='button' onclick='edit(#= id #)' class='btn icon-btn btn-sm btn-outline-success borderless'>" + 
				"<span class='fas fa-pencil-alt'></span></button>" +
				"<span class='d-none d-sm-inline pl-1'>" +
					"<button type='button' onclick='editTime(#= id #)' " +
						"# if (bizHour && bizHour.length == 168) { #" +
							"class='btn icon-btn btn-sm btn-outline-success borderless'>" + 
						"# } else { #" +
							"class='btn icon-btn btn-sm btn-outline-secondary borderless'>" + 
						"# } #" +
					"<span class='fa-regular fa-clock'></span></button>" +
				"</span>" +
			"</div>";
			
	String nameTemplate =
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
				//"<span class='pl-1'><a href='javascript:void(0)' class='stb-status-popover' tabindex='0'>#= name #</a></span>" +
				"<span class='pl-1'>#= name #</span>" +
				"<a href='javascript:showScreen(#= id #,\"#= name #\")' class='btn btn-default btn-xs icon-btn ml-1'><span class='fas fa-search text-info'></span></a>" +
			"</div>";
	
	String effStartDateTemplate = kr.adnetwork.utils.Util.getSmartDate("effectiveStartDate", false, false);
	String effEndDateTemplate = kr.adnetwork.utils.Util.getSmartDate("effectiveEndDate", false, false);
	String apiSyncDateTemplate = kr.adnetwork.utils.Util.getSmartDate("apiSyncDate", false, true);

	String noRecordsTemplate =
			"<div class='container text-center my-4'>" +
				"<div class='d-flex justify-content-center align-self-center'>" +
					"<span class='fa-thin fa-hexagon-exclamation fa-3x'></span>" +
					"<span class='pl-3 mt-2' style='font-weight: 300; font-size: 1.3rem;'>해당 자료 없음</span>" +
				"</div>" +
			"</div>";
%>



<!-- / Page body -->





<!-- Functional tags -->

<func:screenInfoModal />
<func:cmmValidate />


<!-- Closing tags -->

<common:base />
<common:pageClosing />
