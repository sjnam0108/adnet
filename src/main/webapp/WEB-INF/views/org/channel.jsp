<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/org/channel/create" var="createUrl" />
<c:url value="/org/channel/update" var="updateUrl" />
<c:url value="/org/channel/read" var="readUrl" />
<c:url value="/org/channel/destroy" var="destroyUrl" />

<c:url value="/org/channel/activate" var="activateUrl" />
<c:url value="/org/channel/deactivate" var="deactivateUrl" />
<c:url value="/org/channel/enableAppMode" var="enableAppModeUrl" />
<c:url value="/org/channel/disableAppMode" var="disableAppModeUrl" />

<c:url value="/org/channel/readAppendModes" var="readAppendModeUrl" />


<!-- Opening tags -->

<common:mediumPageOpening />


<!-- Page title -->

<h4 class="pt-1 pb-3 mb-3">
	<span class="mr-1 ${sessionScope['loginUser'].icon}"></span>
	${pageTitle}
</h4>

<hr class="border-light container-m--x mt-0 mb-4">





<!-- Page body -->

<link rel="stylesheet" href="/resources/vendor/lib/bootstrap-slider/bootstrap-slider.css">

<script>$.fn.slider = null</script>
<script type="text/javascript" src="/resources/vendor/lib/bootstrap-slider/bootstrap-slider.js"></script>


<!-- Java(optional)  -->

<%
	String editTemplate = 
			"<button type='button' onclick='edit(#= id #)' class='btn icon-btn btn-sm btn-outline-success borderless'>" + 
			"<span class='fas fa-pencil-alt'></span></button>";

	String noRecordsTemplate =
			"<div class='container text-center my-4'>" +
				"<div class='d-flex justify-content-center align-self-center'>" +
					"<span class='fa-thin fa-hexagon-exclamation fa-3x'></span>" +
					"<span class='pl-3 mt-2' style='font-weight: 300; font-size: 1.3rem;'>해당 자료 없음</span>" +
				"</div>" +
			"</div>";
			
	String shortNameTemplate =
			"<div>" +
				"<a href='javascript:navToScrList(#= id #)'><span class='text-link'>#= shortName #</span></a>" + 
			"</div>";
			
	String appendModeTemplate =
			"# if (appendMode == 'A') { #" +
				"<span class='fa-regular fa-robot fa-fw'></span><span class='pl-2'>자율선택</span>" +
			"# } else if (appendMode == 'P') { #" +
				"<span class='fa-regular fa-list-ol fa-fw'></span><span class='pl-2'>재생목록</span>" +
			"# } #";
			
	String lastAdAppDateTemplate = net.doohad.utils.Util.getSmartDate("lastAdAppDate");
	String lastAdReqDateTemplate = net.doohad.utils.Util.getSmartDate("lastAdReqDate");
%>


<!-- Kendo grid  -->

<div class="mb-4">
<kendo:grid name="grid" pageable="true" filterable="true" sortable="true" scrollable="true" reorderable="true" resizable="true" selectable="${value_gridSelectable}" >
	<kendo:grid-excel fileName="${pageTitle}.xlsx" allPages="true" proxyURL="/proxySave"/>
	<kendo:grid-pageable refresh="true" buttonCount="5" pageSize="10" pageSizes="${pageSizesNormal}" />
	<kendo:grid-toolbarTemplate>
    	<div class="clearfix">
    		<div class="float-left">
    			<button id="add-btn" type="button" class="btn btn-outline-success">추가</button>
    			<button type="button" class="btn btn-default d-none d-sm-inline k-grid-excel">엑셀</button>
    		</div>
    		<div class="float-right">
    			<div class="btn-group">
					<button type="button" class="btn btn-secondary dropdown-toggle" data-toggle="dropdown">
						<span class="fa-light fa-lg fa-audio-description"></span>
						<span class="pl-1">광고 편성</span>
					</button>
					<div class="dropdown-menu">
						<a class="dropdown-item" href="javascript:void(0)" id="enable-btn">
							<i class="fa-light fa-check fa-fw"></i><span class="pl-2">활성화</span>
						</a>
						<a class="dropdown-item" href="javascript:void(0)" id="disable-btn">
							<i class="fa-regular fa-blank fa-fw"></i><span class="pl-2">비활성화</span>
						</a>
					</div>
				</div>
				<div class="btn-group">
					<button type="button" class="btn btn-secondary dropdown-toggle" data-toggle="dropdown">
						<span class="fa-light fa-lg fa-wave-pulse"></span>
						<span class="pl-1">활성화 상태</span>
					</button>
					<div class="dropdown-menu">
						<a class="dropdown-item" href="javascript:void(0)" id="activate-btn">
							<i class="fa-light fa-check fa-fw"></i><span class="pl-2">활성화</span>
						</a>
						<a class="dropdown-item" href="javascript:void(0)" id="deactivate-btn">
							<i class="fa-regular fa-blank fa-fw"></i><span class="pl-2">비활성화</span>
						</a>
					</div>
				</div>
    			<button id="delete-btn" type="button" class="btn btn-danger">삭제</button>
    		</div>
    	</div>
	</kendo:grid-toolbarTemplate>
	<kendo:grid-filterable extra="false" />
	<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />
	<kendo:grid-columns>
		<kendo:grid-column title="수정" width="50" filterable="false" sortable="false" template="<%= editTemplate %>" />
		<kendo:grid-column title="채널ID" field="shortName" width="200" template="<%= shortNameTemplate %>" />
		<kendo:grid-column title="채널 이름" field="name" width="200" />
		<kendo:grid-column title="해상도" field="resolution" width="120"  
				template="#= resolution.replace('x', ' x ') #"  />
		<kendo:grid-column title="게시유형" field="viewTypeCode" width="120" />
		<kendo:grid-column title="우선순위" field="viewTypeCode" width="120" template="#= kendo.format('{0:n0}', priority) #" />
		<kendo:grid-column title="광고 추가 모드" field="appendMode" width="150" sortable="false" filterable="false" template="<%= appendModeTemplate %>" >
			<kendo:grid-column-filterable multi="true" itemTemplate="kfcIconText">
				<kendo:dataSource>
					<kendo:dataSource-transport>
						<kendo:dataSource-transport-read url="${readAppendModeUrl}" dataType="json" type="POST" contentType="application/json" />
					</kendo:dataSource-transport>
				</kendo:dataSource>
			</kendo:grid-column-filterable>
		</kendo:grid-column>
		<kendo:grid-column title="광고 편성중" field="adAppended" width="120"
				template="#=adAppended ? \"<span class='fa-light fa-check'>\" : \"\"#" />
		<kendo:grid-column title="활성화 상태" field="activeStatus" width="120"
				template="#=activeStatus ? \"<span class='fa-light fa-check'>\" : \"\"#" />
		<kendo:grid-column title="최근 편성" field="lastAdAppDate" width="150" template="<%= lastAdAppDateTemplate %>" />
		<kendo:grid-column title="최근광고요청" field="lastAdReqDate" width="150" template="<%= lastAdReqDateTemplate %>" />
		<kendo:grid-column title="구독수" field="subCount" width="150" filterable="false" sortable="false" />
	</kendo:grid-columns>
	<kendo:grid-filterable>
		<kendo:grid-filterable-messages selectedItemsFormat="{0} 항목 선택됨"/>
	</kendo:grid-filterable>
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
					<kendo:dataSource-schema-model-field name="priority" type="number" />
					<kendo:dataSource-schema-model-field name="activeStatus" type="boolean" />
					<kendo:dataSource-schema-model-field name="lastAdAppDate" type="date" />
					<kendo:dataSource-schema-model-field name="lastAdReqDate" type="date" />
				</kendo:dataSource-schema-model-fields>
			</kendo:dataSource-schema-model>
		</kendo:dataSource-schema>
	</kendo:dataSource>
</kendo:grid>
</div>

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


/* 선택 체크박스를 포함하는 필터 패널을 보기 좋게 */
.k-filter-selected-items {
	font-weight: 500;
	margin: 0.5em 0;
}
.k-filter-menu .k-button {
	width: 47%;
	margin: 0.5em 1% 0.25em;
}

</style>

<!-- / Kendo grid  -->


<!-- Grid button actions  -->

<script>
$(document).ready(function() {

	// Add
	$("#add-btn").click(function(e) {
		e.preventDefault();
		
		initForm1();

		
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
						error: function(e) {
							var msg = JSON.parse(e.responseText).error;
							if (msg == "DeleteError") {
								showDeleteErrorMsg();
							} else {
								showAlertModal("danger", msg);
							}
						}
					});
				}
			}, true, delRows.length);
		}
	});
	// / Delete
	
	// Activate
	$("#activate-btn").click(function(e) {
		e.preventDefault();
			
		var grid = $("#grid").data("kendoGrid");
		var rows = grid.select();
	
		var opRows = [];
		
		rows.each(function(index, row) {
			var selectedItem = grid.dataItem(row);
			opRows.push(selectedItem.id);
		});
		
		if (opRows.length > 0) {
			$.ajax({
				type: "POST",
				contentType: "application/json",
				dataType: "json",
				url: "${activateUrl}",
				data: JSON.stringify({ items: opRows }),
				success: function (form) {
					showOperationSuccessMsg();
					grid.dataSource.read();
				},
				error: ajaxOperationError
			});
		}
	});
	// / Activate
	
	// Deactivate
	$("#deactivate-btn").click(function(e) {
		e.preventDefault();
		
		console.log("deactivate..");
			
		var grid = $("#grid").data("kendoGrid");
		var rows = grid.select();
	
		var opRows = [];
		
		rows.each(function(index, row) {
			var selectedItem = grid.dataItem(row);
			opRows.push(selectedItem.id);
		});
		
		if (opRows.length > 0) {
			$.ajax({
				type: "POST",
				contentType: "application/json",
				dataType: "json",
				url: "${deactivateUrl}",
				data: JSON.stringify({ items: opRows }),
				success: function (form) {
					showOperationSuccessMsg();
					grid.dataSource.read();
				},
				error: ajaxOperationError
			});
		}
	});
	// / Deactivate
	
	// Enable
	$("#enable-btn").click(function(e) {
		e.preventDefault();
			
		var grid = $("#grid").data("kendoGrid");
		var rows = grid.select();
	
		var opRows = [];
		
		rows.each(function(index, row) {
			var selectedItem = grid.dataItem(row);
			opRows.push(selectedItem.id);
		});
		
		if (opRows.length > 0) {
			$.ajax({
				type: "POST",
				contentType: "application/json",
				dataType: "json",
				url: "${enableAppModeUrl}",
				data: JSON.stringify({ items: opRows }),
				success: function (form) {
					showOperationSuccessMsg();
					grid.dataSource.read();
				},
				error: ajaxOperationError
			});
		}
	});
	// / Enable
	
	// Disable
	$("#disable-btn").click(function(e) {
		e.preventDefault();
			
		var grid = $("#grid").data("kendoGrid");
		var rows = grid.select();
	
		var opRows = [];
		
		rows.each(function(index, row) {
			var selectedItem = grid.dataItem(row);
			opRows.push(selectedItem.id);
		});
		
		if (opRows.length > 0) {
			$.ajax({
				type: "POST",
				contentType: "application/json",
				dataType: "json",
				url: "${disableAppModeUrl}",
				data: JSON.stringify({ items: opRows }),
				success: function (form) {
					showOperationSuccessMsg();
					grid.dataSource.read();
				},
				error: ajaxOperationError
			});
		}
	});
	// / Disable
	
});	
</script>

<!-- / Grid button actions  -->


<!--  Root form container -->
<div id="formRoot"></div>


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
						채널 ID
						<span class="text-danger">*</span>
					</label>
					<input name="shortName" type="text" maxlength="50" class="form-control required">
				</div>
				<div class="form-group col">
					<label class="form-label">
						채널 이름
						<span class="text-danger">*</span>
					</label>
					<input name="name" type="text" maxlength="100" class="form-control required">
				</div>
				<div class="form-group col">
					<label class="form-label">
						해상도 또는 게시유형
					</label>
					<select name="type" class="selectpicker bg-white required" data-style="btn-default" data-none-selected-text="">
						<optgroup label="해상도">
<c:forEach var="item" items="${Resolutions}">
							<option value="R${item.value}">${item.text}</option>
</c:forEach>
						</optgroup>

<c:if test="${fn:length(ViewTypes) > 0 }">						
						<optgroup label="게시유형">
	<c:forEach var="item" items="${ViewTypes}">
							<option value="V${item.value}">${item.text}</option>
	</c:forEach>
						</optgroup>
</c:if>

					</select>
				</div>
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
				<div class="form-group col">
					<label class="form-label">
						광고 추가 모드
					</label>
					<select name="appendMode" class="selectpicker bg-white" data-style="btn-default" data-tick-icon="fa-blank" data-none-selected-text="">
						<option value="A" data-icon="fa-regular fa-robot fa-fw mr-1">자율 광고선택</option>
						<option value="P" data-icon="fa-regular fa-list-ol fa-fw mr-1">재생목록</option>
					</select>
				</div>
				<div class="form-group col">
					<label class="form-label">
						활성화 상태
					</label>
					<div class="pt-1">
						<label class="switcher switcher-lg">
							<input type="checkbox" class="switcher-input" name="activeStatus">
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
        
			<!-- Modal footer -->
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">취소</button>
				<button type="button" class="btn btn-primary" onclick='saveForm1()'>저장</button>
			</div>
			
		</form>
	</div>
</div>

</script>

<!--  / Forms -->


<!--  Scripts -->

<script>

function initForm1(subtitle) {
	
	$("#formRoot").html(kendo.template($("#template-1").html()));
	
	$("#form-1 select[name='type']").selectpicker('render');
	$("#form-1 select[name='appendMode']").selectpicker('render');

	bootstrapSelectVal($("#form-1 select[name='appendMode']"), "P");

	$("#form-1 input[name='priority']").slider();

	
	$("#form-1 span[name='subtitle']").text(subtitle ? subtitle : "추가");
	

	$("#form-1").validate({
		rules: {
			shortName: {
				minlength: 2, alphanumeric: true,
			},
			name: {
				minlength: 2
			},
		}
	});
}


function saveForm1() {

	if ($("#form-1").valid()) {
    	var data = {
    		id: Number($("#form-1").attr("rowid")),
    		shortName: $.trim($("#form-1 input[name='shortName']").val()),
    		name: $.trim($("#form-1 input[name='name']").val()),
    		type: $("#form-1 select[name='type']").val(),
    		priority: Number($.trim($("#form-1 input[name='priority']").val())),
    		appendMode: $("#form-1 select[name='appendMode']").val(),
    		activeStatus: $("#form-1 input[name='activeStatus']").is(':checked'),
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


function edit(id) {
	
	initForm1("변경");

	var dataItem = $("#grid").data("kendoGrid").dataSource.get(id);
	
	$("#form-1").attr("rowid", dataItem.id);
	$("#form-1").attr("url", "${updateUrl}");
	
	
	$("#form-1 input[name='shortName']").val(dataItem.shortName);
	$("#form-1 input[name='name']").val(dataItem.name);
	
	if (dataItem.viewTypeCode) {
		bootstrapSelectVal($("#form-1 select[name='type']"), "V" + dataItem.viewTypeCode);
	} else {
		bootstrapSelectVal($("#form-1 select[name='type']"), "R" + dataItem.resolution);
	}
	
	bootstrapSelectVal($("#form-1 select[name='appendMode']"), dataItem.appendMode);

	$("#form-1 input[name='activeStatus']").prop("checked", dataItem.activeStatus);
	
	$("#form-1 input[name='priority']").slider("setValue", [dataItem.priority]);

	
	$('#form-modal-1 .modal-dialog').draggable({ handle: '.modal-header' });
	$("#form-modal-1").modal();
}


function navToScrList(typeId) {
	var path = "/org/channel/screen/" + typeId;
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
