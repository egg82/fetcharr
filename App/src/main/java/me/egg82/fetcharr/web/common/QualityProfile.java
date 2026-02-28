package me.egg82.fetcharr.web.common;

import kong.unirest.core.json.JSONObject;
import me.egg82.fetcharr.web.ArrAPI;
import me.egg82.fetcharr.web.NullAPI;
import org.jetbrains.annotations.NotNull;

public class QualityProfile extends APIObject {
    public static final QualityProfile UNKNOWN = new QualityProfile();

    public QualityProfile(@NotNull JSONObject obj, @NotNull ArrAPI api) {
        super(obj, api);
    }

    private QualityProfile() {
        super(new JSONObject(), NullAPI.INSTANCE);
    }

    //public boolean unknown() { return id < 0; }

    public static class Quality extends APIObject {
        public static final QualityProfile.Quality UNKNOWN = new QualityProfile.Quality();

        public Quality(@NotNull JSONObject obj, @NotNull ArrAPI api) {
            super(obj, api);
        }

        private Quality() {
            super(new JSONObject(), NullAPI.INSTANCE);
        }

        // public boolean unknown() { return id < 0; }
    }
}
