package util;

import java.time.OffsetDateTime;

public class SteamProfile {
    private final String username;

    private final String avatarIcon;

    private final int hoursSSL;

    private final OffsetDateTime expireAt;

    protected SteamProfile(String username, String avatarIcon, int hoursSSL) {
        this.username = username;
        this.avatarIcon = avatarIcon;
        this.hoursSSL = hoursSSL;
        this.expireAt = OffsetDateTime.now().plusMinutes(STATIC.CACHE_EXPIRE_AFTER);
    }

    public String getUsername() {
        return username;
    }
    public String getAvatarIcon() {
        return avatarIcon;
    }

    public int getHoursSSL() {
        return hoursSSL;
    }
    public boolean isExpired() {
        return OffsetDateTime.now().isAfter(expireAt);
    }
}
