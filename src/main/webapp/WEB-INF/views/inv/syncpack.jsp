<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/inv/syncpack/create" var="createUrl" />
<c:url value="/inv/syncpack/update" var="updateUrl" />
<c:url value="/inv/syncpack/read" var="readUrl" />
<c:url value="/inv/syncpack/destroy" var="destroyUrl" />


<!-- Opening tags -->

<common:mediumPageOpening />


<!-- Page title -->

<h4 class="pt-1 pb-3 mb-3">
	<span class="mr-1 ${sessionScope['loginUser'].icon}"></span>
	${pageTitle}
</h4>

<hr class="border-light container-m--x mt-0 mb-4">





<!-- Page body -->


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
				"<a href='javascript:navToSyncList(#= id #)'><span class='text-link'>#= shortName #</span></a>" + 
			"</div>";
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
    			<button id="add-btn" type="button" class="btn btn-outline-success">추가</button>
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
		<kendo:grid-column title="묶음ID" field="shortName" width="150" template="<%= shortNameTemplate %>" />
		<kendo:grid-column title="묶음 이름" field="name" width="200" />
		<kendo:grid-column title="구독 광고 채널" field="subChannels" width="300" template="#= dispBadgeValues(subChannels) #" />
		<kendo:grid-column title="활성화 상태" field="activeStatus" width="200"
				template="#=activeStatus ? \"<span class='fa-light fa-check'>\" : \"\"#" />
		<kendo:grid-column title="화면수" field="screenCount" width="150" filterable="false" sortable="false" />
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
					<kendo:dataSource-schema-model-field name="activeStatus" type="boolean" />
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
						화면 묶음 ID
						<span class="text-danger">*</span>
					</label>
					<input name="shortName" type="text" maxlength="50" class="form-control required">
				</div>
				<div class="form-group col">
					<label class="form-label">
						화면 묶음 이름
						<span class="text-danger">*</span>
					</label>
					<input name="name" type="text" maxlength="100" class="form-control required">
				</div>
				<div class="form-group col">
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
				<div class="form-group col mb-0">
					<label class="form-label">
						운영자 메모
					</label>
					<textarea name="memo" rows="2" maxlength="150" class="form-control"></textarea>
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

<!--  / Forms -->


<!--  Scripts -->

<script>

function initForm1(subtitle) {
	
	$("#formRoot").html(kendo.template($("#template-1").html()));
	
	$("#form-1 textarea[name='memo']").keypress(function (e) {
		if (e.keyCode != 13) {
			return;
		}
		
		$(this).text().replace(/\n/g, "");
		
		return false;
	});
	
	
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
    		memo: $.trim($("#form-1 textarea[name='memo']").val()),
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

	$("#form-1 input[name='activeStatus']").prop("checked", dataItem.activeStatus);

	$("#form-1 textarea[name='memo']").text(dataItem.memo);

	
	$('#form-modal-1 .modal-dialog').draggable({ handle: '.modal-header' });
	$("#form-modal-1").modal();
}


function navToSyncList(packId) {
	var path = "/inv/syncpack/screen/" + packId;
	location.href = path;
}


function dispBadgeValues(values) {
	
	var ret = "";
	var value = values.split("|");
	  
	for(var i = 0; i < value.length; i ++) {
		if (value[i]) {
			ret = ret + "<span class='badge badge-outline-secondary'>" +
					"<span class='fa-light fa-tower-cell'></span><span class='pl-1'></span>" + 
					value[i] + "</span><span class='pl-1'></span>";
		}
	}
	  
	return ret;
}

</script>

<!--  / Scripts -->


<!-- / Page body -->





<!-- Functional tags -->

<func:cmmValidate />


<!-- Closing tags -->

<common:base />
<common:pageClosing />
