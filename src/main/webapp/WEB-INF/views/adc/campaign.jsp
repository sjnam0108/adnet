<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/adc/campaign/create" var="createUrl" />
<c:url value="/adc/campaign/update" var="updateUrl" />
<c:url value="/adc/campaign/read" var="readUrl" />
<c:url value="/adc/campaign/destroy" var="destroyUrl" />

<c:url value="/adc/campaign/createAd" var="createAdUrl" />
<c:url value="/adc/campaign/readStatuses" var="readStatusUrl" />
<c:url value="/adc/campaign/readAdvertisers" var="readAdvUrl" />
<c:url value="/adc/campaign/readGoalTypes" var="readGoalTypeUrl" />
<c:url value="/adc/campaign/readImpDailyTypes" var="readImpDailyTypeUrl" />


<!-- Opening tags -->

<common:mediumPageOpening />


<!-- Page title -->

<h4 class="pt-1 pb-3 mb-3">
	<span class="mr-1 ${sessionScope['loginUser'].icon}"></span>
	${pageTitle}
</h4>

<hr class="border-light container-m--x mt-0 mb-4">





<!-- Page body -->


<!-- Page scripts  -->

<link rel="stylesheet" href="/resources/vendor/lib/bootstrap-slider/bootstrap-slider.css">

<script>$.fn.slider = null</script>
<script type="text/javascript" src="/resources/vendor/lib/bootstrap-slider/bootstrap-slider.js"></script>


<!-- Java(optional)  -->

<%
	String editTemplate = 
			"<button type='button' onclick='edit(#= id #)' class='btn icon-btn btn-sm btn-outline-success borderless'>" + 
			"<span class='fas fa-pencil-alt'></span></button>";
	String advertiserTemplate = "<a href='javascript:navToAdv(#= advertiser.id #)'><span class='text-link'>#= advertiser.name #</span></a>";
	String freqCapTemplate =
			"# if (freqCap > 1) { #" +
				"<span>#= freqCap #</span>" +
			"# } else { #" +
				"<span></span>" +
			"# } #";
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
	String regDateTemplate = kr.adnetwork.utils.Util.getSmartDate("whoCreationDate", false);
	String lastUpdateDateTemplate = kr.adnetwork.utils.Util.getSmartDate("whoLastUpdateDate", false);
	
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
			"# if (selfManaged) { #" +
				"# if (impDailyType == 'E') { #" +
					"<span title='모든 날짜 균등'><span class='fa-regular fa-equals fa-fw'></span>" +
				"# } else if (impDailyType == 'W') { #" +
					"<span title='통계 기반 요일별 차등'><span class='fa-regular fa-bars-staggered fa-fw'></span>" +
				"# } else { #" +
					"<span>-</span>" +
				"# } #" +
			"# } else { #" +
				"<span></span>" +
			"# } #";

	String budgetTemplate =
			"# if (selfManaged) { #" +
				"<span>#= kendo.format('{0:n0}', budget) #</span>" +
			"# } else { #" +
				"<span></span>" +
			"# } #";
	String goalValueTemplate =
			"# if (selfManaged) { #" +
				"<span>#= kendo.format('{0:n0}', goalValue) #</span>" +
			"# } else { #" +
				"<span></span>" +
			"# } #";
	String sysValueTemplate =
			"# if (selfManaged) { #" +
				"# if (proposedSysValue > 0) { #" +
					"<span class=\"text-muted\">#= kendo.format('{0:n0}', proposedSysValue) #</span>" +
					"<span class='pl-1' title='자동 설정'><span class='fa-regular fa-asterisk text-info fa-2xs'></span></span>" +
				"# } else { #" +
					"<span>#= kendo.format('{0:n0}', sysValue) #</span>" +
				"# } #" +
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
			
	String nameTemplate =
			"<div>" +
				"<a href='javascript:navToCamp(#= id #)'><span class='text-link'>#= name #</span></a>" + 
				"# if (statusCard == 'Y') { #" +
					"<span class='pl-1'></span><span title='오늘 진행되는 광고 소재 없음'><span class='fa-solid fa-rectangle-vertical text-yellow'></span></span>" +
				"# } else if (statusCard == 'R') { #" +
					"<span class='pl-1'></span><span title='활성중인 광고 소재 없음'><span class='fa-solid fa-rectangle-vertical text-danger'></span></span>" +
				"# } #" +
				"# if (tgtTodayDisp) { #" +
					"<span data-toggle='tooltip' data-placement='right' title=\"#= tgtTodayDisp #\">" +
						"<span class='pl-1'><span class='fa-regular fa-golf-flag-hole text-info'></span></span>" +
					"</span>" +
				"# } #" +
			"</div>";

%>

<!-- Kendo grid  -->

<div class="mb-4">
<kendo:grid name="grid" pageable="true" filterable="true" scrollable="true" reorderable="true" resizable="true">
	<kendo:grid-sortable mode="mixed" showIndexes="true"/>
    <kendo:grid-selectable mode="raw"/>
	<kendo:grid-excel fileName="${pageTitle}.xlsx" allPages="true" proxyURL="/proxySave"/>
	<kendo:grid-pageable refresh="true" buttonCount="5" pageSize="10" pageSizes="${pageSizesNormal}" />
	<kendo:grid-toolbarTemplate>
    	<div class="clearfix">
    		<div class="float-left">
    			<div class="btn-group">
					<button type="button" class="btn btn-outline-success dropdown-toggle" data-toggle="dropdown">추가</button>
					<div class="dropdown-menu">
						<a class="dropdown-item" href="javascript:void(0)" id="add-btn">
							<i class="fa-light fa-briefcase fa-fw"></i><span class="pl-2">캠페인만</span>
						</a>
						<a class="dropdown-item" href="javascript:void(0)" id="add-together-btn">
							<i class="fa-light fa-audio-description fa-fw"></i><span class="pl-2">캠페인과 광고를 함께</span>
						</a>
					</div>    			
    			</div>
    			<button type="button" class="btn btn-default d-none d-sm-inline k-grid-excel">엑셀</button>
    		</div>
    		<div class="float-right">
    			<button id="delete-btn" type="button" class="btn btn-danger">삭제</button>
    		</div>
    	</div>
	</kendo:grid-toolbarTemplate>
	<kendo:grid-filterable extra="false" />

	<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />
	<kendo:grid-columns>
		<kendo:grid-column title="수정" width="50" filterable="false" sortable="false" template="<%= editTemplate %>" />
		<kendo:grid-column title="캠페인명" field="name" width="250" template="<%= nameTemplate %>" />
		<kendo:grid-column title="상태" field="status" width="120" template="<%= statusTemplate %>" >
			<kendo:grid-column-filterable multi="true" itemTemplate="kfcIconText">
				<kendo:dataSource>
					<kendo:dataSource-transport>
						<kendo:dataSource-transport-read url="${readStatusUrl}" dataType="json" type="POST" contentType="application/json" />
					</kendo:dataSource-transport>
				</kendo:dataSource>
			</kendo:grid-column-filterable>
		</kendo:grid-column>
		<kendo:grid-column title="광고 수" width="80" filterable="false" sortable="false"
				template="<span>#= adCount #</span>" />
		<kendo:grid-column title="시작일" field="startDate" width="120" template="<%= startDateTemplate %>" />
		<kendo:grid-column title="종료일" field="endDate" width="120" template="<%= endDateTemplate %>" />
		<kendo:grid-column title="광고주" field="advertiser.name" width="200" template="<%= advertiserTemplate %>" />
		<kendo:grid-column title="집행방법" field="goalType" width="120" template="<%= goalTypeTemplate %>" >
			<kendo:grid-column-filterable multi="true" itemTemplate="kfcIconText">
				<kendo:dataSource>
					<kendo:dataSource-transport>
						<kendo:dataSource-transport-read url="${readGoalTypeUrl}" dataType="json" type="POST" contentType="application/json" />
					</kendo:dataSource-transport>
				</kendo:dataSource>
			</kendo:grid-column-filterable>
		</kendo:grid-column>
		<kendo:grid-column title="자체 목표" field="selfManaged" width="120"
				template="#=selfManaged ? \"<span class='fa-light fa-check'>\" : \"\"#" filterable="false" />
		<kendo:grid-column title="예산" field="budget" width="120" template="<%= budgetTemplate %>" />
		<kendo:grid-column title="보장" field="goalValue" width="120" template="<%= goalValueTemplate %>" />
		<kendo:grid-column title="목표" field="sysValue" width="120" template="<%= sysValueTemplate %>" />
		<kendo:grid-column title="일별 분산" field="impDailyType" width="120" template="<%= impDailyTypeTemplate %>" >
			<kendo:grid-column-filterable multi="true" itemTemplate="kfcIconText">
				<kendo:dataSource>
					<kendo:dataSource-transport>
						<kendo:dataSource-transport-read url="${readImpDailyTypeUrl}" dataType="json" type="POST" contentType="application/json" />
					</kendo:dataSource-transport>
				</kendo:dataSource>
			</kendo:grid-column-filterable>
		</kendo:grid-column>
		<kendo:grid-column title="송출금지(초)" field="freqCap" width="150" template="<%= freqCapTemplate %>" />
		<kendo:grid-column title="대행사" field="adAgency" width="150" />
		<kendo:grid-column title="미디어렙사" field="mediaRep" width="150" />
		<kendo:grid-column title="최근 변경" field="whoLastUpdateDate" width="120" template="<%= lastUpdateDateTemplate %>" />
		<kendo:grid-column title="등록" field="whoCreationDate" width="120" template="<%= regDateTemplate %>" />
	</kendo:grid-columns>
	<kendo:grid-dataBound>
		<script>
			function grid_dataBound(e) {
				$('[data-toggle="tooltip"]').tooltip();
				
		        var grid = this;
		        var hasSelfManagedData = false;
		        grid.table.find("tr").each(function () {
		            var dataItem = grid.dataItem(this);
		            
	            	if (dataItem.selfManaged) {
	            		hasSelfManagedData = true;
	            		return false;
	            	}
		        });
				
		        if (!hasSelfManagedData) {
		        	grid.hideColumn(8);
		        	grid.hideColumn(9);
		        	grid.hideColumn(10);
		        	grid.hideColumn(11);
		        	grid.hideColumn(12);
		        	
		        	return;
		        }
		        
		        grid.showColumn(8);
		        grid.showColumn(9);
		        grid.showColumn(10);
		        grid.showColumn(11);
		        grid.showColumn(12);
			}
		</script>
	</kendo:grid-dataBound>
	<kendo:grid-filterable>
		<kendo:grid-filterable-messages selectedItemsFormat="{0} 항목 선택됨"/>
	</kendo:grid-filterable>
	<kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="whoCreationDate" dir="desc"/>
		</kendo:dataSource-sort>
        <kendo:dataSource-filter>
       		<kendo:dataSource-filterItem field="medium.id" operator="eq" logic="and" value="${sessionScope['currMediumId']}" >
       		</kendo:dataSource-filterItem>
   	    </kendo:dataSource-filter>
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
		<kendo:dataSource-schema data="data" total="total" groups="data">
			<kendo:dataSource-schema-model id="id">
				<kendo:dataSource-schema-model-fields>
					<kendo:dataSource-schema-model-field name="freqCap" type="number" />
					<kendo:dataSource-schema-model-field name="budget" type="number" />
					<kendo:dataSource-schema-model-field name="goalValue" type="number" />
					<kendo:dataSource-schema-model-field name="sysValue" type="number" />
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

<style>







/* 라벨 위치의 버튼 크기를 커스텀 */
.btn-custom-label-line {
	width: calc(1.7rem) !important;
	line-height: 1.2rem;
}

</style>

<!-- / Kendo grid  -->


<!-- Grid button actions  -->

<script>
$(document).ready(function() {

	// Add(캠페인)
	$("#add-btn").click(function(e) {
		e.preventDefault();
		
		// 최신의 광고주 정보부터 획득하고 시작
		$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: "${readAdvUrl}",
			data: JSON.stringify({ }),
			success: function (data, status) {
				advertisers = eval(data);
				
				initForm3();
				
				$('#form-modal-3 .modal-dialog').draggable({ handle: '.modal-header' });
				$("#form-modal-3").modal();
			},
			error: ajaxReadError
		});
	});
	// / Add
	
	// Add(캠페인+광고)
	$("#add-together-btn").click(function(e) {
		e.preventDefault();
		
		// 최신의 광고주 정보부터 획득하고 시작
		$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: "${readAdvUrl}",
			data: JSON.stringify({ }),
			success: function (data, status) {
				advertisers = eval(data);
				
				initForm2();
				
				$('#form-modal-2 .modal-dialog').draggable({ handle: '.modal-header' });
				$("#form-modal-2").modal();
			},
			error: ajaxReadError
		});
	});
	// / Add
	
	// Delete
	$("#delete-btn").click(function(e) {
		e.preventDefault();
			
		var grid = $("#grid").data("kendoGrid");
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
						url: "${destroyUrl}",
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


<!--  Root form container -->
<div id="formRoot"></div>


<!--  Forms -->

<script id="template-1" type="text/x-kendo-template">

<div class="modal fade" data-backdrop="static" id="form-modal-1">
	<div class="modal-dialog modal-lg">
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
				<div class="form-row">
					<div class="form-group col">
						<label class="form-label">
							캠페인명
							<span class="text-danger">*</span>
						</label>
						<input name="name" type="text" maxlength="100" class="form-control required">
					</div>
					<div class="form-group col">
						<label class="form-label">
							광고주
						</label>
						<select name="advertiser" class="selectpicker bg-white required" data-style="btn-default" data-none-selected-text="">
						</select>
					</div>
				</div>
				<div class="form-row">
					<div class="form-group col">
						<label class="form-label">
							대행사
						</label>
						<input name="adAgency" type="text" maxlength="25" class="form-control">
					</div>
					<div class="form-group col">
						<label class="form-label">
							미디어렙사
						</label>
						<input name="mediaRep" type="text" maxlength="25" class="form-control">
					</div>
					<div class="form-group col">
						<label class="form-label">
							동일 광고주 광고 송출 금지
							<span data-toggle="tooltip" data-placement="right" title="이 캠페인에 속한 광고 후, 이 캠페인의 다음 광고가 송출되지 않도록 보장되는 시간입니다.">
								<span class="fa-regular fa-circle-info text-info"></span>
							</span>
						</label>
						<select name="freqCapType" class="selectpicker bg-white" data-style="btn-default" data-tick-icon="fa-blank" data-none-selected-text="">
							<option value="0" data-icon="fa-regular fa-earth-asia fa-fw mr-1">매체 설정값(${mediumFreqCapAdv}) 적용</option>
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
					<div class="form-group col mb-2 text-center">
						<div class="btn-group btn-group-toggle" data-toggle="buttons">
							<label class="btn btn-default btn-sm active">
								<input type="radio" name="self-managed-type-radio" value="N" checked> 자체 목표 없음
							</label>
							<label class="btn btn-default btn-sm">
								<input type="radio" name="self-managed-type-radio" value="Y"> 자체 목표 설정
							</label>
						</div>
					</div>
				</div>
				<div class="form-row" name="self-managed-n-div">
					<div class="form-group col text-center">
						<i class="fa-solid fa-quote-left fa-pull-left text-muted"></i>
						<small>광고 예산 혹은 보장/목표 노출량의 값 설정은 개별 광고에서 진행합니다.</small>
						<i class="fa-solid fa-quote-right fa-pull-right text-muted"></i>
					</div>
				</div>
				<div class="form-row" name="self-managed-y-div" style="display: none;">
					<div class="form-group col">
						<label class="form-label">
							광고 예산
						</label>
						<div class="input-group">
							<input name="budget" type="text" value="0" class="form-control input-change-1 required" >
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
							<input name="goalValue" type="text" value="0" class="form-control input-change-1 required" >
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
							<input name="sysValue" type="text" value="0" class="form-control input-change-1 required" >
							<div class="input-group-append">
								<span class="input-group-text">회</span>
							</div>
						</div>
					</div>
				</div>
				<div class="form-row" name="self-managed-y-div" style="display: none;">
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
							<option value="A" data-icon="fa-regular fa-audio-description fa-fw mr-1">포함된 광고 설정에 따름</option>
						</select>
					</div>
				</div>
				<div class="form-row">
					<div class="form-group col">
						<label class="form-label">
							운영자 메모
						</label>
						<textarea name="memo" rows="2" maxlength="150" class="form-control"></textarea>
					</div>
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


<script id="template-2" type="text/x-kendo-template">

<div class="modal fade" data-backdrop="static" id="form-modal-2">
	<div class="modal-dialog modal-lg">
		<form class="modal-content" id="form-2" rowid="-1" url="${createAdUrl}">
      
			<!-- Modal Header -->
			<div class="modal-header move-cursor">
				<h5 class="modal-title">
					캠페인 + 광고
					<span class="font-weight-light pl-1">추가</span>
				</h5>
				<button type="button" class="close" data-dismiss="modal">×</button>
			</div>
        
			<!-- Modal body -->
			<div class="modal-body modal-bg-color">
				<div class="form-row">
					<div class="form-group col">
						<label class="form-label">
							캠페인/광고명
							<span class="text-danger">*</span>
						</label>
						<input name="name" type="text" maxlength="100" class="form-control required">
					</div>
					<div class="form-group col" name="adv-select-type-div">
						<label class="form-label">
							광고주
						</label>
						<div class="float-right">
							<button type="button" class="btn btn-default btn-custom-label-line icon-btn" name="adv-type-toggle-btn" title="새 광고주 함께 등록">
								<span class='fa-regular fa-arrow-right-to-line'></span>
							</button>
						</div>

						<select name="advertiser" class="selectpicker bg-white required" data-style="btn-default" data-none-selected-text="광고주 선택">
						</select>
					</div>
					<div class="form-group col" name="adv-input-type-div" style="display: none;">
						<label class="form-label">
							광고주
							<span class="text-danger">*</span>
						</label>

						<input name="advertiser-name" type="text" maxlength="100" class="form-control required">
					</div>
					<div class="form-group col" name="adv-input-type-div" style="display: none;">
						<label class="form-label">
							광고주 도메인
							<span class="text-danger">*</span>
						</label>
						<div class="float-right">
							<button type="button" class="btn btn-default btn-custom-label-line icon-btn" name="adv-type-toggle-btn" title="등록된 광고주 선택">
								<span class='fa-regular fa-arrow-left-to-line'></span>
							</button>
						</div>

						<input name="advertiser-domain-name" type="text" maxlength="100" class="form-control required">
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
				<button type="button" class="btn btn-primary" onclick='saveForm2()'>저장</button>
			</div>
			
		</form>
	</div>
</div>

</script>


<script id="template-3" type="text/x-kendo-template">

<div class="modal fade" data-backdrop="static" id="form-modal-3">
	<div class="modal-dialog modal-sm">
		<form class="modal-content" id="form-3" rowid="-1" url="${createUrl}">
      
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
						캠페인명
						<span class="text-danger">*</span>
					</label>
					<input name="name" type="text" maxlength="100" class="form-control required">
				</div>
				<div class="form-group col mb-2 text-center">
					<div class="btn-group btn-group-toggle" data-toggle="buttons">
						<label class="btn btn-default btn-sm active">
							<input type="radio" name="adv-input-type-radio" value="S" checked> 등록된 광고주 선택
						</label>
						<label class="btn btn-default btn-sm">
							<input type="radio" name="adv-input-type-radio" value="I"> 새 광고주 함께 등록
						</label>
					</div>
				</div>
				<div class="form-group col" name="advertiser-select-div">
					<label class="form-label">
						광고주
					</label>
					<select name="advertiser" class="selectpicker bg-white" data-style="btn-default" data-none-selected-text="광고주 선택">
					</select>
				</div>
				<div class="form-group col" name="advertiser-input-div" style="display: none;">
					<label class="form-label">
						광고주
						<span class="text-danger">*</span>
					</label>
					<input name="advertiser-name" type="text" maxlength="100" class="form-control required">
				</div>
				<div class="form-group col" name="advertiser-input-div" style="display: none;">
					<label class="form-label">
						광고주 도메인
						<span class="text-danger">*</span>
					</label>
					<input name="advertiser-domain-name" type="text" maxlength="100" class="form-control required">
				</div>
				<div class="form-group col mb-2 text-center">
					<div class="btn-group btn-group-toggle" data-toggle="buttons">
						<label class="btn btn-default btn-sm active">
							<input type="radio" name="self-managed-type-radio" value="N" checked> 자체 목표 없음
						</label>
						<label class="btn btn-default btn-sm" id="abc">
							<input type="radio" name="self-managed-type-radio" value="Y"> 자체 목표 설정
						</label>
					</div>
				</div>
				<div class="form-group col text-center" name="self-managed-n-div">
<i class="fa-solid fa-quote-left fa-pull-left text-muted"></i>
					<small>광고 예산 혹은 보장/목표 노출량의 값 설정은 개별 광고에서 진행합니다.</small>
<i class="fa-solid fa-quote-right fa-pull-right text-muted"></i>
				</div>
				<div class="form-group col" name="self-managed-y-div" style="display: none;">
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
				<div class="form-group col" name="self-managed-y-div" style="display: none;">
					<label class="form-label">
						광고 예산
					</label>
					<div class="input-group">
						<input name="budget" type="text" value="0" class="form-control input-change-3 required" >
						<div class="input-group-append">
							<span class="input-group-text">원</span>
						</div>
					</div>
				</div>
				<div class="form-group col" name="self-managed-y-div" style="display: none;">
					<label class="form-label">
						보장 노출량
					</label>
					<div class="input-group">
						<input name="goalValue" type="text" value="0" class="form-control input-change-3 required" >
						<div class="input-group-append">
							<span class="input-group-text">회</span>
						</div>
					</div>
				</div>
				<div class="form-group col" name="self-managed-y-div" style="display: none;">
					<label class="form-label">
						목표 노출량
					</label>
					<div class="input-group">
						<input name="sysValue" type="text" value="0" class="form-control input-change-3 required" >
						<div class="input-group-append">
							<span class="input-group-text">회</span>
						</div>
					</div>
				</div>
				<div class="form-group col">
					<label class="form-label">
						운영자 메모
					</label>
					<textarea name="memo" rows="2" maxlength="150" class="form-control"></textarea>
				</div>
			</div>
        
			<!-- Modal footer -->
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">취소</button>
				<button type="button" class="btn btn-primary" onclick='saveForm3()'>저장</button>
			</div>
			
		</form>
	</div>
</div>

</script>


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
							<span class="input-group-text">일</span>
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

var advertisers = [];
var advSelMode = true;

function initForm1(subtitle) {
	
	$("#formRoot").html(kendo.template($("#template-1").html()));

	$("#form-1 select[name='advertiser']").selectpicker('render');
	$("#form-1 select[name='freqCapType']").selectpicker('render');
	
	$('[data-toggle="tooltip"]').tooltip();

	$("#form-1 textarea[name='memo']").keypress(function (e) {
		if (e.keyCode != 13) {
			return;
		}
		
		$(this).text().replace(/\n/g, "");
		
		return false;
	});
	
	
	$("#form-1 input[name='self-managed-type-radio']").change(function(){
		if ($("#form-1 input[name='self-managed-type-radio']:checked").val() == 'Y') {
			$("#form-1 div[name='self-managed-y-div']").show();
			$("#form-1 div[name='self-managed-n-div']").hide();
		} else {
			$("#form-1 div[name='self-managed-y-div']").hide();
			$("#form-1 div[name='self-managed-n-div']").show();
		}
	});
	
	
	$("#form-1 select[name='impDailyType']").selectpicker('render');
	$("#form-1 select[name='impHourlyType']").selectpicker('render');
	
	$("#form-1 select[name='goalType']").selectpicker('render');
	bootstrapSelectVal($("#form-1 select[name='goalType']"), "U");
	
	$(".input-change-1").change(function(){
		checkGoalType("form-1");
	});
	
	$("#form-1 select[name='freqCapType']").on("change.bs.select", function(e){
		validateAdvFreqCapType();
	});

	
	advertisers.forEach(function (item, index) {
		$("#form-1 select[name='advertiser']").append("<option value='" + item.value + "'>" + item.text + "</option>");
	});
	$("#form-1 select[name='advertiser']").selectpicker('refresh');

	$("#form-1 span[name='subtitle']").text(subtitle ? subtitle : "추가");
	

	$("#form-1").validate({
		rules: {
			name: {
				minlength: 2,
			},
			freqCap: {
				digits: true, min: 10,
			},
			budget: {
				digits: true,
			},
			sysValue: {
				digits: true,
			},
			goalValue: {
				digits: true,
			},
		}
	});
}


function saveForm1() {

	var advertiser = $("#form-1 select[name='advertiser']").val();
	
	var selfManaged = $("#form-1 input[name='self-managed-type-radio']:checked").val() == 'Y';
	var goalType = $("#form-1 select[name='goalType']").val();
	
	if (selfManaged && goalType == "U") {
		showAlertModal("danger", "캠페인 자체 목표 설정을 위해 광고 예산 혹은 보장/목표 노출량 값을 입력해 주십시오. 혹은 [자체 목표 없음] 옵션을 선택하십시오.");
		return;
	}
	
	if ($("#form-1").valid() && advertiser != "-1") {
		
		var freqCapType = $("#form-1 select[name='freqCapType']").val();
		var freqCap = Number($.trim($("#form-1 input[name='freqCap']").val()));
		if (freqCapType == "0") {
			freqCap = 0;
		} else if (freqCapType == "1") {
			freqCap = 1;
		}
		
    	var data = {
    		id: Number($("#form-1").attr("rowid")),
    		name: $.trim($("#form-1 input[name='name']").val()),
    		advertiser: Number($("#form-1 select[name='advertiser']").val()),
       		selfManaged: selfManaged,
    		goalType: goalType,
    		goalValue: Number($.trim($("#form-1 input[name='goalValue']").val())),
       		sysValue: Number($("#form-1 input[name='sysValue']").val()),
       		budget: Number($("#form-1 input[name='budget']").val()),
    		freqCap: freqCap,
    		impDailyType: $("#form-1 select[name='impDailyType']").val(),
    		impHourlyType: $("#form-1 select[name='impHourlyType']").val(),
    		adAgency: $.trim($("#form-1 input[name='adAgency']").val()),
    		mediaRep: $.trim($("#form-1 input[name='mediaRep']").val()),
    		memo: $.trim($("#form-1 textarea[name='memo']").val()),
    	};
    	
		$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: $("#form-1").attr("url"),
			data: JSON.stringify(data),
			success: function (data, status, xhr) {
				showSaveSuccessMsg();
				$("#form-modal-1").modal("hide");
				$("#grid").data("kendoGrid").dataSource.read();
			},
			error: ajaxSaveError
		});
	}
}


function edit(id) {
	
	// 최신의 광고주 정보부터 획득하고 시작
	$.ajax({
		type: "POST",
		contentType: "application/json",
		dataType: "json",
		url: "${readAdvUrl}",
		data: JSON.stringify({ }),
		success: function (data, status) {
			advertisers = eval(data);
			
			initForm1("변경");

			
			var dataItem = $("#grid").data("kendoGrid").dataSource.get(id);
			
			$("#form-1").attr("rowid", dataItem.id);
			$("#form-1").attr("url", "${updateUrl}");
			
			$("#form-1 input[name='name']").val(dataItem.name);
			$("#form-1 input[name='freqCap']").val(dataItem.freqCap);

			$("#form-1 input[name='adAgency']").val(dataItem.adAgency);
			$("#form-1 input[name='mediaRep']").val(dataItem.mediaRep);

			if (dataItem.selfManaged) {
				bootstrapSelectVal($("#form-1 select[name='goalType']"), dataItem.goalType);
				
				$("#form-1 input[name='budget']").val(dataItem.budget);
				$("#form-1 input[name='goalValue']").val(dataItem.goalValue);
				$("#form-1 input[name='sysValue']").val(dataItem.sysValue);
				
				$('#form-1 input:radio[name=self-managed-type-radio]:input[value="Y"]').click();
			} else {
				$('#form-1 input:radio[name=self-managed-type-radio]:input[value="N"]').click();
			}
			
			if (dataItem.freqCap == 0) {
				bootstrapSelectVal($("#form-1 select[name='freqCapType']"), "0");
				$("#form-1 input[name='freqCap']").val("60");		// 초기값으로 60
			} else if (dataItem.freqCap == 1) {
				bootstrapSelectVal($("#form-1 select[name='freqCapType']"), "1");
				$("#form-1 input[name='freqCap']").val("60");		// 초기값으로 60
			} else {
				bootstrapSelectVal($("#form-1 select[name='freqCapType']"), "2");
				$("#form-1 input[name='freqCap']").val(dataItem.freqCap);
			}
			validateAdvFreqCapType();
			
			
			bootstrapSelectVal($("#form-1 select[name='impDailyType']"), dataItem.impDailyType);
			bootstrapSelectVal($("#form-1 select[name='impHourlyType']"), dataItem.impHourlyType);
			
			bootstrapSelectVal($("#form-1 select[name='advertiser']"), dataItem.advertiser.id);

			$("#form-1 textarea[name='memo']").text(dataItem.memo);
			
			bootstrapSelectDisabled($("#form-1 select[name='impHourlyType']"), true);
			bootstrapSelectDisabled($("#form-1 select[name='advertiser']"), true);
			
			
			$('#form-modal-1 .modal-dialog').draggable({ handle: '.modal-header' });
			$("#form-modal-1").modal();
		},
		error: ajaxReadError
	});
}


function validatePurchType() {

	if ($("#form-2 select[name='purchType']").val() == "H") {
		$("#form-2 div[name='purch-type-depending-div']").hide();
	} else {
		$("#form-2 div[name='purch-type-depending-div']").show();
	}
}


function validateDurationType() {
	
	if ($("#form-2 select[name='durationType']").val() == "0") {
		$("#form-2 div[name='duration-value-div']").hide();
		$("#form-2 input[name='durSecs']").attr('readonly', 'readonly');
	} else {
		$("#form-2 input[name='durSecs']").removeAttr('readonly');
		$("#form-2 div[name='duration-value-div']").show();
		$("#form-2 input[name='durSecs']").select();
		$("#form-2 input[name='durSecs']").focus();
	}
}


function validateCpmType() {
	
	if ($("#form-2 select[name='cpmType']").val() == "0") {
		$("#form-2 div[name='cpm-value-div']").hide();
		$("#form-2 input[name='cpm']").attr('readonly', 'readonly');
	} else {
		$("#form-2 input[name='cpm']").removeAttr('readonly');
		$("#form-2 div[name='cpm-value-div']").show();
		$("#form-2 input[name='cpm']").select();
		$("#form-2 input[name='cpm']").focus();
	}
}


function validateDailyCapType() {
	
	if ($("#form-2 select[name='dailyCapType']").val() == "0") {
		$("#form-2 div[name='daily-cap-value-div']").hide();
		$("#form-2 input[name='dailyCap']").attr('readonly', 'readonly');
	} else {
		$("#form-2 input[name='dailyCap']").removeAttr('readonly');
		$("#form-2 div[name='daily-cap-value-div']").show();
		$("#form-2 input[name='dailyCap']").select();
		$("#form-2 input[name='dailyCap']").focus();
	}
}


function validateFreqCapType() {
	
	if ($("#form-2 select[name='freqCapType']").val() == "0" || $("#form-2 select[name='freqCapType']").val() == "1") {
		$("#form-2 div[name='freq-cap-value-div']").hide();
		$("#form-2 input[name='freqCap']").attr('readonly', 'readonly');
	} else {
		$("#form-2 input[name='freqCap']").removeAttr('readonly');
		$("#form-2 div[name='freq-cap-value-div']").show();
		$("#form-2 input[name='freqCap']").select();
		$("#form-2 input[name='freqCap']").focus();
	}
}


function validateDailyScrCapType() {
	
	if ($("#form-2 select[name='dailyScrCapType']").val() == "0") {
		$("#form-2 div[name='daily-scr-cap-value-div']").hide();
		$("#form-2 input[name='dailyScrCap']").attr('readonly', 'readonly');
	} else {
		$("#form-2 input[name='dailyScrCap']").removeAttr('readonly');
		$("#form-2 div[name='daily-scr-cap-value-div']").show();
		$("#form-2 input[name='dailyScrCap']").select();
		$("#form-2 input[name='dailyScrCap']").focus();
	}
}


function validateAdvFreqCapType() {
	
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


function checkGoalType(formId) {
	
	if (formId == null) {
		formId = "form-2";
	}
	
	// 예산, 보장 노출량, 목표 노출량
	var budget = Number($.trim($("#" + formId + " input[name='budget']").val()));
	var goalValue = Number($.trim($("#" + formId + " input[name='goalValue']").val()));
	var sysValue = Number($.trim($("#" + formId + " input[name='sysValue']").val()));
	
    if (goalValue > 0 || sysValue > 0) {
    	bootstrapSelectVal($("#" + formId + " select[name='goalType']"), "I");
    } else if (budget > 0) {
    	bootstrapSelectVal($("#" + formId + " select[name='goalType']"), "A");
    } else {
    	bootstrapSelectVal($("#" + formId + " select[name='goalType']"), "U");
    }
}


function initForm2(subtitle) {
	
	//
	// 이 메소드의 코드는 ad 페이지의 initForm1()을 이 페이지에 맞게 최소한의 변경을 진행
	//
	//   - form name 변경(form-1을 모두 form-2로)
	//   - campaign의 "동일 광고주 광고 송출 금지 시간", "운영자 메모"는 생략(기본값으로 대체)
	//   - campaign SELECT 없음
	//
	
	$("#formRoot").html(kendo.template($("#template-2").html()));

	$("#form-2 select[name='advertiser']").selectpicker('render');

	// 광고주 입력 모드 변경
	$("#form-2 button[name='adv-type-toggle-btn']").click(function(e) {
		e.preventDefault();
		
		if (advSelMode == true) {
			$("#form-2 div[name='adv-select-type-div']").hide();
			$("#form-2 div[name='adv-input-type-div']").show();
			
			$("#form-2 input[name='advertiser-name']").select();
			$("#form-2 input[name='advertiser-name']").focus();
		} else {
			$("#form-2 div[name='adv-select-type-div']").show();
			$("#form-2 div[name='adv-input-type-div']").hide();
		}

		advSelMode = !advSelMode;
	});
	// / 광고주 입력 모드 변경

	// 광고주 셀렉터 지연 구성
	advertisers.forEach(function (item, index) {
		$("#form-2 select[name='advertiser']").append("<option value='" + item.value + "'>" + item.text + "</option>");
	});
	$("#form-2 select[name='advertiser']").selectpicker('render');
	// 광고주 셀렉터의 초기값이 없도록
	bootstrapSelectVal($("#form-2 select[name='advertiser']"), "");

	
	// -- (S)
	//$("#form-2 select[name='campaign']").selectpicker('render');
	$("#form-2 select[name='purchType']").selectpicker('render');
	$("#form-2 select[name='durationType']").selectpicker('render');
	$("#form-2 select[name='goalType']").selectpicker('render');
	$("#form-2 select[name='cpmType']").selectpicker('render');
	$("#form-2 select[name='impDailyType']").selectpicker('render');
	$("#form-2 select[name='impHourlyType']").selectpicker('render');
	$("#form-2 select[name='impAddRatio']").selectpicker('render');
	$("#form-2 select[name='dailyCapType']").selectpicker('render');
	$("#form-2 select[name='freqCapType']").selectpicker('render');
	$("#form-2 select[name='dailyScrCapType']").selectpicker('render');
	$("#form-2 select[name='viewType']").selectpicker('render');
	
	var today = new Date();
	today.setHours(0, 0, 0, 0);
	
	var start = new Date();
	var end = new Date();
	start.setDate(today.getDate() + 2);
	end.setDate(today.getDate() + 31);
	
	$("#form-2 input[name='startDate']").kendoDatePicker({
		format: "yyyy-MM-dd",
		parseFormats: [
			"yyyy-MM-dd",
		],
		value: start,
		//min: today,
	});
	
	$("#form-2 input[name='endDate']").kendoDatePicker({
		format: "yyyy-MM-dd",
		parseFormats: [
			"yyyy-MM-dd",
		],
		value: end,
		//min: today,
	});
	
	$("#form-2 select[name='purchType']").on("change.bs.select", function(e){
		validatePurchType();
	});
	
	$("#form-2 select[name='durationType']").on("change.bs.select", function(e){
		validateDurationType();
	});
	
	$("#form-2 select[name='cpmType']").on("change.bs.select", function(e){
		validateCpmType();
	});
	
	$("#form-2 select[name='dailyCapType']").on("change.bs.select", function(e){
		validateDailyCapType();
	});
	
	$("#form-2 select[name='freqCapType']").on("change.bs.select", function(e){
		validateFreqCapType();
	});
	
	$("#form-2 select[name='dailyScrCapType']").on("change.bs.select", function(e){
		validateDailyScrCapType();
	});
	
	$(".input-change").change(function(){
		checkGoalType();
	});

	
	//bootstrapSelectVal($("#form-2 select[name='campaign']"), "-1");
	bootstrapSelectVal($("#form-2 select[name='goalType']"), "U");
	bootstrapSelectVal($("#form-2 select[name='impDailyType']"), "E");
	bootstrapSelectVal($("#form-2 select[name='impHourlyType']"), "E");
	bootstrapSelectVal($("#form-2 select[name='impAddRatio']"), "0");
	
	$("#form-2 textarea[name='memo']").keypress(function (e) {
		if (e.keyCode != 13) {
			return;
		}
		
		$(this).text().replace(/\n/g, "");
		
		return false;
	});
	
	$('[data-toggle="tooltip"]').tooltip();

	
	if (subtitle == null) {		
		// ADD 모드
		$("#form-2 input[name='startDate']").data("kendoDatePicker").min(today);
		$("#form-2 input[name='endDate']").data("kendoDatePicker").min(today);
	}

	$("#form-2 input[name='priority']").slider();
	

	$("#form-2 span[name='subtitle']").text(subtitle ? subtitle : "추가");
	

	$("#form-2").validate({
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
	// -- (E)
}


function saveForm2() {

	// kendo datepicker validation
	validateKendoDateValue($("#form-2 input[name='startDate']"));
	validateKendoDateValue($("#form-2 input[name='endDate']"));

	var adv = null;
	var advName = "";
	var advDomainName = "";
	
	if (advSelMode) {
		// 선택
		adv = Number($("#form-2 select[name='advertiser']").val());
	} else {
		// 직접 입력
		adv = -1;
		advName= $.trim($("#form-2 input[name='advertiser-name']").val());
		advDomainName= $.trim($("#form-2 input[name='advertiser-domain-name']").val());
	}
	
	if ($("#form-2").valid() && adv != null) {
		
		var goAhead = true;
		
		var durationType = $("#form-2 select[name='durationType']").val();
		var durSecs = Number($.trim($("#form-2 input[name='durSecs']").val()));
		if (durationType == "0") {
			durSecs = 0;
		} else {
			goAhead = durSecs >= 5;
		}
		
		var cpmType = $("#form-2 select[name='cpmType']").val();
		var cpm = Number($.trim($("#form-2 input[name='cpm']").val()));
		if (cpmType == "0") {
			cpm = 0;
		} else {
			goAhead = goAhead && cpm >= 1;
		}
		
		var dailyCapType = $("#form-2 select[name='dailyCapType']").val();
		var dailyCap = Number($.trim($("#form-2 input[name='dailyCap']").val()));
		if (dailyCapType == "0") {
			dailyCap = 0;
		}
		
		var freqCapType = $("#form-2 select[name='freqCapType']").val();
		var freqCap = Number($.trim($("#form-2 input[name='freqCap']").val()));
		if (freqCapType == "0") {
			freqCap = 0;
		} else if (freqCapType == "1") {
			freqCap = 1;
		}
		
		var dailyScrCapType = $("#form-2 select[name='dailyScrCapType']").val();
		var dailyScrCap = Number($.trim($("#form-2 input[name='dailyScrCap']").val()));
		if (dailyScrCapType == "0") {
			dailyScrCap = 0;
		}
		
		
    	var data = {
       		id: Number($("#form-2").attr("rowid")),
    		name: $.trim($("#form-2 input[name='name']").val()),
    		purchType: $("#form-2 select[name='purchType']").val(),
    		priority: Number($.trim($("#form-2 input[name='priority']").val())),
    		startDate: $("#form-2 input[name='startDate']").data("kendoDatePicker").value(),
    		endDate: $("#form-2 input[name='endDate']").data("kendoDatePicker").value(),
    		impAddRatio: Number($("#form-2 select[name='impAddRatio']").val()),
    		freqCap: freqCap,
    		durSecs: durSecs,
    		goalType: $("#form-2 select[name='goalType']").val(),
    		goalValue: Number($.trim($("#form-2 input[name='goalValue']").val())),
       		sysValue: Number($("#form-2 input[name='sysValue']").val()),
       		budget: Number($("#form-2 input[name='budget']").val()),
    		dailyCap: dailyCap,
    		dailyScrCap: dailyScrCap,
    		cpm: cpm,
    		impDailyType: $("#form-2 select[name='impDailyType']").val(),
    		impHourlyType: $("#form-2 select[name='impHourlyType']").val(),
    		viewType: $("#form-2 select[name='viewType']").val(),
    		memo: $.trim($("#form-2 textarea[name='memo']").val()),
    		
       		advertiser: adv,
       		advName: advName,
       		advDomainName: advDomainName,
    	};
    	
    	if (goAhead) {
        	
    		$.ajax({
    			type: "POST",
    			contentType: "application/json",
    			dataType: "json",
    			url: $("#form-2").attr("url"),
    			data: JSON.stringify(data),
    			success: function (data, status, xhr) {
    				showSaveSuccessMsg();
    				$("#form-modal-2").modal("hide");
    				$("#grid").data("kendoGrid").dataSource.read();
    			},
    			error: ajaxSaveError
    		});
    	}
	}
}


function initForm3(subtitle) {
	
	$("#formRoot").html(kendo.template($("#template-3").html()));

	$('[data-toggle="tooltip"]').tooltip();

	$("#form-3 textarea[name='memo']").keypress(function (e) {
		if (e.keyCode != 13) {
			return;
		}
		
		$(this).text().replace(/\n/g, "");
		
		return false;
	});
	
	$("#form-3 input[name='adv-input-type-radio']").change(function(){
		if ($("#form-3 input[name='adv-input-type-radio']:checked").val() == 'S') {
			$("#form-3 div[name='advertiser-select-div']").show();
			$("#form-3 div[name='advertiser-input-div']").hide();
		} else {
			$("#form-3 div[name='advertiser-select-div']").hide();
			$("#form-3 div[name='advertiser-input-div']").show();
		}
	});
	
	$("#form-3 input[name='self-managed-type-radio']").change(function(){
		if ($("#form-3 input[name='self-managed-type-radio']:checked").val() == 'Y') {
			$("#form-3 div[name='self-managed-y-div']").show();
			$("#form-3 div[name='self-managed-n-div']").hide();
		} else {
			$("#form-3 div[name='self-managed-y-div']").hide();
			$("#form-3 div[name='self-managed-n-div']").show();
		}
	});
	
	
	$("#form-3 select[name='goalType']").selectpicker('render');
	bootstrapSelectVal($("#form-3 select[name='goalType']"), "U");
	
	$(".input-change-3").change(function(){
		checkGoalType("form-3");
	});

	
	// 광고주 셀렉터 지연 구성
	advertisers.forEach(function (item, index) {
		$("#form-3 select[name='advertiser']").append("<option value='" + item.value + "'>" + item.text + "</option>");
	});
	$("#form-3 select[name='advertiser']").selectpicker('render');
	// 광고주 셀렉터의 초기값이 없도록
	bootstrapSelectVal($("#form-3 select[name='advertiser']"), "");

	$("#form-3 span[name='subtitle']").text(subtitle ? subtitle : "추가");
	

	$("#form-3").validate({
		rules: {
			name: {
				minlength: 2,
			},
			freqCap: {
				digits: true,
			},
			budget: {
				digits: true,
			},
			sysValue: {
				digits: true,
			},
			goalValue: {
				digits: true,
			},
		}
	});
}


function saveForm3() {

	var adv = null;
	var advName = "";
	var advDomainName = "";

	var selfManaged = $("#form-3 input[name='self-managed-type-radio']:checked").val() == 'Y';
	var goalType = $("#form-3 select[name='goalType']").val();
	
	if (selfManaged && goalType == "U") {
		showAlertModal("danger", "캠페인 자체 목표 설정을 위해 광고 예산 혹은 보장/목표 노출량 값을 입력해 주십시오. 혹은 [자체 목표 없음] 옵션을 선택하십시오.");
		return;
	}
	
	if ($("#form-3 input[name='adv-input-type-radio']:checked").val() == 'S') {
		// 선택
		adv = Number($("#form-3 select[name='advertiser']").val());
	} else {
		// 직접 입력
		adv = -1;
		advName= $.trim($("#form-3 input[name='advertiser-name']").val());
		advDomainName= $.trim($("#form-3 input[name='advertiser-domain-name']").val());
	}

	if ($("#form-3").valid() && (adv > 0 || (adv == -1 && advName && advDomainName))) {
    	var data = {
    		id: Number($("#form-3").attr("rowid")),
    		name: $.trim($("#form-3 input[name='name']").val()),
       		advertiser: adv,
       		advName: advName,
       		advDomainName: advDomainName,
       		selfManaged: selfManaged,
    		goalType: goalType,
    		goalValue: Number($.trim($("#form-3 input[name='goalValue']").val())),
       		sysValue: Number($("#form-3 input[name='sysValue']").val()),
       		budget: Number($("#form-3 input[name='budget']").val()),
    		memo: $.trim($("#form-3 textarea[name='memo']").val()),
    	};
    	
		$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: $("#form-3").attr("url"),
			data: JSON.stringify(data),
			success: function (data, status, xhr) {
				showSaveSuccessMsg();
				$("#form-modal-3").modal("hide");
				$("#grid").data("kendoGrid").dataSource.read();
			},
			error: ajaxSaveError
		});
	}
}


function navToAdv(advId) {
	var path = "/adc/creative/creatives/" + advId;
	location.href = path;
}


function navToCamp(campId) {
	var path = "/adc/campaign/ads/" + campId;
	location.href = path;
}

</script>


<script>

var calcPasteTgtItem = null;

function openCalc() {
	
	// 총 광고일수: 광고 시작일과 종료일의 차이로
	// 화면당 1일 노출량: 기본값 100으로
	// 매체 화면수: 컨트롤러에 의한 전달

	var totalDays = 1;
	
	var startDate = $("#form-2 input[name='startDate']").data("kendoDatePicker").value();
	var endDate = $("#form-2 input[name='endDate']").data("kendoDatePicker").value();
	
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
	
	calcPasteTgtItem = $("#form-2 input[name='goalValue']");

	
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

</script>

<!--  / Scripts -->


<!-- / Page body -->





<!-- Functional tags -->

<func:cmmValidate />


<!-- Closing tags -->

<common:base />
<common:pageClosing />
