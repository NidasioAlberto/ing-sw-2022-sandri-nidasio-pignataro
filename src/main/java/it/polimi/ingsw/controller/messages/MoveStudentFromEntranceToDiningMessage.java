package it.polimi.ingsw.controller.messages;

import it.polimi.ingsw.controller.GameActionHandler;
import it.polimi.ingsw.model.BaseGameAction;
import it.polimi.ingsw.model.SchoolColor;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Message related to the movement of a student from entrance to the dining.
 */
public class MoveStudentFromEntranceToDiningMessage extends ActionMessage
{
    List<SchoolColor> selectedColors;

    protected MoveStudentFromEntranceToDiningMessage(JSONObject actionJson) throws JSONException
    {
        selectedColors = new ArrayList<>();

        JSONArray colors = actionJson.getJSONArray("selectedColors");
        for (int i = 0; i < colors.length(); i++)
            selectedColors.add(SchoolColor.valueOf(colors.getString(i)));
    }

    public void applyAction(GameActionHandler handler)
    {
        checkHandler(handler);
        handler.moveStudentFromEntranceToDining(selectedColors);
    }

    public BaseGameAction getBaseGameAction()
    {
        return BaseGameAction.MOVE_STUDENT_FROM_ENTRANCE_TO_DINING;
    }
}
