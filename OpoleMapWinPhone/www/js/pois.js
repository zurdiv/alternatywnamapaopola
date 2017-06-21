var poi;

L.NumberedDivIcon = L.Icon.extend({
    options: {
        iconUrl: '',
        number: '',
        shadowUrl: null,
        iconSize: [40, 50],
        iconAnchor: [13, 40],
        className: 'leaflet-div-icon'
    },

    createIcon: function () {
        var div = document.createElement('div');
        var img = this._createImg(this.options['iconUrl']);
        $(img).addClass("marker-image");
        var numdiv = document.createElement('div');
        numdiv.setAttribute ( "class", "number" );
        numdiv.innerHTML = this.options['number'] || '';
        div.appendChild ( img );
        div.appendChild ( numdiv );
        this._setIconStyles(div, 'icon');
        return div;
    },

    createShadow: function () {
        return null;
    }
});

poi = (function() {

    function setMarkerIconDirNormal() {
        if (this.data.id > 75) this.iconDir = "img/markers/marker_poruszaj_sie.png";
        else if (this.data.id > 63) this.iconDir = "img/markers/marker_rozerwij_sie.png";
        else if (this.data.id > 53) this.iconDir = "img/markers/marker_zrelaksuj_sie.png";
        else if (this.data.id > 37) this.iconDir = "img/markers/marker_posmakuj.png";
        else if (this.data.id > 15) this.iconDir = "img/markers/marker_odwiedz.png";
        else this.iconDir = "img/markers/marker_zobacz.png";
    }

    function setMarkerIconDirSelected() {
        if (this.data.id > 75) this.iconDir = "img/markers/marker_poruszaj_sie_zaznaczony.png";
        else if (this.data.id > 63) this.iconDir = "img/markers/marker_rozerwij_sie_zaznaczony.png";
        else if (this.data.id > 53) this.iconDir = "img/markers/marker_zrelaksuj_sie_zaznaczony.png";
        else if (this.data.id > 37) this.iconDir = "img/markers/marker_posmakuj_zaznaczony.png";
        else if (this.data.id > 15) this.iconDir = "img/markers/marker_odwiedz_zaznaczony.png";
        else this.iconDir = "img/markers/marker_zobacz_zaznaczony.png";
    }

    function setData(data) {
        this.data = data;
    }

    function createIcon(mode) {
        this.icon = new L.NumberedDivIcon({
            number: this.data.id - subIndex[mode],
            iconUrl: this.iconDir,
            iconSize: new L.Point(40, 50),
            iconAnchor: new L.Point(13, 40)
        });
    }

    function setIcon() {
        this.marker.setIcon(this.icon);
    }

    function setMarker() {
        this.marker = L.marker([this.data.pos[0], this.data.pos[1]], {
            icon: this.icon
        });
    }

    function getImage(self) {
        if (self.data.id == 75 || self.data.id == 83) return null;
        var photo = $('<img />', {
            src: 'img/photos/' + self.data.id + ".jpg"
        });
        return photo;
    }

    function getRateDiv(self) {
        if(self.data.rate == 0)return null;
        if(Math.floor(self.data.rate) == self.data.rate) self.data.rate = self.data.rate+".0";
        var customDiv = $("<div>").addClass("");
        customDiv.append($('<img />', {src: 'img/star.png'}).addClass("modal-rate-star"));
        customDiv.append($("<div>" + self.data.rate + "</div>").addClass("number-rate"));
        return customDiv.addClass("modal-rate-star");
    }

    function getFeature(tag) {
        if (tag.length < 2) return null;
        tag = tag.replace(".","");
        var photo = $('<img />', {
            src: 'img/features/'+tag+'.png'
        });
        return photo.addClass("modal-icon");
    }

    function getFeatures(self){
        var _t = [];
        for(var tag in self.data.tags) _t.push(getFeature( self.data.tags[tag]));
        return _t;
    }

    function replaceSpacetoNotBraking(text){
        return text.replace(" i ", " i\u00A0")
                .replace(" w "," w\u00A0")
                .replace(" W "," W\u00A0")
                .replace(" Z "," Z\u00A0")
                .replace(" o "," o\u00A0")
                .replace(" O "," O\u00A0")
                .replace(" z "," z\u00A0");
    }

    function setModalDialogDetails(self) {
        this.modalDialog.find(".modal-title:first").empty();
        this.modalDialog.find(".modal-title:first").append(self.data.name);
        this.modalDialog.find(".modal-desc:first").empty();
        this.modalDialog.find(".modal-desc:first").append(replaceSpacetoNotBraking.call(this, self.data.description));
        this.modalDialog.find(".modal-id:first").empty();
        this.modalDialog.find(".modal-id:first").append(self.data.id - subIndex[window.mode] + ".");
        this.modalDialog.find(".modal-img:first").empty();
        this.modalDialog.find(".modal-img:first").append(getImage(self));
        this.modalDialog.find(".modal-rate:first").empty();
        this.modalDialog.find(".modal-rate:first").append(getRateDiv(self));
        this.modalDialog.find(".modal-icons:first").empty();
        this.modalDialog.find(".modal-icons:first").append(getFeatures.call(this, self));
    }

    function setMarkerClickListener(self) {
        this.marker.on("click", function () {
            this.modalDialog = $(".js-modal-template").children().clone();
            this.modalDialog.on("click", function () {
                return $(this).remove();
            });
            setModalDialogDetails.call(this, self);
            return $(".page").append(this.modalDialog);
        });
    }

    function poi(data) { // constructor
        var self = this;
        setData.call(this, data);
        setMarkerIconDirNormal.call(this, data);
        createIcon.call(this);
        setMarker.call(this);
        setMarkerClickListener.call(this, self);
    }

    poi.prototype.show = function() {
        this.hide();
        setMarkerIconDirSelected.call(this);
        createIcon.call(this, window.mode);
        setIcon.call(this);
        return this.marker.addTo(map);
    };

    poi.prototype.hide = function() {
        if (map.hasLayer(this.marker)) {
            return map.removeLayer(this.marker);
        }
    };

    poi.prototype.select = function(){
        for(var i = 0; i < 85; i++){
            var poi = window.pois[i];
            poi.normalizeIcon();
        }
        setMarkerIconDirSelected.call(this);
        createIcon.call(this, window.mode);
        setIcon.call(this);
    };

    poi.prototype.normalizeIcon = function(){
        setMarkerIconDirNormal.call(this);
        createIcon.call(this, window.mode);
        setIcon.call(this);
    };

  return poi;
})();

function positionSelected(id){
    var poi = window.pois[parseInt(id)-1];
    poi.select();
    window.map.panTo(poi.data.pos);
}

function loadPOIS(mode) {
    window.mode = mode;
    var partial = placeTypePartial[mode];

    for(var i = 0; i < window.pois.length; i++){
        window.pois[i].hide();
    }

    for(var i = partial[0]; i < partial[1]; i++){
        window.pois[i].show();
    }
}
