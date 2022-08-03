package sample;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;

class Grid {
    private int size;
    private ArrayList<Cage> cages = new ArrayList<Cage>();
    private Cell[][] cells;
    private double r = 0;
    private double g = 0;
    private double b = 0;

    //Ids of modified cells. (Redo, checkIfSolved). Possibly change type to Cell instead of Integer to hold whole cells.
    private LinkedHashSet<Integer> modifiedCells = new LinkedHashSet<Integer>();

    //A linked hash set of the cells that will be removed when using the redo and undo buttons
    private LinkedHashSet<Cell> removedCells = new LinkedHashSet<Cell>();
    private HashSet<Integer> wrongRows = new HashSet<>();
    private HashSet<Integer> wrongColumns = new HashSet<>();
    private ArrayList<Cage> wrongCages = new ArrayList<>();
    private boolean won = false;

    public Grid() {
        this.size = 0;
    }

    public Grid(int size) {
        this.size = size;
        cells = new Cell[size][size];
    }

    public int getSize() {
        return size;
    }

    public Cell[][] getGridCells() {
        return cells;
    }

    public void addModifiedCell(int cellId) {
        modifiedCells.add(cellId);
        if(modifiedCells.size() == size*size) {
            isSolved();
        }
    }

    public void removeModifiedCell(int cellId) {
        modifiedCells.remove(cellId);
    }

    public LinkedHashSet<Integer> getModifiedCells() {
        return modifiedCells;
    }
    public void changeColour(String s) {
        if(s.equals("win") || s.equals("normal")) {
            for(int i=0; i<size; i++) {
                for(int j=0; j<size; j++) {
                    cells[i][j].highlight(s);
                }
            }
        } else if(s.equals("highlight")) {
            for(Cage cage : wrongCages) {
                cage.highlightCage(s);
            }
            for (Integer x : wrongColumns) {
                for (int i = 0; i < size; i++) {
                    cells[x][i].highlight(s);
                }
            }
            for (Integer x : wrongRows) {
                for (int i = 0; i < size; i++) {
                    cells[i][x].highlight(s);
                }
            }
        }
    }

    public void isSolved() {
        changeColour("normal");
        won = false;

        wrongRows = new HashSet<>();
        wrongColumns = new HashSet<>();
        wrongCages = new ArrayList<>();

        int[] allowedNum = new int[size];
        for(int i=0; i<size; i++) {
            Arrays.fill(allowedNum, 0);
            for(int j=0; j<size; j++) {
                try {
                    allowedNum[cells[i][j].getCellValue()-1] += 1;
                } catch(ArrayIndexOutOfBoundsException e) {
                    wrongColumns.add(i);
                }
            }
            for(int j=0; j<size; j++) {
                if(allowedNum[j] != 1) {
                    wrongColumns.add(i);
                }
            }
        }

        for(int i=0; i<size; i++) {
            Arrays.fill(allowedNum, 0);
            for(int j=0; j<size; j++) {
                try {
                    allowedNum[cells[j][i].getCellValue()-1] += 1;
                } catch(ArrayIndexOutOfBoundsException e) {
                    wrongRows.add(i);
                }
            }
            for(int j=0; j<size; j++) {
                if(allowedNum[j] != 1) {
                    wrongRows.add(i);
                }
            }
        }

        for(Cage cage : cages) {
            if(!cage.isValid()) {
                wrongCages.add(cage);
            }
        }
        if(wrongRows.size() == 0 && wrongColumns.size() == 0 && wrongCages.size() ==0) {
            changeColour("win");
            won = true;
        }
    }

    public void clearGrid() {
        for(int i=0; i<size; i++) {
            for(int j=0; j<size; j++) {
                cells[i][j].setCellValue("0");
                cells[i][j].setText("");
            }
        }
    }

    public void undo() throws CloneNotSupportedException{
        if(modifiedCells.size() != 0) {
            //Determine, using cellId location of the cell in the cells array to edit it.
            int lastId = (int) (modifiedCells.toArray()[modifiedCells.size() - 1]) - 1;
            removedCells.add((Cell) cells[lastId / size][lastId % size].clone());
            cells[lastId / size][lastId % size].setCellValue("0");
            cells[lastId / size][lastId % size].setText("");
        }
    }

    public void redo(){
        if(removedCells.size() != 0) {
            Cell removedCell = (Cell) (removedCells.toArray()[removedCells.size()-1]);
            int lastId = removedCell.getCellID() - 1;
            cells[lastId / size][lastId % size].setCellValue(String.valueOf(removedCell.getCellValue()));
            cells[lastId / size][lastId % size].setText(removedCell.getText());
            removedCells.remove(removedCell);
        }
    }

    public boolean isFormatCorrect(String textInput) {
        textInput = textInput.replaceAll("\n", " ");
        String[] stringContents = textInput.split( " ");
        int size = 0;
        Integer[] possibleSizes = new Integer[] {16,25,36,49,64};
        HashSet<Integer> cellsIdsToConfirm = new HashSet<Integer>();

        for(int i = 0; i < stringContents.length; i++) {
            if(i%2 == 0) {
                if(!stringContents[i].matches("^[1-9][0-9]*[x÷+\\-]?")) {
                    System.out.println(1);
                    return false;
                }
                System.out.println("OK");
            } else {
                String[] cellsIds = stringContents[i].split(",");
                size += cellsIds.length;
                for(int j=0; j<cellsIds.length; j++) {
                    cellsIdsToConfirm.add(Integer.parseInt(cellsIds[j]));
                    if(!cellsIds[j].matches("^[1-9][0-9]*$")) {
                        System.out.println(2);
                        return false;
                    }
                }
            }
        }
        //Size exceeding possible size.
        if(!Arrays.asList(possibleSizes).contains(size)) {
            System.out.println(3);
            return false;
        }
        //Two identical ids.
        if(cellsIdsToConfirm.size() != size) {
            System.out.println(4);
            return false;
        }
        //CellId exceeding the possible range.
        for(int x : cellsIdsToConfirm) {
            if(x < 1 || x > size) {
                System.out.println(5);
                return false;
            }
        }
        return true;
    }

    public void readFromString(String textInput) {
        cages.clear();
        modifiedCells.clear();
        removedCells.clear();

        Label cageLabel = new Label();
        textInput = textInput.replaceAll("\n", " ");
        String[] stringContents = textInput.split( " ");

        for(int i = 0; i < stringContents.length; i++) {
            if(i%2 == 0) {
                cageLabel = new Label(stringContents[i]);
                r = (r + 0.8) % 1;
                b = (b + 0.1) % 1;
                g = (g + 0.2) % 1;
            } else {
                String[] cellsIds = stringContents[i].split(",");
                size += cellsIds.length;
                cages.add(new Cage(cageLabel, cellsIds, new Color(r,g,b,1)));
            }
        }
        size = (int) Math.sqrt((double)size);
        cells = new Cell[size][size];

        for(int i = 0; i < cages.size(); i++) {
            ArrayList<Cell> currentCage = cages.get(i).getCells();
            for(int j = 0; j < currentCage.size(); j++) {
                int cellId = currentCage.get(j).getCellID();
                cells[(cellId-1)/size][(cellId-1)%size] = currentCage.get(j);
            }
        }
    }
}