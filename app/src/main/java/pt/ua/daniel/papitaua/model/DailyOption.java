package pt.ua.daniel.papitaua.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ico on 2015-10-03.
 */
public class DailyOption {
    static final public int DAILY_MEAL_LUNCH = 1;
    static final public int DAILY_MEAL_DINNER = 2;


    private Date date;
    private String canteenSite;
    private String dailyMeal;
    private boolean available;
    private List<MealCourse> mealCourseList;


    public DailyOption(Date date, String canteen, String dailyMeal, boolean available) {
        this.canteenSite = canteen;
        this.date = date;
        this.dailyMeal = dailyMeal;
        this.available = available;

        mealCourseList = new ArrayList<>();
    }

    public void addMealCourse(MealCourse meal) {
        mealCourseList.add(meal);
    }

    public List<MealCourse> getMealCourseList() {
        return mealCourseList;
    }

    public Date getDate() {
        return date;
    }

    public String getCanteenSite() {
        return canteenSite;
    }

    public String getDailyMeal() {
        return dailyMeal;
    }

    public boolean isAvailable() {
        return available;
    }

    @Override
    public String toString() {
        return canteenSite+" - "+dailyMeal+"\n"+date.toString();
    }
}

