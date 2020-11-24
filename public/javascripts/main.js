

function setColor(color, cell) {
    document.getElementById(cell.id).style.backgroundColor = color;
}

function zoomIn(event, cell, sizeOfRows) {
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
}

function zoomOut() {
    var element = document.getElementsByClassName("cell");
    for (var i = 0; i < element.length; i++) {
        element[i].style.transform = "none";
    }
}