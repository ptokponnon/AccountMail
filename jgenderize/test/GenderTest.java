package jgenderize.test;

import jgenderize.model.Gender;
import jgenderize.model.NameGender;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class GenderTest {

    @Test
    public void testNullGender() {
        NameGender name = new NameGender();
        name.setGender("null");
        Assertions.assertEquals(Gender.NULL, name.getGenderType());
    }

    @Test
    public void testFemaleGender() {
        NameGender name = new NameGender();
        name.setGender("female");
        Assertions.assertEquals(Gender.FEMALE, name.getGenderType());
    }

    @Test
    public void testMaleGender() {
        NameGender name = new NameGender();
        name.setGender("male");
        Assertions.assertEquals(Gender.MALE, name.getGenderType());
    }
}
