package ru.yandex.qatools.allure.teamcity;

/**
 * eroshenkoam
 * 6/20/14
 */
public enum ReportBuildPolicy {

    ALWAYS("For all builds"),

    WITH_PROBLEMS("For build with problems"),

    FAILED("For failed builds");

    private String title;

    ReportBuildPolicy(String title) {
        this.title = title;
    }

    public String title() {
        return title;
    }
}
