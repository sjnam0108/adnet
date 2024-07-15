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

<c:url value="/adc/campaign/creatives/read" var="readUrl" />
<c:url value="/adc/campaign/creatives/readCreats" var="readCreatUrl" />
<c:url value="/adc/campaign/creatives/readCurrCreats" var="readCurrCreatUrl" />

<c:url value="/adc/campaign/creatives/link" var="linkUrl" />
<c:url value="/adc/campaign/creatives/update" var="updateUrl" />
<c:url value="/adc/campaign/creatives/unlink" var="unlinkUrl" />
<c:url value="/adc/campaign/creatives/editPack" var="editPackUrl" />


<!-- Opening tags -->

<common:mediumPageOpening />


<!-- Page title -->

<h4 class="pt-1 pb-3 mb-3">
	<span class="text-muted font-weight-light">캠페인<span class="px-2">/</span>${pageTitle}<span class="px-2">/</span></span>
	<span class="mr-1 fa-light fa-photo-film"></span><span class="pl-1">광고 소재</span>
</h4>

<hr class="border-light container-m--x mt-0 mb-4">





<!-- Page body -->


<!-- Page scripts  -->

<link rel="stylesheet" href="/resources/vendor/lib/dragula/dragula.css">

<script src="/resources/vendor/lib/dragula/dragula.js"></script>


<!--  Overview header -->

<c:choose>
<c:when test="${empty Ad}">

<adc:campaign />

</c:when>
<c:otherwise>

<adc:campaign-ad />

</c:otherwise>
</c:choose>

<!--  / Overview header -->


<!--  Tab -->

<ul class="nav nav-tabs tabs-alt mb-4 mt-3 mr-auto d-flex">
	<li class="nav-item">
		<a class="nav-link" href="/adc/campaign/ads/${Campaign.id}">
			<i class="mr-1 fa-light fa-audio-description"></i>
			광고 목록
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" href="/adc/campaign/detail/${Campaign.id}">
			<i class="mr-1 fa-light fa-microscope"></i>
			광고 상세
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link active" href="/adc/campaign/creatives/${Campaign.id}">
			<i class="mr-1 fa-light fa-clapperboard-play"></i>
			광고 소재
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" href="/adc/campaign/mobtargets/${Campaign.id}">
			<i class="mr-1 fa-light fa-bus"></i>
			모바일 타겟팅
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" href="/adc/campaign/invtargets/${Campaign.id}">
			<i class="mr-1 fa-light fa-bullseye-arrow"></i>
			<span id="inven-target-tab-title">인벤 타겟팅</span>
		</a>
	</li>
	<li class="nav-item mr-auto">
		<a class="nav-link" href="/adc/campaign/timetarget/${Campaign.id}">
			<i class="mr-1 fa-light fa-alarm-clock"></i>
			<span id="time-target-tab-title">시간 타겟팅</span>
		</a>
	</li>

<c:if test="${fn:length(currAds) > 0}" >

	<select name="nav-item-ad-select" class="selectpicker bg-white mb-1" data-style="btn-default" data-none-selected-text="" data-width="300px" data-size="10" >

<c:forEach var="item" items="${currAds}">

		<option value="${item.value}" data-content="<span class='fa-light ${item.icon}'></span><span class='pl-2'>${item.text}</span><span class='small pl-3 opacity-75'><span class='fa-regular ${item.subIcon}'></span></span>"></option>

</c:forEach>

	</select>

<script>
$(document).ready(function() {

	$("select[name='nav-item-ad-select']").selectpicker('render');

	$("select[name='nav-item-ad-select']").on("change.bs.select", function(e){
		location.href = "/adc/campaign/creatives/${Campaign.id}/" + $("select[name='nav-item-ad-select']").val();
	});
	
	bootstrapSelectVal($("select[name='nav-item-ad-select']"), "${currAdId}");
	
});	
</script>

</c:if>
	
</ul>

<!--  / Tab -->


<!--  Root form container -->
<div id="formRoot"></div>


<!--  Page details -->

<c:choose>
<c:when test="${fn:length(currAds) == 0}" >

	<div class="card">
		<div class="card-body">
			<div class="form-row">
				<div class='container text-center my-4'>
					<div class='d-flex justify-content-center align-self-center'>
						<span class='fa-thin fa-diamond-exclamation fa-3x'></span>
						<span class='pl-3 mt-2' style='font-weight: 300; font-size: 1.3rem;'>현재 선택된 광고 없음</span>
					</div>
				</div>
			</div>
		</div>
	</div>

</c:when>
<c:otherwise>


	<c:choose>
		<c:when test="${isPackedAdMode}" >
		
<div class="card">
	<div class="card-body">
		<div>
	    	<div class="clearfix">
	    		<div class="float-left">
					묶음 광고 소재
					<span class="small text-muted pl-3">묶음 광고 형태로 노출되는 광고의 소재 순서와 횟수를 지정합니다.</span>
	    		</div>
	    		<div class="float-right">
	    			<div id="time-target-no-data-row-menu">
						<button type='button' id="ad-pack-edit-btn" class='btn icon-btn btn-sm btn-outline-success'> 
							<span class='fas fa-pencil-alt'></span>
						</button>
	    			</div>
	    		</div>
	    	</div>
		</div>
		<div class="row form-row p-3" id="gallery-thumbnails" style="min-height: 100px;"></div>
	</div>
</div>


<!--  Forms -->

<script id="template-2" type="text/x-kendo-template">

<div class="modal fade" data-backdrop="static" id="form-modal-2">
	<div class="modal-dialog modal-lg">
		<form class="modal-content" id="form-2" rowid="-1" url="${editPackUrl}">
      
			<!-- Modal Header -->
			<div class="modal-header move-cursor">
				<h5 class="modal-title">
					묶음 광고
					<span class="font-weight-light pl-1"><span name="subtitle">광고 소재</span></span>
				</h5>
				<button type="button" class="close" data-dismiss="modal">×</button>
			</div>
        
			<!-- Modal body -->
			<div class="modal-body modal-bg-color">
				<div class="form-row">
					<div class="card col px-0 mx-1">
						<h6 class="card-header with-elements bg-secondary text-white py-1">
							<div class="card-header-title">가능한 광고 소재</div>
					<div class="card-header-elements ml-auto small">
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
							<div class="card-header-title">선택된 소재</div>
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
					<label class="btn btn-default px-2">
						<input type="radio" name="thumb-size-radio" value="S">
							<span class="fa-light fa-image fa-xs"></span><span class="pl-2">작은 썸네일</span>
						</input>
					</label>
					<label class="btn btn-default px-2 active">
						<input type="radio" name="thumb-size-radio" value="L" checked>
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

<div class="creat-item card card-condenced pc-opt" ukid="#: creatId #">
	<div class="card-body media align-items-center pl-0 py-0">
		<Img name="thumb-img" src='/thumbs/#= thumbUri #' class='thumb-128'>
		<div class="media-body pl-2">
			<span class="text-dark font-weight-semibold mb-2">
				#: creatName #
				# if (valid) { #
					<span class="pl-1"></span>
					<span class="badge badge-dot badge-success indicator"></span>
				# } #
			</span>
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
			</div>
		</div>
		<div><span class="fas fa-arrows-alt fa-lg text-secondary mobile-opt"></span></div>
	</div>
</div>

</script>


<script id="itemViewTemplate" type="text/x-kendo-template">

<div class="creat-item card card-condenced mr-2 mb-2" ukid="#: creatId #">
	<div class="card-body align-items-center p-0" style="width: 144px;">
		<div class="p-2">
			<Img name="thumb-img" src='/thumbs/#= thumbUri #' class='thumb-128'>
		</div>
		<div class="pt-2">
			<div class='d-flex justify-content-center text-dark mb-2'>#: creatName #</div>
			<div class="text-muted small px-2">
				# if (mediaType == "V") { #
					<span class="fa-solid fa-film text-primary"></span><span class="pl-1 pr-2">동영상</span>
					<span class="fa-regular fa-alarm-clock"></span><span class="pl-1">#: duration #</span>
				# } else if (mediaType == "I") { #
					<span class="fa-solid fa-image text-primary"></span><span class="pl-1">이미지</span>
				# } #
				<span class="pr-2"></span>
				<span class="fa-regular fa-ruler-vertical"></span>
				#: fileLength #
			</div>
		</div>
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

var adData = [];
var adPackIds = "${Ad.adPackIds}";


$(document).ready(function() {

	// Edit Ad Pack
	$("#ad-pack-edit-btn").click(function(e) {
		e.preventDefault();
		
		$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: "${readCreatUrl}",
			data: JSON.stringify({ id: ${Ad.id}, resolution: "${Ad.fixedResolution}" }),
			success: function (data, status) {
				
				initForm2();
			
				var template = kendo.template($("#itemTemplate").html());
				
				for(var i in data) {
					$('#dragula-left').append(template(data[i]));
				}
				
				displaySelCount();
				
				// '유효 광고 복사'를 위한
				adData = data;
				
				
				var value = adPackIds.split("|");
				for(var i = 0; i < value.length; i ++) {
					if (value[i]) {
						for(var j = 0; j < data.length; j ++) {
							if (value[i] == data[j].creatId) {
								$('#dragula-right').append(template(data[j]));
								break;
							}
						}
					}
				}
				
				displaySelCount();
				
	
				$('#form-modal-2 .modal-dialog').draggable({ handle: '.modal-header' });
				$("#form-modal-2").modal();
				
			},
			error: ajaxReadError
		});
	});
	// / Edit Ad Pack

	readCurrCreats();
	
});	


function displaySelCount() {
	
	var length = $("#dragula-right .creat-item").length;
	$("#sel-pl-count").text(length);
}


function copyToRight() {
	
	var template = kendo.template($("#itemTemplate").html());
	
	for(var i in adData) {
		if (adData[i].valid) {
			$('#dragula-right').append(template(adData[i]));
		}
	}
	
	displaySelCount();
}


function initForm2(subtitle) {

	$("#formRoot").html(kendo.template($("#template-2").html()));

	// 썸네일 크기
	$("#form-2 input[name='thumb-size-radio']").change(function(){
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


function saveForm2() {

	var ids = "";
	$("#dragula-right .creat-item").each(function(index) {
		if (ids) {
			ids = ids + "|" + $(this).attr("ukid");
		} else {
			ids = $(this).attr("ukid");
		}
	});

	var data = {
    	ad: ${Ad.id},
    	ids: ids,
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
			
			adPackIds = ids;
			readCurrCreats();
		},
		error: ajaxSaveError
	});
}


function reload() {
	
	setTimeout(function(){
		location.reload();
	}, 1000);
}


function readCurrCreats() {
	
	$.ajax({
		type: "POST",
		contentType: "application/json",
		dataType: "json",
		url: "${readCurrCreatUrl}",
		data: JSON.stringify({ id: ${Ad.id}, resolution: "${Ad.fixedResolution}" }),
		success: function (data, status) {
			
			$('#gallery-thumbnails').html("<div class='gallery-sizer col-6 col-sm-4 col-md-3 col-xl-2 position-absolute'></div>");
			
			var template = kendo.template($("#itemViewTemplate").html());
			
			for(var i in data) {
				$('#gallery-thumbnails').append(template(data[i]));
			}
			
			if (data.length == 0) {
				var html = 	"<div class='container text-center my-4'>";
				html += 		"<div class='d-flex justify-content-center align-self-center'>";
				html += 			"<span class='fa-thin fa-hexagon-exclamation fa-3x'></span>";
				html += 			"<span class='pl-3 mt-2' style='font-weight: 300; font-size: 1.3rem;'>해당 자료 없음</span>";
				html +=			"</div>";
				html +=		"</div>";
				$('#gallery-thumbnails').html(html);
			}
		},
		error: ajaxReadError
	});
}

</script>

<!--  / Scripts -->
			
		
		</c:when>
		<c:otherwise>
		
		
<!-- Java(optional)  -->

<%
	String editTemplate = 
			"<button type='button' onclick='edit(#= id #)' class='btn icon-btn btn-sm btn-outline-success borderless'>" + 
			"<span class='fas fa-pencil-alt'></span></button>";

	String statusTemplate =
			"# if (creative.status == 'D') { #" +
				"<span class='fa-regular fa-asterisk fa-fw'></span><span class='pl-1'>준비</span>" +
			"# } else if (creative.status == 'P') { #" +
				"<span class='fa-regular fa-square-question fa-fw'></span><span class='pl-1'>승인대기</span>" +
			"# } else if (creative.status == 'J') { #" +
				"<span class='fa-regular fa-do-not-enter fa-fw'></span><span class='pl-1'>거절</span>" +
			"# } else if (creative.status == 'A') { #" +
				"<span class='fa-regular fa-square-check text-blue fa-fw'></span><span class='pl-1'>승인</span>" +
			"# } else if (creative.status == 'V') { #" +
				"<span class='fa-regular fa-box-archive fa-fw'></span><span class='pl-1'>보관</span>" +
			"# } else { #" +
				"<span>-</span>" +
			"# } #";
			
	String nameTemplate =
			"<div>" + 
				"<a href='javascript:navToCreat(#= creative.advertiser.id #, #= creative.id #)'><span class='text-link'>#= creative.name #</span></a>" + 
				"# if (creative.invenTargeted == true) { #" +
					"<span class='pl-1'></span><span title='인벤토리 타겟팅'><span class='fa-regular fa-bullseye-arrow text-blue'></span></span>" +
				"# } #" +
			"</div>";
	
	String startDateTemplate = kr.adnetwork.utils.Util.getSmartDate("startDate", false, false);
	String endDateTemplate = kr.adnetwork.utils.Util.getSmartDate("endDate", false, false);

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
    <kendo:grid-selectable mode="raw"/>
	<kendo:grid-excel fileName="${pageTitle}.xlsx" allPages="true" proxyURL="/proxySave"/>
	<kendo:grid-pageable refresh="true" previousNext="false" numeric="false" pageSize="10000" info="false" />

	<kendo:grid-toolbarTemplate>
    	<div class="clearfix">
    		<div class="float-left">
    			<button id="link-btn" type="button" class="btn btn-outline-success">소재와 연결</button>
    		</div>
    		<div class="float-right">
    			<button id="unlink-btn" type="button" class="btn btn-danger">연결 해제</button>
    		</div>
    	</div>
	</kendo:grid-toolbarTemplate>
	<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />
	<kendo:grid-columns>
		<kendo:grid-column title="수정" width="50" filterable="false" sortable="false" template="<%= editTemplate %>" />
		<kendo:grid-column title="광고 소재" field="creative.name" width="160" filterable="false" template="<%= nameTemplate %>" />
		<kendo:grid-column title="해상도" field="creative.fileResolutions" width="150" filterable="false"
				template="#= dispBadgeValues(creative.fileResolutions) #" />
		<kendo:grid-column title="상태" field="creative.status" width="150" filterable="false" template="<%= statusTemplate %>" />
		<kendo:grid-column title="시작일" field="startDate" width="120" template="<%= startDateTemplate %>" filterable="false" />
		<kendo:grid-column title="종료일" field="endDate" width="120" template="<%= endDateTemplate %>" format="{0:yyyy M/d}" filterable="false" />
		<kendo:grid-column title="가중치" field="weight" width="120" filterable="false" />
	</kendo:grid-columns>
	<kendo:grid-dataBound>
		<script>
			function grid_dataBound(e) {
				var grid = e.sender;
				var rows = grid.dataSource.view();
        			
				for (var i = 0; i < rows.length; i ++) {
					if (rows[i].creative.status == 'V') {
						var editButton = grid.tbody.find("tr[data-uid='" + rows[i].uid + "'] .icon-btn");
						editButton.attr("disabled", true);
					}
				}
			}
		</script>
	</kendo:grid-dataBound>
	<kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="creative.name" dir="asc"/>
		</kendo:dataSource-sort>
       	<kendo:dataSource-filter>
      		<kendo:dataSource-filterItem field="ad.id" operator="eq" logic="and" value="${Ad.id}" >
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
					<kendo:dataSource-schema-model-field name="startDate" type="date" />
					<kendo:dataSource-schema-model-field name="endDate" type="date" />
					<kendo:dataSource-schema-model-field name="weight" type="number" />
				</kendo:dataSource-schema-model-fields>
			</kendo:dataSource-schema-model>
		</kendo:dataSource-schema>
	</kendo:dataSource>
</kendo:grid>
</div>

<style>







/* 그리드 자료 새로고침 버튼을 우측 정렬  */
div.k-pager-wrap.k-grid-pager.k-widget.k-floatwrap {
	display: flex!important;
	justify-content: flex-end!important;
}

</style>

<!-- / Kendo grid  -->


<!--  Forms -->

<script id="template-1" type="text/x-kendo-template">

<div class="modal fade" data-backdrop="static" id="form-modal-1">
	<div class="modal-dialog modal-sm">
		<form class="modal-content" id="form-1" rowid="-1" url="${linkUrl}">
      
			<!-- Modal Header -->
			<div class="modal-header move-cursor">
				<h5 class="modal-title">
					광고
					<span class="font-weight-light pl-1"><span name="subtitle"></span>
				</h5>
				<button type="button" class="close" data-dismiss="modal">×</button>
			</div>
        
			<!-- Modal body -->
			<div class="modal-body modal-bg-color">
				<div class="form-group col" id="view-type-desc-div">
					<label class="form-label">
						게시 유형
					</label>
					<input name="viewType" type="text" class="form-control" readonly>
				</div>
				<div class="form-group col">
					<label class="form-label">
						광고 소재
					</label>
					<select name="creative" class="selectpicker bg-white required" data-style="btn-default" data-none-selected-text="" >
<c:forEach var="item" items="${Creatives}">
							<option value="${item.value}">${item.text}</option>
</c:forEach>
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
				<div class="form-group col">
					<label class="form-label">
						광고 소재간 가중치
						<span class="text-danger">*</span>
					</label>
					<input name="weight" type="text" class="form-control required" value="1">
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


<!-- Grid button actions  -->

<script>
$(document).ready(function() {

	// Link
	$("#link-btn").click(function(e) {
		e.preventDefault();
		
		initForm1();

		
		$('#form-modal-1 .modal-dialog').draggable({ handle: '.modal-header' });
		$("#form-modal-1").modal();
	});
	// / Link
	
	// Unlink
	$("#unlink-btn").click(function(e) {
		e.preventDefault();
			
		var grid = $("#grid").data("kendoGrid");
		var rows = grid.select();
	
		var opRows = [];
		
		rows.each(function(index, row) {
			var selectedItem = grid.dataItem(row);
			opRows.push(selectedItem.id);
		});
		
		if (opRows.length > 0) {
			showConfirmModal("선택된 자료에 대한 연결 해제 작업을 수행할 예정입니다. 계속 진행하시겠습니까?", function(result) {
				if (result) {
					$.ajax({
						type: "POST",
						contentType: "application/json",
						dataType: "json",
						url: "${unlinkUrl}",
						data: JSON.stringify({ items: opRows }),
						success: function (form) {
							showOperationSuccessMsg();
							grid.dataSource.read();
						},
						error: ajaxOperationError
					});
				}
			});
		}
	});
	// / Unlink
	
});	
</script>

<!-- / Grid button actions  -->


<!--  Scripts -->

<script>

function initForm1(subtitle) {

	$("#formRoot").html(kendo.template($("#template-1").html()));
	
	$("#form-1 select[name='creative']").selectpicker('render');
	
	var min = new Date(${Ad.startDateLong});
	var max = new Date(${Ad.endDateLong});
	
	$("#form-1 input[name='startDate']").kendoDatePicker({
		format: "yyyy-MM-dd",
		parseFormats: [
			"yyyy-MM-dd",
		],
		value: new Date(min),
		min: min,
		max: max,
	});
	
	$("#form-1 input[name='endDate']").kendoDatePicker({
		format: "yyyy-MM-dd",
		parseFormats: [
			"yyyy-MM-dd",
		],
		value: new Date(max),
		min: min,
		max: max,
	});
	
	$("#form-1 input[name='viewType']").val("${Ad.viewTypeCode} - ${Ad.fixedResolution}");
	if ("${Ad.viewTypeCode}" == "") {
		$("#view-type-desc-div").hide();
	}
	
	
	$("#form-1 span[name='subtitle']").text(subtitle ? subtitle : "소재와 연결");
	
	$("#form-1").validate({
		rules: {
			name: {
				minlength: 2,
			},
			weight: {
				digits: true,
				min: 1,
			},
		}
	});
}


function saveForm1() {

	// kendo datepicker validation
	validateKendoDateValue($("#form-1 input[name='startDate']"));
	validateKendoDateValue($("#form-1 input[name='endDate']"));

	
	var creative = $("#form-1 select[name='creative']").val();
	
	if ($("#form-1").valid() && creative != "-1") {
    	var data = {
    		id: Number($("#form-1").attr("rowid")),
    		ad: ${Ad.id},
    		creative: Number($("#form-1 select[name='creative']").val()),
    		startDate: $("#form-1 input[name='startDate']").data("kendoDatePicker").value(),
    		endDate: $("#form-1 input[name='endDate']").data("kendoDatePicker").value(),
    		weight: Number($("#form-1 input[name='weight']").val()),
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
	
	initForm1("변경");

	var dataItem = $("#grid").data("kendoGrid").dataSource.get(id);
	
	$("#form-1").attr("rowid", dataItem.id);
	$("#form-1").attr("url", "${updateUrl}");
	
	
	$("#form-1 input[name='weight']").val(dataItem.weight);

	bootstrapSelectVal($("#form-1 select[name='creative']"), dataItem.creative.id);
	
	$("#form-1 input[name='startDate']").data("kendoDatePicker").value(dataItem.startDate);
	$("#form-1 input[name='endDate']").data("kendoDatePicker").value(dataItem.endDate);
	
	bootstrapSelectDisabled($("#form-1 select[name='creative']"), true);

	
	$('#form-modal-1 .modal-dialog').draggable({ handle: '.modal-header' });
	$("#form-modal-1").modal();
}


function dispBadgeValues(values) {
	
	var ret = "";
	var value = values.split("|");
	  
	for(var i = 0; i < value.length; i ++) {
		if (value[i]) {
			var item = value[i].split(":");
			if (item.length == 2) {
				if (Number(item[0]) == 1) {
					ret = ret + "<span class='badge badge-outline-success'>";
				} else if (Number(item[0]) == 0) {
					ret = ret + "<span class='badge badge-outline-secondary'>";
				} else {
					ret = ret + "<span class='badge badge-outline-danger'>";
				}
				
				ret = ret + item[1] + "</span><span class='pl-1'></span>";
			}
		}
	}
	  
	return ret;
}


function navToCreat(advId, id) {
	var path = "/adc/creative/files/" + advId + "/" + id;
	location.href = path;
}

</script>

<!--  / Scripts -->

		
		</c:otherwise>
	</c:choose>


</c:otherwise>
</c:choose>

<!--  / Page details -->


<!-- / Page body -->





<!-- Functional tags -->

<func:cmmValidate />


<!-- Closing tags -->

<common:base />
<common:pageClosing />
