<%@ page language="java" contentType="text/html; charset=UTF-8"
		 pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/rev/dailyapi/readScrTot" var="readScrTotUrl" />
<c:url value="/rev/dailyapi/readSitTot" var="readSitTotUrl" />

<c:url value="/rev/geofence" var="readUrl" />

<c:url value="/rev/monitoring/readResolutions" var="readResolutionUrl" />
<c:url value="/rev/monitoring/readStatuses" var="readStatusUrl" />
<c:url value="/rev/monitoring/recalcScr" var="recalcScrUrl" />


<!-- Opening tags -->

<common:mediumPageOpeningNaverMap />


<!-- Page title -->

<h4 class="pt-1 pb-3 mb-3">
	<span class="mr-1 ${sessionScope['loginUser'].icon}"></span>
	${pageTitle}

</h4>

<hr class="border-light container-m--x mt-0 mb-3">





<!-- Page body -->

<div id="map"style="border: solid 1px #e4e4e4; background-color: white; height: 90% !important; width: 100%;">
	<div id="_panel" style="display: inline-block; position: relative; z-index: 1; margin: 20px 0 0 20px; font-size: 14px;">
		<form method="post" name="form-1" url="${readUrl} }">
			<div class="btn-container" style="display: flex; flex-direction: column; justify-content: center; border: 1px solid #AFAFAF; border-radius: 4px; overflow: hidden; width: 62px; box-sizing: border-box; background-color: white;">
				<div class="btn-box">
					<button type="button" id="bussh-btn" value="active">
						<img src="${BusBtnAUrl}" id="bussh-btn-img">
					</button>
				</div>
				<div class="btn-box">
					<button type="button" id="univ-btn" value="active">
						<img src="${UnivBtnAUrl}" id="univ-btn-img">
					</button>
				</div>
				<div class="btn-box">
					<button type="button" id="buso-btn" value="active">
						<img src="${BusoBtnAUrl}" id="buso-btn-img">
					</button>
				</div>
				<div class="btn-box">
					<button type="button" id="hosp-btn" value="active">
						<img src="${HospBtnAUrl}" id="hosp-btn-img">
					</button>
				</div>
				<div class="btn-box">
					<button type="button" id="bldg-btn" value="active">
						<img src="${BldgBtnAUrl}" id="bldg-btn-img">
					</button>
				</div>
				<div class="btn-box">
					<button type="button" id="fuel-btn" value="active">
						<img src="${FuelBtnAUrl}" id="fuel-btn-img">
					</button>
				</div>
				<div class="btn-box">
					<button type="button" id="cvs-btn" value="active">
						<img src="${CvsBtnAUrl}" id="cvs-btn-img">
					</button>
				</div>
				<div class="btn-box">
					<button type="button" id="histp-btn" value="active">
						<img src="${HisBtnAUrl}" id="histp-btn-img">
					</button>
				</div>
				<div class="btn-box">
					<button type="button" id="gen-btn" value="active">
						<img src="${GenBtnAUrl}" id="gen-btn-img">
					</button>
				</div>
				<div class="btn-box all-btn-box" style="display: flex; align-items: center; width: 60px; height: 21px; background-color: #a3a3a3;">
					<button class="all-btn" type="button" value="active" style="background-color: #a3a3a3;">
						<img src="${AllBtnAUrl}" style="width: 29.5px;">
					</button>
					<div class="divider" style="width: 1px; height: 100%; background-color: white;"></div>
					<button class="all-btn" type="button" value="inactive" style="background-color: #a3a3a3;">
						<img src="${AllBtnIaUrl}" style="width: 29.5px;">
					</button>
				</div>
			</div>
		</form>
	</div>
</div>

<!--  Scripts -->

<script>

	$(document).ready(function() {

		showWaitModal();

		setTimeout(function(){
			drawMap();
			hideWaitModal();
		}, 100);

	});
</script>

<script>
	function drawMap() {

		var rgnTitles = [];
		var rgnLats = [];
		var rgnLngs = [];

		var stateTitles = [];
		var stateLats = [];
		var stateLngs = [];

		var cvsTitles = [];
		var cvsLats = [];
		var cvsLngs = [];
		var cvsLatsLngs = [];

		var hospTitles = [];
		var hospLats = [];
		var hospLngs = [];
		var hospLatsLngs = [];

		var bldgTitles = [];
		var bldgLats = [];
		var bldgLngs = [];
		var bldgLatsLngs = [];

		var busTitles = [];
		var busLats = [];
		var busLngs = [];
		var busLatsLngs = [];

		var fuelTitles = [];
		var fuelLats = [];
		var fuelLngs = [];
		var fuelLatsLngs = [];

		var histpTitles = [];
		var histpLats = [];
		var histpLngs = [];
		var histpLatsLngs = [];

		var busoTitles = [];
		var busoLats = [];
		var busoLngs = [];
		var busoLatsLngs = [];

		var univTitles = [];
		var univLats = [];
		var univLngs = [];
		var univLatsLngs = [];

		var genTitles = [];
		var genLats = [];
		var genLngs = [];
		var genLatsLngs = [];

		<c:forEach var="item" items="${stateList}">
		stateTitles.push("${item.title}");
		stateLats.push(${item.lat});
		stateLngs.push(${item.lng});
		</c:forEach>

		<c:forEach var="item" items="${regionList}">
		rgnTitles.push("${item.title}");
		rgnLats.push(${item.lat});
		rgnLngs.push(${item.lng});
		</c:forEach>

		<c:forEach var="item" items="${CmarkerList}">
		cvsTitles.push("${item.title}");
		cvsLats.push(${item.lat});
		cvsLngs.push(${item.lng});
		cvsLatsLngs.push(new naver.maps.LatLng(${item.lat}, ${item.lng}));
		</c:forEach>

		<c:forEach var="item" items="${HmarkerList}">
		hospTitles.push("${item.title}");
		hospLats.push(${item.lat});
		hospLngs.push(${item.lng});
		hospLatsLngs.push(new naver.maps.LatLng(${item.lat}, ${item.lng}));
		</c:forEach>

		<c:forEach var="item" items="${BmarkerList}">
		bldgTitles.push("${item.title}");
		bldgLats.push(${item.lat});
		bldgLngs.push(${item.lng});
		bldgLatsLngs.push(new naver.maps.LatLng(${item.lat}, ${item.lng}));
		</c:forEach>

		<c:forEach var="item" items="${BusmarkerList}">
		busTitles.push("${item.title}");
		busLats.push(${item.lat});
		busLngs.push(${item.lng});
		busLatsLngs.push(new naver.maps.LatLng(${item.lat}, ${item.lng}));
		</c:forEach>

		<c:forEach var="item" items="${FmarkerList}">
		fuelTitles.push("${item.title}");
		fuelLats.push(${item.lat});
		fuelLngs.push(${item.lng});
		fuelLatsLngs.push(new naver.maps.LatLng(${item.lat}, ${item.lng}));
		</c:forEach>

		<c:forEach var="item" items="${HismarkerList}">
		histpTitles.push("${item.title}");
		histpLats.push(${item.lat});
		histpLngs.push(${item.lng});
		histpLatsLngs.push(new naver.maps.LatLng(${item.lat}, ${item.lng}));
		</c:forEach>

		<c:forEach var="item" items="${BusomarkerList}">
		busoTitles.push("${item.title}");
		busoLats.push(${item.lat});
		busoLngs.push(${item.lng});
		busoLatsLngs.push(new naver.maps.LatLng(${item.lat}, ${item.lng}));
		</c:forEach>

		<c:forEach var="item" items="${UnivmarkerList}">
		univTitles.push("${item.title}");
		univLats.push(${item.lat});
		univLngs.push(${item.lng});
		univLatsLngs.push(new naver.maps.LatLng(${item.lat}, ${item.lng}));
		</c:forEach>

		<c:forEach var="item" items="${GmarkerList}">
		genTitles.push("${item.title}");
		genLats.push(${item.lat});
		genLngs.push(${item.lng});
		genLatsLngs.push(new naver.maps.LatLng(${item.lat}, ${item.lng}));
		</c:forEach>


		function highlightMarker(marker) {
			var icon = marker.getIcon();

			marker.setZIndex(1000);
		}

		function unhighlightMarker(marker) {
			var icon = marker.getIcon();

			marker.setZIndex(100);
		}

		var lat = 37.5512164;
		var lng = 126.98824864606178;

		var map = new naver.maps.Map('map', {
			center: new naver.maps.LatLng(lat, lng),
			zoom: 11,
			logoControl: false,
		});
		var rgnMarkers = [];
		var stateMarkers = [];

		var infoWin = null;

		var cvsInfoWins = [];
		var cvsMarkers = [];
		var cvsdtlMarkers = [];

		var hospInfoWins = [];
		var hospMarkers = [];
		var hospdtlMarkers = [];

		var bldgInfoWins = [];
		var bldgMarkers = [];
		var bldgdtlMarkers = [];

		var busMarkers = [];
		var busdtlMarkers = [];
		var busInfoWins = [];

		var fuelMarkers = [];
		var fueldtlMarkers = [];
		var fuelInfoWins = [];

		var histpMarkers = [];
		var histpdtlMarkers = [];
		var histpInfoWins = [];

		var busoMarkers = [];
		var busodtlMarkers = [];
		var busoInfoWins = [];

		var univMarkers = [];
		var univdtlMarkers = [];
		var univInfoWins = [];

		var genMarkers = [];
		var gendtlMarkers = [];
		var genInfoWins = [];

		const btnValueMap = {
			"#cvs-btn": {
				title: cvsTitles,
				category: "ret",
				infoWins: cvsInfoWins,
				dtlMarkers: cvsdtlMarkers,
				markers: cvsMarkers,
				lats: cvsLats,
				lngs: cvsLngs,
				latslngs: cvsLatsLngs,
				iconImgUrl: "${CvsIcoMarkerUrl}",
				activeImgUrl: "${CvsBtnAUrl}",
				inactiveImgUrl: "${CvsBtnIaUrl}",
				imgTag: "#cvs-btn-img"
			},
			"#hosp-btn": {
				title: hospTitles,
				category: "poc",
				infoWins: hospInfoWins,
				dtlMarkers: hospdtlMarkers,
				markers: hospMarkers,
				lats: hospLats,
				lngs: hospLngs,
				latslngs: hospLatsLngs,
				iconImgUrl: "${HospIcoMarkerUrl}",
				activeImgUrl: "${HospBtnAUrl}",
				inactiveImgUrl: "${HospBtnIaUrl}",
				imgTag: "#hosp-btn-img"
			},
			"#bldg-btn": {
				title: bldgTitles,
				category: "out",
				infoWins: bldgInfoWins,
				dtlMarkers: bldgdtlMarkers,
				markers: bldgMarkers,
				lats: bldgLats,
				lngs: bldgLngs,
				latslngs: bldgLatsLngs,
				iconImgUrl: "${BldgIcoMarkerUrl}",
				activeImgUrl: "${BldgBtnAUrl}",
				inactiveImgUrl: "${BldgBtnIaUrl}",
				imgTag: "#bldg-btn-img"
			},
			"#bussh-btn": {
				title: busTitles,
				category: "out",
				infoWins: busInfoWins,
				dtlMarkers: busdtlMarkers,
				markers: busMarkers,
				lats: busLats,
				lngs: busLngs,
				latslngs: busLatsLngs,
				iconImgUrl: "${BusshIcoMarkerUrl}",
				activeImgUrl: "${BusBtnAUrl}",
				inactiveImgUrl: "${BusBtnIaUrl}",
				imgTag: "#bussh-btn-img"
			},
			"#fuel-btn": {
				title: fuelTitles,
				category: "ret",
				infoWins: fuelInfoWins,
				dtlMarkers: fueldtlMarkers,
				markers: fuelMarkers,
				lats: fuelLats,
				lngs: fuelLngs,
				latslngs: fuelLatsLngs,
				iconImgUrl: "${FuelIcoMarkerUrl}",
				activeImgUrl: "${FuelBtnAUrl}",
				inactiveImgUrl: "${FuelBtnIaUrl}",
				imgTag: "#fuel-btn-img"
			},
			"#histp-btn": {
				title: histpTitles,
				category: "trn",
				infoWins: histpInfoWins,
				dtlMarkers: histpdtlMarkers,
				markers: histpMarkers,
				lats: histpLats,
				lngs: histpLngs,
				latslngs: histpLatsLngs,
				iconImgUrl: "${HistpIcoMarkerUrl}",
				activeImgUrl: "${HisBtnAUrl}",
				inactiveImgUrl: "${HisBtnIaUrl}",
				imgTag: "#histp-btn-img"
			},
			"#buso-btn": {
				title: busoTitles,
				category: "trn",
				infoWins: busoInfoWins,
				dtlMarkers: busodtlMarkers,
				markers: busoMarkers,
				lats: busoLats,
				lngs: busoLngs,
				latslngs: busoLatsLngs,
				iconImgUrl: "${BusIcoMarkerUrl}",
				activeImgUrl: "${BusoBtnAUrl}",
				inactiveImgUrl: "${BusoBtnIaUrl}",
				imgTag: "#buso-btn-img"
			},
			"#univ-btn": {
				title: univTitles,
				category: "edu",
				infoWins: univInfoWins,
				dtlMarkers: univdtlMarkers,
				markers: univMarkers,
				lats: univLats,
				lngs: univLngs,
				latslngs: univLatsLngs,
				iconImgUrl: "${UnivIcoMarkerUrl}",
				activeImgUrl: "${UnivBtnAUrl}",
				inactiveImgUrl: "${UnivBtnIaUrl}",
				imgTag: "#univ-btn-img"
			},
			"#gen-btn": {
				title: genTitles,
				category: "ofc",
				infoWins: genInfoWins,
				dtlMarkers: gendtlMarkers,
				markers: genMarkers,
				lats: genLats,
				lngs: genLngs,
				latslngs: genLatsLngs,
				iconImgUrl: "${GenIcoMarkerUrl}",
				activeImgUrl: "${GenBtnAUrl}",
				inactiveImgUrl: "${GenBtnIaUrl}",
				imgTag: "#gen-btn-img"
			}
		};

		// 카테코리 벨류맵
		const catValueMap = {
			"ret": {
				iconImgUrl: "${RetCirMarkerUrl}",
				colorCode: "#00338E"
				// venueType: ["cvs", "fuel"]
			},
			"poc": {
				iconImgUrl: "${PocCirMarkerUrl}",
				colorCode: "#1273FF"
				// venueType: ["hosp"]
			},
			"out": {
				iconImgUrl: "${OutCirMarkerUrl}",
				colorCode: "#59b51d"
				// venueType: ["bldg", "bus"]
			},
			"trn": {
				iconImgUrl: "${TrnCirMarkerUrl}",
				colorCode: "#FF814A"
				// venueType: ["histp", "buso"]
			},
			"ofc": {
				iconImgUrl: "${OfcCirMarkerUrl}",
				colorCode: "#6A1AE8"
				// venueType: ["gen"]
			},
			"edu": {
				iconImgUrl: "${EduCirMarkerUrl}",
				colorCode: "#E23D3D"
				// venueType: ["univ"]
			},
		};


		var bounds = map.getBounds(),
				southWest = bounds.getSW(),
				northEast = bounds.getNE(),
				lngSpan = northEast.lng() - southWest.lng(),
				latSpan = northEast.lat() - southWest.lat();

		for (var i = 0; i < rgnTitles.length; i++) {
			var marker = new naver.maps.Marker({
				position: new naver.maps.LatLng(rgnLats[i], rgnLngs[i]),
				icon: {
					content: [
						'<div class="cs_mapbridge">',
						'<div class="rounded-marker rounded-marker-white">'
						+ '<span class="rounded-marker-title">' + rgnTitles[i] + '</span>'
						+ '</div>'
						+ '</div>',

					].join(''),
				}

			});

			rgnMarkers.push(marker);

		}

		for (var i = 0; i < stateTitles.length; i++) {
			var marker = new naver.maps.Marker({
				position: new naver.maps.LatLng(stateLats[i], stateLngs[i]),
// 			map: map,
				icon: {
					content: [
						'<div class="rounded-marker rounded-marker-white">',
						'<div class="circle">',
						'</div>',
						'<span class="rounded-marker-title">' + stateTitles[i] + '</span>',
						'</div>',
					].join(''),
				}
			});

			stateMarkers.push(marker);

		}

		for (var i = 0; i < cvsTitles.length; i++) {
			var marker = new naver.maps.Marker({
				position: new naver.maps.LatLng(cvsLats[i], cvsLngs[i]),
				icon: {
					url: '${RetCirMarkerUrl}',
				},
			});
			cvsMarkers.push(marker);

			var content = [
				'<div>',
				'<div class="bubble-textInfo" style="background-color: #00338E">',
				'<img src="${CvsIcoMarkerUrl}" width="22" height="22"/>',
				'<span class="bubble-text-span">' + cvsTitles[i] + '</span>',
				'</div>',
				'<div class="triangle-wrapInfo">',
				'<div class="triangle-blue"></div>',
				'<div class="triangle-white" style="border-color: #00338E transparent transparent;"></div>',
				'</div>',
				'</div>'
			].join('');

			cvsInfoWins.push(new naver.maps.InfoWindow({
				content: content,
				borderWidth: 0,
				disableAnchor: true,
				backgroundColor: 'transparent',
				pixelOffset: new naver.maps.Point(0, 7),
			}));
		}

		for (var i = 0; i < cvsTitles.length; i++) {
			var marker = new naver.maps.Marker({
				position: new naver.maps.LatLng(cvsLats[i], cvsLngs[i]),
				icon: {
					content: [
						'<div>',
						'<div class="bubble-text" style="background-color: #00338E">',
						'<img src="${CvsIcoMarkerUrl}" width="22" height="22"/>',
						'<span class="bubble-text-span">' + cvsTitles[i] + '</span>',
						'</div>',
						'<div class="triangle-wrap">',
						'<div class="triangle-blue"></div>',
						'<div class="triangle-white" style="border-color: #00338E transparent transparent;"></div>',
						'</div>',
						'</div>'
					].join(''),
					anchor: new naver.maps.Point(16, 40)
				},
			});

			marker.addListener('mouseover', function (e) {
				highlightMarker(e.overlay);
			});
			marker.addListener('mouseout', function (e) {
				unhighlightMarker(e.overlay);
			});

			cvsdtlMarkers.push(marker);

		}

		for (var i = 0; i < hospTitles.length; i++) {
			var marker = new naver.maps.Marker({
				position: new naver.maps.LatLng(hospLats[i], hospLngs[i]),
				icon: {
					url: '${PocCirMarkerUrl}',
				},
			});

			hospMarkers.push(marker);

			var content = [
				'<div>',
				'<div class="bubble-textInfo" style="background-color: #1273FF;">',
				'<img src="${HospIcoMarkerUrl}" width="22" height="22"/>',
				'<span class="bubble-text-span">' + hospTitles[i] + '</span>',
				'</div>',
				'<div class="triangle-wrapInfo">',
				'<div class="triangle-blue"></div>',
				'<div class="triangle-white" style="border-color: #1273FF transparent transparent;"></div>',
				'</div>',
				'</div>'
			].join('');

			hospInfoWins.push(new naver.maps.InfoWindow({
				content: content,
				borderWidth: 0,
				disableAnchor: true,
				backgroundColor: 'transparent',
				pixelOffset: new naver.maps.Point(0, 7),
			}));

		}

		for (var i = 0; i < hospTitles.length; i++) {
			var marker = new naver.maps.Marker({
				position: new naver.maps.LatLng(hospLats[i], hospLngs[i]),
				icon: {
					content: [
						'<div>',
						'<div class="bubble-text" style="background-color: #1273FF;">',
						'<img src="${HospIcoMarkerUrl}" width="22" height="22"/>',
						'<span class="bubble-text-span">' + hospTitles[i] + '</span>',
						'</div>',
						'<div class="triangle-wrap">',
						'<div class="triangle-blue"></div>',
						'<div class="triangle-white" style="border-color: #1273FF transparent transparent;"></div>',
						'</div>',
						'</div>'
					].join(''),
					anchor: new naver.maps.Point(16, 40)
				}
			});

			marker.addListener('mouseover', function (e) {
				highlightMarker(e.overlay);
			});
			marker.addListener('mouseout', function (e) {
				unhighlightMarker(e.overlay);
			});

			hospdtlMarkers.push(marker);
		}

		for (var i = 0; i < bldgTitles.length; i++) {
			var marker = new naver.maps.Marker({
				position: new naver.maps.LatLng(bldgLats[i], bldgLngs[i]),
				icon: {
					url: '${OutCirMarkerUrl}',
				},
			});

			bldgMarkers.push(marker);

			var content = [
				'<div>',
				'<div class="bubble-textInfo" style="background-color: #59b51d;">',
				'<img src="${BldgIcoMarkerUrl}" width="22" height="22"/>',
				'<span class="bubble-text-span">' + bldgTitles[i] + '</span>',
				'</div>',
				'<div class="triangle-wrapInfo">',
				'<div class="triangle-blue"></div>',
				'<div class="triangle-white" style="border-color: #59b51d transparent transparent;"></div>',
				'</div>',
				'</div>'
			].join('');

			bldgInfoWins.push(new naver.maps.InfoWindow({
				content: content,
				borderWidth: 0,
				disableAnchor: true,
				backgroundColor: 'transparent',
				pixelOffset: new naver.maps.Point(0, 7),
			}));

		}

		for (var i = 0; i < bldgTitles.length; i++) {
			var marker = new naver.maps.Marker({
				position: new naver.maps.LatLng(bldgLats[i], bldgLngs[i]),
				icon: {
					content: [
						'<div>',
						'<div class="bubble-text" style="background-color: #59b51d;">',
						'<img src="${BldgIcoMarkerUrl}" width="22" height="22"/>',
						'<span class="bubble-text-span">' + bldgTitles[i] + '</span>',
						'</div>',
						'<div class="triangle-wrap">',
						'<div class="triangle-blue"></div>',
						'<div class="triangle-white" style="border-color: #59b51d transparent transparent;"></div>',
						'</div>',
						'</div>'
					].join(''),
					anchor: new naver.maps.Point(16, 40)
				}
			});

			marker.addListener('mouseover', function (e) {
				highlightMarker(e.overlay);
			});
			marker.addListener('mouseout', function (e) {
				unhighlightMarker(e.overlay);
			});

			bldgdtlMarkers.push(marker);
		}

		for (var i = 0; i < busTitles.length; i++) {
			var marker = new naver.maps.Marker({
				position: new naver.maps.LatLng(busLats[i], busLngs[i]),
				icon: {
					url: '${OutCirMarkerUrl}',
				},
			});

			busMarkers.push(marker);

			var content = [
				'<div>',
				'<div class="bubble-textInfo" style="background-color: #59b51d;">',
				'<img src="${BusshIcoMarkerUrl}" width="22" height="22"/>',
				'<span class="bubble-text-span">' + busTitles[i] + '</span>',
				'</div>',
				'<div class="triangle-wrapInfo">',
				'<div class="triangle-blue"></div>',
				'<div class="triangle-white" style="border-color: #59b51d transparent transparent;"></div>',
				'</div>',
				'</div>'
			].join('');

			busInfoWins.push(new naver.maps.InfoWindow({
				content: content,
				borderWidth: 0,
				disableAnchor: true,
				backgroundColor: 'transparent',
				pixelOffset: new naver.maps.Point(0, 7),
			}));

		}

		for (var i = 0; i < busTitles.length; i++) {
			var marker = new naver.maps.Marker({
				position: new naver.maps.LatLng(busLats[i], busLngs[i]),
				icon: {
					content: [
						'<div>',
						'<div class="bubble-text" style="background-color: #59b51d;">',
						'<img src="${BusshIcoMarkerUrl}" width="22" height="22"/>',
						'<span class="bubble-text-span">' + busTitles[i] + '</span>',
						'</div>',
						'<div class="triangle-wrap">',
						'<div class="triangle-blue"></div>',
						'<div class="triangle-white" style="border-color: #59b51d transparent transparent;"></div>',
						'</div>',
						'</div>'
					].join(''),
					anchor: new naver.maps.Point(16, 40)
				}
			});

			marker.addListener('mouseover', function (e) {
				highlightMarker(e.overlay);
			});
			marker.addListener('mouseout', function (e) {
				unhighlightMarker(e.overlay);
			});

			busdtlMarkers.push(marker);
		}

		for (var i = 0; i < fuelTitles.length; i++) {
			var marker = new naver.maps.Marker({
				position: new naver.maps.LatLng(fuelLats[i], fuelLngs[i]),
				icon: {
					url: '${RetCirMarkerUrl}',
				},
			});
			fuelMarkers.push(marker);

			var content = [
				'<div>',
				'<div class="bubble-textInfo" style="background-color: #00338E;">',
				'<img src="${FuelIcoMarkerUrl}" width="22" height="22"/>',
				'<span class="bubble-text-span">' + fuelTitles[i] + '</span>',
				'</div>',
				'<div class="triangle-wrapInfo">',
				'<div class="triangle-blue"></div>',
				'<div class="triangle-white" style="border-color: #00338E transparent transparent;"></div>',
				'</div>',
				'</div>'
			].join('');

			fuelInfoWins.push(new naver.maps.InfoWindow({
				content: content,
				borderWidth: 0,
				disableAnchor: true,
				backgroundColor: 'transparent',
				pixelOffset: new naver.maps.Point(0, 7),
			}));
		}

		for (var i = 0; i < fuelTitles.length; i++) {
			var marker = new naver.maps.Marker({
				position: new naver.maps.LatLng(fuelLats[i], fuelLngs[i]),
				icon: {
					content: [
						'<div>',
						'<div class="bubble-text" style="background-color: #00338E;">',
						'<img src="${FuelIcoMarkerUrl}" width="22" height="22"/>',
						'<span class="bubble-text-span">' + fuelTitles[i] + '</span>',
						'</div>',
						'<div class="triangle-wrap">',
						'<div class="triangle-blue"></div>',
						'<div class="triangle-white" style="border-color: #00338E transparent transparent;"></div>',
						'</div>',
						'</div>'
					].join(''),
					anchor: new naver.maps.Point(16, 40)
				},
			});

			marker.addListener('mouseover', function (e) {
				highlightMarker(e.overlay);
			});
			marker.addListener('mouseout', function (e) {
				unhighlightMarker(e.overlay);
			});

			fueldtlMarkers.push(marker);

		}

		for (var i = 0; i < histpTitles.length; i++) {
			var marker = new naver.maps.Marker({
				position: new naver.maps.LatLng(histpLats[i], histpLngs[i]),
				icon: {
					url: '${TrnCirMarkerUrl}',
				},
			});
			histpMarkers.push(marker);

			var content = [
				'<div>',
				'<div class="bubble-textInfo" style="background-color: #FF814A;">',
				'<img src="${HistpIcoMarkerUrl}" width="22" height="22"/>',
				'<span class="bubble-text-span">' + histpTitles[i] + '</span>',
				'</div>',
				'<div class="triangle-wrapInfo">',
				'<div class="triangle-blue"></div>',
				'<div class="triangle-white" style="border-color: #FF814A transparent transparent;"></div>',
				'</div>',
				'</div>'
			].join('');

			histpInfoWins.push(new naver.maps.InfoWindow({
				content: content,
				borderWidth: 0,
				disableAnchor: true,
				backgroundColor: 'transparent',
				pixelOffset: new naver.maps.Point(0, 7),
			}));
		}

		for (var i = 0; i < histpTitles.length; i++) {
			var marker = new naver.maps.Marker({
				position: new naver.maps.LatLng(histpLats[i], histpLngs[i]),
				icon: {
					content: [
						'<div>',
						'<div class="bubble-text" style="background-color: #FF814A;">',
						'<img src="${HistpIcoMarkerUrl}" width="22" height="22"/>',
						'<span class="bubble-text-span">' + histpTitles[i] + '</span>',
						'</div>',
						'<div class="triangle-wrap">',
						'<div class="triangle-blue"></div>',
						'<div class="triangle-white" style="border-color: #FF814A transparent transparent;"></div>',
						'</div>',
						'</div>'
					].join(''),
					anchor: new naver.maps.Point(16, 40)
				},
			});

			marker.addListener('mouseover', function (e) {
				highlightMarker(e.overlay);
			});
			marker.addListener('mouseout', function (e) {
				unhighlightMarker(e.overlay);
			});

			histpdtlMarkers.push(marker);

		}

		for (var i = 0; i < busoTitles.length; i++) {
			var marker = new naver.maps.Marker({
				position: new naver.maps.LatLng(busoLats[i], busoLngs[i]),
				icon: {
					url: '${TrnCirMarkerUrl}',
				},
			});
			busoMarkers.push(marker);

			var content = [
				'<div>',
				'<div class="bubble-textInfo" style="background-color: #FF814A;">',
				'<img src="${BusIcoMarkerUrl}" width="22" height="22"/>',
				'<span class="bubble-text-span">' + busoTitles[i] + '</span>',
				'</div>',
				'<div class="triangle-wrapInfo">',
				'<div class="triangle-blue"></div>',
				'<div class="triangle-white" style="border-color: #FF814A transparent transparent;"></div>',
				'</div>',
				'</div>'
			].join('');

			busoInfoWins.push(new naver.maps.InfoWindow({
				content: content,
				borderWidth: 0,
				disableAnchor: true,
				backgroundColor: 'transparent',
				pixelOffset: new naver.maps.Point(0, 7),
			}));
		}

		for (var i = 0; i < busoTitles.length; i++) {
			var marker = new naver.maps.Marker({
				position: new naver.maps.LatLng(busoLats[i], busoLngs[i]),
				icon: {
					content: [
						'<div>',
						'<div class="bubble-text" style="background-color: #FF814A;">',
						'<img src="${BusIcoMarkerUrl}" width="22" height="22"/>',
						'<span class="bubble-text-span">' + busoTitles[i] + '</span>',
						'</div>',
						'<div class="triangle-wrap">',
						'<div class="triangle-blue"></div>',
						'<div class="triangle-white" style="border-color: #FF814A transparent transparent;"></div>',
						'</div>',
						'</div>'
					].join(''),
					anchor: new naver.maps.Point(16, 40)
				},
			});

			marker.addListener('mouseover', function (e) {
				highlightMarker(e.overlay);
			});
			marker.addListener('mouseout', function (e) {
				unhighlightMarker(e.overlay);
			});

			busodtlMarkers.push(marker);

		}

		for (var i = 0; i < univTitles.length; i++) {
			var marker = new naver.maps.Marker({
				position: new naver.maps.LatLng(univLats[i], univLngs[i]),
				icon: {
					url: '${EduCirMarkerUrl}',
				},
			});
			univMarkers.push(marker);

			var content = [
				'<div>',
				'<div class="bubble-textInfo" style="background-color: #E23D3D;">',
				'<img src="${UnivIcoMarkerUrl}" width="22" height="22"/>',
				'<span class="bubble-text-span">' + univTitles[i] + '</span>',
				'</div>',
				'<div class="triangle-wrapInfo">',
				'<div class="triangle-blue"></div>',
				'<div class="triangle-white" style="border-color: #E23D3D transparent transparent;"></div>',
				'</div>',
				'</div>'
			].join('');

			univInfoWins.push(new naver.maps.InfoWindow({
				content: content,
				borderWidth: 0,
				disableAnchor: true,
				backgroundColor: 'transparent',
				pixelOffset: new naver.maps.Point(0, 7),
			}));
		}

		for (var i = 0; i < univTitles.length; i++) {
			var marker = new naver.maps.Marker({
				position: new naver.maps.LatLng(univLats[i], univLngs[i]),
				icon: {
					content: [
						'<div>',
						'<div class="bubble-text" style="background-color: #E23D3D;">',
						'<img src="${UnivIcoMarkerUrl}" width="22" height="22"/>',
						'<span class="bubble-text-span">' + univTitles[i] + '</span>',
						'</div>',
						'<div class="triangle-wrap">',
						'<div class="triangle-blue"></div>',
						'<div class="triangle-white" style="border-color: #E23D3D transparent transparent;"></div>',
						'</div>',
						'</div>'
					].join(''),
					anchor: new naver.maps.Point(16, 40)
				},
			});

			marker.addListener('mouseover', function (e) {
				highlightMarker(e.overlay);
			});
			marker.addListener('mouseout', function (e) {
				unhighlightMarker(e.overlay);
			});

			univdtlMarkers.push(marker);

		}

		for (var i = 0; i < genTitles.length; i++) {
			var marker = new naver.maps.Marker({
				position: new naver.maps.LatLng(genLats[i], genLngs[i]),
				icon: {
					url: '${OfcCirMarkerUrl}',
				},
			});
			genMarkers.push(marker);

			var content = [
				'<div>',
				'<div class="bubble-textInfo" style="background-color: #6A1AE8;">',
				'<img src="${GenIcoMarkerUrl}" width="22" height="22"/>',
				'<span class="bubble-text-span">' + genTitles[i] + '</span>',
				'</div>',
				'<div class="triangle-wrapInfo">',
				'<div class="triangle-blue"></div>',
				'<div class="triangle-white" style="border-color: #6A1AE8 transparent transparent;"></div>',
				'</div>',
				'</div>'
			].join('');

			genInfoWins.push(new naver.maps.InfoWindow({
				content: content,
				borderWidth: 0,
				disableAnchor: true,
				backgroundColor: 'transparent',
				pixelOffset: new naver.maps.Point(0, 7),
			}));
		}

		for (var i = 0; i < genTitles.length; i++) {
			var marker = new naver.maps.Marker({
				position: new naver.maps.LatLng(genLats[i], genLngs[i]),
				icon: {
					content: [
						'<div>',
						'<div class="bubble-text" style="background-color: #6A1AE8;">',
						'<img src="${GenIcoMarkerUrl}" width="22" height="22"/>',
						'<span class="bubble-text-span">' + genTitles[i] + '</span>',
						'</div>',
						'<div class="triangle-wrap">',
						'<div class="triangle-blue"></div>',
						'<div class="triangle-white" style="border-color: #6A1AE8 transparent transparent;"></div>',
						'</div>',
						'</div>'
					].join(''),
					anchor: new naver.maps.Point(16, 40)
				},
			});

			marker.addListener('mouseover', function (e) {
				highlightMarker(e.overlay);
			});
			marker.addListener('mouseout', function (e) {
				unhighlightMarker(e.overlay);
			});

			gendtlMarkers.push(marker);

		}

		// 정보창, 디테일마커 생성 ( 버그 text-info anchor 작동 안함. )
		// for (var btnId in btnValueMap) {
		// 	var btnData = btnValueMap[btnId];
		// 	var catData = catValueMap[btnData.category];
		//
		// 	for (var i = 0; i < btnData.title.length; i++) {
		// 		var marker = new naver.maps.Marker({
		// 			position: new naver.maps.LatLng(btnData.lats[i], btnData.lngs[i]),
		// 			icon: {
		// 				url: catData.iconImgUrl,
		// 			},
		// 		});
		// 		btnData.markers.push(marker);
		//
		// 		var content = [
		// 			'<div>',
		// 			'<div class="bubble-textInfo" style="background-color: ' + catData.colorCode + ';">',
		// 			// '<img src="' + buttonData.iconImgUrl + '" width="22" height="22" style="margin-left: 0px;"/>',
		// 			'<img src="' + btnData.iconImgUrl + '" width="22" height="22"/>',
		// 			'<span class="bubble-text-span">' + btnData.title[i] + '</span>',
		// 			'</div>',
		// 			'<div class="triangle-wrapInfo">',
		// 			'<div class="triangle-blue"></div>',
		// 			'<div class="triangle-white" style="border-color: ' + catData.colorCode + ' transparent transparent;"></div>',
		// 			'</div>',
		// 			'</div>'
		// 		].join('');
		//
		// 		btnData.infoWins.push(new naver.maps.InfoWindow({
		// 			content: content,
		// 			borderWidth: 0,
		// 			disableAnchor: true,
		// 			backgroundColor: 'transparent',
		// 			pixelOffset: new naver.maps.Point(-2, 7),
		// 		}));
		// 	}
		//
		// 	for (var i = 0; i < btnData.title.length; i++) {
		// 		var marker = new naver.maps.Marker({
		// 			position: new naver.maps.LatLng(btnData.lats[i], btnData.lngs[i]),
		// 			icon: {
		// 				content: [
		// 					'<div>',
		// 					'<div class="bubble-text" style="background-color: ' + catData.colorCode+ ';">',
		// 					'<img src="' + btnData.iconImgUrl+ '" width="22" height="22"/>',
		// 					'<span class="bubble-text-span">' + btnData.title[i]+ '</span>',
		// 					'</div>',
		// 					'<div class="triangle-wrapInfo">',
		// 					'<div class="triangle-blue"></div>',
		// 					'<div class="triangle-white" style="border-color: ' + catData.colorCode+ ' transparent transparent;"></div>',
		// 					'</div>',
		// 					'</div>'
		// 				].join(''),
		// 				anchor: new naver.maps.Point(70, 33)
		// 			},
		// 		});
		//
		// 		marker.addListener('mouseover', function (e) {
		// 			highlightMarker(e.overlay);
		// 		});
		// 		marker.addListener('mouseout', function (e) {
		// 			unhighlightMarker(e.overlay);
		// 		});
		//
		// 		btnData.dtlMarkers.push(marker);
		// 	}
		// }
		//
		var nulldata = [];
		var heatMap = new naver.maps.visualization.HeatMap({
			map: null,
			data: nulldata
		})

		// 줌 및 이동 시 히트맵 및 지역 마커 로직
		naver.maps.Event.addListener(map, 'idle', function () {
			var zoom = map.getZoom();

			$.each(btnValueMap, function (btnId, {dtlMarkers, markers}) {
				var btnState = $(btnId).val();
				if (zoom >= 16 && btnState === 'active') {
					updateMarkers(map, dtlMarkers);
					hideMarkers(map, markers);
				} else if (zoom >= 14 && btnState === 'active') {
					hideMarkers(map, dtlMarkers);
					hideMarkers(map, rgnMarkers);
					updateMarkers(map, markers);
				} else if (zoom >= 13) {
					updateMarkers(map, rgnMarkers);
					hideMarkers(map, markers);
				} else {
					hideMarkers(map, rgnMarkers);
					hideMarkers(map, dtlMarkers);
					hideMarkers(map, markers);
				}
			});

			// 줌이 13 이하일때는 히트맵을 켠다.
			if (13 >= zoom) {
				var allHeatmap = []
				$.each(btnValueMap, function (btnId, {latslngs}) {
					if ($(btnId).val() === 'active') {
						allHeatmap.push(...latslngs);
					}
				});

				heatMap.redraw()
				heatMap.setData(allHeatmap)
				startHeatMap(heatMap);
			} else {
				endHeatMap(heatMap)
			}
		});

		function updateMarkers(map, markers) {

			var mapBounds = map.getBounds();
			var marker, position;

			for (var i = 0; i < markers.length; i++) {

				marker = markers[i]
				position = marker.getPosition();

				if (mapBounds.hasLatLng(position)) {
					showMarker(map, marker);
				} else {
					hideMarker(map, marker);
				}
			}
		}

		function hideMarkers(map, markers) {

			var mapBounds = map.getBounds();
			var marker, position;

			for (var i = 0; i < markers.length; i++) {

				marker = markers[i]

				marker.setMap(null);

			}
		}

		function showMarker(map, marker) {

			marker.setMap(map);

		}

		function hideMarker(map, marker) {

			if (!marker.getMap()) return;
			marker.setMap(null);
		}

		// object key count
		var totalBtnCount = Object.keys(btnValueMap).length;
		var activeBtnCount = totalBtnCount

		//버튼 클릭 시 이벤트
		$.each(btnValueMap, function (btnId, {
			dtlMarkers,
			markers,
			activeImgUrl,
			inactiveImgUrl,
			imgTag
		}) {
			$(btnId).click(function () {
				var btn = $(btnId);
				var btnState = btn.val();
				var img = $(imgTag);
				if (btnState === 'inactive') {
					img.attr('src', activeImgUrl);
					btn.val('active');
					btnState = 'active'
					activeBtnCount += 1
				} else {
					img.attr('src', inactiveImgUrl);
					btn.val('inactive');
					btnState = 'inactive'
					activeBtnCount -= 1
				}

				var zoom = map.getZoom();
				var allHeatmap = [];

				if (zoom >= 16 && btnState === 'active') {
					updateMarkers(map, dtlMarkers);
				} else if (zoom >= 16 && btnState === 'inactive') {
					hideMarkers(map, dtlMarkers);
				} else if (zoom >= 14 && btnState === 'active') {
					updateMarkers(map, markers);
				} else if (zoom >= 14 && btnState === 'inactive') {
					hideMarkers(map, markers);
				}

				if (13 >= zoom) {
					$.each(btnValueMap, function (buttonId, {latslngs}) {
						if ($(buttonId).val() === 'active') {
							allHeatmap.push(...latslngs);
						}
					});
				}
				heatMap.redraw();
				heatMap.setData(allHeatmap);
				startHeatMap(heatMap);
			});
		});

		// 전체 (선택,해제) 버튼
		$(".all-btn").click(function (e) {
			var zoom = map.getZoom();
			var allHeatmap = [];

			var allBtn = $(e.currentTarget);
			var allBtnState = allBtn.val();

			// 이미 전체 같은 상태일 경우 함수 실행 안함
			if (allBtnState === 'active') {
				if (activeBtnCount === totalBtnCount) {
					return
				}
			} else {
				if (activeBtnCount === 0) {
					return
				}
			}


			$.each(btnValueMap, function (btnId, {
				dtlMarkers,
				markers,
				activeImgUrl,
				inactiveImgUrl,
				imgTag,
				latslngs
			}) {

				var btn = $(btnId);
				var btnState = btn.val();
				var img = $(imgTag);

				if (allBtnState === 'active') {
					if (btnState === 'active'){
						return
					}
					img.attr('src', activeImgUrl);
					btn.val('active');
					activeBtnCount = totalBtnCount;

					if (zoom >= 16) {
						updateMarkers(map, dtlMarkers);
					} else if (zoom >= 14) {
						updateMarkers(map, markers);
					}
					if (13 >= zoom) {
						allHeatmap.push(...latslngs);
					}
				} else {
					img.attr('src', inactiveImgUrl);
					btn.val('inactive');
					activeBtnCount = 0;

					if (zoom >= 16) {
						hideMarkers(map, dtlMarkers);
					}else if (zoom >= 14) {
						hideMarkers(map, markers);
					}
				}
			});
			heatMap.redraw();
			heatMap.setData(allHeatmap);
			startHeatMap(heatMap);
		});


		function getClickHandler(seq, markers, infoWins) {

			return function (e) {  // 마커를 클릭하는 부분
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

		for (var i = 0; i < cvsMarkers.length; i++) {
			naver.maps.Event.addListener(cvsMarkers[i], "click", getClickHandler(i, cvsMarkers, cvsInfoWins));
		}
		for (var i = 0; i < hospMarkers.length; i++) {
			naver.maps.Event.addListener(hospMarkers[i], "click", getClickHandler(i, hospMarkers, hospInfoWins));
		}
		for (var i = 0; i < bldgMarkers.length; i++) {
			naver.maps.Event.addListener(bldgMarkers[i], "click", getClickHandler(i, bldgMarkers, bldgInfoWins));
		}
		for (var i = 0; i < busMarkers.length; i++) {
			naver.maps.Event.addListener(busMarkers[i], "click", getClickHandler(i, busMarkers, busInfoWins));
		}
		for (var i = 0; i < fuelMarkers.length; i++) {
			naver.maps.Event.addListener(fuelMarkers[i], "click", getClickHandler(i, fuelMarkers, fuelInfoWins));
		}
		for (var i = 0; i < histpMarkers.length; i++) {
			naver.maps.Event.addListener(histpMarkers[i], "click", getClickHandler(i, histpMarkers, histpInfoWins));
		}
		for (var i = 0; i < busoMarkers.length; i++) {
			naver.maps.Event.addListener(busoMarkers[i], "click", getClickHandler(i, busoMarkers, busoInfoWins));
		}
		for (var i = 0; i < univMarkers.length; i++) {
			naver.maps.Event.addListener(univMarkers[i], "click", getClickHandler(i, univMarkers, univInfoWins));
		}
		for (var i = 0; i < genMarkers.length; i++) {
			naver.maps.Event.addListener(genMarkers[i], "click", getClickHandler(i, genMarkers, genInfoWins));
		}

		naver.maps.Event.addListener(map, "click", function (e) {
			if (infoWin) {
				infoWin.close();
				infoWin = null;
			}
		});

		naver.maps.Event.addListener(map, "zoom_changed", function (zoom) {

			if (infoWin && 13 >= zoom) {
				infoWin.close();
				infoWin = null;
			} else if (infoWin && zoom >= 16) {
				infoWin.close();
				infoWin = null;
			}

		});

		function startHeatMap(heatmap) {
			heatmap.setMap(map);
		};

		function endHeatMap(heatmap) {
			heatmap.setMap(null);
		};

		setTimeout(function () {
			map.setZoom(10);
		}, 500);


	}


</script>


<!-- / Scripts -->

<style>
	.rounded-marker {
		border-radius: 10px;
		background: #ffffff;
		border-style: solid;
		border-width: 1px;
		border-color: #a5a5a5;
		padding-top: 2px;
	}

	.rounded-marker-white {
		color: #565656;
		font-weight: 600;
	}

	.rounded-marker-title {
		padding-right: 10px;
		padding-left: 10px;
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
	}

	.bubble-textInfo{
		position: relative;
		bottom: 5px;
		cursor: default;
		color: white;
		max-width: 300px;
		width: fit-content;
		border: 2px solid white;
		border-radius: 15px;
		padding: 2px;
	}

	.bubble-text {
		position: relative;
		bottom: 2px;
		cursor: default;
		color: white;
		max-width: 300px;
		width: fit-content;
		display: flex;
		flex-direction: row;
		border: 2px solid white;
		border-radius: 15px;
		padding: 2px;
		left: -10px;
		align-items: center;
	}

	.bubble-text-span {
		cursor: default;
		margin: 0 10px 0 6px;
		font-size: 0.9rem;
		font-weight: 500;
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
	}

	.triangle-wrap {
		cursor: default;
		position: relative;
		right: -10px;
		bottom: 2px;
	}

	.triangle-wrapInfo {
		cursor: default;
		position: relative;
		right: -46%;
		bottom: 5px;
	}

	.triangle-blue {
		cursor: default;
		width: 0;
		height: 0;
		border-color: white transparent transparent;
		border-style: solid;
		border-width: 8px 6px 0;
		pointer-events: none;
	}
	.triangle-white {
		cursor: default;
		border-style: solid;
		border-width: 8px 6px 0;
		pointer-events: none;
		position: absolute;
		top: -3px;
	}

	.btn-box {
		border-bottom: 1px solid #AFAFAF;
		background-color: white;
		transition: background-color 0.3s ease-in-out;
	}

	.btn-box:last-child {
		border-bottom: none;
		background-color: #a3a3a3;
	}

	.btn-box:hover {
		background-color: #f1f2f2;
	}

	.btn-box:last-child:hover {
		background-color: #a3a3a3;
	}

	.btn-box button {
		margin: 0;
		padding: 0;
		border: none;
		cursor: pointer;
		background-color: white;
		transition: background-color 0.3s ease-in-out;
	}

	.btn-box button:hover {
		background-color: #f1f2f2;
	}

	.btn-box button:focus {
		outline: none;
	}

	.all-btn:active {
		transform: scale(0.9);
	}

</style>

<!-- / Page body -->





<!-- Functional tags -->



<!-- Closing tags -->

<common:base />
<common:pageClosing />
