package vn.viettel.core.db.entity.commonEnum;

public enum Channel {
    MEMBER_FAMILY_STRUCTURE(1, "Family Structure"),
    MEMBER_FAVORITE_STYLE(8, "Favorite Style"),
    MEMBER_PROFESSION(17, "Profession"),
    MEMBER_OCCUPATION(26, "Occupation"),
    MEMBER_WORRIES(28, "Your worries"),
    MEMBER_KNOWN_SOURCE(51, "How do you know this website from ?");

    private long id;

    private String description;

    Channel(long id, String description) {
        this.id = id;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Channel getChannelById() {
        for(Channel e : values()) {
            if(e.id == id) return e;
        }
        return null;
    }
}
