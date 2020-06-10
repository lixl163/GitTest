package com.zsinfo.guoranhao;

import com.google.gson.Gson;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testGsonToJson(){
        Gson gson = new Gson();
        String tom = gson.toJson(new Student("tom", "11"));

    }

    public class Student{

        private String name;
        private String age;

        public Student(String name, String age) {
            this.name = name;
            this.age = age;
        }
    }
}