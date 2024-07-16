<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>

<%@ taglib prefix="rev" tagdir="/WEB-INF/tags/rev"%>


<!-- URL -->

<c:url value="/rev/mediumrpt/creat/readOverview" var="readOverviewUrl" />
<c:url value="/rev/mediumrpt/creat/read" var="readUrl" />


<!-- Opening tags -->

<common:mediumPageOpening />


<!-- Page title -->

<h4 class="pt-1 pb-3 mb-3">
	<span class="text-muted font-weight-light">${pageTitle}<span class="px-2">/</span></span>
	<span class="mr-1 fa-light fa-clapperboard-play"></span><span class="pl-1">광고 소재</span>
</h4>

<hr class="border-light container-m--x mt-0 mb-3">






<!-- Page body -->


<!--  Overview header -->

<rev:medium />


<!--  Scripts -->

<script>

function navigateToDate(date) {
	
	showWaitModal();
	location.href = "/rev/mediumrpt/creat?date=" + date;
}


function navToRptCreat(campId, creatId) {
	var path = "/rev/camprpt/creat/" + campId + "?filter=" + creatId;
	location.href = path;
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

</script>

<!--  / Overview header -->


<!--  Tab -->
<ul class="nav nav-tabs tabs-alt mb-4 mt-4">
	<li class="nav-item">
		<a class="nav-link" href="javascript:navigateToSummary();">
			<i class="mr-1 fa-light fa-ballot"></i>
			요약
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" href="javascript:navigateToCamp();">
			<i class="mr-1 fa-light fa-briefcase"></i>
			캠페인
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" href="javascript:navigateToAd();">
			<i class="mr-1 fa-light fa-audio-description"></i>
			광고
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link active" href="javascript:navigateToCreat();">
			<i class="mr-1 fa-light fa-clapperboard-play"></i>
			광고 소재
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" href="javascript:navigateToScreen();">
			<i class="mr-1 fa-light fa-screen-users"></i>
			화면
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" href="javascript:navigateToSite();">
			<i class="mr-1 fa-light fa-map-pin"></i>
			사이트
		</a>
	</li>
</ul>
<!--  / Tab -->


<!--  Root form container -->
<div id="formRoot"></div>


<!--  Page details -->


<!-- Java(optional)  -->

<%
	String nameTemplate =
			"<div>" + 
				"# if (paused == true) { #" +
					"<span title='잠시 멈춤'><span class='fa-regular fa-circle-pause text-danger'></span></span><span class='pr-1'></span>" +
				"# } #" +
				"# if (totalRow) { #" +
					"<span class='accent'>#= name #</span>" + 
				"# } else { #" +
					"<a href='javascript:navToRptCreat(#= campId #, #= creatId #)'><span class='text-link'>#= name #</span></a>" +
				"# } #" +
				"# if (invenTargeted == true) { #" +
					"<span class='pl-1'></span><span title='인벤토리 타겟팅'><span class='fa-regular fa-bullseye-arrow text-blue'></span></span>" +
				"# } #" +
				"# if (timeTargeted == true) { #" +
					"<span class='pl-1'></span><span title='시간 타겟팅'><span class='fa-regular fa-alarm-clock text-green'></span></span>" +
				"# } #" +
			"</div>";
	String categoryTemplate =
			"# if (category == 'A') { #" +
				"<span class='fa-regular fa-tv-retro fa-fw'></span><span class='pl-1'>가전</span>" +
			"# } else if (category == 'B') { #" +
				"<span class='fa-regular fa-gamepad fa-fw'></span><span class='pl-1'>게임</span>" +
			"# } else if (category == 'C') { #" +
				"<span class='fa-regular fa-building-flag fa-fw'></span><span class='pl-1'>관공서/단체</span>" +
			"# } else if (category == 'D') { #" +
				"<span class='fa-regular fa-golf-club fa-fw'></span><span class='pl-1'>관광/레저</span>" +
			"# } else if (category == 'E') { #" +
				"<span class='fa-regular fa-books fa-fw'></span><span class='pl-1'>교육/출판</span>" +
			"# } else if (category == 'F') { #" +
				"<span class='fa-regular fa-coins fa-fw'></span><span class='pl-1'>금융</span>" +
			"# } else if (category == 'G') { #" +
				"<span class='fa-regular fa-music fa-fw'></span><span class='pl-1'>문화/엔터테인먼트</span>" +
			"# } else if (category == 'H') { #" +
				"<span class='fa-regular fa-hand-holding-seedling fa-fw'></span><span class='pl-1'>미디어/서비스</span>" +
			"# } else if (category == 'I') { #" +
				"<span class='fa-regular fa-toothbrush fa-fw'></span><span class='pl-1'>생활용품</span>" +
			"# } else if (category == 'J') { #" +
				"<span class='fa-regular fa-truck-fast fa-fw'></span><span class='pl-1'>유통</span>" +
			"# } else if (category == 'K') { #" +
				"<span class='fa-regular fa-syringe fa-fw'></span><span class='pl-1'>제약/의료</span>" +
			"# } else if (category == 'L') { #" +
				"<span class='fa-regular fa-martini-glass fa-fw'></span><span class='pl-1'>주류</span>" +
			"# } else if (category == 'M') { #" +
				"<span class='fa-regular fa-house fa-fw'></span><span class='pl-1'>주택/가구</span>" +
			"# } else if (category == 'N') { #" +
				"<span class='fa-regular fa-shirt fa-fw'></span><span class='pl-1'>패션</span>" +
			"# } else if (category == 'O') { #" +
				"<span class='fa-regular fa-spray-can fa-fw'></span><span class='pl-1'>화장품</span>" +
			"# } else { #" +
				"<span></span>" +
			"# } #";

	String rowClass =
			"# if (totalRow) { #" +
				"accent" +
			"# } #";

			
	String noRecordsTemplate =
			"<div class='container text-center my-4'>" +
				"<div class='d-flex justify-content-center align-self-center'>" +
					"<span class='fa-thin fa-hexagon-exclamation fa-3x'></span>" +
					"<span class='pl-3 mt-2' style='font-weight: 300; font-size: 1.3rem;'>해당 자료 없음</span>" +
				"</div>" +
			"</div>";
			
	String dataNameTemplate =
			"<div>" + 
				"# if (totalRow) { #" +
					"<span class='accent'>#= name #</span>" + 
				"# } else { #" +
					"<a href='javascript:navToRptCreat(#= parentId #, #= dataId #)'><span class='text-link'>#= name #</span></a>" +
				"# } #" +
			"</div>";

	String actualAmountTemplate = "<span class='" + rowClass + "'>#= kendo.format('{0:n0}', actualAmount) #</span>";
	String totalTemplate = "<span class='" + rowClass + "'>#= kendo.format('{0:n0}', total) #</span>";
	String actualCpmTemplate = "<span class='" + rowClass + "'>#= kendo.format('{0:n0}', actualCpm) #</span>";

	String cntScreenTemplate = 
			"# if (totalRow) { #" +
				"<span></span>" + 
			"# } else { #" +
				"<span>#= kendo.format('{0:n0}', cntScreen) #</span>" + 
			"# } #";
	
	String cnt00Template = "<span class='" + rowClass + "'>#= kendo.format('{0:n0}', cnt00) #</span>";
	String cnt01Template = "<span class='" + rowClass + "'>#= kendo.format('{0:n0}', cnt01) #</span>";
	String cnt02Template = "<span class='" + rowClass + "'>#= kendo.format('{0:n0}', cnt02) #</span>";
	String cnt03Template = "<span class='" + rowClass + "'>#= kendo.format('{0:n0}', cnt03) #</span>";
	String cnt04Template = "<span class='" + rowClass + "'>#= kendo.format('{0:n0}', cnt04) #</span>";
	String cnt05Template = "<span class='" + rowClass + "'>#= kendo.format('{0:n0}', cnt05) #</span>";
	String cnt06Template = "<span class='" + rowClass + "'>#= kendo.format('{0:n0}', cnt06) #</span>";
	String cnt07Template = "<span class='" + rowClass + "'>#= kendo.format('{0:n0}', cnt07) #</span>";
	String cnt08Template = "<span class='" + rowClass + "'>#= kendo.format('{0:n0}', cnt08) #</span>";
	String cnt09Template = "<span class='" + rowClass + "'>#= kendo.format('{0:n0}', cnt09) #</span>";
	String cnt10Template = "<span class='" + rowClass + "'>#= kendo.format('{0:n0}', cnt10) #</span>";
	String cnt11Template = "<span class='" + rowClass + "'>#= kendo.format('{0:n0}', cnt11) #</span>";
	String cnt12Template = "<span class='" + rowClass + "'>#= kendo.format('{0:n0}', cnt12) #</span>";
	String cnt13Template = "<span class='" + rowClass + "'>#= kendo.format('{0:n0}', cnt13) #</span>";
	String cnt14Template = "<span class='" + rowClass + "'>#= kendo.format('{0:n0}', cnt14) #</span>";
	String cnt15Template = "<span class='" + rowClass + "'>#= kendo.format('{0:n0}', cnt15) #</span>";
	String cnt16Template = "<span class='" + rowClass + "'>#= kendo.format('{0:n0}', cnt16) #</span>";
	String cnt17Template = "<span class='" + rowClass + "'>#= kendo.format('{0:n0}', cnt17) #</span>";
	String cnt18Template = "<span class='" + rowClass + "'>#= kendo.format('{0:n0}', cnt18) #</span>";
	String cnt19Template = "<span class='" + rowClass + "'>#= kendo.format('{0:n0}', cnt19) #</span>";
	String cnt20Template = "<span class='" + rowClass + "'>#= kendo.format('{0:n0}', cnt20) #</span>";
	String cnt21Template = "<span class='" + rowClass + "'>#= kendo.format('{0:n0}', cnt21) #</span>";
	String cnt22Template = "<span class='" + rowClass + "'>#= kendo.format('{0:n0}', cnt22) #</span>";
	String cnt23Template = "<span class='" + rowClass + "'>#= kendo.format('{0:n0}', cnt23) #</span>";
%>


<!-- Kendo grid  -->

<div class="mb-4">
<div class="card" style="border-bottom: transparent;">
	<h6 class="card-header with-elements py-2">
		<span class="lead">
			<span class="fa-light fa-filters fa-lg"></span>
			<span class="ml-1">필터</span>
		</span>
		<div class="card-header-elements ml-auto">
			<button type='button' id="expander-btn" class='btn icon-btn btn-sm btn-outline-secondary'> 
				<span id="expander-btn-exp" style="display: none;"><span class='fa-light fa-chevron-down fa-lg'></span></span>
				<span id="expander-btn-col"><span class='fa-light fa-chevron-left fa-lg'></span></span>
			</button>
			<span class="pr-2"></span>
			<button type='button' id="filter-btn" class='btn icon-btn btn-sm btn-outline-secondary'> 
				<span class='fa-light fa-filter fa-lg'></span>
			</button>
		</div>
	</h6>
</div>
<div id="grid-overview-container" class="mb-4" style="display: none;">
<kendo:grid name="grid-overview" pageable="true" scrollable="true" filterable="false" sortable="false" resizable="true">
    <kendo:grid-selectable mode="multiple, raw"/>
	<kendo:grid-pageable refresh="true" buttonCount="5" pageSize="10" pageSizes="${pageSizesNormal}" />
	<kendo:grid-excel fileName="매체리포트(광고소재검색요약).xlsx" allPages="true" proxyURL="/proxySave"/>
   	<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />
	<kendo:grid-columns>
		<kendo:grid-column title="광고 소재" field="name" width="220" template="<%= nameTemplate %>" filterable="false" sticky="true" />
		<kendo:grid-column title="등록된 해상도" width="200" sortable="false" filterable="false" template="#= dispBadgeValues(fileResolutions) #" />
		<kendo:grid-column title="범주" field="category" width="150" filterable="false" template="<%= categoryTemplate %>" />
		<kendo:grid-column title="광고주" field="advertiserName" width="200" filterable="false" />
	</kendo:grid-columns>
	<kendo:grid-dataBound>
		<script>
			function grid_dataBound(e) {
				$('[data-toggle="tooltip"]').tooltip();
			}
		</script>
	</kendo:grid-dataBound>
	<kendo:grid-filterable>
		<kendo:grid-filterable-messages selectedItemsFormat="{0} 항목 선택됨"/>
	</kendo:grid-filterable>
	<kendo:dataSource serverPaging="false" serverSorting="false" serverFiltering="false" serverGrouping="false" error="kendoReadError">
		<kendo:dataSource-transport>
			<kendo:dataSource-transport-read url="${readOverviewUrl}" dataType="json" type="POST" contentType="application/json">
				<kendo:dataSource-transport-read-data>
					<script>
						function additionalData(e) {
							return { reqStrValue1: "${currDate}", reqStrValue2: searchIds };
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
			<kendo:dataSource-schema-model>
				<kendo:dataSource-schema-model-fields>
					<kendo:dataSource-schema-model-field name="startDate" type="date" />
					<kendo:dataSource-schema-model-field name="endDate" type="date" />
				</kendo:dataSource-schema-model-fields>
			</kendo:dataSource-schema-model>
		</kendo:dataSource-schema>
	</kendo:dataSource>
</kendo:grid>
</div>
</div>


<style>







/* 그리드 행의 높이 지정 */
.k-grid tbody tr, .k-grid tbody tr td
{
    height: 40px;
}


/* 합계 행 스타일 */
.accent {
	font-weight: 500;
}

</style>

<!-- / Kendo grid  -->


<!-- Grid button actions  -->

<script>
$(document).ready(function() {

	// Filter
	$("#filter-btn").click(function(e) {
		e.preventDefault();
		
		
		initForm1();

		
		$('#form-modal-1 .modal-dialog').draggable({ handle: '.modal-header' });
		$("#form-modal-1").modal();
	});
	// / Filter

	
	// 헤더 제외 그리드 내용 보이기/감추기
	$("#expander-btn").click(function(e) {
		e.preventDefault();
		
		if ($("#grid-overview-container").is(':visible')) {
			$("#expander-btn-exp").hide();
			$("#expander-btn-col").show();

			$("#grid-overview-container").hide();
		} else {
			$("#expander-btn-exp").show();
			$("#expander-btn-col").hide();

			$("#grid-overview-container").show();
		}
	});
	// / 그리드 보이기/감추기

});


var searchIds = null;

function initForm1(subtitle) {
	
	$("#formRoot").html(kendo.template($("#template-1").html()));

	$("#form-1 select[name='ad']").selectpicker('render');
	

	if (searchIds == null) {
		
	} else {
		
		$("#form-1 select[name='ad'] option").each(function() {
			$(this).prop("selected", ("|" + searchIds + "|").indexOf("|" + $(this).val() + "|") != -1);
		});
		$("#form-1 select[name='ad']").selectpicker("refresh");
	}
}


function searchForm() {

	var items = eval($("#form-1 select[name='ad']").val());

	if (items != null) {
		searchIds = "";
		items.forEach(function (item, index) {
			if (searchIds) {
				searchIds += "|";
			}
			searchIds += item;
		});
		
		
		if (items.length == ${CreativesSize}) {
			searchIds = null;
		}
		
		if (searchIds == null) {
			$("#filter-btn").addClass("btn-outline-secondary").removeClass("btn-primary");
		} else {
			$("#filter-btn").addClass("btn-primary").removeClass("btn-outline-secondary");
		}
		
		
		$("#form-modal-1").modal("hide");
		
		$("#grid-overview").data("kendoGrid").dataSource.read();
		$("#grid-data").data("kendoGrid").dataSource.read();
	}
}

</script>

<!-- / Grid button actions  -->


<!--  Forms -->

<script id="template-1" type="text/x-kendo-template">

<div class="modal fade" data-backdrop="static" id="form-modal-1">
	<div class="modal-dialog modal-sm">
		<form class="modal-content" id="form-1" rowid="-1" url="${createUrl}">
      
			<!-- Modal Header -->
			<div class="modal-header move-cursor">
				<h5 class="modal-title">
					필터링
					<span class="font-weight-light pl-1"><span name="subtitle">광고 소재</span>
				</h5>
				<button type="button" class="close" data-dismiss="modal">×</button>
			</div>
        
			<!-- Modal body -->
			<div class="modal-body modal-bg-color">
				<div class="form-row">
					<div class="form-group col mb-0">
						<label class="form-label">
							광고 소재
						</label>
						<select name="ad" class="selectpicker bg-white" data-style="btn-default" 
								data-none-selected-text=""
								data-count-selected-text="{0} 개의 광고 소재 항목이 선택됨"
								data-selected-text-format="count" title="대상 광고 소재를 선택하십시오"
								data-actions-box="true"
								data-select-all-text="전체 선택"
								data-deselect-all-text="전체 선택 해제"
								multiple>
<c:forEach var="item" items="${Creatives}">
	<c:choose>
	<c:when test="${item.icon eq 'selected'}">
							<option value="${item.value}" selected>${item.text}</option>
	</c:when>
	<c:otherwise>
							<option value="${item.value}">${item.text}</option>
	</c:otherwise>
	</c:choose>
</c:forEach>
						</select>
					</div>
				</div>
			</div>
        
			<!-- Modal footer -->
			<div class="modal-footer d-flex">
				<button type="button" class="btn btn-default" data-dismiss="modal">취소</button>
				<button type="button" class="btn btn-primary" onclick='searchForm()'>검색</button>
			</div>
			
		</form>
	</div>
</div>

</script>

<!--  / Forms -->


<!-- Kendo grid  -->

<div class="mb-4">
<div class="card" style="border-bottom: transparent;">
	<h6 class="card-header with-elements py-2">
		<span class="lead">
			<span class="fa-light fa-clapperboard-play fa-lg"></span>
			<span class="ml-1">광고 소재</span>
		</span>
		<div class="card-header-elements ml-auto">
			<button type='button' id="excel-btn" class='btn icon-btn btn-sm btn-outline-secondary'> 
				<span class='fa-light fa-file-excel fa-lg'></span>
			</button>
		</div>
	</h6>
</div>
<kendo:grid name="grid-data" pageable="true" scrollable="true" filterable="false" sortable="false" resizable="true">
    <kendo:grid-selectable mode="multiple, raw"/>
	<kendo:grid-pageable refresh="true" buttonCount="5" pageSize="10" pageSizes="${pageSizesNormal}" />
	<kendo:grid-excel fileName="매체리포트(광고소재).xlsx" allPages="true" proxyURL="/proxySave"/>
   	<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />
	<kendo:grid-columns>
		<kendo:grid-column title="광고 소재" field="name" width="220" template="<%= dataNameTemplate %>" filterable="false" sticky="true" />
		<kendo:grid-column title="노출량" field="total" width="100" template="<%= totalTemplate %>" filterable="false" />
		<kendo:grid-column title="화면수" field="cntScreen" width="100" template="<%= cntScreenTemplate %>" filterable="false" />
		<kendo:grid-column title="집행금액" field="actualAmount" width="120" template="<%= actualAmountTemplate %>" filterable="false" />
		<kendo:grid-column title="집행CPM" field="actualCpm" width="120" template="<%= actualCpmTemplate %>" filterable="false" />
		<kendo:grid-column title="00" field="cnt00" width="80" template="<%= cnt00Template %>" filterable="false" />
		<kendo:grid-column title="01" field="cnt01" width="80" template="<%= cnt01Template %>" filterable="false" />
		<kendo:grid-column title="02" field="cnt02" width="80" template="<%= cnt02Template %>" filterable="false" />
		<kendo:grid-column title="03" field="cnt03" width="80" template="<%= cnt03Template %>" filterable="false" />
		<kendo:grid-column title="04" field="cnt04" width="80" template="<%= cnt04Template %>" filterable="false" />
		<kendo:grid-column title="05" field="cnt05" width="80" template="<%= cnt05Template %>" filterable="false" />
		<kendo:grid-column title="06" field="cnt06" width="80" template="<%= cnt06Template %>" filterable="false" />
		<kendo:grid-column title="07" field="cnt07" width="80" template="<%= cnt07Template %>" filterable="false" />
		<kendo:grid-column title="08" field="cnt08" width="80" template="<%= cnt08Template %>" filterable="false" />
		<kendo:grid-column title="09" field="cnt09" width="80" template="<%= cnt09Template %>" filterable="false" />
		<kendo:grid-column title="10" field="cnt10" width="80" template="<%= cnt10Template %>" filterable="false" />
		<kendo:grid-column title="11" field="cnt11" width="80" template="<%= cnt11Template %>" filterable="false" />
		<kendo:grid-column title="12" field="cnt12" width="80" template="<%= cnt12Template %>" filterable="false" />
		<kendo:grid-column title="13" field="cnt13" width="80" template="<%= cnt13Template %>" filterable="false" />
		<kendo:grid-column title="14" field="cnt14" width="80" template="<%= cnt14Template %>" filterable="false" />
		<kendo:grid-column title="15" field="cnt15" width="80" template="<%= cnt15Template %>" filterable="false" />
		<kendo:grid-column title="16" field="cnt16" width="80" template="<%= cnt16Template %>" filterable="false" />
		<kendo:grid-column title="17" field="cnt17" width="80" template="<%= cnt17Template %>" filterable="false" />
		<kendo:grid-column title="18" field="cnt18" width="80" template="<%= cnt18Template %>" filterable="false" />
		<kendo:grid-column title="19" field="cnt19" width="80" template="<%= cnt19Template %>" filterable="false" />
		<kendo:grid-column title="20" field="cnt20" width="80" template="<%= cnt20Template %>" filterable="false" />
		<kendo:grid-column title="21" field="cnt21" width="80" template="<%= cnt21Template %>" filterable="false" />
		<kendo:grid-column title="22" field="cnt22" width="80" template="<%= cnt22Template %>" filterable="false" />
		<kendo:grid-column title="23" field="cnt23" width="80" template="<%= cnt23Template %>" filterable="false" />
	</kendo:grid-columns>
	<kendo:grid-dataBound>
		<script>
			function grid_dataBound(e) {
				$('[data-toggle="tooltip"]').tooltip();
			}
		</script>
	</kendo:grid-dataBound>
	<kendo:grid-filterable>
		<kendo:grid-filterable-messages selectedItemsFormat="{0} 항목 선택됨"/>
	</kendo:grid-filterable>
	<kendo:dataSource serverPaging="false" serverSorting="false" serverFiltering="false" serverGrouping="false" error="kendoReadError">
		<kendo:dataSource-transport>
			<kendo:dataSource-transport-read url="${readUrl}" dataType="json" type="POST" contentType="application/json">
				<kendo:dataSource-transport-read-data>
					<script>
						function additionalData(e) {
							return { reqStrValue1: "${currDate}", reqStrValue2: searchIds };
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
			<kendo:dataSource-schema-model>
				<kendo:dataSource-schema-model-fields>
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

	// Excel
	$("#excel-btn").click(function(e) {
		e.preventDefault();
		
		$("#grid-data").data("kendoGrid").saveAsExcel();
	});
	// / Excel

});
</script>

<!-- / Grid button actions  -->


<!-- / Page body -->





<!-- Functional tags -->


<!-- Closing tags -->

<common:base />
<common:pageClosing />
