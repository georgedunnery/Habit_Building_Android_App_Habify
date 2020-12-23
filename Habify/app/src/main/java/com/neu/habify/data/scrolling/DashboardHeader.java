package com.neu.habify.data.scrolling;

public class DashboardHeader {

    // No id for this: it should be generated based on info in daily habits, not stored in database
    private String testMessage;
    private int completed;
    private int remaining;

    public DashboardHeader(String msg, int completed, int remaining) {
        this.testMessage = msg;
        this.completed = completed;
        this.remaining = remaining;
    }

    public String getTestMessage() {return this.testMessage;}
    public void setTestMessage(String msg) {this.testMessage = msg;}
    public int getCompleted() { return this.completed; }
    public int getRemaining() { return this.remaining; }

}
