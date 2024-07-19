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

<c:url value="/adc/creative/update" var="updateUrl" />
<c:url value="/adc/creative/submit" var="submitUrl" />
<c:url value="/adc/creative/recall" var="recallUrl" />
<c:url value="/adc/creative/reject" var="rejectUrl" />
<c:url value="/adc/creative/archive" var="archiveUrl" />
<c:url value="/adc/creative/unarchive" var="unarchiveUrl" />
<c:url value="/adc/creative/pause" var="pauseUrl" />
<c:url value="/adc/creative/resume" var="resumeUrl" />
<c:url value="/adc/creative/destroy" var="destroyUrl" />

<c:url value="/adc/creative/copy" var="copyUrl" />

<c:url value="/adc/creative/detail/readAuditTrail" var="readAuditTrailUrl" />
<c:url value="/adc/creative/detail/readAuditTrailValue" var="readAuditTrailValueUrl" />


<!-- Opening tags -->

<common:mediumPageOpening />


<!-- Page title -->

<h4 class="pt-1 pb-3 mb-3">
	<span class="text-muted font-weight-light">광고주<span class="px-2">/</span>${pageTitle}<span class="px-2">/</span></span>
	<span class="mr-1 fa-light fa-microscope"></span><span class="pl-1">소재 상세</span>
</h4>

<hr class="border-light container-m--x mt-0 mb-4">





<!-- Page body -->


<!-- Java(optional)  -->

<%
	String dateTemplate = kr.adnetwork.utils.Util.getSmartDate("whoCreationDate", true, true);
	
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
			"# } else if (actType == 'X' || (actType == 'U' && tgtType == 'File')) { #" +
				"<span class='fa-light fa-trash-can fa-fw text-danger'></span><span class='pl-1'>삭제</span>" +
			"# } else if (actType == 'E') { #" +
				"<span class='fa-light fa-pencil-alt fa-fw'></span><span class='pl-1'>변경</span>" +
			"# } else if (actType == 'S' && tgtType == 'File') { #" +
				"<span class='fa-light fa-cloud-arrow-up fa-fw'></span><span class='pl-1'>업로드</span>" +
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
				"# if (actType == 'E' && tgtType == 'Inven' && !tgtValue) { #" +
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
				".replace('{IconCF}', '<span class=\"badge badge-pill bg-secondary text-white default-pointer\">소재 파일</span><span class=\"pr-1\"></span>')" +
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

<adc:advertiser />

<!--  / Overview header -->


<!--  Tab -->
<ul class="nav nav-tabs tabs-alt mb-4 mt-3">
	<li class="nav-item">
		<a class="nav-link" href="/adc/creative/creatives/${Advertiser.id}">
			<i class="mr-1 fa-light fa-clapperboard-play"></i>
			소재 목록
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link active" href="/adc/creative/detail/${Advertiser.id}">
			<i class="mr-1 fa-light fa-microscope"></i>
			소재 상세
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" href="/adc/creative/files/${Advertiser.id}">
			<i class="mr-1 fa-light fa-photo-film"></i>
			소재 파일
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" href="/adc/creative/invtargets/${Advertiser.id}">
			<i class="mr-1 fa-light fa-bullseye-arrow"></i>
			<span id="inven-target-tab-title">인벤 타겟팅</span>
		</a>
	</li>
	<li class="nav-item mr-auto">
		<a class="nav-link" href="/adc/creative/timetarget/${Advertiser.id}">
			<i class="mr-1 fa-light fa-alarm-clock"></i>
			<span id="time-target-tab-title">시간 타겟팅</span>
		</a>
	</li>

<c:if test="${fn:length(currCreatives) > 0}" >

	<select name="nav-item-creat-select" class="selectpicker bg-white mb-1" data-style="btn-default" data-none-selected-text="" data-width="250px" data-size="10" >

<c:forEach var="item" items="${currCreatives}">

		<option value="${item.value}" data-content="<span class='fa-light ${item.icon}'></span><span class='pl-2'>${item.text}</span>"></option>

</c:forEach>

	</select>

<script>
$(document).ready(function() {

	$("select[name='nav-item-creat-select']").selectpicker('render');

	$("select[name='nav-item-creat-select']").on("change.bs.select", function(e){
		location.href = "/adc/creative/detail/${Advertiser.id}/" + $("select[name='nav-item-creat-select']").val();
	});
	
	bootstrapSelectVal($("select[name='nav-item-creat-select']"), "${currCreatId}");
	
});	
</script>

</c:if>
	
</ul>
<!--  / Tab -->


<!--  Root form container -->
<div id="formRoot"></div>


<!--  Page details -->

<c:choose>
<c:when test="${fn:length(currCreatives) == 0}" >

	<div class="card">
		<div class="card-body">
			<div class="form-row">
				<div class='container text-center my-4'>
					<div class='d-flex justify-content-center align-self-center'>
						<span class='fa-thin fa-diamond-exclamation fa-3x'></span>
						<span class='pl-3 mt-2' style='font-weight: 300; font-size: 1.3rem;'>현재 선택된 광고 소재 없음</span>
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
			<span class="fa-stack fa-xs" title="광고 소재" style="vertical-align: top; color: #a0a0a0;">
				<span class="fa-solid fa-circle fa-stack-2x"></span>
				<span class="fa-solid fa-clapperboard-play fa-stack-1x fa-inverse fa-lg"></span>
			</span>
			<span id="creative-name"></span>
		</span>
		<div class="card-header-elements ml-auto">
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
					<a class="dropdown-item" href="javascript:void(0)" id="submit-btn">
						<i class="fa-light fa-paper-plane fa-fw"></i><span class="pl-2">승인 요청</span>
					</a>
					<a class="dropdown-item" href="javascript:void(0)" id="recall-btn">
						<i class="fa-light fa-arrow-rotate-left fa-fw"></i><span class="pl-2">승인 요청 회수</span>
					</a>
					<div class="dropdown-divider"></div>
					<a class="dropdown-item" href="javascript:void(0)" id="reject-btn">
						<i class="fa-light fa-thumbs-down fa-fw"></i><span class="pl-2">승인 소재 거절</span>
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
	<div class="row no-gutters row-bordered row-border-light">
		<div class="col">
			<div class="d-flex align-items-center container-p-x py-4">
			
<c:choose>
<c:when test="${Creative.type == 'C'}">

				<span class="fa-thin fa-audio-description fa-3x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">유형</div>
					<div class="text-large">일반 광고</div>
				</div>
				
</c:when>
<c:when test="${Creative.type == 'F'}">

				<span class="fa-thin fa-house fa-3x text-orange"></span>
				<div class="ml-3">
					<div class="text-muted small">유형</div>
					<div class="text-large">대체 광고</div>
				</div>
				
</c:when>
</c:choose>
			
			</div>
		</div>
		<div class="col">
			<div class="d-flex align-items-center container-p-x py-4">
			
<c:choose>
<c:when test="${Creative.status == 'D'}">

				<span class="fa-thin fa-asterisk fa-3x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">상태</div>
					<div class="text-large">준비</div>
				</div>
				
</c:when>
<c:when test="${Creative.status == 'P'}">

				<span class="fa-thin fa-square-question fa-3x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">상태</div>
					<div class="text-large">승인대기</div>
				</div>
				
</c:when>
<c:when test="${Creative.status == 'A'}">

				<span class="fa-thin fa-square-check fa-3x text-blue"></span>
				<div class="ml-3">
					<div class="text-muted small">상태</div>
					<div class="text-large">승인</div>
				</div>
				
</c:when>
<c:when test="${Creative.status == 'J'}">

				<span class="fa-thin fa-do-not-enter fa-3x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">상태</div>
					<div class="text-large">거절</div>
				</div>
				
</c:when>
<c:when test="${Creative.status == 'V'}">

				<span class="fa-thin fa-box-archive fa-3x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">상태</div>
					<div class="text-large">보관</div>
				</div>
				
</c:when>
</c:choose>
			
			</div>
		</div>
		<div class="col">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-sidebar fa-3x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">게시 유형</div>
<c:choose>
<c:when test="${not empty Creative.viewTypeCode}">

					<div class="text-large">
						<span class="pr-2">${Creative.viewTypeCode}</span><small><span class="text-muted">${Creative.fixedResolution}</span></small>
					</div>

</c:when>
<c:otherwise>

					<div class="text-large">매체 기본 유형</div>

</c:otherwise>
</c:choose>
				</div>
			</div>
		</div>
		
<c:if test="${Creative.type eq 'F'}">
		
		<div class="col">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-weight-scale fa-3x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">대체 광고간 가중치</div>
					<div class="text-large">${Creative.fbWeight}</div>
				</div>
			</div>
		</div>
		
</c:if>		
		
	</div>
</div>
</div>


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
<kendo:grid name="grid-audit" pageable="true" filterable="true" scrollable="true"
		reorderable="true" resizable="true" detailTemplate="template">
	<kendo:grid-filterable />

	<kendo:grid-sortable mode="mixed" showIndexes="true"/>
	<kendo:grid-selectable mode="row"/>
	<kendo:grid-excel fileName="${pageTitle}.xlsx" allPages="true" proxyURL="/proxySave"/>
	<kendo:grid-pageable refresh="true" buttonCount="5" pageSize="10" pageSizes="${pageSizesNormal}" />
   	<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />
	<kendo:grid-columns>
		<kendo:grid-column title="시간" field="whoCreationDate" width="120" template="<%= dateTemplate %>" sticky="true" />
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
							return { reqIntValue1:  ${Creative.id} };
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

	# if ( actType == 'E' || ( actType == 'S' && tgtType == 'File' )) { #
	
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
	
	if (${Creative.paused} == true) {
		title = "<span title='잠시 멈춤'><span class='fa-light fa-circle-pause text-danger'></span></span><span class='pr-1'></span>";
	}
	title = title + "<span>${Creative.name}</span>";
	if (${Creative.invenTargeted} == true) {
		title = title + "<span class='pl-1'></span><span title='인벤토리 타겟팅'><span class='fa-light fa-bullseye-arrow text-blue'></span></span>";
	}
	if (${Creative.timeTargeted} == true) {
		title = title + "<span class='pl-1'></span><span title='시간 타겟팅'><span class='fa-light fa-alarm-clock text-green'></span></span>";
	}
	
	var resolutions = "<span class='pl-3'>" + dispResoBadgeValues("${Creative.fileResolutions}") + "</span>";
	$("#creative-name").html(title + resolutions);
	
	
	// Edit
	$("#edit-btn").click(function(e) {
		e.preventDefault();
		
		edit();
	});
	// / Edit
	
	// Submit
	$("#submit-btn").click(function(e) {
		e.preventDefault();

		$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: "${submitUrl}",
			data: JSON.stringify({ items: [ ${Creative.id} ] }),
			success: function (form) {
				showOperationSuccessMsg();
				reload();
			},
			error: ajaxOperationError
		});
	});
	// / Submit
	
	// Recall
	$("#recall-btn").click(function(e) {
		e.preventDefault();

		$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: "${recallUrl}",
			data: JSON.stringify({ items: [ ${Creative.id} ] }),
			success: function (form) {
				showOperationSuccessMsg();
				reload();
			},
			error: ajaxOperationError
		});
	});
	// / Recall
	
	// Reject
	$("#reject-btn").click(function(e) {
		e.preventDefault();
		
		var box = bootbox.prompt({
			size: "small",
			title: "거절 이유",
			backdrop: "static",
			buttons: {
				cancel: {
					label: '취소',
					className: "btn-default",
				},
				confirm: {
					label: '확인',
					className: "btn-danger",
				}
			},
			animate: false,
			callback: function(result) {
				if (result) {
					var realRes = $.trim(result);
					if (realRes) {
						
						$.ajax({
							type: "POST",
							contentType: "application/json",
							dataType: "json",
							url: "${rejectUrl}",
							data: JSON.stringify({ items: [ ${Creative.id} ], reason: realRes }),
							success: function (form) {
								showOperationSuccessMsg();
								reload();
							},
							error: ajaxOperationError
						});

					} else {
						showConfirmModal("거절 이유가 입력되지 않아, 자동으로 거절이 취소되었습니다.", function(result) { });
					}
				} else {
					showConfirmModal("거절 이유가 입력되지 않아, 자동으로 거절이 취소되었습니다.", function(result) { });
				}
			},
			className: "modal-level-top",
		}).init(function() {
			setTimeout(function(){
				$('.modal-backdrop:last-child').addClass('modal-level-top');
			});
		});
		
		box.find('.modal-level-top .modal-dialog').addClass("modal-dialog-vertical-center");
		box.find('.modal-level-top .modal-content').addClass("modal-content-border-1");

<c:if test="${not isMobileMode}">

		box.find('.modal-level-top .modal-header').addClass("move-cursor");
		box.find('.modal-level-top .modal-dialog').draggable({ handle: '.modal-header' });

</c:if>

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
			data: JSON.stringify({ items: [ ${Creative.id} ] }),
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
			data: JSON.stringify({ items: [ ${Creative.id} ] }),
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
			data: JSON.stringify({ items: [ ${Creative.id} ] }),
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
			data: JSON.stringify({ items: [ ${Creative.id} ] }),
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
			data: JSON.stringify({ items: [ ${Creative.id} ] }),
			success: function (form) {
				showOperationSuccessMsg();
				reload();
			},
			error: ajaxOperationError
		});
	});
	// / Delete
	
});	
</script>

<!-- / Form button actions  -->


<!--  Forms -->

<script id="template-1" type="text/x-kendo-template">

<div class="modal fade" data-backdrop="static" id="form-modal-1">
	<div class="modal-dialog modal-sm">
		<form class="modal-content" id="form-1" rowid="-1" url="${createUrl}">
      
			<!-- Modal Header -->
			<div class="modal-header move-cursor">
				<h5 class="modal-title">
					${pageTitle}
					<span class="font-weight-light pl-1"><span name="subtitle"></span>
				</h5>
				<button type="button" class="close" data-dismiss="modal">×</button>
			</div>
        
			<!-- Modal body -->
			<div class="modal-body modal-bg-color">
				<div class="form-group col">
					<label class="form-label">
						광고 소재명
						<span class="text-danger">*</span>
					</label>
					<input name="name" type="text" maxlength="100" class="form-control required">
				</div>
				<div class="form-group col">
					<label class="form-label">
						광고주
					</label>
					<input type="text" class="form-control" value="${Advertiser.name}" readonly="readonly">
				</div>
				<div class="form-group col">
					<label class="form-label">
						범주
					</label>
					<select name="category" class="selectpicker bg-white" data-style="btn-default" data-tick-icon="fa-blank" data-none-selected-text="">
						<option value="" data-icon="fa-regular fa-blank fa-fw mr-1"></option>
						<option value="A" data-icon="fa-regular fa-tv-retro fa-fw mr-1">가전</option>
						<option value="B" data-icon="fa-regular fa-gamepad fa-fw mr-1">게임</option>
						<option value="C" data-icon="fa-regular fa-building-flag fa-fw mr-1">관공서/단체</option>
						<option value="D" data-icon="fa-regular fa-golf-club fa-fw mr-1">관광/레저</option>
						<option value="E" data-icon="fa-regular fa-books fa-fw mr-1">교육/출판</option>
						<option value="F" data-icon="fa-regular fa-coins fa-fw mr-1">금융</option>
						<option value="G" data-icon="fa-regular fa-music fa-fw mr-1">문화/엔터테인먼트</option>
						<option value="H" data-icon="fa-regular fa-hand-holding-seedling fa-fw mr-1">미디어/서비스</option>
						<option value="I" data-icon="fa-regular fa-toothbrush fa-fw mr-1">생활용품</option>
						<option value="J" data-icon="fa-regular fa-truck-fast fa-fw mr-1">유통</option>
						<option value="K" data-icon="fa-regular fa-syringe fa-fw mr-1">제약/의료</option>
						<option value="L" data-icon="fa-regular fa-martini-glass fa-fw mr-1">주류</option>
						<option value="M" data-icon="fa-regular fa-house fa-fw mr-1">주택/가구</option>
						<option value="N" data-icon="fa-regular fa-shirt fa-fw mr-1">패션</option>
						<option value="O" data-icon="fa-regular fa-spray-can fa-fw mr-1">화장품</option>
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
				<div class="form-group col">
					<label class="form-label">
						소재 유형
					</label>
					<select name="type" class="selectpicker bg-white" data-style="btn-default" data-tick-icon="fa-blank" data-none-selected-text="">
						<option value="C" data-icon="fa-regular fa-audio-description fa-fw mr-1">일반 광고</option>
						<option value="F" data-icon="fa-regular fa-house fa-fw text-orange mr-1">대체 광고</option>
					</select>
				</div>
				<div class="form-group col" name="fallback-value-div" style="display: none;">
					<label class="form-label">
						화면의 재생시간 정책
					</label>
					<div>
						<label class="switcher">
							<input type="checkbox" class="switcher-input check-switch-status" name="durPolicyOverriden">
							<span class="switcher-indicator">
								<span class="switcher-yes"></span>
								<span class="switcher-no"></span>
							</span>
							<span class="switcher-label">매체 화면의 재생시간 정책 무시 허용</span>
						</label>
					</div>
				</div>
				<div class="form-group col" name="fallback-value-div" style="display: none;">
					<label class="form-label">
						대체 광고간 가중치
					</label>
					<input name="fbWeight" type="text" class="form-control required" value="1">
				</div>
			</div>
        
			<!-- Modal footer -->
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">취소</button>
				<button type="button" class="btn btn-primary" onclick='saveForm1()'>저장</button>
			</div>
			
		</form>
	</div>
</div>

</script>

<style>

/* 폼에서 열 사이의 간격을 원래대로 넓게.
   modal medium size에서 form-row를 이용한 경우에만 적용되고,
   modal-lg에는 적용하지 않는 것이 원칙 */
.form-row>.col, .form-row>[class*="col-"] {
	padding-right: .75rem;
	padding-left: .75rem;
}

</style>

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


function validateForm1Type() {
	
	if ($("#form-1 select[name='type']").val() == "C") {
		$("#form-1 div[name='fallback-value-div']").hide();
	} else {
		$("#form-1 div[name='fallback-value-div']").show();
		
		$("#form-1 input[name='durPolicyOverriden']").prop("checked", false);
		$("#form-1 input[name='fbWeight']").val("1");
	}
}


function initForm1(subtitle) {
	
	$("#formRoot").html(kendo.template($("#template-1").html()));
	
	$("#form-1 select[name='type']").selectpicker('render');
	$("#form-1 select[name='category']").selectpicker('render');
	$("#form-1 select[name='viewType']").selectpicker('render');
	
	// 범주 셀렉터의 초기값이 없도록
	bootstrapSelectVal($("#form-1 select[name='category']"), "");
	
	$("#form-1 select[name='type']").on("change.bs.select", function(e){
		validateForm1Type();
	});
	
	
	$("#form-1 span[name='subtitle']").text(subtitle ? subtitle : "추가");

	$("#form-1").validate({
		rules: {
			name: {
				minlength: 2,
			},
			fbWeight: {
				digits: true,
				min: 1,
			},
		}
	});
}


function saveForm1() {

	if ($("#form-1").valid()) {
    	var data = {
    		id: Number($("#form-1").attr("rowid")),
    		name: $.trim($("#form-1 input[name='name']").val()),
    		advertiser: ${Advertiser.id},
    		type: $("#form-1 select[name='type']").val(),
       		viewType: $("#form-1 select[name='viewType']").val(),
    		durPolicyOverriden: $("#form-1 input[name='durPolicyOverriden']").is(':checked'),
    		fbWeight: Number($.trim($("#form-1 input[name='fbWeight']").val())),
    		category: $("#form-1 select[name='category']").val(),
    	};
    	
		$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: $("#form-1").attr("url"),
			data: JSON.stringify(data),
			success: function (form) {
				showSaveSuccessMsg();
				$("#form-modal-1").modal("hide");
				reload();
			},
			error: ajaxSaveError
		});
	}
}


function edit() {
	
	initForm1("변경");

	
	$("#form-1").attr("rowid", ${Creative.id});
	$("#form-1").attr("url", "${updateUrl}");
	
	$("#form-1 input[name='name']").val("${Creative.name}");
	$("#form-1 input[name='fbWeight']").val(${Creative.fbWeight});
	console.log("edit1");

	bootstrapSelectVal($("#form-1 select[name='type']"), "${Creative.type}");
	bootstrapSelectVal($("#form-1 select[name='category']"), "${Creative.category}");
	bootstrapSelectVal($("#form-1 select[name='viewType']"), "${Creative.viewTypeCode}");
	
	$("#form-1 input[name='durPolicyOverriden']").prop("checked", ${Creative.durPolicyOverriden});
	
	//validateForm1Type();
	if ($("#form-1 select[name='type']").val() == "C") {
		$("#form-1 div[name='fallback-value-div']").hide();
	} else {
		$("#form-1 div[name='fallback-value-div']").show();
	}
	
	
	// 기등록된 소재 파일을 가지는 소재의 게시 유형은 변경 불가능
	if ("${Creative.fileResolutions}" != "") {
		bootstrapSelectDisabled($("#form-1 select[name='viewType']"), true);
	}

	
	$('#form-modal-1 .modal-dialog').draggable({ handle: '.modal-header' });
	$("#form-modal-1").modal();
}


function reload() {
	
	setTimeout(function(){
		location.reload();
	}, 1000);
}


var invenOrderTemplate = kendo.template($("#targetOrderViewInvenTemplate").html());

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
