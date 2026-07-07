package skit_project.example_labs.lab4.submission.src.main.java.com.skit;

public class Course {
    private Long id;
    private String name;
    private int credits;
    private boolean active;

    public Course(Long id, String name, int credits, boolean active) {
        this.id = id;
        this.name = name;
        this.credits = credits;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCredits() {
        return credits;
    }

    public boolean isActive() {
        return active;
    }
}


