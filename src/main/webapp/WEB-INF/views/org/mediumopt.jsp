<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/org/mediumopt/createAdv" var="createAdvUrl" />
<c:url value="/org/mediumopt/readAdv" var="readAdvUrl" />
<c:url value="/org/mediumopt/updateAdv" var="updateAdvUrl" />
<c:url value="/org/mediumopt/destroyAdv" var="destroyAdvUrl" />

<c:url value="/org/mediumopt/createCond" var="createCondUrl" />
<c:url value="/org/mediumopt/updateCond" var="updateCondUrl" />
<c:url value="/org/mediumopt/readCond" var="readCondUrl" />
<c:url value="/org/mediumopt/destroyCond" var="destroyCondUrl" />

<c:url value="/org/mediumopt/readOpt" var="readOptUrl" />


<!-- Opening tags -->

<common:mediumPageOpening />


<!-- Page title -->

<h4 class="pt-1 pb-3 mb-3">
	<span class="mr-1 ${sessionScope['loginUser'].icon}"></span>
	${pageTitle}
</h4>




<!-- Page body -->


<!-- Java(optional)  -->

<%
	String editAdvTemplate = 
			"<button type='button' onclick='editAdv(#= id #)' class='btn icon-btn btn-sm btn-outline-success borderless'>" + 
			"<span class='fas fa-pencil-alt'></span></button>";

	String editCondTemplate = 
			"<button type='button' onclick='editCond(#= id #)' class='btn icon-btn btn-sm btn-outline-success borderless'>" + 
			"<span class='fas fa-pencil-alt'></span></button>";

			
	String noRecordsTemplate =
			"<div class='container text-center my-4'>" +
				"<div class='d-flex justify-content-center align-self-center'>" +
					"<span class='fa-thin fa-hexagon-exclamation fa-3x'></span>" +
					"<span class='pl-3 mt-2' style='font-weight: 300; font-size: 1.3rem;'>해당 자료 없음</span>" +
				"</div>" +
			"</div>";

			
	String advNameTemplate =
			"<a href='javascript:navToAdv(#= id #)'><span class='text-link'>#= name #</span></a>";
			
	String optIDTemplate =
			"<a href=\"javascript:navToScrList('#= text #')\"><span class='text-link'>#= text #</span></a>";
%>



<!--  Forms -->

<ul class="nav nav-tabs tabs-alt container-p-x container-m--x mb-4">
	<li class="nav-item">
		<a class="nav-link active" data-toggle="tab" href="#advertiser">
			<i class="mr-1 fa-light fa-user-tie-hair"></i>
			광고주
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" data-toggle="tab" href="#sitecond">
			<i class="mr-1 fa-light fa-location-crosshairs"></i>
			입지 유형
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" data-toggle="tab" href="#opt">
			<i class="mr-1 fa-light fa-sliders-simple"></i>
			화면 옵션
		</a>
	</li>
</ul>

<div class="tab-content">
	<div class="tab-pane active" id="advertiser">



<!-- Kendo grid  -->

<div class="mb-4">
<kendo:grid name="grid-adv" pageable="true" filterable="true" scrollable="true" reorderable="true" resizable="true">
	<kendo:grid-sortable mode="mixed" showIndexes="true"/>
    <kendo:grid-selectable mode="multiple, raw"/>
	<kendo:grid-excel fileName="광고주.xlsx" allPages="true" proxyURL="/proxySave"/>
	<kendo:grid-pageable refresh="true" buttonCount="5" pageSize="10" pageSizes="${pageSizesNormal}" />
	<kendo:grid-toolbarTemplate>
    	<div class="clearfix">
    		<div class="float-left">
    			<button id="add-adv-btn" type="button" class="btn btn-outline-success">추가</button>
    			<button type="button" class="btn btn-default d-none d-sm-inline k-grid-excel">엑셀</button>
    		</div>
    		<div class="float-right">
    			<button id="delete-adv-btn" type="button" class="btn btn-danger">삭제</button>
    		</div>
    	</div>
	</kendo:grid-toolbarTemplate>
	<kendo:grid-filterable extra="false" />

	<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />
	<kendo:grid-columns>
		<kendo:grid-column title="수정" width="50" template="<%= editAdvTemplate %>" />
		<kendo:grid-column title="광고주" field="name" width="150" template="<%= advNameTemplate %>" sticky="true" />
		<kendo:grid-column title="도메인명" field="domainName" width="200" />
		<kendo:grid-column title="캠페인수" field="campaignCount" width="150" filterable="false" sortable="false" />
		<kendo:grid-column title="광고 소재수" field="creativeCount" width="150" filterable="false" sortable="false" />
	</kendo:grid-columns>
	<kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="name" dir="asc"/>
		</kendo:dataSource-sort>
       	<kendo:dataSource-filter>
      		<kendo:dataSource-filterItem field="medium.id" operator="eq" logic="and" value="${sessionScope['currMediumId']}" >
      		</kendo:dataSource-filterItem>
  	    </kendo:dataSource-filter>
		<kendo:dataSource-transport>
			<kendo:dataSource-transport-read url="${readAdvUrl}" dataType="json" type="POST" contentType="application/json"/>
			<kendo:dataSource-transport-parameterMap>
				<script>
					function parameterMap(options,type) {
						return JSON.stringify(options);	
					}
				</script>
			</kendo:dataSource-transport-parameterMap>
		</kendo:dataSource-transport>
		<kendo:dataSource-schema data="data" total="total" groups="data">
			<kendo:dataSource-schema-model id="id" />
		</kendo:dataSource-schema>
	</kendo:dataSource>
</kendo:grid>
</div>

<!-- / Kendo grid  -->


<!-- Grid button actions  -->

<script>
$(document).ready(function() {

	// Add
	$("#add-adv-btn").click(function(e) {
		e.preventDefault();
		
		initForm1();

		
		$('#form-modal-1 .modal-dialog').draggable({ handle: '.modal-header' });
		$("#form-modal-1").modal();
	});
	// / Add
	
	// Delete
	$("#delete-adv-btn").click(function(e) {
		e.preventDefault();
			
		var grid = $("#grid-adv").data("kendoGrid");
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
						url: "${destroyAdvUrl}",
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
	<div class="tab-pane" id="sitecond">
                        

	
<!-- Kendo grid  -->

<div class="mb-4">
<kendo:grid name="grid-cond" pageable="true" filterable="true" scrollable="true" reorderable="true" resizable="true">
	<kendo:grid-sortable mode="mixed" showIndexes="true"/>
    <kendo:grid-selectable mode="multiple, raw"/>
	<kendo:grid-excel fileName="입지유형.xlsx" allPages="true" proxyURL="/proxySave"/>
	<kendo:grid-pageable refresh="true" buttonCount="5" pageSize="10" pageSizes="${pageSizesNormal}" />
	<kendo:grid-toolbarTemplate>
    	<div class="clearfix">
    		<div class="float-left">
    			<button id="add-cond-btn" type="button" class="btn btn-outline-success">추가</button>
    			<button type="button" class="btn btn-default d-none d-sm-inline k-grid-excel">엑셀</button>
    		</div>
    		<div class="float-right">
    			<button id="delete-cond-btn" type="button" class="btn btn-danger">삭제</button>
    		</div>
    	</div>
	</kendo:grid-toolbarTemplate>
	<kendo:grid-filterable extra="false" />

	<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />
	<kendo:grid-columns>
		<kendo:grid-column title="수정" width="50" filterable="false" sortable="false" template="<%= editCondTemplate %>" />
		<kendo:grid-column title="입지 이름" field="name" width="200" sticky="true" />
		<kendo:grid-column title="코드" field="code" width="200" />
		<kendo:grid-column title="활성화 상태" field="activeStatus" width="200"
				template="#=activeStatus ? \"<span class='fa-light fa-check'>\" : \"\"#" />
		<kendo:grid-column title="사이트수" field="siteCount" width="150" filterable="false" sortable="false" />
		<kendo:grid-column title="화면수" field="screenCount" width="150" filterable="false" sortable="false" />
	</kendo:grid-columns>
	<kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="name" dir="asc"/>
		</kendo:dataSource-sort>
       	<kendo:dataSource-filter>
      		<kendo:dataSource-filterItem field="medium.id" operator="eq" logic="and" value="${sessionScope['currMediumId']}" >
      		</kendo:dataSource-filterItem>
  	    </kendo:dataSource-filter>
		<kendo:dataSource-transport>
			<kendo:dataSource-transport-read url="${readCondUrl}" dataType="json" type="POST" contentType="application/json"/>
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
	$("#add-cond-btn").click(function(e) {
		e.preventDefault();
		
		initForm2();

		
		$('#form-modal-2 .modal-dialog').draggable({ handle: '.modal-header' });
		$("#form-modal-2").modal();
	});
	// / Add
	
	// Delete
	$("#delete-cond-btn").click(function(e) {
		e.preventDefault();
			
		var grid = $("#grid-cond").data("kendoGrid");
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
						url: "${destroyCondUrl}",
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
	<div class="tab-pane" id="opt">



<!-- Kendo grid  -->

<div class="mb-4">
<kendo:grid name="grid-opt" pageable="true" scrollable="false" resizable="true">
	<kendo:grid-sortable mode="mixed" showIndexes="true"/>
    <kendo:grid-selectable mode="multiple, raw"/>
	<kendo:grid-excel fileName="화면옵션.xlsx" allPages="true" proxyURL="/proxySave"/>
	<kendo:grid-pageable refresh="true" previousNext="false" numeric="false" pageSize="10000" info="true" />
	<kendo:grid-toolbarTemplate>
    	<div class="clearfix">
    		<div class="float-left">
    			<button type="button" class="btn btn-default d-none d-sm-inline k-grid-excel">엑셀</button>
    		</div>
    	</div>
	</kendo:grid-toolbarTemplate>
   	<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />
	<kendo:grid-columns>
		<kendo:grid-column title="옵션ID" field="text" width="120" filterable="false" template="<%= optIDTemplate %>" />
		<kendo:grid-column title="옵션명" field="value" width="120" filterable="false" sticky="true" />
		<kendo:grid-column title="화면수" field="icon" width="120" filterable="false" sortable="false" />
	</kendo:grid-columns>
	<kendo:grid-filterable>
		<kendo:grid-filterable-messages selectedItemsFormat="{0} 항목 선택됨"/>
	</kendo:grid-filterable>
	<kendo:dataSource serverPaging="false" serverSorting="false" serverFiltering="false" serverGrouping="false" error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="text" dir="asc"/>
		</kendo:dataSource-sort>
		<kendo:dataSource-transport>
			<kendo:dataSource-transport-read url="${readOptUrl}" dataType="json" type="POST" contentType="application/json"/>
			<kendo:dataSource-transport-parameterMap>
				<script>
					function parameterMap(options,type) {
						return JSON.stringify(options);	
					}
				</script>
			</kendo:dataSource-transport-parameterMap>
		</kendo:dataSource-transport>
		<kendo:dataSource-schema>
			<kendo:dataSource-schema-model id="text" />
		</kendo:dataSource-schema>
	</kendo:dataSource>
</kendo:grid>
</div>

<!-- / Kendo grid  -->


	</div>
</div>


<!--  Root form container -->
<div id="formRoot"></div>

<style>




/* 그리드 행의 높이 지정 */
.k-grid tbody tr, .k-grid tbody tr td
{
    height: 40px;
}


/* 그리드 자료 새로고침 버튼을 우측 정렬  */
div.k-pager-wrap.k-grid-pager.k-widget.k-floatwrap {
	display: flex!important;
	justify-content: flex-end!important;
}

</style>


<!--  Forms -->

<script id="template-1" type="text/x-kendo-template">

<div class="modal fade" data-backdrop="static" id="form-modal-1">
	<div class="modal-dialog modal-sm">
		<form class="modal-content" id="form-1" rowid="-1" url="${createAdvUrl}">
      
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
						광고주
						<span class="text-danger">*</span>
					</label>
					<input name="name" type="text" maxlength="100" class="form-control required">
				</div>
				<div class="form-group col">
					<label class="form-label">
						광고주 도메인
						<span class="text-danger">*</span>
					</label>
					<input name="domainName" type="text" maxlength="100" class="form-control required">
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
		<form class="modal-content" id="form-2" rowid="-1" url="${createCondUrl}">
      
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
						입지 이름
						<span class="text-danger">*</span>
					</label>
					<input name="name" type="text" maxlength="100" class="form-control required">
				</div>
				<div class="form-group col">
					<label class="form-label">
						코드
						<span class="text-danger">*</span>
					</label>
					<input name="code" type="text" maxlength="100" class="form-control required">
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

	$("#form-1 span[name='subtitle']").text(subtitle ? subtitle : "추가");
	

	$("#form-1").validate({
		rules: {
		}
	});
}


function saveForm1() {

	if ($("#form-1").valid()) {
    	var data = {
    		id: Number($("#form-1").attr("rowid")),
    		name: $.trim($("#form-1 input[name='name']").val()),
    		domainName: $.trim($("#form-1 input[name='domainName']").val()),
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
				$("#grid-adv").data("kendoGrid").dataSource.read();
			},
			error: ajaxSaveError
		});
	}
}


function editAdv(id) {
	
	initForm1("변경");

	var dataItem = $("#grid-adv").data("kendoGrid").dataSource.get(id);
	
	$("#form-1").attr("rowid", dataItem.id);
	$("#form-1").attr("url", "${updateAdvUrl}");
	
	$("#form-1 input[name='name']").val(dataItem.name);
	$("#form-1 input[name='domainName']").val(dataItem.domainName);

	
	$('#form-modal-1 .modal-dialog').draggable({ handle: '.modal-header' });
	$("#form-modal-1").modal();
}


function navToAdv(advId) {
	var path = "/adc/creative/creatives/" + advId;
	location.href = path;
}


function initForm2(subtitle) {
	
	$("#formRoot").html(kendo.template($("#template-2").html()));

	$("#form-2 span[name='subtitle']").text(subtitle ? subtitle : "추가");
	

	$("#form-2").validate({
		rules: {
		}
	});
}


function saveForm2() {

	if ($("#form-2").valid()) {
    	var data = {
    		id: Number($("#form-2").attr("rowid")),
    		name: $.trim($("#form-2 input[name='name']").val()),
    		code: $.trim($("#form-2 input[name='code']").val()),
    		activeStatus: $("#form-2 input[name='activeStatus']").is(':checked'),
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
				$("#grid-cond").data("kendoGrid").dataSource.read();
			},
			error: ajaxSaveError
		});
	}
}


function editCond(id) {
	
	initForm2("변경");

	var dataItem = $("#grid-cond").data("kendoGrid").dataSource.get(id);
	
	$("#form-2").attr("rowid", dataItem.id);
	$("#form-2").attr("url", "${updateCondUrl}");
	
	$("#form-2 input[name='name']").val(dataItem.name);
	$("#form-2 input[name='code']").val(dataItem.code);

	$("#form-2 input[name='activeStatus']").prop("checked", dataItem.activeStatus);

	
	$('#form-modal-2 .modal-dialog').draggable({ handle: '.modal-header' });
	$("#form-modal-2").modal();
}


function navToScrList(optID) {
	var path = "/org/mediumopt/screen/" + optID;
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
