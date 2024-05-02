package com.lipengyu.study.流流流;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Test {
    public static void main(String[] args) {
        Student[] students = new Student[5];
        for (int i = 0; i < 5; i++) {
            students[i] = new Student(18 + i, 0 + i, "lpy" + i);
        }
        List<Student> list = new ArrayList<>();
        // Collections.addAll(list, students);
        for (Student s: students) {
            list.add(s);
        }
        Map<Integer, Student> collect2 = list.stream().collect(Collectors.toMap(e -> e.id, e -> e));
        System.out.println(collect2);
    }
}

class Student {
    int age;
    int id;

    String name;

    public Student(int age, int id, String name) {
        this.age = age;
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Student{" +
                "age=" + age +
                ", id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
