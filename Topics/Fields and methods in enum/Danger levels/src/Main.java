enum DangerLevel {
    HIGH(3),
    MEDIUM(2),
    LOW(1);

    private final int levelOfDanger;

    DangerLevel(int levelOfDanger) {
        this.levelOfDanger = levelOfDanger;
    }

    public int getLevel() {
        return levelOfDanger;
    }
}