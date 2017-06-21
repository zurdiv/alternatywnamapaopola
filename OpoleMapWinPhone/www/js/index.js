window.pois = [];

var minZoom = 14;
var maxZoom = 17;

var categoriesNumbers = {
	zobacz: 0,
	odwiedz: 1,
	posmakuj: 2,
	zrelaksuj_sie: 3,
	rozwerwij_sie: 4,
	poruszaj_sie: 5,
	wszystkie: 6
};

var placeTypePartial = {
	0: [0,15],
	1: [15,37],
	2: [37,53],
	3: [53, 63],
	4: [63, 75],
	5: [75, 85],
	6: [0, 85]
};

var subIndex = {
	0: 0,
	1: 15,
	2: 37,
	3: 53,
	4: 63,
	5: 75,
	6: 0
};

var preferedPoisAndroid = {
	"pl-PL": poisPl,
	"de-DE": poisDe,
	"es-ES": poisEs,
	"uk-UA": poisUk,
	"en-US": poisEn,
	"en-UK": poisEn
};

var categoriesDirAndroid = {
	"pl-PL": "pl",
	"de-DE": "de",
	"es-ES": "es",
	"uk-UA": "uk",
	"en-UK": "en",
	"en-US": "en"
};

var categoriesDirWindowsPhone = {
	"PL": "pl",
	"DE": "de",
	"ES": "es",
	"UA": "uk",
	"UK": "en",
	"US": "en"
};

var preferedPoisWindowsPhone = {
	"PL": poisPl,
	"DE": poisDe,
	"ES": poisEs,
	"UA": poisPl,
	"UK": poisEn,
	"US": poisEn
};

var gps = {
	marker: null,

	icon: L.icon({
		iconUrl: 'img/markers/my_location.png',
		iconSize: [40, 50],
		iconAnchor: [13, 40]
	}),

	locate: function(){
		navigator.geolocation.getCurrentPosition(gps.onSuccess, gps.onError);
	},

	createMarker: function(lat, lon){
		if(gps.marker) window.map.removeLayer(gps.marker);
		gps.marker = L.marker([lat, lon], {
			icon: gps.icon
		});
		window.map.addLayer(gps.marker);
	},

	onSuccess: function(position) {
		gps.createMarker(position.coords.latitude, position.coords.longitude);
	},

	onError: function(error) {
		alert('code: '    + error.code    + '\n' +
			  'message: ' + error.message + '\n');
	}
};

var app = {

	initialize: function() {
		this.bindEvents();
	},

	bindEvents: function() {
		document.addEventListener('deviceready', this.onDeviceReady,
			false);
	},

	createMap: function (layer) {
		var map = L.map('map', {
			center: [50.663224, 17.930206],
			zoom: 15,
			minZoom: minZoom,
			maxZoom: maxZoom,
			doubleClickZoom: false,
			zoomControl: true,
			layers: [layer]
		});
		window.map = map;
		return map;
	},

	createLayer: function () {
		return L.tileLayer('map/{z}/{x}/{y}.png', {
			minZoom: minZoom,
			maxZoom: maxZoom,
			attribution: 'AlternatywnaMapaOpola',
			tms: true,
		});
	},

	createBounds: function () {
		var southWest = [50.650076, 17.909789];
		var northEast = [50.676372, 17.950623];
		var bounds = new L.LatLngBounds(southWest, northEast);
		return bounds;
	},

	loadPois: function () {
		if(window.language in preferedPoisAndroid)
			var pois = preferedPoisAndroid[window.language];
		else
			var pois = poisEn;
		return pois;
	},

	initAllPois: function (pois) {
		for (var i = 0; i < 85; i++) {
			var p = pois[i];
			var pt = new poi(p);
			window.pois.push(pt);
		}
	},

	setCategoryOnClickListener: function(category){
		$("#"+category).on("click", function(){
			loadPOIS(categoriesNumbers[category]);
			loadPlaces(categoriesNumbers[category]);
		});
	},

	updateButtons: function () {
		if(window.language in preferedPoisAndroid)
			var lan = categoriesDirAndroid[window.language];
		else
			var lan = "en";
		$(".button").each(function (index) {
			var src = "img/categories/" +
				lan + "/"
				+ $(this).attr("id") +
					".png";
			$(this).attr('src', src);
		});
	},

	initElements: function () {
		var layer = app.createLayer();
		var map = app.createMap(app.createLayer());

		map.setMaxBounds(app.createBounds());
		app.initAllPois(app.loadPois());
		layer.addTo(map);
		app.updateButtons();
		for (var category in categoriesNumbers) app.setCategoryOnClickListener(category);
		gps.locate();
	},

	getLanguageAndStart: function () {
		navigator.globalization.getPreferredLanguage(
			function (language) {
				window.language = language.value;
				app.initElements();
			},
			function () {
				alert('Error getting language\n');
			}
		);
	},

	onDeviceReady: function() {
		app.getLanguageAndStart();
	},

	receivedEvent: function(id) {
		console.log('Received Event: ' + id);
	}
};

app.initialize();

setTimeout(function() {
	$('#splashscreen').fadeOut(500);
}, 1000);


