<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/fnd/setupfile/create" var="createUrl" />
<c:url value="/fnd/setupfile/read" var="readUrl" />
<c:url value="/fnd/setupfile/update" var="updateUrl" />
<c:url value="/fnd/setupfile/destroy" var="destroyUrl" />

<c:url value="/fnd/setupfile/readfileoverview" var="readFileOverviewUrl" />


<!-- Opening tags -->

<common:pageOpening />


<!-- Page title -->

<h4 class="pt-1 pb-3 mb-3">
	<span class="mr-1 ${sessionScope['loginUser'].icon}"></span>
	${pageTitle}
</h4>

<hr class="border-light container-m--x mt-0 mb-4">





<!-- Page body -->


<!-- Page scripts  -->

<link rel="stylesheet" href="/resources/vendor/lib/smartwizard/smartwizard.css">
<link rel="stylesheet" href="/resources/vendor/lib/bootstrap-maxlength/bootstrap-maxlength.css">

<script src="/resources/vendor/lib/smartwizard/smartwizard.js"></script>
<script src="/resources/vendor/lib/autosize/autosize.js"></script>
<script src="/resources/vendor/lib/bootstrap-maxlength/bootstrap-maxlength.js"></script>


<!-- Java(optional)  -->

<%
	String editTemplate = 
			"<div class='text-nowrap'>" +
				"<button type='button' onclick='edit(#= id #)' class='btn icon-btn btn-sm btn-outline-success borderless'>" + 
				"<span class='fa-solid fa-pencil-alt'></span></button>" +
				"<span class='pl-1'></span>" +
				"<button type='button' onclick='download(#= id #)' class='btn icon-btn btn-sm btn-outline-secondary borderless'>" + 
				"<span class='fa-solid fa-download'></span></button>" +
			"</div>";
			
	String noRecordsTemplate =
			"<div class='container text-center my-4'>" +
				"<div class='d-flex justify-content-center align-self-center'>" +
					"<span class='fa-thin fa-hexagon-exclamation fa-3x'></span>" +
					"<span class='pl-3 mt-2' style='font-weight: 300; font-size: 1.3rem;'>해당 자료 없음</span>" +
				"</div>" +
			"</div>";

	String lengthTemplate = "<div class='len-container'><span data-toggle='tooltip' data-placement='top' title='#= dispFileLength #'>#= smartLength #</span></div>";
	String creationDateTemplate = net.doohad.utils.Util.getSmartDate("whoCreationDate", false, true);
%>


<!-- Kendo grid  -->

<div class="mb-4">
<kendo:grid name="grid" pageable="true" filterable="true" sortable="true" scrollable="true" reorderable="true" resizable="true">
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
		<kendo:grid-column title="수정..." width="80" filterable="false" sortable="false" template="<%= editTemplate %>" />
		<kendo:grid-column title="제품" field="prodKeyword" width="100" filterable="false" sortable="false" />
		<kendo:grid-column title="버전" field="version" width="100" filterable="false" sortable="false" />
		<kendo:grid-column title="파일명" field="filename" width="300" />
		<kendo:grid-column title="파일크기" field="fileLength" width="100" filterable="false" template="<%= lengthTemplate %>" />
		<kendo:grid-column title="플랫폼" field="platKeyword" width="100" filterable="false" sortable="false" />
		<kendo:grid-column title="대분류" field="majorCat" width="150" filterable="false" sortable="false" />
		<kendo:grid-column title="서비스중" field="activeStatus" width="100"
				template="#= activeStatus ? \"<span class='fa-light fa-check'>\" : \"\"#" />
		<kendo:grid-column title="등록" field="whoCreationDate" width="120" template="<%= creationDateTemplate %>" />
	</kendo:grid-columns>
	<kendo:grid-dataBound>
		<script>
			function grid_dataBound(e) {
				$('[data-toggle="tooltip"]').tooltip({
					   container: '.len-container'
				});
        	}
		</script>
	</kendo:grid-dataBound>
	<kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="whoCreationDate" dir="desc"/>
		</kendo:dataSource-sort>
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
					<kendo:dataSource-schema-model-field name="whoCreationDate" type="date" />
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
		
		initSmartWizard();

		
		$('#wizard-modal .modal-dialog').draggable({ handle: '.modal-header' });
		$("#wizard-modal").modal();
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

<script id="sw-template" type="text/x-kendo-template">

<div class="modal fade" data-backdrop="static" id="wizard-modal">
	<div class="modal-dialog">
		<form class="modal-content" id="wizard-form">
      
			<!-- Modal Header -->
			<div class="modal-header move-cursor">
				<h5 class="modal-title">
					${pageTitle}
					<span class="font-weight-light pl-1"><span name="subtitle">${form_add}</span></span>
				</h5>
				<button type="button" class="close" data-dismiss="modal">×</button>
			</div>
        
			<!-- Modal body -->
			<div class="modal-body modal-bg-color">

				<div name="smartWizard">
					<ul>
						<li name="tab-0">
							<a href="\\\#sw-step-1" class="mb-3">
								<span class="sw-done-icon"><span class="fa-regular fa-check"></span></span>
								<span class="sw-icon"><span class="fa-regular fa-cloud-upload-alt"></span></span>
								업로드
								<div class="text-muted small">설치 파일</div>
							</a>
						</li>
						<li name="tab-1">
							<a href="\\\#sw-step-2" class="mb-3">
								<span class="sw-done-icon"><span class="fa-regular fa-check"></span></span>
								<span class="sw-icon"><span class="fa-regular fa-pen-field"></span></span>
								폼 입력
								<div class="text-muted small">확인 및 내용 입력</div>
							</a>
						</li>
					</ul>

					<div class="mb-3">
						<div id="sw-step-1" class="card animated fadeIn">
							<div class="card-body">
								<div class="drop-zone upload-root-div">
									<input name="files" type="file" />
								</div>
							</div>
						</div>
						<div id="sw-step-2" class="card animated fadeIn py-3">
						
							<div class="card-body py-0">
								<div class="form-row mb-3">
									<div class="col-sm-12">
										<label class="form-label">
											파일명
										</label>
										<input name="filename" type="text" class="form-control" readonly="readonly">
									</div>
								</div>
								<div class="form-row mb-3">
									<div class="col-sm-5">
										<label class="form-label">
											대분류
										</label>
										<input name="majorCat" type="text" class="form-control" readonly="readonly">
									</div>
									<div class="col-sm-3">
										<label class="form-label">
											제품
										</label>
										<input name="prodKeyword" type="text" class="form-control" readonly="readonly">
									</div>
									<div class="col-sm-4">
										<label class="form-label">
											버전
										</label>
										<input name="version" type="text" class="form-control" readonly="readonly">
									</div>
								</div>
								<div class="form-row mb-3">
									<div class="col-sm-8">
										<label class="form-label">
											파일크기
										</label>
										<input name="length" type="text" class="form-control" readonly="readonly">
									</div>
									<div class="col-sm-4">
										<label class="form-label">
											플랫폼
										</label>
										<input name="platKeyword" type="text" class="form-control" readonly="readonly">
									</div>
								</div>
								<div class="form-row">
									<div class="col-sm-12">
										<label class="form-label">
											개선/변경 목록
										</label>
										<textarea name="updateList" rows="3" class="form-control" maxlength="300"></textarea>
									</div>
								</div>
							</div>

						</div>
					</div>
				</div>
			</div>
        
			<!-- Modal footer -->
			<div class="modal-footer d-flex">
				<div name="error-msg-div">
					<span class="fa-solid fa-minus-circle text-danger"></span>
					<span class="pl-1" name="error-msg-span"></span>
				</div>
				<button type="button" class="btn btn-default ml-auto" data-dismiss="modal">취소</button>
				<button name="save-btn" type="button" class="btn btn-primary disabled" onclick='saveWizardForm()' name="saveBtn">저장</button>
			</div>
			
		</form>
	</div>
</div>

</script>

<script id="template-2" type="text/x-kendo-template">

<div class="modal fade" data-backdrop="static" id="form-modal-2">
	<div class="modal-dialog">
		<form class="modal-content" id="form-2" rowid="-1" url="${createUrl}">
      
			<!-- Modal Header -->
			<div class="modal-header move-cursor">
				<h5 class="modal-title">
					${pageTitle}
					<span class="font-weight-light pl-1"><span name="subtitle"></span></span>
				</h5>
				<button type="button" class="close" data-dismiss="modal">×</button>
			</div>
        
			<!-- Modal body -->
			<div class="modal-body modal-bg-color">
				<div class="form-row">
					<div class="form-group col-10">
						<label class="form-label">
							파일명
						</label>
						<input name="filename" type="text" class="form-control" readonly="readonly">
					</div>
					<div class="form-group col-2">
						<label class="form-label">
							서비스
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
				<div class="form-row">
					<div class="form-group col-sm-5">
						<label class="form-label">
							대분류
						</label>
						<input name="majorCat" type="text" class="form-control" readonly="readonly">
					</div>
					<div class="form-group col-sm-3">
						<label class="form-label">
							제품
						</label>
						<input name="prodKeyword" type="text" class="form-control" readonly="readonly">
					</div>
					<div class="form-group col-sm-4">
						<label class="form-label">
							버전
						</label>
						<input name="version" type="text" class="form-control" readonly="readonly">
					</div>
				</div>
				<div class="form-row">
					<div class="form-group col-sm-8">
						<label class="form-label">
							파일크기
						</label>
						<input name="length" type="text" class="form-control" readonly="readonly">
					</div>
					<div class="form-group col-sm-4">
						<label class="form-label">
							플랫폼
						</label>
						<input name="platKeyword" type="text" class="form-control" readonly="readonly">
					</div>
				</div>
				<div class="form-row mb-0">
					<div class="form-group col">
						<label class="form-label">
							개선/변경 목록
						</label>
						<textarea name="updateList" rows="3" class="form-control" maxlength="300"></textarea>
					</div>
				</div>
			</div>
        
			<!-- Modal footer -->
			<div class="modal-footer d-flex">
				<button type="button" class="btn btn-default" data-dismiss="modal">취소</button>
				<button type="button" class="btn btn-primary" onclick='saveForm2()'>저장</button>
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
	padding-right: 0.75rem;
	padding-left: 0.75rem;
}
.a-warn {
    padding: .25em .417em;
    background: rgba(24,28,33,0.1);
    font-weight: 500;
    font-size: .858em;
    border-bottom-right-radius: .125rem;
    border-bottom-left-radius: .125rem
}

</style>

<!--  / Forms -->


<!-- Smart wizard -->

<style>

.upload-root-div {
	height: 200px;
}

.k-upload-files {
	height: 100px;
	overflow-x: hidden;
	overflow-y: hidden;
}

strong.k-upload-status.k-upload-status-total { font-weight: 500; color: #2e2e2e; }

/*div.k-dropzone.k-dropzone-hovered em, div.k-dropzone em { color: #2e2e2e; }*/

.k-upload .k-upload-files ~ .k-button {
	width: 48%;
	margin: 3px 3px 0.25em 1%;
}

.k-upload .k-button {
	height: 38px;
	border-radius: .25rem;
}

.k-upload .k-upload-button {
	border-color: transparent;
	background: #8897AA;
	color: #fff;
}

.k-upload .k-upload-button:hover {
	background: #818fa2;
}

.k-upload .k-upload-files ~ .k-upload-selected {
	border-color: transparent;
	background: #e84c64;
	color: #fff;
}

.k-upload .k-upload-files ~ .k-upload-selected:hover {
	background: #dc485f;
}

.k-upload .k-upload-files ~ .k-clear-selected {
	background: transparent;
	color: #4E5155;
	border: 1px solid rgba(24,28,33,0.1);
}

.k-upload .k-upload-files ~ .k-clear-selected:hover {
	background: rgba(24,28,33,0.06);
}


/*	수정 사항  */
.k-file.k-file-invalid.k-file-error {
	padding: 8px;
	border-width: 0 0 1px !important;
	align-items: center !important;;
	position: relative;
	line-height: 1.42857;
}
.k-file.k-toupload {
	padding: 8px !important;
	border-width: 0 0 1px !important;
	align-items: center !important;
	position: relative;
	line-height: 1.42857;
}
.k-file-validation-message {
	font-size: 9.432px !important;
	color: #d9534f; !important;
}
.k-file-name {
	font-size: 14px ;
	color: #b3b3b3;
}


</style>

<!-- / Smart wizard -->


<!--  Scripts -->

<script>

var goAhead = false;

function initSmartWizard() {
	
	$("#formRoot").html(kendo.template($("#sw-template").html()));
	
	$("#wizard-form div[name='smartWizard']").smartWizard({
		showStepURLhash: false,
		toolbarSettings: {
			showNextButton: false,
			showPreviousButton: false,
		}
	});
	
	// textarea 자동으로 크기 조절
	autosize($("#wizard-form textarea[name='updateList']"));
	
	// textarea 입력 문자 수 표시 / 제어
	$("#wizard-form textarea[name='updateList']").maxlength({
        warningClass: "badge badge-pill badge-secondary",
        limitReachedClass: "badge badge-pill badge-danger",
        placement: "top-right-inside",
        validate: true,
        threshold: 200,
	});

	
	$("#wizard-form input[name='files']").kendoUpload({
		multiple: false,
		async: {
			saveUrl: "${uploadModel.saveUrl}",
			autoUpload: false
		},
		localization: {
			cancel: "취소",
			dropFilesHere: "업로드 대상 파일을 여기에 끌어 놓기",
			headerStatusUploaded: "완료",
			headerStatusUploading: "업로드중...",
			remove: "삭제",
			retry: "재시도",
			select: "파일 선택...",
			uploadSelectedFiles: "업로드 시작",
			clearSelectedFiles: "목록 지우기",
			invalidFileExtension: "허용되지 않는 파일 유형입니다.",
		},
		dropZone: ".drop-zone",
		upload: function(e) {
			e.data = {
				type: "${uploadModel.type}",
			};
		},
		success: function(e) {
			$("#wizard-form div[name='smartWizard']").smartWizard("next");

			$.ajax({
				type: "POST",
				contentType: "application/json",
				dataType: "json",
				url: "${readFileOverviewUrl}",
				data: JSON.stringify({ file: e.files[0].name }),
				success: function (data) {
					
					$("#wizard-form input[name='filename']").val(data.filename);
					$("#wizard-form input[name='length']").val(data.lengthStr);

					$("#wizard-form input[name='majorCat']").val(data.majorCat);
					$("#wizard-form input[name='prodKeyword']").val(data.prodKeyword);
					$("#wizard-form input[name='platKeyword']").val(data.platKeyword);
					$("#wizard-form input[name='version']").val(data.version);
					
					$("#wizard-form").attr("file", e.files[0].name);
					
					if (data.errorMsg) {
						$("#wizard-form div[name='error-msg-div']").show();
						$("#wizard-form span[name='error-msg-span']").html(data.errorMsg);
						
						$("#wizard-form textarea[name='updateList']").attr("readonly", true);
						
						goAhead = false;
					} else {
						$("#wizard-form span[name='error-msg-span']").html("");
						$("#wizard-form div[name='error-msg-div']").hide();
						$("#wizard-form button[name='save-btn']").removeClass("disabled");
						
						$("#wizard-form textarea[name='updateList']").attr("readonly", false);
						
						goAhead = true;
					}
				},
				error: ajaxReadError
			});
		},
		validation: {
			allowedExtensions: ${uploadModel.allowedExtensions}
		},
	});
	
	$("#wizard-form div[name='error-msg-div']").hide();
	$("#wizard-form button[name='proc-btn']").addClass("disabled");
	goAhread = false;
}


function saveWizardForm() {

	if (goAhead) {
		
		showWaitModal();
		
    	var data = {
       		file: $("#wizard-form").attr("file"),
       		updateList: $.trim($("#wizard-form textarea[name='updateList']").val()),
       	};
        
   		$.ajax({
   			type: "POST",
   			contentType: "application/json",
   			dataType: "json",
   			url: "${createUrl}",
   			data: JSON.stringify(data),
   			success: function (form) {
   				showSaveSuccessMsg();
   				hideWaitModal();
   				$("#wizard-modal").modal("hide");
   				$("#grid").data("kendoGrid").dataSource.read();
   			},
			error: function(e) {
				hideWaitModal();
				ajaxSaveError(e);
			}
   		});
	}
}


function initForm2(subtitle) {
	
	$("#formRoot").html(kendo.template($("#template-2").html()));

	$("#form-2 input[name='activeStatus']").prop("checked", false);
	
	// textarea 자동으로 크기 조절
	autosize($("#form-2 textarea[name='updateList']"));
	
	// textarea 입력 문자 수 표시 / 제어
	$("#form-2 textarea[name='updateList']").maxlength({
        warningClass: "badge badge-pill badge-secondary",
        limitReachedClass: "badge badge-pill badge-danger",
        placement: "top-right-inside",
        validate: true,
        threshold: 200,
	});

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
    		updateList: $.trim($("#form-2 textarea[name='updateList']").val()),
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
				$("#grid").data("kendoGrid").dataSource.read();
			},
			error: ajaxSaveError
		});
	}
}


function edit(id) {
	
	initForm2("변경");

	var dataItem = $("#grid").data("kendoGrid").dataSource.get(id);
	
	$("#form-2").attr("rowid", dataItem.id);
	$("#form-2").attr("url", "${updateUrl}");
	
	$("#form-2 input[name='filename']").val(dataItem.filename);

	$("#form-2 input[name='majorCat']").val(dataItem.majorCat);
	$("#form-2 input[name='prodKeyword']").val(dataItem.prodKeyword);
	$("#form-2 input[name='version']").val(dataItem.version);

	$("#form-2 input[name='length']").val(dataItem.smartLength + " (" + kendo.format("{0:n0}", dataItem.fileLength) + " bytes)");
	$("#form-2 input[name='platKeyword']").val(dataItem.platKeyword);
	
	$("#form-2 input[name='activeStatus']").prop("checked", dataItem.activeStatus);

	$("#form-2 textarea[name='updateList']").val(dataItem.updateList);
	
	// autosize lib 초기 textarea 높이 재계산 목적
	setTimeout(function() {
		autosize.update($("#form-2 textarea[name='updateList']"));
	}, 500);
	
	
	$('#form-modal-2 .modal-dialog').draggable({ handle: '.modal-header' });
	$("#form-modal-2").modal();
}


function download(id) {
	
	var dataItem = $("#grid").data("kendoGrid").dataSource.get(id);
	
	location.href = dataItem.httpFilename;
}

</script>

<!--  / Scripts -->


<!-- / Page body -->





<!-- Functional tags -->

<func:cmmValidate />


<!-- Closing tags -->

<common:base />
<common:pageClosing />
