<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/org/currmedium/read" var="readUrl" />
<c:url value="/org/currmedium/update" var="updateUrl" />
<c:url value="/org/currmedium/updateTime" var="updateTimeUrl" />

<c:url value="/org/currmedium/readSiteLocs" var="readSiteLocUrl" />


<!-- Opening tags -->

<common:mediumPageOpeningNaverMap />


<!-- Page title -->

<h4 class="pt-1 pb-3 mb-3">
	<span class="mr-1 ${sessionScope['loginUser'].icon}"></span>
	${pageTitle}
	<span class='small text-muted pl-3'>${subtitle}</span>
</h4>





<!-- Page body -->


<!-- Page scripts  -->

<script src="/resources/vendor/lib/clipboard/clipboard.js"></script>


<!--  Forms -->

<ul class="nav nav-tabs tabs-alt container-p-x container-m--x mb-4" id="curr-medium-tabs">
	<li class="nav-item">
		<a class="nav-link active" data-toggle="tab" href="#basic-info">
			<i class="mr-1 fa-light fa-icons"></i>
			일반
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" data-toggle="tab" href="#ad-selection">
			<i class="mr-1 fa-light fa-audio-description"></i>
			광고
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" data-toggle="tab" href="#site-map" id="site-map-tab">
			<i class="mr-1 fa-light fa-map"></i>
			지도
		</a>
	</li>
</ul>

<form id="form-1">
<div class="tab-content">
	<div class="tab-pane active" id="basic-info">
		<div class="card">
			<div class="row no-gutters row-bordered row-border-light">
				<div class="col-sm-12 col-md-3">
					<div class="d-flex align-items-center justify-content-center py-3">
						<span class="fa-thin fa-screen-users fa-2x text-gray"></span>
						<div class="ml-3">
							<div class="text-muted small">화면수 (활성/전체)</div>
							<div class="text-large">
								<span>${activeScrCnt}</span>
								<span class="text-muted small">/</span>
								<span class="text-muted">${availScrCnt}</span>
							</div>
						</div>
					</div>
				</div>
				<div class="col-sm-12 col-md-3">
					<div class="d-flex align-items-center justify-content-center py-3">
						<span class="fa-thin fa-map-pin fa-2x text-gray"></span>
						<div class="ml-3">
							<div class="text-muted small">사이트 (활성/전체)</div>
							<div class="text-large">
								<span>${activeSiteCnt}</span>
								<span class="text-muted small">/</span>
								<span class="text-muted">${availSiteCnt}</span>
							</div>
						</div>
					</div>
				</div>
				<div class="col-sm-12 col-md-6">
					<div class="d-flex align-items-center justify-content-center py-3">
						<span class="fa-thin fa-plug fa-2x text-gray"></span>
						<div class="ml-3">
							<div class="text-muted small">API 키</div>
							<div>
								<span class="d-flex align-items-center">
									<span style="font-size: 120%;" id="api-key-span">${apiKey}</span>
									<span class="px-1"></span>
									<button type='button' onclick='copyToClipboard()' title="클립보드로 복사"
											class='btn icon-btn btn-xs btn-outline-secondary borderless clipboard-btn' 
											data-clipboard-action="copy" data-clipboard-target="#api-key-span"> 
										<span class='fas fa-copy'></span>
									</button>
								</span>
							</div>
						</div>
					</div>
				</div>
			</div>
			<hr class="m-0">
			<div class="card-body">
				<div class="pb-2">
			    	<div class="clearfix">
			    		<div class="float-left">
							운영 시간
							<span class="small text-muted pl-3">매체의 운영 시간을 요일 및 시간별 한 시간 단위로 지정합니다.</span>
			    		</div>
			    		<div class="float-right">
							<button type='button' onclick='editBizTime()' class='btn icon-btn btn-sm btn-outline-success'> 
								<span class='fas fa-pencil-alt'></span>
							</button>
			    		</div>
			    	</div>
				</div>
				<!-- 
				<div class="form-row">
					<div class="col-4">
						<div class="form-group col mb-0">
							<label class="form-label">
								1주일 총 운영시간
							</label>
							<div class="input-group">
								<input type="text" class="form-control" value="${bizHours}" readonly>
								<div class="input-group-append">
									<span class="input-group-text">시간</span>
								</div>
							</div>
						</div>
					</div>
				</div>
				-->
				<div class="form-row">
					<div class="mt-3 ml-4">

						<table id="base-time-table">
							<tr>
								<th></th>
								<th><pre class="my-0">AM</pre></th>
								<th><pre class="my-0">1</pre></th>
								<th><pre class="my-0">2</pre></th>
								<th><pre class="my-0">3</pre></th>
								<th><pre class="my-0">4</pre></th>
								<th><pre class="my-0">5</pre></th>
								<th><pre class="my-0">6</pre></th>
								<th><pre class="my-0">7</pre></th>
								<th><pre class="my-0">8</pre></th>
								<th><pre class="my-0">9</pre></th>
								<th><pre class="my-0">10</pre></th>
								<th><pre class="my-0">11</pre></th>
								<th><pre class="my-0">PM</pre></th>
								<th><pre class="my-0">1</pre></th>
								<th><pre class="my-0">2</pre></th>
								<th><pre class="my-0">3</pre></th>
								<th><pre class="my-0">4</pre></th>
								<th><pre class="my-0">5</pre></th>
								<th><pre class="my-0">6</pre></th>
								<th><pre class="my-0">7</pre></th>
								<th><pre class="my-0">8</pre></th>
								<th><pre class="my-0">9</pre></th>
								<th><pre class="my-0">10</pre></th>
								<th><pre class="my-0">11</pre></th>
							</tr>
							<tr>
								<th class="pr-2">월<small>요일</small></th>
								<td id="btt0"></td>
								<td id="btt1"></td>
								<td id="btt2"></td>
								<td id="btt3"></td>
								<td id="btt4"></td>
								<td id="btt5"></td>
								<td id="btt6"></td>
								<td id="btt7"></td>
								<td id="btt8"></td>
								<td id="btt9"></td>
								<td id="btt10"></td>
								<td id="btt11"></td>
								<td id="btt12"></td>
								<td id="btt13"></td>
								<td id="btt14"></td>
								<td id="btt15"></td>
								<td id="btt16"></td>
								<td id="btt17"></td>
								<td id="btt18"></td>
								<td id="btt19"></td>
								<td id="btt20"></td>
								<td id="btt21"></td>
								<td id="btt22"></td>
								<td id="btt23"></td>
							</tr>
							<tr>
								<th>화<small>요일</small></th>
								<td id="btt24"></td>
								<td id="btt25"></td>
								<td id="btt26"></td>
								<td id="btt27"></td>
								<td id="btt28"></td>
								<td id="btt29"></td>
								<td id="btt30"></td>
								<td id="btt31"></td>
								<td id="btt32"></td>
								<td id="btt33"></td>
								<td id="btt34"></td>
								<td id="btt35"></td>
								<td id="btt36"></td>
								<td id="btt37"></td>
								<td id="btt38"></td>
								<td id="btt39"></td>
								<td id="btt40"></td>
								<td id="btt41"></td>
								<td id="btt42"></td>
								<td id="btt43"></td>
								<td id="btt44"></td>
								<td id="btt45"></td>
								<td id="btt46"></td>
								<td id="btt47"></td>
							</tr>
							<tr>
								<th>수<small>요일</small></th>
								<td id="btt48"></td>
								<td id="btt49"></td>
								<td id="btt50"></td>
								<td id="btt51"></td>
								<td id="btt52"></td>
								<td id="btt53"></td>
								<td id="btt54"></td>
								<td id="btt55"></td>
								<td id="btt56"></td>
								<td id="btt57"></td>
								<td id="btt58"></td>
								<td id="btt59"></td>
								<td id="btt60"></td>
								<td id="btt61"></td>
								<td id="btt62"></td>
								<td id="btt63"></td>
								<td id="btt64"></td>
								<td id="btt65"></td>
								<td id="btt66"></td>
								<td id="btt67"></td>
								<td id="btt68"></td>
								<td id="btt69"></td>
								<td id="btt70"></td>
								<td id="btt71"></td>
							</tr>
							<tr>
								<th>목<small>요일</small></th>
								<td id="btt72"></td>
								<td id="btt73"></td>
								<td id="btt74"></td>
								<td id="btt75"></td>
								<td id="btt76"></td>
								<td id="btt77"></td>
								<td id="btt78"></td>
								<td id="btt79"></td>
								<td id="btt80"></td>
								<td id="btt81"></td>
								<td id="btt82"></td>
								<td id="btt83"></td>
								<td id="btt84"></td>
								<td id="btt85"></td>
								<td id="btt86"></td>
								<td id="btt87"></td>
								<td id="btt88"></td>
								<td id="btt89"></td>
								<td id="btt90"></td>
								<td id="btt91"></td>
								<td id="btt92"></td>
								<td id="btt93"></td>
								<td id="btt94"></td>
								<td id="btt95"></td>
							</tr>
							<tr>
								<th>금<small>요일</small></th>
								<td id="btt96"></td>
								<td id="btt97"></td>
								<td id="btt98"></td>
								<td id="btt99"></td>
								<td id="btt100"></td>
								<td id="btt101"></td>
								<td id="btt102"></td>
								<td id="btt103"></td>
								<td id="btt104"></td>
								<td id="btt105"></td>
								<td id="btt106"></td>
								<td id="btt107"></td>
								<td id="btt108"></td>
								<td id="btt109"></td>
								<td id="btt110"></td>
								<td id="btt111"></td>
								<td id="btt112"></td>
								<td id="btt113"></td>
								<td id="btt114"></td>
								<td id="btt115"></td>
								<td id="btt116"></td>
								<td id="btt117"></td>
								<td id="btt118"></td>
								<td id="btt119"></td>
							</tr>
							<tr>
								<th>토<small>요일</small></th>
								<td id="btt120"></td>
								<td id="btt121"></td>
								<td id="btt122"></td>
								<td id="btt123"></td>
								<td id="btt124"></td>
								<td id="btt125"></td>
								<td id="btt126"></td>
								<td id="btt127"></td>
								<td id="btt128"></td>
								<td id="btt129"></td>
								<td id="btt130"></td>
								<td id="btt131"></td>
								<td id="btt132"></td>
								<td id="btt133"></td>
								<td id="btt134"></td>
								<td id="btt135"></td>
								<td id="btt136"></td>
								<td id="btt137"></td>
								<td id="btt138"></td>
								<td id="btt139"></td>
								<td id="btt140"></td>
								<td id="btt141"></td>
								<td id="btt142"></td>
								<td id="btt143"></td>
							</tr>
							<tr>
								<th>일<small>요일</small></th>
								<td id="btt144"></td>
								<td id="btt145"></td>
								<td id="btt146"></td>
								<td id="btt147"></td>
								<td id="btt148"></td>
								<td id="btt149"></td>
								<td id="btt150"></td>
								<td id="btt151"></td>
								<td id="btt152"></td>
								<td id="btt153"></td>
								<td id="btt154"></td>
								<td id="btt155"></td>
								<td id="btt156"></td>
								<td id="btt157"></td>
								<td id="btt158"></td>
								<td id="btt159"></td>
								<td id="btt160"></td>
								<td id="btt161"></td>
								<td id="btt162"></td>
								<td id="btt163"></td>
								<td id="btt164"></td>
								<td id="btt165"></td>
								<td id="btt166"></td>
								<td id="btt167"></td>
							</tr>
						</table>
					
					</div>
				</div>
			</div>
			<hr class="m-0" />
			<div class="card-body">
				<div class="pb-3">
		    		<div class="float-left">
						매체 옵션
						<span class="small text-muted pl-3">매체에서 사용중인 옵션의 이름 및 고유ID를 지정합니다. 형식은 {고유ID},{이름}이며, 옵션 구분은 '|'로 합니다. 예) tester,테스터|abc,옵션명</span>
		    		</div>
				</div>
				<div class="pt-3">
					<input name="opts" type="text" class="form-control col-10">
				</div>
			</div>
			<hr class="m-0" />
			<div class="card-body">
				<div class="pb-3">
		    		<div class="float-left">
						인벤 자료 등록 요청 시 매체의 기본값
						<span class="small text-muted pl-3">매체 인벤 자료 등록(인벤 일괄 업로드, 인벤 API 요청)할 때, 따로 명시하지 않아도 이 매체에 대해 시스템에서 인지하는 기본 값입니다. 문자열은 json 형식이며, 정확한 항목명은 다음의 도움말 링크 문서를 참조하십시오.</span>
		    		</div>
		    		<div class="float-right">
						<button type='button' onclick='downloadGuideDoc()' class='btn icon-btn btn-sm btn-outline-secondary'> 
							<span class='fas fa-download'></span>
						</button>
		    		</div>
				</div>
				<div class="pt-3">
					<input name="invenValues" type="text" class="form-control col-10">
				</div>
			</div>
		</div>
	</div>
	<div class="tab-pane" id="ad-selection">
		<div class="card">
			<div class="row no-gutters row-bordered row-border-light">
				<div class="col-sm-6">
					<div class="card-body">
						<div class="form-group mb-0">
							<label class="form-label">
								보장 노출량 대비 목표 노출량 기본 비율
								<span class="small text-muted pl-3">광고에 보장 노출량이 설정되었을 경우, 시스템에서 내부적으로 설정하는 목표 노출량의 기본 비율을 지정합니다. 100%로 설정하면, 목표 노출량과 보장 노출량을 동일하게 한다는 것입니다.</span>
							</label>
							<div class="form-row px-1">
								<div class="col">
									<select name="sysValuePctType" class="selectpicker bg-white" data-style="btn-default" data-tick-icon="fa-blank" data-none-selected-text="">
										<option value="0" data-icon="fa-regular fa-circle-xmark fa-fw mr-1">기본 비율 설정 안함</option>
										<option value="1" data-icon="fa-regular fa-input-numeric fa-fw mr-1">값 지정</option>
									</select>
								</div>
								<div name="sys-value-pct-value-div" class="col" style="display: none;">
									<div class="input-group">
										<input name="sysValuePct" type="text" value="" class="form-control" >
										<div class="input-group-append">
											<span class="input-group-text">%</span>
										</div>
									</div>
								</div>
							</div>

						</div>
					</div>
				</div>
				<div class="col-sm-6">
					<div class="card-body">
						<div class="form-group mb-0">
							<label class="form-label">
								동일 광고주 광고 송출 금지 시간
								<span class="small text-muted pl-3">현재의 광고가 노출된 후, 이후에 현재 광고의 광고주 광고(현재 광고 포함)가 곧이어 노출되지 않게 하기 위해 의도적으로 다른 광고주의 광고가 노출되도록 보장하는 초단위 시간입니다. 최근 광고의 마지막 노출 종료 시점부터 다음 동일한 광고의 선택 시점까지의 시간 차이입니다. 동일 이름의 <span class="text-dark">캠페인 속성</span>이 우선 적용됩니다.</span>
							</label>
							<div class="form-row px-1">
								<div class="col">
									<select name="advFreqCapType" class="selectpicker bg-white" data-style="btn-default" data-tick-icon="fa-blank" data-none-selected-text="">
										<option value="0" data-icon="fa-regular fa-circle-xmark fa-fw mr-1">금지 시간 설정 안함</option>
										<option value="1" data-icon="fa-regular fa-input-numeric fa-fw mr-1">값 지정</option>
									</select>
								</div>
								<div name="adv-freq-cap-value-div" class="col" style="display: none;">
									<div class="input-group">
										<input name="advFreqCap" type="text" value="" class="form-control" >
										<div class="input-group-append">
											<span class="input-group-text">초</span>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="col-sm-6">
					<div class="card-body">
						<div class="form-group mb-0">
							<label class="form-label">
								동일 광고 송출 금지 시간
								<span class="small text-muted pl-3">현재의 광고가 노출된 후, 이후에 동일한 광고가 곧이어 노출되지 않게 하기 위해 의도적으로 다른 광고가 노출되도록 보장하는 초단위 시간입니다. 최근 광고의 마지막 노출 종료 시점부터 다음 동일한 광고의 선택 시점까지의 시간 차이입니다. 동일 이름의 <span class="text-dark">광고 속성</span>이 우선 적용됩니다.</span>
							</label>
							<div class="form-row px-1">
								<div class="col">
									<select name="adFreqCapType" class="selectpicker bg-white" data-style="btn-default" data-tick-icon="fa-blank" data-none-selected-text="">
										<option value="0" data-icon="fa-regular fa-circle-xmark fa-fw mr-1">금지 시간 설정 안함</option>
										<option value="1" data-icon="fa-regular fa-input-numeric fa-fw mr-1">값 지정</option>
									</select>
								</div>
								<div name="ad-freq-cap-value-div" class="col" style="display: none;">
									<div class="input-group">
										<input name="adFreqCap" type="text" value="" class="form-control" >
										<div class="input-group-append">
											<span class="input-group-text">초</span>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="col-sm-6">
					<div class="card-body">
						<div class="form-group mb-0">
							<label class="form-label">
								동일 범주 광고 송출 금지 시간
								<span class="small text-muted pl-3">현재의 광고가 노출된 후, 이후에 동일한 범주의 광고가 곧이어 노출되지 않게 하기 위해 의도적으로 다른 범주의 광고가 노출되도록 보장하는 초단위 시간입니다. 최근 광고의 마지막 노출 종료 시점부터 다음 동일한 범주 광고의 선택 시점까지의 시간 차이입니다.</span>
							</label>
							<div class="form-row px-1">
								<div class="col">
									<select name="catFreqCapType" class="selectpicker bg-white" data-style="btn-default" data-tick-icon="fa-blank" data-none-selected-text="">
										<option value="0" data-icon="fa-regular fa-circle-xmark fa-fw mr-1">금지 시간 설정 안함</option>
										<option value="1" data-icon="fa-regular fa-input-numeric fa-fw mr-1">값 지정</option>
									</select>
								</div>
								<div name="cat-freq-cap-value-div" class="col" style="display: none;">
									<div class="input-group">
										<input name="catFreqCap" type="text" value="" class="form-control" >
										<div class="input-group-append">
											<span class="input-group-text">초</span>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="col-sm-6">
					<div class="card-body">
						<div class="form-group mb-0">
							<label class="form-label">
								광고의 화면당 하루 노출한도
								<span class="small text-muted pl-3">개별 화면당 하루동안 노출될 수 있는 최대 횟수 한도를 설정합니다. 동일 이름의 <span class="text-dark">광고 속성</span>이 우선 적용됩니다.</span>
							</label>
							<div class="form-row px-1">
								<div class="col">
									<select name="adDailyScrCapType" class="selectpicker bg-white" data-style="btn-default" data-tick-icon="fa-blank" data-none-selected-text="">
										<option value="0" data-icon="fa-regular fa-circle-xmark fa-fw mr-1">노출 한도 설정 안함</option>
										<option value="1" data-icon="fa-regular fa-input-numeric fa-fw mr-1">값 지정</option>
									</select>
								</div>
								<div name="ad-daily-scr-cap-value-div" class="col" style="display: none;">
									<div class="input-group">
										<input name="adDailyScrCap" type="text" value="" class="form-control" >
										<div class="input-group-append">
											<span class="input-group-text">회</span>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="col-sm-6">
					<div class="card-body">
						<div class="form-group mb-0">
							<label class="form-label">
								시간당 광고 노출 계획
								<span class="small text-muted pl-3">광고 예산이나 보장 노출량/목표 노출량이 설정되지 않은 광고에 대한 노출 빈도를, 한 시간당 노출될 횟수로 설정합니다.
							</label>
							<div class="form-row px-1">
								<div class="col">
									<select name="impPlanPerHourType" class="selectpicker bg-white" data-style="btn-default" data-tick-icon="fa-blank" data-none-selected-text="" data-container="body">
										<option value="0" data-icon="fa-regular fa-circle-xmark fa-fw mr-1">기본 값(6회, 10분 주기) 적용</option>
										<option value="1" data-icon="fa-regular fa-list-radio fa-fw mr-1">값 선택</option>
									</select>
								</div>
								<div name="imp-plan-per-hour-value-div" class="col" style="display: none;">
									<select name="impPlanPerHour" class="selectpicker bg-white" data-style="btn-default" data-none-selected-text="" data-container="body">
										<option value="1">1 회 (60분 주기)</option>
										<option value="2">2 회 (30분 주기)</option>
										<option value="3">3 회 (20분 주기)</option>
										<option value="4">4 회 (15분 주기)</option>
										<option value="5">5 회 (12분 주기)</option>
										<option value="6">6 회 (10분 주기)</option>
										<option value="7">7 회 (9분 주기)</option>
										<option value="8">8 회 (8분 주기)</option>
										<option value="9">9 회 (7분 주기)</option>
										<option value="10">10 회 (6분 주기)</option>
										<option value="12">12 회 (5분 주기)</option>
										<option value="15">15 회 (4분 주기)</option>
										<option value="20">20 회 (3분 주기)</option>
										<option value="30">30 회 (2분 주기)</option>
										<option value="60">60 회 (1분 주기)</option>
									</select>
								</div>
							</div>
							
						</div>
					</div>
				</div>
				<div class="col-sm-6">
					<div class="card-body">
						<div class="form-group mb-0">
							<label class="form-label">
								매체의 화면 수
								<span class="small text-muted pl-3">매체의 화면 수가 유동적으로 자주 변경되는 경우나, 화면 수가 많아 한 값으로 특정하기 힘든 경우 우선적으로 적용되는 값입니다. 이 값이 제공되지 않으면 필요 시점에 활성화 화면 수를 직접 확인하여 이용하게 됩니다.</span>
							</label>
							<div class="form-row px-1">
								<div class="col">
									<select name="activeScrCountType" class="selectpicker bg-white" data-style="btn-default" data-tick-icon="fa-blank" data-none-selected-text="" data-container="body">
										<option value="0" data-icon="fa-regular fa-circle-xmark fa-fw mr-1">화면 수 지정 안함</option>
										<option value="1" data-icon="fa-regular fa-input-numeric fa-fw mr-1">값 지정</option>
									</select>
								</div>
								<div name="active-scr-count-value-div" class="col" style="display: none;">
									<div class="input-group">
										<input name="activeScrCount" type="text" value="" class="form-control" >
										<div class="input-group-append">
											<span class="input-group-text">기</span>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="tab-pane p-0 mb-3" id="site-map">
		<div id="site-map-container" style="border: solid 1px #e4e4e4; background-color: white; height: 700px; width: 100%;">
		</div>
	</div>
</div>
</form>

<div class="text-right mt-3">
	<button id="save-btn" type="button" class="btn btn-primary">저장</button>
</div>


<!--  Root form container -->
<div id="formRoot"></div>


<script id="template-2" type="text/x-kendo-template">

<div class="modal fade" data-backdrop="static" id="form-modal-2">
	<div class="modal-dialog modal-lg">
		<form class="modal-content" id="form-2" rowid="-1" url="${createUrl}">
      
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
				<div class="form-row">
					<div class="form-group col-5">
						<label class="form-label">
							매체명
						</label>
						<input name="name" type="text" maxlength="100" class="form-control" readonly>
					</div>
					<div class="form-group col-4">
						<label class="form-label">
							매체의 단축명
						</label>
						<input name="shortName" type="text" maxlength="50" class="form-control" readonly>
					</div>
					<div class="form-group col-3">
						<label class="form-label">
							1주일 총 운영시간
						</label>
						<div class="input-group">
							<input name="bizHrs" type="text" class="form-control" value="0" readonly>
							<div class="input-group-append">
								<span class="input-group-text">시간</span>
							</div>
						</div>
					</div>
				</div>
				<div class="form-row">
					<div class="mx-auto">

<table id="time-table">
	<tr>
		<th></th>
		<th><pre class="my-0">AM</pre></th>
		<th><pre class="my-0">1</pre></th>
		<th><pre class="my-0">2</pre></th>
		<th><pre class="my-0">3</pre></th>
		<th><pre class="my-0">4</pre></th>
		<th><pre class="my-0">5</pre></th>
		<th><pre class="my-0">6</pre></th>
		<th><pre class="my-0">7</pre></th>
		<th><pre class="my-0">8</pre></th>
		<th><pre class="my-0">9</pre></th>
		<th><pre class="my-0">10</pre></th>
		<th><pre class="my-0">11</pre></th>
		<th><pre class="my-0">PM</pre></th>
		<th><pre class="my-0">1</pre></th>
		<th><pre class="my-0">2</pre></th>
		<th><pre class="my-0">3</pre></th>
		<th><pre class="my-0">4</pre></th>
		<th><pre class="my-0">5</pre></th>
		<th><pre class="my-0">6</pre></th>
		<th><pre class="my-0">7</pre></th>
		<th><pre class="my-0">8</pre></th>
		<th><pre class="my-0">9</pre></th>
		<th><pre class="my-0">10</pre></th>
		<th><pre class="my-0">11</pre></th>
	</tr>
	<tr>
		<th class="pr-2">월<small>요일</small></th>
		<td id="tt0"></td>
		<td id="tt1"></td>
		<td id="tt2"></td>
		<td id="tt3"></td>
		<td id="tt4"></td>
		<td id="tt5"></td>
		<td id="tt6"></td>
		<td id="tt7"></td>
		<td id="tt8"></td>
		<td id="tt9"></td>
		<td id="tt10"></td>
		<td id="tt11"></td>
		<td id="tt12"></td>
		<td id="tt13"></td>
		<td id="tt14"></td>
		<td id="tt15"></td>
		<td id="tt16"></td>
		<td id="tt17"></td>
		<td id="tt18"></td>
		<td id="tt19"></td>
		<td id="tt20"></td>
		<td id="tt21"></td>
		<td id="tt22"></td>
		<td id="tt23"></td>
	</tr>
	<tr>
		<th>화<small>요일</small></th>
		<td id="tt24"></td>
		<td id="tt25"></td>
		<td id="tt26"></td>
		<td id="tt27"></td>
		<td id="tt28"></td>
		<td id="tt29"></td>
		<td id="tt30"></td>
		<td id="tt31"></td>
		<td id="tt32"></td>
		<td id="tt33"></td>
		<td id="tt34"></td>
		<td id="tt35"></td>
		<td id="tt36"></td>
		<td id="tt37"></td>
		<td id="tt38"></td>
		<td id="tt39"></td>
		<td id="tt40"></td>
		<td id="tt41"></td>
		<td id="tt42"></td>
		<td id="tt43"></td>
		<td id="tt44"></td>
		<td id="tt45"></td>
		<td id="tt46"></td>
		<td id="tt47"></td>
	</tr>
	<tr>
		<th>수<small>요일</small></th>
		<td id="tt48"></td>
		<td id="tt49"></td>
		<td id="tt50"></td>
		<td id="tt51"></td>
		<td id="tt52"></td>
		<td id="tt53"></td>
		<td id="tt54"></td>
		<td id="tt55"></td>
		<td id="tt56"></td>
		<td id="tt57"></td>
		<td id="tt58"></td>
		<td id="tt59"></td>
		<td id="tt60"></td>
		<td id="tt61"></td>
		<td id="tt62"></td>
		<td id="tt63"></td>
		<td id="tt64"></td>
		<td id="tt65"></td>
		<td id="tt66"></td>
		<td id="tt67"></td>
		<td id="tt68"></td>
		<td id="tt69"></td>
		<td id="tt70"></td>
		<td id="tt71"></td>
	</tr>
	<tr>
		<th>목<small>요일</small></th>
		<td id="tt72"></td>
		<td id="tt73"></td>
		<td id="tt74"></td>
		<td id="tt75"></td>
		<td id="tt76"></td>
		<td id="tt77"></td>
		<td id="tt78"></td>
		<td id="tt79"></td>
		<td id="tt80"></td>
		<td id="tt81"></td>
		<td id="tt82"></td>
		<td id="tt83"></td>
		<td id="tt84"></td>
		<td id="tt85"></td>
		<td id="tt86"></td>
		<td id="tt87"></td>
		<td id="tt88"></td>
		<td id="tt89"></td>
		<td id="tt90"></td>
		<td id="tt91"></td>
		<td id="tt92"></td>
		<td id="tt93"></td>
		<td id="tt94"></td>
		<td id="tt95"></td>
	</tr>
	<tr>
		<th>금<small>요일</small></th>
		<td id="tt96"></td>
		<td id="tt97"></td>
		<td id="tt98"></td>
		<td id="tt99"></td>
		<td id="tt100"></td>
		<td id="tt101"></td>
		<td id="tt102"></td>
		<td id="tt103"></td>
		<td id="tt104"></td>
		<td id="tt105"></td>
		<td id="tt106"></td>
		<td id="tt107"></td>
		<td id="tt108"></td>
		<td id="tt109"></td>
		<td id="tt110"></td>
		<td id="tt111"></td>
		<td id="tt112"></td>
		<td id="tt113"></td>
		<td id="tt114"></td>
		<td id="tt115"></td>
		<td id="tt116"></td>
		<td id="tt117"></td>
		<td id="tt118"></td>
		<td id="tt119"></td>
	</tr>
	<tr>
		<th>토<small>요일</small></th>
		<td id="tt120"></td>
		<td id="tt121"></td>
		<td id="tt122"></td>
		<td id="tt123"></td>
		<td id="tt124"></td>
		<td id="tt125"></td>
		<td id="tt126"></td>
		<td id="tt127"></td>
		<td id="tt128"></td>
		<td id="tt129"></td>
		<td id="tt130"></td>
		<td id="tt131"></td>
		<td id="tt132"></td>
		<td id="tt133"></td>
		<td id="tt134"></td>
		<td id="tt135"></td>
		<td id="tt136"></td>
		<td id="tt137"></td>
		<td id="tt138"></td>
		<td id="tt139"></td>
		<td id="tt140"></td>
		<td id="tt141"></td>
		<td id="tt142"></td>
		<td id="tt143"></td>
	</tr>
	<tr>
		<th>일<small>요일</small></th>
		<td id="tt144"></td>
		<td id="tt145"></td>
		<td id="tt146"></td>
		<td id="tt147"></td>
		<td id="tt148"></td>
		<td id="tt149"></td>
		<td id="tt150"></td>
		<td id="tt151"></td>
		<td id="tt152"></td>
		<td id="tt153"></td>
		<td id="tt154"></td>
		<td id="tt155"></td>
		<td id="tt156"></td>
		<td id="tt157"></td>
		<td id="tt158"></td>
		<td id="tt159"></td>
		<td id="tt160"></td>
		<td id="tt161"></td>
		<td id="tt162"></td>
		<td id="tt163"></td>
		<td id="tt164"></td>
		<td id="tt165"></td>
		<td id="tt166"></td>
		<td id="tt167"></td>
	</tr>
</table>

						<div class="text-center mt-2">
							이용 방법
							<span data-toggle="tooltip" data-placement="top" title="위의 요일별 시간 테이블의 칸을 클릭 혹은 마우스 드래그를 하여 운영 시간을 지정하세요.">
								<span class="fa-regular fa-circle-info text-info"></span>
							</span>
							<span class="px-3"><span class="fa-regular fa-slash-forward fa-2xs text-muted"></span></span>
							주의 사항
							<span data-toggle="tooltip" data-placement="top" title="운영 시간은 반드시 지정되어야 합니다.">
								<span class="fa-regular fa-triangle-exclamation text-yellow"></span>
							</span>
						</div>
					</div>
				</div>

			</div>
        
			<!-- Modal footer -->
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">취소</button>
				<button name="save-btn" type="button" class="btn btn-primary" onclick='saveForm2()'>저장</button>
			</div>
			
		</form>
	</div>
</div>

</script>


<style>

/* 시간 설정 테이블 */
table#base-time-table {
	border-spacing: 0px;
}
table#base-time-table th {
	font-weight: 400;
}
table#base-time-table td {
	border: 1px solid #fff;
	width: 28px;
	height: 30px;
	margin: 10px;
	background-color: rgba(24,28,33,0.06) !important;
	cursor: default;
	padding: 0px;
}
table#base-time-table td.selected {
	background-color: #02a96b !important;
}
table#base-time-table td.rselected {
	background-color: #02BC77 !important;
}
table#base-time-table td.nzselected {
	background-color: #f00 !important;
}

</style>

<!--  / Forms -->

<!--  Scripts -->

<script>

function setBizHourStr() {

	var val = "${bizHour}";
	if (val.length == 168) {
		for(var i = 0; i < 168; i++) {
			$("#btt" + i).removeClass("selected rselected nselected");
			if (Number(val.substr(i, 1)) == 1) {
				$("#btt" + i).addClass("rselected");
			}
		}
	}
}


function copyToClipboard() {
	
	showOperationSuccessMsg();
}


function initForm2(subtitle) {
	
	$("#formRoot").html(kendo.template($("#template-2").html()));
	
	$('[data-toggle="tooltip"]').tooltip();

	
	$("#form-2 span[name='subtitle']").text(subtitle ? subtitle : "추가");
}


function editBizTime() {
	
	initForm2("운영 시간");

	$("#form-2").attr("url", "${updateTimeUrl}");
	
	$("#form-2 input[name='shortName']").val("${shortName}");
	$("#form-2 input[name='name']").val("${name}");


	// Time table 제어 코드
	timeTable = $("#time-table");
	timeTable.find("td").mousedown(function (e) {

		if($(this).hasClass("rselected")){
			isTimeMouseDown = true;
		} else {
			isTimeMouseDown2 = true;		  
		}
		  
		var cell = $(this);

		if (e.shiftKey) {
			ttSelectTo(cell);                
		} else {
			if(cell.hasClass("rselected")){
				cell.removeClass("rselected")
				timeTableIds.splice(cell.attr('id').substr(2),1,0);
			} else {
				cell.addClass("selected")
				timeTableIds.splice(cell.attr('id').substr(2),1,1);
			}

			timeStartCellIndex = cell.index() - 1;
			timeStartRowIndex = cell.parent().index();
		}
	      
		return false; // prevent text selection
		
	}).mouseover(function () {

		if (isTimeMouseDown){
			timeTable.find(".nselected").addClass("rselected");
			ttSelectTo2($(this));
		}
		if(isTimeMouseDown2) {
			timeTable.find(".selected").removeClass("selected");
			ttSelectTo($(this));
		}
		
	}).bind("selectstart", function () {

		return false;
	});

	// 운영 시간 표시
	ttSetValueStr("${bizHour}", true);
	mouseUpAct();
	
	
	$('#form-modal-2 .modal-dialog').draggable({ handle: '.modal-header' });
	$("#form-modal-2").modal();
	validateBizHours();
}


function mouseUpAct() {
	
	var cnt = 0;
	for(var i = 0; i < timeTableIds.length; i++) {
		if (timeTableIds[i] == 1) {
			cnt ++;
		}
	}
	
	if (Number($("#form-2 input[name='bizHrs']").val()) != cnt) {
		$("#form-2 input[name='bizHrs']").val(cnt);
	}
	validateBizHours();
}


function validateBizHours() {

	if (Number($("#form-2 input[name='bizHrs']").val()) == 0) {
		$("#form-2 button[name='save-btn']").addClass("disabled");
	} else {
		$("#form-2 button[name='save-btn']").removeClass("disabled");
	}
}


function saveForm2() {

	if (Number($("#form-2 input[name='bizHrs']").val()) > 0) {
		var data = {
		   		id: Number($("#form-2").attr("rowid")),
		   		bizHour: ttGetValueStr(),
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

				setTimeout(function(){
					location.reload();
				}, 1000);
			},
			error: ajaxSaveError
		});
	}
}


function mouseUpAct() {
	
	var cnt = 0;
	for(var i = 0; i < timeTableIds.length; i++) {
		if (timeTableIds[i] == 1) {
			cnt ++;
		}
	}
	
	if (Number($("#form-2 input[name='bizHrs']").val()) != cnt) {
		$("#form-2 input[name='bizHrs']").val(cnt);
	}
	validateBizHours();
}


function downloadGuideDoc() {
	
	location.href = "/adn/common/download?type=XlsTemplate&file=Inventory_Data_Setting_Guide.pdf";
}


function validateSysValuePctType() {
	
	if ($("select[name='sysValuePctType']").val() == "0") {
		$("div[name='sys-value-pct-value-div']").hide();
		$("input[name='sysValuePct']").attr('readonly', 'readonly');
	} else {
		$("input[name='sysValuePct']").removeAttr('readonly');
		$("div[name='sys-value-pct-value-div']").show();
		$("input[name='sysValuePct']").select();
		$("input[name='sysValuePct']").focus();
	}
}

function validateAdvFreqCapType() {
	
	if ($("select[name='advFreqCapType']").val() == "0") {
		$("div[name='adv-freq-cap-value-div']").hide();
		$("input[name='advFreqCap']").attr('readonly', 'readonly');
	} else {
		$("input[name='advFreqCap']").removeAttr('readonly');
		$("div[name='adv-freq-cap-value-div']").show();
		$("input[name='advFreqCap']").select();
		$("input[name='advFreqCap']").focus();
	}
}

function validateAdFreqCapType() {
	
	if ($("select[name='adFreqCapType']").val() == "0") {
		$("div[name='ad-freq-cap-value-div']").hide();
		$("input[name='adFreqCap']").attr('readonly', 'readonly');
	} else {
		$("input[name='adFreqCap']").removeAttr('readonly');
		$("div[name='ad-freq-cap-value-div']").show();
		$("input[name='adFreqCap']").select();
		$("input[name='adFreqCap']").focus();
	}
}

function validateCatFreqCapType() {
	
	if ($("select[name='catFreqCapType']").val() == "0") {
		$("div[name='cat-freq-cap-value-div']").hide();
		$("input[name='catFreqCap']").attr('readonly', 'readonly');
	} else {
		$("input[name='catFreqCap']").removeAttr('readonly');
		$("div[name='cat-freq-cap-value-div']").show();
		$("input[name='catFreqCap']").select();
		$("input[name='catFreqCap']").focus();
	}
}

function validateAdDailyScrCapType() {
	
	if ($("select[name='adDailyScrCapType']").val() == "0") {
		$("div[name='ad-daily-scr-cap-value-div']").hide();
		$("input[name='adDailyScrCap']").attr('readonly', 'readonly');
	} else {
		$("input[name='adDailyScrCap']").removeAttr('readonly');
		$("div[name='ad-daily-scr-cap-value-div']").show();
		$("input[name='adDailyScrCap']").select();
		$("input[name='adDailyScrCap']").focus();
	}
}

function validateImpPlanPerHourType() {
	
	if ($("select[name='impPlanPerHourType']").val() == "0") {
		$("div[name='imp-plan-per-hour-value-div']").hide();
		bootstrapSelectDisabled($("select[name='impPlanPerHour']"), true);
	} else {
		$("input[name='activeScrCount']").removeAttr('readonly');
		$("div[name='imp-plan-per-hour-value-div']").show();
		bootstrapSelectDisabled($("select[name='impPlanPerHour']"), false);
	}
}

function validateActiveScrCountType() {
	
	if ($("select[name='activeScrCountType']").val() == "0") {
		$("div[name='active-scr-count-value-div']").hide();
		$("input[name='activeScrCount']").attr('readonly', 'readonly');
	} else {
		$("input[name='activeScrCount']").removeAttr('readonly');
		$("div[name='active-scr-count-value-div']").show();
		$("input[name='activeScrCount']").select();
		$("input[name='activeScrCount']").focus();
	}
}


var infoWin = null;

function drawMap(data) {
	
	var map = new naver.maps.Map('site-map-container', {
		center: new naver.maps.LatLng(37.5512164, 126.98824864606178),
		zoom: 15
	}); 

	var titles = [];
	var types = [];
	var lats = [];
	var lngs = [];
	
	if (data) {
		data.forEach(function (item) {
			titles.push(item.title);
			types.push(item.venueType);
			lats.push(item.lat);
			lngs.push(item.lng);
		});
	}

	var markers = [];
	var infoWins = [];
	
	var bounds = [];
	
	if (titles.length > 0) {
		for (var i = 0; i < titles.length; i++) {
			var markerFile = "";
			if (types[i] == "UNIV") {
				markerFile = "marker-univ.png";
			} else if (types[i] == "FUEL") {
				markerFile = "marker-fuel.png";
			} else if (types[i] == "CVS") {
				markerFile = "marker-cvs.png";
			} else if (types[i] == "BUSSH") {
				markerFile = "marker-bussh.png";
			} else if (types[i] == "BLDG") {
				markerFile = "marker-bldg.png";
			} else if (types[i] == "HOSP") {
				markerFile = "marker-hosp.png";
			} else if (types[i] == "HISTP") {
				markerFile = "marker-histp.png";
			} else if (types[i] == "BUS") {
				markerFile = "marker-bus.png";
			} else if (types[i] == "GEN") {
				markerFile = "marker-gen.png";
			}

			// 수정중
			var marker = new naver.maps.Marker({
				position: new naver.maps.LatLng(lats[i], lngs[i]),
				icon: {
					content: '<img class="marker-image" src=\'/resources/shared/images/marker/' + markerFile + '\' width="46px" height="59px">',
					size: new naver.maps.Size(46, 59),
					scaledSize: new naver.maps.Size(46, 59),
					anchor: new naver.maps.Point(23, 59)
				},
				map: map
										
			});
			
			markers.push(marker);
			
			var content = [
				'<div class="rounded-marker rounded-marker-white">',
		        	'<span class="rounded-marker-title">' + titles[i] + '</span>',
		        '</div>'
		    ].join('');
			
			infoWins.push(new naver.maps.InfoWindow({
			    content: content,
			    borderWidth: 0,
			    disableAnchor: true,
			    backgroundColor: 'transparent',
			    pixelOffset: new naver.maps.Point(0, 0),
			}));
			
			bounds.push(marker.position);
		}
	}
	
	if (bounds.length == 1) {
		map.setCenter(bounds[0]);
	} else {
		map.fitBounds(bounds);
	}
	

    function getClickHandler(seq) {
		
		return function(e) {  // 마커를 클릭하는 부분
			var marker = markers[seq], // 클릭한 마커의 시퀀스로 찾는다.
				infoWindow = infoWins[seq]; // 클릭한 마커의 시퀀스로 찾는다

			if (infoWindow.getMap()) {
				infoWindow.close();
				infoWin = null;
			} else {
				infoWin = infoWindow;
				infoWindow.open(map, marker);
			}
    	}
    };

	for(var i = 0; i < markers.length; i++){
		naver.maps.Event.addListener(markers[i], "click", getClickHandler(i));
	}
	
	naver.maps.Event.addListener(map, "click", function(e) {
		if (infoWin) {
    	    infoWin.close();
    	    infoWin = null;
		}
	});
	
	
    var htmlMarker1 = {
		content: '<div style="cursor:pointer;width:32px;height:32px;line-height:32px;font-size:10px;color:#2791FA;text-align:center;font-weight:500;background:url(/resources/shared/images/cluster-marker.png);background-size:contain;"></div>',
		size: N.Size(32, 32),
		anchor: N.Point(16, 16)
    }, htmlMarker2 = {
		content: '<div style="cursor:pointer;width:44px;height:44px;line-height:44px;font-size:12px;color:#2791FA;text-align:center;font-weight:500;background:url(/resources/shared/images/cluster-marker.png);background-size:contain;"></div>',
		size: N.Size(44, 44),
		anchor: N.Point(22, 22)
	}, htmlMarker3 = {
		content: '<div style="cursor:pointer;width:56px;height:56px;line-height:56px;font-size:14px;color:#2791FA;text-align:center;font-weight:500;background:url(/resources/shared/images/cluster-marker.png);background-size:contain;"></div>',
		size: N.Size(56, 56),
		anchor: N.Point(28, 28)
	}, htmlMarker4 = {
		content: '<div style="cursor:pointer;width:64px;height:64px;line-height:64px;font-size:16px;color:#2791FA;text-align:center;font-weight:500;background:url(/resources/shared/images/cluster-marker.png);background-size:contain;"></div>',
		size: N.Size(64, 64),
		anchor: N.Point(32, 32)
	}, htmlMarker5 = {
		content: '<div style="cursor:pointer;width:80px;height:80px;line-height:80px;font-size:18px;color:#2791FA;text-align:center;font-weight:500;background:url(/resources/shared/images/cluster-marker.png);background-size:contain;"></div>',
		size: N.Size(80, 80),
		anchor: N.Point(40, 40)
	};	
	
	var markerClustering = new MarkerClustering({
		minClusterSize: 2, // 최소 클러스트링 단위
		maxZoom: 13,
		map: map,
		markers: markers, // 마커설정
		disableClickZoom: false,
		gridSize: 120,
		icons: [htmlMarker1, htmlMarker2, htmlMarker3, htmlMarker4, htmlMarker5],
		indexGenerator: [10, 100, 200, 500, 1000],
		stylingFunction: function(clusterMarker, count) {    
			$(clusterMarker.getElement()).find('div:first-child').text(count);
		}
	});
}


function readSiteLocData() {
	
	$.ajax({
		type: "POST",
		contentType: "application/json",
		dataType: "json",
		url: "${readSiteLocUrl}",
		data: JSON.stringify({ }),
		success: function (data) {
			drawMap(data);
		},
		error: ajaxReadError
	});
}


$(document).ready(function() {
	
	//$('[data-toggle="tooltip"]').tooltip();
	
	if (Clipboard.isSupported()) {
		new Clipboard('.clipboard-btn');
	} else {
		$('.clipboard-btn').prop('disabled', true);
	}

	$("select[name='impPlanPerHour']").selectpicker('render');

	$("select[name='sysValuePctType']").selectpicker('render');
	$("select[name='advFreqCapType']").selectpicker('render');
	$("select[name='adFreqCapType']").selectpicker('render');
	$("select[name='catFreqCapType']").selectpicker('render');
	$("select[name='adDailyScrCapType']").selectpicker('render');
	$("select[name='impPlanPerHourType']").selectpicker('render');
	$("select[name='activeScrCountType']").selectpicker('render');

	bootstrapSelectVal($("select[name='impPlanPerHour']"), "6");

	
	
	$("select[name='sysValuePctType']").on("change.bs.select", function(e){
		validateSysValuePctType();
	});

	$("select[name='advFreqCapType']").on("change.bs.select", function(e){
		validateAdvFreqCapType();
	});

	$("select[name='adFreqCapType']").on("change.bs.select", function(e){
		validateAdFreqCapType();
	});

	$("select[name='catFreqCapType']").on("change.bs.select", function(e){
		validateCatFreqCapType();
	});

	$("select[name='adDailyScrCapType']").on("change.bs.select", function(e){
		validateAdDailyScrCapType();
	});

	$("select[name='impPlanPerHourType']").on("change.bs.select", function(e){
		validateImpPlanPerHourType();
	});

	$("select[name='activeScrCountType']").on("change.bs.select", function(e){
		validateActiveScrCountType();
	});

	
	// form 로드 후, validation 설정을 위한 시간을 획득
	setTimeout(function(){
		$("#form-1").validate({
			rules: {
				sysValuePct: {
					digits: true, min: 50, max: 500,
				},
				adFreqCap: {
					digits: true, min: 10,
				},
				advFreqCap: {
					digits: true, min: 10,
				},
				catFreqCap: {
					digits: true, min: 10,
				},
				adDailyScrCap: {
					digits: true, min: 1,
				},
				activeScrCount: {
					digits: true, min: 1,
				},
			}
		});
	}, 1000);


	$("#save-btn").click(function(e) {
		
		if ($("#form-1").valid()) {
			
			var sysValuePct = $.trim($("input[name='sysValuePct']").val());
			if ($("select[name='sysValuePctType']").val() == "0") {
				sysValuePct = "";
			}
			
			var advFreqCap = $.trim($("input[name='advFreqCap']").val());
			if ($("select[name='advFreqCapType']").val() == "0") {
				advFreqCap = "";
			}
			
			var adFreqCap = $.trim($("input[name='adFreqCap']").val());
			if ($("select[name='adFreqCapType']").val() == "0") {
				adFreqCap = "";
			}
			
			var catFreqCap = $.trim($("input[name='catFreqCap']").val());
			if ($("select[name='catFreqCapType']").val() == "0") {
				catFreqCap = "";
			}
			
			var adDailyScrCap = $.trim($("input[name='adDailyScrCap']").val());
			if ($("select[name='adDailyScrCapType']").val() == "0") {
				adDailyScrCap = "";
			}
			
			var impPlanPerHour = $("select[name='impPlanPerHour']").val();
			if ($("select[name='impPlanPerHourType']").val() == "0") {
				impPlanPerHour = "6";
			}
			
			var activeScrCount = $.trim($("input[name='activeScrCount']").val());
			if ($("select[name='activeScrCountType']").val() == "0") {
				activeScrCount = "";
			}
			
	    	var data = {
	   			invenValues: $.trim($("input[name='invenValues']").val()),
	   			opts: $.trim($("input[name='opts']").val()),
	   			sysValuePct: sysValuePct,
	   			adFreqCap: adFreqCap,
	   			advFreqCap: advFreqCap,
	   			catFreqCap: catFreqCap,
	   			adDailyScrCap: adDailyScrCap,
	   			impPlanPerHour: impPlanPerHour,
	   			activeScrCount: activeScrCount,
	   		};

	       	$.ajax({
	   			type: "POST",
	   			contentType: "application/json",
	   			dataType: "json",
	   			url: "${updateUrl}",
	   			data: JSON.stringify(data),
	   			success: function (form) {
	   				showAlertModal("success", "설정이 변경되었습니다.");
	   			},
	   			error: ajaxSaveError
	   		});
		}
	});
	

	$.ajax({
		type: "POST",
		contentType: "application/json",
		dataType: "json",
		url: "${readUrl}",
		data: JSON.stringify({ }),
		success: function (data, status) {
			for(var i in data) {
				if (data[i].code == "inven.default") {
					$("input[name='invenValues']").val(data[i].value);
				} else if (data[i].code == "opt.list") {
					$("input[name='opts']").val(data[i].value);
				} else if (data[i].code == "sysValue.pct") {
					
					if (data[i].value) {
						bootstrapSelectVal($("select[name='sysValuePctType']"), "1");
						$("input[name='sysValuePct']").val(data[i].value);
					} else {
						bootstrapSelectVal($("select[name='sysValuePctType']"), "0");
						$("input[name='sysValuePct']").val("");
					}
					validateSysValuePctType();
					
				} else if (data[i].code == "freqCap.ad") {
					
					if (data[i].value) {
						bootstrapSelectVal($("select[name='adFreqCapType']"), "1");
						$("input[name='adFreqCap']").val(data[i].value);
					} else {
						bootstrapSelectVal($("select[name='adFreqCapType']"), "0");
						$("input[name='adFreqCap']").val("");
					}
					validateAdFreqCapType();

				} else if (data[i].code == "freqCap.advertiser") {
					
					if (data[i].value) {
						bootstrapSelectVal($("select[name='advFreqCapType']"), "1");
						$("input[name='advFreqCap']").val(data[i].value);
					} else {
						bootstrapSelectVal($("select[name='advFreqCapType']"), "0");
						$("input[name='advFreqCap']").val("");
					}
					validateAdvFreqCapType();

				} else if (data[i].code == "freqCap.category") {
					
					if (data[i].value) {
						bootstrapSelectVal($("select[name='catFreqCapType']"), "1");
						$("input[name='catFreqCap']").val(data[i].value);
					} else {
						bootstrapSelectVal($("select[name='catFreqCapType']"), "0");
						$("input[name='catFreqCap']").val("");
					}
					validateCatFreqCapType();
					
				} else if (data[i].code == "freqCap.daily.screen") {
					
					if (data[i].value) {
						bootstrapSelectVal($("select[name='adDailyScrCapType']"), "1");
						$("input[name='adDailyScrCap']").val(data[i].value);
					} else {
						bootstrapSelectVal($("select[name='adDailyScrCapType']"), "0");
						$("input[name='adDailyScrCap']").val("");
					}
					validateAdDailyScrCapType();
					
				} else if (data[i].code == "activeCount.screen") {
					
					if (data[i].value) {
						bootstrapSelectVal($("select[name='activeScrCountType']"), "1");
						$("input[name='activeScrCount']").val(data[i].value);
					} else {
						bootstrapSelectVal($("select[name='activeScrCountType']"), "0");
						$("input[name='activeScrCount']").val("");
					}
					validateActiveScrCountType();
					
				} else if (data[i].code == "impress.per.hour") {
					
					bootstrapSelectVal($("select[name='impPlanPerHour']"), data[i].value);
					
					if (data[i].value == "6") {
						bootstrapSelectVal($("select[name='impPlanPerHourType']"), "0");
					} else {
						bootstrapSelectVal($("select[name='impPlanPerHourType']"), "1");
					}
					validateImpPlanPerHourType();
					
				}
			}
		},
		error: ajaxReadError
	});
	
	setBizHourStr();

	
	$('#curr-medium-tabs a').on('shown.bs.tab', function(e){
		var id = $(e.target).attr("id");
		
		if (id == "site-map-tab") {
			// Naver Map 버그 해결
			window.dispatchEvent(new Event('resize'));
		}
	});
	
	// 지도 표시
	drawMap(null);
	
	readSiteLocData();
});

</script>


<!--  / Scripts -->

<style>

.rounded-marker {
	border-radius: 10px; 
	background: #000;
	font-weight: 300;
}

.rounded-marker-white {
	color: white;
}

.rounded-marker-yellow {
	color: yellow;
	font-weight: 400;
}

.rounded-marker-title {
	padding-right: 10px; 
	padding-left: 10px;
}

.blue-marker {
	width: 1.4rem;
	height: 1.4rem;
	border-radius: 1.4rem;
	background: #0066ff;
    display: inline-block;
    border-bottom-right-radius: 0;
    position: relative;
    transform: rotate(45deg);
}
.green-marker {
	width: 1.4rem;
	height: 1.4rem;
	border-radius: 1.4rem;
	background: #2d862d;
    display: inline-block;
    border-bottom-right-radius: 0;
    position: relative;
    transform: rotate(45deg);
}
.orange-marker {
	width: 1.4rem;
	height: 1.4rem;
	border-radius: 1.4rem;
	background: #e65c00;
    display: inline-block;
    border-bottom-right-radius: 0;
    position: relative;
    transform: rotate(45deg);
}
.gold-marker {
	width: 1.4rem;
	height: 1.4rem;
	border-radius: 1.4rem;
	background: #cc9900;
    display: inline-block;
    border-bottom-right-radius: 0;
    position: relative;
    transform: rotate(45deg);
}
.purple-marker {
	width: 1.4rem;
	height: 1.4rem;
	border-radius: 1.4rem;
	background: #9933ff;
    display: inline-block;
    border-bottom-right-radius: 0;
    position: relative;
    transform: rotate(45deg);
}

.marker-image {
	image-rendering: -webkit-optimize-contrast;
	/*image-rendering: crisp-edges;*/
}
</style>

<!-- / Page body -->





<!-- Functional tags -->

<func:cmmTimeTable />
<func:cmmValidate />


<!-- Closing tags -->

<common:base />
<common:pageClosing />
