package util;

public class LeaderboardEntry{
    protected final String steamID;
    protected final int rank;
    protected final int xp;

    protected LeaderboardEntry(String steamID, int rank, int xp) {
        this.steamID = steamID;
        this.rank = rank;
        this.xp = xp;
    }

    public String getSteamID() {
        return steamID;
    }
    public int getRank() {
        return rank;
    }
    public int getXp() {
        return xp;
    }
}
