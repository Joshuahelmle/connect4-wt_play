
var player1 = true;


function setColor(row, col,preview = false) {
    //document.getElementById(cell.id).style.backgroundColor = color;
    //console.log(`cell-(${row},${col})`);
    document.getElementById(`cell-(${row},${col})`).classList.add(player1 ? "red" : "yellow")
    if(!preview) {
        player1 = !player1;
    }
}

function zoomIn(event, cell, sizeOfRows, previewRowIdx) {
    let cells = document.getElementsByClassName("cell");
    let colOfHoveredCell = cell.id.substr(cell.id.indexOf(",") + 1, 1);
    let rowCounter = 0;

        for(let i = 0; i < cells.length; i++) {
        let col = cells[i].id.substr(cells[i].id.indexOf(",") + 1, 1);
        let currentRow = cells[i].id.substr(cells[i].id.indexOf(",") - 1, 1);
        if (rowCounter.toString() === currentRow && colOfHoveredCell === col && rowCounter <= sizeOfRows) {
            cells[i].classList.add('scaled')
            rowCounter += 1;
        }

    }

    let cellToBeHighlighted = document.getElementById("cell-(" + previewRowIdx + ","+ colOfHoveredCell + ")");
    cellToBeHighlighted.classList.replace("unset", "preview");
    setColor(previewRowIdx,colOfHoveredCell,true)


}

function zoomOut() {
    let element = document.getElementsByClassName("cell");
    for (let i = 0; i < element.length; i++) {
        if(element[i].classList.contains('preview')){
            element[i].classList.remove('preview', player1 ? 'red' : 'yellow');
            element[i].classList.add('unset');
        }
        element[i].classList.remove('scaled');
    }
}