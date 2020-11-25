

function setColor(color, cell) {
    document.getElementById(cell.id).classList.add(color === "red" ? "red" : "yellow")
}

function zoomIn(event, cell, sizeOfRows, previewRowIdx) {
    var cells = document.getElementsByClassName("cell");
    var colOfHoveredCell = cell.id.substr(cell.id.indexOf(",") + 1, 1);
    var rowCounter = 0;

        for(var i = 0; i < cells.length; i++) {
        var col = cells[i].id.substr(cells[i].id.indexOf(",") + 1, 1);
        var currentRow = cells[i].id.substr(cells[i].id.indexOf(",") - 1, 1);
        if (rowCounter.toString() === currentRow && colOfHoveredCell === col && rowCounter <= sizeOfRows) {
            cells[i].style.transform = 'scale(1.5)';
            rowCounter += 1;
        }

    }

    var previewColIdx = cell.id.substr(cell.id.indexOf(",") + 1, 1);
    var cellToBeHighlighted = document.getElementById("cell-(" + previewRowIdx + ","+ previewColIdx + ")");
    cellToBeHighlighted.classList.replace("unset", "preview");
    cellToBeHighlighted.style.backgroundColor = 'darkred';


}

function zoomOut() {
    var element = document.getElementsByClassName("cell");
    for (var i = 0; i < element.length; i++) {
        element[i].style.transform = "none";

        if(element[i].className === "cell preview") {
            element[i].style.backgroundColor = '#f7eed5';
        }
    }
}