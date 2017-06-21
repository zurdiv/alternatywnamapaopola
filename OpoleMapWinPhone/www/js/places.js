function setOnPlaceClickListeners() {
    $(".place").on("click", function () {
        positionSelected($(this).attr("id"));
        $("#places-panel").panel("close");
    });
}

function prepareListView() {
    var listView = $(".places-listview");
    listView.empty();
    return listView;
}

function createRow(p) {
    var trTemplate = $('.place-template-row').clone().removeClass("place-template-row")
        .addClass("place").css("display", "block").attr("id", p.id).attr("data-role", "close");
    trTemplate.append(p.id - subIndex[window.mode] + ". " + p.name);
    trTemplate.attr("id", p.id);
    return trTemplate;
}

function addRowToListView(listView, trTemplate) {
    listView.append(trTemplate);
}

function loadPlaces(mode) {
	var partial = placeTypePartial[mode];
	var pois = app.loadPois();
    var listView = prepareListView();

    for (var i = partial[0]; i < partial[1]; i++) {
        addRowToListView(listView, createRow(pois[i]));
	}
    setOnPlaceClickListeners();
}
