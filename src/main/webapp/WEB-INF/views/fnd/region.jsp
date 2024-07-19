<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/fnd/region/createRgn" var="createRgnUrl" />
<c:url value="/fnd/region/readRgn" var="readRgnUrl" />
<c:url value="/fnd/region/updateRgn" var="updateRgnUrl" />
<c:url value="/fnd/region/destroyRgn" var="destroyRgnUrl" />

<c:url value="/fnd/region/createSt" var="createStUrl" />
<c:url value="/fnd/region/readSt" var="readStUrl" />
<c:url value="/fnd/region/updateSt" var="updateStUrl" />
<c:url value="/fnd/region/destroySt" var="destroyStUrl" />

<c:url value="/fnd/region/createMobRgn" var="createMobRgnUrl" />
<c:url value="/fnd/region/readMobRgn" var="readMobRgnUrl" />
<c:url value="/fnd/region/updateMobRgn" var="updateMobRgnUrl" />
<c:url value="/fnd/region/destroyMobRgn" var="destroyMobRgnUrl" />


<!-- Opening tags -->

<common:pageOpening />


<!-- Page title -->

<h4 class="pt-1 pb-3 mb-3">
	<span class="mr-1 ${sessionScope['loginUser'].icon}"></span>
	${pageTitle}
</h4>




<!-- Page body -->


<!-- Java(optional)  -->

<%
	String editStTemplate = 
			"<button type='button' onclick='editSt(#= id #)' class='btn icon-btn btn-sm btn-outline-success borderless'>" + 
			"<span class='fas fa-pencil-alt'></span></button>";

	String editRgnTemplate = 
			"<button type='button' onclick='editRgn(#= id #)' class='btn icon-btn btn-sm btn-outline-success borderless'>" + 
			"<span class='fas fa-pencil-alt'></span></button>";

	String editMobRgnTemplate = 
			"<button type='button' onclick='editMobRgn(#= id #)' class='btn icon-btn btn-sm btn-outline-success borderless'>" + 
			"<span class='fas fa-pencil-alt'></span></button>";
			
	String noRecordsTemplate =
			"<div class='container text-center my-4'>" +
				"<div class='d-flex justify-content-center align-self-center'>" +
					"<span class='fa-thin fa-hexagon-exclamation fa-3x'></span>" +
					"<span class='pl-3 mt-2' style='font-weight: 300; font-size: 1.3rem;'>해당 자료 없음</span>" +
				"</div>" +
			"</div>";
%>



<!--  Forms -->

<ul class="nav nav-tabs tabs-alt container-p-x container-m--x mb-4">
	<li class="nav-item">
		<a class="nav-link active" data-toggle="tab" href="#state">
			<i class="mr-1 fa-light fa-city"></i>
			광역시/도
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" data-toggle="tab" href="#region">
			<i class="mr-1 fa-light fa-mountain-city"></i>
			시/군/구
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" data-toggle="tab" href="#mobile">
			<i class="mr-1 fa-light fa-bus"></i>
			모바일 타겟팅 지역
		</a>
	</li>
</ul>

<div class="tab-content">
	<div class="tab-pane active" id="state">



<!-- Kendo grid  -->

<div class="mb-4">
<kendo:grid name="grid-st" pageable="true" filterable="true" scrollable="false" reorderable="true" resizable="true">
	<kendo:grid-sortable mode="mixed" showIndexes="true"/>
    <kendo:grid-selectable mode="multiple, row"/>
	<kendo:grid-excel fileName="지역(광역시도).xlsx" allPages="true" proxyURL="/proxySave"/>
	<kendo:grid-pageable refresh="true" buttonCount="5" pageSize="10" pageSizes="${pageSizesNormal}" />
	<kendo:grid-toolbarTemplate>
    	<div class="clearfix">
    		<div class="float-left">
    			<button id="add-st-btn" type="button" class="btn btn-outline-success">추가</button>
    			<button type="button" class="btn btn-default d-none d-sm-inline k-grid-excel">엑셀</button>
    		</div>
    		<div class="float-right">
    			<button id="delete-st-btn" type="button" class="btn btn-danger">삭제</button>
    		</div>
    	</div>
	</kendo:grid-toolbarTemplate>
	<kendo:grid-filterable extra="false" />

   	<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />
	<kendo:grid-columns>
		<kendo:grid-column title="수정" width="50" filterable="false" sortable="false" template="<%= editStTemplate %>" />
		<kendo:grid-column title="광역시/도" field="name" />
		<kendo:grid-column title="코드" field="code" />
		<kendo:grid-column title="리스트에 포함" field="listIncluded"
				template="#=listIncluded ? \"<span class='fa-light fa-check'>\" : \"\"#" />
	</kendo:grid-columns>
	<kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="name" dir="asc"/>
		</kendo:dataSource-sort>
		<kendo:dataSource-transport>
			<kendo:dataSource-transport-read url="${readStUrl}" dataType="json" type="POST" contentType="application/json"/>
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
					<kendo:dataSource-schema-model-field name="listIncluded" type="boolean" />
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

	// Add
	$("#add-st-btn").click(function(e) {
		e.preventDefault();
		
		initForm2();

		
		$('#form-modal-2 .modal-dialog').draggable({ handle: '.modal-header' });
		$("#form-modal-2").modal();
	});
	// / Add
	
	// Delete
	$("#delete-st-btn").click(function(e) {
		e.preventDefault();
			
		var grid = $("#grid-st").data("kendoGrid");
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
						url: "${destroyStUrl}",
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


	
	</div>
	<div class="tab-pane" id="region">
                        

	
<!-- Kendo grid  -->

<div class="mb-4">
<kendo:grid name="grid-rgn" pageable="true" filterable="true" scrollable="false" reorderable="true" resizable="true">
	<kendo:grid-sortable mode="mixed" showIndexes="true"/>
    <kendo:grid-selectable mode="multiple, row"/>
	<kendo:grid-excel fileName="지역(시/군/구).xlsx" allPages="true" proxyURL="/proxySave"/>
	<kendo:grid-pageable refresh="true" buttonCount="5" pageSize="10" pageSizes="${pageSizesNormal}" />
	<kendo:grid-toolbarTemplate>
    	<div class="clearfix">
    		<div class="float-left">
    			<button id="add-rgn-btn" type="button" class="btn btn-outline-success">추가</button>
    			<button type="button" class="btn btn-default d-none d-sm-inline k-grid-excel">엑셀</button>
    		</div>
    		<div class="float-right">
    			<button id="delete-rgn-btn" type="button" class="btn btn-danger">삭제</button>
    		</div>
    	</div>
	</kendo:grid-toolbarTemplate>
	<kendo:grid-filterable extra="false" />

   	<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />
	<kendo:grid-columns>
		<kendo:grid-column title="수정" width="50" filterable="false" sortable="false" template="<%= editRgnTemplate %>" />
		<kendo:grid-column title="시/군/구" field="name" />
		<kendo:grid-column title="코드" field="code" />
		<kendo:grid-column title="리스트에 포함" field="listIncluded"
				template="#=listIncluded ? \"<span class='fa-light fa-check'>\" : \"\"#" />
	</kendo:grid-columns>
	<kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="name" dir="asc"/>
		</kendo:dataSource-sort>
		<kendo:dataSource-transport>
			<kendo:dataSource-transport-read url="${readRgnUrl}" dataType="json" type="POST" contentType="application/json"/>
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
					<kendo:dataSource-schema-model-field name="listIncluded" type="boolean" />
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

	// Add
	$("#add-rgn-btn").click(function(e) {
		e.preventDefault();
		
		initForm1();

		
		$('#form-modal-1 .modal-dialog').draggable({ handle: '.modal-header' });
		$("#form-modal-1").modal();
	});
	// / Add
	
	// Delete
	$("#delete-rgn-btn").click(function(e) {
		e.preventDefault();
			
		var grid = $("#grid-rgn").data("kendoGrid");
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
						url: "${destroyRgnUrl}",
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


	
	</div>
	<div class="tab-pane" id="mobile">



<!-- Kendo grid  -->

<div class="mb-4">
<kendo:grid name="grid-mob-rgn" pageable="true" filterable="true" scrollable="true" reorderable="true" resizable="true">
	<kendo:grid-sortable mode="mixed" showIndexes="true"/>
    <kendo:grid-selectable mode="multiple, row"/>
	<kendo:grid-excel fileName="지역(모바일타겟팅지역).xlsx" allPages="true" proxyURL="/proxySave"/>
	<kendo:grid-pageable refresh="true" buttonCount="5" pageSize="10" pageSizes="${pageSizesNormal}" />
	<kendo:grid-toolbarTemplate>
    	<div class="clearfix">
    		<div class="float-left">
    			<button id="add-mob-rgn-btn" type="button" class="btn btn-outline-success">추가</button>
    			<button type="button" class="btn btn-default d-none d-sm-inline k-grid-excel">엑셀</button>
    		</div>
    		<div class="float-right">
    			<button id="delete-mob-rgn-btn" type="button" class="btn btn-danger">삭제</button>
    		</div>
    	</div>
	</kendo:grid-toolbarTemplate>
	<kendo:grid-filterable extra="false" />

   	<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />
	<kendo:grid-columns>
		<kendo:grid-column title="수정" width="50" filterable="false" sortable="false" template="<%= editMobRgnTemplate %>" />
		<kendo:grid-column title="지역" field="name" width="200" />
		<kendo:grid-column title="지오코딩 필터명" field="gcName" width="250" />
		<kendo:grid-column title="코드" field="code" width="300" />
		<kendo:grid-column title="활성화" field="activeStatus" width="100"
				template="#= activeStatus ? \"<span class='fa-light fa-check'>\" : \"\"#" />
	</kendo:grid-columns>
	<kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="name" dir="asc"/>
		</kendo:dataSource-sort>
		<kendo:dataSource-transport>
			<kendo:dataSource-transport-read url="${readMobRgnUrl}" dataType="json" type="POST" contentType="application/json"/>
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

<!-- / Kendo grid  -->


<!-- Grid button actions  -->

<script>
$(document).ready(function() {

	// Add
	$("#add-mob-rgn-btn").click(function(e) {
		e.preventDefault();
		
		initForm3();

		
		$('#form-modal-3 .modal-dialog').draggable({ handle: '.modal-header' });
		$("#form-modal-3").modal();
	});
	// / Add
	
	// Delete
	$("#delete-mob-rgn-btn").click(function(e) {
		e.preventDefault();
			
		var grid = $("#grid-mob-rgn").data("kendoGrid");
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
						url: "${destroyMobRgnUrl}",
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


	
	</div>
</div>


<!--  Root form container -->
<div id="formRoot"></div>


<!--  Forms -->

<script id="template-1" type="text/x-kendo-template">

<div class="modal fade" data-backdrop="static" id="form-modal-1">
	<div class="modal-dialog modal-sm">
		<form class="modal-content" id="form-1" rowid="-1" url="${createRgnUrl}">
      
			<!-- Modal Header -->
			<div class="modal-header move-cursor">
				<h5 class="modal-title">
					시/군/구
					<span class="font-weight-light pl-1"><span name="subtitle"></span>
				</h5>
				<button type="button" class="close" data-dismiss="modal">×</button>
			</div>
        
			<!-- Modal body -->
			<div class="modal-body modal-bg-color">
				<div class="form-group col">
					<label class="form-label">
						시/군/구
						<span class="text-danger">*</span>
					</label>
					<input name="name" type="text" maxlength="50" class="form-control required">
				</div>
				<div class="form-group col">
					<label class="form-label">
						코드
						<span class="text-danger">*</span>
					</label>
					<input name="code" type="text" maxlength="15" class="form-control required">
				</div>
				<div class="form-group col">
					<label class="form-label">
						시/군/구 리스트에 포함
					</label>
					<div class="pt-1">
						<label class="switcher switcher-lg">
							<input type="checkbox" class="switcher-input" name="listIncluded">
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


<script id="template-2" type="text/x-kendo-template">

<div class="modal fade" data-backdrop="static" id="form-modal-2">
	<div class="modal-dialog modal-sm">
		<form class="modal-content" id="form-2" rowid="-1" url="${createStUrl}">
      
			<!-- Modal Header -->
			<div class="modal-header move-cursor">
				<h5 class="modal-title">
					광역시/도
					<span class="font-weight-light pl-1"><span name="subtitle"></span>
				</h5>
				<button type="button" class="close" data-dismiss="modal">×</button>
			</div>
        
			<!-- Modal body -->
			<div class="modal-body modal-bg-color">
				<div class="form-group col">
					<label class="form-label">
						광역시/도
						<span class="text-danger">*</span>
					</label>
					<input name="name" type="text" maxlength="50" class="form-control required">
				</div>
				<div class="form-group col">
					<label class="form-label">
						코드
						<span class="text-danger">*</span>
					</label>
					<input name="code" type="text" maxlength="15" class="form-control required">
				</div>
				<div class="form-group col">
					<label class="form-label">
						시/도 리스트에 포함
					</label>
					<div class="pt-1">
						<label class="switcher switcher-lg">
							<input type="checkbox" class="switcher-input" name="listIncluded">
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
				<button type="button" class="btn btn-primary" onclick='saveForm2()'>저장</button>
			</div>
			
		</form>
	</div>
</div>

</script>


<script id="template-3" type="text/x-kendo-template">

<div class="modal fade" data-backdrop="static" id="form-modal-3">
	<div class="modal-dialog modal-sm">
		<form class="modal-content" id="form-3" rowid="-1" url="${createMobRgnUrl}">
      
			<!-- Modal Header -->
			<div class="modal-header move-cursor">
				<h5 class="modal-title">
					모바일 타겟팅 지역
					<span class="font-weight-light pl-1"><span name="subtitle"></span>
				</h5>
				<button type="button" class="close" data-dismiss="modal">×</button>
			</div>
        
			<!-- Modal body -->
			<div class="modal-body modal-bg-color">
				<div class="form-group col">
					<label class="form-label">
						지역명
						<span class="text-danger">*</span>
					</label>
					<input name="name" type="text" maxlength="50" class="form-control required">
				</div>
				<div class="form-group col">
					<label class="form-label">
						지오코딩 필터명
						<span class="text-danger">*</span>
					</label>
					<input name="gcName" type="text" maxlength="50" class="form-control required">
				</div>
				<div class="form-group col">
					<label class="form-label">
						코드
						<span class="text-danger">*</span>
					</label>
					<input name="code" type="text" maxlength="50" class="form-control required">
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

<!--  / Forms -->


<!--  Scripts -->

<script>

function initForm1(subtitle) {
	
	$("#formRoot").html(kendo.template($("#template-1").html()));

	$("#form-1 input[name='listIncluded']").prop("checked", true);
	

	$("#form-1 span[name='subtitle']").text(subtitle ? subtitle : "추가");
	
	$("#form-1").validate({
		rules: {
			name: {
				minlength: 2
			},
			code: {
				minlength: 3, alphanumeric: true,
			},
		}
	});
}


function saveForm1() {

	if ($("#form-1").valid()) {
    	var data = {
    		id: Number($("#form-1").attr("rowid")),
    		name: $.trim($("#form-1 input[name='name']").val()),
    		code: $.trim($("#form-1 input[name='code']").val()),
    		listIncluded: $("#form-1 input[name='listIncluded']").is(':checked'),
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
				$("#grid-rgn").data("kendoGrid").dataSource.read();
			},
			error: ajaxSaveError
		});
	}
}


function editRgn(id) {
	
	initForm1("변경");

	var dataItem = $("#grid-rgn").data("kendoGrid").dataSource.get(id);
	
	$("#form-1").attr("rowid", dataItem.id);
	$("#form-1").attr("url", "${updateRgnUrl}");
	
	$("#form-1 input[name='name']").val(dataItem.name);
	$("#form-1 input[name='code']").val(dataItem.code);

	$("#form-1 input[name='listIncluded']").prop("checked", dataItem.listIncluded);

	
	$('#form-modal-1 .modal-dialog').draggable({ handle: '.modal-header' });
	$("#form-modal-1").modal();
}


function initForm2(subtitle) {
	
	$("#formRoot").html(kendo.template($("#template-2").html()));

	$("#form-2 input[name='listIncluded']").prop("checked", true);
	

	$("#form-2 span[name='subtitle']").text(subtitle ? subtitle : "추가");
	
	$("#form-2").validate({
		rules: {
			name: {
				minlength: 2
			},
			code: {
				minlength: 2, alphanumeric: true,
			},
		}
	});
}


function saveForm2() {

	if ($("#form-2").valid()) {
    	var data = {
    		id: Number($("#form-2").attr("rowid")),
    		name: $.trim($("#form-2 input[name='name']").val()),
    		code: $.trim($("#form-2 input[name='code']").val()),
    		listIncluded: $("#form-2 input[name='listIncluded']").is(':checked'),
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
				$("#grid-st").data("kendoGrid").dataSource.read();
			},
			error: ajaxSaveError
		});
	}
}


function editSt(id) {
	
	initForm2("변경");

	var dataItem = $("#grid-st").data("kendoGrid").dataSource.get(id);
	
	$("#form-2").attr("rowid", dataItem.id);
	$("#form-2").attr("url", "${updateStUrl}");
	
	$("#form-2 input[name='name']").val(dataItem.name);
	$("#form-2 input[name='code']").val(dataItem.code);

	$("#form-2 input[name='listIncluded']").prop("checked", dataItem.listIncluded);

	
	$('#form-modal-2 .modal-dialog').draggable({ handle: '.modal-header' });
	$("#form-modal-2").modal();
}


function initForm3(subtitle) {
	
	$("#formRoot").html(kendo.template($("#template-3").html()));

	$("#form-3 input[name='activeStatus']").prop("checked", true);
	

	$("#form-3 span[name='subtitle']").text(subtitle ? subtitle : "추가");
	
	$("#form-3").validate({
		rules: {
			name: {
				minlength: 2
			},
			gcName: {
				minlength: 2
			},
			code: {
				minlength: 2,
			},
		}
	});
}


function saveForm3() {

	if ($("#form-3").valid()) {
    	var data = {
    		id: Number($("#form-3").attr("rowid")),
    		name: $.trim($("#form-3 input[name='name']").val()),
    		gcName: $.trim($("#form-3 input[name='gcName']").val()),
    		code: $.trim($("#form-3 input[name='code']").val()),
    		activeStatus: $("#form-3 input[name='activeStatus']").is(':checked'),
    	};
    	
		$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: $("#form-3").attr("url"),
			data: JSON.stringify(data),
			success: function (form) {
				showSaveSuccessMsg();
				$("#form-modal-3").modal("hide");
				$("#grid-mob-rgn").data("kendoGrid").dataSource.read();
			},
			error: ajaxSaveError
		});
	}
}


function editMobRgn(id) {
	
	initForm3("변경");

	var dataItem = $("#grid-mob-rgn").data("kendoGrid").dataSource.get(id);
	
	$("#form-3").attr("rowid", dataItem.id);
	$("#form-3").attr("url", "${updateMobRgnUrl}");
	
	$("#form-3 input[name='name']").val(dataItem.name);
	$("#form-3 input[name='gcName']").val(dataItem.gcName);
	$("#form-3 input[name='code']").val(dataItem.code);

	$("#form-3 input[name='activeStatus']").prop("checked", dataItem.activeStatus);

	
	$('#form-modal-3 .modal-dialog').draggable({ handle: '.modal-header' });
	$("#form-modal-3").modal();
}

</script>

<!--  / Scripts -->


<!-- / Page body -->





<!-- Functional tags -->

<func:cmmValidate />


<!-- Closing tags -->

<common:base />
<common:pageClosing />
