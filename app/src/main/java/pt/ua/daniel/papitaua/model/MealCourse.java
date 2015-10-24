package pt.ua.daniel.papitaua.model;

/**
 * Created by ico on 2015-10-07.
 */
public class MealCourse {
    private int courseOrder;
    private String foodOption;

    public int getMealCourse() {
        return courseOrder;
    }

    public String getFoodOption() {
        return foodOption;
    }

    public MealCourse(int mealCourseOrder, String foodOption) {
        this.courseOrder = mealCourseOrder;
        this.foodOption = foodOption;
    }
}
