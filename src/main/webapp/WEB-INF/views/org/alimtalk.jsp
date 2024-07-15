<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/org/alimtalk/read" var="readUrl" />
<c:url value="/org/alimtalk/destroy" var="destroyUrl" />

<c:url value="/org/alimtalk/createActScr" var="createActScrUrl" />
<c:url value="/org/alimtalk/updateActScr" var="updateActScrUrl" />
<c:url value="/org/alimtalk/createTest" var="createTestUrl" />


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

<link rel="stylesheet" href="/resources/vendor/lib/bootstrap-tagsinput/bootstrap-tagsinput.css">

<script src="/resources/vendor/lib/bootstrap-tagsinput/bootstrap-tagsinput.js"></script>


<!-- Java(optional)  -->

<%
	String editTemplate = 
			"<button type='button' onclick='editActScr(#= id #)' class='btn icon-btn btn-sm btn-outline-success borderless'>" + 
			"<span class='fas fa-pencil-alt'></span></button>";

	String noRecordsTemplate =
			"<div class='container text-center my-4'>" +
				"<div class='d-flex justify-content-center align-self-center'>" +
					"<span class='fa-thin fa-hexagon-exclamation fa-3x'></span>" +
					"<span class='pl-3 mt-2' style='font-weight: 300; font-size: 1.3rem;'>해당 자료 없음</span>" +
				"</div>" +
			"</div>";
			
	String checkListTemplate =
			"<span>" +
			"# if (eventType = 'ActScr') { #" +
				"<small>활성 화면수 </small>#= cfStr1 # <small>미만, </small>#= cfStr2 #<small> 회 연속</small>" +
			"# } #" +
			"</span>";
%>


<!-- Kendo grid  -->

<div class="mb-4">
<kendo:grid name="grid" pageable="true" filterable="true" scrollable="true" reorderable="true" resizable="true">
	<kendo:grid-sortable mode="mixed" showIndexes="true"/>
    <kendo:grid-selectable mode="multiple, raw"/>
	<kendo:grid-excel fileName="${pageTitle}.xlsx" allPages="true" proxyURL="/proxySave"/>
	<kendo:grid-pageable refresh="true" buttonCount="5" pageSize="10" pageSizes="${pageSizesNormal}" />
	<kendo:grid-toolbarTemplate>
    	<div class="clearfix">
    		<div class="float-left">
    			<div class="btn-group">
    			<button type="button" class="btn btn-outline-success dropdown-toggle" data-toggle="dropdown">알림톡 추가</button>
					<div class="dropdown-menu">
						<a class="dropdown-item" href="javascript:void(0)" id="add-act-scr-btn">
							<i class="fa-light fa-monitor-waveform fa-fw"></i><span class="pl-2">활성 화면 모니터링</span>
						</a>
					</div>
    			</div>
    			<button id="test-btn" type="button" class="btn btn-default d-none d-sm-inline">테스트</button>
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
		<kendo:grid-column title="알림톡ID" field="shortName" width="120" />
		<kendo:grid-column title="점검 요약" field="checkList" width="250" filterable="false" sortable="false" template="<%= checkListTemplate %>" />
		<kendo:grid-column title="운영시간(hrs)" field="bizHours" width="120" filterable="false" sortable="false" />
		<kendo:grid-column title="알림톡 수신" field="subCount" width="120" filterable="false" sortable="false" />
		<kendo:grid-column title="점검지연(분)" field="delayChkMins" width="120" filterable="false" />
		<kendo:grid-column title="재알림대기(분)" field="waitMins" width="120" filterable="false" />
		<kendo:grid-column title="활성화?" field="activeStatus" width="120"
				template="#=activeStatus ? \"<span class='fa-light fa-check'>\" : \"\"#" />
	</kendo:grid-columns>
	<kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="shortName" dir="asc"/>
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
					<kendo:dataSource-schema-model-field name="activeStatus" type="boolean" />
					<kendo:dataSource-schema-model-field name="bizHours" type="number" />
					<kendo:dataSource-schema-model-field name="subCount" type="number" />
					<kendo:dataSource-schema-model-field name="delayChkMins" type="number" />
				</kendo:dataSource-schema-model-fields>
			</kendo:dataSource-schema-model>
		</kendo:dataSource-schema>
	</kendo:dataSource>
</kendo:grid>
</div>

<style>

/* 폼에서 열 사이의 간격을 원래대로 넓게.
   modal medium size에서 form-row를 이용한 경우에만 적용되고,
   modal-lg에는 적용하지 않는 것이 원칙 */
.form-row>.col, .form-row>[class*="col-"] {
	padding-right: .75rem;
	padding-left: .75rem;
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


/* 시간 설정 테이블 */
table.time-table {
	border-spacing: 0px;
}
table.time-table th {
	font-weight: 400;
}
table.time-table td {
	border: 1px solid #fff;
	width: 28px;
	height: 30px;
	margin: 10px;
	background-color: rgba(24,28,33,0.06) !important;
	cursor: default;
	padding: 0px;
}
table.time-table td.selected {
	background-color: #02a96b !important;
}
table.time-table td.rselected {
	background-color: #02BC77 !important;
}
table.time-table td.nzselected {
	background-color: rgba(2, 188, 119, 0.5) !important;
}


/* 커스텀 폼 컨트롤 */
.form-control-custom-select {
	display: inline-block;
	width: 70px;
	font-size: 1.1175rem;
	margin: 0 0.5rem;
}

.form-control-custom-text {
	display: inline-block;
	width: 70px;
	font-size: 1.1175rem;
	margin: 0 0.5rem;
	text-align: center;
}

.form-control-custom-dropdown {
	margin: 0 0.5rem;
	vertical-align: baseline;
}

a.custom-font {
	font-size: 1.1175rem;
	cursor: default;
}

</style>

<!-- / Kendo grid  -->


<!-- Grid button actions  -->

<script>
$(document).ready(function() {

	// Add
	$("#add-act-scr-btn").click(function(e) {
		e.preventDefault();
		
		initFormActScr();

		
		$('#form-modal-1 .modal-dialog').draggable({ handle: '.modal-header' });
		$("#form-modal-1").modal();
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

	// Test
	$("#test-btn").click(function(e) {
		e.preventDefault();
		
		initFormTest();
	
	
		$('#form-modal-2 .modal-dialog').draggable({ handle: '.modal-header' });
		$("#form-modal-2").modal();
	});
	// / Test

});	


</script>

<!-- / Grid button actions  -->


<!--  Root form container -->
<div id="formRoot"></div>


<!--  Forms -->

<script id="template-1" type="text/x-kendo-template">

<div class="modal fade" data-backdrop="static" id="form-modal-1">
	<div class="modal-dialog modal-lg">
		<form class="modal-content" id="form-1" rowid="-1" url="${createActScrUrl}">
      
			<!-- Modal Header -->
			<div class="modal-header move-cursor">
				<h5 class="modal-title">
					활성 화면 모니터링 알림톡
					<span class="font-weight-light pl-1"><span name="subtitle"></span></span>
				</h5>
				<button type="button" class="close" data-dismiss="modal">×</button>
			</div>
        
			<!-- Modal body -->
			<div class="modal-body modal-bg-color">
				<div class="form-row">
					<div class="form-group col-10">
						<label class="form-label">
							알림톡ID
							<span class="text-danger">*</span>
						</label>
						<input name="shortName" type="text" maxlength="70" class="form-control required">
					</div>
					<div class="form-group col-2">
						<label class="form-label">
							활성화 상태
						</label>
						<div class="pt-1">
							<label class="switcher switcher-lg">
								<input type="checkbox" class="switcher-input" name="activeStatus" checked>
								<span class="switcher-indicator">
									<span class="switcher-yes">
										<span class="fa-solid fa-check"></span>
									</span>
									<span class="switcher-no">
										<span class="fa-solid fa-xmark"></span>
									</span>
								</span>
							</label>
						</div>
					</div>
				</div>
				<div class="form-row">
					<div class="form-group col light-style">
						<label class="form-label">
							알림톡 수신 휴대폰번호
						</label>
						<input name="subscribers" type="text" maxlength="250" class="form-control required">
					</div>
				</div>
				<div class="form-row">
					<div class="form-group col mb-0">
						<label class="form-label">
							운영/이벤트 점검 시간
						</label>
					</div>
				</div>
				<div class="form-row">
					<div class="mx-auto">

<common:timetable />

					</div>
				</div>
				<div class="form-row mt-4 mb-0">
					<div class="form-group col lead mb-0">
						<div class="lead">
							<div>
								1분 주기로 점검하는 동안, 활성 화면수가
								<input name="scrCount" type="text" maxlength="10" class="form-control form-control-custom-text" value="">
								미만인 상태로
								<div class="btn-group form-control-custom-dropdown">
									<a class="btn btn-default dropdown-toggle bg-white custom-font" data-toggle="dropdown" href="\\\\#"><span id="failCount"></span></a>
									<ul class="dropdown-menu dropdown-menu-custom">
										<li><a class="custom-font" style="font-weight: 300;">1</a></li>
										<li><a class="custom-font" style="font-weight: 300;">3</a></li>
										<li><a class="custom-font" style="font-weight: 300;">5</a></li>
										<li><a class="custom-font" style="font-weight: 300;">10</a></li>
									</ul>
								</div>
								회 연속 확인되면 알림톡을 발송합니다.
							</div>
							<div class="mt-3">
								점검 시작은
								<input name="delayMins" type="text" maxlength="10" class="form-control form-control-custom-text" value="">
								분 경과후에 시작하고,
								알림톡 발송 후,
								<div class="btn-group form-control-custom-dropdown">
									<a class="btn btn-default dropdown-toggle bg-white custom-font" data-toggle="dropdown" href="\\\\#"><span id="coolMins"></span></a>
									<ul class="dropdown-menu dropdown-menu-custom">
										<li><a class="custom-font" style="font-weight: 300;">30</a></li>
										<li><a class="custom-font" style="font-weight: 300;">60</a></li>
										<li><a class="custom-font" style="font-weight: 300;">120</a></li>
										<li><a class="custom-font" style="font-weight: 300;">180</a></li>
									</ul>
								</div>
								분 동안은 동일한 내용의 알림을 하지 않습니다.
							</div>
						</div>
					</div>
				</div>
			</div>
        
			<!-- Modal footer -->
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">취소</button>
				<button type="button" class="btn btn-primary" onclick='saveFormActScr()'>저장</button>
			</div>
			
		</form>
	</div>
</div>

</script>

<script id="template-2" type="text/x-kendo-template">

<div class="modal fade" data-backdrop="static" id="form-modal-2">
	<div class="modal-dialog modal-sm">
		<form class="modal-content" id="form-2" rowid="-1" url="${createTestUrl}">
      
			<!-- Modal Header -->
			<div class="modal-header move-cursor">
				<h5 class="modal-title">
					알림톡 테스트 발송
				</h5>
				<button type="button" class="close" data-dismiss="modal">×</button>
			</div>
        
			<!-- Modal body -->
			<div class="modal-body modal-bg-color">
				<div class="form-row">
					<div class="form-group col light-style">
						<label class="form-label">
							알림톡 수신 휴대폰번호
							<span class="text-danger">*</span>
						</label>
						<input name="subscribers" type="text" maxlength="250" class="form-control required">
					</div>
				</div>
			</div>
        
			<!-- Modal footer -->
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">취소</button>
				<button type="button" class="btn btn-primary" onclick='saveFormTest()'>진행</button>
			</div>
			
		</form>
	</div>
</div>

</script>

<!--  / Forms -->


<!--  Scripts -->

<script>

function initFormActScr(subtitle) {
	
	$("#formRoot").html(kendo.template($("#template-1").html()));

	$("#form-1 input[name='subscribers']").tagsinput({
		trimValue: true,
		tagClass: 'badge badge-outline-secondary text-secondary',
		maxChars: 11,
		maxTags: 20,
	});
	
	$("#form-1 input[name='subscribers']").on('beforeItemAdd', function(event) {
		
		var item = event.item;
		
		if (item.length == 13) {
			// 아래 add에 의해 재귀호출되었을때, 정상 성공을 위해.
			
		} else if (item.replace(/[\D]/g,"") == item && event.item.startsWith("010")) {
			event.cancel = true;
			event.preventDefault = true;
			item = "010-" + item.substr(3, 4) + "-" + item.substr(7);
			$(this).tagsinput('add', item);
		} else {
			event.cancel = true;
		}
	});

	// Time table 제어 코드
	ttSetTimeTable($("#form-1 table[name='time-table']"));
	ttDisplayHours("${mediumBizHours}");
	
	$("#form-1 input[name='scrCount']").val("${activeScrCnt}");
	$("#form-1 input[name='delayMins']").val("5");

	$("#failCount").text("5");
	$("#coolMins").text("30");
	
	$(".dropdown-menu li a").click(function(){
		var selText = $(this).text();
		$(this).parents('.btn-group').find('.dropdown-toggle span').html(selText);
	});	

	
	$("#form-1 span[name='subtitle']").text(subtitle ? subtitle : "추가");
	

	$("#form-1").validate({
		rules: {
			shortName: {
				minlength: 2, alphanumeric: true,
			},
		}
	});
}


function saveFormActScr() {

	var scrCount = Number($.trim($("#form-1 input[name='scrCount']").val()));
	var delayMins = Number($.trim($("#form-1 input[name='delayMins']").val()));
	var failCount = Number($("#failCount").text());
	var coolMins = Number($("#coolMins").text());
	
	if ($("#form-1").valid() && scrCount > 0 && delayMins >= 0 && delayMins < 60) {
    	var data = {
    		id: Number($("#form-1").attr("rowid")),
    		shortName: $.trim($("#form-1 input[name='shortName']").val()),
    		subscribers: $.trim($("#form-1 input[name='subscribers']").val()),
    		bizHours: ttGetValueStr(),
    		activeStatus: $("#form-1 input[name='activeStatus']").is(':checked'),
    		scrCount: scrCount,
    		failCount: failCount,
    		coolMins: coolMins,
    		delayMins: delayMins,
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
				$("#grid").data("kendoGrid").dataSource.read();
			},
			error: ajaxSaveError
		});
	}
}


function editActScr(id) {
	
	initFormActScr("변경");

	var dataItem = $("#grid").data("kendoGrid").dataSource.get(id);
	
	$("#form-1").attr("rowid", dataItem.id);
	$("#form-1").attr("url", "${updateActScrUrl}");
	
	$("#form-1 input[name='shortName']").val(dataItem.shortName);
	$("#form-1 input[name='activeStatus']").prop("checked", dataItem.activeStatus);
	
	var value = dataItem.subscribers.split("|");
	for(var i = 0; i < value.length; i ++) {
		if (value[i]) {
			$("#form-1 input[name='subscribers']").tagsinput('add', value[i]);
		}
	}

	ttDisplayHours(dataItem.bizHour);
	
	$("#form-1 input[name='scrCount']").val(dataItem.cfStr1);
	$("#form-1 input[name='delayMins']").val(dataItem.delayChkMins);
	$("#failCount").text(dataItem.cfStr2);
	$("#coolMins").text(dataItem.waitMins);
	

	$('#form-modal-1 .modal-dialog').draggable({ handle: '.modal-header' });
	$("#form-modal-1").modal();
}


function initFormTest() {
	
	$("#formRoot").html(kendo.template($("#template-2").html()));

	$("#form-2 input[name='subscribers']").tagsinput({
		trimValue: true,
		tagClass: 'badge badge-outline-secondary text-secondary',
		maxChars: 11,
		maxTags: 20,
	});
	
	$("#form-2 input[name='subscribers']").on('beforeItemAdd', function(event) {
		
		var item = event.item;
		
		if (item.length == 13) {
			// 아래 add에 의해 재귀호출되었을때, 정상 성공을 위해.
			
		} else if (item.replace(/[\D]/g,"") == item && event.item.startsWith("010")) {
			event.cancel = true;
			event.preventDefault = true;
			item = "010-" + item.substr(3, 4) + "-" + item.substr(7);
			$(this).tagsinput('add', item);
		} else {
			event.cancel = true;
		}
	});

	
	$("#form-2").validate({
		rules: {
		}
	});
}


function saveFormTest() {

	var subscribers = $.trim($("#form-2 input[name='subscribers']").val());
	
	if ($("#form-2").valid() && subscribers) {
    	var data = {
    		subscribers: subscribers,
    	};
    	
		$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: $("#form-2").attr("url"),
			data: JSON.stringify(data),
			success: function (form) {
				showSaveSuccessMsg();
				$("#form-modal-2").modal("hide");
				$("#grid").data("kendoGrid").dataSource.read();
			},
			error: ajaxSaveError
		});
	}
}

</script>

<!--  / Scripts -->


<!-- / Page body -->





<!-- Functional tags -->

<func:cmmTimeTable />
<func:cmmValidate />


<!-- Closing tags -->

<common:base />
<common:pageClosing />
