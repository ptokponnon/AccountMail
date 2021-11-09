package jgenderize.test;

import jgenderize.GenderizeIoAPI;
import jgenderize.client.Genderize;
import jgenderize.model.NameGender;
import java.util.List;
import java.util.Locale;
import jakarta.ws.rs.NotAuthorizedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GenderizeIoAPITest {

    @Test
    public void testGetSingleNameGender() {
        Genderize api = GenderizeIoAPI.create();
        NameGender gender = api.getGender("Kim");
        Assertions.assertTrue(gender.isFemale());
    }

    @Test
    public void testGetSingleNameGenderByLocalization() {
        Genderize api = GenderizeIoAPI.create();
        NameGender gender = api.getGender("Kim", new Locale("da", "DK"));
        Assertions.assertTrue(gender.isMale());
    }

    @Test
    public void testGetMultiNameGenderByLocalization() {
        Genderize api = GenderizeIoAPI.create();
        List<NameGender> genders = api.getGenders(new String[]{"Robson", "Gilmar", "Marlise"}, new Locale("pt", "BR"));
        Assertions.assertEquals(3, genders.size());
        Assertions.assertTrue(genders.get(0).isMale());
        Assertions.assertTrue(genders.get(1).isMale());
        Assertions.assertTrue(genders.get(2).isFemale());
    }

    @Test
    public void testGetMultiNameGender() {
        Genderize api = GenderizeIoAPI.create();
        List<NameGender> genders = api.getGenders("Robson", "John", "Anna");
        Assertions.assertEquals(3, genders.size());
        Assertions.assertTrue(genders.get(0).isMale());
        Assertions.assertTrue(genders.get(1).isMale());
        Assertions.assertTrue(genders.get(2).isFemale());
    }

    @Test
    public void testGetSingleNameBadAPIKey() {
        Assertions.assertThrows(NotAuthorizedException.class, () -> GenderizeIoAPI.create("invalid_api_key").getGender("Kim"));
    }

    @Test
    public void testGetNoNames() {
        List<NameGender> genders = GenderizeIoAPI.create().getGenders();
        Assertions.assertEquals(0, genders.size());
    }

    @Test
    public void testGetSingleNameGenderWithMultiMethod() {
        List<NameGender> genders = GenderizeIoAPI.create().getGenders("Robson");
        Assertions.assertEquals(1, genders.size());
        Assertions.assertTrue(genders.get(0).isMale());
    }

    @Test
    public void testGetSingleNameUnknownGender() {
        NameGender gender = GenderizeIoAPI.create().getGender("Thunderhorse");
        Assertions.assertFalse(gender.isFemale());
        Assertions.assertFalse(gender.isMale());
    }
}
