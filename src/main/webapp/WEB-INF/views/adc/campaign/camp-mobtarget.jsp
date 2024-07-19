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

<c:url value="/adc/campaign/mobtargets/read" var="readUrl" />
<c:url value="/adc/campaign/mobtargets/readOrdered" var="readOrderedUrl" />
<c:url value="/adc/campaign/mobtargets/reorder" var="reorderUrl" />

<c:url value="/adc/campaign/mobtargets/readMobRgn" var="readMobRgnUrl" />
<c:url value="/adc/campaign/mobtargets/readRadRgn" var="readRadRgnUrl" />
<c:url value="/adc/campaign/mobtargets/saveMobRgn" var="saveMobRgnUrl" />
<c:url value="/adc/campaign/mobtargets/saveRadRgn" var="saveRadRgnUrl" />

<c:url value="/adc/campaign/mobtargets/updateFilterType" var="updateFilterTypeUrl" />
<c:url value="/adc/campaign/mobtargets/destroy" var="destroyUrl" />


<!-- Opening tags -->

<common:mediumPageOpeningNaverMap />


<!-- Page title -->

<h4 class="pt-1 pb-3 mb-3">
	<span class="text-muted font-weight-light">캠페인<span class="px-2">/</span>${pageTitle}<span class="px-2">/</span></span>
	<span class="mr-1 fa-light fa-bullseye-arrow"></span><span class="pl-1">모바일 타겟팅</span>
</h4>

<hr class="border-light container-m--x mt-0 mb-4">





<!-- Page body -->


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

<ul class="nav nav-tabs tabs-alt mb-4 mt-3">
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
		<a class="nav-link" href="/adc/campaign/creatives/${Campaign.id}">
			<i class="mr-1 fa-light fa-clapperboard-play"></i>
			광고 소재
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link active" href="/adc/campaign/mobtargets/${Campaign.id}">
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
		location.href = "/adc/campaign/mobtargets/${Campaign.id}/" + $("select[name='nav-item-ad-select']").val();
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


<!-- Page scripts  -->


<link href="/resources/vendor/lib/bootstrap-toggle/bootstrap-toggle.min.css" rel="stylesheet">
<link rel="stylesheet" href="/resources/vendor/lib/dragula/dragula.css">

<script src="/resources/vendor/lib/bootstrap-toggle/bootstrap-toggle.min.js"></script>
<script src="/resources/vendor/lib/dragula/dragula.js"></script>
<script src="/resources/vendor/lib/sortablejs/sortable.js"></script>


<!-- Java(optional)  -->

<%
	String noRecordsTemplate =
			"<div class='container text-center my-4'>" +
				"<div class='d-flex justify-content-center align-self-center'>" +
					"<span class='fa-thin fa-hexagon-exclamation fa-3x'></span>" +
					"<span class='pl-3 mt-2' style='font-weight: 300; font-size: 1.3rem;'>해당 자료 없음</span>" +
				"</div>" +
			"</div>";
			
	
	String mobTypeTemplate =
			"# if (mobType == 'RG') { #" +
				"<span class='fa-regular fa-mountain-city fa-fw'></span><span class='pl-1'>모바일 지역</span>" +
			"# } else if (mobType == 'CR') { #" +
				"<span class='fa-regular fa-circle-dot fa-fw'></span><span class='pl-1'>원 반경 지역</span>" +
			"# } else { #" +
				"<span>-</span>" +
			"# } #";

	String andOrTemplate =
			"<div class='toggle-container'>" +
				"<input class='toggle-btn' type='checkbox' data-toggle='toggle' data-on='And' data-off='Or' " +
						"data-onstyle='info' data-offstyle='success' rowid='#= id #' #= filterType == 'A' ? 'checked' : '' #>" +
			"</div>";
			
	// 지리 위치 대상에 대한 변경 예상
	String geoLocTemplate =
			"<a href='javascript:showGeoLoc(\"ADMOB\", #= id #)' class='btn btn-default btn-xs icon-btn'>" +
				"<span class='fa-regular fa-location-dot text-info'></span>" +
			"</a>";
%>



<!-- Kendo grid  -->

<div class="mb-4">
<kendo:grid name="grid" pageable="true" filterable="false" sortable="false" scrollable="true" reorderable="true" resizable="true">
    <kendo:grid-selectable mode="multiple, row"/>
	<kendo:grid-pageable refresh="true" previousNext="false" numeric="false" pageSize="10000" info="false" />
	<kendo:grid-toolbarTemplate>
    	<div class="clearfix">
    		<div class="float-left">
    			<button type="button" class="btn btn-outline-success dropdown-toggle" data-toggle="dropdown">타겟팅 추가</button>
				<div class="dropdown-menu">
					<a class="dropdown-item" href="javascript:void(0)" id="mob-region-btn">
						<i class="fa-light fa-mountain-city fa-fw"></i><span class="pl-2">모바일 지역</span>
					</a>
					<div class="dropdown-divider"></div>
					<a class="dropdown-item" href="javascript:void(0)" id="mob-circle-radius-btn">
						<i class="fa-light fa-circle-dot fa-fw"></i><span class="pl-2">원 반경 지역</span>
					</a>
				</div>
   				<button id="reorder-btn" type="button" class="btn btn-default">순서 변경</button>
    		</div>
    		<div class="float-right">
    			<button id="delete-mob-target-btn" type="button" class="btn btn-danger">삭제</button>
    		</div>
    	</div>
	</kendo:grid-toolbarTemplate>
	<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />
	<kendo:grid-columns>
		<kendo:grid-column width="100" template="<%= andOrTemplate %>" />
		<kendo:grid-column title="타겟 유형" field="mobType" width="150" template="<%= mobTypeTemplate %>" />
		<kendo:grid-column title="대상" field="tgtName" width="500" />
		<kendo:grid-column title="서비스중" field="activeStatus" width="100"
				template="#= activeStatus ? \"<span class='fa-light fa-check'>\" : \"\"#" />
		<kendo:grid-column title="지리 위치" width="100" template="<%= geoLocTemplate %>" />
	</kendo:grid-columns>
	<kendo:grid-dataBound>
		<script>
			function grid_dataBound(e) {
				$('.toggle-btn').bootstrapToggle();
				$('.toggle-btn').change(function() {
					$.ajax({
						type: "POST",
						contentType: "application/json",
						dataType: "json",
						url: "${updateFilterTypeUrl}",
						data: JSON.stringify({ id: Number($(this).attr("rowid")), filterType: $(this).is(":checked") ? "A" : "O" }),
						success: function (form) {
        					showOperationSuccessMsg();
        					$("#grid").data("kendoGrid").dataSource.read();
						},
						error: ajaxOperationError
					});
					
			    });
				
				var rows = this.dataSource.view();
				if (rows.length > 0) {
					$(this.tbody.find("tr[data-uid='" + rows[0].uid + "'] .toggle-container")).hide();
				}
        	}
		</script>
	</kendo:grid-dataBound>
	<kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="siblingSeq" dir="asc"/>
		</kendo:dataSource-sort>
       	<kendo:dataSource-filter>
      		<kendo:dataSource-filterItem field="adId" operator="eq" logic="and" value="${Ad.id}" >
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


/* 순서 변경의 항목 보기 좋게 */
.reorder-custom {
	font-size:.9rem; font-weight: 300; margin: .2rem;
}


#grid-mob-rgn tr.k-alt {
	background: transparent;
}

</style>

<!-- / Kendo grid  -->


<!-- Grid button actions  -->

<script>
$(document).ready(function() {

	// Mobile Add
	$("#mob-region-btn").click(function(e) {
		e.preventDefault();
		
		initForm11();

		$('#form-modal-11 .modal-dialog').draggable({ handle: '.modal-header' });
		$("#form-modal-11").modal();
	});

	$("#mob-circle-radius-btn").click(function(e) {
		e.preventDefault();
		
		initForm12();

		$('#form-modal-12 .modal-dialog').draggable({ handle: '.modal-header' });
		$("#form-modal-12").modal();
	});
	// / Mobile Add
	
	// Delete
	$("#delete-mob-target-btn").click(function(e) {
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
							
							//삭제 대상
							//calcTargetScreenCnt();
						},
						error: ajaxDeleteError
					});
				}
			}, true, delRows.length);
		}
	});
	// / Delete
	
	// Reorder
	$("#reorder-btn").click(function(e) {
		e.preventDefault();
		
		initForm16();

		
		$('#form-modal-16 .modal-dialog').draggable({ handle: '.modal-header' });
		$("#form-modal-16").modal();
	});
	// / Reorder
	
	// Calc
	$("#calc-target-btn").click(function(e) {
		e.preventDefault();
			
		calcTargetScreenCnt();
	});
	// / Calc
	
	// Geo Loc
	$("#geo-loc-btn").click(function(e) {
		e.preventDefault();
			
		showGeoLoc("AD", ${Ad.id});
	});
	// / Geo Loc
	
});	
</script>

<!-- / Grid button actions  -->


<!--  Forms -->

<script id="template-11" type="text/x-kendo-template">

<div class="modal fade" data-backdrop="static" id="form-modal-11">
	<div class="modal-dialog">
		<form class="modal-content" id="form-11" rowid="-1" url="${saveMobRgnUrl}">
      
			<!-- Modal Header -->
			<div class="modal-header move-cursor">
				<h5 class="modal-title">
					모바일 지역
					<span class="font-weight-light pl-1"><span name="subtitle"></span></span>
				</h5>
				<button type="button" class="close" data-dismiss="modal">×</button>
			</div>
        
			<!-- Modal body -->
			<div class="modal-body modal-bg-color">
    			<div id="grid-mob-rgn" class="m-2"></div>
			</div>
        
			<!-- Modal footer -->
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">취소</button>
				<button type="button" class="btn btn-primary" name="save-btn" onclick='saveForm11()'>저장</button>
			</div>
			
		</form>
	</div>
</div>

</script>


<script id="template-12" type="text/x-kendo-template">

<div class="modal fade" data-backdrop="static" id="form-modal-12">
	<div class="modal-dialog">
		<form class="modal-content" id="form-12" rowid="-1" url="${saveRadRgnUrl}">
      
			<!-- Modal Header -->
			<div class="modal-header move-cursor">
				<h5 class="modal-title">
					지도의 원 반경 지역
					<span class="font-weight-light pl-1"><span name="subtitle"></span></span>
				</h5>
				<button type="button" class="close" data-dismiss="modal">×</button>
			</div>
        
			<!-- Modal body -->
			<div class="modal-body modal-bg-color">
    			<div id="grid-rad-rgn" class="m-2"></div>
			</div>
        
			<!-- Modal footer -->
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">취소</button>
				<button type="button" class="btn btn-primary" name="save-btn" onclick='saveForm12()'>저장</button>
			</div>
			
		</form>
	</div>
</div>

</script>


<script id="template-16" type="text/x-kendo-template">

<div class="modal fade" data-backdrop="static" id="form-modal-16">
	<div class="modal-dialog">
		<form class="modal-content" id="form-16" rowid="-1" url="">
      
			<!-- Modal Header -->
			<div class="modal-header move-cursor">
				<h5 class="modal-title">
					모바일 타겟팅
					<span class="font-weight-light pl-1"><span>순서 변경</span></span>
				</h5>
				<button type="button" class="close" data-dismiss="modal">×</button>
			</div>
        
			<!-- Modal body -->
			<div class="modal-body modal-bg-color">

				<div id="reorder-root-div"></div>

			</div>
        
			<!-- Modal footer -->
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">취소</button>
				<button type="button" class="btn btn-primary" onclick='saveForm16()'>저장</button>
			</div>
			
		</form>
	</div>
</div>

</script>


<script id="template-reorder-item" type="text/x-kendo-template">

# if (mobType == "RG") { #
	<span name="data" class="badge badge-pill badge-secondary reorder-custom" data-id="#: id #">
		<span class="font-weight-bold pr-1">#: seq #</span>
		<span class='fa-regular fa-mountain-city opacity-50'></span><span class='pl-1'>모바일 지역</span>
		<span class='px-1'>-</span>
		#: tgtDisplayShort #
	</span>
# } else if (mobType == "CR") { #
	<span name="data" class="badge badge-pill badge-secondary reorder-custom" data-id="#: id #">
		<span class="font-weight-bold pr-1">#: seq #</span>
		<span class='fa-regular fa-circle-dot opacity-50'></span><span class='pl-1'>원 반경 지역</span>
		<span class='px-1'>-</span>
		#: tgtDisplayShort #
	</span>
# } else { #
	<span class="badge badge-pill badge-secondary reorder-custom" data-id="#: id #">#: tgtDisplayShort #</span>
# } #

</script>

<!--  / Forms -->

<!--  Scripts -->

<script>

function initForm11(subtitle) {
	
	$("#formRoot").html(kendo.template($("#template-11").html()));
	
    $("#grid-mob-rgn").kendoGrid({
        dataSource: {
            transport: {
            	read: {
            		url: "${readMobRgnUrl}",
                    dataType: "json",
                    type: "POST",
                    contentType: "application/json",
            	},
				parameterMap: function (options,type) {
					return JSON.stringify(options);
				}
            },
            schema: {
            	data: "data",
            	total: "total",
            },
            pageSize: 10,
            sort: { field: "name", dir: "asc" }
        },
        sortable: true,
        filterable: {
        	extra: false,
        },
        scrollable: false,
        selectable: "",
        resizable: true,
        pageable: {
            buttonCount: 3,
        },
        columns: [{
        	field: "",
        	title: "",
        	template: "<label class='custom-control custom-radio m-0 p-0'><input name='popup-row-sel' type='radio' class='custom-control-input' value='#= id #'><span class='custom-control-label'></span></label>",
        	width: 1,
        },{
            field: "name",
            title: "지역명",
            width: 200
        }]
    });
	
	
	$("#form-11 span[name='subtitle']").text(subtitle ? subtitle : "타겟팅 추가");
}


function saveForm11() {

	var id = $("input[name='popup-row-sel']:checked").val();
	if (id) {
		var data = {
			ad: ${Ad.id},
			id: Number(id),
		};
        	
		$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: $("#form-11").attr("url"),
			data: JSON.stringify(data),
			success: function (data, status, xhr) {
				showSaveSuccessMsg();
				$("#form-modal-11").modal("hide");
				$("#grid").data("kendoGrid").dataSource.read();
			},
			error: ajaxSaveError
		});
	}
}


function initForm12(subtitle) {
	
	$("#formRoot").html(kendo.template($("#template-12").html()));
	
    $("#grid-rad-rgn").kendoGrid({
        dataSource: {
            transport: {
            	read: {
            		url: "${readRadRgnUrl}",
                    dataType: "json",
                    type: "POST",
                    contentType: "application/json",
            	},
				parameterMap: function (options,type) {
					return JSON.stringify(options);
				}
            },
            schema: {
            	data: "data",
            	total: "total",
            },
            pageSize: 10,
            sort: { field: "name", dir: "asc" },
            filter: { field: "medium.id", operator: "eq", logic: "and", value: "${sessionScope['currMediumId']}" }
        },
        sortable: true,
        filterable: {
        	extra: false,
        },
        scrollable: false,
        selectable: "",
        resizable: true,
        pageable: {
            buttonCount: 3,
        },
        columns: [{
        	field: "",
        	title: "",
        	template: "<label class='custom-control custom-radio m-0 p-0'><input name='popup-row-sel' type='radio' class='custom-control-input' value='#= id #'><span class='custom-control-label'></span></label>",
        	width: 1,
        },{
            field: "name",
            title: "지역명",
            width: 200
        }]
    });
	
	
	$("#form-12 span[name='subtitle']").text(subtitle ? subtitle : "타겟팅 추가");
}


function saveForm12() {

	var id = $("input[name='popup-row-sel']:checked").val();
	if (id) {
		var data = {
			ad: ${Ad.id},
			id: Number(id),
		};
        	
		$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: $("#form-12").attr("url"),
			data: JSON.stringify(data),
			success: function (data, status, xhr) {
				showSaveSuccessMsg();
				$("#form-modal-12").modal("hide");
				$("#grid").data("kendoGrid").dataSource.read();
			},
			error: ajaxSaveError
		});
	}
}


function initForm16() {
	
	$("#formRoot").html(kendo.template($("#template-16").html()));
	
	$.ajax({
		type: "POST",
		contentType: "application/json",
		dataType: "json",
		url: "${readOrderedUrl}",
		data: JSON.stringify({ adId: ${Ad.id} }),
		success: function (data) {
			
	    	var itemTemplate = kendo.template($("#template-reorder-item").html());
	    	
	    	for(var i = 0; i < data.length; i++) {
	        	$("#reorder-root-div").append(itemTemplate(data[i]));
	    	}
	    	Sortable.create(document.getElementById("reorder-root-div"), { animation: 150 });
		},
		error: ajaxReadError
	});
}


function saveForm16() {

	var items = "";
	$("#reorder-root-div span[name='data']").each(function(i, obj) {
		if (items) {
			items = items + "|";
		}
		items = items + $(obj).attr("data-id");
	})
	
	if (items) {
		$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: "${reorderUrl}",
			data: JSON.stringify({ items: items }),
			success: function (form) {
				showSaveSuccessMsg();
				$("#form-modal-16").modal("hide");
				$("#grid").data("kendoGrid").dataSource.read();
			},
			error: ajaxSaveError
		});
	} else {
		$("#form-modal-16").modal("hide");
		$("#grid").data("kendoGrid").dataSource.read();
	}
}

</script>

<!--  / Scripts -->



</c:otherwise>
</c:choose>

<!--  / Page details -->


<!-- / Page body -->





<!-- Functional tags -->

<func:cmmValidate />
<func:geoLocModal />


<!-- Closing tags -->

<common:base />
<common:pageClosing />
