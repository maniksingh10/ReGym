package com.veersgym.manik;

public class GymMember {

    private String name;
    private String gender;
    private String branch;
    private String emailid;
    private String mobile;
    private String traineremailid;
    private String id;
    private int gymid;
    private int amount;
    private long feedate;
    private long joindate;
    private int months;

    public GymMember() {
    }

    public GymMember(String name, String gender, String branch, String emailid, String mobile, String traineremailid, String id, int gymid, int amount, long feedate, long joindate, int months) {
        this.name = name;
        this.gender = gender;
        this.branch = branch;
        this.emailid = emailid;
        this.mobile = mobile;
        this.traineremailid = traineremailid;
        this.id = id;
        this.gymid = gymid;
        this.amount = amount;
        this.feedate = feedate;
        this.joindate = joindate;
        this.months = months;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public String getBranch() {
        return branch;
    }

    public String getEmailid() {
        return emailid;
    }

    public String getMobile() {
        return mobile;
    }

    public String getTraineremailid() {
        return traineremailid;
    }

    public String getId() {
        return id;
    }

    public int getGymid() {
        return gymid;
    }

    public int getAmount() {
        return amount;
    }

    public long getFeedate() {
        return feedate;
    }

    public long getJoindate() {
        return joindate;
    }

    public int getMonths() {
        return months;
    }
}
