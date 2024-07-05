<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/adc/playlist/create" var="createUrl" />
<c:url value="/adc/playlist/update" var="updateUrl" />
<c:url value="/adc/playlist/read" var="readUrl" />
<c:url value="/adc/playlist/destroy" var="destroyUrl" />

<c:url value="/adc/playlist/readAds" var="readAdUrl" />
<c:url value="/adc/playlist/editAdValue" var="editAdValueUrl" />
<c:url value="/adc/playlist/copyAs" var="copyAsUrl" />

<c:url value="/adc/playlist/readChannels" var="readChannelUrl" />


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

<link rel="stylesheet" href="/resources/vendor/lib/dragula/dragula.css">

<script src="/resources/vendor/lib/dragula/dragula.js"></script>


<!-- Java(optional)  -->

<%
	String editTemplate = 
			"<div class='text-nowrap'>" +
				"<button type='button' onclick='edit(#= id #)' class='btn icon-btn btn-sm btn-outline-success borderless'>" + 
				"<span class='fas fa-pencil-alt'></span></button>" +
				"<span class='pl-1'>" +
					"<button type='button' onclick='copyAs(#= id #)' class='btn icon-btn btn-sm btn-outline-success borderless'>" + 
					"<span class='fa-regular fa-copy'></span></button>" +
				"</span>" +
				"<span class='pl-1'>" +
					"<button type='button' onclick='editList(#= id #)' class='btn icon-btn btn-sm btn-outline-secondary borderless'>" + 
					"<span class='fa-regular fa-list-ol'></span></button>" +
				"</span>" +
			"</div>";

	String noRecordsTemplate =
			"<div class='container text-center my-4'>" +
				"<div class='d-flex justify-content-center align-self-center'>" +
					"<span class='fa-thin fa-hexagon-exclamation fa-3x'></span>" +
					"<span class='pl-3 mt-2' style='font-weight: 300; font-size: 1.3rem;'>해당 자료 없음</span>" +
				"</div>" +
			"</div>";
	
			
	String startDateTemplate = net.doohad.utils.Util.getSmartDate("startDate", false);			
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
    			<div class="d-none d-xl-inline">
					<div class="btn-group btn-group-toggle" data-toggle="buttons">
						<label class="btn btn-outline-secondary btn-sm active" title="사용중 혹은 계획중">
							<input type="radio" name="view-type-radio" value="F" checked>
								<span class='fa-light fa-star fa-lg mx-1'></span>
							</input>
						</label>
						<label class="btn btn-outline-secondary btn-sm" title="모든 자료">
							<input type="radio" name="view-type-radio" value="A">
								ALL
							</input>
						</label>
					</div>
	   				<span class="px-2">
	    				<span class="fa-solid fa-pipe text-muted"></span>
	   				</span>
    			</div>
    			<button id="delete-btn" type="button" class="btn btn-danger">삭제</button>
    		</div>
    	</div>
	</kendo:grid-toolbarTemplate>
	<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />
	<kendo:grid-columns>
		<kendo:grid-column title="수정..." width="110" filterable="false" sortable="false" template="<%= editTemplate %>" />
		<kendo:grid-column title="재생목록" field="name" width="250" />
		<kendo:grid-column title="광고 채널" field="channelId" width="200" template="#= channel #" >
			<kendo:grid-column-filterable multi="true" itemTemplate="kfcTextOnly">
				<kendo:dataSource>
					<kendo:dataSource-transport>
						<kendo:dataSource-transport-read url="${readChannelUrl}" dataType="json" type="POST" contentType="application/json" />
					</kendo:dataSource-transport>
				</kendo:dataSource>
			</kendo:grid-column-filterable>
		</kendo:grid-column>
		<kendo:grid-column title="해상도" field="resolution" width="150" filterable="false" sortable="false" />
		<kendo:grid-column title="게시유형" field="viewTypeCode" width="150" filterable="false" sortable="false" />
		<kendo:grid-column title="시작" field="startDate" width="120" template="<%= startDateTemplate %>" />
		<kendo:grid-column title="활성화 상태" field="activeStatus" width="120"
				template="#=activeStatus ? \"<span class='fa-light fa-check'>\" : \"\"#" />
		<kendo:grid-column title="광고수" field="adCount" width="120" />
		<kendo:grid-column title="시간(초)" field="totDurSecs" width="120" />
	</kendo:grid-columns>
	<kendo:grid-filterable>
		<kendo:grid-filterable-messages selectedItemsFormat="{0} 항목 선택됨"/>
	</kendo:grid-filterable>
	<kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="startDate" dir="desc"/>
		</kendo:dataSource-sort>
       	<kendo:dataSource-filter>
      		<kendo:dataSource-filterItem field="medium.id" operator="eq" logic="and" value="${sessionScope['currMediumId']}" >
      		</kendo:dataSource-filterItem>
  	    </kendo:dataSource-filter>
		<kendo:dataSource-transport>
			<kendo:dataSource-transport-read url="${readUrl}" dataType="json" type="POST" contentType="application/json">
				<kendo:dataSource-transport-read-data>
					<script>
						function additionalData(e) {
							return { reqStrValue1: $("input[name='view-type-radio']:checked").val() };
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
					<kendo:dataSource-schema-model-field name="startDate" type="date" />
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

	
	// 필터 유형
	$("input[name='view-type-radio']").change(function(){
		
		$("#grid").data("kendoGrid").dataSource.read();
	});
	
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
						재생목록
						<span class="text-danger">*</span>
					</label>
					<input name="name" type="text" maxlength="100" class="form-control required">
				</div>
				<div class="form-group col">
					<label class="form-label">
						광고 채널
						<span class="text-danger">*</span>
					</label>
					<select name="channel" class="selectpicker bg-white" data-style="btn-default" data-none-selected-text="">
<c:forEach var="item" items="${Channels}">
						<option value="${item.value}">${item.text}</option>
</c:forEach>
					</select>
				</div>
				<div class="form-group col">
					<label class="form-label">
						시작
						<span class="text-danger">*</span>
					</label>
						<input name="startDate" type="text" class="form-control border-none required">
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


<script id="template-2" type="text/x-kendo-template">

<div class="modal fade" data-backdrop="static" id="form-modal-2">
	<div class="modal-dialog modal-lg">
		<form class="modal-content" id="form-2" rowid="-1" url="${editAdValueUrl}">
      
			<!-- Modal Header -->
			<div class="modal-header move-cursor">
				<h5 class="modal-title">
					<span name="title">재생목록</span>
					<span class="font-weight-light pl-1"><span name="subtitle">편성표</span></span>
				</h5>
				<button type="button" class="close" data-dismiss="modal">×</button>
			</div>
        
			<!-- Modal body -->
			<div class="modal-body modal-bg-color">
				<div class="form-row">
					<div class="card col px-0 mx-1">
						<h6 class="card-header with-elements bg-secondary text-white py-1">
							<div class="card-header-title">가능한 광고</div>
							<div class="card-header-elements ml-auto small opacity-75">
								<span id="avail-pl-count">5</span>
								<span>항목</span>
							</div>
							<div class="pl-2 small">
								<button type="button" class="btn btn-sm btn-outline-default" onClick="copyToRight()">
									<span class="fas fa-chevron-double-right text-white"></span>
								</button>
							</div>
						</h6>
						<div id="dragula-left" class="card-body px-2 pb-2 pt-3 creat-container">
						</div>
					</div>
					
					<div class="card col px-0 mx-1">
						<h6 class="card-header with-elements bg-success text-white py-1">
							<div class="card-header-title">선택 목록</div>
							<div class="card-header-elements ml-auto small opacity-75">
								<span id="sel-pl-count">0</span>
								<span>항목</span>
							</div>
						</h6>
						<div id="dragula-right" class="card-body px-2 pb-2 pt-3 creat-container">
						</div>
					</div>
				</div>
			</div>
        
			<!-- Modal footer -->
			<div class="modal-footer d-flex">
				<div class="btn-group btn-group-toggle mr-auto" data-toggle="buttons">
					<label class="btn btn-default px-2 active">
						<input type="radio" name="thumb-size-radio" value="S" checked>
							<span class="fa-light fa-image fa-xs"></span><span class="pl-2">작은 썸네일</span>
						</input>
					</label>
					<label class="btn btn-default px-2">
						<input type="radio" name="thumb-size-radio" value="L">
							<span class="fa-light fa-image fa-lg"></span><span class="pl-2">큰 썸네일</span>
						</input>
					</label>
				</div>
				<button type="button" class="btn btn-default" data-dismiss="modal">취소</button>
				<button type="button" class="btn btn-primary" onclick='saveForm2()'>저장</button>
			</div>
			
		</form>
	</div>
</div>

</script>


<script id="itemTemplate" type="text/x-kendo-template">

<div class="creat-item card card-condenced pc-opt #= valid ? '' : 'border-danger' #" ukid="#: adCreatId #" dur="#: intDuration #">
	<div class="card-body media align-items-center pl-0 py-0">
		<Img name="thumb-img" src='/thumbs/#= thumbUri #' class='thumb-64'>
		<div class="media-body pl-2">
			<div class="clearfix">
				<span class="text-dark mb-2" style="font-weight:400;">
					#: creatName #
				</span>
				<button type="button" class="btn btn-xs btn-default float-right" onClick="appendOneItem(#: adCreatId #)">
					<span class="fa-regular fa-clone"></span>
				</button>
			</div>
			<div class="text-muted small text-ellipsis">
				# if (mediaType == "V") { #
					<span class="fa-solid fa-film text-primary"></span><span class="pl-1 pr-2">동영상</span>
					<span class="fa-regular fa-alarm-clock"></span><span class="pl-1">#: duration #</span>
				# } else if (mediaType == "I") { #
					<span class="fa-solid fa-image text-primary"></span><span class="pl-1">이미지</span>
				# } #
				<span class="pr-2"></span>
				<span class="fa-regular fa-ruler-vertical"></span>
				#: fileLength #
				<span class="pr-2"></span>
				<span class="fa-regular fa-audio-description"></span>
				#: adName #
				<span class="pr-2"></span>
				<span class="fa-regular fa-user-tie-hair"></span>
				#: advertiser #
			</div>
		</div>
		<div><span class="fas fa-arrows-alt fa-lg text-secondary mobile-opt"></span></div>
	</div>
</div>

</script>


<script id="yellowItemTemplate" type="text/x-kendo-template">

<div class="creat-item card card-condenced pc-opt border-danger" ukid="-1">
	<div class="card-body media align-items-center pl-0 py-0">
		<Img name="thumb-img" src='/resources/shared/images/no_thumb.png' class='thumb-64'>
		<div class="media-body pl-2">
			<span class="text-dark mb-2" style="font-weight:400;">
				서비스 불가능
			</span>
			<div class="text-muted small text-ellipsis">
				<span class="fa-regular fa-audio-description"></span>
				#: adName #
				<span class="pr-2"></span>
				<span class="fa-regular fa-user-tie-hair"></span>
				#: advertiser #
			</div>
		</div>
		<div><span class="fas fa-arrows-alt fa-lg text-secondary mobile-opt"></span></div>
	</div>
</div>

</script>


<script id="redItemTemplate" type="text/x-kendo-template">

<div class="creat-item card card-condenced pc-opt border-danger" ukid="-1">
	<div class="card-body media align-items-center pl-0 py-0">
		<Img name="thumb-img" src='/resources/shared/images/no_thumb.png' class='thumb-64'>
		<div class="media-body pl-2">
			<span class="text-dark mb-2" style="font-weight:400;">
				미확인 광고/컨텐츠
			</span>
		</div>
		<div><span class="fas fa-arrows-alt fa-lg text-secondary mobile-opt"></span></div>
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
					<span class="font-weight-light pl-1">새 이름으로 저장</span>
				</h5>
				<button type="button" class="close" data-dismiss="modal">×</button>
			</div>
        
			<!-- Modal body -->
			<div class="modal-body modal-bg-color">
				<div class="form-group col">
					<label class="form-label">
						재생목록
						<span class="text-danger">*</span>
					</label>
					<input name="name" type="text" maxlength="100" class="form-control required">
				</div>
				<div class="form-group col">
					<label class="form-label">
						광고 채널
						<span class="text-danger">*</span>
					</label>
					<select name="channel" class="selectpicker bg-white" data-style="btn-default" data-none-selected-text="">
<c:forEach var="item" items="${Channels}">
						<option value="${item.value}">${item.text}</option>
</c:forEach>
					</select>
				</div>
				<div class="form-group col">
					<label class="form-label">
						광고
					</label>
					<input name="desc" type="text" class="form-control" readonly>
				</div>
				<div class="form-group col">
					<label class="form-label">
						시작
						<span class="text-danger">*</span>
					</label>
						<input name="startDate" type="text" class="form-control border-none required">
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

<style>

/* 광고 소재들을 포함하는 컨테이너 박스가 많을 경우 스크롤 되도록 */
.creat-container {
	overflow-y: auto; height: 600px;
}


/* 소재 항목과 항목간 간격 조정 */
.creat-item {
	margin-bottom: 0.4rem;
}


/* 소재 오버 마우스 모습이 포인트에서 이동십자 포인트로 표시(PC 환경) */
.pc-opt {

<c:if test="${not isMobileMode}">
	cursor: move;
</c:if>

}


/* 끌기 가능한 아이콘 버튼 표시(모바일 환경) */
.mobile-opt {

<c:if test="${not isMobileMode}">
	display: none;
</c:if>

}


/* 썸네일 이미지 */
.thumb-64 {
	width: 64px;
	height: 64px;
}
.thumb-128 {
	width: 128px;
	height: 128px;
}


</style>

<!--  / Forms -->


<!--  Scripts -->

<script>

function initForm1(subtitle) {
	
	$("#formRoot").html(kendo.template($("#template-1").html()));
	
	$("#form-1 select[name='channel']").selectpicker('render');
	
	$("#form-1 input[name='startDate']").kendoDateTimePicker({
		format: "yyyy-MM-dd HH:mm", 
		parseFormats: [
			"yyyy-MM-dd HH:mm", "yyyy-MM-dd HHmm", "yyyy-MM-dd HH mm", "yyyy-MM-dd HH",
			"yyyyMMdd HH:mm", "yyyyMMdd HHmm", "yyyyMMdd HH mm", "yyyyMMdd HH",
			"yyyy-MM-dd", "yyyyMMdd", "MMdd HH mm", "MMdd HHmm", "MMdd HH", "MMdd"
		],
		change: onKendoPickerChange, 
	});

	$("#form-1 span[name='subtitle']").text(subtitle ? subtitle : "추가");
	

	$("#form-1").validate({
		rules: {
		}
	});
}


function saveForm1() {
	
	// kendo datepicker validation
	validateKendoDateTimeValue($("#form-1 input[name='startDate']"));
	
	var channel = $("#form-1 select[name='channel']").val();
	
	if ($("#form-1").valid() && channel) {
    	var data = {
    		id: Number($("#form-1").attr("rowid")),
    		name: $.trim($("#form-1 input[name='name']").val()),
    		channel: Number(channel),
    		startDate: $("#form-1 input[name='startDate']").data("kendoDateTimePicker").value(),
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
	
	
	$("#form-1 input[name='name']").val(dataItem.name);
	
	bootstrapSelectVal($("#form-1 select[name='channel']"), dataItem.channelId);
	bootstrapSelectDisabled($("#form-1 select[name='channel']"), true);

	$("#form-1 input[name='startDate']").data("kendoDateTimePicker").value(dataItem.startDate);

	$("#form-1 input[name='activeStatus']").prop("checked", dataItem.activeStatus);

	
	$('#form-modal-1 .modal-dialog').draggable({ handle: '.modal-header' });
	$("#form-modal-1").modal();
}


var adData = [];
var plId = null;

function initForm2(title) {
	
	$("#formRoot").html(kendo.template($("#template-2").html()));

	$("#form-2 span[name='title']").text(title);
	

	// 썸네일 크기
	$("#form-2 input[name='thumb-size-radio']").change(function() {
		if ($(this).val() == 'S') {
			$("#form-2 img[name='thumb-img']").removeClass("thumb-128").addClass("thumb-64");
		} else {
			$("#form-2 img[name='thumb-img']").removeClass("thumb-64").addClass("thumb-128");
		}
	});


	dragula([$('#dragula-left')[0], $('#dragula-right')[0]], {
		revertOnSpill: true,
		copy: function (el, source) {
			return source === $('#dragula-left')[0];
		},
		accepts: function (el, target) {
			return target !== $('#dragula-left')[0];
		},		
		removeOnSpill: function (el, source) {
			return source === $('#dragula-right')[0];
		},
		moves: function (el, container, handle) {

<c:if test="${isMobileMode}">
			var iconHandler = handle;
			if (handle.tagName == "path") {
				iconHandler = handle.parentElement;
			}

			return iconHandler.classList.contains('fa-arrows-alt');
</c:if>

<c:if test="${not isMobileMode}">
			return true;
</c:if>

		},
	}).on('dragend', function (el) {
		displaySelCount();
	});
}


function displayAvailCount() {
	
	var length = $("#dragula-left .creat-item").length;
	$("#avail-pl-count").text(length);
}


function displaySelCount() {
	
	var length = $("#dragula-right .creat-item").length;
	$("#sel-pl-count").text(length);
}


function appendOneItem(id) {
	
	var template = kendo.template($("#itemTemplate").html());
	
	for(var i in adData) {
		if (adData[i].valid && id != -1 && adData[i].adCreatId == id) {
			$('#dragula-right').append(template(adData[i]));
		}
	}
	
	displaySelCount();
}


function copyToRight() {
	
	var template = kendo.template($("#itemTemplate").html());
	
	for(var i in adData) {
		if (adData[i].valid) {
			$('#dragula-right').append(template(adData[i]));
		}
	}
	
	if ($("#form-2 input[name='thumb-size-radio']:checked").val() == 'S') {
		$("#form-2 img[name='thumb-img']").removeClass("thumb-128").addClass("thumb-64");
	} else {
		$("#form-2 img[name='thumb-img']").removeClass("thumb-64").addClass("thumb-128");
	}
	
	displaySelCount();
}


function editList(id) {
	
	showWaitModal();
	
	var dataItem = $("#grid").data("kendoGrid").dataSource.get(id);
	
	$.ajax({
		type: "POST",
		contentType: "application/json",
		dataType: "json",
		url: "${readAdUrl}",
		data: JSON.stringify({ id: dataItem.id }),
		success: function (data, status) {
			
			plId = dataItem.id;
			
			initForm2(dataItem.name);
			
			var template = kendo.template($("#itemTemplate").html());
			var redTemplate = kendo.template($("#redItemTemplate").html());
			var yellowTemplate = kendo.template($("#yellowItemTemplate").html());
			for(var i in data) {
				if (data[i].valid) {
					$('#dragula-left').append(template(data[i]));
				} else {
					$('#dragula-left').append(yellowTemplate(data[i]));
				}
			}
			
			displayAvailCount();
			displaySelCount();
			
			// '유효 광고 복사'를 위한
			adData = data;
			
			
			var value = dataItem.adValue.split("|");
			for(var i = 0; i < value.length; i ++) {
				var itemIncluded = false;
				if (value[i]) {
					for(var j = 0; j < data.length; j ++) {
						if (value[i] == data[j].adCreatId) {
							if (data[j].valid) {
								$('#dragula-right').append(template(data[j]));
							} else {
								$('#dragula-right').append(yellowTemplate(data[j]));
							}
							itemIncluded = true;
							break;
						}
					}
					if (!itemIncluded) {
						$('#dragula-right').append(redTemplate);
					}
				}
			}
			
			displaySelCount();
			

			$('#form-modal-2 .modal-dialog').draggable({ handle: '.modal-header' });
			$("#form-modal-2").modal();
			
			hideWaitModal();
		},
		error: ajaxReadError
	});
}


function saveForm2() {

	var ids = "";
	var goAhead = true;
	
	var adCnt = 0;
	var totDurSecs = 0;
	$("#dragula-right .creat-item").each(function(index) {
		var currId = $(this).attr("ukid");
		if (ids) {
			ids = ids + "|" + currId;
		} else {
			ids = currId;
		}
		if (currId == "-1") {
			goAhead = false;
		} else {
			var dur = $(this).attr("dur");
			if (dur) {
				totDurSecs = totDurSecs + Number($(this).attr("dur"));
				adCnt ++;
			}
		}
	});

	if (!goAhead) {
		showAlertModal("danger", "미확인이거나 서비스 불가능한 광고/컨텐츠를 포함하는 재생목록은 저장될 수 없습니다.");
		return;
	}
	
	var data = {
    	id: plId,
    	ids: ids,
    	cnt: adCnt,
    	duration: totDurSecs,
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


function initForm3() {
	
	$("#formRoot").html(kendo.template($("#template-3").html()));
	
	$("#form-3 select[name='channel']").selectpicker('render');
	
	$("#form-3 input[name='startDate']").kendoDateTimePicker({
		format: "yyyy-MM-dd HH:mm", 
		parseFormats: [
			"yyyy-MM-dd HH:mm", "yyyy-MM-dd HHmm", "yyyy-MM-dd HH mm", "yyyy-MM-dd HH",
			"yyyyMMdd HH:mm", "yyyyMMdd HHmm", "yyyyMMdd HH mm", "yyyyMMdd HH",
			"yyyy-MM-dd", "yyyyMMdd", "MMdd HH mm", "MMdd HHmm", "MMdd HH", "MMdd"
		],
		change: onKendoPickerChange, 
	});
	

	$("#form-3").validate({
		rules: {
		}
	});
}


function copyAs(id) {
	
	initForm3();

	var dataItem = $("#grid").data("kendoGrid").dataSource.get(id);
	
	$("#form-3").attr("rowid", dataItem.id);
	$("#form-3").attr("url", "${copyAsUrl}");
	
	
	$("#form-3 input[name='name']").val(dataItem.name);
	$("#form-3 input[name='desc']").val(dataItem.adCount + " 광고 - " + dataItem.totDurSecs + " 초");
	
	bootstrapSelectVal($("#form-3 select[name='channel']"), dataItem.channelId);

	$("#form-3 input[name='startDate']").data("kendoDateTimePicker").value(dataItem.startDate);

	
	$('#form-modal-3 .modal-dialog').draggable({ handle: '.modal-header' });
	$("#form-modal-3").modal();
}


function saveForm3() {
	
	// kendo datepicker validation
	validateKendoDateTimeValue($("#form-3 input[name='startDate']"));
	
	var channel = $("#form-3 select[name='channel']").val();
	
	if ($("#form-3").valid() && channel) {
    	var data = {
    		id: Number($("#form-3").attr("rowid")),
    		name: $.trim($("#form-3 input[name='name']").val()),
    		channel: Number(channel),
    		startDate: $("#form-3 input[name='startDate']").data("kendoDateTimePicker").value(),
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

<func:cmmValidate />


<!-- Closing tags -->

<common:base />
<common:pageClosing />
