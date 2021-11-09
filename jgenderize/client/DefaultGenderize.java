package jgenderize.client;

import jgenderize.model.NameGender;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;

public class DefaultGenderize implements Genderize, Serializable {

    private final transient Client client = ClientBuilder.newClient();
    
    private static final String GENDERIZE_IO_API_URL = "https://api.genderize.io/";

    private static final GenericType<List<NameGender>> NAME_GENDER_LIST_TYPE
            = new GenericType<List<NameGender>>() {};

    /**
     * May be null if not using an API key.
     */
    private final String apiKey;

    public DefaultGenderize() {
        this.apiKey = null;
    }

    /**
     * @param apiKey Genderize.io API key.
     */
    public DefaultGenderize(String apiKey) {
        this.apiKey = apiKey;
    }

    public NameGender getGender(String name, Locale locale) {
        return getGenders(new String[]{name}, locale).get(0);
    }

    public List<NameGender> getGenders(String[] names, Locale locale) {
        if (names.length == 0) {
            return Collections.emptyList();
        }

        WebTarget target = client.target(GENDERIZE_IO_API_URL);
        for (int i = 0; i < names.length; i++) {
            target = target.queryParam(String.format("name[%d]", i), names[i]);
        }
        if (locale != null) {
            target = target.queryParam("country_id", locale.getCountry());
            target = target.queryParam("language_id", locale.getLanguage());
        }
        if (apiKey != null) {
            target = target.queryParam("apikey", apiKey);
        }
        Invocation.Builder builder = target.request(MediaType.APPLICATION_JSON_TYPE);

        return builder.get(NAME_GENDER_LIST_TYPE);
    }

    public NameGender getGender(String name) {
        return getGender(name, null);
    }

    public List<NameGender> getGenders(String... names) {
        return getGenders(names, null);
    }

}
