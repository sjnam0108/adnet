<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>


<!-- URL -->

<c:url value="/adc/creative/create" var="createUrl" />
<c:url value="/adc/creative/read" var="readUrl" />
<c:url value="/adc/creative/update" var="updateUrl" />
<c:url value="/adc/creative/destroy" var="destroyUrl" />

<c:url value="/adc/creative/submit" var="submitUrl" />
<c:url value="/adc/creative/recall" var="recallUrl" />
<c:url value="/adc/creative/reject" var="rejectUrl" />
<c:url value="/adc/creative/archive" var="archiveUrl" />
<c:url value="/adc/creative/unarchive" var="unarchiveUrl" />
<c:url value="/adc/creative/pause" var="pauseUrl" />
<c:url value="/adc/creative/resume" var="resumeUrl" />
<c:url value="/adc/creative/copy" var="copyUrl" />

<c:url value="/adc/creative/readCategories" var="readCategoryUrl" />
<c:url value="/adc/creative/readTypes" var="readTypeUrl" />
<c:url value="/adc/creative/readStatuses" var="readStatusUrl" />
<c:url value="/adc/creative/readAdvertisers" var="readAdvUrl" />

<c:url value="/adc/creative/readCreatFiles" var="createCreatFileUrl" />


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
	String editTemplate =
			"<button type='button' onclick='edit(#= id #)' class='btn icon-btn btn-sm btn-outline-success borderless'>" +
			"<span class='fas fa-pencil-alt'></span></button>";

	String typeTemplate =
			"# if (type == 'C') { #" +
				"<span title='일반 광고'><span class='fa-regular fa-audio-description fa-fw'></span></span>" +
			"# } else if (type == 'F') { #" +
				"<span title='대체 광고'><span class='fa-regular fa-house text-orange fa-fw'></span></span>" +
			"# } #";
	String advertiserTemplate = "<a href='javascript:navToAdv(#= advertiser.id #)'><span class='text-link'>#= advertiser.name #</span></a>";
	String durPolicyTemplate =
			"# if (type == 'F') { #" +
				"# if (durPolicyOverriden) { #" +
					"<span class='fa-light fa-check'></span>" +
				"# } else { #" +
					"<span></span>" +
				"# } #" +
			"# } else { #" +
				"<span>-</span>" +
			"# } #";
	String fbWeightTemplate =
			"# if (type == 'F') { #" +
				"<span>#= fbWeight #</span>" +
			"# } else { #" +
				"<span>-</span>" +
			"# } #";
	String statusTemplate =
			"# if (status == 'D') { #" +
				"<span class='fa-regular fa-asterisk fa-fw'></span><span class='pl-1'>준비</span>" +
			"# } else if (status == 'P') { #" +
				"<span class='fa-regular fa-square-question fa-fw'></span><span class='pl-1'>승인대기</span>" +
			"# } else if (status == 'J') { #" +
				"<span class='fa-regular fa-do-not-enter fa-fw'></span><span class='pl-1'>거절</span>" +
			"# } else if (status == 'A') { #" +
				"<span class='fa-regular fa-square-check text-blue fa-fw'></span><span class='pl-1'>승인</span>" +
			"# } else if (status == 'V') { #" +
				"<span class='fa-regular fa-box-archive fa-fw'></span><span class='pl-1'>보관</span>" +
			"# } else { #" +
				"<span>-</span>" +
			"# } #";

	String regDateTemplate = kr.adnetwork.utils.Util.getSmartDate("whoCreationDate");
	String lastPlayDateTemplate = kr.adnetwork.utils.Util.getSmartDate("lastPlayDate");

	String nameTemplate =
			"<div>" +
				"# if (paused == true) { #" +
					"<span title='잠시 멈춤'><span class='fa-regular fa-circle-pause text-danger'></span></span><span class='pr-1'></span>" +
				"# } #" +
				"<a href='javascript:navToCreat(#= advertiser.id #, #= id #)'><span class='text-link'>#= name #</span></a>" +
				"# if (invenTargeted == true) { #" +
					"<span class='pl-1'></span><span title='인벤토리 타겟팅'><span class='fa-regular fa-bullseye-arrow text-blue'></span></span>" +
				"# } #" +
				"# if (timeTargeted == true) { #" +
					"<span class='pl-1'></span><span title='시간 타겟팅'><span class='fa-regular fa-alarm-clock text-green'></span></span>" +
				"# } #" +
			"</div>";

	String noRecordsTemplate =
			"<div class='container text-center my-4'>" +
				"<div class='d-flex justify-content-center align-self-center'>" +
					"<span class='fa-thin fa-hexagon-exclamation fa-3x'></span>" +
					"<span class='pl-3 mt-2' style='font-weight: 300; font-size: 1.3rem;'>해당 자료 없음</span>" +
				"</div>" +
			"</div>";

	String categoryTemplate =
			"# if (category == 'A') { #" +
				"<span title='가전'><span class='fa-regular fa-tv-retro fa-fw'></span></span>" +
			"# } else if (category == 'B') { #" +
				"<span title='게임'><span class='fa-regular fa-gamepad fa-fw'></span></span>" +
			"# } else if (category == 'C') { #" +
				"<span title='관공서/단체'><span class='fa-regular fa-building-flag fa-fw'></span></span>" +
			"# } else if (category == 'D') { #" +
				"<span title='관광/레저'><span class='fa-regular fa-golf-club fa-fw'></span></span>" +
			"# } else if (category == 'E') { #" +
				"<span title='교육/출판'><span class='fa-regular fa-books fa-fw'></span></span>" +
			"# } else if (category == 'F') { #" +
				"<span title='금융'><span class='fa-regular fa-coins fa-fw'></span></span>" +
			"# } else if (category == 'G') { #" +
				"<span title='문화/엔터테인먼트'><span class='fa-regular fa-music fa-fw'></span></span>" +
			"# } else if (category == 'H') { #" +
				"<span title='미디어/서비스'><span class='fa-regular fa-hand-holding-seedling fa-fw'></span></span>" +
			"# } else if (category == 'I') { #" +
				"<span title='생활용품'><span class='fa-regular fa-toothbrush fa-fw'></span></span>" +
			"# } else if (category == 'J') { #" +
				"<span title='유통'><span class='fa-regular fa-truck-fast fa-fw'></span></span>" +
			"# } else if (category == 'K') { #" +
				"<span title='제약/의료'><span class='fa-regular fa-syringe fa-fw'></span></span>" +
			"# } else if (category == 'L') { #" +
				"<span title='주류'><span class='fa-regular fa-martini-glass fa-fw'></span></span>" +
			"# } else if (category == 'M') { #" +
				"<span title='주택/가구'><span class='fa-regular fa-house fa-fw'></span></span>" +
			"# } else if (category == 'N') { #" +
				"<span title='패션'><span class='fa-regular fa-shirt fa-fw'></span></span>" +
			"# } else if (category == 'O') { #" +
				"<span title='화장품'><span class='fa-regular fa-spray-can fa-fw'></span></span>" +
			"# } #";


	String thumbTemplate = "<Img src='/thumbs/#= ctntFolder.name #/#= thumbFilename #' width='128' height='128'>";
	String fileTemplate = "<span class='pr-2'>#= srcFilename #</span><a href='" +
			"#= ctntFolder.webPath #/#= ctntFolder.name #/#= uuid #/#= filename #' target='_blank'>" +
			"<span class='fa-light fa-arrow-up-right-from-square'></span></a>";
	String resolTemplate =
			"# if (fitnessOfResRatio == 1) { #" +
				"<span class='badge badge-outline-success'>#= resolution #</span>" +
			"# } else if (fitnessOfResRatio == 0) { #" +
				"<span class='badge badge-outline-secondary'>#= resolution #</span>" +
			"# } else if (fitnessOfResRatio == -1) { #" +
				"<span class='badge badge-outline-danger'>#= resolution #</span>" +
			"# } #";
	String durTemplate =
			"# if (mediaType == 'V') { #" +
				"<div class='dur-container'><span data-toggle='tooltip' data-placement='top' title='#= dispSrcDurSecs #'>#= durSecs #s</span></div>" +
			"# } else { #" +
				"<span>-</span>" +
			"# } #";

	String lengthTemplate = "<div class='len-container'><span data-toggle='tooltip' data-placement='top' title='#= dispFileLength #'>#= smartLength #</span></div>";

	String typeNameTemplate =
			"<div>" +
				"# if (creative.type == 'C') { #" +
					"<span title='일반 광고' class='pr-1'><span class='fa-regular fa-audio-description fa-fw'></span></span>" +
				"# } else if (creative.type == 'F') { #" +
					"<span title='대체 광고' class='pr-1'><span class='fa-regular fa-house text-orange fa-fw'></span></span>" +
				"# } #" +
				"# if (creative.paused == true) { #" +
					"<span title='잠시 멈춤'><span class='fa-regular fa-circle-pause text-danger'></span></span><span class='pr-1'></span>" +
				"# } #" +
				"<a href='javascript:navToCreat(#= creative.advertiser.id #, #= creative.id #)'><span class='text-link'>#= creative.name #</span></a>" +
				"# if (creative.invenTargeted == true) { #" +
					"<span class='pl-1'></span><span title='인벤토리 타겟팅'><span class='fa-regular fa-bullseye-arrow text-blue'></span></span>" +
				"# } #" +
				"# if (creative.timeTargeted == true) { #" +
					"<span class='pl-1'></span><span title='시간 타겟팅'><span class='fa-regular fa-alarm-clock text-green'></span></span>" +
				"# } #" +
			"</div>";
%>



<!--  Forms -->

<ul class="nav nav-tabs tabs-alt container-p-x container-m--x mb-4">
	<li class="nav-item">
		<a class="nav-link active" data-toggle="tab" href="#creative">
			<i class="mr-1 fa-light fa-clapperboard-play"></i>
			광고 소재
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" data-toggle="tab" href="#creat-file">
			<i class="mr-1 fa-light fa-photo-film"></i>
			소재 파일
		</a>
	</li>
</ul>

<div class="tab-content">
	<div class="tab-pane active" id="creative">



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

<c:if test="${fn:length(otherMedia) > 0}" >
    			<div class="btn-group">
					<button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown">
						<span class="fa-light fa-lg fa-wand-sparkles"></span>
						<span class="pl-1">다른 매체로 복사</span>
					</button>
					<div class="dropdown-menu">

	<c:forEach var="item" items="${otherMedia}">

						<a class="dropdown-item" href="javascript:copyToMedium('${item.shortName}')">
							<span class="fa-light fa-earth-asia"></span>
							<span class="pl-1">${item.shortName}</span>
							<span class='small pl-3'>${item.name}</span>
						</a>

	</c:forEach>

					</div>

</c:if>

				</div>
    			<div class="btn-group">
					<button type="button" class="btn btn-secondary dropdown-toggle" data-toggle="dropdown">
						<span class="fa-light fa-lg fa-signs-post"></span>
						<span class="pl-1">진행</span>
					</button>
					<div class="dropdown-menu">
						<a class="dropdown-item" href="javascript:void(0)" id="submit-btn">
							<i class="fa-light fa-paper-plane fa-fw"></i><span class="pl-2">승인 요청</span>
						</a>
						<a class="dropdown-item" href="javascript:void(0)" id="recall-btn">
							<i class="fa-light fa-arrow-rotate-left fa-fw"></i><span class="pl-2">승인 요청 회수</span>
						</a>
						<div class="dropdown-divider"></div>
						<a class="dropdown-item" href="javascript:void(0)" id="reject-btn">
							<i class="fa-light fa-thumbs-down fa-fw"></i><span class="pl-2">승인 소재 거절</span>
						</a>
						<div class="dropdown-divider"></div>
						<a class="dropdown-item" href="javascript:void(0)" id="archive-btn">
							<i class="fa-light fa-box-archive fa-fw"></i><span class="pl-2">보관</span>
						</a>
						<a class="dropdown-item" href="javascript:void(0)" id="unarchive-btn">
							<i class="fa-light fa-box-open fa-fw"></i><span class="pl-2">보관 해제</span>
						</a>
						<div class="dropdown-divider"></div>
						<a class="dropdown-item" href="javascript:void(0)" id="pause-btn">
							<i class="fa-regular fa-circle-pause text-danger fa-fw"></i><span class="pl-2">잠시 멈춤</span>
						</a>
						<a class="dropdown-item" href="javascript:void(0)" id="resume-btn">
							<i class="fa-light fa-play fa-fw"></i><span class="pl-2">재개</span>
						</a>
						<div class="dropdown-divider"></div>
						<a class="dropdown-item" href="javascript:void(0)" id="delete-btn">
							<i class="fa-regular fa-trash-can text-danger fa-fw"></i><span class="pl-2">삭제</span>
						</a>
					</div>
				</div>
    		</div>
    	</div>
	</kendo:grid-toolbarTemplate>
	<kendo:grid-filterable extra="false" />

	<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />
	<kendo:grid-columns>
		<kendo:grid-column title="수정" width="50" filterable="false" sortable="false" template="<%= editTemplate %>" />
		<kendo:grid-column title="유형" field="type" width="80" template="<%= typeTemplate %>" >
			<kendo:grid-column-filterable multi="true" itemTemplate="kfcIconText">
				<kendo:dataSource>
					<kendo:dataSource-transport>
						<kendo:dataSource-transport-read url="${readTypeUrl}" dataType="json" type="POST" contentType="application/json" />
					</kendo:dataSource-transport>
				</kendo:dataSource>
			</kendo:grid-column-filterable>
		</kendo:grid-column>
		<kendo:grid-column title="광고 소재" field="name" width="250" template="<%= nameTemplate %>" />
		<kendo:grid-column title="등록된 해상도" width="200" sortable="false" filterable="false"
				template="#= dispBadgeValues(fileResolutions) #" />
		<kendo:grid-column title="범주" field="category" width="80" template="<%= categoryTemplate %>" >
			<kendo:grid-column-filterable multi="true" itemTemplate="kfcIconText">
				<kendo:dataSource>
					<kendo:dataSource-transport>
						<kendo:dataSource-transport-read url="${readCategoryUrl}" dataType="json" type="POST" contentType="application/json" />
					</kendo:dataSource-transport>
				</kendo:dataSource>
			</kendo:grid-column-filterable>
		</kendo:grid-column>
		<kendo:grid-column title="게시 유형" field="viewTypeCode" width="120"  />
		<kendo:grid-column title="상태" field="status" width="120" template="<%= statusTemplate %>" >
			<kendo:grid-column-filterable multi="true" itemTemplate="kfcIconText">
				<kendo:dataSource>
					<kendo:dataSource-transport>
						<kendo:dataSource-transport-read url="${readStatusUrl}" dataType="json" type="POST" contentType="application/json" />
					</kendo:dataSource-transport>
				</kendo:dataSource>
			</kendo:grid-column-filterable>
		</kendo:grid-column>
		<kendo:grid-column title="광고주" field="advertiser.name" width="200" template="<%= advertiserTemplate %>" />
		<kendo:grid-column title="최근 방송" field="lastPlayDate" width="120" filterable="false" sortable="false" template="<%= lastPlayDateTemplate %>" />
		<kendo:grid-column title="잠시 멈춤" field="paused" width="120"
				template="#=paused ? \"<span class='fa-light fa-check'>\" : \"\"#" />
		<kendo:grid-column title="재생시간 정책 무시" field="durPolicyOverriden" width="180" template="<%= durPolicyTemplate %>" />
		<kendo:grid-column title="대체 광고 가중치" field="fbWeight" width="150" template="<%= fbWeightTemplate %>" />
		<kendo:grid-column title="등록" field="whoCreationDate" width="120" template="<%= regDateTemplate %>" />
	</kendo:grid-columns>
	<kendo:grid-filterable>
		<kendo:grid-filterable-messages selectedItemsFormat="{0} 항목 선택됨"/>
	</kendo:grid-filterable>
	<kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="whoCreationDate" dir="desc"/>
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
					<kendo:dataSource-schema-model-field name="paused" type="boolean" />
					<kendo:dataSource-schema-model-field name="durPolicyOverriden" type="boolean" />
					<kendo:dataSource-schema-model-field name="fbWeight" type="number" />
					<kendo:dataSource-schema-model-field name="lastPlayDate" type="date" />
					<kendo:dataSource-schema-model-field name="whoCreationDate" type="date" />
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
	$("#add-btn").click(function(e) {
		e.preventDefault();

		// 최신의 광고주 정보부터 획득하고 시작
		$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: "${readAdvUrl}",
			data: JSON.stringify({ }),
			success: function (data, status) {
				advertisers = eval(data);

				initForm2();

				$('#form-modal-2 .modal-dialog').draggable({ handle: '.modal-header' });
				$("#form-modal-2").modal();
			},
			error: ajaxReadError
		});
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

	// Submit
	$("#submit-btn").click(function(e) {
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
				url: "${submitUrl}",
				data: JSON.stringify({ items: opRows }),
				success: function (form) {
					showOperationSuccessMsg();
					grid.dataSource.read();
				},
				error: ajaxOperationError
			});
		}
	});
	// / Submit

	// Recall
	$("#recall-btn").click(function(e) {
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
				url: "${recallUrl}",
				data: JSON.stringify({ items: opRows }),
				success: function (form) {
					showOperationSuccessMsg();
					grid.dataSource.read();
				},
				error: ajaxOperationError
			});
		}
	});
	// / Recall

	// Reject
	$("#reject-btn").click(function(e) {
		e.preventDefault();

		var grid = $("#grid").data("kendoGrid");
		var rows = grid.select();

		var opRows = [];

		rows.each(function(index, row) {
			var selectedItem = grid.dataItem(row);
			opRows.push(selectedItem.id);
		});

		if (opRows.length > 0) {

			var box = bootbox.prompt({
				size: "small",
				title: "거절 이유",
				backdrop: "static",
				buttons: {
					cancel: {
						label: '취소',
						className: "btn-default",
					},
					confirm: {
						label: '확인',
						className: "btn-danger",
					}
				},
				animate: false,
				callback: function(result) {
					if (result) {
						var realRes = $.trim(result);
						if (realRes) {

							$.ajax({
								type: "POST",
								contentType: "application/json",
								dataType: "json",
								url: "${rejectUrl}",
								data: JSON.stringify({ items: opRows, reason: realRes }),
								success: function (form) {
									showOperationSuccessMsg();
									grid.dataSource.read();
									$("#grid").data("kendoGrid").dataSource.read();
								},
								error: ajaxOperationError
							});

						} else {
							showConfirmModal("거절 이유가 입력되지 않아, 자동으로 거절이 취소되었습니다.", function(result) { });
						}
					} else {
						showConfirmModal("거절 이유가 입력되지 않아, 자동으로 거절이 취소되었습니다.", function(result) { });
					}
				},
				className: "modal-level-top",
			}).init(function() {
				setTimeout(function(){
					$('.modal-backdrop:last-child').addClass('modal-level-top');
				});
			});

			box.find('.modal-level-top .modal-dialog').addClass("modal-dialog-vertical-center");
			box.find('.modal-level-top .modal-content').addClass("modal-content-border-1");

<c:if test="${not isMobileMode}">

			box.find('.modal-level-top .modal-header').addClass("move-cursor");
			box.find('.modal-level-top .modal-dialog').draggable({ handle: '.modal-header' });

</c:if>

		}
	});
	// / Reject

	// Archive
	$("#archive-btn").click(function(e) {
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
				url: "${archiveUrl}",
				data: JSON.stringify({ items: opRows }),
				success: function (form) {
					showOperationSuccessMsg();
					grid.dataSource.read();
				},
				error: ajaxOperationError
			});
		}
	});
	// / Archive

	// Unarchive
	$("#unarchive-btn").click(function(e) {
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
				url: "${unarchiveUrl}",
				data: JSON.stringify({ items: opRows }),
				success: function (form) {
					showOperationSuccessMsg();
					grid.dataSource.read();
				},
				error: ajaxOperationError
			});
		}
	});
	// / Unarchive

	// Pause
	$("#pause-btn").click(function(e) {
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
				url: "${pauseUrl}",
				data: JSON.stringify({ items: opRows }),
				success: function (form) {
					showOperationSuccessMsg();
					grid.dataSource.read();
				},
				error: ajaxOperationError
			});
		}
	});
	// / Pause

	// Resume
	$("#resume-btn").click(function(e) {
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
				url: "${resumeUrl}",
				data: JSON.stringify({ items: opRows }),
				success: function (form) {
					showOperationSuccessMsg();
					grid.dataSource.read();
				},
				error: ajaxOperationError
			});
		}
	});
	// / Resume

});
</script>

<!-- / Grid button actions  -->



	</div>
	<div class="tab-pane" id="creat-file">



<div class="mb-4">
<kendo:grid name="grid-file" pageable="true" filterable="true" scrollable="true" reorderable="true" resizable="true">
    <kendo:grid-selectable mode="raw"/>
	<kendo:grid-sortable mode="mixed" showIndexes="true"/>
	<kendo:grid-excel fileName="광고소재(소재파일).xlsx" allPages="true" proxyURL="/proxySave"/>
	<kendo:grid-pageable refresh="true" buttonCount="5" pageSize="10" pageSizes="${pageSizesNormal}" />
	<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />

	<kendo:grid-columns>
		<kendo:grid-column title="썸네일" field="thumbFilename" width="160" filterable="false" template="<%= thumbTemplate %>" />
		<kendo:grid-column title="광고 소재" field="creative.name" width="200" template="<%= typeNameTemplate %>" />
		<kendo:grid-column title="파일명" field="srcFilename" width="250" template="<%= fileTemplate %>"  />
		<kendo:grid-column title="해상도" field="resolution" width="100" template="<%= resolTemplate %>" />
		<kendo:grid-column title="파일형식" field="mimeType" width="120" />
		<kendo:grid-column title="재생시간" field="durSecs" width="100" filterable="false" template="<%= durTemplate %>" />
		<kendo:grid-column title="파일크기" field="fileLength" width="100" filterable="false" template="<%= lengthTemplate %>" />
		<kendo:grid-column title="컨텐츠 폴더" field="ctntFolder.name" width="120" filterable="false" />
		<kendo:grid-column title="UUID" field="uuid" width="300" filterable="false" />
		<kendo:grid-column title="등록" field="whoCreationDate" width="120" template="<%= regDateTemplate %>" />
	</kendo:grid-columns>
	<kendo:grid-dataBound>
		<script>
			function grid_dataBound(e) {
				var containerSelector = $('.dur-container').length > 0 ? '.dur-container' : '.len-container';
				$('[data-toggle="tooltip"]').tooltip({
					container: containerSelector
				});
			}
		</script>
	</kendo:grid-dataBound>
	<kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="whoCreationDate" dir="desc"/>
		</kendo:dataSource-sort>
       	<kendo:dataSource-filter>
      		<kendo:dataSource-filterItem field="medium.id" operator="eq" logic="and" value="${sessionScope['currMediumId']}" >
      		</kendo:dataSource-filterItem>
  	    </kendo:dataSource-filter>
		<kendo:dataSource-transport>
			<kendo:dataSource-transport-read url="${createCreatFileUrl}" dataType="json" type="POST" contentType="application/json"/>
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
					<kendo:dataSource-schema-model-field name="whoCreationDate" type="date" />
				</kendo:dataSource-schema-model-fields>
			</kendo:dataSource-schema-model>
		</kendo:dataSource-schema>
	</kendo:dataSource>
</kendo:grid>
</div>



	</div>
</div>


<style>






</style>


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
					<span class="font-weight-light pl-1"><span name="subtitle"></span></span>
				</h5>
				<button type="button" class="close" data-dismiss="modal">×</button>
			</div>

			<!-- Modal body -->
			<div class="modal-body modal-bg-color">
				<div class="form-group col">
					<label class="form-label">
						광고 소재명
						<span class="text-danger">*</span>
					</label>
					<input name="name" type="text" maxlength="100" class="form-control required">
				</div>
				<div class="form-group col">
					<label class="form-label">
						광고주
					</label>
					<select name="advertiser" class="selectpicker bg-white" data-style="btn-default" data-none-selected-text="">
					</select>
				</div>
				<div class="form-group col">
					<label class="form-label">
						범주
					</label>
					<select name="category" class="selectpicker bg-white" data-style="btn-default" data-tick-icon="fa-blank" data-none-selected-text="">
						<option value="" data-icon="fa-regular fa-blank fa-fw mr-1"></option>
						<option value="A" data-icon="fa-regular fa-tv-retro fa-fw mr-1">가전</option>
						<option value="B" data-icon="fa-regular fa-gamepad fa-fw mr-1">게임</option>
						<option value="C" data-icon="fa-regular fa-building-flag fa-fw mr-1">관공서/단체</option>
						<option value="D" data-icon="fa-regular fa-golf-club fa-fw mr-1">관광/레저</option>
						<option value="E" data-icon="fa-regular fa-books fa-fw mr-1">교육/출판</option>
						<option value="F" data-icon="fa-regular fa-coins fa-fw mr-1">금융</option>
						<option value="G" data-icon="fa-regular fa-music fa-fw mr-1">문화/엔터테인먼트</option>
						<option value="H" data-icon="fa-regular fa-hand-holding-seedling fa-fw mr-1">미디어/서비스</option>
						<option value="I" data-icon="fa-regular fa-toothbrush fa-fw mr-1">생활용품</option>
						<option value="J" data-icon="fa-regular fa-truck-fast fa-fw mr-1">유통</option>
						<option value="K" data-icon="fa-regular fa-syringe fa-fw mr-1">제약/의료</option>
						<option value="L" data-icon="fa-regular fa-martini-glass fa-fw mr-1">주류</option>
						<option value="M" data-icon="fa-regular fa-house fa-fw mr-1">주택/가구</option>
						<option value="N" data-icon="fa-regular fa-shirt fa-fw mr-1">패션</option>
						<option value="O" data-icon="fa-regular fa-spray-can fa-fw mr-1">화장품</option>
					</select>
				</div>
				<div class="form-group col">
					<label class="form-label">
						게시 유형
					</label>
					<select name="viewType" class="selectpicker bg-white" data-style="btn-default" data-tick-icon="fa-blank" data-none-selected-text="">
<c:forEach var="item" items="${ViewTypes}">
						<option value="${item.value}">${item.text}</option>
</c:forEach>
					</select>
				</div>
				<div class="form-group col">
					<label class="form-label">
						소재 유형
					</label>
					<select name="type" class="selectpicker bg-white" data-style="btn-default" data-tick-icon="fa-blank" data-none-selected-text="">
						<option value="C" data-icon="fa-regular fa-audio-description fa-fw mr-1">일반 광고</option>
						<option value="F" data-icon="fa-regular fa-house fa-fw text-orange mr-1">대체 광고</option>
					</select>
				</div>
				<div class="form-group col" name="fallback-value-div" style="display: none;">
					<label class="form-label">
						화면의 재생시간 정책
					</label>
					<div>
						<label class="switcher">
							<input type="checkbox" class="switcher-input check-switch-status" name="durPolicyOverriden">
							<span class="switcher-indicator">
								<span class="switcher-yes"></span>
								<span class="switcher-no"></span>
							</span>
							<span class="switcher-label">매체 화면의 재생시간 정책 무시 허용</span>
						</label>
					</div>
				</div>
				<div class="form-group col" name="fallback-value-div" style="display: none;">
					<label class="form-label">
						대체 광고간 가중치
					</label>
					<input name="fbWeight" type="text" class="form-control required" value="1">
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
				<div class="form-group col">
					<label class="form-label">
						광고 소재명
						<span class="text-danger">*</span>
					</label>
					<input name="name" type="text" maxlength="100" class="form-control required">
				</div>
				<div class="form-group col mb-2 text-center">
					<div class="btn-group btn-group-toggle" data-toggle="buttons">
						<label class="btn btn-default btn-sm active">
							<input type="radio" name="adv-input-type-radio" value="S" checked> 등록된 광고주 선택
						</label>
						<label class="btn btn-default btn-sm">
							<input type="radio" name="adv-input-type-radio" value="I"> 새 광고주 함께 등록
						</label>
					</div>
				</div>
				<div class="form-group col" name="advertiser-select-div">
					<label class="form-label">
						광고주
					</label>
					<select name="advertiser" class="selectpicker bg-white" data-style="btn-default" data-none-selected-text="광고주 선택">
					</select>
				</div>
				<div class="form-group col" name="advertiser-input-div" style="display: none;">
					<label class="form-label">
						광고주
						<span class="text-danger">*</span>
					</label>
					<input name="advertiser-name" type="text" maxlength="100" class="form-control required">
				</div>
				<div class="form-group col" name="advertiser-input-div" style="display: none;">
					<label class="form-label">
						광고주 도메인
						<span class="text-danger">*</span>
					</label>
					<input name="advertiser-domain-name" type="text" maxlength="100" class="form-control required">
				</div>
				<div class="form-group col">
					<label class="form-label">
						범주
					</label>
					<select name="category" class="selectpicker bg-white" data-style="btn-default" data-tick-icon="fa-blank" data-none-selected-text="">
						<option value="" data-icon="fa-regular fa-blank fa-fw mr-1"></option>
						<option value="A" data-icon="fa-regular fa-tv-retro fa-fw mr-1">가전</option>
						<option value="B" data-icon="fa-regular fa-gamepad fa-fw mr-1">게임</option>
						<option value="C" data-icon="fa-regular fa-building-flag fa-fw mr-1">관공서/단체</option>
						<option value="D" data-icon="fa-regular fa-golf-club fa-fw mr-1">관광/레저</option>
						<option value="E" data-icon="fa-regular fa-books fa-fw mr-1">교육/출판</option>
						<option value="F" data-icon="fa-regular fa-coins fa-fw mr-1">금융</option>
						<option value="G" data-icon="fa-regular fa-music fa-fw mr-1">문화/엔터테인먼트</option>
						<option value="H" data-icon="fa-regular fa-hand-holding-seedling fa-fw mr-1">미디어/서비스</option>
						<option value="I" data-icon="fa-regular fa-toothbrush fa-fw mr-1">생활용품</option>
						<option value="J" data-icon="fa-regular fa-truck-fast fa-fw mr-1">유통</option>
						<option value="K" data-icon="fa-regular fa-syringe fa-fw mr-1">제약/의료</option>
						<option value="L" data-icon="fa-regular fa-martini-glass fa-fw mr-1">주류</option>
						<option value="M" data-icon="fa-regular fa-house fa-fw mr-1">주택/가구</option>
						<option value="N" data-icon="fa-regular fa-shirt fa-fw mr-1">패션</option>
						<option value="O" data-icon="fa-regular fa-spray-can fa-fw mr-1">화장품</option>
					</select>
				</div>
				<div class="form-group col">
					<label class="form-label">
						게시 유형
					</label>
					<select name="viewType" class="selectpicker bg-white" data-style="btn-default" data-tick-icon="fa-blank" data-none-selected-text="">
<c:forEach var="item" items="${ViewTypes}">
						<option value="${item.value}">${item.text}</option>
</c:forEach>
					</select>
				</div>
				<div class="form-group col">
					<label class="form-label">
						소재 유형
					</label>
					<select name="type" class="selectpicker bg-white" data-style="btn-default" data-tick-icon="fa-blank" data-none-selected-text="">
						<option value="C" data-icon="fa-regular fa-audio-description fa-fw mr-1">일반 광고</option>
						<option value="F" data-icon="fa-regular fa-house fa-fw text-orange mr-1">대체 광고</option>
					</select>
				</div>
				<div class="form-group col" name="fallback-value-div" style="display: none;">
					<label class="form-label">
						화면의 재생시간 정책
					</label>
					<div>
						<label class="switcher">
							<input type="checkbox" class="switcher-input check-switch-status" name="durPolicyOverriden">
							<span class="switcher-indicator">
								<span class="switcher-yes"></span>
								<span class="switcher-no"></span>
							</span>
							<span class="switcher-label">매체 화면의 재생시간 정책 무시 허용</span>
						</label>
					</div>
				</div>
				<div class="form-group col" name="fallback-value-div" style="display: none;">
					<label class="form-label">
						대체 광고간 가중치
					</label>
					<input name="fbWeight" type="text" class="form-control required" value="1">
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

var advertisers = [];

function validateForm1Type() {

	if ($("#form-1 select[name='type']").val() == "C") {
		$("#form-1 div[name='fallback-value-div']").hide();
	} else {
		$("#form-1 div[name='fallback-value-div']").show();

		$("#form-1 input[name='durPolicyOverriden']").prop("checked", false);
		$("#form-1 input[name='fbWeight']").val("1");
	}
}


function validateForm2Type() {

	if ($("#form-2 select[name='type']").val() == "C") {
		$("#form-2 div[name='fallback-value-div']").hide();
	} else {
		$("#form-2 div[name='fallback-value-div']").show();

		$("#form-2 input[name='durPolicyOverriden']").prop("checked", false);
		$("#form-2 input[name='fbWeight']").val("1");
	}
}


function initForm1(subtitle) {

	$("#formRoot").html(kendo.template($("#template-1").html()));

	$("#form-1 select[name='advertiser']").selectpicker('render');
	$("#form-1 select[name='type']").selectpicker('render');
	$("#form-1 select[name='category']").selectpicker('render');
	$("#form-1 select[name='viewType']").selectpicker('render');

	// 범주 셀렉터의 초기값이 없도록
	bootstrapSelectVal($("#form-1 select[name='category']"), "");

	$("#form-1 select[name='type']").on("change.bs.select", function(e){
		validateForm1Type();
	});


	advertisers.forEach(function (item, index) {
		$("#form-1 select[name='advertiser']").append("<option value='" + item.value + "'>" + item.text + "</option>");
	});
	$("#form-1 select[name='advertiser']").selectpicker('refresh');


	$("#form-1 span[name='subtitle']").text(subtitle ? subtitle : "추가");

	$("#form-1").validate({
		rules: {
			name: {
				minlength: 2,
			},
			fbWeight: {
				digits: true,
				min: 1,
			},
		}
	});
}


function saveForm1() {

	var advertiser = $("#form-1 select[name='advertiser']").val();

	if ($("#form-1").valid() && advertiser != "-1") {
    	var data = {
    		id: Number($("#form-1").attr("rowid")),
    		name: $.trim($("#form-1 input[name='name']").val()),
    		advertiser: Number($("#form-1 select[name='advertiser']").val()),
    		type: $("#form-1 select[name='type']").val(),
       		viewType: $("#form-1 select[name='viewType']").val(),
    		durPolicyOverriden: $("#form-1 input[name='durPolicyOverriden']").is(':checked'),
    		fbWeight: Number($.trim($("#form-1 input[name='fbWeight']").val())),
    		category: $("#form-1 select[name='category']").val(),
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

	// 최신의 광고주 정보부터 획득하고 시작
	$.ajax({
		type: "POST",
		contentType: "application/json",
		dataType: "json",
		url: "${readAdvUrl}",
		data: JSON.stringify({ }),
		success: function (data, status) {
			advertisers = eval(data);

			initForm1("변경");


			var dataItem = $("#grid").data("kendoGrid").dataSource.get(id);

			$("#form-1").attr("rowid", dataItem.id);
			$("#form-1").attr("url", "${updateUrl}");

			bootstrapSelectVal($("#form-1 select[name='advertiser']"), dataItem.advertiser.id);
			bootstrapSelectVal($("#form-1 select[name='type']"), dataItem.type);
			bootstrapSelectVal($("#form-1 select[name='category']"), dataItem.category);
			bootstrapSelectVal($("#form-1 select[name='viewType']"), dataItem.viewTypeCode);

			validateForm1Type();


			$("#form-1 input[name='name']").val(dataItem.name);
			$("#form-1 input[name='fbWeight']").val(dataItem.fbWeight);

			$("#form-1 input[name='durPolicyOverriden']").prop("checked", dataItem.durPolicyOverriden);

			bootstrapSelectDisabled($("#form-1 select[name='advertiser']"), true);

			// 기등록된 소재 파일을 가지는 소재의 게시 유형은 변경 불가능
			if (dataItem.fileResolutions != "") {
				bootstrapSelectDisabled($("#form-1 select[name='viewType']"), true);
			}


			$('#form-modal-1 .modal-dialog').draggable({ handle: '.modal-header' });
			$("#form-modal-1").modal();
		},
		error: ajaxReadError
	});
}


function initForm2(subtitle) {

	$("#formRoot").html(kendo.template($("#template-2").html()));

	$("#form-2 select[name='type']").selectpicker('render');
	$("#form-2 select[name='category']").selectpicker('render');
	$("#form-2 select[name='viewType']").selectpicker('render');

	// 범주 셀렉터의 초기값이 없도록
	bootstrapSelectVal($("#form-2 select[name='category']"), "");

	$("#form-2 select[name='type']").on("change.bs.select", function(e) {
		validateForm2Type();
	});

	$("#form-2 input[name='adv-input-type-radio']").change(function(){
		if ($("#form-2 input[name='adv-input-type-radio']:checked").val() == 'S') {
			$("#form-2 div[name='advertiser-select-div']").show();
			$("#form-2 div[name='advertiser-input-div']").hide();
		} else {
			$("#form-2 div[name='advertiser-select-div']").hide();
			$("#form-2 div[name='advertiser-input-div']").show();
		}
	});


	// 광고주 셀렉터 지연 구성
	advertisers.forEach(function (item, index) {
		$("#form-2 select[name='advertiser']").append("<option value='" + item.value + "'>" + item.text + "</option>");
	});
	$("#form-2 select[name='advertiser']").selectpicker('render');
	// 광고주 셀렉터의 초기값이 없도록
	bootstrapSelectVal($("#form-2 select[name='advertiser']"), "");


	$("#form-2 span[name='subtitle']").text(subtitle ? subtitle : "추가");

	$("#form-2").validate({
		rules: {
			name: {
				minlength: 2,
			},
			fbWeight: {
				digits: true,
				min: 1,
			},
		}
	});
}


function saveForm2() {

	var adv = null;
	var advName = "";
	var advDomainName = "";

	if ($("#form-2 input[name='adv-input-type-radio']:checked").val() == 'S') {
		// 선택
		adv = Number($("#form-2 select[name='advertiser']").val());
	} else {
		// 직접 입력
		adv = -1;
		advName= $.trim($("#form-2 input[name='advertiser-name']").val());
		advDomainName= $.trim($("#form-2 input[name='advertiser-domain-name']").val());
	}

	if ($("#form-2").valid() && adv != null) {
    	var data = {
       		id: Number($("#form-2").attr("rowid")),
       		name: $.trim($("#form-2 input[name='name']").val()),
       		advertiser: adv,
       		advName: advName,
       		advDomainName: advDomainName,
       		type: $("#form-2 select[name='type']").val(),
       		viewType: $("#form-2 select[name='viewType']").val(),
       		durPolicyOverriden: $("#form-2 input[name='durPolicyOverriden']").is(':checked'),
       		fbWeight: Number($.trim($("#form-2 input[name='fbWeight']").val())),
    		category: $("#form-2 select[name='category']").val(),
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


function navToCreat(advId, id) {
	var path = "/adc/creative/files/" + advId + "/" + id;
	location.href = path;
}


function navToAdv(advId) {
	var path = "/adc/creative/creatives/" + advId;
	location.href = path;
}


function copyToMedium(shortName) {

	var grid = $("#grid").data("kendoGrid");
	var rows = grid.select();

	var opRows = [];

	rows.each(function(index, row) {
		var selectedItem = grid.dataItem(row);
		opRows.push(selectedItem.id);
	});

	if (opRows.length > 0) {
		showWaitModal();

		$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: "${copyUrl}",
			data: JSON.stringify({ items: opRows, mediumID: shortName }),
			success: function (form) {
				showOperationSuccessMsg();
				hideWaitModal();
			},
			error: function(e) {
				hideWaitModal();
				ajaxOperationError(e);
			}
		});
	}
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

<!--  / Scripts -->


<!-- / Page body -->





<!-- Functional tags -->

<func:cmmValidate />


<!-- Closing tags -->

<common:base />
<common:pageClosing />
