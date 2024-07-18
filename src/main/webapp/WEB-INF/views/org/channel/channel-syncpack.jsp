<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ taglib prefix="org" tagdir="/WEB-INF/tags/org"%>


<!-- URL -->

<c:url value="/org/channel/syncpack/read" var="readUrl" />

<c:url value="/org/channel/syncpack/createWithNames" var="createWithNameUrl" />
<c:url value="/org/channel/syncpack/createWithShortNames" var="createWithShortNameUrl" />
<c:url value="/org/channel/syncpack/destroy" var="destroyUrl" />


<!-- Opening tags -->

<common:mediumPageOpening />


<!-- Page title -->

<h4 class="pt-1 pb-3 mb-3">
	<span class="text-muted font-weight-light">${pageTitle}<span class="px-2">/</span></span>
	<span class="mr-1 fa-light fa-rectangle-vertical-history"></span><span class="pl-1">동기화 화면 묶음</span>
</h4>

<hr class="border-light container-m--x mt-0 mb-4">





<!-- Page body -->


<!--  Overview header -->

<org:channel />

<!--  / Overview header -->


<!--  Tab -->
<ul class="nav nav-tabs tabs-alt mb-4 mt-3">
	<li class="nav-item">
		<a class="nav-link" href="/org/channel/screen/${Channel.id}">
			<i class="mr-1 fa-light fa-screen-users"></i>
			화면
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link active" href="/org/channel/syncpack/${Channel.id}">
			<i class="mr-1 fa-light fa-rectangle-vertical-history"></i>
			동기화 화면 묶음
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" href="/org/channel/ad/${Channel.id}">
			<i class="mr-1 fa-light fa-audio-description"></i>
			채널 광고
		</a>
	</li>
	<li class="nav-item mr-auto">
		<a class="nav-link" href="/org/channel/playlist/${Channel.id}">
			<i class="mr-1 fa-light fa-list-ol"></i>
			재생목록
		</a>
	</li>

<c:if test="${fn:length(currChannels) > 0}" >

	<select name="nav-item-chan-select" class="selectpicker bg-white mb-1" data-style="btn-default" data-none-selected-text="" data-width="250px" data-size="10" >

<c:forEach var="item" items="${currChannels}">

		<option value="${item.value}" data-content="<span class='fa-light ${item.icon}'></span><span class='pl-2'>${item.text}</span><span class='small pl-3 opacity-75'>${item.subIcon}</span>"></option>

</c:forEach>

	</select>

<script>
$(document).ready(function() {

	$("select[name='nav-item-chan-select']").selectpicker('render');

	$("select[name='nav-item-chan-select']").on("change.bs.select", function(e){
		location.href = "/org/channel/syncpack/" + $("select[name='nav-item-chan-select']").val();
	});
	
	bootstrapSelectVal($("select[name='nav-item-chan-select']"), "${currChanId}");
	
});	
</script>

</c:if>
	
</ul>
<!--  / Tab -->


<!--  Root form container -->
<div id="formRoot"></div>


<!-- Java(optional)  -->

<%
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
<kendo:grid name="grid" pageable="true" filterable="true" scrollable="true" reorderable="true" resizable="true">
	<kendo:grid-sortable mode="mixed" showIndexes="true"/>
    <kendo:grid-selectable mode="multiple, raw"/>
	<kendo:grid-excel fileName="${pageTitle}.xlsx" allPages="true" proxyURL="/proxySave"/>
	<kendo:grid-pageable refresh="true" buttonCount="5" pageSize="10" pageSizes="${pageSizesNormal}" />
	<kendo:grid-toolbarTemplate>
    	<div class="clearfix">
    		<div class="float-left">
    			<div class="btn-group">
					<button type="button" class="btn btn-outline-success dropdown-toggle" data-toggle="dropdown">추가</button>
					<div class="dropdown-menu">
						<a class="dropdown-item" href="javascript:void(0)" id="add-by-name-btn">
							묶음 이름 목록으로
						</a>
						<a class="dropdown-item" href="javascript:void(0)" id="add-by-shortName-btn">
							묶음ID 목록으로
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
		<kendo:grid-column title="묶음ID" field="shortName" width="150" />
		<kendo:grid-column title="묶음 이름" field="name" width="250" />
		<kendo:grid-column title="활성화 상태" field="activeStatus" width="150"
				template="#=activeStatus ? \"<span class='fa-light fa-check'>\" : \"\"#" />
		<kendo:grid-column title="화면수" field="screenCount" width="150" filterable="false" sortable="false" />
	</kendo:grid-columns>
	<kendo:grid-filterable>
		<kendo:grid-filterable-messages selectedItemsFormat="{0} 항목 선택됨"/>
	</kendo:grid-filterable>
	<kendo:dataSource serverPaging="false" serverSorting="false" serverFiltering="false" serverGrouping="false" error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="shortName" dir="asc"/>
		</kendo:dataSource-sort>
		<kendo:dataSource-transport>
			<kendo:dataSource-transport-read url="${readUrl}" dataType="json" type="POST" contentType="application/json">
				<kendo:dataSource-transport-read-data>
					<script>
						function additionalData(e) {
							return { reqIntValue1:  ${Channel.id} };
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
					<kendo:dataSource-schema-model-field name="activeStatus" type="boolean" />
				</kendo:dataSource-schema-model-fields>
			</kendo:dataSource-schema-model>
		</kendo:dataSource-schema>
	</kendo:dataSource>
</kendo:grid>
</div>

<style>




/* 그리드 행의 높이 지정 */
.k-grid tbody tr, .k-grid tbody tr td
{
    height: 40px;
}

</style>

<!-- / Kendo grid  -->


<!-- Grid button actions  -->

<script>
$(document).ready(function() {

	// Add
	$("#add-by-name-btn").click(function(e) {
		e.preventDefault();
		
		initForm1();

		
		$('#form-modal-1 .modal-dialog').draggable({ handle: '.modal-header' });
		$("#form-modal-1").modal();
	});
	
	$("#add-by-shortName-btn").click(function(e) {
		e.preventDefault();
		
		initForm2();

		
		$('#form-modal-2 .modal-dialog').draggable({ handle: '.modal-header' });
		$("#form-modal-2").modal();
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


<!--  Forms -->

<script id="template-1" type="text/x-kendo-template">

<div class="modal fade" data-backdrop="static" id="form-modal-1">
	<div class="modal-dialog modal-sm">
		<form class="modal-content" id="form-1" rowid="-1" url="${createWithNameUrl}">
      
			<!-- Modal Header -->
			<div class="modal-header move-cursor">
				<h5 class="modal-title">
					구독
					<span class="font-weight-light pl-1">묶음 이름으로 추가</span>
				</h5>
				<button type="button" class="close" data-dismiss="modal">×</button>
			</div>
        
			<!-- Modal body -->
			<div class="modal-body modal-bg-color">
				<div class="form-group col mb-0">
					<label class="form-label">
						묶음 이름
						<span class="text-danger">*</span>
					</label>
					<textarea name="list" rows="15" maxlength="2000" class="form-control required"></textarea>
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
		<form class="modal-content" id="form-2" rowid="-1" url="${createWithShortNameUrl}">
      
			<!-- Modal Header -->
			<div class="modal-header move-cursor">
				<h5 class="modal-title">
					구독
					<span class="font-weight-light pl-1">묶음ID로 추가</span>
				</h5>
				<button type="button" class="close" data-dismiss="modal">×</button>
			</div>
        
			<!-- Modal body -->
			<div class="modal-body modal-bg-color">
				<div class="form-group col mb-0">
					<label class="form-label">
						묶음ID
						<span class="text-danger">*</span>
					</label>
					<textarea name="list" rows="15" maxlength="2000" class="form-control required"></textarea>
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

<!--  / Forms -->


<!--  Scripts -->

<script>

function initForm1(subtitle) {
	
	$("#formRoot").html(kendo.template($("#template-1").html()));
	

	$("#form-1").validate({
		rules: {
		}
	});
}


function saveForm1() {

	if ($("#form-1").valid()) {
    	var data = {
    		id: ${Channel.id},
    		list: $.trim($("#form-1 textarea[name='list']").val()),
    	};
    	
		$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: $("#form-1").attr("url"),
			data: JSON.stringify(data),
			success: function (form, status, xhr) {
				// 따옴표 제거
				var respText = xhr.responseText.substring(1, xhr.responseText.length - 1);
				if (respText == "Ok") {
					showSaveSuccessMsg();
				} else {
					var msg = "다음의 요청 항목은 동기화 화면 묶음 미등록으로 확인되어 작업을 수행할 수 없습니다:<br>" + respText;
					showAlertModal("info", msg);
				}
				$("#form-modal-1").modal("hide");
				$("#grid").data("kendoGrid").dataSource.read();
			},
			error: ajaxSaveError
		});
	}
}


function initForm2(subtitle) {
	
	$("#formRoot").html(kendo.template($("#template-2").html()));
	

	$("#form-2").validate({
		rules: {
		}
	});
}


function saveForm2() {

	if ($("#form-2").valid()) {
    	var data = {
    		id: ${Channel.id},
    		list: $.trim($("#form-2 textarea[name='list']").val()),
    	};
    	
		$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: $("#form-2").attr("url"),
			data: JSON.stringify(data),
			success: function (form, status, xhr) {
				// 따옴표 제거
				var respText = xhr.responseText.substring(1, xhr.responseText.length - 1);
				if (respText == "Ok") {
					showSaveSuccessMsg();
				} else {
					var msg = "다음의 요청 항목은 동기화 화면 묶음 미등록으로 확인되어 작업을 수행할 수 없습니다:<br>" + respText;
					showAlertModal("info", msg);
				}
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

<func:screenInfoModal />
<func:cmmValidate />


<!-- Closing tags -->

<common:base />
<common:pageClosing />
