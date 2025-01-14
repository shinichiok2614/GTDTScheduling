package org.example;

import model.Course;
import model.Data;
import model.Department;

import java.util.ArrayList;

public class Schedule {
    private ArrayList<ClassByBit> listClass;
    private boolean isFitnessChanged = true;
    private double fitness = -1;
    private int classNumb = 0;
    private int numbOfConflicts = 0;
    private Data data;

    public Data getData() {
        return data;
    }
    public void setData (Data data) {
        this.data = data;
    }

    public Schedule(Data data) {
        this.data = data;
        listClass = new ArrayList<ClassByBit>(data.getNumberOfClasses());
    }

    /* Add condition when initialize - Each class has a difference course */
    public Schedule initialize() {
        new ArrayList<Department>(data.getListDept()).forEach(dept -> {
            dept.getListCourse().forEach(course -> {
                ClassByBit newClass = new ClassByBit(toBinaryStr(classNumb++, 3), toBinaryStr(indexOfCourseFromData(data, course), 3));
                newClass.setMtBit(toBinaryStr((int)(Math.random() * data.getListMT().size()), 2));
                newClass.setRoomBit(toBinaryStr((int)(Math.random() * data.getListRoom().size()), 2));
                newClass.setInstructorBit(toBinaryStr((int)(Math.random() * data.getListInstructor().size()), 2));

                listClass.add(newClass);
            });
        });

        return this;
    }

    private int indexOfCourseFromData(Data data, Course course) {
        int index = data.getListCourse().indexOf(course);
        return index;
    }


    public int getNumbOfConflicts() {
        return this.numbOfConflicts;
    }

    public ArrayList<ClassByBit> getListClass () {
        isFitnessChanged = true;
        return listClass;
    }

    public double getFitness() {
        if (isFitnessChanged == true) {
            fitness = calculteFitness();
            isFitnessChanged = false;
        }
        return fitness;
    }

    private double calculteFitness() {
        numbOfConflicts = 0;

        listClass.forEach(x ->{
            int indexRoom = Integer.parseInt(x.getRoomBit(), 2);
            int seatingCapacity = data.getListRoom().get(indexRoom).getSeatingCapacity();
            int indexCourse = Integer.parseInt(x.getCourseBit(), 2);
            int maxNumbOfStudents = data.getListCourse().get(indexCourse).getMaxNumbOfStudents();
            if (seatingCapacity < maxNumbOfStudents) numbOfConflicts++;

            listClass.stream().filter(y -> listClass.indexOf(y) >= listClass.indexOf(x)).forEach(y -> {
                //if (x.getCourseBit().equals(y.getCourseBit())) numbOfConflicts++;
                if (x.getMtBit().equals(y.getMtBit()) && (!x.getIdBit().equals(y.getIdBit())) ) {
                    if(x.getRoomBit().equals(y.getRoomBit())) numbOfConflicts++;
                    if(x.getInstructorBit().equals(y.getInstructorBit())) numbOfConflicts++;
                }
            });
        });

        return 1/ (double) (numbOfConflicts + 1);
    }

    // to Binary String
    public String toBinaryStr(int n, int length) {
        String binaryStr = Integer.toBinaryString(n);
        if (binaryStr.length() < length) {
            int sub = length - binaryStr.length();
            //Add sub bit(s) 0
            for (int i = 0 ; i < sub; i++) {
                binaryStr = "0" + binaryStr;
            }
        }
        return binaryStr;
    }

    @Override
    public String toString() {
        String chromosome = "";
        for (int i = 0; i < listClass.size(); i++) {
            chromosome += listClass.get(i).toString() + " ";
        }
        return chromosome;
    }
}
