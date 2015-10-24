package pt.ua.daniel.papitaua.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds information for daily menus at the UA's canteens; it  may contain one or several days
 */
public class UAMenus {

    static final public int COURSE_ORDER_SOUP = 0;
    static final public int COURSE_ORDER_MEAT_NRM = 1;
    static final public int COURSE_ORDER_FISH_NRM = 2;

    // the list of options, per day and per canteen
    private static List<DailyOption> dailyMenus = new ArrayList<>();

    /**
     * parses the web service response to instantiate and fill a FoodMenus object
     * @param jsonResults the response from the remote web service
     * @return a navigable collection of daily food options
     * @throws IOException
     */
    static public UAMenus parseJson(String jsonResults) throws IOException {
        UAMenus menusToReturn = new UAMenus();  // return object

        JSONArray menuOptionsArray, jsonArray2;
        JSONObject workingJsonObject;
        DailyOption dailyOption;
        SimpleDateFormat dateParser = new SimpleDateFormat("EEE, d MMM yyy HH:mm:ss z");

        try {
            workingJsonObject = new JSONObject(jsonResults);    // access root object
            workingJsonObject = workingJsonObject.getJSONObject("menus"); // access menus within root
            menuOptionsArray = workingJsonObject.getJSONArray("menu"); // options for a zone

            // go through the several entries (day,canteen)
            for (int dailyEntry = 0; dailyEntry < menuOptionsArray.length(); dailyEntry++) {
                dailyOption = new DailyOption(
                        dateParser.parse(menuOptionsArray.getJSONObject(dailyEntry).getJSONObject("@attributes").getString("date")),
                        menuOptionsArray.getJSONObject(dailyEntry).getJSONObject("@attributes").getString("canteen"),
                        menuOptionsArray.getJSONObject(dailyEntry).getJSONObject("@attributes").getString("meal"),
                        menuOptionsArray.getJSONObject(dailyEntry).getJSONObject("@attributes").getString("disabled").compareTo("0") == 0);

                if (dailyOption.isAvailable()) {
                    jsonArray2 = menuOptionsArray.getJSONObject(dailyEntry).getJSONObject("items").getJSONArray("item");
                    // get the several meals in a canteen, in a day
                    dailyOption.addMealCourse(new MealCourse(COURSE_ORDER_SOUP, parseForObjectOrString(jsonArray2, COURSE_ORDER_SOUP) ));
                    dailyOption.addMealCourse(new MealCourse(COURSE_ORDER_MEAT_NRM, parseForObjectOrString(jsonArray2, COURSE_ORDER_MEAT_NRM) ));
                    dailyOption.addMealCourse(new MealCourse(COURSE_ORDER_FISH_NRM, parseForObjectOrString(jsonArray2, COURSE_ORDER_FISH_NRM) ));
                    //// TODO: 2015-10-07 complete with more meal courses
                }
                menusToReturn.add(dailyOption);
            }
        } catch (JSONException je) {
            je.printStackTrace();
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return menusToReturn;
    }

    /**
     * parses the meal course options; the JSON is not coherent, and can be an object or a string
     * @param array array with the meal options
     * @param index position to retrieve
     * @return the meal course option
     * @throws JSONException
     */
    static private String parseForObjectOrString(JSONArray array, int index) throws JSONException {
        JSONObject tempJsonObject = array.optJSONObject(index);
        if( null == tempJsonObject ) {
            // no json object, treat as string
            return array.getString(index).toString();
        } else {
            return array.getJSONObject(index).getJSONObject("@attributes").getString("name");
        }

    }

    private void add(DailyOption dailyOption) {
        this.dailyMenus.add(dailyOption);
    }

    /**
     * dumps the content of the object into a string for logging/dubbuging
     * @return the contents as String
     */
    @Override
    public String toString() {
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");
        StringBuilder builder = new StringBuilder();
        for (DailyOption option : this.dailyMenus) {
            builder.append("Day: ");
            builder.append(dateFormater.format(option.getDate()));
            builder.append("\tCanteen: ");
            builder.append(option.getCanteenSite());
            builder.append("\tMeal type: ");
            builder.append(option.getDailyMeal());
            builder.append("\tIs open? ");
            builder.append(option.isAvailable());
            builder.append("\n");
            for (MealCourse mealOption: option.getMealCourseList() ) {
                builder.append("\tCourse: ");   builder.append(mealOption.getMealCourse());
                builder.append("\t meal: ");   builder.append(mealOption.getFoodOption());
                builder.append("\n");
            }
        }
        return builder.toString();
    }

    public static List<DailyOption> getDailyMenus() { return dailyMenus; }
}
