package sample;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collections;

class Cage {
    Label label;
    private ArrayList<Cell> cells = new ArrayList<Cell>();

    public Label getCageLabel() {
        return label;
    }

    //method used to highlight cells
    public void highlightCage(String s) {
        if(s.equals("highlight")) {
            for (Cell cell : cells) {
                cell.highlight("highlight");
            }
        } else if(s.equals("normal")) {
            for (Cell cell : cells) {
                cell.highlight("normal");
            }
        }
    }

    public ArrayList<Cell> getCells() {
        return cells;
    }

    //constructor used to make a cage with a certain colour, cell IDs and the number and operation that will be displayed in the label
    public Cage(Label numberOperationLabel, String[] cellsIds, Color colour) {
        this.label = numberOperationLabel;
        for(int i = 0; i < cellsIds.length; i++) {
            if(i == 0) {
                cells.add(new Cell(numberOperationLabel,Integer.parseInt(cellsIds[i]), colour, this));
            } else {
                cells.add(new Cell(Integer.parseInt(cellsIds[i]), colour, this));
            }
        }
    }


    public boolean isValid() {
        String target = label.getText().substring(0,label.getText().length()-1);
        String operator = label.getText().substring(label.getText().length()-1);

        switch (operator) {
            case "+":
                int sum = 0;
                for(Cell cell : cells) {
                    sum += cell.getCellValue();
                }
                if(sum != Integer.parseInt(target)) {
                    return false;
                }
                break;
            case "x":
                int product = 1;
                for(Cell cell : cells) {
                    product *= cell.getCellValue();
                }
                if(product != Integer.parseInt(target)) {
                    return false;
                }
                break;
            case "÷":
                ArrayList<Integer> cellValues = new ArrayList<>();
                for(int i=0; i<cells.size(); i++) {
                    cellValues.add(cells.get(i).getCellValue());
                }
                Integer maxValue = Collections.max(cellValues);
                cellValues.remove(maxValue);
                int productSmallerValues = 1;
                for(int i=0; i<cellValues.size(); i++) {
                    productSmallerValues *= cellValues.get(i);
                }
                if(maxValue / productSmallerValues != Integer.parseInt(target)) {
                    return false;
                }
                break;
            case "-":
                ArrayList<Integer> cellValues2 = new ArrayList<>();
                for(int i=0; i<cells.size(); i++) {
                    cellValues2.add(cells.get(i).getCellValue());
                }
                Integer maxValue2 = Collections.max(cellValues2);
                cellValues2.remove(maxValue2);
                int sumSmallerValues = 0;
                for(int i=0; i<cellValues2.size(); i++) {
                    sumSmallerValues += cellValues2.get(i);
                }
                if(maxValue2 - sumSmallerValues != Integer.parseInt(target)) {
                    return false;
                }
                break;
        }

        return true;
    }

    //method to print cell
    public void printCells() {
        for(int i = 0; i<cells.size(); i++) {
            System.out.println(cells.get(i).operationNumberLabel());
            System.out.println(cells.get(i).getCellID());
            System.out.println();
        }
    }
}
